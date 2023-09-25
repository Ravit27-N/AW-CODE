package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.dto.ReminderAdvanceFilterRequest;
import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Interview;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ReminderCustomElasticsearchRepository {
  Page<ReminderElasticsearchDocument> elasticSearchAdvanceFilters(
      ReminderAdvanceFilterRequest request, Pageable pageable);

  void deleteElasticsearchDocumentById(int id);

  void updateReminderInterviewElasticsearchDocument(
      List<ReminderElasticsearchDocument> reminderElasticDocList, Interview interview);

  void updateReminderCandidateElasticsearchDocument(
      List<ReminderElasticsearchDocument> reminderElasticDocList, Candidate candidate);

}
