package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.DateType;
import com.tessi.cxm.pfl.ms32.dto.UserFilterPreferenceDto;
import com.tessi.cxm.pfl.ms32.dto.UserFilterPreferenceResponseDto;
import com.tessi.cxm.pfl.ms32.entity.UserFilterPreference;
import com.tessi.cxm.pfl.ms32.exception.DateTypeNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.FilterPreferenceBadRequestException;
import com.tessi.cxm.pfl.ms32.exception.FilterPreferenceJDBCException;
import com.tessi.cxm.pfl.ms32.repository.FilterPreferenceRepository;
import com.tessi.cxm.pfl.ms32.util.DateHelper;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterPreferenceService {

  private final ModelMapper modelMapper;
  private final FilterPreferenceRepository filterPreferenceRepository;
  private final ProfileFeignClient profileFeignClient;

  /**
   * Create a new record of {@link UserFilterPreference} in DB by user invoke if not existed,
   * otherwise update.
   *
   * @param filterPrefDto - object of {@link UserFilterPreferenceDto}.
   * @param token - object of {@link HttpHeaders}.
   * @return - object of {@link UserFilterPreferenceDto}.
   */
  @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
  public UserFilterPreferenceDto save(UserFilterPreferenceDto filterPrefDto, String token) {
    this.checkValidation(filterPrefDto);
    UserFilterPreference filterPrefEntity =
        this.findFilterPreference(token)
            .orElseGet(() -> this.mappingNewEntity(filterPrefDto, token));

    this.modelMapper.map(filterPrefDto, filterPrefEntity);
    if (!DateType.CUSTOM_RANGES.getKey().equalsIgnoreCase(filterPrefEntity.getSelectDateType())) {
      filterPrefEntity.setCustomStartDate(null);
      filterPrefEntity.setCustomEndDate(null);
    } else {
      filterPrefEntity.setCustomStartDate(filterPrefEntity.getCustomStartDate());
      filterPrefEntity.setCustomEndDate(filterPrefEntity.getCustomEndDate());
    }
    return this.modelMapper.map(this.save(filterPrefEntity), UserFilterPreferenceDto.class);
  }

  /**
   * Check request body of {@link UserFilterPreferenceDto}, It will throw error if it has invalid
   * field.
   *
   * @param filterPrefDTO - object of {@link UserFilterPreferenceDto}.
   */
  private void checkValidation(UserFilterPreferenceDto filterPrefDTO) {
    if (DateType.valueOfKey(filterPrefDTO.getSelectDateType()) == null) {
      throw new DateTypeNotFoundException("selectDateType is not found");
    }

    if (filterPrefDTO.getSelectDateType().equals(DateType.CUSTOM_RANGES.getKey())) {
      if (filterPrefDTO.getCustomStartDate() == null) {
        throw new FilterPreferenceBadRequestException("The \"customStartDate\" field is required");
      }

      if (filterPrefDTO.getCustomEndDate() == null) {
        throw new FilterPreferenceBadRequestException("The \"customEndDate\" field is required");
      }
    }
  }

  /**
   * Get user filter preference by using token of user invoke.
   *
   * @param token - object of {@link String}}
   * @return - object of {@link}
   */
  public UserFilterPreferenceResponseDto getUserFilterCriteria(String token) {
    UserFilterPreference entity =
            this.findFilterPreference(token).orElse(this.getDefaultUserFilterPreference());
    return this.modelMapper.map(entity, UserFilterPreferenceResponseDto.class);
  }

  /**
   * Get user filter preference by using token of user invoke.
   *
   * @param token - object of {@link String}}
   * @return - object of {@link}
   */
  public UserFilterPreferenceDto getFilterPreference(String token) {
    UserFilterPreference entity =
        this.findFilterPreference(token).orElse(this.getDefaultUserFilterPreference());
    return this.modelMapper.map(entity, UserFilterPreferenceDto.class);
  }

  /**
   * Find object of {@link UserFilterPreference} by using token of user invoke.
   *
   * @param token - object of {@link String}
   * @return - object of {@link UserFilterPreference}
   */
  private Optional<UserFilterPreference> findFilterPreference(String token) {
    return this.filterPreferenceRepository.findByOwnerId(this.getUserDetail(token).getOwnerId());
  }

  /**
   * Mapping filterPrefDTO of {@link UserFilterPreferenceDto} to entity of {@link
   * UserFilterPreference}.
   *
   * @param filterPrefDTO - object of {@link UserFilterPreferenceDto}.
   * @param token - object of {@link String}.
   */
  private UserFilterPreference mappingNewEntity(
      UserFilterPreferenceDto filterPrefDTO, String token) {
    final var filterPrefEntity = this.modelMapper.map(filterPrefDTO, UserFilterPreference.class);
    UserDetail userDetail = this.getUserDetail(token);
    filterPrefEntity.setCreatedBy(userDetail.getUsername());
    filterPrefEntity.setOwnerId(userDetail.getOwnerId());
    return filterPrefEntity;
  }

  /**
   * Create or update object of {@link UserFilterPreference}.
   *
   * @param entity - object of {@link UserFilterPreference}.
   * @return object of {@link UserFilterPreference}.
   */
  private UserFilterPreference save(UserFilterPreference entity) {
    try {
      return this.filterPreferenceRepository.save(entity);
    } catch (JDBCException e) {
      throw new FilterPreferenceJDBCException(
          "Fail to create or update \"user filter preference\" ", e);
    }
  }

  public UserFilterPreference getDefaultUserFilterPreference() {
    UserFilterPreference userFilterPreference = new UserFilterPreference();
    userFilterPreference.setSelectDateType(DateType.LAST_7_DAYS.getKey());
    return userFilterPreference;
  }

  /**
   * Get user detail by token of user invoke.
   *
   * @param token - value of {@link String}.
   * @return object of {@link UserDetail}.
   */
  public UserDetail getUserDetail(String token) {
    try {
      return this.profileFeignClient.getUserDetail(token);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new UserAccessDeniedExceptionHandler("Fail to get user detail");
    }
  }

  public List<Map<String, Date>> testGetDateTypeFilter() {
    List<Map<String, Date>> dates = new ArrayList<>();
    dates.add(Map.of("startOfToday", DateHelper.today()));
    dates.add(Map.of("endOfToday", DateHelper.today()));
    dates.add(Map.of("startOfYesterday", DateHelper.yesterday()));
    dates.add(Map.of("endOfYesterday", DateHelper.yesterday()));
    dates.add(Map.of("startOfLast7days", DateHelper.startOfLast7days()));
    dates.add(Map.of("startOfLast30days", DateHelper.startOfLast30days()));
    dates.add(Map.of("firstDayOfThisMonth", DateHelper.firstDayOfThisMonth()));
    dates.add(Map.of("lastDayOfThisMonth", DateHelper.lastDayOfThisMonth()));
    dates.add(Map.of("firstDayOfLastMonth", DateHelper.firstDayOfLastMonth()));
    dates.add(Map.of("lastDayOfLastMonth", DateHelper.lastDayOfLastMonth()));
    dates.add(Map.of("firstDayOfLast3Months", DateHelper.firstDayOfLast3Months()));
    dates.add(Map.of("lastDayOfLast3Months", DateHelper.lastDayOfLast3Months()));
    return dates;
  }
}
