package com.allweb.rms.core.scheduler.batch.task;

import static com.allweb.rms.core.scheduler.ReminderConstants.EMAIL_SENT_SUCCESS;
import static com.allweb.rms.core.scheduler.ReminderConstants.EMAIL_TASK_DATA_MAP;
import static com.allweb.rms.core.scheduler.ReminderConstants.FIREBASE_SENT_SUCCESS;
import static com.allweb.rms.core.scheduler.ReminderConstants.FIREBASE_TASK_DATA_MAP;
import static com.allweb.rms.core.scheduler.ReminderConstants.REMINDER_KEY;
import static com.allweb.rms.core.scheduler.ReminderConstants.REMINDER_NOT_FOUND;

import com.allweb.rms.core.scheduler.ReminderConstants;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.repository.elastic.ReminderElasticsearchRepository;
import com.allweb.rms.repository.jpa.ReminderRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReminderReportTasklet extends AbstractTasklet {

  private final ReminderRepository reminderRepository;
  private final ReminderElasticsearchRepository reminderElasticsearchRepository;
  private final Map<String, Object> firebaseTaskDataMap = new HashMap<>();
  private final Map<String, Object> emailTaskDataMap = new HashMap<>();
  private Reminder reminder;
  private boolean allowReport = true;

  public ReminderReportTasklet(
      ReminderRepository reminderRepository,
      ReminderElasticsearchRepository reminderElasticsearchRepository) {
    this.reminderRepository = reminderRepository;
    this.reminderElasticsearchRepository = reminderElasticsearchRepository;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void beforeExecute(StepContribution contribution, ChunkContext chunkContext) {
    ExecutionContext jobExecutionContext = this.getJobExecutionContext(chunkContext);
    Object emailTaskDataMapObject = jobExecutionContext.get(EMAIL_TASK_DATA_MAP);
    if (emailTaskDataMapObject != null) {
      this.emailTaskDataMap.putAll((Map<String, Object>) emailTaskDataMapObject);
    }
    Object firebaseTaskDataMapObject = jobExecutionContext.get(FIREBASE_TASK_DATA_MAP);
    if (firebaseTaskDataMapObject != null) {
      this.firebaseTaskDataMap.putAll((Map<String, Object>) firebaseTaskDataMapObject);
    }
    reminder = (Reminder) jobExecutionContext.get(REMINDER_KEY);
    Optional<String> allowReportObject =
        this.getJobParameter(ReminderConstants.ALLOW_REPORT_SENDING_STATE_KEY, chunkContext);
    allowReportObject.ifPresent(s -> this.allowReport = Boolean.parseBoolean(s));
  }

  @Override
  protected void validate() {
    if (reminder == null) {
      throw new NullPointerException(REMINDER_NOT_FOUND);
    }
  }

  @Override
  protected RepeatStatus executeInternal(StepContribution contribution, ChunkContext chunkContext) {
    if (allowReport) {
      boolean isEmailSend =
          this.emailTaskDataMap.containsKey(EMAIL_SENT_SUCCESS)
              && (boolean) this.emailTaskDataMap.get(EMAIL_SENT_SUCCESS);
      boolean isFirebaseSent =
          this.firebaseTaskDataMap.containsKey(FIREBASE_SENT_SUCCESS)
              && ((int) this.firebaseTaskDataMap.get(FIREBASE_SENT_SUCCESS)) > 0;
      boolean notificationSendingState = isEmailSend || isFirebaseSent;
      this.updateReminderSentState(notificationSendingState);
    }
    return RepeatStatus.FINISHED;
  }

  @Transactional
  public void updateReminderSentState(boolean isSend) {
    reminder.setSend(isSend);
    if (isSend) {
      this.reminderElasticsearchRepository.deleteElasticsearchDocumentById(reminder.getId());
    }
    this.reminderRepository.save(reminder);
  }
}
