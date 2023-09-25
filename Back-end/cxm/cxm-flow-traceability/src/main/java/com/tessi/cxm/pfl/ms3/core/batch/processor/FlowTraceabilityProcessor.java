package com.tessi.cxm.pfl.ms3.core.batch.processor;

import com.tessi.cxm.pfl.ms3.dto.CountDocumentOfFlowProjection;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component("flowTraceabilityProcessor")
@StepScope
@Slf4j
public class FlowTraceabilityProcessor implements
    ItemProcessor<CountDocumentOfFlowProjection, FlowTraceability>,
    StepExecutionListener {

  @Override
  public FlowTraceability process(CountDocumentOfFlowProjection countDocumentOfFlow)
      throws Exception {
    log.info("Mapping object in FlowTraceabilityProcessor");
    return this.mapData(countDocumentOfFlow);
  }

  private FlowTraceability mapData(CountDocumentOfFlowProjection countDocumentOfFlow) {
    FlowTraceability flow = countDocumentOfFlow.getFlowTraceability();
    flow.setStatus(FlowTraceabilityStatus.COMPLETED.getValue());
    flow.setDateStatus(new Date());
    var flowHistory = flow.getFlowHistories().stream().filter(
            his -> his.getEvent().equals(FlowTraceabilityStatus.COMPLETED.getFlowHistoryStatus()))
        .findFirst();
    if (flowHistory.isPresent()) {
      var history = flowHistory.get();
      history.setDateTime(flow.getDateStatus());
      flow.addFlowHistory(history);
      return flow;
    }

    var history = new FlowHistory();
    history.setDateTime(new Date());
    history.setEvent(FlowTraceabilityStatus.COMPLETED.getFlowHistoryStatus());
    history.setCreatedAt(new Date());
    history.setCreatedBy(flow.getLastModifiedBy());
    history.setServer(ComputerSystemProduct.getDeviceId());
    flow.addFlowHistory(history);
    return flow;
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {
    //do nothing
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    return ExitStatus.COMPLETED;
  }
}
