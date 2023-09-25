package com.tessi.cxm.pfl.ms11.service;

import com.tessi.cxm.pfl.ms11.repository.CriteriaDistributionRepository;
import com.tessi.cxm.pfl.ms11.repository.PortalSettingRepository;
import com.tessi.cxm.pfl.ms11.repository.SettingInstructionRepository;
import com.tessi.cxm.pfl.ms11.repository.SettingRepository;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import lombok.Setter;
import org.modelmapper.ModelMapper;

public class MockSettingService extends SettingService {
  @Setter private String username;

  public MockSettingService(
      SettingRepository settingRepository,
      ModelMapper modelMapper,
      SettingInstructionRepository settingInstructionRepository,
      ProfileFeignClient profileFeignClient,
      PortalSettingRepository portalSettingRepository) {
    super(
        settingRepository,
        modelMapper,
        settingInstructionRepository,
        profileFeignClient,
        portalSettingRepository);
  }

  @Override
  protected String getUsername() {
    return this.username;
  }
}
