package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class CandidateInsertHandlerChain extends BaseHandlerChain {
  CandidateInsertHandlerChain(
      CandidateInsertHandler candidateInsertHandler,
      CandidateReportCandidateInsertHandler candidateReportCandidateInsertHandler) {
    this.addHandler(candidateInsertHandler);
    this.addHandler(candidateReportCandidateInsertHandler);
  }
}
