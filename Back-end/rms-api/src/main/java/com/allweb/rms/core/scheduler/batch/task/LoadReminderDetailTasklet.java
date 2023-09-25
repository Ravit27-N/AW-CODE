package com.allweb.rms.core.scheduler.batch.task;

import com.allweb.rms.core.scheduler.ReminderConstants;
import com.allweb.rms.core.scheduler.batch.exception.TaskExecutionException;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.exception.ReminderNotFoundException;
import com.allweb.rms.repository.jpa.ReminderRepository;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class LoadReminderDetailTasklet extends AbstractTasklet {

  private static final String REMINDER_ID_NOT_FOUND = "Reminder Id is not found.";

  private final ReminderRepository reminderRepository;
  private String reminderId;
  private Reminder reminder;

  public LoadReminderDetailTasklet(ReminderRepository reminderRepository) {
    this.reminderRepository = reminderRepository;
  }

  @Override
  protected void beforeExecute(StepContribution contribution, ChunkContext chunkContext) {
    this.getJobParameter(ReminderConstants.REMINDER_ID_KEY, chunkContext)
        .ifPresent(s -> this.reminderId = String.valueOf(s));
  }

  @Override
  protected void validate() {
    if (StringUtils.isBlank(reminderId)) {
      throw new IllegalArgumentException(REMINDER_ID_NOT_FOUND);
    }
  }

  @Override
  protected RepeatStatus executeInternal(StepContribution contribution, ChunkContext chunkContext)
      throws TaskExecutionException {
    Optional<Reminder> reminderEntity =
        this.reminderRepository.findById(Integer.valueOf(this.reminderId));
    if (!reminderEntity.isPresent()) {
      throw new ReminderNotFoundException(Integer.valueOf(this.reminderId));
    }
    reminder = reminderEntity.get();
    return RepeatStatus.FINISHED;
  }

  @Override
  protected void afterExecute(StepContribution contribution, ChunkContext chunkContext) {
    if (reminder != null) {
      ExecutionContext stepExecutionContext = this.getStepExecutionContext(chunkContext);
      stepExecutionContext.put(ReminderConstants.REMINDER_KEY, reminder);
    }
  }
}
