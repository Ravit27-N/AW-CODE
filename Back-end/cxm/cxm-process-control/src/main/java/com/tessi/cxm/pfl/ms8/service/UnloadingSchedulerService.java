package com.tessi.cxm.pfl.ms8.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.core.scheduler.FlowUnloadingJob;
import com.tessi.cxm.pfl.ms8.dto.FlowUnloadingPayload;
import com.tessi.cxm.pfl.ms8.exception.ScheduleFailedException;
import com.tessi.cxm.pfl.ms8.exception.UnloadingDetailNotFoundException;
import com.tessi.cxm.pfl.ms8.repository.UnloadingSchedulerJobRepository;
import com.tessi.cxm.pfl.shared.exception.JsonProcessingExceptionHandler;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.model.SharedUnloadDate;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.DayOfWeek;
import com.tessi.cxm.pfl.shared.utils.UnloadingUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UnloadingSchedulerService {

  private final ProfileFeignClient profileFeignClient;
  private final UnloadingSchedulerJobRepository unloadingSchedulerJobRepository;
  private final Scheduler scheduler;
  private final ObjectMapper objectMapper;

  /**
   * Create new or reschedule the existing Flow unloading scheduler.
   */
  public void scheduleFlowUnloading(SharedClientUnloadDetailsDTO clientUnloadDetails) {
    try {
      JobDetail jobDetail = this.getFlowUnloadingJobDetail();
      List<TriggerKey> clientTriggerKeys = this.getClientTriggerKeys(clientUnloadDetails);

      List<TriggerKey> removedTriggerKeys = this.removeExistingScheduler(
          String.valueOf(clientUnloadDetails.getClientId()),
          clientTriggerKeys);
      log.info("Remove old triggers: {}.", removedTriggerKeys);

      List<TriggerKey> rescheduledTriggerKeys = this.rescheduleExistingFlowUnloading(
          clientUnloadDetails,
          clientTriggerKeys, jobDetail);
      log.info("Rescheduled triggers: {}.", rescheduledTriggerKeys);

      List<TriggerKey> scheduledTriggerKeys = this.scheduleNewFlowUnloading(clientUnloadDetails,
          clientTriggerKeys, jobDetail);
      log.info("New scheduled triggers: {}.", scheduledTriggerKeys);
    } catch (SchedulerException e) {
      throw new ScheduleFailedException(
          "Failed to schedule the Flow unloading for client: " + clientUnloadDetails.getClientId(),
          e);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void unscheduleFlowUnloading(long clientId) {
    String groupKey = String.valueOf(clientId);
    try {
      var existingTriggers = this.scheduler.getTriggerKeys(GroupMatcher.groupEquals(groupKey));
      log.info("Removing the existing cron schedulers of client: {}.", clientId);
      this.scheduler.unscheduleJobs(List.copyOf(existingTriggers));
      log.info("Removing the existing planned unloading Flow for client: {}.", clientId);
      this.unloadingSchedulerJobRepository.deleteAllByClientId(clientId);
    } catch (SchedulerException exception) {
      throw new ScheduleFailedException(
          "Failed to remove the existing unloading Flow and scheduler for client: " + clientId
              + ".",
          exception);
    }
  }

  private final JobKey jobKey = new JobKey("FLOW_UNLOADING_JOB");

  private JobDetail getFlowUnloadingJobDetail()
      throws SchedulerException {

    JobDetail jobDetail = this.scheduler.getJobDetail(jobKey);
    if (jobDetail == null) {
      jobDetail = JobBuilder.newJob(FlowUnloadingJob.class)
          .withIdentity(jobKey)
          .storeDurably()
          .build();
      this.scheduler.addJob(jobDetail, false);
    }

    return jobDetail;
  }

  private List<TriggerKey> removeExistingScheduler(String groupKey,
      List<TriggerKey> clientTriggerKeys)
      throws SchedulerException {
    var existingTriggers = this.scheduler.getTriggerKeys(GroupMatcher.groupEquals(groupKey));

    var removingTriggerKeys = existingTriggers.stream()
        .filter(trigger -> clientTriggerKeys.stream()
            .noneMatch(triggerKey -> triggerKey.getName().equalsIgnoreCase(trigger.getName())))
        .collect(
            Collectors.toList());
    try {
      // Remove only triggers as JobDetails is storing durability
      this.scheduler.unscheduleJobs(removingTriggerKeys);
    } catch (SchedulerException e) {
      log.error("Failed to remove Trigger.");
    }
    return removingTriggerKeys;
  }

  private List<TriggerKey> scheduleNewFlowUnloading(
      SharedClientUnloadDetailsDTO clientUnloadDetails,
      List<TriggerKey> clientTriggerKeys, JobDetail jobDetail) throws SchedulerException {
    var groupKey = String.valueOf(clientUnloadDetails.getClientId());
    var existingTriggers = this.scheduler.getTriggerKeys(GroupMatcher.groupEquals(groupKey));
    List<TriggerKey> newTriggerKeys = clientTriggerKeys.stream().filter(
            triggerKey -> existingTriggers.stream().noneMatch(
                existingTrigger -> existingTrigger.getName().equalsIgnoreCase(triggerKey.getName())))
        .collect(
            Collectors.toList());
    Set<Trigger> newTriggers = this.createTriggers(clientUnloadDetails, newTriggerKeys, jobDetail);

    this.scheduler.scheduleJob(jobDetail, newTriggers, true);

    return newTriggerKeys;
  }

  private List<TriggerKey> rescheduleExistingFlowUnloading(
      SharedClientUnloadDetailsDTO clientUnloadDetails,
      List<TriggerKey> clientTriggerKeys, JobDetail jobDetail) throws SchedulerException {
    var groupKey = String.valueOf(clientUnloadDetails.getClientId());
    var existingTriggers = this.scheduler.getTriggerKeys(GroupMatcher.groupEquals(groupKey));
    List<TriggerKey> oldTriggerKeys = clientTriggerKeys.stream().filter(
            triggerKey -> existingTriggers.stream().anyMatch(
                existingTrigger -> existingTrigger.getName().equalsIgnoreCase(triggerKey.getName())))
        .collect(
            Collectors.toList());
    Set<Trigger> oldTriggers = this.createTriggers(clientUnloadDetails, oldTriggerKeys, jobDetail);

    oldTriggers.forEach(trigger -> {
      try {
        scheduler.rescheduleJob(trigger.getKey(), trigger);
      } catch (SchedulerException exception) {
        throw new ScheduleFailedException(
            "Failed to reschedule the existing unloading Flow and scheduler for client: "
                + clientUnloadDetails.getClientId()
                + ".",
            exception);
      }
    });

    return oldTriggerKeys;
  }

  private List<TriggerKey> getClientTriggerKeys(SharedClientUnloadDetailsDTO clientUnloadDetails) {
    var groupKey = String.valueOf(clientUnloadDetails.getClientId());
    return clientUnloadDetails.getClientUnloads().stream()
        .map(clientUnload -> new TriggerKey(this.getTriggerName(clientUnload), groupKey))
        .collect(Collectors.toList());
  }

  private String getTriggerName(SharedClientUnloadDTO clientUnload) {
    var triggerKeyFormat = "%s-%s-%s";
    var hourString = StringUtils.leftPad(String.valueOf(clientUnload.getHour()), 2, "0");
    var minuteString = StringUtils.leftPad(String.valueOf(clientUnload.getMinute()), 2, "0");
    return String.format(triggerKeyFormat, clientUnload.getDayOfWeek(), hourString,
        minuteString);
  }

  private Set<Trigger> createTriggers(SharedClientUnloadDetailsDTO clientUnloadDetails,
      List<TriggerKey> triggerKeys, JobDetail jobDetail) {
    return triggerKeys.stream().map(triggerKey -> {
      TriggerBuilder<? extends Trigger> triggerBuilder =
          TriggerBuilder.newTrigger()
              .withIdentity(triggerKey)
              .withSchedule(getCronScheduleBuilder(clientUnloadDetails, triggerKey))
              .usingJobData(this.getJobDataMap(clientUnloadDetails, triggerKey))
              .startNow()
              .forJob(jobDetail);

      return triggerBuilder.build();
    }).collect(Collectors.toSet());
  }

  private JobDataMap getJobDataMap(SharedClientUnloadDetailsDTO clientUnloadDetails,
      TriggerKey triggerKey) {
    var jobData = new JobDataMap();
    var clientUnload = this.getClientUnload(clientUnloadDetails, triggerKey);

    var flowUnloadingPayload = FlowUnloadingPayload.builder()
        .clientId(clientUnloadDetails.getClientId())
        .clientUnloads(clientUnload)
        .publicHolidays(clientUnloadDetails.getPublicHolidays())
        .build();
    try {
      var payloadJson = this.objectMapper.writeValueAsString(flowUnloadingPayload);
      jobData.put(ProcessControlConstants.CLIENT_UNLOADING_DETAIL, payloadJson);
    } catch (JsonProcessingException e) {
      throw new JsonProcessingExceptionHandler(e.getMessage());
    }
    return jobData;
  }

  private CronScheduleBuilder getCronScheduleBuilder(
      SharedClientUnloadDetailsDTO clientUnloadDetails,
      TriggerKey triggerKey) {
    var clientUnload = this.getClientUnload(clientUnloadDetails, triggerKey);
    return CronScheduleBuilder.weeklyOnDayAndHourAndMinute(
            DayOfWeek.getValue(clientUnload.getDayOfWeek()),
            clientUnload.getHour(), clientUnload.getMinute())
        .inTimeZone(
            TimeZone.getTimeZone(StringUtils.defaultIfBlank(clientUnload.getZoneId(), "UTC")))
        .withMisfireHandlingInstructionFireAndProceed();
  }

  private SharedClientUnloadDTO getClientUnload(SharedClientUnloadDetailsDTO clientUnloadDetails,
      TriggerKey triggerKey) {
    return clientUnloadDetails.getClientUnloads()
        .stream().filter(
            clientUnloadDTO -> triggerKey.getName()
                .equalsIgnoreCase(this.getTriggerName(clientUnloadDTO)))
        .findFirst().orElseThrow(() -> new UnloadingDetailNotFoundException(
            "Unloading details is not found: " + triggerKey.getName() + "."));
  }

  public SharedUnloadDate getNearestClientUnloadingDate(String token) {
    final SharedClientUnloadDetailsDTO clientUnloads =
        this.profileFeignClient.getClientUnloadDetails(token);

    final SharedClientUnloadDetailDTO clientUnloadDetailDTO =
        UnloadingUtils.findNearestUnloading(clientUnloads, new Date());

    SharedUnloadDate unloadDate = new SharedUnloadDate();
    if (clientUnloadDetailDTO != null && clientUnloadDetailDTO.getNextFiredTime() != null) {
      unloadDate.setUnloadDate(clientUnloadDetailDTO.getNextFiredTime());
    }
    return unloadDate;
  }
}
