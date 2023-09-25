package com.allweb.rms.service;

import com.allweb.rms.repository.jpa.CandidateRepository;
import com.allweb.rms.repository.jpa.InterviewRepository;
import com.allweb.rms.repository.jpa.ReminderRepository;
import com.allweb.rms.repository.jpa.ResultRepository;
import com.google.api.client.util.Strings;
import com.google.common.collect.Maps;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

  private final CandidateService candidateService;
  private final InterviewRepository interviewRepository;
  private final ReminderRepository reminderRepository;
  private final ResultRepository resultRepository;
  private final CandidateRepository candidateRepository;

  @Autowired
  public DashboardService(
      CandidateService candidateService,
      InterviewRepository interviewRepository,
      ReminderRepository reminderRepository,
      ResultRepository resultRepository,
      CandidateRepository candidateRepository) {
    this.candidateService = candidateService;
    this.interviewRepository = interviewRepository;
    this.reminderRepository = reminderRepository;
    this.resultRepository = resultRepository;
    this.candidateRepository = candidateRepository;
  }

  public Map<String, Object> findTopCandidates(Pageable pageable) {
    List<Map<String, Object>> topCandidates =
        candidateService.findTopCandidateByGpa(pageable).stream()
            .map(
                candidate -> {
                  Map<String, Object> candidateResp = new HashMap<>();
                  candidateResp.put("id", candidate.getId());
                  candidateResp.put("fullName", candidate.getFullName());
                  candidateResp.put("gender", candidate.getGender());
                  candidateResp.put("gpa", candidate.getGpa());
                  candidateResp.put("photoUrl", candidate.getPhotoUrl());
                  return candidateResp;
                })
            .toList();
    return Maps.newHashMap(Map.of("contents", topCandidates));
  }

  public Map<String, Object> countGenderAndStatusCandidate() {
    var genderCountAsJson = candidateRepository.countCandidatesGroupByGender();
    var statusCountJson = candidateRepository.countCandidatesGroupsByStatus();
    if (genderCountAsJson == null) {
      genderCountAsJson = Map.of();
    }
    if (statusCountJson == null) {
      statusCountJson = Map.of();
    }
    candidateRepository.countCandidatesGroupByGender();
    return Maps.newHashMap(Map.of("gender", genderCountAsJson, "status", statusCountJson));
  }

  public Map<String, Object> numbersOfData() {
    return Maps.newHashMap(
        Map.of(
            "candidates", candidateRepository.countCandidatesByDeletedIsFalse(),
            "interviews", interviewRepository.countAllByDeleteFalseAndCandidateDeleteFalse(),
            "reminders", reminderRepository.countAllByDeleteFalse(),
            "results", resultRepository.countAllByCandidateDeleteFalseAndInterviewDeleteFalse(),
            "activities", candidateRepository.countActivitiesByCandidateDeletedIsFalse()));
  }

  public Map<String, Object> reportInterviewsOnGraphByYear(String year) {
    Map<String, Object> reportMap = new HashMap<>();
    if (Strings.isNullOrEmpty(year)) {
      year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    }
    reportMap.put("graph", interviewRepository.reportInterviewsOnGraphStatusByYear(year));
    reportMap.put("total", interviewRepository.reportInterviewOnGraphMonthlyByYear(year));
    return Maps.newHashMap(Map.of("reportInterviews", reportMap));
  }
}
