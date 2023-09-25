package com.allweb.rms.repository.elastic.impl;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.JsonData;
import com.allweb.rms.entity.dto.CandidateElasticsearchRequest;
import com.allweb.rms.entity.elastic.CandidateElasticsearchDocument;
import com.allweb.rms.entity.elastic.CandidateReportElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.CandidateStatus;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Result;
import com.allweb.rms.entity.jpa.University;
import com.allweb.rms.event.ElasticsearchDocumentIndexNotFoundEvent;
import com.allweb.rms.repository.elastic.CandidateCustomElasticsearchRepository;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticIndexingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class CandidateCustomElasticsearchRepositoryImpl
    implements CandidateCustomElasticsearchRepository {

  private static final String CANDIDATE_SALUTATION_FIELD = "salutation";
  private static final String CANDIDATE_FIRST_NAME_FIELD = "first_name";
  private static final String CANDIDATE_LAST_NAME_FIELD = "last_name";
  private static final String CANDIDATE_GENDER_FIELD = "gender";
  private static final String CANDIDATE_GPA_FIELD = "gpa";
  private static final String CANDIDATE_TELEPHONE_FIELD = "telephone";
  private static final String CANDIDATE_PRIORITY_FIELD = "priority";
  private static final String CANDIDATE_IS_DELETED_FIELD = "is_deleted";
  private static final String CANDIDATE_REMINDER_COUNT = "reminder_count";
  private static final String CANDIDATE_CREATED_AT_FIELD = "created_at";
  private static final String CANDIDATE_STATUS_TITLE_FIELD = "candidate_status.title";
  private static final String CANDIDATE_INTERVIEW_COUNT_FIELD = "interview_count";
  private static final String UNIVERSITY_NAME_FIELD = "universities.name";
  private static final String INTERVIEW_TITLE_FIELD = "interviews.title";
  private static final String INTERVIEW_DATE_TIME_FIELD = "interviews.date_time";
  private static final String TITLE_FIELD = "title";
  private static final String KEYWORD_SUFFIX = ".keyword";
  private static final String[] fields =
      new String[] {
        "firstname",
        CANDIDATE_TELEPHONE_FIELD,
        CANDIDATE_PRIORITY_FIELD,
        CANDIDATE_GPA_FIELD,
        "university",
        "createdAt",
        "candidateStatus",
        CANDIDATE_GENDER_FIELD,
      };
  private static final Float DEFAULT_BOOST = 1.0F;
  private final ElasticsearchOperations elasticsearchOperations;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ObjectMapper objectMapper;

  private final List<String> sortOrderField;
  private final Map<String, String> fieldMap;
  private final List<String> supportedKeywordPrefixedFieldList;

  @Value("${pattern.date.format}")
  private String defaultDateTimeFormat;

  @Value("${application.default.client.timezone}")
  private String defaultClientTimeZone;

  public CandidateCustomElasticsearchRepositoryImpl(
      ElasticsearchOperations elasticsearchOperations,
      ApplicationEventPublisher applicationEventPublisher,
      ObjectMapper objectMapper) {
    this.elasticsearchOperations = elasticsearchOperations;
    this.applicationEventPublisher = applicationEventPublisher;
    this.objectMapper = objectMapper;
    List<String> defaultSortOrderFields = Arrays.asList(ArrayUtils.subarray(fields, 0, 6));
    this.sortOrderField = new ArrayList<>(defaultSortOrderFields);
    this.sortOrderField.add(fields[7]);
    this.supportedKeywordPrefixedFieldList =
        new ArrayList<>(Arrays.asList(fields[1], fields[2], fields[3], fields[7]));
    this.fieldMap = new HashMap<>();
    this.fieldMap.put(fields[4], UNIVERSITY_NAME_FIELD);
    this.fieldMap.put(fields[5], CANDIDATE_CREATED_AT_FIELD);
    this.fieldMap.put(fields[6], CANDIDATE_STATUS_TITLE_FIELD);
  }

  @Override
  public Page<CandidateElasticsearchDocument> elasticAdvanceSearch(
      CandidateElasticsearchRequest request) {
    Pageable pageable =
        resolveCandidateElasticsearchPageable(
            request.getPageable(), this.sortOrderField.toArray(new String[0]));
    NativeQuery searchQueryBuilder =
        new NativeQueryBuilder()
            .withQuery(
                query -> {
                  if (!request.getCandidateName().isBlank()
                      || !request.getUniversity().isBlank()
                      || !request.getGender().isBlank()
                      || !request.getPosition().isBlank()
                      || request.getGpa() > 0) {
                    this.advanceFilterSpecificField(request, query);
                  } else {
                    this.buildFullTextFilterQuery(request, query);
                  }
                  return query;
                })
            .withSort(
                pageable.getSort().stream()
                    .map(
                        order ->
                            new SortOptions.Builder()
                                .field(
                                    fs ->
                                        fs.field(order.getProperty())
                                            .order(
                                                order.isAscending()
                                                    ? SortOrder.Asc
                                                    : SortOrder.Desc))
                                .build())
                    .toList())
            .withPageable(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()))
            .build();
    try {
      SearchHits<CandidateElasticsearchDocument> searchHits =
          this.elasticsearchOperations.search(
              searchQueryBuilder,
              CandidateElasticsearchDocument.class,
              IndexCoordinates.of(CandidateElasticsearchDocument.INDEX_NAME));
      SearchPage<CandidateElasticsearchDocument> searchPage =
          SearchHitSupport.searchPageFor(searchHits, pageable);
      return searchPage.map(SearchHit::getContent);
    } catch (NoSuchIndexException noSuchIndexException) {
      this.applicationEventPublisher.publishEvent(
          new ElasticsearchDocumentIndexNotFoundEvent(
              this, CandidateElasticsearchDocument.INDEX_NAME));
    } catch (Exception e) {
      log.error("", e);
    }
    return new PageImpl<>(new ArrayList<>(), pageable, 0);
  }

  @Override
  public Page<CandidateReportElasticsearchDocument> getReportFromElasticsearch(
      CandidateElasticsearchRequest request) {

    Pageable pageable =
        resolveCandidateElasticsearchPageable(
            request.getPageable(), this.sortOrderField.toArray(new String[0]));
    NativeQuery reportQuery =
        new NativeQueryBuilder()
            .withQuery(
                query -> {
                  this.buildReportFilterQuery(request, query);
                  this.buildReportSearchFilterQuery(request, query);
                  return query;
                })
            .withPageable(pageable)
            .build();
    try {
      SearchHits<CandidateReportElasticsearchDocument> searchHits =
          this.elasticsearchOperations.search(
              reportQuery,
              CandidateReportElasticsearchDocument.class,
              IndexCoordinates.of(CandidateReportElasticsearchDocument.INDEX_NAME));
      SearchPage<CandidateReportElasticsearchDocument> searchPage =
          SearchHitSupport.searchPageFor(searchHits, pageable);
      return searchPage.map(SearchHit::getContent);
    } catch (NoSuchIndexException noSuchIndexException) {
      this.applicationEventPublisher.publishEvent(
          new ElasticsearchDocumentIndexNotFoundEvent(
              this, CandidateReportElasticsearchDocument.INDEX_NAME));
    }
    return new PageImpl<>(new ArrayList<>(), pageable, 0);
  }

  @Override
  public void updateCandidateInterview(
      int candidateId,
      @Nullable Interview lastInterview,
      @Nullable Result interviewResult,
      int candidateInterviewCount) {
    String updateScript =
        "ctx._source.interview_count = params._interview_count;"
            + "ctx._source.interviews = params._last_interview;";
    Map<String, Object> lastInterviewDataMap =
        this.getCandidateLastInterviewDataMap(
            CandidateElasticsearchDocument.INDEX_NAME, lastInterview, interviewResult);
    Map<String, Object> params = new HashMap<>();
    params.put("_interview_count", candidateInterviewCount);
    params.put("_last_interview", lastInterviewDataMap);
    UpdateQuery updateQuery =
        UpdateQuery.builder(String.valueOf(candidateId))
            .withScript(updateScript)
            .withParams(params)
            .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
            .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .withScriptType(ScriptType.INLINE)
            .build();
    this.elasticsearchOperations.update(
        updateQuery, IndexCoordinates.of(CandidateElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public void updateCandidateReportInterview(
      int candidateId, @Nullable Interview lastInterview, @Nullable Result interviewResult) {
    String updateScript = "ctx._source.interviews = params._last_interview;";
    Map<String, Object> lastInterviewDataMap =
        this.getCandidateLastInterviewDataMap(
            CandidateReportElasticsearchDocument.INDEX_NAME, lastInterview, interviewResult);
    Map<String, Object> params = new HashMap<>();
    params.put("_last_interview", lastInterviewDataMap);
    UpdateQuery updateQuery =
        UpdateQuery.builder(String.valueOf(candidateId))
            .withScript(updateScript)
            .withParams(params)
            .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
            .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .withScriptType(ScriptType.INLINE)
            .build();
    this.elasticsearchOperations.update(
        updateQuery, IndexCoordinates.of(CandidateReportElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public void updateCandidate(
      Candidate candidate, CandidateStatus candidateStatus, List<University> universityList) {
    String updateScript = this.getCandidateUpdateScript();
    Map<String, Object> params =
        this.getCandidateUpdateParameters(candidate, candidateStatus, universityList);
    UpdateQuery updateQuery =
        UpdateQuery.builder(String.valueOf(candidate.getId()))
            .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
            .withRefreshPolicy(RefreshPolicy.WAIT_UNTIL)
            .withParams(params)
            .withScript(updateScript)
            .withScriptType(ScriptType.INLINE)
            .build();
    this.elasticsearchOperations.update(
        updateQuery, IndexCoordinates.of(CandidateElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public void updateCandidateReport(Candidate candidate, List<University> universities) {
    String updateScript = this.getCandidateReportUpdateScript();
    Map<String, Object> params = this.getCandidateUpdateParameters(candidate, universities);
    UpdateQuery updateQuery =
        UpdateQuery.builder(String.valueOf(candidate.getId()))
            .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
            .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .withScript(updateScript)
            .withParams(params)
            .withScriptType(ScriptType.INLINE)
            .build();
    this.elasticsearchOperations.update(
        updateQuery, IndexCoordinates.of(CandidateReportElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public CandidateReportElasticsearchDocument saveCandidateReportElasticDocument(
      CandidateReportElasticsearchDocument candidateElasticsearchDocument) {
    return this.elasticsearchOperations.save(
        candidateElasticsearchDocument,
        IndexCoordinates.of(CandidateReportElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public void incrementCandidateReminderCount(int candidateId, int increment) {
    String updateScript = "ctx._source.reminder_count += " + increment;
    UpdateQuery updateQuery =
        UpdateQuery.builder(String.valueOf(candidateId))
            .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
            .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .withScript(updateScript)
            .withScriptType(ScriptType.INLINE)
            .build();
    this.elasticsearchOperations.update(
        updateQuery, IndexCoordinates.of(CandidateElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public void decrementCandidateReminderCount(int candidateId, int decrement) {
    String updateScript =
        "int reminderCount = ctx._source.reminder_count - %d;"
            + "ctx._source.reminder_count = reminderCount <= 0 ? 0 : reminderCount";
    updateScript = String.format(updateScript, decrement);
    UpdateQuery updateQuery =
        UpdateQuery.builder(String.valueOf(candidateId))
            .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
            .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .withScript(updateScript)
            .withScriptType(ScriptType.INLINE)
            .build();
    this.elasticsearchOperations.update(
        updateQuery, IndexCoordinates.of(CandidateElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public void updateCandidateReminderCount(int candidateId, int reminderCount) {
    String updateScript = "ctx._source.reminder_count = params._reminder_count";
    Map<String, Object> params = new HashMap<>();
    params.put("_reminder_count", reminderCount);
    UpdateQuery updateQuery =
        UpdateQuery.builder(String.valueOf(candidateId))
            .withLang(ElasticConstants.PAINLESS_SCRIPT_LANGUAGE)
            .withRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .withScript(updateScript)
            .withParams(params)
            .withScriptType(ScriptType.INLINE)
            .build();
    this.elasticsearchOperations.update(
        updateQuery, IndexCoordinates.of(CandidateElasticsearchDocument.INDEX_NAME));
  }

  @Override
  public void deleteCandidateReportByCandidateId(int candidateId) {
    this.elasticsearchOperations.delete(
        String.valueOf(candidateId),
        IndexCoordinates.of(CandidateReportElasticsearchDocument.INDEX_NAME));
  }

  private String getCandidateUpdateScript() {
    return getCandidateReportUpdateScript()
        + "ctx._source.status_id = params._candidate_status_id;"
        + "ctx._source.description = params._candidate_description;"
        + "ctx._source.is_deleted = params._candidate_is_deleted;"
        + "ctx._source.candidate_status = params._candidate_status;";
  }

  private Map<String, Object> getCandidateUpdateParameters(
      Candidate candidate,
      @Nullable CandidateStatus candidateStatus,
      List<University> universityList) {
    Map<String, Object> candidateStatusDataMap = new HashMap<>();
    if (candidateStatus != null) {
      candidateStatusDataMap.put("id", candidateStatus.getId());
      candidateStatusDataMap.put(TITLE_FIELD, candidateStatus.getTitle());
      candidateStatusDataMap.put("active", candidateStatus.isActive());
    }
    Map<String, Object> params = this.getCandidateUpdateParameters(candidate, universityList);
    params.put("_candidate_status_id", candidate.getCandidateStatus().getId());
    if (candidate.getDescription() != null) {
      params.put("_candidate_description", candidate.getDescription());
    } else {
      params.put("_candidate_description", "");
    }
    params.put("_candidate_status", candidateStatusDataMap);
    return params;
  }

  private String getCandidateReportUpdateScript() {
    return "ctx._source.salutation = params._candidate_salutation;"
        + "ctx._source.first_name = params._candidate_firstname;"
        + "ctx._source.last_name = params._candidate_lastname;"
        + "ctx._source.gpa = params._candidate_gpa;"
        + "ctx._source.gender = params._candidate_gender;"
        + "ctx._source.date_of_birth = params._date_of_birth;"
        + "ctx._source.year_of_experience = params._year_of_experience;"
        + "ctx._source.email = params._candidate_email;"
        + "ctx._source.photo_url = params._candidate_photo_url;"
        + "ctx._source.telephone = params._candidate_telephone;"
        + "ctx._source.active = params._candidate_active;"
        + "ctx._source.priority = params._candidate_priority;"
        + "ctx._source.created_at = params._candidate_created_at;"
        + "ctx._source.updated_at = params._candidate_updated_at;"
        + "ctx._source.universities = params._candidate_universities;";
  }

  private Map<String, Object> getCandidateUpdateParameters(
      Candidate candidate, List<University> universityList) {
    List<Map<String, Object>> candidateUniversityDataMap =
        this.getUniversityDataMap(universityList);
    Map<String, Object> params = new HashMap<>();
    params.put("_candidate_salutation", candidate.getSalutation());
    params.put("_candidate_firstname", candidate.getFirstname());
    params.put("_candidate_lastname", candidate.getLastname());
    params.put("_candidate_gpa", candidate.getGpa());
    params.put(
        "_date_of_birth",
        candidate.getDateOfBirth() == null ? null : new Date(candidate.getDateOfBirth().getTime()));
    params.put("_year_of_experience", candidate.getYearOfExperience());
    params.put("_candidate_gender", candidate.getGender());
    params.put("_candidate_email", candidate.getEmail());
    params.put("_candidate_telephone", candidate.getTelephone());
    params.put("_candidate_active", candidate.isActive());
    params.put("_candidate_priority", candidate.getPriority());
    params.put("_candidate_created_at", new Date(candidate.getCreatedAt().getTime()));
    params.put("_candidate_updated_at", new Date(candidate.getUpdatedAt().getTime()));
    if (candidate.getDescription() != null) {
      params.put("_candidate_description", candidate.getDescription());
    } else {
      params.put("_candidate_description", "");
    }
    if (candidate.getPhotoUrl() != null) {
      params.put("_candidate_photo_url", candidate.getPhotoUrl());
    } else {
      params.put("_candidate_photo_url", "");
    }
    params.put("_candidate_is_deleted", candidate.isDeleted());
    params.put("_candidate_universities", candidateUniversityDataMap);
    return params;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getCandidateLastInterviewDataMap(
      String forIndexName, @Nullable Interview lastInterview, @Nullable Result interviewResult) {
    Map<String, Object> lastInterviewResultDataMap = new HashMap<>();
    if (interviewResult != null) {
      lastInterviewResultDataMap.put("id", interviewResult.getId());
      lastInterviewResultDataMap.put("average", interviewResult.getAverage());
      lastInterviewResultDataMap.put("english", interviewResult.getEnglish());
      lastInterviewResultDataMap.put("flexibility", interviewResult.getFlexibility());
      lastInterviewResultDataMap.put("logical", interviewResult.getLogical());
      lastInterviewResultDataMap.put("oral", interviewResult.getOral());
      Map<String, Object> scoreDataMap = new HashMap<>();
      try {
        scoreDataMap.putAll(this.objectMapper.readValue(interviewResult.getScore(), Map.class));
      } catch (JsonProcessingException e) {
        if (log.isDebugEnabled()) {
          log.debug(e.getMessage(), e);
        }
      }
      lastInterviewResultDataMap.put("score", scoreDataMap);
    }
    Map<String, Object> lastInterviewDataMap = new HashMap<>();
    if (lastInterview != null) {
      lastInterviewDataMap.put("id", lastInterview.getId());
      lastInterviewDataMap.put(TITLE_FIELD, lastInterview.getTitle());
      if (CandidateElasticsearchDocument.INDEX_NAME.equals(forIndexName)) {
        lastInterviewDataMap.put("last_interview", new Date(lastInterview.getDateTime().getTime()));
      } else {
        lastInterviewDataMap.put(
            "date_time",
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .format(new Date(lastInterview.getDateTime().getTime())));
      }
      lastInterviewDataMap.put("description", lastInterview.getDescription());
      lastInterviewDataMap.put("result", lastInterviewResultDataMap);
    }
    return lastInterviewDataMap;
  }

  private List<Map<String, Object>> getUniversityDataMap(List<University> universityList) {
    List<Map<String, Object>> candidateUniversityDataMap = new ArrayList<>();
    if (!universityList.isEmpty()) {
      List<Map<String, Object>> candidateUniversityList =
          universityList.stream()
              .map(
                  university -> {
                    Map<String, Object> universityDataMap = new HashMap<>();
                    universityDataMap.put("id", university.getId());
                    universityDataMap.put("name", university.getName());
                    return universityDataMap;
                  })
              .toList();
      candidateUniversityDataMap.addAll(candidateUniversityList);
    }
    return candidateUniversityDataMap;
  }

  private void buildReportSearchFilterQuery(
      CandidateElasticsearchRequest request, Query.Builder queryBuilder) {
    if (StringUtils.isNotBlank(request.getFilter())) {
      String filterText = request.getFilter().trim().toLowerCase();
      filterFullText(queryBuilder, filterText);
    }
  }

  private void buildReportFilterQuery(
      CandidateElasticsearchRequest request, Query.Builder queryBuilder) {
    SimpleDateFormat dateFormatter = new SimpleDateFormat(this.defaultDateTimeFormat);
    queryBuilder.bool(
        b ->
            b.filter(
                ft ->
                    ft.range(
                        range -> {
                          range
                              .field(INTERVIEW_DATE_TIME_FIELD)
                              .format(this.defaultDateTimeFormat)
                              .timeZone(this.defaultClientTimeZone);
                          if (request.getDateFrom() != null && request.getDateTo() != null) {
                            range
                                .from((dateFormatter.format(request.getDateFrom())))
                                .to((dateFormatter.format(request.getDateTo())));
                          } else if (request.getDateFrom() != null) {
                            range.from((dateFormatter.format(request.getDateFrom())));
                          } else if (request.getDateTo() != null) {
                            range.to(dateFormatter.format(request.getDateTo()));
                          }
                          return range;
                        })));
  }

  private void buildFullTextFilterQuery(
      CandidateElasticsearchRequest request, Query.Builder builder) {
    builder.bool(
        query ->
            query.filter(
                q ->
                    q.term(
                        term ->
                            term.field(CANDIDATE_IS_DELETED_FIELD).value(request.isDeleted()))));

    // This condition will filter if getFilter is not isBlank
    if (!request.getFilter().isBlank()
        && request.getFilterBy().length == 0
        && request.getCandidateStatus().isBlank()) {
      builder.bool(query -> this.filterSearchText(query, request.getFilter()));
    }

    if (Arrays.stream(request.getFilterBy())
        .anyMatch(filterBy -> filterBy.equalsIgnoreCase("both"))) {
      builder.bool(
          query -> {
            if (request.getFilterBy().length > 0) {
              query
                  .should(
                      q ->
                          q.range(
                              rn -> rn.field(CANDIDATE_INTERVIEW_COUNT_FIELD).gt(JsonData.of(0))))
                  .should(q -> q.range(rn -> rn.field(CANDIDATE_REMINDER_COUNT).gt(JsonData.of(0))))
                  .filter(
                      ft ->
                          ft.term(
                              term ->
                                  term.field(CANDIDATE_IS_DELETED_FIELD)
                                      .value(request.isDeleted())))
                  .minimumShouldMatch("2");
            }
            this.filterCandidateStatus(query, request);

            if (!request.getFilter().isBlank()) {
              query.must(m -> m.bool(b -> this.filterSearchText(b, request.getFilter())));
            }
            return query;
          });
    } else if (Arrays.stream(request.getFilterBy())
        .anyMatch(filterBy -> filterBy.equalsIgnoreCase("interview"))) {
      builder.bool(
          query -> {
            query
                    .filter(
                            q ->
                                    q.term(
                                            term ->
                                                    term.field(CANDIDATE_IS_DELETED_FIELD).value(request.isDeleted())))
                    .filter(
                q ->
                    q.bool(
                        b ->
                            b.should(
                                s ->
                                    s.range(
                                        r ->
                                            r.field(CANDIDATE_INTERVIEW_COUNT_FIELD)
                                                .gt(JsonData.of(0))))));

            this.filterCandidateStatus(query, request);

            if (!request.getFilter().isBlank()) {
              query.must(m -> m.bool(b -> this.filterSearchText(b, request.getFilter())));
            }
            return query;
          });
    } else if (Arrays.stream(request.getFilterBy())
        .anyMatch(filterBy -> filterBy.equalsIgnoreCase("reminder"))) {
      builder.bool(
          query -> {
            query
                .filter(
                    q ->
                        q.term(
                            term ->
                                term.field(CANDIDATE_IS_DELETED_FIELD).value(request.isDeleted())))
                .filter(
                    q ->
                        q.term(
                            term ->
                                term.field(CANDIDATE_IS_DELETED_FIELD).value(request.isDeleted())))
                .filter(
                    q ->
                        q.bool(
                            b ->
                                b.should(
                                    s ->
                                        s.range(
                                            r ->
                                                r.field(CANDIDATE_REMINDER_COUNT)
                                                    .gt(JsonData.of(0))))));
            this.filterCandidateStatus(query, request);
            if (!request.getFilter().isBlank()) {
              query.must(m -> m.bool(b -> this.filterSearchText(b, request.getFilter())));
            }

            return query;
          });
    }

    if ((!request.getCandidateStatus().isBlank() || !request.getFilter().isBlank())
        && request.getFilterBy().length == 0) {
      builder.bool(
          query -> {
            if (!request.getCandidateStatus().isBlank()) {
              query
                  .filter(
                      ft ->
                          ft.match(
                              t ->
                                  t.field(CANDIDATE_STATUS_TITLE_FIELD)
                                      .query(request.getCandidateStatus().trim().toLowerCase())))
                  .filter(
                      q ->
                          q.term(
                              term ->
                                  term.field(CANDIDATE_IS_DELETED_FIELD)
                                      .value(request.isDeleted())));
            }
            if (!request.getFilter().isBlank()) {
              query.must(m -> m.bool(b -> this.filterSearchText(b, request.getFilter())));
            }
            return query;
          });
    }
  }

  private Pageable resolveCandidateElasticsearchPageable(
      Pageable sourcePageable, String[] supportedSortFields) {
    List<Sort.Order> list = new ArrayList<>();
    if (ArrayUtils.contains(supportedSortFields, fields[0])) {
      list.addAll(this.generateCandidateFullNameSortOrder(sourcePageable));
    }
    list.addAll(this.generateKeywordPrefixedSortOrder(sourcePageable, supportedSortFields));
    list.addAll(this.generateNoneKeywordPrefixedSortOrder(sourcePageable, supportedSortFields));
    return PageRequest.of(
        sourcePageable.getPageNumber(), sourcePageable.getPageSize(), Sort.by(list));
  }

  List<Sort.Order> generateKeywordPrefixedSortOrder(
      Pageable pageable, String[] supportedSortFields) {
    return pageable
        .getSort()
        .get()
        .filter(
            order ->
                ArrayUtils.contains(supportedSortFields, order.getProperty())
                    && supportedKeywordPrefixedFieldList.contains(order.getProperty()))
        .map(
            order -> {
              // need to map to elastic field
              String keywordPrefixedField =
                  this.fieldMap.getOrDefault(order.getProperty(), order.getProperty())
                      + KEYWORD_SUFFIX;
              return order.isAscending()
                  ? Sort.Order.asc(keywordPrefixedField)
                  : Sort.Order.desc(keywordPrefixedField);
            })
        .toList();
  }

  List<Sort.Order> generateNoneKeywordPrefixedSortOrder(
      Pageable pageable, String[] supportedSortFields) {
    return pageable
        .getSort()
        .get()
        .filter(
            order ->
                !supportedKeywordPrefixedFieldList.contains(order.getProperty())
                    && !fields[0].equalsIgnoreCase(order.getProperty())
                    && ArrayUtils.contains(supportedSortFields, order.getProperty()))
        .map(
            order -> {
              String elasticField =
                  this.fieldMap.getOrDefault(order.getProperty(), order.getProperty());
              return order.isAscending()
                  ? Sort.Order.asc(elasticField)
                  : Sort.Order.desc(elasticField);
            })
        .toList();
  }

  List<Sort.Order> generateCandidateFullNameSortOrder(Pageable pageable) {
    List<Sort.Order> orders = new ArrayList<>();
    Sort.Order candidateSortOrder = pageable.getSort().getOrderFor(fields[0]);
    if (candidateSortOrder != null) {
      boolean asc = candidateSortOrder.isAscending();
      String salutationKeywordField = CANDIDATE_SALUTATION_FIELD + KEYWORD_SUFFIX;
      String firstNameKeywordField = CANDIDATE_FIRST_NAME_FIELD + KEYWORD_SUFFIX;
      String lastNameKeywordField = CANDIDATE_LAST_NAME_FIELD + KEYWORD_SUFFIX;
      orders.add(
          asc ? Sort.Order.asc(salutationKeywordField) : Sort.Order.desc(salutationKeywordField));
      orders.add(
          asc ? Sort.Order.asc(firstNameKeywordField) : Sort.Order.desc(firstNameKeywordField));
      orders.add(
          asc ? Sort.Order.asc(lastNameKeywordField) : Sort.Order.desc(lastNameKeywordField));
    }
    return orders;
  }

  private BoolQuery.Builder filterSearchText(BoolQuery.Builder boolQ, String filterText) {
    boolQ
        .filter(qs -> qs.term(ter -> ter.field(CANDIDATE_IS_DELETED_FIELD).value(false)))
        .should(
            qs ->
                qs.queryString(
                    qk ->
                        qk.fields(
                                List.of(
                                    CANDIDATE_FIRST_NAME_FIELD,
                                    CANDIDATE_LAST_NAME_FIELD,
                                    CANDIDATE_SALUTATION_FIELD + KEYWORD_SUFFIX))
                            .query(String.format("*%s*", filterText.trim().toLowerCase()))
                            .boost(DEFAULT_BOOST)
                            .defaultOperator(Operator.And)
                            .analyzeWildcard(true)))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            INTERVIEW_TITLE_FIELD, filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.match(
                    qk ->
                        qk.field(CANDIDATE_STATUS_TITLE_FIELD)
                            .query(filterText.trim().toLowerCase())))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            CANDIDATE_PRIORITY_FIELD, filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            INTERVIEW_DATE_TIME_FIELD, filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            "interviews.result.flexibility", filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            "interviews.result.remark", filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            "interviews.result.oral", filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            "interviews.result.english", filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            "interviews.result.logical", filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.match(
                    qk -> qk.field(CANDIDATE_GENDER_FIELD).query(filterText.trim().toLowerCase())))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            CANDIDATE_TELEPHONE_FIELD, filterText.trim().toLowerCase(), qk)))
        .should(
            qs ->
                qs.wildcard(
                    qk ->
                        filterWithWildcard(
                            UNIVERSITY_NAME_FIELD, filterText.trim().toLowerCase(), qk)))
        .minimumShouldMatch("1");
    return boolQ;
  }

  private WildcardQuery.Builder filterWithWildcard(
      String field, String value, WildcardQuery.Builder queryBuilder) {
    return queryBuilder
        .field(field + KEYWORD_SUFFIX)
        .value(String.format("*%s*", value))
        .caseInsensitive(true)
        .boost(DEFAULT_BOOST);
  }

  private void filterFullText(Query.Builder queryBuilder, String filterText) {
    queryBuilder.bool(
        q ->
            q.should(qs -> qs.queryString(qk -> queryFullName(filterText, qk)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard(INTERVIEW_TITLE_FIELD, filterText, qk)))
                .should(
                    qs -> qs.match(qk -> qk.field(CANDIDATE_STATUS_TITLE_FIELD).query(filterText)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard(CANDIDATE_PRIORITY_FIELD, filterText, qk)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard(INTERVIEW_DATE_TIME_FIELD, filterText, qk)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk ->
                                filterWithWildcard(
                                    "interviews.result.flexibility", filterText, qk)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard("interviews.result.remark", filterText, qk)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard("interviews.result.oral", filterText, qk)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard("interviews.result.english", filterText, qk)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard("interviews.result.logical", filterText, qk)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard(CANDIDATE_TELEPHONE_FIELD, filterText, qk)))
                .should(qs -> qs.match(qk -> qk.field(CANDIDATE_GENDER_FIELD).query(filterText)))
                .should(
                    qs ->
                        qs.wildcard(
                            qk -> filterWithWildcard(UNIVERSITY_NAME_FIELD, filterText, qk))));
  }

  private QueryStringQuery.Builder queryFullName(String filter, QueryStringQuery.Builder builder) {
    return builder
        .fields(
            List.of(
                CANDIDATE_FIRST_NAME_FIELD,
                CANDIDATE_LAST_NAME_FIELD,
                CANDIDATE_SALUTATION_FIELD + KEYWORD_SUFFIX))
        .defaultOperator(Operator.And)
        .query(String.format("*%s*", filter))
        .analyzeWildcard(Boolean.TRUE)
        .boost(DEFAULT_BOOST);
  }

  private void filterCandidateStatus(
      BoolQuery.Builder query, CandidateElasticsearchRequest request) {
    if (!request.getCandidateStatus().isBlank()) {
      query
          .filter(
              ft ->
                  ft.match(
                      t ->
                          t.field(CANDIDATE_STATUS_TITLE_FIELD)
                              .query(request.getCandidateStatus().trim().toLowerCase())))
          .filter(ft -> ft.term(t -> t.field(CANDIDATE_IS_DELETED_FIELD).value(false)));
    }
  }

  private void advanceFilterSpecificField(
      CandidateElasticsearchRequest request, Query.Builder builder) {
    builder.bool(
        query -> {
          query.filter(q -> q.term(term -> term.field(CANDIDATE_IS_DELETED_FIELD).value(false)));
          if (!request.getCandidateName().isBlank()) {
            query.filter(
                ft -> ft.queryString(qs -> this.filterFullName(qs, request.getCandidateName())));
          }
          if (!request.getUniversity().isBlank()) {
            query.filter(
                ft ->
                    ft.wildcard(
                        wc ->
                            this.filterBy_field_wildCardQuery(
                                wc, request.getUniversity(), UNIVERSITY_NAME_FIELD)));
          }
          if (!request.getPosition().isBlank()) {
            query.filter(
                ft ->
                    ft.wildcard(
                        wc ->
                            this.filterBy_field_wildCardQuery(
                                wc, request.getPosition(), INTERVIEW_TITLE_FIELD)));
          }
          if (!request.getGender().isBlank()) {
            query.filter(
                ft ->
                    ft.match(
                        mt ->
                            mt.field(CANDIDATE_GENDER_FIELD)
                                .query(request.getGender().trim().toLowerCase())));
          }
          if (request.getGpa() > 0) {
            query.filter(
                ft ->
                    ft.range(
                        range ->
                            range
                                .field(CANDIDATE_GPA_FIELD)
                                .gte(JsonData.of(request.getGpa()))
                                .lt(JsonData.of(request.getGpa() + 1))));
          }

          return query;
        });
  }

  private QueryStringQuery.Builder filterFullName(QueryStringQuery.Builder builder, String filter) {
    return builder
        .fields(
            List.of(
                CANDIDATE_FIRST_NAME_FIELD,
                CANDIDATE_LAST_NAME_FIELD,
                CANDIDATE_SALUTATION_FIELD + KEYWORD_SUFFIX))
        .query(String.format("*%s*", filter.trim().toLowerCase()))
        .boost(DEFAULT_BOOST)
        .defaultOperator(Operator.And)
        .analyzeWildcard(true);
  }

  private WildcardQuery.Builder filterBy_field_wildCardQuery(
      WildcardQuery.Builder builder, String valueOfField, String field) {

    return this.filterWithWildcard(field, valueOfField.trim().toLowerCase(), builder);
  }
}
