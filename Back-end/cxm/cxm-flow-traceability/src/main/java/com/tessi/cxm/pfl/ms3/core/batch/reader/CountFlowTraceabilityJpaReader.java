package com.tessi.cxm.pfl.ms3.core.batch.reader;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class CountFlowTraceabilityJpaReader<T extends Serializable> extends
    RepositoryItemReader<T> {

  @Transactional(readOnly = true)
  public List<T> getCountFromFlow() {
    try {
      return this.doPageRead();
    } catch (Exception ex) {
      log.warn("Unable to count document by flow", ex);
      return Collections.emptyList();
    }
  }
}
