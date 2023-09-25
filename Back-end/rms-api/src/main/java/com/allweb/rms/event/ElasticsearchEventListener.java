package com.allweb.rms.event;

import com.allweb.rms.entity.elastic.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ElasticsearchEventListener {
  private static final String NOT_SUCCESS_MESSAGE = " not ";
  private static final String ELASTICSEARCH_TEMPLATE_PATH = "elasticsearch/templates";
  private final String candidateTemplatePath;
  private final String candidateReportTemplatePath;
  private final String interviewTemplatePath;
  private final String reminderTemplatePath;
  private final String demandTemplatePath;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ElasticsearchOperations elasticsearchOperations;
  private final RestClient restHighLevelClient;

  public ElasticsearchEventListener(
           ElasticsearchOperations elasticsearchOperations, RestClient restHighLevelClient) {
    this.elasticsearchOperations = elasticsearchOperations;
    this.restHighLevelClient = restHighLevelClient;
    candidateTemplatePath =
        ELASTICSEARCH_TEMPLATE_PATH + File.separator + "candidate-index-template.json";
    candidateReportTemplatePath =
        ELASTICSEARCH_TEMPLATE_PATH + File.separator + "candidate-report-index-template.json";
    interviewTemplatePath =
        ELASTICSEARCH_TEMPLATE_PATH + File.separator + "interview-index-template.json";
    reminderTemplatePath =
        ELASTICSEARCH_TEMPLATE_PATH + File.separator + "reminder-index-template.json";
    demandTemplatePath = ELASTICSEARCH_TEMPLATE_PATH + File.separator + "demand-index-template.json";
  }

  @EventListener(ApplicationStartedEvent.class)
  public void initializeElasticsearch() {
    try {
      List<String> existingIndices = this.findAllExistingElasticsearchIndices();
      if (!existingIndices.contains(CandidateElasticsearchDocument.INDEX_NAME)) {
        boolean candidateIndexInitialized =
            this.initializeElasticsearchIndex(
                elasticsearchOperations,
                CandidateElasticsearchDocument.INDEX_NAME,
                candidateTemplatePath);
        log.info(
            "Candidate Elasticsearch index is{}initialized.",
            candidateIndexInitialized ? " " : NOT_SUCCESS_MESSAGE);
      }
      if (!existingIndices.contains(CandidateReportElasticsearchDocument.INDEX_NAME)) {
        boolean candidateReportIndexInitialized =
            this.initializeElasticsearchIndex(
                elasticsearchOperations,
                CandidateReportElasticsearchDocument.INDEX_NAME,
                candidateReportTemplatePath);
        log.info(
            "Candidate report Elasticsearch index is{}initialized.",
            candidateReportIndexInitialized ? " " : NOT_SUCCESS_MESSAGE);
      }
      if (!existingIndices.contains(InterviewElasticsearchDocument.INDEX_NAME)) {
        boolean interviewIndexInitialized =
            this.initializeElasticsearchIndex(
                elasticsearchOperations,
                InterviewElasticsearchDocument.INDEX_NAME,
                interviewTemplatePath);
        log.info(
            "Interview Elasticsearch index is{}initialized.",
            interviewIndexInitialized ? " " : NOT_SUCCESS_MESSAGE);
      }
      if (!existingIndices.contains(ReminderElasticsearchDocument.INDEX_NAME)) {
        boolean reminderIndexInitialized =
            this.initializeElasticsearchIndex(
                elasticsearchOperations,
                ReminderElasticsearchDocument.INDEX_NAME,
                reminderTemplatePath);
        log.info(
            "Reminder Elasticsearch index is{}initialized.",
            reminderIndexInitialized ? " " : NOT_SUCCESS_MESSAGE);
      }
      if (!existingIndices.contains(DemandElasticsearchDocument.INDEX_NAME)) {
        boolean demandIndexInitialized =
                this.initializeElasticsearchIndex(
                        elasticsearchOperations,
                        DemandElasticsearchDocument.INDEX_NAME,
                        demandTemplatePath);
        log.info(
                "Reminder Elasticsearch index is{}initialized.",
                demandIndexInitialized ? " " : NOT_SUCCESS_MESSAGE);
      }
    } catch (IOException | ElasticsearchException e) {
      log.debug(e.getMessage(), e);
    }
  }

  @EventListener(ElasticsearchDocumentIndexNotFoundEvent.class)
  public void handleElasticsearchDocumentIndexNotFound(
      ElasticsearchDocumentIndexNotFoundEvent elasticsearchDocumentIndexNotFoundEvent) {
    try {
      boolean indexCreated = false;
      if (CandidateElasticsearchDocument.INDEX_NAME.equals(
          elasticsearchDocumentIndexNotFoundEvent.getIndexName())) {
        indexCreated =
            this.initializeElasticsearchIndex(
                elasticsearchOperations,
                CandidateElasticsearchDocument.INDEX_NAME,
                candidateTemplatePath);
      } else if (CandidateReportElasticsearchDocument.INDEX_NAME.equals(
          elasticsearchDocumentIndexNotFoundEvent.getIndexName())) {
        indexCreated =
            this.initializeElasticsearchIndex(
                elasticsearchOperations,
                CandidateReportElasticsearchDocument.INDEX_NAME,
                candidateReportTemplatePath);
      } else if (InterviewElasticsearchDocument.INDEX_NAME.equals(
          elasticsearchDocumentIndexNotFoundEvent.getIndexName())) {
        indexCreated =
            this.initializeElasticsearchIndex(
                elasticsearchOperations,
                InterviewElasticsearchDocument.INDEX_NAME,
                interviewTemplatePath);
      } else if (ReminderElasticsearchDocument.INDEX_NAME.equals(
          elasticsearchDocumentIndexNotFoundEvent.getIndexName())) {
        indexCreated =
            this.initializeElasticsearchIndex(
                elasticsearchOperations,
                ReminderElasticsearchDocument.INDEX_NAME,
                reminderTemplatePath);
      }
      else if (DemandElasticsearchDocument.INDEX_NAME.equals(
              elasticsearchDocumentIndexNotFoundEvent.getIndexName())) {
        indexCreated =
                this.initializeElasticsearchIndex(
                        elasticsearchOperations,
                        DemandElasticsearchDocument.INDEX_NAME,
                        demandTemplatePath);
      }
      log.debug(
          "Elasticsearch index \"{}\" is{}created.",
          elasticsearchDocumentIndexNotFoundEvent.getIndexName(),
          indexCreated ? " " : NOT_SUCCESS_MESSAGE);
    } catch (IOException | ElasticsearchException exception) {
      log.debug(exception.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  private List<String> findAllExistingElasticsearchIndices() throws IOException {
    InputStream responseStream =
        this.restHighLevelClient
            .performRequest(new Request("GET", "/_cat/indices/idx_*?format=json"))
            .getEntity()
            .getContent();
    List<Map<String, String>> indices = objectMapper.readValue(responseStream, List.class);
    return indices.stream().map(index -> index.get("index")).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private boolean initializeElasticsearchIndex(
      ElasticsearchOperations elasticsearchOperations, String indexName, String indexTemplatePath)
      throws IOException {
    IndexOperations indexOperations =
        elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
    if (!indexOperations.existsTemplate(indexName)) {
      File indexTemplateFile = new ClassPathResource(indexTemplatePath).getFile();
      String indexTemplateContent =
          FileUtils.readFileToString(indexTemplateFile, StandardCharsets.UTF_8);
      Map<String, Object> indexTemplateContentMap =
          objectMapper.readValue(indexTemplateContent, Map.class);
      PutTemplateRequest putTemplateRequest =
          PutTemplateRequest.builder(indexName, indexName)
              .withMappings(
                  Document.from((Map<String, Object>) indexTemplateContentMap.get("mappings")))
              .withOrder((int) indexTemplateContentMap.get("order"))
              .build();
      indexOperations.putTemplate(putTemplateRequest);
    }
    return indexOperations.create();
  }
}
