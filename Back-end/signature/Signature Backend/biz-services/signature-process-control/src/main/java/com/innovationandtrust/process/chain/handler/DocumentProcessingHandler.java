package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.DocumentProcessAction;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.service.SignedDocumentService;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/** This class about processing on document required. */
@Slf4j
@Component
public class DocumentProcessingHandler extends AbstractExecutionHandler {

  private final SignedDocumentService signedDocumentService;

  public DocumentProcessingHandler(SignedDocumentService signedDocumentService) {
    this.signedDocumentService = signedDocumentService;
  }

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
        var fileResponse = this.signedDocumentService.downloadManifest(project);
        context.put(SignProcessConstant.DOWNLOAD_MANIFEST, fileResponse);
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
      }
      case DOWNLOAD_SIGNED_DOC -> {
        var fileResponse =
            this.signedDocumentService.downloadSignedDocument(
                project, context.get(SignProcessConstant.DOC_ID, String.class));
        context.put(SignProcessConstant.DOWNLOAD_SIGNED_DOC, fileResponse);
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
      }
      case UPLOAD_DOC_FOR_SIGN -> {
        this.signedDocumentService.uploadDocument(
            context.get(SignProcessConstant.UPLOAD_MODIFIED_DOC_FOR_SIGN, MultipartFile.class),
            project,
            context.get(SignProcessConstant.DOC_ID, String.class));
        context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
      }
      case VIEW_DOC_FOR_SIGN -> context.put(
          SignProcessConstant.VIEW_DOC_FOR_SIGN,
          this.signedDocumentService.viewDocument(
              project, context.get(SignProcessConstant.DOC_ID, String.class)));
      case DELETED_DOC -> this.signedDocumentService.deleteDocuments(project.getFlowId());
      case STORE_SIGNED_DOCUMENTS -> this.signedDocumentService.storeSignedDocuments(project);
      case DOWNLOAD_DOC_FOR_SIGN -> this.signedDocumentService.downloadDocuments(project);
      default -> log.warn("[NO DOCUMENT ACTION]");
    }
    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }
}
