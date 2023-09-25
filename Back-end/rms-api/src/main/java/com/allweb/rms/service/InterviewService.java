package com.allweb.rms.service;

import com.allweb.rms.entity.dto.InterviewElasticsearchRequest;
import com.allweb.rms.entity.dto.InterviewRequest;
import com.allweb.rms.entity.dto.InterviewResponse;
import com.allweb.rms.entity.dto.ReminderRequest;
import com.allweb.rms.entity.dto.ReminderResponse;
import com.allweb.rms.entity.elastic.InterviewElasticsearchDocument;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.exception.CandidateNotFoundException;
import com.allweb.rms.exception.InterviewNotFoundException;
import com.allweb.rms.repository.elastic.InterviewElasticsearchRepository;
import com.allweb.rms.repository.jpa.CandidateRepository;
import com.allweb.rms.repository.jpa.InterviewRepository;
import com.allweb.rms.repository.jpa.ReminderRepository;
import com.allweb.rms.security.utils.AuthenticationUtils;
import com.allweb.rms.service.elastic.ElasticIndexingService;
import com.allweb.rms.service.elastic.request.InterviewElasticUpdateRequest;
import com.allweb.rms.service.elastic.request.InterviewHardDeleteElasticRequest;
import com.allweb.rms.service.elastic.request.InterviewInsertElasticRequest;
import com.allweb.rms.service.mail.MailService;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.rms.utils.ReminderType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Setter
@Slf4j
@Getter
public class InterviewService {

  private static final String MSG_FORMAT = "Interview id %s not found";
  private final AuthenticationUtils authenticationUtils;
  private final InterviewRepository interviewRepository;
  private final CandidateRepository candidateRepository;
  private final InterviewStatusService interviewStatusService;
  private final ModelMapper modelMapper;
  private final MailService mailService;
  @Autowired private InterviewElasticsearchRepository interviewElasticsearchRepository;
  @Autowired private ReminderRepository reminderRepository;
  @Autowired private ReminderService reminderService;
  @Autowired private ElasticIndexingService elasticIndexingService;

  @Autowired
  public InterviewService(
      InterviewRepository interviewRepository,
      CandidateRepository candidateRepository,
      AuthenticationUtils authenticationUtils,
      InterviewStatusService interviewStatusService,
      ModelMapper modelMapper,
      MailService mailService) {
    this.interviewRepository = interviewRepository;
    this.candidateRepository = candidateRepository;
    this.authenticationUtils = authenticationUtils;
    this.interviewStatusService = interviewStatusService;
    this.modelMapper = modelMapper;
    this.mailService = mailService;
  }

  /**
   * Method use retrieve interview info by its id
   *
   * @param id of interview
   * @return interview properties
   */
  @Transactional(readOnly = true)
  public InterviewResponse getInterviewById(int id) {
    return interviewRepository
        .getInterviewResponse(id)
        .orElseThrow(() -> new InterviewNotFoundException(String.format(MSG_FORMAT, id)));
  }

  public EntityResponseHandler<InterviewResponse> findAllByElasticsearch(
      InterviewElasticsearchRequest request) {
    Page<InterviewElasticsearchDocument> interviews =
        this.interviewElasticsearchRepository.findAllByElasticsearch(request);
    return new EntityResponseHandler<>(
        interviews.map(
            interviewElasticsearchDocument ->
                this.modelMapper.map(interviewElasticsearchDocument, InterviewResponse.class)));
  }

  /**
   * Method use create interview
   *
   * @param interviewRequest of interview object
   * @return interview properties
   */
  public InterviewResponse saveInterview(InterviewRequest interviewRequest) {
    interviewRequest.setCandidate(
        candidateRepository
            .findById(interviewRequest.getCandidateId())
            .orElseThrow(() -> new CandidateNotFoundException(interviewRequest.getCandidateId())));
    Interview interview = this.saveOrUpdate(interviewRequest);
    if (interviewRequest.isSendInvite()) {
      mailService.setMailInterview(interviewRequest);
    }
    InterviewInsertElasticRequest interviewInsertElasticRequest =
        new InterviewInsertElasticRequest(interview);
    this.elasticIndexingService.execute(interviewInsertElasticRequest);
    if (interviewRequest.isSetReminder()) {
      interviewRequest.setAuthenticatedUser(authenticationUtils.getAuthenticatedUser());
      ReminderResponse reminderResponse =
          reminderService.saveReminder(this.getReminderRequest(interviewRequest, interview));
      this.modelMapper.map(reminderResponse, Reminder.class);
    }
    return convertToInterviewResponse(interview);
  }

  // set reminder object
  private ReminderRequest getReminderRequest(
      InterviewRequest interviewRequest, Interview interview) {
    int reminderBefore = interviewRequest.getReminderTime() * 60;
    return ReminderRequest.builder()
        .reminderType(ReminderType.INTERVIEW.getValue())
        .interviewId(interview.getId())
        .title(interviewRequest.getTitle())
        .dateReminder(interviewRequest.getDateTime())
        .remindBefore(reminderBefore)
        .description(interviewRequest.getDescription())
        .active(true)
        .interview(interview)
        .authenticatedUser(interviewRequest.getAuthenticatedUser())
        .build();
  }

  /**
   * Method use update interview
   *
   * @param id of interview
   * @return Interview object
   */
  @Transactional
  public InterviewResponse updateInterview(InterviewRequest interviewRequest, int id) {
    Optional<Interview> interviewObject = this.interviewRepository.findById(id);
    if (interviewObject.isPresent()) {
      Interview interview = interviewObject.get();
      interviewRequest.setId(interview.getId());
      modelMapper.map(interviewRequest, interview);
      interview.setUserId(this.authenticationUtils.getAuthenticatedUser().getUserId());
      interview.setCandidate(
          candidateRepository
              .findByIdAndDeletedIsFalse(interviewRequest.getCandidateId())
              .orElseThrow(
                  () -> new CandidateNotFoundException(interviewRequest.getCandidateId())));
      interview.setInterviewStatus(
          interviewStatusService.getStatusByIdAndActiveIsTrue(interviewRequest.getStatusId()));
      Interview updatedInterview = interviewRepository.save(interview);
      List<Reminder> interviewReminders =
          this.reminderRepository.findByReminderTypeIdAndInterviewId(
              ReminderType.INTERVIEW.getValue(), updatedInterview.getId());
      interviewReminders.forEach(
          reminder -> this.reminderService.hardDeleteReminderById(reminder.getId()));
      this.reminderService.saveReminder(
          this.getReminderRequest(interviewRequest, updatedInterview));
      elasticIndexingService.execute(new InterviewElasticUpdateRequest(updatedInterview));
      return convertToInterviewResponse(updatedInterview);
    } else {
      throw new InterviewNotFoundException(String.format(MSG_FORMAT, id));
    }
  }

  /**
   * Method use save and update interview
   *
   * @param interviewRequest object
   * @return Interview object
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public Interview save(InterviewRequest interviewRequest) {
    return this.saveOrUpdate(interviewRequest);
  }

  /**
   * Method use update status interview
   *
   * @param id of interview
   * @param statusId of status
   * @return Interview object
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public InterviewResponse updateStatusInterview(int statusId, int id) {
    Interview getInterview =
        interviewRepository
            .findById(id)
            .orElseThrow(() -> new InterviewNotFoundException(String.format(MSG_FORMAT, id)));
    getInterview.setInterviewStatus(interviewStatusService.getStatusByIdAndActiveIsTrue(statusId));
    Interview updatedInterview = interviewRepository.save(getInterview);
    this.elasticIndexingService.execute(new InterviewElasticUpdateRequest(updatedInterview));
    return convertToInterviewResponse(updatedInterview);
  }

  /**
   * Method soft delete interview
   *
   * @param id of interview
   * @param isDeleted boolean
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void deleteSoftInterview(int id, boolean isDeleted) {
    Interview interviewUpdate =
        interviewRepository
            .findById(id)
            .orElseThrow(() -> new InterviewNotFoundException(String.format(MSG_FORMAT, id)));

    interviewUpdate.setDelete(isDeleted);
    Interview updatedInterview = interviewRepository.save(interviewUpdate);
    if (isDeleted) {
      this.unscheduledRemindersByInterviewId(updatedInterview.getId(), true);
    }
    this.elasticIndexingService.execute(new InterviewElasticUpdateRequest(updatedInterview));
  }

  private void unscheduledRemindersByInterviewId(int interviewId, boolean alsoRemoveReminder) {
    List<Reminder> interviewReminders =
        this.reminderRepository.findByReminderTypeIdAndInterviewId(
            ReminderType.INTERVIEW.getValue(), interviewId);
    interviewReminders.forEach(
        reminder -> {
          if (alsoRemoveReminder) {
            reminderRepository.delete(reminder);
          }
          reminderService.unScheduledJob(reminder.getId(), ReminderType.INTERVIEW.getValue());
        });
  }

  /**
   * Method use to delete interview
   *
   * @param id of interview
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void deleteInterview(int id) {
    Interview interview =
        interviewRepository
            .findById(id)
            .orElseThrow(() -> new InterviewNotFoundException(String.format(MSG_FORMAT, id)));
    this.unscheduledRemindersByInterviewId(interview.getId(), false);
    interviewRepository.delete(interview);
    this.elasticIndexingService.execute(new InterviewHardDeleteElasticRequest(interview));
  }

  /**
   * Method use to convert entity to interviewResponse
   *
   * @param interview InterviewObject
   * @return interviewResponse
   */
  public InterviewResponse convertToInterviewResponse(Interview interview) {
    return modelMapper.map(interview, InterviewResponse.class);
  }

  public Interview convertToEntity(InterviewRequest interviewRequest) {
    return modelMapper.map(interviewRequest, Interview.class);
  }

  private Interview saveOrUpdate(InterviewRequest interviewRequest) {
    interviewRequest.setUserId(authenticationUtils.getAuthenticatedUser().getUserId());
    interviewRequest.setInterviewStatus(
        interviewStatusService.getStatusByIdAndActiveIsTrue(interviewRequest.getStatusId()));
    return interviewRepository.save(convertToEntity(interviewRequest));
  }
}
