package com.tessi.cxm.pfl.ms3.core.batch.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.ms3.core.batch.reader.CountFlowDocumentJpaReader;
import com.tessi.cxm.pfl.ms3.core.batch.reader.CountFlowTraceabilityJpaReader;
import com.tessi.cxm.pfl.ms3.dto.CountDocumentOfFlowImpl;
import com.tessi.cxm.pfl.ms3.dto.CountDocumentOfFlowProjection;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class FlowTraceabilityListener implements StepExecutionListener {

  private List<Long> docIds;

  private final ModelMapper mapper;

  private final CountFlowTraceabilityJpaReader<CountDocumentOfFlowProjection> flowCountJpaReader;
  private final CountFlowDocumentJpaReader<CountDocumentOfFlowProjection> documentCountJpaReader;

  private List<CountDocumentOfFlowImpl> countFromFlow;

  @Autowired
  public FlowTraceabilityListener(
      ModelMapper mapper,
      @Qualifier("countDocumentFromFlowReader")
      CountFlowTraceabilityJpaReader<CountDocumentOfFlowProjection> flowCountJpaReader,
      @Qualifier("countDocumentOfFlowReader")
      CountFlowDocumentJpaReader<CountDocumentOfFlowProjection> documentCountJpaReader) {
    this.mapper = mapper;
    this.flowCountJpaReader = flowCountJpaReader;
    this.documentCountJpaReader = documentCountJpaReader;
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.docIds = this.getDocuments(stepExecution.getJobExecution().getExecutionContext());
    log.info("Document id after receive notification: {}", this.docIds);
    this.countFromFlow = this.countFromFlow();
    List<CountDocumentOfFlowImpl> countFromDocuments = this.countFromDocument();
    var flows = countFromDocuments.stream()
        .filter(obj -> this.countFromFlow.stream().anyMatch(obj::equals))
        .collect(Collectors.toList());
    stepExecution.getJobExecution().getExecutionContext()
        .put(FlowTraceabilityConstant.FLOW_CONTEXT, flows);
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    stepExecution.getJobExecution().getExecutionContext()
        .remove(FlowTraceabilityConstant.FLOW_DOCUMENT_CONTEXT);
    return ExitStatus.COMPLETED;
  }

  private List<Long> getDocuments(ExecutionContext executionContext) {

    var objectMapper = new ObjectMapper();
    return objectMapper.convertValue(
        executionContext.get(FlowTraceabilityConstant.FLOW_DOCUMENT_CONTEXT),
        new TypeReference<>() {});
  }

  public List<CountDocumentOfFlowImpl> countFromDocument() {
    var flowIds = this.countFromFlow.stream().map(obj -> obj.getFlowTraceability().getId())
        .distinct()
        .collect(Collectors.toList());
    if (!flowIds.isEmpty()) {
      this.documentCountJpaReader.setArguments(
          List.of(flowIds, FlowDocumentStatus.getIgnoreStatus()));
      this.documentCountJpaReader.setPageSize(flowIds.size());
      return this.documentCountJpaReader.getDocuments().stream()
          .map(obj -> this.mapper.map(obj, CountDocumentOfFlowImpl.class)).collect(
              Collectors.toList());
    }
    return Collections.emptyList();
  }

  public List<CountDocumentOfFlowImpl> countFromFlow() {
    if (!this.docIds.isEmpty()) {
      this.flowCountJpaReader.setArguments(
          List.of(this.docIds.stream().distinct().collect(Collectors.toList())));
      this.flowCountJpaReader.setPageSize(this.docIds.size());
      return this.flowCountJpaReader.getCountFromFlow().stream()
          .map(obj -> this.mapper.map(obj, CountDocumentOfFlowImpl.class)).collect(
              Collectors.toList());
    }
    return Collections.emptyList();
  }
}
