package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.TemplateVariable;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignAttachment;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignDepositFlowLaunchRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.core.scheduler.CampaignJobScheduler;
import com.tessi.cxm.pfl.ms8.dto.CampaignJobData;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.exception.JsonProcessingExceptionHandler;
import com.tessi.cxm.pfl.shared.scheduler.SchedulerHandler;
import com.tessi.cxm.pfl.shared.scheduler.model.DateTimeInfo;
import com.tessi.cxm.pfl.shared.scheduler.model.JobDetailDescriptor;
import com.tessi.cxm.pfl.shared.scheduler.model.JobTriggerDescriptor;
import com.tessi.cxm.pfl.shared.scheduler.util.JobData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CampaignScheduleHandler extends AbstractExecutionHandler {

  private final SchedulerHandler schedulerHandler;
  private final ObjectMapper objectMapper;

  public CampaignScheduleHandler(SchedulerHandler schedulerHandler, ObjectMapper objectMapper) {
    this.schedulerHandler = schedulerHandler;
    this.objectMapper = objectMapper;
  }

  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final boolean isScheduled = context.get(FlowTreatmentConstants.IS_SET_SCHEDULE, Boolean.class);
    if (!isScheduled) {
      return ExecutionState.NEXT;
    }

    final CampaignDepositFlowLaunchRequest request =
        context.get(
            FlowTreatmentConstants.CAMPAIGN_DEPOSIT_FLOW_LAUNCH_REQUEST,
            CampaignDepositFlowLaunchRequest.class);
    final String composedFileId =
        context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class);
    final Map<String, String> attachments =
        context.get(FlowTreatmentConstants.ATTACHMENTS, Map.class);
    request.setComposedFileId(composedFileId);
    final String groupId = "CAMPAIGN_".concat(request.getType().toUpperCase());
    final String flowId = request.getFlowId();
    final Date dateSchedule = request.getDateSchedule();
    final JobData jobData = jobData(request, attachments);
    final JobDetailDescriptor jobDetail = createJobDetail(flowId, groupId);
    JobTriggerDescriptor jobTrigger = createTrigger(flowId, groupId, jobData);
    DateTimeInfo triggerOn =
        new DateTimeInfo(dateSchedule, SchedulerHandler.SCHEDULER_ZONE.getId());
    final Date dateToSchedule = jobTrigger.triggerOn(triggerOn);
    setSchedule(jobDetail, jobTrigger);
    log.info("Set scheduler for {}-{}", jobDetail.getJobKey().getName(), dateToSchedule);
    return ExecutionState.END;
  }

  private void setSchedule(JobDetailDescriptor jobDetail, JobTriggerDescriptor jobTrigger) {
    try {
      if (this.schedulerHandler.exists(jobDetail)) {
        jobTrigger.forJob(jobDetail);
        this.schedulerHandler.scheduleJob(jobTrigger);
      } else {
        this.schedulerHandler.scheduleJob(jobTrigger, jobDetail);
      }
    } catch (SchedulerException e) {
      throw new ExecutionException("Unable to set Schedule", e);
    }
  }

  private JobTriggerDescriptor createTrigger(String id, String groupId, JobData jobData) {
    JobTriggerDescriptor jobTriggerDescriptor = new JobTriggerDescriptor(id, groupId);
    jobTriggerDescriptor.setData(jobData);
    return jobTriggerDescriptor;
  }

  private JobDetailDescriptor createJobDetail(String id, String groupId) {
    JobDetailDescriptor jobDetailDescriptor =
        new JobDetailDescriptor(id, groupId, CampaignJobScheduler.class);
    jobDetailDescriptor.setStoreDurably(true);
    return jobDetailDescriptor;
  }

  private JobData jobData(
      CampaignDepositFlowLaunchRequest request, Map<String, String> attachments) {
    CampaignJobData campaignJobData =
        CampaignJobData.builder()
            .flowId(request.getFlowId())
            .composedFileId(request.getComposedFileId())
            .createdBy(request.getCreatedBy())
            .type(request.getType())
            .build();
    if (TemplateVariable.EMAIL.equalsIgnoreCase(request.getType())) {
      campaignJobData.setSenderEmail(request.getSenderMail());
      campaignJobData.setSenderName(request.getSenderName());
      campaignJobData.setAttachments(attachments);
    }
    return () -> {
      try {
        return new JobDataMap(
            Map.of(
                ProcessControlConstants.CAMPAIGN_FLOW_JOB_DETAIL,
                this.objectMapper.writeValueAsString(campaignJobData)));
      } catch (JsonProcessingException e) {
        throw new JsonProcessingExceptionHandler(e.getMessage());
      }
    };
  }
}
