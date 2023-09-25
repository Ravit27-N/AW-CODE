package com.tessi.cxm.pfl.ms3.core.batch.reader;


import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.ms3.dto.CountDocumentOfFlowProjection;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class FlowTraceabilityReader implements ItemReader<CountDocumentOfFlowProjection>,
    StepExecutionListener {

  private List<CountDocumentOfFlowProjection> flows;

  private int rowCount = 0;

  private ModelMapper modelMapper;

  @Autowired
  public void setModelMapper(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public CountDocumentOfFlowProjection read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (rowCount < this.flows.size()) {
      return this.flows.get(rowCount++);
    }
    return null;
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.flows = this.getDocuments(stepExecution.getJobExecution().getExecutionContext()).stream()
        .map(obj -> modelMapper.map(obj, CountDocumentOfFlowProjection.class)).collect(
            Collectors.toList());
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    stepExecution.getJobExecution().getExecutionContext().remove(FlowTraceabilityConstant.FLOW_CONTEXT);
    return ExitStatus.COMPLETED;
  }

  public List<Object> getDocuments(
      ExecutionContext executionContext) {
    return (List<Object>) executionContext.get(FlowTraceabilityConstant.FLOW_CONTEXT);
  }
}
