package com.innovationandtrust.sftp.component.chain.execution;

import com.innovationandtrust.sftp.component.chain.handler.FileValidationHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileValidatingExecutionManager extends ExecutionManager {

  private final FileValidationHandler fileValidationHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(fileValidationHandler));
  }
}
