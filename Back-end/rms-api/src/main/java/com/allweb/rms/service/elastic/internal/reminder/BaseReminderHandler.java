package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.entity.elastic.Candidate;
import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Reminder;

public class BaseReminderHandler {
  protected ReminderElasticsearchDocument.Interview getReminderInterview(Reminder reminder) {
    Interview interview = reminder.getInterview();
    if (interview != null && !reminder.isDeleted()) {
      ReminderElasticsearchDocument.Interview reminderInterview =
          new ReminderElasticsearchDocument.Interview();
      reminderInterview.setId(interview.getId());
      reminderInterview.setTitle(interview.getTitle());
      return reminderInterview;
    }
    return null;
  }

  protected Candidate getReminderCandidate(Reminder reminder) {
    com.allweb.rms.entity.jpa.Candidate candidate = reminder.getCandidate();
    if (candidate != null && !candidate.isDeleted()) {
      Candidate reminderCandidate = new Candidate();
      reminderCandidate.setId(candidate.getId());
      reminderCandidate.setFullName(candidate.getFullName());
      return reminderCandidate;
    }
    return null;
  }
}
