package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class InterviewHardDeleteHandlerChain extends BaseHandlerChain {
  InterviewHardDeleteHandlerChain(
      InterviewHardDeleteHandler interviewHardDeleteHandler,
      CandidateInterviewUpdateHandler candidateInterviewUpdateHandler,
      CandidateReportInterviewUpdateHandler candidateReportInterviewUpdateHandler,
      ReminderInterviewHardDeleteHandler reminderInterviewHardDeleteHandler) {
    this.addHandler(interviewHardDeleteHandler);
    this.addHandler(candidateInterviewUpdateHandler);
    this.addHandler(candidateReportInterviewUpdateHandler);
    this.addHandler(reminderInterviewHardDeleteHandler);
  }
}
