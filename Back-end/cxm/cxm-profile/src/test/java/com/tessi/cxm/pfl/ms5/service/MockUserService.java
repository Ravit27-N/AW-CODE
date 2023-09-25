package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRequestResetPasswordRepository;
import com.tessi.cxm.pfl.ms5.service.specification.PasswordArchiveService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import lombok.Setter;
import org.modelmapper.ModelMapper;

public class MockUserService extends UserService {

  @Setter private boolean isSupperAdmin;
  @Setter private String userId;

  public MockUserService(
      DepartmentRepository departmentRepository,
      KeycloakService keycloakService,
      ModelMapper mapper,
      UserRepository userRepository,
      DepartmentService departmentService,
      ProfileRepository profileRepository,
      UserProfileRepository userProfileRepository,
      UserRequestResetPasswordRepository userRequestResetPasswordRepository,
      PasswordArchiveService passwordArchiveService) {
    super(
        departmentRepository,
        keycloakService,
        mapper,
        userRepository,
        departmentService,
        profileRepository,
        userProfileRepository,
        userRequestResetPasswordRepository,
        passwordArchiveService);
  }

  @Override
  public boolean isAdmin() {
    return this.isSupperAdmin;
  }

  @Override
  protected String getUserId() {
    return this.userId;
  }
}
