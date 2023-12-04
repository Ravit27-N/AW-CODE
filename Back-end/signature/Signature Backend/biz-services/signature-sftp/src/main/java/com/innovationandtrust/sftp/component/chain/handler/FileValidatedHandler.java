package com.innovationandtrust.sftp.component.chain.handler;

import com.innovationandtrust.sftp.constant.SftpProcessConstant;
import com.innovationandtrust.sftp.exception.InvalidProjectDocumentException;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.sftp.service.ProjectService;
import com.innovationandtrust.sftp.utils.FileValidator;
import com.innovationandtrust.share.model.sftp.ProjectAuthorModel;
import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.file.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileValidatedHandler extends AbstractExecutionHandler {

  private final FileProvider fileProvider;

  private final ProjectService projectService;

  private final FileErrorHandler fileErrorHandler;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var request = context.get(SftpProcessConstant.SFTP_FILE_REQUEST, SftpFileRequest.class);
    var source = Path.of(request.getFilePath());
    var destinationPath = FileUtils.toPath(source);

    this.fileProvider.unZipCommand(source, destinationPath);
    // If annexe's file out of ANNEXE FOLDER is error.
    var xmlFile = new AtomicReference<String>(null);
    var totalXml = new AtomicInteger();
    var pdfFiles = new ArrayList<String>();
    this.fileProvider
        .getFiles(destinationPath)
        .forEach(
            file -> {
              if (!file.toLowerCase().contains(FileValidator.ANNEXE_FOLDER)
                  && Files.isRegularFile(Path.of(file))) {
                if (Objects.equals(FilenameUtils.getExtension(file), FileValidator.PDF_EXTENSION)) {
                  pdfFiles.add(file);
                } else if (Objects.equals(
                    FilenameUtils.getExtension(file), FileValidator.XML_EXTENSION)) {
                  xmlFile.set(file);
                  totalXml.getAndIncrement();
                }
              }
            });
    if (totalXml.get() != 1) {
      this.fileErrorHandler.errorDocsNotMatched(request);
      return ExecutionState.END;
    }
    log.info("Pdf files name: {}", pdfFiles);
    ProjectModel project;
    try {
      project = projectService.validateProject(xmlFile.get(), pdfFiles);
    } catch (Exception e) {
      log.error("Project validation failed", e);
      if (e instanceof InvalidProjectDocumentException) {
        this.fileErrorHandler.errorDocsNotMatched(request);
      } else {
        this.fileErrorHandler.errorProjectValidation(request, e.getMessage());
      }
      return ExecutionState.END;
    }

    var corporateUser = request.getCorporateUser();
    project.setAuthor(
        new ProjectAuthorModel(
            corporateUser.getUserEntityId(), corporateUser.getNormalUser().getUserEntityId()));

    context.put(SftpProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }
}
