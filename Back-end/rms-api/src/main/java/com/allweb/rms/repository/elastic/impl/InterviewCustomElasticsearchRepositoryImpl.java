package com.allweb.rms.repository.elastic.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import com.allweb.rms.entity.dto.InterviewElasticsearchRequest;
import com.allweb.rms.entity.elastic.InterviewElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.event.ElasticsearchDocumentIndexNotFoundEvent;
import com.allweb.rms.repository.elastic.InterviewCustomElasticsearchRepository;
import com.allweb.rms.service.elastic.ElasticConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.ScriptType;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.BulkOptions;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InterviewCustomElasticsearchRepositoryImpl
        implements InterviewCustomElasticsearchRepository {

    private static final String DATE_TIME_FIELD = "date_time";
    private static final String CREATE_AT_FIELD = "created_at";
    private static final String STATUS_FIELD = "status";
    private static final String TITLE_FIELD = "title";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String CANDIDATE_FULL_NAME = "candidate.full_name";
    private static final String IS_DELETED_FIELD = "is_deleted";
    private static final String KEYWORD_SUFFIXED = ".keyword";
    private static final Float DEFAULT_BOOST = 1.0f;
    private static final String[] SUPPORTED_SORT_FIELDS =
            new String[]{
                    TITLE_FIELD, STATUS_FIELD, CANDIDATE_FULL_NAME, DATE_TIME_FIELD, CREATE_AT_FIELD
            };
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ElasticsearchOperations elasticsearchOperations;

    private final SimpleDateFormat dateFormatter;
    private final String defaultDateFormat;

    @Value("${application.default.client.timezone}")
    private String defaultClientTimeZone;

    public InterviewCustomElasticsearchRepositoryImpl(
            ApplicationEventPublisher applicationEventPublisher,
            ElasticsearchOperations elasticsearchOperations,
            @Value("${pattern.date.format}") String defaultDateFormat) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.elasticsearchOperations = elasticsearchOperations;
        this.defaultDateFormat = defaultDateFormat;
        this.dateFormatter = new SimpleDateFormat(defaultDateFormat);
    }

    @Override
    public Page<InterviewElasticsearchDocument> findAllByElasticsearch(
            InterviewElasticsearchRequest request) {

        Pageable pageable =
                ElasticsearchPageableUtils.resolveKeywordPrefixedPageable(
                        request.getPageable(), SUPPORTED_SORT_FIELDS);
        NativeQuery searchQuery =
                new NativeQueryBuilder()
                        .withQuery(
                                query -> {
                                    this.buildFilterQuery(request, query);
                                    return query;
                                })
                        .withPageable(pageable)
                        .build();
        try {
            SearchHits<InterviewElasticsearchDocument> searchHits =
                    this.elasticsearchOperations.search(
                            searchQuery,
                            InterviewElasticsearchDocument.class,
                            IndexCoordinates.of(InterviewElasticsearchDocument.INDEX_NAME));
            SearchPage<InterviewElasticsearchDocument> searchPage =
                    SearchHitSupport.searchPageFor(searchHits, request.getPageable());
            return searchPage.map(SearchHit::getContent);
        } catch (NoSuchIndexException noSuchIndexException) {
            this.applicationEventPublisher.publishEvent(
                    new ElasticsearchDocumentIndexNotFoundEvent(
                            this, InterviewElasticsearchDocument.INDEX_NAME));
        } catch (Exception ex) {
            log.error("", ex);
        }
        return new PageImpl<>(new ArrayList<>(), request.getPageable(), 0);
    }


    @Override
    public void updateInterviewElasticsearch(Interview interview, int resultCount) {
        String updateScript = "ctx._source.title = params._interview_title;" +
                "ctx._source.date_time = params._interview_date_time;" +
                "ctx._source.description = params._interview_description;" +
                "ctx._source.created_at = params._interview_created_at;" +
                "ctx._source.updated_at = params._interview_updated_at;" +
                "ctx._source.has_result = params._interview_has_result;" +
                "ctx._source.is_deleted = params._interview_is_deleted;" +
                "ctx._source.status = params._interview_status;" +
                "ctx._source.candidate = params._interview_candidate;";
        Map<String, Object> candidateDataMap = new HashMap<>();
        Candidate candidate = interview.getCandidate();
        candidateDataMap.put("id", candidate.getId());
        candidateDataMap.put("full_name", String.format("%s %s %s", candidate.getSalutation(), candidate.getFirstname(), candidate.getLastname()));
        Map<String, Object> interviewDataMap = new HashMap<>();
        interviewDataMap.put("_interview_title", interview.getTitle());
        interviewDataMap.put("_interview_date_time", new Date(interview.getDateTime().getTime()));
        interviewDataMap.put("_interview_description", interview.getDescription());
        interviewDataMap.put("_interview_created_at", new Date(interview.getCreatedAt().getTime()));
        interviewDataMap.put("_interview_updated_at", new Date(interview.getUpdatedAt().getTime()));
        interviewDataMap.put("_interview_has_result", resultCount > 0);
        interviewDataMap.put("_interview_is_deleted", interview.isDelete() || candidate.isDeleted());
        interviewDataMap.put("_interview_status", interview.getInterviewStatus().getName());
        interviewDataMap.put("_interview_candidate", candidateDataMap);
        UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(interview.getId()))
                .withScript(updateScript)
                .withParams(interviewDataMap)
                .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
                .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
                .withScriptType(ScriptType.INLINE)
                .build();
        this.elasticsearchOperations.update(updateQuery, IndexCoordinates.of(InterviewElasticsearchDocument.INDEX_NAME));
    }


    @Override
    public void updateInterviewCandidate(
            List<InterviewElasticsearchDocument> interviewList, Candidate candidate) {
        String updateScript =
                "ctx._source.candidate = params._candidate;ctx._source.is_deleted = params._is_deleted";
        Map<String, Object> candidateDataMap = new HashMap<>();
        if (candidate != null) {
            candidateDataMap.put("id", candidate.getId());
            candidateDataMap.put("full_name", candidate.getFullName());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("_candidate", candidateDataMap);
        List<UpdateQuery> updateQueryList = new ArrayList<>();
        interviewList.forEach(
                interview -> {
                    params.put(
                            "_is_deleted", interview.isDeleted() || (candidate != null && candidate.isDeleted()));
                    updateQueryList.add(
                            UpdateQuery.builder(String.valueOf(interview.getId()))
                                    .withScript(updateScript)
                                    .withParams(params)
                                    .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
                                    .withScriptType(ScriptType.INLINE)
                                    .build());
                });

        if (!updateQueryList.isEmpty()) {
            BulkOptions immediateRefreshOptions =
                    BulkOptions.builder().withRefreshPolicy(RefreshPolicy.IMMEDIATE).build();
            this.elasticsearchOperations.bulkUpdate(
                    updateQueryList,
                    immediateRefreshOptions,
                    IndexCoordinates.of(InterviewElasticsearchDocument.INDEX_NAME));
        }
    }

    @Override
    public void incrementReminderCount(int interviewId, int increment) {
        String updateScript = "ctx._source.reminder_count += " + increment + ";";
        UpdateQuery updateQuery =
                UpdateQuery.builder(String.valueOf(interviewId))
                        .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
                        .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
                        .withScript(updateScript)
                        .withScriptType(ScriptType.INLINE)
                        .build();
        this.elasticsearchOperations.update(
                updateQuery, IndexCoordinates.of(InterviewElasticsearchDocument.INDEX_NAME));
    }

    @Override
    public void decrementReminderCount(int interviewId, int decrement) {
        String updateScript =
                "int reminderCount; reminderCount = ctx._source.reminder_count - %d;"
                        + "ctx._source.reminder_count = reminderCount <= 0 ? 0 : reminderCount;";
        updateScript = String.format(updateScript, decrement);
        UpdateQuery updateQuery =
                UpdateQuery.builder(String.valueOf(interviewId))
                        .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
                        .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
                        .withScript(updateScript)
                        .withScriptType(ScriptType.INLINE)
                        .build();
        this.elasticsearchOperations.update(
                updateQuery, IndexCoordinates.of(InterviewElasticsearchDocument.INDEX_NAME));
    }

    @Override
    public void updateInterviewReminderCount(int interviewId, int reminderCount) {
        String updateScript = "ctx._source.reminder_count = params._interview_reminder_count";
        Map<String, Object> params = new HashMap<>();
        params.put("_interview_reminder_count", reminderCount);
        UpdateQuery updateQuery =
                UpdateQuery.builder(String.valueOf(interviewId))
                        .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
                        .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
                        .withScript(updateScript)
                        .withParams(params)
                        .withScriptType(ScriptType.INLINE)
                        .build();
        this.elasticsearchOperations.update(
                updateQuery, IndexCoordinates.of(InterviewElasticsearchDocument.INDEX_NAME));
    }

    private void buildFilterQuery(InterviewElasticsearchRequest request, Query.Builder queryBuilder) {
        if (ArrayUtils.isNotEmpty(request.getStatus()) && StringUtils.isNotEmpty(request.getFilter())) {
            queryStatusAndFilter(request, queryBuilder);
        } else if (StringUtils.isNotEmpty(request.getFilter())) {
            queryFilter(request, queryBuilder);
        } else if (ArrayUtils.isNotEmpty(request.getStatus())) {
            queryStatus(request, queryBuilder);
        } else {
            filterDateTime(request, queryBuilder);
        }
    }

    private void queryStatusAndFilter(InterviewElasticsearchRequest request, Query.Builder queryBuilder) {
        queryBuilder.bool(
                builder ->
                        builder
                                .filter(
                                        q ->
                                                q.range(range ->
                                                        dateTimeFiltering(request, range)
                                                ))
                                .filter(q ->
                                        q.match(qs ->
                                                filterStatus(request, qs)))
                                .should(
                                        q ->
                                                q.wildcard(
                                                        qs -> wildCardQuery(CANDIDATE_FULL_NAME + KEYWORD_SUFFIXED, request, qs)))
                                .should(q ->
                                        q.wildcard(
                                                qs -> wildCardQuery(TITLE_FIELD + KEYWORD_SUFFIXED, request, qs))).
                                filter(
                                        q -> q.term(term -> term.field(IS_DELETED_FIELD).value(false)))
                                .minimumShouldMatch("1"));
    }

    private void queryStatus(InterviewElasticsearchRequest request, Query.Builder queryBuilder) {
        queryBuilder.bool(
                builder ->
                        builder
                                .filter(
                                        q ->
                                                q.range(range ->
                                                        dateTimeFiltering(request, range)
                                                ))
                                .filter(q ->
                                        q.match(qs ->
                                                filterStatus(request, qs))).
                                filter(
                                        q -> q.term(term -> term.field(IS_DELETED_FIELD).value(false))));
    }

    private void queryFilter(InterviewElasticsearchRequest request, Query.Builder queryBuilder) {
        queryBuilder.bool(
                builder ->
                        builder
                                .filter(
                                        q ->
                                                q.range(range ->
                                                        dateTimeFiltering(request, range)
                                                ))
                                .should(
                                        q ->
                                                q.wildcard(
                                                        qs -> wildCardQuery(CANDIDATE_FULL_NAME + KEYWORD_SUFFIXED, request, qs)))
                                .should(q ->
                                        q.wildcard(
                                                qs -> wildCardQuery(TITLE_FIELD + KEYWORD_SUFFIXED, request, qs)))
                                .filter(
                                        q -> q.term(term -> term.field(IS_DELETED_FIELD).value(false)))
                                .minimumShouldMatch("1"));
    }


    private WildcardQuery.Builder wildCardQuery(String field, InterviewElasticsearchRequest request, WildcardQuery.Builder builder) {
        builder.caseInsensitive(true)
                .field(field)
                .boost(DEFAULT_BOOST);
        if (StringUtils.isNotBlank(request.getFilter())) {
            builder.value(String.format("*%s*", request.getFilter().trim().toLowerCase()));
        }
        return builder;
    }

    private MatchQuery.Builder filterStatus(InterviewElasticsearchRequest request, MatchQuery.Builder builder) {
        StringBuilder stringBuilder = new StringBuilder();
        builder.field(STATUS_FIELD).query("");
        if (ArrayUtils.isNotEmpty(request.getStatus())) {
            for (int i = 0; i < request.getStatus().length; i++) {
                stringBuilder.append(request.getStatus()[i]).append(" AND ");
            }
            builder.query(String.format("%s", stringBuilder.
                    substring(0, stringBuilder.length() - 5).toString()));
        }
        return builder;
    }

    private void filterDateTime(InterviewElasticsearchRequest request, Query.Builder queryBuilder) {
        queryBuilder.bool(
                query ->
                        query
                                .filter(
                                        q ->
                                                q.range(
                                                        range ->
                                                                dateTimeFiltering(request, range)
                                                )
                                ).filter(
                                        q -> q.term(term -> term.field(IS_DELETED_FIELD).value(false))
                                ));
    }

    private RangeQuery.Builder dateTimeFiltering(InterviewElasticsearchRequest request, RangeQuery.Builder range) {
        range.field(DATE_TIME_FIELD)
                .format(defaultDateFormat)
                .timeZone(defaultClientTimeZone);
        if (request.getStartDate() != null && request.getEndDate() != null) {
            range
                    .from(dateFormatter.format(request.getStartDate()))
                    .to(dateFormatter.format(request.getEndDate()));
        } else if (request.getStartDate() == null && request.getEndDate() != null) {
            range.from(dateFormatter.format(request.getEndDate()));
        } else if (request.getStartDate() != null) {
            range.to(dateFormatter.format(request.getStartDate()));
        }

        return range;
    }
}
