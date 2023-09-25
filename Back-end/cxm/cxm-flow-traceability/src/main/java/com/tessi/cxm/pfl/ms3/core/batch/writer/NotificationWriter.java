package com.tessi.cxm.pfl.ms3.core.batch.writer;

import com.tessi.cxm.pfl.ms3.entity.FlowDocumentNotification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Writer class for write item by using {@link ItemWriter}.
 *
 * @author Piseth KHON
 * @author Sokhour LACH
 */
@Slf4j
public class NotificationWriter implements ItemWriter<List<FlowDocumentNotification>> {

  private final JpaItemWriter<FlowDocumentNotification> jpaItemWriter;

  public NotificationWriter(JpaItemWriter<FlowDocumentNotification> jpaItemWriter) {
    this.jpaItemWriter = jpaItemWriter;
  }

  /** {@inheritDoc} */
  @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
  @Override
  public void write(List<? extends List<FlowDocumentNotification>> items) {
    for (var o : items) {
      this.jpaItemWriter.write(o);
    }
  }
}
