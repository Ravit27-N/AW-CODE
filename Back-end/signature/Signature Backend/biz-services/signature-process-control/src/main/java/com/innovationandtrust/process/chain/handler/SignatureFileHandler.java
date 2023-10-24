package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.PdfUtils;
import com.innovationandtrust.share.enums.FileAction;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.FileNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InternalErrorException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignatureFileHandler extends AbstractExecutionHandler {

  private final FileProvider fileProvider;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    var fileAction = context.get(SignProcessConstant.FILE_ACTION, FileAction.class);

    project
        .getParticipantByUuid(uuid)
        .ifPresentOrElse(
            participant -> {
              this.validateParticipant(
                  participant,
                  Objects.equals(
                      project.getSignatureLevel(), SignatureSettingLevel.ADVANCE.name()));

              switch (fileAction) {
                case UPLOAD -> {
                  participant.setSignatureMode(
                      context.get(SignProcessConstant.SIGNATURE_MODE, SignatureMode.class).name());
                  this.uploadFile(
                      project,
                      context.get(SignProcessConstant.SIGNATURE_IMAGE, MultipartFile.class),
                      participant);
                }
                case REMOVE -> this.removeFile(project, participant);
                default -> this.downloadFile(context, project, participant);
              }
            },
            () -> {
              throw new InvalidRequestException(
                  "Cannot find any participant with this uuid " + uuid);
            });

    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private void uploadFile(Project project, MultipartFile file, Participant participant) {
    var path = Path.of(project.getFlowId(), PdfUtils.SIGNATURE_FILE_PATH);
    var extension = FileNameUtils.getExtension(file.getOriginalFilename());

    if (Objects.nonNull(participant.getSignatureImage())
        && StringUtils.hasText(participant.getSignatureImage())) {
      var oldFile = path.resolve(participant.getSignatureImage());
      // Delete old file
      org.apache.commons.io.FileUtils.deleteQuietly(oldFile.toFile());
    }

    log.info("Uploading signature file to {}.", path);
    var filename = participant.getUuid() + "." + extension;
    this.fileProvider.upload(file.getResource(), path.toString(), filename);

    participant.setSignatureImage(filename);
  }

  private void removeFile(Project project, Participant participant) {
    checkExistFile(participant.getSignatureImage());
    var path =
        Path.of(project.getFlowId(), PdfUtils.SIGNATURE_FILE_PATH, participant.getSignatureImage());

    log.info("Removing signature file {}.", path);
    org.apache.commons.io.FileUtils.deleteQuietly(path.toFile());
    // Set default back
    participant.setSignatureMode(SignatureMode.WRITE.name());
    participant.setSignatureImage(null);
  }

  private void downloadFile(ExecutionContext context, Project project, Participant participant) {
    checkExistFile(participant.getSignatureImage());
    var filePath =
        Path.of(
            fileProvider.basePath(),
            project.getFlowId(),
            PdfUtils.SIGNATURE_FILE_PATH,
            participant.getSignatureImage());
    try {
      log.info("Downloading signature file {}.", filePath);
      context.put(
          SignProcessConstant.SIGNATURE_IMAGE,
          Base64.getEncoder().encodeToString(Files.readAllBytes(filePath)));
    } catch (IOException e) {
      log.error("Cannot view signature file {}", filePath);
      throw new InternalErrorException("Cannot view file. Please upload again.");
    }
  }

  private void checkExistFile(String fileName) {
    if (!StringUtils.hasText(fileName)) {
      throw new FileNotFoundException("This signatory has no signature file.");
    }
  }

  private void validateParticipant(Participant participant, boolean isAdvanceProject) {
    log.info("Validating participant uuid: {}", participant.getUuid());
    if (!participant.isSigner()) {
      throw new InvalidRequestException("This participant is not a signer.");
    } else if (!participant.getValidPhone().isValid()) {
      throw new InvalidRequestException("This participant not validate phone yet.");
    } else if (!participant.getOtp().isValidated()) {
      throw new InvalidRequestException("This participant not validate otp yet.");
    } else if (isAdvanceProject && !participant.isDocumentVerified()) {
      throw new InvalidRequestException(
          "This project is advance project. Participant must verify document first.");
    } else if (participant.isSigned()) {
      throw new InvalidRequestException("This participant has been signed documents.");
    } else if (participant.isRefused()) {
      throw new InvalidRequestException("This participant has been refused documents.");
    }
  }
}
