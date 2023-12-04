package com.innovationandtrust.process.chain.handler.eid;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.share.enums.FileAction;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.file.provider.FileProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class IdentityDocumentHandlerTest {

  private IdentityDocumentHandler identityDocumentHandler;
  @Mock private FileProvider fileProvider;
  private ExecutionContext context;
  private String basePath;

  @BeforeEach
  public void setUp() {

    identityDocumentHandler =
        spy(new IdentityDocumentHandler(fileProvider, mock(JsonFileProcessHandler.class)));
    basePath = UnitTestProvider.fileProvider().getBasePath();
    context = UnitTestProvider.getContext();
    context.put(SignProcessConstant.FILE_ACTION, FileAction.DOWNLOAD);
  }

  @Test
  @DisplayName("[IdentityDocumentHandler] Identity Document Handler")
  void testIdentityDocumentHandler() {
    final var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    project.setSignatureLevel(SignatureSettingLevel.QUALIFY.getValue());
    project
        .getParticipants()
        .forEach(
            participant -> {
              participant.setDocumentFront(UnitTestConstant.IMAGE_DOC_ID);
              participant.setDocumentBack(UnitTestConstant.IMAGE_DOC_ID);
            });
    when(this.fileProvider.basePath()).thenReturn(basePath);
    identityDocumentHandler.execute(context);
    verify(identityDocumentHandler).execute(context);
  }

  @Test
  @DisplayName("[IdentityDocumentHandler] Identity Document Not Exists")
  void testIdentityDocumentHandlerExist() {
    final var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    project.setSignatureLevel(SignatureSettingLevel.QUALIFY.getValue());
    project
        .getParticipants()
        .forEach(
            participant -> {
              participant.setDocumentFront(UnitTestConstant.IMAGE_NOT_EXIST);
              participant.setDocumentBack(UnitTestConstant.IMAGE_NOT_EXIST);
            });
    when(this.fileProvider.basePath()).thenReturn(basePath);
    identityDocumentHandler.execute(context);
    verify(identityDocumentHandler).execute(context);
  }
}
