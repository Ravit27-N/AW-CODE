package com.innovationandtrust.process.chain.execution;

import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadModifiedDocumentExecutionHandler extends ExecutionManager {

  private final JsonFileProcessHandler jsonFileProcessHandler;

  private final DocumentProcessingHandler documentProcessingHandler;

  public UploadModifiedDocumentExecutionHandler(
      JsonFileProcessHandler jsonFileProcessHandler,
      DocumentProcessingHandler documentProcessingHandler) {
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.documentProcessingHandler = documentProcessingHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(
        List.of(
            // Load existing json file for setting up a signing process
            jsonFileProcessHandler,
            // To upload modified the document before request signing
            documentProcessingHandler,
            // Update to data to json file after requesting a sign process
            jsonFileProcessHandler));
  }
}
