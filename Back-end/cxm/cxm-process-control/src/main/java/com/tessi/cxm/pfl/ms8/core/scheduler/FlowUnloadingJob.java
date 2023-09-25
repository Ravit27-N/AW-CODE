package com.tessi.cxm.pfl.ms8.core.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.dto.FlowUnloadingPayload;
import com.tessi.cxm.pfl.ms8.service.FlowUnloadingService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class FlowUnloadingJob extends QuartzJobBean implements InterruptableJob {

  private FlowUnloadingService flowUnloadingService;
  private ObjectMapper objectMapper;

  @Autowired
  public void setFlowUnloadingService(
      FlowUnloadingService flowUnloadingService) {
    this.flowUnloadingService = flowUnloadingService;
  }

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }


  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    try {
      FlowUnloadingPayload payload = this.getFLowUnloadingPayload(context);
      log.info("Start unloading for client: {}.", payload.getClientId());
      this.flowUnloadingService.unloadFlow(payload, context.getFireTime());
      log.info("Flow unloading for client {} is finished.", payload.getClientId());
    } catch (Exception exception) {
      log.error("Failed to unload Flows.", exception);
    }
  }

  private FlowUnloadingPayload getFLowUnloadingPayload(JobExecutionContext context)
      throws JsonProcessingException {
    var payload = context.getMergedJobDataMap()
        .get(ProcessControlConstants.CLIENT_UNLOADING_DETAIL);
    if (payload != null) {
      return this.objectMapper.readValue(payload.toString(), FlowUnloadingPayload.class);
    }
    return new FlowUnloadingPayload();
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    log.info("Interrupting");
  }
}
