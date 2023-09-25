package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class CandidateHardDeleteHandlerChain extends BaseHandlerChain {

  CandidateHardDeleteHandlerChain(
      CandidateHardDeleteHandler candidateHardDeleteHandler,
      CandidateReportCandidateHardDeleteHandler candidateReportCandidateHardDeleteHandler,
      InterviewCandidateHardDeleteHandler interviewCandidateHardDeleteHandler,
      ReminderCandidateHardDeleteHandler reminderCandidateHardDeleteHandler) {
    this.addHandler(candidateHardDeleteHandler);
    this.addHandler(candidateReportCandidateHardDeleteHandler);
    this.addHandler(interviewCandidateHardDeleteHandler);
    this.addHandler(reminderCandidateHardDeleteHandler);
  }
}
