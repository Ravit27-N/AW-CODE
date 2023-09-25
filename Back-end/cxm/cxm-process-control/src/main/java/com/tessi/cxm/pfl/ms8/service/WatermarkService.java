package com.tessi.cxm.pfl.ms8.service;

import com.tessi.cxm.pfl.ms8.dto.WatermarkDto;
import com.tessi.cxm.pfl.ms8.entity.Watermark;
import com.tessi.cxm.pfl.ms8.exception.WatermarkDuplicatedException;
import com.tessi.cxm.pfl.ms8.exception.WatermarkNotFoundException;
import com.tessi.cxm.pfl.ms8.repository.WatermarkRepository;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.service.ServiceUtils;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WatermarkService implements ServiceUtils {
  private final WatermarkRepository watermarkRepository;
  private final ProfileFeignClient profileFeignClient;
  private final ModelMapper modelMapper;

  @Override
  public ProfileFeignClient getProfileFeignClient() {
    return this.profileFeignClient;
  }

  public Optional<Watermark> findWatermarkByFlowId(String flowId) {
    return this.watermarkRepository.findByFlowId(flowId);
  }

  @Transactional(rollbackFor = Exception.class)
  public WatermarkDto saveWatermark(WatermarkDto watermarkDto) {
    // validate privilege user.
    validateUserPrivilege(null, ProfileConstants.Privilege.CREATE);

    // only once watermark per flow accepted.
    if (findWatermarkByFlowId(watermarkDto.getFlowId()).isPresent()) {
      throw new WatermarkDuplicatedException(watermarkDto.getFlowId());
    }

    // get owner details of user.
    UserDetail userDetail = getUserDetail();
    Watermark watermark = this.modelMapper.map(watermarkDto, Watermark.class);

    // set owner reference.
    watermark.setOwnerId(userDetail.getOwnerId());
    watermark.setCreatedBy(userDetail.getUsername());
    watermark = this.watermarkRepository.save(watermark);
    watermarkDto.setId(watermark.getId());
    return watermarkDto;
  }

  @Transactional(rollbackFor = Exception.class)
  public WatermarkDto updateWatermark(WatermarkDto watermarkDto) {
    // load watermark details by id.
    Watermark watermark =
        findWatermarkByFlowId(watermarkDto.getFlowId())
            .orElseThrow(() -> new WatermarkNotFoundException(watermarkDto.getFlowId()));
    this.modelMapper.map(watermarkDto, watermark);

    // validate privilege user.
    validateUserPrivilege(watermark.getOwnerId(), ProfileConstants.Privilege.MODIFY);

    // set owner reference modified by.
    watermark.setLastModifiedBy(getPreferredUsername());
    this.watermarkRepository.save(watermark);
    return watermarkDto;
  }

  @Transactional(readOnly = true)
  public WatermarkDto getWatermark(String flowId) {
    return findWatermarkByFlowId(flowId)
        .map(watermark -> this.modelMapper.map(watermark, WatermarkDto.class))
        .orElseThrow(() -> new WatermarkNotFoundException(flowId));
  }

  private void validateUserPrivilege(Long ownerId, String privilege) {
    final String funcKey = ProfileConstants.CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY;
    // validate the modification level of the user who owns this flow.
    if (ownerId != null && StringUtils.isNotBlank(privilege)) {
      validateUserAccessPrivilege(funcKey, privilege, false, ownerId);
    } else {
      // validate activatable or visibility of users who can create a watermark for this flow.
      getUserPrivilegeDetails(funcKey, privilege, true, false);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteWatermark(String flowId) {
    findWatermarkByFlowId(flowId)
        .ifPresent(
            watermark -> {
              validateUserPrivilege(watermark.getOwnerId(), ProfileConstants.Privilege.DELETE);
              watermarkRepository.delete(watermark);
            });
  }
}
