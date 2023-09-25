package com.allweb.rms.service;

import com.allweb.rms.entity.dto.ActivityRequest;
import com.allweb.rms.entity.dto.ActivityResponse;
import com.allweb.rms.entity.jpa.Activity;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.exception.ActivityNotFoundException;
import com.allweb.rms.exception.CandidateNotFoundException;
import com.allweb.rms.repository.jpa.ActivityRepository;
import com.allweb.rms.repository.jpa.CandidateRepository;
import com.allweb.rms.security.utils.AuthenticationUtils;
import com.allweb.rms.utils.EntityResponseHandler;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ActivityService {
  private static final String MSG_FORMAT = "Activity id %s not found";
  private final ActivityRepository activityRepository;
  private final ModelMapper modelMapper;
  private final CandidateRepository candidateRepository;
  private final AuthenticationUtils authenticationUtils;

  @Autowired
  public ActivityService(
      ActivityRepository activityRepository,
      ModelMapper modelMapper,
      CandidateRepository candidateRepository,
      AuthenticationUtils authenticationUtils) {
    this.activityRepository = activityRepository;
    this.modelMapper = modelMapper;
    this.candidateRepository = candidateRepository;
    this.authenticationUtils = authenticationUtils;
  }

  /**
   * Method use to get pagination result
   *
   * @param filter is value of string that u want to get it from activity
   * @param size is number of page
   * @param page is number of page index
   * @param sortByField is field of Activity
   * @param sortDirection is direction sort ex(ASC,DESC)
   * @return Activity
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<ActivityResponse> getActivities(
      int page, int size, String filter, String sortDirection, String sortByField) {

    if (sortByField.equals("firstname")) sortByField = "candidate.firstname";
    Pageable pageable =
        PageRequest.of(
            page - 1, size, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    if (Strings.isNullOrEmpty(filter)) {
      return new EntityResponseHandler<>(
          activityRepository
              .getAll(pageable)
              .map(entity -> modelMapper.map(entity, ActivityResponse.class)));
    }
    return new EntityResponseHandler<>(
        activityRepository
            .fetchAllByFilteringField(filter.toLowerCase(), pageable)
            .map(entity -> modelMapper.map(entity, ActivityResponse.class)));
  }

  /**
   * Method use to retrieve Activity info by its id
   *
   * @param id of Activity
   * @return candidate properties
   */
  @Transactional(readOnly = true)
  public ActivityResponse getActivityById(int id) {
    return activityRepository
        .fetchById(id)
        .orElseThrow(() -> new ActivityNotFoundException(String.format(MSG_FORMAT, id)));
  }

  /**
   * Method use add new Activity
   *
   * @param activityRequest of activity
   * @return candidate properties
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public ActivityResponse save(ActivityRequest activityRequest) {
    Candidate candidate =
        candidateRepository
            .findById(activityRequest.getCandidateId())
            .orElseThrow(() -> new CandidateNotFoundException(activityRequest.getCandidateId()));
    return convertToActivityResponse(
        activityRepository.save(
            new Activity(
                activityRequest.getId(),
                candidate,
                authenticationUtils.getAuthenticatedUser().getUserId(),
                activityRequest.getTitle(),
                activityRequest.getDescription())));
  }

  /**
   * Method use update existed Activity
   *
   * @param activityRequest of activity
   * @return candidate properties
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public ActivityResponse update(ActivityRequest activityRequest, int id) {
    Candidate candidate =
        candidateRepository
            .findById(activityRequest.getCandidateId())
            .orElseThrow(() -> new CandidateNotFoundException(activityRequest.getCandidateId()));
    activityRequest.setId(id);
    return convertToActivityResponse(
        activityRepository.save(
            new Activity(
                activityRequest.getId(),
                candidate,
                authenticationUtils.getAuthenticatedUser().getUserId(),
                activityRequest.getTitle(),
                activityRequest.getDescription())));
  }

  /**
   * Method use delete Activity by its id
   *
   * @param id of activity id
   */
  @Transactional
  public void delete(int id) {
    getActivityById(id);
    activityRepository.deleteById(id);
  }

  /**
   * Method use to convert activity entities to ActivityResponse
   *
   * @param activity Object
   * @return ActivityResponse
   */
  public ActivityResponse convertToActivityResponse(Activity activity) {
    return modelMapper.map(activity, ActivityResponse.class);
  }

  /**
   * Method use to convert activityResponse to Activity entities
   *
   * @param activityResponse ActivityObject
   * @return Activity
   */
  public Activity convertToActivityEntities(ActivityResponse activityResponse) {
    return modelMapper.map(activityResponse, Activity.class);
  }

  /**
   * @param size is number of page
   * @param page is number of page index
   * @return page by default @Test method
   */
  @Transactional(readOnly = true)
  public Page<Activity> getPage(int page, int size) {
    return activityRepository.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
  }
}
