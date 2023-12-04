package com.innovationandtrust.process.chain.handler.eid;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.constant.PathConstant;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.share.enums.FileAction;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class IdentityDocumentHandler extends AbstractExecutionHandler {

  private final FileProvider fileProvider;

  private final JsonFileProcessHandler jsonFileProcessHandler;

  public IdentityDocumentHandler(
      FileProvider fileProvider, JsonFileProcessHandler jsonFileProcessHandler) {
    this.fileProvider = fileProvider;
    this.jsonFileProcessHandler = jsonFileProcessHandler;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    this.jsonFileProcessHandler.execute(context);
    final var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    final var fileAction = context.get(SignProcessConstant.FILE_ACTION, FileAction.class);
    final var flowId = project.getFlowId();

    final var documents = new ArrayList<>();

    if (Objects.nonNull(fileAction)
        && Objects.equals(fileAction, FileAction.DOWNLOAD)
        && project.getSignatureLevel().equals(SignatureSettingLevel.QUALIFY.name())) {

      project.getParticipants().stream()
          .filter(Participant::isSigner)
          .forEach(
              (Participant participant) ->
                  getDocumentFromParticipant(flowId, documents, participant));

      context.put(SignProcessConstant.IDENTITY_DOCUMENTS, documents);
    }

    return ExecutionState.END;
  }

  private void getDocumentFromParticipant(
      String flowId, ArrayList<Object> documents, Participant participant) {
    final var document = new DocumentResponse();
    document.setParticipantUuid(participant.getUuid());

    final var eidPath = Path.of(flowId, PathConstant.VIDEO_RECORD_PATH);

    final var frontPath = participant.getDocumentFront();
    final var frontPathResolve = eidPath.resolve(frontPath);
    final var backPath = participant.getDocumentBack();
    final var backPathResolve = eidPath.resolve(backPath);

    if (StringUtils.hasText(frontPath) && fileExist(frontPathResolve)) {
      log.info("Getting document front.");
      document.getDocuments().add(builDocument(frontPathResolve, DocumentResponse.Type.FRONT));

      if (StringUtils.hasText(backPath) && fileExist(backPathResolve)) {
        log.info("Getting document back.");
        document.getDocuments().add(builDocument(backPathResolve, DocumentResponse.Type.BACK));
      }
    }

    documents.add(document);
  }

  private DocumentResponse.Document builDocument(Path path, DocumentResponse.Type type) {
    return DocumentResponse.Document.builder()
        .type(type)
        .base64(fileProvider.encodeFileToBase64(path.toString()))
        .fileName(String.valueOf(path.getFileName()))
        .fileType(getExtension(path.toString()))
        .build();
  }

  private boolean fileExist(Path path) {
    final var filePath = Path.of(this.fileProvider.basePath()).resolve(path);
    if (!Files.exists(filePath)) {
      log.warn("File doesn't exist. Path: {}", filePath);
      return false;
    }
    return true;
  }

  private static String getExtension(String path) {
    return FileNameUtils.getExtension(Path.of(path));
  }
}
