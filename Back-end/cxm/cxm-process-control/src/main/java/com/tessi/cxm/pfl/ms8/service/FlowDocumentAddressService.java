package com.tessi.cxm.pfl.ms8.service;

import com.tessi.cxm.pfl.ms8.dto.FlowDocumentAddressDto;
import com.tessi.cxm.pfl.ms8.dto.FlowDocumentAddressLineDto;
import com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress;
import com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress_;
import com.tessi.cxm.pfl.ms8.model.ModifiedFlowDocumentAddress;
import com.tessi.cxm.pfl.ms8.repository.FlowDocumentAddressRepository;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.service.ServiceUtils;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.AddressValidator;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FlowDocumentAddressService implements ServiceUtils {

  private final ModelMapper mapper;
  private final ProfileFeignClient profileFeignClient;
  private final FlowDocumentAddressRepository flowDocumentAddressRepository;
  private final AddressValidator addressValidator;

  @Override
  public ProfileFeignClient getProfileFeignClient() {
    return this.profileFeignClient;
  }

  @Transactional(readOnly = true)
  public List<ModifiedFlowDocumentAddress> getModifiedFlowDocumentAddress(String flowId) {
    var flowDocumentMap =
        this.flowDocumentAddressRepository.findAllByFlowId(flowId).stream()
            .collect(
                Collectors.groupingBy(com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress::getDocId));
    return flowDocumentMap.entrySet().stream()
        .map(
            docAddressEntry ->
                new ModifiedFlowDocumentAddress(
                    hasModifiedAddress(docAddressEntry.getValue()),
                    docAddressEntry.getKey(),
                    getFirstAddress(docAddressEntry.getValue())))
        .collect(Collectors.toList());
  }

  private boolean hasModifiedAddress(
      List<com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress> flowDocumentAddresses) {
    return flowDocumentAddresses.stream()
        .anyMatch(docAddress -> StringUtils.isNotBlank(docAddress.getModifiedAddress()));
  }

  private String getFirstAddress(List<FlowDocumentAddress> flowDocumentAddresses) {
    flowDocumentAddresses.sort(Comparator.comparing(FlowDocumentAddress::getAddressLineNumber));
    return flowDocumentAddresses.stream()
        .findFirst()
        .map(
            docAddress ->
                (StringUtils.isNotBlank(docAddress.getModifiedAddress()))
                    ? docAddress.getModifiedAddress()
                    : docAddress.getOriginalAddress())
        .orElse(null);
  }

  @Transactional(readOnly = true)
  public List<FlowDocumentAddressLineDto> getFlowDocumentAddress(String flowId, String docId) {
    return this.flowDocumentAddressRepository
        .findAllByFlowIdAndDocIdAndModifiedAddressIsNotNull(
            flowId, docId, Sort.by(FlowDocumentAddress_.ADDRESS_LINE_NUMBER))
        .stream()
        .map(docAddress -> this.mapper.map(docAddress, FlowDocumentAddressLineDto.class))
        .collect(Collectors.toList());
  }

  @Transactional(rollbackFor = Exception.class)
  public FlowDocumentAddressDto updateFlowDocumentAddress(
      FlowDocumentAddressDto flowDocumentAddressDto) {
    validatePrivilege(flowDocumentAddressDto.getFlowId());

    AddressDto addressDto = this.getAddressDto(flowDocumentAddressDto);
    this.addressValidator.validate(addressDto);

    List<com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress> flowDocumentAddresses =
        flowDocumentAddressDto.getFlowDocumentAddress().stream()
            .map(
                flowDocumentAddress ->
                    this.mapper.map(
                        flowDocumentAddress,
                        com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress.class))
            .collect(Collectors.toList());
    // add flowId and document id to addressLines.
    flowDocumentAddresses.forEach(
        flowDocumentAddress -> {
          flowDocumentAddress.setFlowId(flowDocumentAddressDto.getFlowId());
          flowDocumentAddress.setDocId(flowDocumentAddressDto.getDocId());
        });
    // map user details for this flowDocumentAddress.
    mapUserDetails(flowDocumentAddresses);
    // remove old address.
    var lineAddress =
        flowDocumentAddresses.stream()
            .map(FlowDocumentAddress::getAddressLineNumber)
            .collect(Collectors.toList());

    this.flowDocumentAddressRepository.updateFlowDocumentAddressModified(
        LocalDateTime.now(),
        this.getPreferredUsername(),
        flowDocumentAddressDto.getFlowId(),
        flowDocumentAddressDto.getDocId(),
        lineAddress);
    this.flowDocumentAddressRepository.saveAll(flowDocumentAddresses);
    return flowDocumentAddressDto;
  }

  private AddressDto getAddressDto(FlowDocumentAddressDto flowDocumentAddressDto){
    AddressDto addressDto=new AddressDto();
    List<FlowDocumentAddressLineDto> flowDocumentAddress =
        flowDocumentAddressDto.getFlowDocumentAddress();

    flowDocumentAddress.forEach(
        flowDocumentAddressLineDto -> {
          switch (flowDocumentAddressLineDto.getAddressLineNumber()) {
            case 1:
              addressDto.setLine1(flowDocumentAddressLineDto.getAddress());
              break;
            case 2:
              addressDto.setLine2(flowDocumentAddressLineDto.getAddress());
              break;
            case 3:
              addressDto.setLine3(flowDocumentAddressLineDto.getAddress());
              break;
            case 4:
              addressDto.setLine4(flowDocumentAddressLineDto.getAddress());
              break;
            case 5:
              addressDto.setLine5(flowDocumentAddressLineDto.getAddress());
              break;
            case 6:
              addressDto.setLine6(flowDocumentAddressLineDto.getAddress());
              break;
            case 7:
              addressDto.setLine7(flowDocumentAddressLineDto.getAddress());
              break;
            default:
              break;
          }
        });
    return addressDto;
  }

  private void mapUserDetails(
      List<com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress> flowDocumentAddresses) {
    UserDetail userDetail = this.getUserDetail();
    flowDocumentAddresses.forEach(
        flowDocumentAddress -> {
          flowDocumentAddress.setOwnerId(userDetail.getOwnerId());
          flowDocumentAddress.setCreatedBy(userDetail.getUsername());
        });
  }
  
  private void validatePrivilege(String flowId) {
    FlowDocumentAddress flowDocumentAddress =
        this.flowDocumentAddressRepository.findFirstByFlowId(flowId);
    final String funcKey = ProfileConstants.CXM_FLOW_DEPOSIT;
    final String privKey = ProfileConstants.FlowDepositArea.MODIFY_OR_CORRECT_AN_ADDRESS;
    this.validateUserAccessPrivilege(funcKey, privKey, false, flowDocumentAddress.getOwnerId());
  }
}
