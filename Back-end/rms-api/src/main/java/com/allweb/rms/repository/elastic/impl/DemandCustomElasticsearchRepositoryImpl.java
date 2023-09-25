package com.allweb.rms.repository.elastic.impl;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import com.allweb.rms.entity.dto.DemandElasticsearchRequest;
import com.allweb.rms.entity.elastic.DemandElasticsearchDocument;
import com.allweb.rms.event.ElasticsearchDocumentIndexNotFoundEvent;
import com.allweb.rms.repository.elastic.DemandCustomElasticsearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class DemandCustomElasticsearchRepositoryImpl
    implements DemandCustomElasticsearchRepository {
  private static final String DEMAND_IS_DELETED_FIELD = "is_deleted";
  private static final String SORT_BY_PROJECT = "project.name";
  private final ElasticsearchOperations elasticsearchOperations;
  private static final String PROJECT_NAME = "project.name";
  private static final String JOB_TITLE = "jobDescription.title";
  private static final String Qty_OF_CANDIDATE = "nb_required";
  private static final String DEAD_LINE = "project.name";

  private final ApplicationEventPublisher applicationEventPublisher;
  private static final String KEYWORD_SUFFIX = ".keyword";
  private static final String[] SUPPORTED_SORT_FIELDS =
          new String[] {
                 SORT_BY_PROJECT,"jobDescription.title","dead_line","created_at","active"
          };
  private static final Float DEFAULT_BOOST = 1.0F;

  public DemandCustomElasticsearchRepositoryImpl(
      ElasticsearchOperations elasticsearchOperations,
      ApplicationEventPublisher applicationEventPublisher) {
    this.elasticsearchOperations = elasticsearchOperations;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  public Page<DemandElasticsearchDocument> findAllByElasticsearch(
      DemandElasticsearchRequest request) {
    Pageable keywordPrefixedPageable =
            ElasticsearchPageableUtils.resolveKeywordPrefixedPageable(request.getPageable(), SUPPORTED_SORT_FIELDS);
    if(request.getSortByField().contains("dead_line") || request.getSortByField().contains("active")){
       keywordPrefixedPageable = request.getPageable();
    }
    NativeQuery nativeQuery =
        new NativeQueryBuilder()
            .withQuery(
                query -> {
                  this.advanceFilterSpecificField(request, query);
                  return query;
                })
                .withPageable(keywordPrefixedPageable)
            .build();
    try {
      SearchHits<DemandElasticsearchDocument> searchHits =
          this.elasticsearchOperations.search(
                  nativeQuery,
              DemandElasticsearchDocument.class,
              IndexCoordinates.of(DemandElasticsearchDocument.INDEX_NAME));
      SearchPage<DemandElasticsearchDocument> searchPage =
          SearchHitSupport.searchPageFor(searchHits,keywordPrefixedPageable);
      return searchPage.map(SearchHit::getContent);
    } catch (NoSuchIndexException noSuchIndexException) {
      this.applicationEventPublisher.publishEvent(
          new ElasticsearchDocumentIndexNotFoundEvent(
              this, DemandElasticsearchDocument.INDEX_NAME));
    }
    catch (Exception ex){
      log.error("",ex);
    }

    return new PageImpl<>(new ArrayList<>(),keywordPrefixedPageable,0);

  }

  private void advanceFilterSpecificField(
      DemandElasticsearchRequest request, Query.Builder builder) {
    builder.bool(
            query ->{
                    query.filter(
                            ft ->
                                    ft.term(
                                            term -> term.field("is_deleted").value(request.isDeleted())
                                    )
                    );
                    if (request.getFilter() != null){
                      this.filterSearchText(query,request.getFilter());
                    }
                    return query;
                            }
    );
  }
  private BoolQuery.Builder filterSearchText(BoolQuery.Builder boolQ, String filterText) {
    if (!isNumeric(filterText)) {
      boolQ
          .should(
              qs ->
                  qs.wildcard(
                      qk -> filterWithWildcard(JOB_TITLE, filterText.trim().toLowerCase(), qk)))
          .should(
              qs ->
                  qs.wildcard(
                      qk -> filterWithWildcard(PROJECT_NAME, filterText.trim().toLowerCase(), qk)))
          .minimumShouldMatch("1");
    }
    else {
        boolQ
                .should(
                        qs ->
                                qs.match(qk -> qk.field(Qty_OF_CANDIDATE).query(filterText.trim().toLowerCase())))
                .minimumShouldMatch("1");
    }
      return boolQ;
  }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
  private WildcardQuery.Builder filterWithWildcard(
          String field, String value, WildcardQuery.Builder queryBuilder) {
    return queryBuilder
            .field(field + KEYWORD_SUFFIX)
            .value(String.format("*%s*", value))
            .caseInsensitive(true)
            .boost(DEFAULT_BOOST);
  }
}
