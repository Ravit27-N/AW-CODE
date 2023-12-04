package com.innovationandtrust.process.service;

import com.innovationandtrust.process.constant.PathConstant;
import com.innovationandtrust.process.exception.ConvertPDFFailException;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.CloseSession;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.nimbusds.common.contenttype.ContentType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class SignedDocumentService {

  private final ApiNgFeignClientFacade apiNgFeignClientFacade;
  private final ProfileFeignClient profileFeignClient;
  private final ProjectFeignClient projectFeignClient;
  private final FileProvider fileProvider;

  public SignedDocumentService(
      ApiNgFeignClientFacade apiNgFeignClientFacade,
      ProfileFeignClient profileFeignClient,
      ProjectFeignClient projectFeignClient,
      FileProvider fileProvider) {
    this.apiNgFeignClientFacade = apiNgFeignClientFacade;
    this.profileFeignClient = profileFeignClient;
    this.projectFeignClient = projectFeignClient;
    this.fileProvider = fileProvider;
  }

  public FileResponse downloadManifest(Project project) {
    if (!project.isFinished()) {
      throw new BadRequestException("The project is not finished.");
    }
    if (Objects.isNull(project.getSessionId())) {
      throw new IllegalArgumentException("Project does not have sessionId.");
    }
    byte[] resource = new byte[0];
    try {
      if (Objects.nonNull(project.getManifestFileName())) {
        Path path =
            Path.of(
                this.fileProvider.basePath(),
                project.getFlowId(),
                PathConstant.SIGNED_DOCUMENT_PATH,
                project.getManifestFileName());
        resource = Files.readAllBytes(path);
      }
      if (resource.length == 0) {
        this.apiNgFeignClientFacade.closeSession(
            project.getSessionId(), new CloseSession(true, "download proof of signature project."));
      }
    } catch (Exception e) {
      log.error("Failed to close session or session already closed", e);
    }
    if (resource.length == 0) {
      var user = this.profileFeignClient.findCompanyUserById(project.getCreatedBy());
      var corporate = this.profileFeignClient.findUserById(user.getCreatedBy());
      resource = this.apiNgFeignClientFacade.downloadManifest(project.getSessionId());
      project.setManifestFileName(
          String.format(
              "%s_%s_%s_proof.pdf",
              corporate.getUserEntityId(), user.getUserEntityId(), project.getId()));
      this.storeDocument(resource, project.getFlowId(), project.getManifestFileName());
    }
    return new FileResponse(
        resource,
        resource.length,
        ContentType.APPLICATION_PDF.getType(),
        project.getManifestFileName());
  }

  /**
   * To download the documents from the signature-project-management microservice.
   *
   * @param project refers to the object of {@link Project}
   */
  public void downloadDocuments(Project project) {
    project.getDocuments().stream()
        .filter(doc -> StringUtils.hasText(doc.getFullPath()))
        .forEach(
            doc -> {
              var uuid = UUID.randomUUID().toString();
              var resource = projectFeignClient.downloadDocument(doc.getFullPath());
              this.fileProvider.storeFile(
                  resource,
                  doc.getFileName(),
                  FileUtils.path(project.getFlowId(), PathConstant.DOCUMENT_PATH).toString());
              doc.setUuid(uuid);
              doc.setFullPath("");
            });
  }

  public String viewDocument(Project project, String docId) {
    var doc = project.getDocumentByUuid(docId);
    var filePath =
        FileUtils.path(project.getFlowId(), PathConstant.DOCUMENT_PATH).resolve(doc.getFileName());
    return this.fileProvider.encodeFileToBase64(filePath.toString());
  }

  public FileResponse downloadSignedDocument(Project project, String docId) {
    var doc = project.getDocumentByUuid(docId);

    //  Allow only authenticated user can download document by document id
    if (Objects.isNull(doc) && StringUtils.hasText(AuthenticationUtils.getAccessToken())) {
      doc = project.getDocumentById(docId);
    }
    if (Objects.nonNull(doc)) {
      log.info("Getting document file with name: {}...", doc.getFileName());

      Path path =
          Path.of(
              this.fileProvider.basePath(),
              project.getFlowId(),
              PathConstant.SIGNED_DOCUMENT_PATH,
              doc.getFileName());

      if (Files.exists(path)) {
        try {
          log.info("Downloading signed document from {}", PathConstant.SIGNED_DOCUMENT_PATH);
          var resource = Files.readAllBytes(path);
          return new FileResponse(
              resource, resource.length, doc.getContentType(), doc.getOriginalFileName());
        } catch (IOException exception) {
          log.error("Cannot download document", exception);
        }
      }
      return storeAndDownloadFromApiNg(doc, project.getFlowId());
    }

    throw new IllegalArgumentException(
        "The document is not available! Please verify your `docId` again!");
  }

  public void uploadDocument(MultipartFile file, Project project, String docId) {
    var doc = project.getDocumentByUuid(docId);
    try (var pdfreader = new PdfReader(file.getInputStream());
        var pdfWriter =
            new PdfWriter(
                Path.of(
                        fileProvider.basePath(),
                        project.getFlowId(),
                        PathConstant.DOCUMENT_PATH,
                        doc.getFileName())
                    .toString())) {
      PdfDocument pdfDoc = new PdfDocument(pdfreader, pdfWriter);
      PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
      form.flattenFields();
      pdfDoc.close();
    } catch (Exception exception) {
      var message = "Fail to convert document :" + doc.getFileName();
      log.error(message + " :" + exception);
      throw new ConvertPDFFailException(message, exception);
    }
  }

  public void deleteDocuments(String flowId) {
    Executors.newSingleThreadExecutor()
        .execute(
            () -> this.fileProvider.deleteFile(FileUtils.path(flowId, PathConstant.DOCUMENT_PATH)));
  }

  public void storeSignedDocuments(Project project) {
    Executors.newSingleThreadExecutor()
        .execute(
            () ->
                project
                    .getDocuments()
                    .forEach(
                        document -> {
                          try {
                            storeAndDownloadFromApiNg(document, project.getFlowId());
                          } catch (Exception exception) {
                            log.error("Error occur while storing signed document ", exception);
                          }
                        }));
  }

  private FileResponse storeAndDownloadFromApiNg(Document document, String flowId) {
    log.info("Downloading signed document from API NG ...");
    var resource = this.apiNgFeignClientFacade.downloadDocument(document.getDocUrl());

    log.info("Storing signed document ...");
    this.storeDocument(resource, flowId, document.getFileName());

    return new FileResponse(
        resource, resource.length, document.getContentType(), document.getOriginalFileName());
  }

  private void storeDocument(byte[] resource, String flowId, String fileName) {
    FileUtils.createDirIfNotExist(
        Path.of(this.fileProvider.basePath(), flowId, PathConstant.SIGNED_DOCUMENT_PATH));
    this.fileProvider.storeFile(
        new ByteArrayResource(resource),
        fileName,
        FileUtils.path(flowId, PathConstant.SIGNED_DOCUMENT_PATH).toString());
  }
}
