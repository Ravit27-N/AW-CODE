package com.innovationandtrust.process.chain.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.FileAction;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.exception.exceptions.FileNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InternalErrorException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class SignatureFileHandlerTest {
  private SignatureFileHandler signatureFileHandler;
  @Mock private FileProvider fileProvider;
  private Project project;
  private ExecutionContext context;

  @BeforeEach
  public void setup() {

    signatureFileHandler = spy(new SignatureFileHandler(fileProvider));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
    context.put(SignProcessConstant.SIGNATURE_IMAGE, UnitTestProvider.getMultipartFile());
    context.put(SignProcessConstant.SIGNATURE_MODE, SignatureMode.IMPORT);
  }

  @Test
  @DisplayName("[Signature file] UPLOAD")
  void uploadSignatureFile() {
    // given
    context.put(SignProcessConstant.FILE_ACTION, FileAction.UPLOAD);

    this.signatureFileHandler.execute(context);
    verify(this.signatureFileHandler).execute(context);
  }

  @Test
  @DisplayName("[Signature file] REMOVE")
  void removeSignatureFile() {
    // given
    context.put(SignProcessConstant.FILE_ACTION, FileAction.REMOVE);

    this.signatureFileHandler.execute(context);
    verify(this.signatureFileHandler).execute(context);
  }

  @Test
  @DisplayName("[Signature file] DOWNLOAD")
  void downloadSignatureFile() {
    // given
    context.put(SignProcessConstant.FILE_ACTION, FileAction.DOWNLOAD);

    when(this.fileProvider.basePath()).thenReturn(UnitTestProvider.basePath());
    this.signatureFileHandler.execute(context);
    verify(this.signatureFileHandler).execute(context);
  }

  @Test
  @DisplayName("[Signature file] DOWNLOAD fail")
  void downloadSignatureFileFail() {
    // given
    context.put(SignProcessConstant.FILE_ACTION, FileAction.DOWNLOAD);

    when(this.fileProvider.basePath()).thenReturn("D:\\app\\invalid-path");

    var exception =
        assertThrows(
            InternalErrorException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @DisplayName("[Signature file] DOWNLOAD fail (Participant has no file)")
  void participantHasNoFile() {
    // given
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setSignatureImage(null);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.FILE_ACTION, FileAction.DOWNLOAD);

    var exception =
        assertThrows(FileNotFoundException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @DisplayName("[Signature file] Participant not found")
  void invalidParticipant() {
    // given
    context.put(SignProcessConstant.PARTICIPANT_ID, "INVALID");

    var exception =
        assertThrows(
            InvalidRequestException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());
    context.put(SignProcessConstant.PARTICIPANT_ID, UnitTestConstant.UUID);
  }

  @Test
  @DisplayName("[Signature file] DOWNLOAD validations fail")
  void validationFails() {
    project.setSignatureLevel(SignatureSettingLevel.ADVANCE.name());
    // case 1
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setRole(RoleConstant.ROLE_APPROVAL);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);

    log.info("Validating participant role");
    var exception =
        assertThrows(
            InvalidRequestException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());

    // case 2
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setRole(RoleConstant.ROLE_SIGNATORY);
              participant.getValidPhone().setValid(false);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);

    log.info("Validating participant valid phone");
    exception =
        assertThrows(
            InvalidRequestException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());

    // case 3
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setRole(RoleConstant.ROLE_SIGNATORY);
              participant.getValidPhone().setValid(true);
              participant.getOtp().setValidated(false);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);

    log.info("Validating participant validated opt");
    exception =
        assertThrows(
            InvalidRequestException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());

    // case 4
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setRole(RoleConstant.ROLE_SIGNATORY);
              participant.getValidPhone().setValid(true);
              participant.getOtp().setValidated(true);
              participant.setDocumentVerified(false);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);

    log.info("Validating participant document verified");
    exception =
        assertThrows(
            InvalidRequestException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());

    // case 5
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setRole(RoleConstant.ROLE_SIGNATORY);
              participant.getValidPhone().setValid(true);
              participant.getOtp().setValidated(true);
              participant.setDocumentVerified(true);
              participant.setSigned(true);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);

    log.info("Validating participant has already signed");
    exception =
        assertThrows(
            InvalidRequestException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());

    // case 6
    project
        .getParticipantByUuid(UnitTestConstant.UUID)
        .ifPresent(
            participant -> {
              participant.setRole(RoleConstant.ROLE_SIGNATORY);
              participant.getValidPhone().setValid(true);
              participant.getOtp().setValidated(true);
              participant.setDocumentVerified(true);
              participant.setSigned(false);
              participant.setRefused(true);
            });
    context.put(SignProcessConstant.PROJECT_KEY, project);

    log.info("Validating participant has already refused");
    exception =
        assertThrows(
            InvalidRequestException.class, () -> this.signatureFileHandler.execute(context));
    log.error("[Exception thrown]: {}", exception.getMessage());

    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
  }
}
