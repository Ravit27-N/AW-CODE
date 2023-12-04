package com.innovationandtrust.process.chain.execution;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.process.chain.handler.CompleteSigningProcessHandler;
import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.GetUserInfoHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.OtpProcessingHandler;
import com.innovationandtrust.process.chain.handler.RecipientHandler;
import com.innovationandtrust.process.chain.handler.VerificationProcessHandler;
import com.innovationandtrust.process.chain.handler.webhook.ProjectWebHookHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class OtherProcessingExecutionManagerTest {
  @Test
  @DisplayName("Verify Document Execution Manager Test")
  void verifyDocumentExecutionManager() {
    VerifyDocumentExecutionManager verifyDocumentExecutionManager =
        spy(
            new VerifyDocumentExecutionManager(
                mock(JsonFileProcessHandler.class), mock(VerificationProcessHandler.class)));

    verifyDocumentExecutionManager.afterPropertiesSet();
    verify(verifyDocumentExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Recipient Execution Manager Test")
  void recipientExecutionManager() {
    RecipientExecutionManager recipientExecutionManager =
        spy(
            new RecipientExecutionManager(
                mock(JsonFileProcessHandler.class),
                mock(RecipientHandler.class),
                mock(ProjectWebHookHandler.class),
                mock(CompleteSigningProcessHandler.class)));

    recipientExecutionManager.afterPropertiesSet();
    verify(recipientExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Upload Modified Document Execution Handler Test")
  void uploadModifiedDocumentExecutionHandler() {
    UploadModifiedDocumentExecutionHandler uploadModifiedDocumentExecutionHandler =
        spy(
            new UploadModifiedDocumentExecutionHandler(
                mock(JsonFileProcessHandler.class), mock(DocumentProcessingHandler.class)));

    uploadModifiedDocumentExecutionHandler.afterPropertiesSet();
    verify(uploadModifiedDocumentExecutionHandler, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Generate OTP Execution Manager Test")
  void generateOTPExecutionManager() {
    GenerateOTPExecutionManager generateOTPExecutionManager =
        spy(
            new GenerateOTPExecutionManager(
                mock(JsonFileProcessHandler.class),
                mock(GetUserInfoHandler.class),
                mock(OtpProcessingHandler.class)));

    generateOTPExecutionManager.afterPropertiesSet();
    verify(generateOTPExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("Document Process Execution Manager Test")
  void documentProcessExecutionManager() {
    DocumentProcessExecutionManager documentProcessExecutionManager =
        spy(
            new DocumentProcessExecutionManager(
                mock(JsonFileProcessHandler.class), mock(DocumentProcessingHandler.class)));

    documentProcessExecutionManager.afterPropertiesSet();
    verify(documentProcessExecutionManager, times(1)).afterPropertiesSet();
  }
}
