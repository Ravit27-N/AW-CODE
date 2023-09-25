package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class ReminderHardDeleteHandlerChain extends BaseHandlerChain {
  ReminderHardDeleteHandlerChain(
      ReminderHardDeleteHandler reminderHardDeleteHandler,
      InterviewReminderUpdateHandler interviewReminderUpdateHandler,
      CandidateReminderUpdateHandler candidateReminderUpdateHandler) {
    this.addHandler(reminderHardDeleteHandler);
    this.addHandler(interviewReminderUpdateHandler);
    this.addHandler(candidateReminderUpdateHandler);
  }
}
