package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.CorporateUserDto;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import com.innovationandtrust.profile.model.dto.UserEmployee;
import com.innovationandtrust.profile.model.projection.DataMigrationResult;
import com.innovationandtrust.profile.service.restclient.BusinessUnitsFeignClient;
import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.corporateprofile.BusinessUnitDTO;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataMigrationService {
  private final CompanyService companyService;
  private final CorporateUserService corporateUserService;
  private final NormalUserService normalUserService;
  private final CompanySettingService companySettingService;

  private final BusinessUnitsFeignClient businessUnitsFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;

  private static final String BUSINESS_UNIT_ID = "businessUnitId";
  private static final String USER_ACCESS_ID = "userAccessId";

  /**
   * Migration data by updating them to fill the missing data.
   *
   * @return result of migration.
   */
  public Map<String, DataMigrationResult> migrateOldData() {
    // list and update all company and corporate admin of each company
    var companyResults = this.updateCompany();

    // list and update all corporate admin of each company
    var corporateResults = this.updateCorporate();

    // list and update all end-users
    var userResults = this.updateAllEndUser();

    Map<String, DataMigrationResult> result = new HashMap<>();
    result.put("company", companyResults);
    result.put("corporate-admin", corporateResults);
    result.put("end-user", userResults);
    return result;
  }

  private DataMigrationResult updateCompany() {
    var companyResults = new DataMigrationResult();
    var companyList = this.companyService.findAll();
    companyResults.setTotal(companyList.size());
    companyList.forEach(
        company -> {
          try {
            var updated = this.companyService.update(company);
            var companySettings =
                this.corporateProfileFeignClient.findCompanySettings(updated.getUuid());
            if (companySettings.isEmpty()) {
              CompanySettingDto settingDto =
                  CompanySettingDto.builder()
                      .companyUuid(updated.getUuid())
                      .signatureLevel(SignatureSettingLevel.SIMPLE.name())
                      .fileType(Collections.emptySet())
                      .channelReminder(NotificationConstant.EMAIL)
                      .build();
              this.companySettingService.save(List.of(settingDto));
            }
          } catch (Exception e) {
            this.setErrorResult(
                companyResults,
                String.format(
                    "Company name `%s` could not update: %s", company.getName(), e.getMessage()));
          }
        });
    return companyResults;
  }

  private DataMigrationResult updateCorporate() {
    var corporateResults = new DataMigrationResult();
    var corporateList = this.corporateUserService.findAll();
    this.updateUser(corporateList, corporateResults);
    return corporateResults;
  }

  private DataMigrationResult updateAllEndUser() {
    var userResult = new DataMigrationResult();
    var users = this.normalUserService.findAll();
    this.updateUser(users, userResult);
    return userResult;
  }

  private <T extends UserEmployee> void updateUser(List<T> users, DataMigrationResult userResult) {
    userResult.setTotal(users.size());
    users.forEach(
        user -> {
          try {
            var ids = this.getBusinessUnitId(user, !(user instanceof NormalUserDto));
            if (Objects.nonNull(ids.get(BUSINESS_UNIT_ID))) {
              user.setBusinessId(ids.get(BUSINESS_UNIT_ID));
              user.setUserAccessId(ids.get(USER_ACCESS_ID));
              if (user instanceof NormalUserDto normalUserDto) {
                this.normalUserService.update(normalUserDto);
              } else {
                this.corporateUserService.update((CorporateUserDto) user);
              }
            } else {
              this.setErrorResult(
                  userResult,
                  String.format("Account `%s` could not find business unit", user.getEmail()));
            }
          } catch (Exception e) {
            log.error("Update user " + user.getEmail() + ", Error updating user: ", e);
            this.setErrorResult(
                userResult, String.format("Account `%s`, %s", user.getEmail(), e.getMessage()));
          }
        });
  }

  private void setErrorResult(DataMigrationResult errorResult, String msg) {
    errorResult.setFails(errorResult.getFails() + 1);
    errorResult.getErrors().add(msg);
  }

  private <T extends UserEmployee> Map<String, Long> getBusinessUnitId(
      T user, boolean isCorporateUser) {
    Map<String, Long> ids = new HashMap<>();
    long companyId = Objects.nonNull(user.getCompany()) ? user.getCompany().getId() : 0;
    Long businessUnitId =
        Objects.nonNull(user.getBusinessUnit()) ? user.getBusinessUnit().getId() : null;
    Long userAccessId = Objects.nonNull(user.getUserAccess()) ? user.getUserAccess().getId() : null;
    if (Objects.isNull(businessUnitId)) {
      // if employee not exists get company businessUnits
      if (!isCorporateUser && (companyId == 0)) {
        var createdByUser = this.corporateUserService.findById(user.getCreatedBy());
        companyId = createdByUser.getCompanyId();
      }
      if (companyId != 0) {
        // should store businessUnit in a variable
        var businessUnits = this.businessUnitsFeignClient.findByCompanyId(companyId, "asc");
        if (!businessUnits.getContents().isEmpty()) {
          businessUnitId = businessUnits.getContents().get(0).getId();
        } else {
          businessUnitId = this.createUnit(companyId);
        }
      }
    }
    ids.put(BUSINESS_UNIT_ID, businessUnitId);
    ids.put(USER_ACCESS_ID, Objects.nonNull(userAccessId) ? userAccessId : 1L);
    return ids;
  }

  private Long createUnit(long companyId) {
    try {
      // if company have no businessUnit, create new one
      var businessUnitDto = new BusinessUnitDTO();
      businessUnitDto.setUnitName("HR");
      businessUnitDto.setSortOrder(1);
      businessUnitDto.setCompanyId(companyId);
      var newBusinessUnit = this.businessUnitsFeignClient.save(businessUnitDto);
      return newBusinessUnit.getId();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format(
              "Fails to create new businessUnit for company id `%s`, %s",
              companyId, e.getMessage()));
    }
  }
}
