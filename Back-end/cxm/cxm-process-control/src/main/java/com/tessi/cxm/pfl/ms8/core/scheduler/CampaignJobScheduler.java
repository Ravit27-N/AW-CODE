package com.tessi.cxm.pfl.ms8.core.scheduler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.core.flow.portal.campaign.CampaignSchedulerLauncher;
import com.tessi.cxm.pfl.ms8.dto.CampaignJobData;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class CampaignJobScheduler extends QuartzJobBean implements InterruptableJob {
  private CampaignSchedulerLauncher campaignSchedulerLauncher;

  private KeycloakService keycloakService;
  private ObjectMapper objectMapper;

  @Autowired
  public void setCampaignSchedulerLauncher(CampaignSchedulerLauncher campaignSchedulerLauncher) {
    this.campaignSchedulerLauncher = campaignSchedulerLauncher;
  }

  @Autowired
  public void setKeycloakService(KeycloakService keycloakService) {
    this.keycloakService = keycloakService;
  }

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Execute the actual job. The job data map will already have been applied as bean property values
   * by execute. The contract is exactly the same as for the standard Quartz execute method.
   *
   * @see #execute
   */
  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    try {
      final CampaignJobData campaignJobData = this.getCampaignJobData(context);
      var executionContext = new ExecutionContext();
      executionContext.put(FlowTreatmentConstants.FLOW_UUID, campaignJobData.getFlowId());
      executionContext.put(FlowTreatmentConstants.CREATED_BY, campaignJobData.getCreatedBy());
      executionContext.put(FlowTreatmentConstants.FLOW_TYPE, campaignJobData.getType());
      executionContext.put(
          FlowTreatmentConstants.COMPOSED_FILE_ID, campaignJobData.getComposedFileId());
      executionContext.put(FlowTreatmentConstants.BEARER_TOKEN, this.getAuthToken());
      executionContext.put(FlowTreatmentConstants.SENDER_EMAIL, campaignJobData.getSenderEmail());
      executionContext.put(FlowTreatmentConstants.SENDER_NAME, campaignJobData.getSenderName());
      executionContext.put(FlowTreatmentConstants.ATTACHMENTS, campaignJobData.getAttachments());
      this.campaignSchedulerLauncher.execute(executionContext);
    } catch (Exception exception) {
      log.error("Failed to launch campaign schedule.", exception);
    }
  }

  /**
   * Called by the <code>{@link Scheduler}</code> when a user interrupts the <code>Job</code>.
   *
   * @throws UnableToInterruptJobException if there is an exception while interrupting the job.
   */
  @Override
  public void interrupt() throws UnableToInterruptJobException {
    log.info("Interrupting");
  }

  private String getAuthToken() {
    return this.keycloakService.getToken();
  }

  private CampaignJobData getCampaignJobData(JobExecutionContext context)
      throws JsonProcessingException {
    var payload =
        context.getMergedJobDataMap().get(ProcessControlConstants.CAMPAIGN_FLOW_JOB_DETAIL);
    if (payload != null) {
      return this.objectMapper.readValue(payload.toString(), CampaignJobData.class);
    }
    return new CampaignJobData();
  }
}
