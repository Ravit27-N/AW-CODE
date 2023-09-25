package com.allweb.rms.core.scheduler.batch.task;

import com.allweb.rms.core.firebase.FCMUtils;
import com.allweb.rms.core.firebase.WebPushMulticastMessageConfigurer;
import com.allweb.rms.core.scheduler.ReminderConstants;
import com.allweb.rms.core.scheduler.batch.exception.TaskExecutionException;
import com.allweb.rms.entity.jpa.FirebaseToken;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.service.FCMService;
import com.allweb.rms.service.FirebaseTokenService;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FirebaseNotificationSendingTasklet extends AbstractTasklet {

  private final FCMService fcmService;
  private final FirebaseTokenService firebaseTokenService;
  private final Map<String, Object> dataMap = new HashMap<>();
  private Reminder reminder;
  private List<FirebaseToken> userFirebaseTokens;
  private int sentSuccessCount;

  public FirebaseNotificationSendingTasklet(
      FCMService fcmService, FirebaseTokenService firebaseTokenService) {
    this.fcmService = fcmService;
    this.firebaseTokenService = firebaseTokenService;
  }

  @Override
  public void beforeExecute(StepContribution contribution, ChunkContext chunkContext) {
    ExecutionContext jobExecutionContext = this.getJobExecutionContext(chunkContext);
    this.reminder = (Reminder) jobExecutionContext.get(ReminderConstants.REMINDER_KEY);
    if (this.reminder != null) {
      userFirebaseTokens = firebaseTokenService.getAll(this.reminder.getUserId());
      log.info("Firebase token found {}.", userFirebaseTokens.size());
    }
  }

  @Override
  protected void validate() {
    if (reminder == null) {
      throw new NullPointerException("Reminder is null.");
    }
  }

  @Override
  public RepeatStatus executeInternal(StepContribution contribution, ChunkContext chunkContext)
      throws TaskExecutionException {
    if (this.userFirebaseTokens != null && !this.userFirebaseTokens.isEmpty()) {
      TaskExecutionException taskExecutionException = null;
      try {
        BatchResponse batchResponse = this.sendMulticastMessage();
        log.info(
            "Send Firebase message to user id {} with total {}, success {}.",
            this.reminder.getUserId(),
            this.userFirebaseTokens.size(),
            batchResponse.getSuccessCount());
        this.sentSuccessCount = batchResponse.getSuccessCount();
      } catch (FirebaseMessagingException e) {
        taskExecutionException = new TaskExecutionException(e);
      }
      if (taskExecutionException != null) {
        throw taskExecutionException;
      }
    }
    return RepeatStatus.FINISHED;
  }

  @Override
  protected void afterExecute(StepContribution contribution, ChunkContext chunkContext) {
    this.dataMap.put(ReminderConstants.FIREBASE_SENT_SUCCESS, this.sentSuccessCount);
    ExecutionContext stepExecutionContext = this.getStepExecutionContext(chunkContext);
    stepExecutionContext.put(ReminderConstants.FIREBASE_TASK_DATA_MAP, this.dataMap);
  }

  private BatchResponse sendMulticastMessage() throws FirebaseMessagingException {
    WebPushMulticastMessageConfigurer configurer = new WebPushMulticastMessageConfigurer();
    configurer.setTitle(reminder.getTitle());
    configurer.setBody(reminder.getDescription());
    this.userFirebaseTokens.forEach(token -> configurer.addDeviceToken(token.getFcmToken()));
    MulticastMessage message = FCMUtils.createMulticastMessage(configurer);
    log.info("Pushed notification");
    return fcmService.sendMulticastMessage(message);
  }
}
