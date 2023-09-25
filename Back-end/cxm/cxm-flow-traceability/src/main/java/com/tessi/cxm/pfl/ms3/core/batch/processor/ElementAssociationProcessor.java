package com.tessi.cxm.pfl.ms3.core.batch.processor;

import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDocumentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

/**
 * Implementation class for item transformation. Given an item as input, this interface provides an
 * extension point which allows for the application of business logic in an item oriented processing
 * scenario. It should be noted that while it's possible to return a different type than the one
 * provided, it's not strictly necessary. Furthermore, returning {@code null} indicates that the
 * item should not be continued to be processed.
 *
 * @author Piseth KHON
 */
@Component
@Slf4j
@StepScope
public class ElementAssociationProcessor
    implements ItemProcessor<ElementAssociationDocumentContext, ElementAssociation> {

  /**
   * Process the provided item, returning a potentially modified or new item for continued
   * processing. If the returned result is {@code null}, it is assumed that processing of the item
   * should not continue.
   *
   * <p>A {@code null} item will never reach this method because the only possible sources are:
   *
   * <ul>
   *   <li>an {@link ItemReader} (which indicates no more items)
   *   <li>a previous {@link ItemProcessor} in a composite processor (which indicates a filtered
   *       item)
   * </ul>
   *
   * @param item to be processed, never {@code null}.
   * @return potentially modified or new item for continued processing, {@code null} if processing
   *     of the provided item should not continue.
   * @throws Exception thrown if exception occurs during processing.
   */
  @Override
  public ElementAssociation process(ElementAssociationDocumentContext item) throws Exception {
    var document = new FlowDocument();
    document.setId(item.getDocumentId());
    return ElementAssociation.builder()
        .elementName(item.getElementName())
        .fileId(item.getFileId())
        .flowDocument(document)
        .extension(item.getExtension())
        .build();
  }
}
