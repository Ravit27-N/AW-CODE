package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.dto.InterviewElasticsearchRequest;
import com.allweb.rms.entity.elastic.InterviewElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Interview;
import java.util.List;
import org.springframework.data.domain.Page;

public interface InterviewCustomElasticsearchRepository {
  Page<InterviewElasticsearchDocument> findAllByElasticsearch(
      InterviewElasticsearchRequest request);

  void updateInterviewElasticsearch(Interview interview, int resultCount);

  void updateInterviewCandidate(
      List<InterviewElasticsearchDocument> interviewList, Candidate candidate);

  void incrementReminderCount(int interviewId, int decrement);

  void decrementReminderCount(int interviewId, int decrement);

  void updateInterviewReminderCount(int interviewId, int reminderCount);
}
