package com.tessi.cxm.pfl.ms3.core.batch.reader;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reader class for read item from source by using {@link RepositoryItemReader}.
 *
 * @author Piseth KHON
 */
@Slf4j
public class NotificationJpaReader<T> extends RepositoryItemReader<T> {

  @Getter
  @Setter
  private List<T> flowDocuments;

  /**
   * Read next item from input.
   *
   * @throws Exception Allows subclasses to throw checked exceptions for interpretation by the
   *                   framework
   */
  @Transactional(readOnly = true)
  public void readItem() throws Exception {
    var result = new ArrayList<T>();

    while (true) {
      final T flowDocument = super.read();
      if (flowDocument == null) {
        close();
        break;
      }
      result.add(flowDocument);
    }

    log.info("siz of item {}", result.size());
    setFlowDocuments(result);
  }
}
