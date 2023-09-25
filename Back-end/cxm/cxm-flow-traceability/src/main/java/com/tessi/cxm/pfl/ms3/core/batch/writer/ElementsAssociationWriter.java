package com.tessi.cxm.pfl.ms3.core.batch.writer;

import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;

/**
 * Implementation class for item writer by use {@link JpaItemWriter}.
 *
 * @author Piseth KHON
 */
@Slf4j
public class ElementsAssociationWriter implements ItemWriter<ElementAssociation> {
  private final JpaItemWriter<ElementAssociation> jpaItemWriter;

  public ElementsAssociationWriter(JpaItemWriter<ElementAssociation> jpaItemWriter) {
    this.jpaItemWriter = jpaItemWriter;
  }

  /**
   * Process the supplied data element. Will not be called with any null items in normal operation.
   *
   * @param items items to be written
   */
  @Override
  public void write(List<? extends ElementAssociation> items) {
    this.jpaItemWriter.write(items);
  }
}
