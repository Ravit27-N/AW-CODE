package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import lombok.Setter;

public class MockStorageFileService extends StorageFileService {

  @Setter
  private String userId;
  @Setter
  private boolean isSupperAdmin;

  public MockStorageFileService(
      FileManagerResource fileManagerResource,
      ProfileService profileService,
      ClientRepository clientRepository,
      UserRepository userRepository,
      KeycloakService keycloakService,
      SettingFeignClient settingFeignClient) {
    super(
        fileManagerResource,
        profileService,
        clientRepository,
        userRepository,
        keycloakService,
        settingFeignClient);
  }

  @Override
  public boolean isAdmin() {
    return this.isSupperAdmin;
  }

  @Override
  protected String getPrincipalIdentifier() {
    return this.userId;
  }
}
