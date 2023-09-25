package com.allweb.rms.core.scheduler.batch.task;

import com.allweb.rms.core.scheduler.ReminderConstants;
import com.allweb.rms.core.scheduler.batch.exception.ReminderInvalidException;
import com.allweb.rms.core.scheduler.batch.exception.TaskExecutionException;
import com.allweb.rms.entity.jpa.Reminder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ReminderValidationTasklet extends AbstractTasklet {
  private Reminder reminder;

  @Override
  protected RepeatStatus executeInternal(StepContribution contribution, ChunkContext chunkContext)
      throws TaskExecutionException {
    if (!reminder.isActive() || reminder.isDeleted() || reminder.isSend()) {
      throw new ReminderInvalidException(reminder);
    }
    return RepeatStatus.FINISHED;
  }

  @Override
  protected void validate() {
    if (reminder == null) {
      throw new NullPointerException(ReminderConstants.REMINDER_NOT_FOUND);
    }
  }

  @Override
  protected void beforeExecute(StepContribution contribution, ChunkContext chunkContext) {
    reminder =
        (Reminder) this.getJobExecutionContext(chunkContext).get(ReminderConstants.REMINDER_KEY);
  }
}
