package com.allweb.rms.service;

import com.allweb.rms.component.ReminderModelAssembler;
import com.allweb.rms.core.scheduler.CompositeProxyJobData;
import com.allweb.rms.core.scheduler.JobData;
import com.allweb.rms.core.scheduler.ReminderConstants;
import com.allweb.rms.core.scheduler.job.ReminderBatchJobLauncher;
import com.allweb.rms.core.scheduler.model.DateTimeInfo;
import com.allweb.rms.core.scheduler.model.JobDetailDescriptor;
import com.allweb.rms.core.scheduler.model.JobTriggerDescriptor;
import com.allweb.rms.core.scheduler.support.ReminderJobData;
import com.allweb.rms.core.scheduler.support.SpringBatchJobData;
import com.allweb.rms.entity.dto.*;
import com.allweb.rms.entity.elastic.ReminderElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.exception.CandidateNotFoundException;
import com.allweb.rms.exception.InterviewNotFoundException;
import com.allweb.rms.exception.ReminderNotFoundException;
import com.allweb.rms.exception.ReminderTypeNotFoundException;
import com.allweb.rms.repository.elastic.ReminderElasticsearchRepository;
import com.allweb.rms.repository.jpa.CandidateRepository;
import com.allweb.rms.repository.jpa.InterviewRepository;
import com.allweb.rms.repository.jpa.ReminderRepository;
import com.allweb.rms.repository.jpa.ReminderTypeRepository;
import com.allweb.rms.security.AuthenticatedUser;
import com.allweb.rms.security.utils.AuthenticationUtils;
import com.allweb.rms.service.elastic.ElasticIndexingService;
import com.allweb.rms.service.elastic.request.ReminderHardDeleteElasticRequest;
import com.allweb.rms.service.elastic.request.ReminderInsertElasticRequest;
import com.allweb.rms.service.elastic.request.ReminderUpdateElasticRequest;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.rms.utils.ReminderType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class ReminderService {
  static final String REMINDER_ID_SUFFIX = "-BEFORE";
  private final ReminderRepository reminderRepository;
  private final ReminderTypeRepository reminderTypeRepository;
  private final CandidateRepository candidateRepository;
  private final InterviewRepository interviewRepository;
  @Autowired private ReminderModelAssembler assembler;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ReminderElasticsearchRepository reminderElasticsearchRepository;
  @Autowired private ElasticIndexingService elasticIndexingService;
  @Autowired private Executor simpleThreadPoolTaskExecutor;

  @Autowired private AuthenticationUtils utils;
  @Autowired private ModelMapper modelMapper;
  @Autowired private SchedulerService schedulerService;

  // This array will hold the id that user will delete
  private ArrayList<Integer> historyOfDelete = new ArrayList<>();

  public ReminderService(
      ReminderRepository reminderRepository,
      ReminderTypeRepository reminderTypeRepository,
      CandidateRepository candidateRepository,
      InterviewRepository interviewRepository) {
    this.reminderRepository = reminderRepository;
    this.reminderTypeRepository = reminderTypeRepository;
    this.candidateRepository = candidateRepository;
    this.interviewRepository = interviewRepository;
  }

  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  public EntityResponseHandler<ReminderResponseList> getReminders(
      ReminderAdvanceFilterRequest request, Pageable pageable) {
    Page<ReminderElasticsearchDocument> reminderDocuments =
        this.reminderElasticsearchRepository.elasticSearchAdvanceFilters(request, pageable);
    return new EntityResponseHandler<>(
            reminderDocuments.map(
                    reminderDocument -> modelMapper.map(reminderDocument,ReminderResponseList.class)
            )
    );
//            demand.map(
//                    demandElasticsearchDocument ->
//                            modelMapper.map(demandElasticsearchDocument, DemandDTO_List.class)));
  }


  /** Create a new {@link Reminder}. */
  @Transactional(rollbackFor = Exception.class)
  public ReminderResponse saveReminder(ReminderRequest reminderRequest) {
    Reminder reminder = modelMapper.map(reminderRequest, Reminder.class);
    final AuthenticatedUser authenticatedUser =
        reminderRequest.getAuthenticatedUser() != null
            ? reminderRequest.getAuthenticatedUser()
            : this.utils.getAuthenticatedUser();
    reminder.setUserId(authenticatedUser.getUserId());
    // get reminder type by id
    Optional<com.allweb.rms.entity.jpa.ReminderType> reminderTypeEntity =
        this.reminderTypeRepository.findById(reminderRequest.getReminderType());
    if (reminderTypeEntity.isPresent()) {
      reminder.setReminderType(reminderTypeEntity.get());
      ReminderType reminderType = ReminderType.valueOf(reminderTypeEntity.get().getId());
      if (reminderType == ReminderType.INTERVIEW) {
        Interview interview = reminderRequest.getInterview();
        if (interview == null) {
          interview =
              this.interviewRepository
                  .findById(reminderRequest.getInterviewId())
                  .orElseThrow(
                      () -> new InterviewNotFoundException(reminderRequest.getInterviewId()));
        }
        reminder.setInterview(interview);
      } else if (reminderType == ReminderType.SPECIAL) {
        reminder.setCandidate(
            this.candidateRepository
                .findById(reminderRequest.getCandidateId())
                .orElseThrow(
                    () -> new CandidateNotFoundException(reminderRequest.getCandidateId())));
      }
    }
    reminder = reminderRepository.save(reminder);
    ReminderResponse savedReminder = modelMapper.map(reminder, ReminderResponse.class);
    int reminderId = reminder.getId();
    simpleThreadPoolTaskExecutor.execute(
        () -> {
          try {
            scheduleReminderJob(String.valueOf(reminderId), reminderRequest, authenticatedUser);
          } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
          }
        });

    ReminderElasticsearchDocument reminderElastic = this.modelMapper.map(reminder,ReminderElasticsearchDocument.class);
    reminderElastic.setReminderType(reminder.getReminderType().getId());
    this.addMapFullNameCD(reminder,reminderRequest,reminderElastic);
    this.reminderElasticsearchRepository.save(reminderElastic);
    return savedReminder;
  }
  public void addMapFullNameCD(Reminder reminder,ReminderRequest reminderRequest , ReminderElasticsearchDocument reminderElastic){
    if(reminder.getInterview() != null){
      Interview interview =
              this.interviewRepository
                      .findById(reminderRequest.getInterviewId())
                      .orElseThrow(
                              () -> new InterviewNotFoundException(reminderRequest.getInterviewId()));
      reminderElastic.getInterview().setFullName(interview.getCandidate().getFullName());
      reminderElastic.getInterview().setCid(interview.getCandidate().getId());
    }
  }
  public void addMapFullNameCD(Reminder reminder , ReminderElasticsearchDocument reminderElastic){
    if(reminder.getInterview() != null){
      Interview interview =
              this.interviewRepository
                      .findById(reminder.getInterview().getId())
                      .orElseThrow(
                              () -> new InterviewNotFoundException(reminder.getInterview().getId()));
      reminderElastic.getInterview().setFullName(interview.getCandidate().getFullName());
      reminderElastic.getInterview().setCid(interview.getCandidate().getId());
    }
  }


  /**
   * Update reminder
   *
   * @param reminderId Reminder's id.
   * @param reminderRequest Reminder's detail.
   * @return {@link ReminderRequest}
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public ReminderResponse updateReminder(int reminderId, ReminderRequest reminderRequest) {
    Reminder reminder =
        reminderRepository
            .findByIdAndDeletedIsFalse(reminderId)
            .orElseThrow(() -> new ReminderNotFoundException(reminderId));
    modelMapper.map(reminderRequest, reminder);
    if (reminderRequest.getReminderType() != null) {
      this.updateReminderType(reminder, reminderRequest);
    }
    Reminder updatedReminder = reminderRepository.save(reminder);
    boolean active = updatedReminder.isActive();
    final String reminderType = updatedReminder.getReminderType().getId();
    final AuthenticatedUser authenticatedUser = this.utils.getAuthenticatedUser();
    simpleThreadPoolTaskExecutor.execute(
        () -> {
          TriggerKey triggerKey = new TriggerKey(String.valueOf(reminderId), reminderType);
          try {
            if (schedulerService.exists(triggerKey)) {
              schedulerService.unScheduledJob(triggerKey);
              log.info(String.format("Scheduler job with Reminder id %d is deleted.", reminderId));
              scheduleReminderJob(String.valueOf(reminderId), reminderRequest, authenticatedUser);
              log.info(String.format("Scheduler job with Reminder id %d is created.", reminderId));
            } else {
              log.info(String.format("Scheduler job with Reminder id %d is deleted.", reminderId));
              scheduleReminderJob(String.valueOf(reminderId), reminderRequest, authenticatedUser);
            }
            if (!active) {
              schedulerService.pauseJob(triggerKey);
              log.info(String.format("Scheduler job with Reminder id %d is paused.", reminderId));
            }
          } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
          }
        });
    modelMapper.map(updatedReminder, reminderRequest);
    ReminderElasticsearchDocument reminderElastic = modelMapper.map(updatedReminder,ReminderElasticsearchDocument.class);
   this.addMapFullNameCD(updatedReminder,reminderRequest,reminderElastic);
    reminderElastic.setReminderType(reminder.getReminderType().getId());
    reminderElasticsearchRepository.save(reminderElastic);
    return modelMapper.map(updatedReminder, ReminderResponse.class);
  }

  private void updateReminderType(Reminder reminder, ReminderRequest reminderRequest) {
    Optional<com.allweb.rms.entity.jpa.ReminderType> reminderTypeEntity =
        this.reminderTypeRepository.findById(reminderRequest.getReminderType());
    if (reminderTypeEntity.isPresent()) {
      reminder.setReminderType(reminderTypeEntity.get());
      if (reminderRequest.getReminderType().equals(ReminderType.SPECIAL.getValue())
          && reminderRequest.getCandidateId() > 0) {
        Optional<Candidate> candidateObject =
            this.candidateRepository.findById(reminderRequest.getCandidateId());
        if (candidateObject.isEmpty()) {
          throw new CandidateNotFoundException(reminderRequest.getCandidateId());
        }
        reminder.setCandidate(candidateObject.get());
        reminder.setInterview(null);
      } else if (reminderRequest.getReminderType().equals(ReminderType.INTERVIEW.getValue())
          && reminderRequest.getInterviewId() > 0) {
        Optional<Interview> interviewObject =
            this.interviewRepository.findById(reminderRequest.getInterviewId());
        if (interviewObject.isEmpty()) {
          throw new InterviewNotFoundException(reminderRequest.getInterviewId());
        }
        reminder.setInterview(interviewObject.get());
        reminder.setCandidate(null);
      } else { // Normal ReminderType
        reminder.setCandidate(null);
        reminder.setInterview(null);
      }
    } else {
      throw new ReminderTypeNotFoundException(reminderRequest.getReminderType());
    }
  }

  /**
   * @param reminderId Reminder's id.
   * @param active Reminder's active.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateReminderStatus(int reminderId, boolean active) {
    Reminder reminder =
        reminderRepository
            .findByIdAndDeletedIsFalse(reminderId)
            .orElseThrow(() -> new ReminderNotFoundException(reminderId));
    ReminderElasticsearchDocument reminderElastic = this.reminderElasticsearchRepository.findById(reminderId).orElseThrow(() -> new ReminderNotFoundException(reminderId));
    reminder.setActive(active);
    reminder = reminderRepository.save(reminder);
    final String reminderType = reminder.getReminderType().getId();
    simpleThreadPoolTaskExecutor.execute(
        () -> {
          TriggerKey triggerKey = new TriggerKey(String.valueOf(reminderId), reminderType);
          try {
            if (!schedulerService.exists(triggerKey)) {
              log.info(String.format("No scheduler job with Reminder id %d.", reminderId));
              return;
            }
            if (active) {
              schedulerService.resumeJob(triggerKey);
              log.info(String.format("Scheduler job with Reminder id %d is resumed.", reminderId));
            } else {
              schedulerService.pauseJob(triggerKey);
              log.info(String.format("Scheduler job with Reminder id %d is paused.", reminderId));
            }
          } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
          }
        });
   reminderElastic.setActive(active);
    reminderElasticsearchRepository.save(reminderElastic);
    modelMapper.map(reminder, ReminderResponse.class);
  }

  /**
   * Get reminder info
   *
   * @param id Reminder's id
   * @return {@link Reminder}
   */
  @Transactional(readOnly = true)
  public ReminderResponse getReminderById(int id) {
    Reminder reminder =
     reminderRepository
        .findByIdAndDeletedIsFalse(id)
        .orElseThrow(() -> new ReminderNotFoundException(id));
    return modelMapper.map(reminder,ReminderResponse.class);
  }

  // this method will recover deleted reminder
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public ResponseEntity<?> recoverDeleteReminder() {
    if (historyOfDelete.size() > 0) {
      Reminder reminder =
          reminderRepository
              .findById(historyOfDelete.get(historyOfDelete.size() - 1))
              .orElseThrow(() -> new ReminderNotFoundException(historyOfDelete.size() - 1));
      ReminderElasticsearchDocument reminderElastic = this.reminderElasticsearchRepository.findById(reminder.getId()).orElseThrow(() -> new ReminderNotFoundException(reminder.getId()));
      reminder.setDeleted(false);
      reminder.setActive(true);
      reminderRepository.save(reminder);
      reminderElastic.setDeleted(false);
      reminderElastic.setActive(true);
      reminderElasticsearchRepository.save(reminderElastic);
      final String reminderType = reminder.getReminderType().getId();
      this.unScheduledJob(historyOfDelete.size() - 1, reminderType);
      historyOfDelete.remove(historyOfDelete.size() - 1);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.ok().build();
    }
  }

  /**
   * Soft delete Reminder by id
   *
   * @param reminderId Reminder's id for soft delete.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void softDeleteReminderById(int reminderId) {
    Reminder reminder =
        reminderRepository
            .findByIdAndDeletedIsFalse(reminderId)
            .orElseThrow(() -> new ReminderNotFoundException(reminderId));
    reminder.setDeleted(true);
    reminder.setActive(false);
    reminderRepository.save(reminder);
    historyOfDelete.add(reminderId);
    this.elasticIndexingService.execute(new ReminderUpdateElasticRequest(reminder));
    final String reminderType = reminder.getReminderType().getId();
    this.unScheduledJob(reminderId, reminderType);
  }

  /**
   * Hard delete Reminder by id. It cannot be undone.
   *
   * @param reminderId Reminder's id for hard delete.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void hardDeleteReminderById(int reminderId) {
    Reminder reminder =
        reminderRepository
            .findByIdAndDeletedIsFalse(reminderId)
            .orElseThrow(() -> new ReminderNotFoundException(reminderId));
    reminderRepository.delete(reminder);
    this.elasticIndexingService.execute(new ReminderHardDeleteElasticRequest(reminder));
    final String reminderType = reminder.getReminderType().getId();
    this.unScheduledJob(reminderId, reminderType);
  }

  public void unScheduledJob(int reminderId, String reminderType) {
    simpleThreadPoolTaskExecutor.execute(
        () -> {
          TriggerKey triggerKey = new TriggerKey(String.valueOf(reminderId), reminderType);
          try {
            this.schedulerService.unScheduledJob(triggerKey);
          } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
          }
        });
  }

  // Schedule
  private void scheduleReminderJob(
      String reminderId, ReminderRequest reminderDto, AuthenticatedUser authenticatedUser)
      throws SchedulerException {
    JobDetailDescriptor jobDetail = createJobDetailDescriptor(reminderDto);
    List<JobTriggerDescriptor> jobTriggers =
        createJobTriggerDescriptors(reminderId, reminderDto, authenticatedUser);
    for (JobTriggerDescriptor jobTrigger : jobTriggers) {
      if (this.schedulerService.exists(jobDetail)) {
        jobTrigger.forJob(jobDetail);
        this.schedulerService.scheduleJob(jobTrigger);
      } else {
        this.schedulerService.scheduleJob(jobTrigger, jobDetail);
      }
    }
  }

  private JobDetailDescriptor createJobDetailDescriptor(ReminderRequest reminderDto) {
    JobDetailDescriptor jobDetailDescriptor =
        new JobDetailDescriptor(
            reminderDto.getReminderType(),
            reminderDto.getReminderType(),
            ReminderBatchJobLauncher.class);
    jobDetailDescriptor.setStoreDurably(true);
    return jobDetailDescriptor;
  }

  private List<JobTriggerDescriptor> createJobTriggerDescriptors(
      String reminderId, ReminderRequest reminderDto, AuthenticatedUser authenticatedUser) {
    List<JobTriggerDescriptor> jobTriggers = new ArrayList<>();

    if (reminderDto.getRemindBefore() > 0) {
      JobTriggerDescriptor jobTrigger1 =
          this.createJobTrigger(
              reminderId,
              reminderId + REMINDER_ID_SUFFIX,
              reminderDto.getReminderType(),
              false,
              authenticatedUser);
      DateTimeInfo triggerOnDateTime = new DateTimeInfo(reminderDto.getDateReminder());
      triggerOnDateTime.minus(Duration.ofSeconds(reminderDto.getRemindBefore()));
      jobTrigger1.triggerOn(triggerOnDateTime);
      jobTriggers.add(jobTrigger1);
    }
    JobTriggerDescriptor jobTrigger2 =
        this.createJobTrigger(
            reminderId, reminderId, reminderDto.getReminderType(), true, authenticatedUser);
    jobTrigger2.triggerOn(new DateTimeInfo(reminderDto.getDateReminder()));
    jobTriggers.add(jobTrigger2);
    return jobTriggers;
  }

  private JobTriggerDescriptor createJobTrigger(
      String reminderId,
      String jobName,
      String groupName,
      boolean allowReport,
      AuthenticatedUser authenticatedUser) {
    JobTriggerDescriptor jobTriggerDescriptor = new JobTriggerDescriptor(jobName, groupName);
    JobData reminderJobData = createReminderJobData(reminderId, allowReport, authenticatedUser);
    jobTriggerDescriptor.setData(reminderJobData);
    return jobTriggerDescriptor;
  }

  private JobData createReminderJobData(
      String reminderId, boolean allowReport, AuthenticatedUser authenticatedUser) {
    ReminderJobData reminderJobData = new ReminderJobData();
    reminderJobData.setReminderId(reminderId);
    reminderJobData.setUserEmail(authenticatedUser.getEmail());
    SpringBatchJobData batchJobData = new SpringBatchJobData();
    batchJobData.setJobName(ReminderConstants.BATCH_REMINDER);
    batchJobData.allowReportSendingState(allowReport);
    CompositeProxyJobData compositeJobData = new CompositeProxyJobData();

    compositeJobData.addJobData("reminderJobData", reminderJobData);
    compositeJobData.addJobData("batchJobData", batchJobData);
    return compositeJobData;
  }


}
