package com.allweb.rms.config;

import com.allweb.rms.entity.dto.CandidateDTO;
import com.allweb.rms.entity.dto.InterviewResponse;
import com.allweb.rms.entity.dto.ReminderRequest;
import com.allweb.rms.entity.dto.ReminderResponse;
import com.allweb.rms.entity.elastic.CandidateElasticsearchDocument;
import com.allweb.rms.entity.elastic.CandidateReportElasticsearchDocument;
import com.allweb.rms.entity.elastic.InterviewElasticsearchDocument;
import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import com.allweb.rms.entity.elastic.Result;
import com.allweb.rms.entity.elastic.University;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.InterviewStatus;
import com.allweb.rms.entity.jpa.Reminder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper(ObjectMapper objectMapper) {
    ModelMapper modelMapper = new ModelMapper();
    configureReminderToRequestMapping(modelMapper);
    configureReminderToResponseMapping(modelMapper);
    modelMapper
        .getConfiguration()
        .setMatchingStrategy(MatchingStrategies.STANDARD)
        .setSkipNullEnabled(true);
    configureCandidateElasticDocumentToCandidateDTOMapping(modelMapper, objectMapper);
    configureCandidateReportElasticDocumentToCandidateDTOMapping(modelMapper, objectMapper);
    configureReminderElasticDocumentToReminderResponse(modelMapper);
    configureInterviewElasticsearchDocumentToInterviewResponse(modelMapper);
    configureInterviewToElasticsearchDocument(modelMapper);
    configureInterviewToResponse(modelMapper);
    return modelMapper;
  }

  private void configureReminderToRequestMapping(ModelMapper modelMapper) {
    modelMapper
        .createTypeMap(Reminder.class, ReminderRequest.class)
        .addMappings(
            mapper ->
                mapper.map(
                    (Reminder reminder) -> reminder.getReminderType().getId(),
                    ReminderRequest::setReminderType))
        .addMappings(
            mapper ->
                mapper.map(
                    (Reminder reminder) -> reminder.getCandidate().getId(),
                    ReminderRequest::setCandidateId))
        .addMappings(
            mapper ->
                mapper.map(
                    (Reminder reminder) -> reminder.getInterview().getId(),
                    ReminderRequest::setInterviewId));
  }

  private void configureReminderToResponseMapping(ModelMapper modelMapper) {
    modelMapper
        .createTypeMap(Reminder.class, ReminderResponse.class)
        .addMappings(
            mapper ->
                mapper.map(
                    (Reminder reminder) -> reminder.getReminderType().getId(),
                    ReminderResponse::setReminderType))
        .addMappings(
            mapper ->
                mapper
                    .using(
                        (MappingContext<Candidate, Map<String, Object>> mappingContext) -> {
                          Map<String, Object> candidateMap = new HashMap<>();
                          Candidate candidate = mappingContext.getSource();
                          if (candidate != null) {
                            candidateMap.put("id", candidate.getId());
                            candidateMap.put(
                                "fullName",
                                String.format(
                                    "%s %s %s",
                                    candidate.getSalutation(),
                                    candidate.getFirstname(),
                                    candidate.getLastname()));
                            return candidateMap;
                          }
                          return null;
                        })
                    .map(Reminder::getCandidate, ReminderResponse::setCandidate))
        .addMappings(
            mapper ->
                mapper
                    .using(
                        (MappingContext<Interview, Map<String, Object>> mappingContext) -> {
                          Map<String, Object> interviewMap = new HashMap<>();
                          Interview interview = mappingContext.getSource();
                          if (interview != null) {
                            interviewMap.put("id", interview.getId());
                            interviewMap.put("title", interview.getTitle());
                            return interviewMap;
                          }
                          return null;
                        })
                    .map(Reminder::getInterview, ReminderResponse::setInterview));
  }

  private void configureCandidateElasticDocumentToCandidateDTOMapping(
      ModelMapper modelMapper, ObjectMapper objectMapper) {
    modelMapper
        .createTypeMap(CandidateElasticsearchDocument.class, CandidateDTO.class)
        .addMappings(
            mapping ->
                mapping
                    .using(context -> convertCandidateStatusToJsonNode(context, objectMapper))
                    .map(
                        CandidateElasticsearchDocument::getCandidateStatus,
                        CandidateDTO::setCandidateStatus))
        .addMappings(
            mapping ->
                mapping
                    .using(context -> convertCandidateInterviewToJsonNode(context, objectMapper))
                    .map(
                        CandidateElasticsearchDocument::getInterviews, CandidateDTO::setInterviews))
        .addMappings(
            mapping ->
                mapping
                    .using(context -> convertCandidateUniversityToJsonNode(context, objectMapper))
                    .map(
                        CandidateElasticsearchDocument::getUniversities,
                        CandidateDTO::setUniversities));
  }

  private JsonNode convertCandidateStatusToJsonNode(
      MappingContext<Object, Object> mappingContext, ObjectMapper objectMapper) {
    if (mappingContext.getSource() != null) {
      CandidateElasticsearchDocument.CandidateStatus candidateStatus =
          (CandidateElasticsearchDocument.CandidateStatus) mappingContext.getSource();
      if (candidateStatus.getId() > 0) {
        return objectMapper.valueToTree(candidateStatus);
      }
    }
    return objectMapper.createObjectNode();
  }

  private JsonNode convertCandidateInterviewToJsonNode(
      MappingContext<Object, Object> mappingContext, ObjectMapper objectMapper) {
    if (mappingContext.getSource() != null) {
      CandidateElasticsearchDocument.Interview interview =
          (CandidateElasticsearchDocument.Interview) mappingContext.getSource();
      if (interview.getId() > 0) {
        return objectMapper.valueToTree(interview);
      }
    }
    return objectMapper.createObjectNode();
  }

  @SuppressWarnings("unchecked")
  private JsonNode convertCandidateUniversityToJsonNode(
      MappingContext<Object, Object> mappingContext, ObjectMapper objectMapper) {
    if (mappingContext.getSource() != null) {
      List<University> universities = (List<University>) mappingContext.getSource();
      return objectMapper.valueToTree(
          universities.stream()
              .filter(university -> university.getId() > 0).toList());
    }
    return objectMapper.createObjectNode();
  }

  private void configureCandidateReportElasticDocumentToCandidateDTOMapping(
      ModelMapper modelMapper, ObjectMapper objectMapper) {
    Converter<Map<String, Object>, JsonNode> mapToJsonNodeConverter =
        context ->
            context.getSource() != null
                ? objectMapper.valueToTree(context.getSource())
                : objectMapper.createObjectNode();
    modelMapper
        .createTypeMap(CandidateReportElasticsearchDocument.class, CandidateDTO.class)
        .addMappings(
            mapping ->
                mapping.map(
                    CandidateReportElasticsearchDocument::getFirstName, CandidateDTO::setFirstname))
        .addMappings(
            mapping ->
                mapping.map(
                    CandidateReportElasticsearchDocument::getLastName, CandidateDTO::setLastname))
        .addMappings(
            mapping ->
                mapping
                    .using(
                        context -> {
                          if (context.getSource() != null) {
                            CandidateReportElasticsearchDocument.Interview interview =
                                (CandidateReportElasticsearchDocument.Interview)
                                    context.getSource();
                            JsonNode jsonNode = objectMapper.valueToTree(interview);
                            Result interviewResult = interview.getResult();
                            if (interviewResult != null && interviewResult.getId() <= 0) {
                              ((ObjectNode) jsonNode)
                                  .set(
                                      "result",
                                      objectMapper.valueToTree(new HashMap<String, Object>()));
                            }
                            return jsonNode;
                          }
                          return objectMapper.createObjectNode();
                        })
                    .map(
                        CandidateReportElasticsearchDocument::getInterview,
                        CandidateDTO::setInterviews))
        .addMappings(
            mapping ->
                mapping
                    .using(mapToJsonNodeConverter)
                    .map(
                        CandidateReportElasticsearchDocument::getUniversities,
                        CandidateDTO::setUniversities));
  }

  private void configureReminderElasticDocumentToReminderResponse(ModelMapper modelMapper) {
    modelMapper
        .createTypeMap(ReminderElasticsearchDocument.class, ReminderResponse.class)
        .addMappings(
            mapping ->
                mapping
                    .using(
                        context -> {
                          Map<String, Object> candidateMap = null;
                          if (context.getSource() != null) {
                            com.allweb.rms.entity.elastic.Candidate candidate =
                                (com.allweb.rms.entity.elastic.Candidate) context.getSource();
                            if (candidate.getId() != null) {
                              candidateMap = new HashMap<>();
                              candidateMap.put("id", candidate.getId());
                              candidateMap.put("fullName", candidate.getFullName());
                            }
                          }
                          return candidateMap;
                        })
                    .map(
                        ReminderElasticsearchDocument::getCandidate,
                        ReminderResponse::setCandidate))
        .addMappings(
            mapping ->
                mapping
                    .using(
                        context -> {
                          Map<String, Object> interviewMap = null;
                          if (context.getSource() != null) {
                            ReminderElasticsearchDocument.Interview interview =
                                (ReminderElasticsearchDocument.Interview) context.getSource();
                            if (interview.getId() != null) {
                              interviewMap = new HashMap<>();
                              interviewMap.put("id", interview.getId());
                              interviewMap.put("title", interview.getTitle());
                            }
                          }
                          return interviewMap;
                        })
                    .map(
                        ReminderElasticsearchDocument::getInterview,
                        ReminderResponse::setInterview));
  }

  private void configureInterviewElasticsearchDocumentToInterviewResponse(ModelMapper modelMapper) {
    modelMapper
        .createTypeMap(InterviewElasticsearchDocument.class, InterviewResponse.class)
        .addMappings(
            mapping ->
                mapping.map(
                    interviewElasticsearchDocument ->
                        interviewElasticsearchDocument.getCandidate().getId(),
                    InterviewResponse::setCandidateId))
        .addMappings(
            mapping ->
                mapping.map(
                    interviewElasticsearchDocument ->
                        interviewElasticsearchDocument.getCandidate().getFullName(),
                    InterviewResponse::setCandidateFullName));
  }

  private void configureInterviewToElasticsearchDocument(ModelMapper modelMapper) {
    modelMapper
        .createTypeMap(Interview.class, InterviewElasticsearchDocument.class)
        .addMappings(
            mapping ->
                mapping
                    .using(getInterviewStatusToStringConverter())
                    .map(Interview::getInterviewStatus, InterviewElasticsearchDocument::setStatus))
        .addMappings(
            mapping ->
                mapping
                    .using(
                        context -> {
                          com.allweb.rms.entity.elastic.Candidate interviewCandidate =
                              new com.allweb.rms.entity.elastic.Candidate();
                          if (context.getSource() != null) {
                            Candidate candidate = (Candidate) context.getSource();
                            interviewCandidate.setId(candidate.getId());
                            interviewCandidate.setFullName(
                                String.format(
                                    "%s %s %s",
                                    candidate.getSalutation(),
                                    candidate.getFirstname(),
                                    candidate.getLastname()));
                          }
                          return interviewCandidate;
                        })
                    .map(Interview::getCandidate, InterviewElasticsearchDocument::setCandidate));
  }

  private void configureInterviewToResponse(ModelMapper modelMapper) {
    modelMapper
        .createTypeMap(Interview.class, InterviewResponse.class)
        .addMappings(
            mapping ->
                mapping
                    .using(getInterviewStatusToStringConverter())
                    .map(Interview::getInterviewStatus, InterviewResponse::setStatus));
  }

  private Converter<InterviewStatus, String> getInterviewStatusToStringConverter() {
    return context -> {
      String status = "";
      if (context.getSource() != null) {
        InterviewStatus interviewStatus = context.getSource();
        status = interviewStatus.getName();
      }
      return status;
    };
  }
}
