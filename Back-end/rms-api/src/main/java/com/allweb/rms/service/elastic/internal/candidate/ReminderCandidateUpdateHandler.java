package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.repository.elastic.ReminderElasticsearchRepository;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.Handler;
import com.allweb.rms.utils.ReminderType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class ReminderCandidateUpdateHandler implements Handler {
  private final ReminderElasticsearchRepository reminderElasticsearchRepository;

  ReminderCandidateUpdateHandler(ReminderElasticsearchRepository reminderElasticsearchRepository) {
    this.reminderElasticsearchRepository = reminderElasticsearchRepository;
  }

  @Override
  public void handle(ChainContext context) {
    Candidate candidate = (Candidate) context.get(ElasticConstants.CANDIDATE_OBJECT_KEY);
    List<ReminderElasticsearchDocument> candidateReminderList =
        this.reminderElasticsearchRepository.findByReminderTypeAndCandidateId(
            ReminderType.SPECIAL.getValue(), candidate.getId());
    if (!candidateReminderList.isEmpty()) {
      if (context.contains(ElasticConstants.OPERATION_KEY)
          && ElasticConstants.DELETE_OPERATION.equals(
              context.get(ElasticConstants.OPERATION_KEY))) {
        this.reminderElasticsearchRepository.deleteAll(candidateReminderList);
        return;
      }
      this.reminderElasticsearchRepository.updateReminderCandidateElasticsearchDocument(
          candidateReminderList, candidate);
    }
  }
}