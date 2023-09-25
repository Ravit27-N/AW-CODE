package com.allweb.rms.repository.elastic.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.allweb.rms.entity.dto.ReminderAdvanceFilterRequest;
import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.event.ElasticsearchDocumentIndexNotFoundEvent;
import com.allweb.rms.repository.elastic.ReminderCustomElasticsearchRepository;
import com.allweb.rms.service.elastic.ElasticConstants;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.BulkOptions;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Slf4j
public class ReminderCustomElasticsearchRepositoryImpl
    implements ReminderCustomElasticsearchRepository {

  private static final String INTERVIEW_TITLE_FIELD = "title";
  private static final String CANDIDATE_FULL_NAME_FIELD = "candidate.full_name";
  private static final String DESCRIPTION_FIELD = "description";
  private static final String REMINDER_TYPE_FIELD = "reminder_type";
  private static final String DATE_REMINDER_FIELD = "date_reminder";
  private static final String CREATED_AT_FIELD = "created_at";
  private static final Float DEFAULT_BOOST = 1.0f;
  private static final String KEYWORD = ".keyword";
  private static final String[] SUPPORTED_SORT_FIELDS =
      new String[] {
        INTERVIEW_TITLE_FIELD, CANDIDATE_FULL_NAME_FIELD, DATE_REMINDER_FIELD, CREATED_AT_FIELD
      };
  private final ElasticsearchOperations elasticsearchOperations;
  private final ApplicationEventPublisher applicationEventPublisher;

  private final String defaultDateFormat;
  private static final String DATE_FORMAT = "dd-MM-yyyy";
  private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

  @Value("${application.default.client.timezone}")
  private String defaultClientTimeZone;

  public ReminderCustomElasticsearchRepositoryImpl(
      ElasticsearchOperations elasticsearchOperations,
      ApplicationEventPublisher applicationEventPublisher,
      @Value("${pattern.date.format}") String defaultDateFormat) {
    this.elasticsearchOperations = elasticsearchOperations;
    this.applicationEventPublisher = applicationEventPublisher;
    this.defaultDateFormat = defaultDateFormat;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  public Page<ReminderElasticsearchDocument> elasticSearchAdvanceFilters(
      ReminderAdvanceFilterRequest request, Pageable pageable) {
    Pageable keywordPrefixedPageable =
        ElasticsearchPageableUtils.resolveKeywordPrefixedPageable(pageable, SUPPORTED_SORT_FIELDS);
    NativeQuery query1 =
        new NativeQueryBuilder()
            .withQuery(
                query -> {
                  this.buildFilterQuery(request, query);
                  if (StringUtils.isNotBlank(request.getFilter())) {
                    this.buildFullTextSearchQuery(request, query);
                  }
                  return query;
                })
            .withPageable(keywordPrefixedPageable)
            .build();
    try {
      SearchHits<ReminderElasticsearchDocument> hits =
          this.elasticsearchOperations.search(
              query1,
              ReminderElasticsearchDocument.class,
              IndexCoordinates.of(ReminderElasticsearchDocument.INDEX_NAME));
      SearchPage<ReminderElasticsearchDocument> searchPage =
          SearchHitSupport.searchPageFor(hits, keywordPrefixedPageable);
      return searchPage.map(SearchHit::getContent);
    } catch (NoSuchIndexException noSuchIndexException) {
      this.applicationEventPublisher.publishEvent(
          new ElasticsearchDocumentIndexNotFoundEvent(
              this, ReminderElasticsearchDocument.INDEX_NAME));
    } catch (Exception ex) {
      log.error("", ex);
    }
    return new PageImpl<>(new ArrayList<>(), keywordPrefixedPageable, 0);
  }

  @Override
  public void deleteElasticsearchDocumentById(int id) {
    this.elasticsearchOperations.delete(
        String.valueOf(id), IndexCoordinates.of(ReminderElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public void updateReminderInterviewElasticsearchDocument(
      List<ReminderElasticsearchDocument> reminderElasticDocList, Interview interview) {
    String updateScript = "ctx._source.interview = params._interview;";
    Map<String, Object> interviewDataMap = new HashMap<>();
    if (!interview.isDelete() && !interview.getCandidate().isDeleted()) {
      interviewDataMap.put("id", interview.getId());
      interviewDataMap.put(INTERVIEW_TITLE_FIELD, interview.getTitle());
    }
    Map<String, Object> params = new HashMap<>();
    params.put("_interview", interviewDataMap);
    List<UpdateQuery> queryList =
        this.getUpdateQueryList(reminderElasticDocList, updateScript, params);
    if (!queryList.isEmpty()) {
      this.elasticsearchOperations.bulkUpdate(
          queryList,
          this.getRefreshableBulkUpdateOption(),
          IndexCoordinates.of(ReminderElasticsearchDocument.INDEX_NAME));
    }
  }

  @Override
  public void updateReminderCandidateElasticsearchDocument(
      List<ReminderElasticsearchDocument> reminderElasticDocList, Candidate candidate) {
    String updateScript = "ctx._source.candidate = params._candidate;";
    Map<String, Object> candidateDataMap = new HashMap<>();
    if (candidate != null && !candidate.isDeleted()) {
      candidateDataMap.put("id", candidate.getId());
      candidateDataMap.put("full_name", candidate.getFullName());
    }
    Map<String, Object> params = new HashMap<>();
    params.put("_candidate", candidateDataMap);
    List<UpdateQuery> queryList =
        this.getUpdateQueryList(reminderElasticDocList, updateScript, params);
    if (!queryList.isEmpty()) {
      this.elasticsearchOperations.bulkUpdate(
          queryList,
          this.getRefreshableBulkUpdateOption(),
          IndexCoordinates.of(ReminderElasticsearchDocument.INDEX_NAME));
    }
  }



  private List<UpdateQuery> getUpdateQueryList(
      List<ReminderElasticsearchDocument> reminderElasticDocList,
      String updateScript,
      Map<String, Object> params) {
    List<UpdateQuery> queryList = new ArrayList<>();
    reminderElasticDocList.forEach(
        reminderElasticDoc ->
            queryList.add(
                UpdateQuery.builder(String.valueOf(reminderElasticDoc.getId()))
                    .withScript(updateScript)
                    .withParams(params)
                    .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
                    .build()));
    return queryList;
  }

  private BulkOptions getRefreshableBulkUpdateOption() {
    return BulkOptions.builder().withRefreshPolicy(RefreshPolicy.IMMEDIATE).build();
  }

  private void buildFilterQuery(ReminderAdvanceFilterRequest request, Query.Builder queryBuilder) {

    queryBuilder.bool(
        query ->{
            query
                .filter(q -> q.term(term -> term.field("deleted").value(request.isDeleted())))
                .filter(f -> f.match(m -> this.reminderTypefilter(request, m)));
          if (request.getFrom() != null || request.getTo() != null) {
           query .filter(q -> q.range(range -> dateTimeFiltering(request, range)));
          }
          if (request.getActive() != null) {
            query.filter(q -> q.term(term -> term.field("active").value(request.getActive())));
          }
             return query;
        });
  }

  private void buildFullTextSearchQuery(
      ReminderAdvanceFilterRequest request, Query.Builder queryBuilder) {
      queryBuilder.bool(
          builder -> {
            builder
                .filter(q -> q.range(range -> this.dateTimeFiltering(request, range)))
                .filter(f -> f.match(m -> this.reminderTypefilter(request, m)))
                .filter(f -> f.term(term -> term.field("deleted").value(request.isDeleted())))
                .should(
                    q ->
                        q.wildcard(
                            qs ->
                                qs.value(
                                        String.format(
                                            "*%s*", request.getFilter().trim().toLowerCase()))
                                    .field(INTERVIEW_TITLE_FIELD + KEYWORD)
                                    .boost(DEFAULT_BOOST)
                                    .caseInsensitive(true)))
                .should(
                    q ->
                        q.wildcard(
                            qs ->
                                qs.value(
                                        String.format(
                                            "*%s*", request.getFilter().trim().toLowerCase()))
                                    .field(CANDIDATE_FULL_NAME_FIELD + KEYWORD)
                                    .boost(DEFAULT_BOOST)
                                    .caseInsensitive(true)))
                .minimumShouldMatch("1");

            // minimumShouldMatch = 1 mean (.should) have to be at least one true

            return builder;
          });

  }

  // This function is for filter dateTime
  private RangeQuery.Builder dateTimeFiltering(
      ReminderAdvanceFilterRequest request, RangeQuery.Builder range) {
    range.field(DATE_REMINDER_FIELD).format(defaultDateFormat).timeZone(this.defaultClientTimeZone);
    if (request.getFrom() != null && request.getTo() != null) {
      range.from(dateFormatter.format(request.getFrom())).to(dateFormatter.format(request.getTo()));
    } else if (request.getFrom() == null && request.getTo() != null) {
      range.to(dateFormatter.format(request.getTo()));
    } else if (request.getFrom() != null) {
      range.from(dateFormatter.format(request.getFrom()));
    }
    return range;
  }

  // This function is for filter ReminderType
  private MatchQuery.Builder reminderTypefilter(
      ReminderAdvanceFilterRequest request, MatchQuery.Builder matchQuery) {
    if (request.getReminderTypes() == null){
      request.setReminderTypes(new String[0]);
    }
    String name;
    var queryReminderTypeString =
        new Object() {
          String queryReminderType = "";
        };
    if (request.getReminderTypes().length == 0) {
      matchQuery.field(REMINDER_TYPE_FIELD).query("NORMAL AND SPECIAL AND INTERVIEW");
    } else {
      IntStream.range(0, request.getReminderTypes().length)
          .forEach(
              idx ->
                  queryReminderTypeString.queryReminderType +=
                      request.getReminderTypes()[idx] + " AND ");
      name =
          queryReminderTypeString.queryReminderType.substring(
              0, queryReminderTypeString.queryReminderType.length() - 4);
      matchQuery.field(REMINDER_TYPE_FIELD).query(name);
    }
    return matchQuery;
  }
}
