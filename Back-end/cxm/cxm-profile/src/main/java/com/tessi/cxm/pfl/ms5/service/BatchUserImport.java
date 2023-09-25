package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.core.flow.UserCreationManager;
import com.tessi.cxm.pfl.ms5.dto.BatchUserResponseDto;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class BatchUserImport {

  private final UserCreationManager userCreationManager;
  private final BatchUserResource batchUserResource;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public BatchUserResponseDto process(ExecutionContext executionContext) throws IOException {
    long total = 0L;
    long failedCount = 0L;
    long successCount = 0L;
    boolean hasMoreRecord;
    do {
      UserRecord data = batchUserResource.read();
      if (data != null) {
        total++;
        try {
          userCreationManager.create(data, executionContext);
          successCount++;
        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
          failedCount++;
        }
        hasMoreRecord = true;
      } else {
        hasMoreRecord = false;
      }
    } while (hasMoreRecord);

    this.batchUserResource.close();

    return BatchUserResponseDto.builder()
        .total(total)
        .successCount(successCount)
        .errorCount(failedCount)
        .build();
  }
}
