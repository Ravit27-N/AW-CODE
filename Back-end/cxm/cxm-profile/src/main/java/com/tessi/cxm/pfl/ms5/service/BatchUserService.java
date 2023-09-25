package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.constant.DataReaderValidationType;
import com.tessi.cxm.pfl.ms5.constant.UserManagementConstants.User;
import com.tessi.cxm.pfl.ms5.core.flow.UserCreationManager;
import com.tessi.cxm.pfl.ms5.dto.BatchUserResponseDto;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganizationUserImpl;
import com.tessi.cxm.pfl.ms5.dto.UserOrganization;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.implementation.CSVBatchUserResource;
import com.tessi.cxm.pfl.ms5.service.specification.ClientSpecification;
import com.tessi.cxm.pfl.ms5.util.BatchUserOrganization;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BatchUserService implements AdminService {

  @Value("${cxm.oauth2.group}")
  private String defaultGroup;

  @Value("${cxm.user.admin-id}")
  private String adminUserId;

  private final UserCreationManager userCreationManager;
  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;
  private final ProfileService profileService;
  private final ProfileRepository profileRepository;

  public BatchUserResponseDto create(MultipartFile multipartFile) {
    ExecutionContext executionContext = new ExecutionContext();

    log.info("Start processing the imported batch of users");
    if (!isAdmin()) {

      boolean notContainsUserCreatedPrivilege =
          this.profileService.notContainsPrivilege(
              ProfileConstants.CXM_USER_MANAGEMENT,
              ProfileConstants.CXM_USER_MANAGEMENT.concat("_".concat(User.CREATE)));

      if (notContainsUserCreatedPrivilege) {
        throw new UserAccessDeniedExceptionHandler();
      }

      UserPrivilegeDetails privilegeDetails =
          profileService.getUserPrivilegeDetails(
              false,
              ProfileConstants.CXM_USER_MANAGEMENT,
              ProfileConstants.CXM_USER_MANAGEMENT.concat("_").concat(User.MODIFY),
              false);

      var organization =
          this.userRepository
              .loadOrganizationUser(AuthenticationUtils.getPrincipalIdentifier())
              .orElse(new LoadOrganizationUserImpl());

      var userOrgByLevel = this.getOrgByLevel(privilegeDetails.getLevel(), organization);
      var userOrganization = new BatchUserOrganization(userOrgByLevel);
      List<Profile> userOrgProfiles = this.getProfileOrgByLevel(organization.getClientId());
      executionContext.put(DataReaderConstant.KEY_USER_ORGANIZATION_BY_LEVEL, userOrganization);
      executionContext.put(DataReaderConstant.KEY_USER_ORGANIZATION_PROFILES, userOrgProfiles);
    }

    try {
      BatchUserResource resource = new CSVBatchUserResource(multipartFile);
      BatchUserImport userImport = new BatchUserImport(userCreationManager, resource);
      putSuperAdminProperty(executionContext);
      putUserAccessType(executionContext);
      putKeycloak(executionContext);
      BatchUserResponseDto process = userImport.process(executionContext);
      if (process.getErrorCount() > 0) {
        log.info("The user batch import completed successfully with error {}", process.getErrorCount());
      } else {
        log.info("The user batch import completed successfully");
      }
      return process;
    } catch (IOException ioe) {
      log.error("The user batch import was completed unsuccessfully");
      throw new FileErrorException("Fail to import batch users", ioe);
    }
  }

  public List<UserOrganization> getOrgByLevel(String level, LoadOrganization loadOrganization) {
    var specification = ClientSpecification.getOrganizationInfo(level, loadOrganization);

    return this.departmentRepository.findAll(specification, Department.class, Tuple.class).stream()
        .map(UserOrganization::new)
        .collect(Collectors.toList());
  }

  public List<Profile> getProfileOrgByLevel(Long clientId) {
    return this.profileRepository.findByClientId(clientId);
  }

  private void putSuperAdminProperty(ExecutionContext executionContext) {
    String superAdminUserName = AuthenticationUtils.getPrincipal();
    String superAdminId = AuthenticationUtils.getPrincipalIdentifier();
    executionContext.put(DataReaderConstant.SUPER_ADMIN_USER_NAME, superAdminUserName);
    executionContext.put(DataReaderConstant.SUPER_ADMIN_ID, superAdminId);
  }

  private void putUserAccessType(ExecutionContext executionContext) {
    String userAccessLevel = isAdmin() ? DataReaderValidationType.ADMIN.getValue()
        : DataReaderValidationType.CLIENT_ADMIN.getValue();
    executionContext.put(DataReaderConstant.USER_ACCESS_LEVEL, userAccessLevel);
  }

  private void putKeycloak(ExecutionContext executionContext) {
    executionContext.put(DataReaderConstant.KEY_CLOAK_DEFAULT_USE_GROUP, this.defaultGroup);
  }

  @Override
  public String getConfiguredUserAdminId() {
    return this.adminUserId;
  }

  @Override
  public UserRepository getUserRepository() {
    return this.userRepository;
  }
}
