package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.DocumentProcessAction;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.exception.ConvertPDFFailException;
import com.innovationandtrust.process.model.FileResponse;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.CloseSession;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.nimbusds.common.contenttype.ContentType;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentProcessingHandler extends AbstractExecutionHandler {

  public static final String DOCUMENT_PATH = "document";

  private final FileProvider fileProvider;
  private final ProjectFeignClient projectFeignClient;
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final ProfileFeignClient profileFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var action =
        context
            .find(SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.class)
            .orElse(DocumentProcessAction.DOWNLOAD_DOC_FOR_SIGN);
    switch (action) {
      case CHECK_PROJECT_FINISH -> context.put(
          SignProcessConstant.IS_PROJECT_FINISHED, project.isFinished());
      case DOWNLOAD_MANIFEST -> {
        var fileResponse = this.downloadManifest(project);
        context.put(SignProcessConstant.DOWNLOAD_MANIFEST, fileResponse);
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
      }
      case DOWNLOAD_SIGNED_DOC -> {
        var fileResponse =
            this.downloadSignedDocument(
                project, context.get(SignProcessConstant.DOC_ID, String.class));
        context.put(SignProcessConstant.DOWNLOAD_SIGNED_DOC, fileResponse);
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
      }
      case UPLOAD_DOC_FOR_SIGN -> {
        this.uploadDocument(
            context.get(SignProcessConstant.UPLOAD_MODIFIED_DOC_FOR_SIGN, MultipartFile.class),
            project,
            context.get(SignProcessConstant.DOC_ID, String.class));
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
      }
      case VIEW_DOC_FOR_SIGN -> context.put(
          SignProcessConstant.VIEW_DOC_FOR_SIGN,
          this.viewDocument(project, context.get(SignProcessConstant.DOC_ID, String.class)));
      case DELETED_DOC -> deleteDocuments(project.getFlowId());
      default -> this.downloadDocuments(project);
    }
    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private FileResponse downloadManifest(Project project) {
    if (project.isFinished()) {
      if (Objects.nonNull(project.getSessionId())) {
        try {
          this.apiNgFeignClient.closeSession(
              project.getSessionId(),
              new CloseSession(true, "download proof of signature project."));
        } catch (Exception e) {
          log.error("Failed to close session or session already closed", e);
        }
        var user = this.profileFeignClient.findCompanyUserById(project.getCreatedBy());
        var corporate = this.profileFeignClient.findUserById(user.getCreatedBy());
        var resource = this.apiNgFeignClient.downloadManifest(project.getSessionId());
        var fileName =
            String.format(
                "%s_%s_%s_proof.pdf",
                corporate.getUserEntityId(), user.getUserEntityId(), project.getId());
        return new FileResponse(
            resource, resource.length, ContentType.APPLICATION_PDF.getType(), fileName);
      }
      throw new IllegalArgumentException("Project does not have sessionId.");
    }
    throw new BadRequestException("The project is not finished.");
  }
  
  /**
   * To download the documents from the signature-project-management microservice.
   *
   * @param project refers to the object of {@link Project}
   */
  private void downloadDocuments(Project project) {
    project.getDocuments().stream()
        .filter(doc -> StringUtils.hasText(doc.getFullPath()))
        .forEach(
            doc -> {
              var uuid = UUID.randomUUID().toString();
              var resource = projectFeignClient.downloadDocument(doc.getFullPath());
              this.fileProvider.storeFile(
                  resource,
                  doc.getFileName(),
                  FileUtils.path(project.getFlowId(), DOCUMENT_PATH).toString());
              doc.setUuid(uuid);
              doc.setFullPath("");
            });
  }

  private String viewDocument(Project project, String docId) {
    var doc = project.getDocumentByUuid(docId);
    var filePath =
        FileUtils.path(project.getFlowId(), DocumentProcessingHandler.DOCUMENT_PATH)
            .resolve(doc.getFileName());
    return this.fileProvider.encodeFileToBase64(filePath.toString());
  }

  private FileResponse downloadSignedDocument(Project project, String docId) {
    var doc = project.getDocumentByUuid(docId);
    if (Objects.nonNull(doc)) {
      var resource = this.apiNgFeignClient.downloadDocument(doc.getDocUrl());
      return new FileResponse(
          resource, resource.length, doc.getContentType(), doc.getOriginalFileName());
    }
    throw new IllegalArgumentException(
        "The document is not available! Please verify your `docId` again!");
  }

  private void uploadDocument(MultipartFile file, Project project, String docId) {
    var doc = project.getDocumentByUuid(docId);
    try (var pdfreader = new PdfReader(file.getInputStream());
        var pdfWriter =
            new PdfWriter(
                Path.of(
                        fileProvider.basePath(),
                        project.getFlowId(),
                        DOCUMENT_PATH,
                        doc.getFileName())
                    .toString())) {
      PdfDocument pdfDoc = new PdfDocument(pdfreader, pdfWriter);
      PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
      form.flattenFields();
      pdfDoc.close();
    } catch (Exception exception) {
      var message = "Fail to convert document :" + doc.getFileName();
      log.error(message + " :" + exception);
      throw new ConvertPDFFailException(message);
    }
  }

  private void deleteDocuments(String flowId) {
    Executors.newSingleThreadExecutor()
        .execute(() -> this.fileProvider.deleteFile(FileUtils.path(flowId, DOCUMENT_PATH)));
  }
}
