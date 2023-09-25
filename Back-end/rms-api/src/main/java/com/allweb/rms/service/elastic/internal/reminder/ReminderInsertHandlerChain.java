package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class ReminderInsertHandlerChain extends BaseHandlerChain {
  ReminderInsertHandlerChain(
      ReminderInsertHandler reminderInsertHandler,
      InterviewReminderUpdateHandler interviewReminderUpdateHandler,
      CandidateReminderUpdateHandler candidateReminderUpdateHandler) {
    this.addHandler(reminderInsertHandler);
    this.addHandler(interviewReminderUpdateHandler);
    this.addHandler(candidateReminderUpdateHandler);
  }
}
