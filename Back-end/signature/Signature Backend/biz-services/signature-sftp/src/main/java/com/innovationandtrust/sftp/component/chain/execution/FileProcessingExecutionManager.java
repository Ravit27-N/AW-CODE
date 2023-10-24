package com.innovationandtrust.sftp.component.chain.execution;

import com.innovationandtrust.sftp.component.chain.handler.FileErrorHandler;
import com.innovationandtrust.sftp.component.chain.handler.FileValidatedHandler;
import com.innovationandtrust.sftp.component.chain.handler.ProjectCreationHandler;
import com.innovationandtrust.sftp.component.chain.handler.TemplateValidationHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileProcessingExecutionManager extends ExecutionManager {

  private final FileErrorHandler fileErrorHandler;
  private final FileValidatedHandler fileValidatedHandler;
  private final TemplateValidationHandler templateValidationHandler;
  private final ProjectCreationHandler projectCreationHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            fileErrorHandler,
            fileValidatedHandler,
            templateValidationHandler,
            projectCreationHandler));
  }
}
