package com.innovationandtrust.process.chain.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentCountry;
import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentRotationType;
import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentType;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class VerificationProcessHandlerTest {
  private VerificationProcessHandler verificationProcessHandler;
  @Mock private SignatureIdentityVerificationFeignClient verificationFeignClient;
  private Project project;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    verificationProcessHandler = spy(new VerificationProcessHandler(verificationFeignClient));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);

    var documents =
        new DocumentVerificationRequest(
            UnitTestProvider.getMultipartFile(),
            UnitTestProvider.getMultipartFile(),
            DocumentCountry.FRENCH,
            DocumentType.IDENTITY_CARD,
            DocumentRotationType.NORTH);
    context.put(SignProcessConstant.DOCUMENTS_TO_VERIFY, documents);
  }

  @Test
  @DisplayName("[Verify documents]")
  void verifyDocuments() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setDocumentVerified(false);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    var result = VerificationDocumentResponse.builder().authenticity(true).build();

    // when
    when(this.verificationFeignClient.getDossierById(anyString())).thenReturn(new DossierDto());
    when(this.verificationFeignClient.verifyDocument(anyString(), any())).thenReturn(result);

    this.verificationProcessHandler.execute(context);
    verify(this.verificationProcessHandler).execute(context);
  }

  @Test
  @DisplayName("[Verify documents] Project not advanced")
  void verifyDocumentsProjectNotAdvanced() {
    // given
    project.setSignatureLevel(SignatureSettingLevel.SIMPLE.name());
    context.put(SignProcessConstant.PROJECT_KEY, project);
    var exception =
        assertThrows(
            InvalidRequestException.class, () -> this.verificationProcessHandler.execute(context));

    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @DisplayName("[Verify documents] Participant phone not valid")
  void verifyDocumentsPhoneNotValid() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.getValidPhone().setValid(false);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    var exception =
        assertThrows(
            InvalidRequestException.class, () -> this.verificationProcessHandler.execute(context));

    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @DisplayName("[Verify documents] Participant verified doc")
  void participantVerified() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setDocumentVerified(true);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    var exception =
        assertThrows(
            InvalidRequestException.class, () -> this.verificationProcessHandler.execute(context));

    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @DisplayName("[Verify documents] Participant not found")
  void participantNotFound() {
    // given
    context.put(SignProcessConstant.PARTICIPANT_ID, "NULL");

    var exception =
        assertThrows(
            InvalidRequestException.class, () -> this.verificationProcessHandler.execute(context));

    log.error("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @DisplayName("[Verify documents] Participant not have dossier")
  void participantNotHaveDossier() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setDocumentVerified(false);
              participant.setDossierId("NOT NULL");
            });
    context.put(SignProcessConstant.PARTICIPANT_ID, UnitTestConstant.UUID);
    context.put(SignProcessConstant.PROJECT_KEY, project);

    var result = VerificationDocumentResponse.builder().authenticity(true).build();

    // when
    when(this.verificationFeignClient.getDossierById(anyString())).thenReturn(null);
    when(this.verificationFeignClient.verifyDocument(anyString(), any())).thenReturn(result);

    this.verificationProcessHandler.execute(context);
    verify(this.verificationProcessHandler).execute(context);
  }
}
