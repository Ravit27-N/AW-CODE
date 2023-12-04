package com.innovationandtrust.process.chain.execution;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.execution.sign.CoSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.CounterSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.IndividualSignProcessExecutionManager;
import com.innovationandtrust.process.chain.execution.sign.SigningProcessExecutionManager;
import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.CreateProjectCompleteHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.DossierProcessHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.NotificationReminderScheduleHandler;
import com.innovationandtrust.process.chain.handler.OtpProcessingHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.RecipientInvitationHandler;
import com.innovationandtrust.process.chain.handler.RequestSigningHandler;
import com.innovationandtrust.process.chain.handler.ViewerInvitationHandler;
import com.innovationandtrust.process.chain.handler.expired.ProjectExpiredScheduleHandler;
import com.innovationandtrust.process.chain.handler.sign.PrepareSignDocumentHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningInfoHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessDecisionHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessHandler;
import com.innovationandtrust.process.chain.handler.sign.UpdateToProcessingHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SignProcessingExecutionManagerTest {
  @Test
  @DisplayName("Counter Sign Process Execution Manager Test")
  void counterSignProcessExecutionManager() {
    CounterSignProcessExecutionManager counterSignProcessExecutionManager =
        spy(
            new CounterSignProcessExecutionManager(
                mock(PrepareSignDocumentHandler.class),
                mock(SigningProcessHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(ParticipantOrderInvitationHandler.class),
                mock(JsonFileProcessHandler.class),
                mock(UpdateToProcessingHandler.class)));

    counterSignProcessExecutionManager.afterPropertiesSet();
    verify(counterSignProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Individual Sign Process Execution Manager Test")
  void individualSignProcessExecutionManager() {
    IndividualSignProcessExecutionManager individualSignProcessExecutionManager =
        spy(
            new IndividualSignProcessExecutionManager(
                mock(PrepareSignDocumentHandler.class),
                mock(SigningProcessHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(ParticipantUnorderedInvitationHandler.class),
                mock(JsonFileProcessHandler.class),
                mock(UpdateToProcessingHandler.class)));

    individualSignProcessExecutionManager.afterPropertiesSet();
    verify(individualSignProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Signing Process Execution Manager Test")
  void signingProcessExecutionManager() {
    SigningProcessExecutionManager signingProcessExecutionManager =
        spy(
            new SigningProcessExecutionManager(
                mock(JsonFileProcessHandler.class),
                mock(SigningProcessDecisionHandler.class),
                mock(RecipientInvitationHandler.class),
                mock(CompleteSigningProcessHandler.class),
                mock(DocumentProcessingHandler.class)));

    signingProcessExecutionManager.afterPropertiesSet();
    verify(signingProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Complete Sign Process Execution Manager Test")
  void completeSignProcessExecutionManager() {
    CompleteSignProcessExecutionManager completeSignProcessExecutionManager =
        spy(
            new CompleteSignProcessExecutionManager(
                mock(JsonFileProcessHandler.class),
                mock(DocumentProcessingHandler.class),
                mock(ProjectWebHookHandler.class)));

    completeSignProcessExecutionManager.afterPropertiesSet();
    verify(completeSignProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("ProjectCoSignExecutionManager Test")
  void projectCoSignExecutionManager() {
    ProjectCoSignExecutionManager projectCoSignExecutionManager =
        spy(
            new ProjectCoSignExecutionManager(
                mock(DocumentProcessingHandler.class),
                mock(RequestSigningHandler.class),
                mock(JsonFileProcessHandler.class),
                mock(ParticipantUnorderedInvitationHandler.class),
                mock(CreateProjectCompleteHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(NotificationReminderScheduleHandler.class),
                mock(ViewerInvitationHandler.class),
                mock(ProjectExpiredScheduleHandler.class),
                mock(DossierProcessHandler.class)));

    projectCoSignExecutionManager.afterPropertiesSet();
    verify(projectCoSignExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Project Counter Sign Execution Manager Test")
  void projectCounterSignExecutionManager() {
    ProjectCounterSignExecutionManager projectCounterSignExecutionManager =
        spy(
            new ProjectCounterSignExecutionManager(
                mock(DocumentProcessingHandler.class),
                mock(RequestSigningHandler.class),
                mock(JsonFileProcessHandler.class),
                mock(ParticipantOrderInvitationHandler.class),
                mock(CreateProjectCompleteHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(NotificationReminderScheduleHandler.class),
                mock(ViewerInvitationHandler.class),
                mock(ProjectExpiredScheduleHandler.class),
                mock(DossierProcessHandler.class)));

    projectCounterSignExecutionManager.afterPropertiesSet();
    verify(projectCounterSignExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Project Individual Sign Execution Manager Test")
  void projectIndividualSignExecutionManager() {
    ProjectIndividualSignExecutionManager projectIndividualSignExecutionManager =
        spy(
            new ProjectIndividualSignExecutionManager(
                mock(DocumentProcessingHandler.class),
                mock(ParticipantUnorderedInvitationHandler.class),
                mock(NotificationReminderScheduleHandler.class),
                mock(JsonFileProcessHandler.class),
                mock(ViewerInvitationHandler.class),
                mock(ProjectExpiredScheduleHandler.class),
                mock(CreateProjectCompleteHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(DossierProcessHandler.class)));

    projectIndividualSignExecutionManager.afterPropertiesSet();
    verify(projectIndividualSignExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Setup Individual Sign Process Execution Manager Test")
  void setupIndividualSignProcessExecutionManager() {
    SetupIndividualSignProcessExecutionManager setupIndividualSignProcessExecutionManager =
        spy(
            new SetupIndividualSignProcessExecutionManager(
                mock(JsonFileProcessHandler.class),
                mock(RequestSigningHandler.class),
                mock(OtpProcessingHandler.class)));

    setupIndividualSignProcessExecutionManager.afterPropertiesSet();
    verify(setupIndividualSignProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Signing Info Execution Manager Test")
  void signingInfoExecutionManager() {
    SigningInfoExecutionManager signingInfoExecutionManager =
        spy(
            new SigningInfoExecutionManager(
                mock(JsonFileProcessHandler.class), mock(SigningInfoHandler.class)));

    signingInfoExecutionManager.afterPropertiesSet();
    verify(signingInfoExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Co-Sign Process Execution Manager Test")
  void coSignProcessExecutionManager() {
    CoSignProcessExecutionManager coSignProcessExecutionManager =
        spy(
            new CoSignProcessExecutionManager(
                mock(PrepareSignDocumentHandler.class),
                mock(SigningProcessHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(ParticipantUnorderedInvitationHandler.class),
                mock(JsonFileProcessHandler.class),
                mock(UpdateToProcessingHandler.class)));

    coSignProcessExecutionManager.afterPropertiesSet();
    verify(coSignProcessExecutionManager, times(1)).afterPropertiesSet();
  }
}
