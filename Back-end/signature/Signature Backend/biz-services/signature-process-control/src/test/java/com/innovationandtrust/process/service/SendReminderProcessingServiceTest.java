package com.innovationandtrust.process.service;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.reminder.SendReminderProcessExecutionManager;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SendReminderProcessingServiceTest {

  private SendReminderService sendReminderService;
  @Mock private SendReminderProcessExecutionManager sendReminderProcessExecutionManager;

  @BeforeEach
  public void setup() {
    sendReminderService = spy(new SendReminderService(sendReminderProcessExecutionManager));
  }

  @Test
  @DisplayName("Project send reminder test")
  void testSendReminderTest() {
    // when
    String flowId = "022e2923-924b-4745-a2a8-250077141b83";
    var context = new ExecutionContext();
    context.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    this.sendReminderProcessExecutionManager.execute(context);
    this.sendReminderService.sendReminder(flowId);

    verify(sendReminderService, times(1)).sendReminder(flowId);
  }
}
