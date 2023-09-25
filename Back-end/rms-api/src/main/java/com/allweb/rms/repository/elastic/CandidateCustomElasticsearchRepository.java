package com.allweb.rms.repository.elastic;

import com.allweb.rms.entity.dto.CandidateElasticsearchRequest;
import com.allweb.rms.entity.elastic.CandidateElasticsearchDocument;
import com.allweb.rms.entity.elastic.CandidateReportElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.CandidateStatus;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Result;
import com.allweb.rms.entity.jpa.University;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface CandidateCustomElasticsearchRepository {
  Page<CandidateElasticsearchDocument> elasticAdvanceSearch(CandidateElasticsearchRequest request);

  Page<CandidateReportElasticsearchDocument> getReportFromElasticsearch(
      CandidateElasticsearchRequest request);

  void updateCandidateInterview(
      int candidateId,
      @Nullable Interview lastInterview,
      @Nullable Result interviewResult,
      int candidateInterviewCount);

  void updateCandidateReportInterview(
      int candidateId, @Nullable Interview lastInterview, @Nullable Result interviewResult);

  void updateCandidate(
      Candidate candidate,
      @Nullable CandidateStatus candidateStatus,
      List<University> universities);

  void updateCandidateReport(Candidate candidate, List<University> universities);

  CandidateReportElasticsearchDocument saveCandidateReportElasticDocument(
      CandidateReportElasticsearchDocument candidateElasticsearchDocument);

  void incrementCandidateReminderCount(int candidateId, int increment);

  void decrementCandidateReminderCount(int candidateId, int decrement);

  void updateCandidateReminderCount(int candidateId, int reminderCount);

  void deleteCandidateReportByCandidateId(int candidateId);
}
