package com.innovationandtrust.sftp.chain.execution;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.innovationandtrust.sftp.component.chain.execution.CreateUserAndFolderExecutionManager;
import com.innovationandtrust.sftp.component.chain.execution.FileProcessingExecutionManager;
import com.innovationandtrust.sftp.component.chain.execution.FileValidatingExecutionManager;
import com.innovationandtrust.sftp.component.chain.handler.FileErrorHandler;
import com.innovationandtrust.sftp.component.chain.handler.FileValidatedHandler;
import com.innovationandtrust.sftp.component.chain.handler.FileValidationHandler;
import com.innovationandtrust.sftp.component.chain.handler.ProjectCreationHandler;
import com.innovationandtrust.sftp.component.chain.handler.SFTPGoCreateFolderHandler;
import com.innovationandtrust.sftp.component.chain.handler.SFTPGoCreateUserHandler;
import com.innovationandtrust.sftp.component.chain.handler.TemplateValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SFTPExecutionManagerTest {

  @Test
  @DisplayName("Create user and folder Execution manager")
  void testCreateUserAndFolderManager() {
    final var createUserAndFolderExecuteManager =
        spy(
            new CreateUserAndFolderExecutionManager(
                mock(SFTPGoCreateFolderHandler.class), mock(SFTPGoCreateUserHandler.class)));

    createUserAndFolderExecuteManager.afterPropertiesSet();
    // then
    verify(createUserAndFolderExecuteManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("File process execution manager")
  void testFileProcessExecutionManager() {

    final var fileProcessExecutionManager =
        spy(
            new FileProcessingExecutionManager(
                mock(FileErrorHandler.class),
                mock(FileValidatedHandler.class),
                mock(TemplateValidationHandler.class),
                mock(ProjectCreationHandler.class)));

    fileProcessExecutionManager.afterPropertiesSet();
    // then
    verify(fileProcessExecutionManager, times(1)).afterPropertiesSet();
  }

  @Test
  @DisplayName("File validating execution manager")
  void testFileValidatingExecutionManager() {

    final var fileValidatingExecutionManager =
        spy(new FileValidatingExecutionManager(mock(FileValidationHandler.class)));

    fileValidatingExecutionManager.afterPropertiesSet();
    // then
    verify(fileValidatingExecutionManager, times(1)).afterPropertiesSet();
  }
}
