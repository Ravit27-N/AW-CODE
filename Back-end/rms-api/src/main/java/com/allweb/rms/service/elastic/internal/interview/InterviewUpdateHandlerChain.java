package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class InterviewUpdateHandlerChain extends BaseHandlerChain {

  InterviewUpdateHandlerChain(
      InterviewUpdateHandler interviewUpdateHandler,
      CandidateInterviewUpdateHandler candidateInterviewUpdateHandler,
      CandidateReportInterviewUpdateHandler candidateReportInterviewUpdateHandler,
      ReminderInterviewUpdateHandler reminderInterviewUpdateHandler) {
    this.addHandler(interviewUpdateHandler);
    this.addHandler(candidateInterviewUpdateHandler);
    this.addHandler(candidateReportInterviewUpdateHandler);
    this.addHandler(reminderInterviewUpdateHandler);
  }
}
