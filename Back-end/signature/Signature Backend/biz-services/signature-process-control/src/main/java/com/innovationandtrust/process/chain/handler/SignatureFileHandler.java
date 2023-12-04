package com.innovationandtrust.process.chain.handler;

import static com.innovationandtrust.utils.commons.CommonUsages.convertToList;
import static com.innovationandtrust.utils.commons.CommonUsages.fileToMultipartFile;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.PathConstant;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.SigningProcessDto;
import com.innovationandtrust.process.utils.ProcessControlUtils;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class SignatureFileHandler extends AbstractExecutionHandler {

  private final FileProvider fileProvider;

  public SignatureFileHandler(FileProvider fileProvider) {
    this.fileProvider = fileProvider;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var requestsObj = context.get(SignProcessConstant.MULTI_SIGNING_PROJECTS);

    if (Objects.nonNull(requestsObj)) {
      this.multipleProjects(context);
    } else {
      this.project(context);
    }

    return ExecutionState.NEXT;
  }

  private void project(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);

    project
        .getParticipantByUuid(uuid)
        .ifPresentOrElse(
            participant ->
                this.fileProcessing(
                    context,
                    project,
                    participant,
                    context.get(SignProcessConstant.FILE_ACTION, FileAction.class)),
            () -> ProcessControlUtils.invalidParticipant(project.getFlowId(), uuid));

    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
  }

  private void multipleProjects(ExecutionContext context) {

    final var fileAction = context.get(SignProcessConstant.FILE_ACTION, FileAction.class);
    final var tempImage = context.get(SignProcessConstant.TEMP_SIGNATURE_IMAGE, String.class);
    var isExist = false;
    Path filePath = null;

    if (Objects.equals(fileAction, FileAction.UPLOAD) && StringUtils.hasText(tempImage)) {
      filePath = Path.of(fileProvider.basePath(), PathConstant.SIGNATURE_FILE_PATH_TEMP, tempImage);

      isExist = fileProvider.isExist(filePath, true);
      if (!isExist) {
        throw new InvalidRequestException(String.format("File %s not found.", tempImage));
      }

      context.put(SignProcessConstant.SIGNATURE_IMAGE, fileToMultipartFile(filePath));
    }

    var requests =
        convertToList(
            context.get(SignProcessConstant.MULTI_SIGNING_PROJECTS), SigningProcessDto.class);
    var projects = convertToList(context.get(SignProcessConstant.PROJECTS), Project.class);
    if (!projects.isEmpty()) {
      for (var request : requests) {
        projects.stream()
            .filter(project -> Objects.equals(request.getFlowId(), project.getFlowId()))
            .findFirst()
            .ifPresent(
                project ->
                    project
                        .getParticipantByUuid(request.getUuid())
                        .ifPresentOrElse(
                            participant ->
                                this.fileProcessing(context, project, participant, fileAction),
                            () ->
                                ProcessControlUtils.invalidParticipant(
                                    project.getFlowId(), request.getUuid())));
      }

      context.put(SignProcessConstant.PROJECTS, projects);
      context.put(
          SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE_MULTIPLE);

      if (isExist) {
        FileUtils.deleteQuietly(filePath != null ? filePath.toFile() : null);
      }
    }
  }

  private void fileProcessing(
      ExecutionContext context, Project project, Participant participant, FileAction fileAction) {
    this.validateParticipant(
        participant,
        Objects.equals(project.getSignatureLevel(), SignatureSettingLevel.ADVANCE.name()));

    switch (fileAction) {
      case UPLOAD -> this.uploadFile(
          project,
          context.get(SignProcessConstant.SIGNATURE_IMAGE, MultipartFile.class),
          participant,
          context.get(SignProcessConstant.SIGNATURE_MODE, SignatureMode.class).name());
      case REMOVE -> this.removeFile(project, participant);
      default -> this.downloadFile(context, project, participant);
    }
  }

  private void uploadFile(
      Project project, MultipartFile file, Participant participant, String mode) {
    participant.setSignatureMode(mode);
    var path = Path.of(project.getFlowId(), PathConstant.SIGNATURE_FILE_PATH);
    var extension = FileNameUtils.getExtension(file.getOriginalFilename());

    if (Objects.nonNull(participant.getSignatureImage())
        && StringUtils.hasText(participant.getSignatureImage())) {
      var oldFile = path.resolve(participant.getSignatureImage());
      // Delete old file
      FileUtils.deleteQuietly(oldFile.toFile());
    }

    log.info("Uploading signature file to {}.", path);
    var filename = participant.getUuid() + "." + extension;
    this.fileProvider.upload(file.getResource(), path.toString(), filename);

    participant.setSignatureImage(filename);
  }

  private void removeFile(Project project, Participant participant) {
    var filePath = getFilePath(project.getFlowId(), participant.getSignatureImage());
    checkExistFile(filePath);

    log.info("Removing signature file {}.", filePath);
    FileUtils.deleteQuietly(filePath.toFile());
    // Set default back
    participant.setSignatureMode(SignatureMode.WRITE.name());
    participant.setSignatureImage(null);
  }

  private void downloadFile(ExecutionContext context, Project project, Participant participant) {
    var filePath = getFilePath(project.getFlowId(), participant.getSignatureImage());

    if (Objects.isNull(context.get(SignProcessConstant.SIGNATURE_IMAGE))) {
      checkExistFile(filePath);

      try {
        log.info("Downloading signature file {}.", filePath);
        context.put(
            SignProcessConstant.SIGNATURE_IMAGE,
            Base64.getEncoder().encodeToString(Files.readAllBytes(filePath)));
      } catch (IOException e) {
        log.error("Cannot view signature file {}", filePath, e);
        throw new InternalErrorException("Cannot view file. Please upload again.");
      }
    }
  }

  private Path getFilePath(String flowId, String imageName) {
    return Path.of(fileProvider.basePath(), flowId, PathConstant.SIGNATURE_FILE_PATH, imageName);
  }

  private void checkExistFile(Path path) {
    if (!StringUtils.hasText(path.getFileName().toString()) && fileProvider.isExist(path, true)) {
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
    } else {
      log.info("This participant is valid.");
    }
  }
}
