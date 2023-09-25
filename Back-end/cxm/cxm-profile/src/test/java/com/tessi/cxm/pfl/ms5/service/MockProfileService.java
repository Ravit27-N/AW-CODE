package com.tessi.cxm.pfl.ms5.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileDetailsRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

public class MockProfileService extends ProfileService {
  @Getter
  @Setter
  private String username;

  /**
   * The constructor used to initial required bean.
   *
   * @param modelMapper bean of {@link ModelMapper}
   * @param profileRepository bean of {@link ProfileRepository}
   * @param profileDetailsRepository bean of {@link ProfileDetailsRepository}
   * @param clientRepository bean of {@link ClientRepository}
   * @param userProfileRepository bean of {@link UserProfileRepository}
   * @param clientService
   * @param userRepository
   * @param keycloakService
   * @param objectMapper bean of {@link ObjectMapper}
   * @param departmentRepository
   */
  public MockProfileService(
      ModelMapper modelMapper,
      ProfileRepository profileRepository,
      ProfileDetailsRepository profileDetailsRepository,
      ClientRepository clientRepository,
      UserProfileRepository userProfileRepository,
      ClientService clientService,
      UserRepository userRepository,
      KeycloakService keycloakService,
      ObjectMapper objectMapper,
      DepartmentRepository departmentRepository) {
    super(
        modelMapper,
        profileRepository,
        profileDetailsRepository,
        clientRepository,
        userProfileRepository,
        clientService,
        userRepository,
        keycloakService,
        objectMapper,
        departmentRepository);
  }
}
