package com.innovationandtrust.sftp.component.chain.handler;

import com.innovationandtrust.sftp.constant.SftpProcessConstant;
import com.innovationandtrust.sftp.model.EmailModel;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.sftp.service.FileService;
import com.innovationandtrust.sftp.service.ProjectService;
import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectCreationHandler extends AbstractExecutionHandler {

  private final ProjectService projectService;
  private final FileService fileService;
  private final FileErrorHandler fileErrorHandler;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SftpProcessConstant.PROJECT_KEY, ProjectModel.class);
    var sftpRequest = context.get(SftpProcessConstant.SFTP_FILE_REQUEST, SftpFileRequest.class);
    project.setFlowId(sftpRequest.getFlowId());
    this.createProject(project, sftpRequest);
    return ExecutionState.END;
  }

  private void createProject(ProjectModel project, SftpFileRequest request) {
    project.setSftpZipFile(request.getFilename());
    project.setCorporateFolder(request.getCorporateUuid());
    var createdProject = this.projectService.createProject(project);
    if (!createdProject) {
      // Do action for failed creation
      log.error("Failed creating project");
      this.createProjectFailed(request);
      this.fileService.deleteDirectory(request);
    } else {
      this.fileService.copyFileToProcessed(request);
      this.fileService.moveFileToDoneIN(request);
      this.fileService.deleteDirectory(request);
    }
    log.info("Create project success: {}", createdProject);
  }

  private void createProjectFailed(SftpFileRequest request) {
    request.setMessage("Échec de la création du projet");
    var errorEmail =
        EmailModel.builder()
            .subject("Échec de la création du projet")
            .message("Échec de la création du projet: ")
            .build();

    this.fileErrorHandler.processErrorFileAndSendMail(request, errorEmail);
  }
}
