package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class CandidateUpdateHandlerChain extends BaseHandlerChain {

  CandidateUpdateHandlerChain(
      CandidateUpdateHandler candidateUpdateHandler,
      CandidateReportCandidateUpdateHandler candidateReportCandidateUpdateHandler,
      InterviewCandidateUpdateHandler interviewCandidateUpdateHandler,
      ReminderCandidateHardDeleteHandler reminderCandidateHardDeleteHandler) {
    this.addHandler(candidateUpdateHandler);
    this.addHandler(candidateReportCandidateUpdateHandler);
    this.addHandler(interviewCandidateUpdateHandler);
    this.addHandler(reminderCandidateHardDeleteHandler);
  }
}
