package com.innovationandtrust.process.service;

import com.innovationandtrust.process.chain.execution.reminder.SendReminderProcessExecutionManager;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendReminderService {
  private final SendReminderProcessExecutionManager sendReminderProcessExecutionManager;

  public SendReminderService(
      SendReminderProcessExecutionManager sendReminderProcessExecutionManager) {
    this.sendReminderProcessExecutionManager = sendReminderProcessExecutionManager;
  }

  public void sendReminder(String flowId) {
    var context = new ExecutionContext();
    context.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
    this.sendReminderProcessExecutionManager.execute(context);
  }
}
