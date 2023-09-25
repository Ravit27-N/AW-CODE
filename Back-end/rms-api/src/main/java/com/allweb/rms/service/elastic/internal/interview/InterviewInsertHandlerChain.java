package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class InterviewInsertHandlerChain extends BaseHandlerChain {
  InterviewInsertHandlerChain(
      InterviewInsertHandler interviewInsertHandler,
      CandidateInterviewUpdateHandler candidateInterviewUpdateHandler,
      CandidateReportInterviewUpdateHandler candidateReportInterviewUpdateHandler) {
    this.addHandler(interviewInsertHandler);
    this.addHandler(candidateInterviewUpdateHandler);
    this.addHandler(candidateReportInterviewUpdateHandler);
  }
}
