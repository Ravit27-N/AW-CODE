package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ReminderElasticsearchRepository
    extends ElasticsearchRepository<ReminderElasticsearchDocument, Integer>,
        ReminderCustomElasticsearchRepository {
  List<ReminderElasticsearchDocument> findByReminderType(String reminderType);

  void deleteByInterviewId(int interviewId);

  List<ReminderElasticsearchDocument> findByReminderTypeAndCandidateId(
      String reminderType, int candidateId);
  @Query(count = true,query = "")
  long findAllByIsNotDelect();
}
