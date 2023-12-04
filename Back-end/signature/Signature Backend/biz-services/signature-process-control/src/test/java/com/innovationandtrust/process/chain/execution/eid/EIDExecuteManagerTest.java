package com.innovationandtrust.process.chain.execution.eid;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.ParticipantOrderInvitationHandler;
import com.innovationandtrust.process.chain.handler.ParticipantUnorderedInvitationHandler;
import com.innovationandtrust.process.chain.handler.RecipientInvitationHandler;
import com.innovationandtrust.process.chain.handler.eid.EIDSignProcessHandler;
import com.innovationandtrust.process.chain.handler.eid.EIDSigningProcessDecisionHandler;
import com.innovationandtrust.process.chain.handler.eid.EIDValidateOTPSignHandler;
import com.innovationandtrust.process.chain.handler.eid.RefusedProjectValidateHandler;
import com.innovationandtrust.process.chain.handler.eid.RequestToSignHandler;
import com.innovationandtrust.process.chain.handler.eid.VideoAuthorizationHandler;
import com.innovationandtrust.process.chain.handler.eid.VideoVerificationHandler;
import com.innovationandtrust.process.chain.handler.eid.VideoVerifiedHandler;
import com.innovationandtrust.process.chain.handler.eid.VideoVerifiedMailHandler;
import com.innovationandtrust.process.chain.handler.refuse.RefusingProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EIDExecuteManagerTest {

  @Test
  @DisplayName("Co-Sign Document Process Execution")
  void testEIDCoSignDocumentExecuteManager() {
    final var eIDCoSignDocumentExecuteManager =
        spy(
            new EIDCoSignDocumentExecuteManager(
                mock(JsonFileProcessHandler.class),
                mock(EIDSignProcessHandler.class),
                mock(ParticipantUnorderedInvitationHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(EIDValidateOTPSignHandler.class)));
    eIDCoSignDocumentExecuteManager.afterPropertiesSet();

    // then
    verify(eIDCoSignDocumentExecuteManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Counter-Sign Document Process Execution")
  void testEIDCounterSignDocumentExecuteManager() {
    final var eIDCounterSignDocumentExecuteManager =
        spy(
            new EIDCounterSignDocumentExecuteManager(
                mock(JsonFileProcessHandler.class),
                mock(EIDSignProcessHandler.class),
                mock(ParticipantOrderInvitationHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(EIDValidateOTPSignHandler.class)));
    eIDCounterSignDocumentExecuteManager.afterPropertiesSet();

    // then
    verify(eIDCounterSignDocumentExecuteManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Request to Sign Process Execution")
  void testEIDRequestToSignExecutionManager() {
    final var eIDRequestToSignExecutionManager =
        spy(
            new EIDRequestToSignExecutionManager(
                mock(JsonFileProcessHandler.class), mock(RequestToSignHandler.class)));
    eIDRequestToSignExecutionManager.afterPropertiesSet();

    // then
    verify(eIDRequestToSignExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Signing Process Execution")
  void testEIDSigningProcessExecutionManager() {
    final var eIDSigningProcessExecutionManager =
        spy(
            new EIDSigningProcessExecutionManager(
                mock(JsonFileProcessHandler.class),
                mock(RecipientInvitationHandler.class),
                mock(CompleteSigningProcessHandler.class),
                mock(EIDSigningProcessDecisionHandler.class),
                mock(DocumentProcessingHandler.class)));
    eIDSigningProcessExecutionManager.afterPropertiesSet();

    // then
    verify(eIDSigningProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Video Authorization Process Execution")
  void testEIDVideoAuthorizationProcessExecutionManager() {
    final var eIDVideoAuthorizationProcessExecutionManager =
        spy(
            new EIDVideoAuthorizationProcessExecutionManager(
                mock(JsonFileProcessHandler.class), mock(VideoAuthorizationHandler.class)));
    eIDVideoAuthorizationProcessExecutionManager.afterPropertiesSet();

    // then
    verify(eIDVideoAuthorizationProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Video Verification Process Execution")
  void testEIDVideoVerificationProcessExecutionManager() {
    final var eIDVideoVerificationProcessExecutionManager =
        spy(
            new EIDVideoVerificationProcessExecutionManager(
                mock(JsonFileProcessHandler.class), mock(VideoVerificationHandler.class)));
    eIDVideoVerificationProcessExecutionManager.afterPropertiesSet();

    // then
    verify(eIDVideoVerificationProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Video Verified Process Execution")
  void testEIDVideoVerifiedProcessExecutionManager() {
    final var eIDVideoVerifiedProcessExecutionManager =
        spy(
            new EIDVideoVerifiedProcessExecutionManager(
                mock(VideoVerifiedHandler.class),
                mock(JsonFileProcessHandler.class),
                mock(VideoVerifiedMailHandler.class),
                mock(RefusedProjectValidateHandler.class),
                mock(RefusingProcessHandler.class),
                mock(CompleteSigningProcessHandler.class)));
    eIDVideoVerifiedProcessExecutionManager.afterPropertiesSet();

    // then
    verify(eIDVideoVerifiedProcessExecutionManager, times(1)).afterPropertiesSet();
  }
}
