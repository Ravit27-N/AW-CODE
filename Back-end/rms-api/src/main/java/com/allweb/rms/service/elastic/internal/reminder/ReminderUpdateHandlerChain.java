package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.service.elastic.BaseHandlerChain;
import org.springframework.stereotype.Component;

@Component
class ReminderUpdateHandlerChain extends BaseHandlerChain {
  public ReminderUpdateHandlerChain(
      ReminderUpdateHandler reminderUpdateHandler,
      InterviewReminderUpdateHandler interviewReminderUpdateHandler,
      CandidateReminderUpdateHandler candidateReminderUpdateHandler) {
    this.addHandler(reminderUpdateHandler);
    this.addHandler(interviewReminderUpdateHandler);
    this.addHandler(candidateReminderUpdateHandler);
  }
}
