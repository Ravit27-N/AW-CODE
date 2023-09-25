package com.tessi.cxm.pfl.ms3.core.batch.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDocumentContext;
import java.util.List;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

/**
 * Reader class for read item from executionContext by using {@link ItemReader}.
 *
 * @author Piseth KHON
 */
@Component
@StepScope
public class ElementAssociationReader
    implements ItemReader<ElementAssociationDocumentContext>,
    StepExecutionListener {

  private List<ElementAssociationDocumentContext> elementAssociation;
  private int elementAssociationCount = 0;

  /**
   * Reads a piece of input data and advance to the next one. Implementations <strong>must</strong>
   * return <code>null</code> at the end of the input data set. In a transactional setting, caller
   * might get the same item twice from successive calls (or otherwise), if the first call was in a
   * transaction that rolled back.
   *
   * @return T the item to be processed or {@code null} if the data source is exhausted
   * @throws ParseException                if there is a problem parsing the current record (but the
   *                                       next one may still be valid)
   * @throws NonTransientResourceException if there is a fatal exception in the underlying resource.
   *                                       After throwing this exception implementations should
   *                                       endeavour to return null from subsequent calls to read.
   * @throws UnexpectedInputException      if there is an uncategorized problem with the input data.
   *                                       Assume potentially transient, so subsequent calls to read
   *                                       might succeed.
   * @throws Exception                     if there is a non-specific error.
   */
  @Override
  public ElementAssociationDocumentContext read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (elementAssociationCount < this.elementAssociation.size()) {
      return this.elementAssociation.get(elementAssociationCount++);
    }
    return null;
  }

  /**
   * Initialize the state of the listener with the {@link StepExecution} from the current scope. Get
   * elementsAssociation from context by current scope.
   *
   * @param stepExecution instance of {@link StepExecution}.
   */
  @Override
  public void beforeStep(StepExecution stepExecution) {
    ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

    this.elementAssociation = this.getElementAssociation(executionContext);
  }

  /**
   * Give a listener a chance to modify the exit status from a step. The value returned will be
   * combined with the normal exit status using {@link ExitStatus#and(ExitStatus)}.
   *
   * <p>Called after execution of step's processing logic (both successful or failed). Throwing
   * exception in this method has no effect, it will only be logged.
   *
   * @param stepExecution {@link StepExecution} instance.
   * @return an {@link ExitStatus} to combine with the normal value. Return {@code null} to leave
   * the old value unchanged.
   */
  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    return ExitStatus.COMPLETED;
  }

  /**
   * Convert value from execution context to base object.
   *
   * @param executionContext current scope of execution context
   * @return base object
   */
  public List<ElementAssociationDocumentContext> getElementAssociation(
      ExecutionContext executionContext) {
    var mapper = new ObjectMapper();
    return mapper.convertValue(
        executionContext.get(FlowTraceabilityConstant.ELEMENT_ASSOCIATION_CONTEXT),
        new TypeReference<>() {});
  }
}
