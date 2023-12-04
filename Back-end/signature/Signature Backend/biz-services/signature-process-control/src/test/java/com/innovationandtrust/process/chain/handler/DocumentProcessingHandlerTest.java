package com.innovationandtrust.process.chain.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.DocumentProcessAction;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.SignedDocumentService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@ExtendWith(SpringExtension.class)
class DocumentProcessingHandlerTest {

  private DocumentProcessingHandler documentProcessingHandler;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private SignedDocumentService signedDocumentService;

  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  private Project project;
  private ExecutionContext context;
  private JsonFileProcessHandler jsonFileProcessHandler;

  @BeforeEach
  public void setup() {

    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    documentProcessingHandler = spy(new DocumentProcessingHandler(signedDocumentService));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
  }

  private void downloadManifestWhen() {
    // given
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.DOWNLOAD_MANIFEST);
    project.getParticipants().forEach(UnitTestProvider::toProcessed);
    var userCompany = UnitTestProvider.getUserCompany();
    var user = UnitTestProvider.getUser();

    // update project to be finished for possible downloading manifest.
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    jsonFileProcessHandler.execute(context);

    // when
    when(profileFeignClient.findCompanyUserById(anyLong())).thenReturn(userCompany);
    when(profileFeignClient.findUserById(anyLong())).thenReturn(user);
    when(apiNgFeignClient.downloadManifest(anyLong())).thenReturn("byte[]".getBytes());
  }

  private void downloadManifestAfter() {
    // update json file to original project for other test case
    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
    jsonFileProcessHandler.execute(context);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  @Test
  @Order(1)
  @DisplayName("[Document Process] DOWNLOAD_MANIFEST project not finished")
  void downloadManifestError() {
    // given
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.DOWNLOAD_MANIFEST);

    var exception =
        assertThrows(
            BadRequestException.class, () -> this.documentProcessingHandler.execute(context));

    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @Order(2)
  @DisplayName("[Document Process] DOWNLOAD_MANIFEST project no session id")
  void download_manifest_error_no_session() {
    // when
    this.downloadManifestWhen();
    project.getDetail().setSessionId(null);
    context.put(SignProcessConstant.PROJECT_KEY, project);

    var exception =
        assertThrows(
            IllegalArgumentException.class, () -> this.documentProcessingHandler.execute(context));

    log.info("[Exception thrown]: {}", exception.getMessage());

    // after
    this.downloadManifestAfter();
  }

  @Test
  @Order(3)
  @DisplayName("[Document Process] DOWNLOAD_MANIFEST")
  void download_manifest() {
    // when
    this.downloadManifestWhen();

    this.documentProcessingHandler.execute(context);
    verify(this.documentProcessingHandler).execute(context);

    // after
    this.downloadManifestAfter();
  }

  @Test
  @Order(4)
  @DisplayName("[Document Process] DOWNLOAD_MANIFEST close session fail")
  void download_manifest_close_session() {
    // when
    this.downloadManifestWhen();

    doThrow(new RuntimeException()).when(apiNgFeignClient).closeSession(anyLong(), any());
    this.documentProcessingHandler.execute(context);
    verify(this.documentProcessingHandler).execute(context);

    // after
    this.downloadManifestAfter();
  }

  @Test
  @DisplayName("[Document Process] CHECK_PROJECT_FINISH")
  void check_project_finish() {
    // given
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.CHECK_PROJECT_FINISH);

    this.documentProcessingHandler.execute(context);
    verify(this.documentProcessingHandler).execute(context);
  }

  @Test
  @DisplayName("[Document Process] DOWNLOAD_SIGNED_DOC")
  void download_signed_doc() {
    // given
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.DOWNLOAD_SIGNED_DOC);
    context.put(SignProcessConstant.DOC_ID, UnitTestConstant.DOC_ID);

    // when
    when(this.apiNgFeignClient.downloadDocument(anyString())).thenReturn("Document".getBytes());

    this.documentProcessingHandler.execute(context);
    verify(this.documentProcessingHandler).execute(context);
  }

  @Test
  @DisplayName("[Document Process] DOWNLOAD_SIGNED_DOC error")
  void download_signed_doc_error() {
    // given
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.DOWNLOAD_SIGNED_DOC);

    var exception =
        assertThrows(
            IllegalArgumentException.class, () -> this.documentProcessingHandler.execute(context));

    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @DisplayName("[Document Process] UPLOAD_DOC_FOR_SIGN")
  void upload_doc_for_sign() {
    // given
    MultipartFile file = UnitTestProvider.getDocument();
    context.put(SignProcessConstant.UPLOAD_MODIFIED_DOC_FOR_SIGN, file);
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.UPLOAD_DOC_FOR_SIGN);
    context.put(SignProcessConstant.DOC_ID, UnitTestConstant.DOC_ID);

    this.documentProcessingHandler.execute(context);
    verify(this.documentProcessingHandler).execute(context);
  }

  @Test
  @DisplayName("[Document Process] VIEW_DOC_FOR_SIGN")
  void view_doc_for_sign() {
    context.put(
        SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.VIEW_DOC_FOR_SIGN);
    context.put(SignProcessConstant.DOC_ID, UnitTestConstant.DOC_ID);

    this.documentProcessingHandler.execute(context);
    verify(this.documentProcessingHandler).execute(context);
  }

  @Test
  @DisplayName("[Document Process] DELETED_DOC")
  void deleted_doc() {
    context.put(SignProcessConstant.DOCUMENT_PROCESS_ACTION, DocumentProcessAction.DELETED_DOC);
    context.put(SignProcessConstant.DOC_ID, UnitTestConstant.DOC_ID);
    try (MockedStatic<FileProvider> fileProviderMockedStatic = mockStatic(FileProvider.class)) {
      this.documentProcessingHandler.execute(context);
      verify(this.documentProcessingHandler).execute(context);
    }
  }

  @Test
  @DisplayName("[Document Process] DOWNLOAD_DOC")
  void download_doc() {
    // given
    project.getDocuments().get(0).setFullPath("fullPath/");
    context.put(SignProcessConstant.PROJECT_KEY, project);

    // when
    when(projectFeignClient.downloadDocument(anyString()))
        .thenReturn(new ByteArrayResource("Byte[]".getBytes()));
    this.documentProcessingHandler.execute(context);
    verify(this.documentProcessingHandler).execute(context);
  }
}
