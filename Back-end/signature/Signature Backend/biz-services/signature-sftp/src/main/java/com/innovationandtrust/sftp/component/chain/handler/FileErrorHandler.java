package com.innovationandtrust.sftp.component.chain.handler;

import com.innovationandtrust.sftp.constant.SftpProcessConstant;
import com.innovationandtrust.sftp.model.EmailModel;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.sftp.service.FileDetectionService;
import com.innovationandtrust.sftp.service.FileService;
import com.innovationandtrust.sftp.service.MailService;
import com.innovationandtrust.sftp.utils.FileValidator;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.feignclient.FacadeUrl;
import com.innovationandtrust.utils.file.provider.FileProvider;
import java.nio.file.Path;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileErrorHandler extends AbstractExecutionHandler {
  @jakarta.annotation.Resource(name = "facadeUriConfig")
  protected FacadeUrl facadeUrl;

  private final FileProvider fileProvider;
  private final MailService mailService;
  private final FileService fileService;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var request = context.get(SftpProcessConstant.SFTP_FILE_REQUEST, SftpFileRequest.class);
    switch (FileValidator.validateZipFile(
        Path.of(this.fileProvider.getBasePath(), request.getFilePath()))) {
      case ERROR_INVALID_FILE_NAME -> {
        this.errorInvalidFilename(request);
        return ExecutionState.END;
      }
      case ERROR_NO_PDF_FILE -> {
        this.errorNoPdfFile(request);
        return ExecutionState.END;
      }
      case ERROR_NO_XML_FILE -> {
        this.errorNoXmlFile(request);
        return ExecutionState.END;
      }
      default -> {
        context.put(SftpProcessConstant.SFTP_FILE_REQUEST, getUserUuid(request));
        return ExecutionState.NEXT;
      }
    }
  }

  private SftpFileRequest getUserUuid(SftpFileRequest request) {
    var userUuid = FileDetectionService.getUuid(request.getFilename(), 0);
    request.setUserUuid(userUuid);
    this.facadeUrl.setUserUuid(userUuid);
    return request;
  }

  private void errorInvalidFilename(SftpFileRequest request) {
    // implement a process of invalid filename error
    log.info("Invalid file name: {}", request.getFilename());
    request.setMessage("Le nom du fichier n'est pas conforme.");
    var errorEmail =
        EmailModel.builder()
            .subject("Le nom du fichier n'est pas conforme.")
            .message("Le nom du fichier n'est pas conforme: ")
            .build();
    this.processErrorFileAndSendMail(request, errorEmail);
  }

  private void errorNoPdfFile(SftpFileRequest request) {
    // implement a process of pdf file not available error
    log.info("The pdf file not found: {}", request.getFilename());
    request.setMessage("Fichier PDF/fichier descriptif manquant.");
    var errorEmail =
        EmailModel.builder()
            .subject("Fichier PDF/fichier descriptif manquant.")
            .message("Fichier PDF/fichier descriptif manquant: ")
            .build();
    this.processErrorFileAndSendMail(request, errorEmail);
  }

  private void errorNoXmlFile(SftpFileRequest request) {
    // implement a process of xml file not available error
    log.info("The xml file not found: {}", request.getFilename());
    request.setMessage("La fiche descriptive n'est pas conforme.");
    var errorEmail =
        EmailModel.builder()
            .subject("La fiche descriptive n'est pas conforme.")
            .message("La fiche descriptive n'est pas conforme: ")
            .build();
    this.processErrorFileAndSendMail(request, errorEmail);
  }

  public void errorDocsNotMatched(SftpFileRequest request) {
    log.error("Descriptive file does not match PDF file(s)");
    request.setMessage("Le fichier descriptif ne correspond pas au(x) fichier(s) PDF.");
    var errorEmail =
        EmailModel.builder()
            .subject("Le fichier descriptif ne correspond pas au(x) fichier(s) PDF.")
            .message("Le fichier descriptif ne correspond pas au(x) fichier(s) PDF: ")
            .build();
    this.processErrorFileAndSendMail(request, errorEmail);
  }

  public void errorProjectValidation(SftpFileRequest request, String message) {
    log.error("XML validation data failed");
    request.setMessage(message);
    var errorEmail =
        EmailModel.builder().subject("XML validation data failed").message(message).build();
    this.processErrorFileAndSendMail(request, errorEmail);
  }

  public void errorUserNotFound(SftpFileRequest request) {
    log.error("Reference-client is invalid : " + request.getCorporateUser().getNormalUser());
    request.setMessage(
        "L'utilisateur de l'entreprise avec cet uuid : "
            + request.getCorporateUser().getNormalUser().getUserId()
            + " n'est pas trouvé.");
    var errorEmail =
        EmailModel.builder()
            .subject("Le client de référence n'est pas valide.")
            .message("Le client de référence n'est pas valide: ")
            .build();
    if (Objects.nonNull(request.getCorporateUser().getEmail()))
      this.processErrorFileAndSendMail(request, errorEmail);
    else this.processErrorFileAndSendMail(request, null);
  }

  public void errorTemplateNotFound(SftpFileRequest request) {
    log.error("Template not found");
    request.setMessage("Modèle non trouvé");
    var errorEmail =
        EmailModel.builder().subject("Modèle non trouvé").message("Modèle non trouvé: ").build();
    this.processErrorFileAndSendMail(request, errorEmail);
  }

  public void errorAuthorizeTemplate(SftpFileRequest request) {
    log.error("Template authorization failed");
    request.setMessage("Échec de l'autorisation du modèle");
    var errorEmail =
        EmailModel.builder()
            .subject("Échec de l'autorisation du modèle")
            .message("Échec de l'autorisation du modèle: ")
            .build();
    this.processErrorFileAndSendMail(request, errorEmail);
  }

  public void errorNotMatchTemplate(SftpFileRequest request) {
    log.error("Project's template does not match");
    request.setMessage("Le modèle de document ne correspond pas");
    var errorEmail =
        EmailModel.builder()
            .subject("Le modèle de document ne correspond pas")
            .message("Le modèle de document ne correspond pas: ")
            .build();
    this.processErrorFileAndSendMail(request, errorEmail);
  }

  public void processErrorFileAndSendMail(SftpFileRequest request, EmailModel emailModel) {
    // create error.txt file
    this.fileService.createErrorMessage(request);
    // copy file to error folder in source-out
    this.fileService.copyFileToErrorOUT(request);
    // copy file from processing folder into error folder in source-in
    this.fileService.moveFileToErrorIN(request);
    // send error mail to corporate user
    if (Objects.nonNull(request.getCorporateUser()) && Objects.nonNull(emailModel))
      this.mailService.sendErrorMail(request, emailModel);
  }
}
