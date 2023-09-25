package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.core.flow.UserCreationManager;
import lombok.Setter;

public class MockBatchUserService extends BatchUserService {

  @Setter
  private boolean isSupperAdmin;
  @Setter
  private String userId;

  public MockBatchUserService(
      UserCreationManager userCreationManager,
      UserRepository userRepository,
      DepartmentRepository departmentRepository,
      ProfileService profileService,
      ProfileRepository profileRepository) {
    super(userCreationManager, userRepository, departmentRepository,
        profileService, profileRepository);
  }

  @Override
  public boolean isAdmin() {
    return isSupperAdmin;
  }

  @Override
  public boolean isSuperAdmin() {
    return isSupperAdmin;
  }
}
