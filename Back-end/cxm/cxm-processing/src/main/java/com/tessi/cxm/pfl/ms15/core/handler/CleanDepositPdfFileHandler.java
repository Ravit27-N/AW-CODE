package com.tessi.cxm.pfl.ms15.core.handler;

import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.tessi.cxm.pfl.ms15.constant.ProcessingConstant;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanDepositPdfFileHandler extends AbstractExecutionHandler {
  private final FileService fileService;
  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return {@link ExecutionState}
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var fileId = context.get(ProcessingConstant.FILE_ID, String.class);
    Path filePath =
        this.fileService.getPath(PreProcessingConstant.COMPOSED_PDF_PATH).resolve(fileId);
    log.info("The file will be deleted in a second.");
    this.fileService.deleteDirectoryQuietly(filePath);

    return ExecutionState.NEXT;
  }
}
