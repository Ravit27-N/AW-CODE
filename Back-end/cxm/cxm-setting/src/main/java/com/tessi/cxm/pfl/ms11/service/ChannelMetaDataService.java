package com.tessi.cxm.pfl.ms11.service;

import com.google.common.collect.Ordering;
import com.tessi.cxm.pfl.ms11.constant.ChannelMetaDataTypeConstant;
import com.tessi.cxm.pfl.ms11.constant.ChannelMetadataType;
import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataItem;
import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataRequestDto;
import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataResponseDto;
import com.tessi.cxm.pfl.ms11.entity.ChannelMetadata;
import com.tessi.cxm.pfl.ms11.exception.ChannelMetaDataNotOrderException;
import com.tessi.cxm.pfl.ms11.exception.ChannelMetaDataNotUniqueException;
import com.tessi.cxm.pfl.ms11.exception.HubDomainNameFailureException;
import com.tessi.cxm.pfl.ms11.exception.SenderLabelSizeException;
import com.tessi.cxm.pfl.ms11.exception.SenderMailInvalidException;
import com.tessi.cxm.pfl.ms11.exception.UnsubscribeLinkInvalidException;
import com.tessi.cxm.pfl.ms11.repository.ChannelMetadataRepository;
import com.tessi.cxm.pfl.ms11.util.SettingPrivilegeUtil;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerDomainNameRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.DomainNameRequest;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.AESHelper;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.EmailValidator;
import com.tessi.cxm.pfl.shared.utils.HubDigitalFlowHelper;
import feign.FeignException.FeignClientException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Slf4j
public class ChannelMetaDataService {

  private static final String SENDER_MAIL_INVALID = "Sender mail is invalid";
  private static final String UNSUBSCRIBE_LINK_MAIL_INVALID = "Unsubscribe link mail is invalid";
  private static final String CHANEL_METADATA_NOT_START_FROM_1 = "Channel metadata must be start form 1";
  private static final String CHANEL_METADATA_VALUE_NOT_UNIQUE = "Channel metadata value must be unique";
  private static final String CHANEL_METADATA_ORDER_NOT_UNIQUE = "Channel metadata order must be unique";
  private static final String CHANEL_METADATA_NOT_ORDERING = "Channel metadata must be ordering";
  private static final String SENDER_LABEL_LENGTH = "Length of value must be lest than 11";
  private final ChannelMetadataRepository channelMetadataRepository;
  private final ModelMapper modelMapper;
  private final ProfileFeignClient profileFeignClient;
  private final HubDigitalFlow hubDigitalFlow;
  private final HubDigitalFlowHelper hubDigitalFlowHelper;

  @Autowired
  public void setSettingPrivilegeUtil(ProfileFeignClient profileFeignClient) {
    SettingPrivilegeUtil.setProfileFeignClient(profileFeignClient);
  }

  @Autowired
  public void setHubDigitalFlowHelper(AESHelper aesHelper) {
    this.hubDigitalFlowHelper.setAesHelper(aesHelper);
  }

  /**
   * Save Channel Metadata
   *
   * @param dto Value to create
   * @return channelMetadataDto
   */
  @Transactional(rollbackFor = Exception.class)
  public ChannelMetadataRequestDto save(ChannelMetadataRequestDto dto) {
    // Validate authorization is user admin && client exist in system.
    SettingPrivilegeUtil.validateAdminRequest(dto.getCustomer());

    // Validate body of request.
    this.validateRequestBody(dto);

    // Get metadata from database.
    var entityListOptional = this.channelMetadataRepository.findAllByCustomerAndType(
        dto.getCustomer(), dto.getType());
    List<ChannelMetadata> entityResponses;

    // Modified metadata form.
    if (!entityListOptional.isEmpty() && entityListOptional.stream()
        .anyMatch(Objects::nonNull)) {
      // filter to get id of channel metadata to remove.
      var channelMetadataIdRemoved = this.filterMetadataRemovable(dto, entityListOptional);
      entityResponses = this.updateChannelMetadata(dto, entityListOptional,
          channelMetadataIdRemoved);
    } else {
      entityResponses = this.saveChannelMetadata(dto);
    }

    // Create domain name and link with this customer.
    if (ChannelMetaDataTypeConstant.SENDER_MAIL.equals(dto.getType())) {
      this.createDomainName(dto);
    }

    // Mapping entity to dto.
    var response = entityResponses.stream()
        .map(entity -> this.modelMapper.map(entity, ChannelMetadataItem.class))
        .collect(Collectors.toList());

    return ChannelMetadataRequestDto.builder()
        .customer(dto.getCustomer())
        .type(dto.getType())
        .metadata(response)
        .build();
  }

  /**
   * Get all channel metadata per types.
   *
   * @param customer - customer name {@link String}.
   * @param types    - collection of type {@link String}.
   * @return - object of {@link ChannelMetadataResponseDto}.
   */
  public ChannelMetadataResponseDto getChannelMetadataPerTypes(String customer,
      List<String> types) {
    if (!customer.isBlank()) {
      // Validate authorization is user admin && client exist in system.
      SettingPrivilegeUtil.validateAdminRequest(customer);
    } else {
      customer = this.getUserDetail().getClientName();
      // Validate a client exist.
      SettingPrivilegeUtil.validateClientExist(customer);
    }

    // Validate channel metadata types, It will throw error when type not found.
    types.forEach(ChannelMetadataType::getByKey);

    // Get all channel metadata from db.
    var entities = this.channelMetadataRepository.findAllChannelMetadata(customer, types);

    // Mapping entities to dto.
    ChannelMetadataResponseDto dto = new ChannelMetadataResponseDto();
    dto.setCustomer(customer);

    // Initialized metadata.
    types.forEach(
        type -> dto.add(ChannelMetadataType.getFieldByKey(type).getField(), new ArrayList<>()));

    // Prefill metadata.
    entities.stream().collect(Collectors.groupingBy(ChannelMetadata::getType))
        .forEach((type, channelMetadata) -> {
          var metadataList = channelMetadata.stream()
              .sorted(Comparator.comparing(ChannelMetadata::getOrder))
              .map(
                  item -> new ChannelMetadataItem(item.getId(), item.getValue(),
                      item.getOrder()))
              .collect(Collectors.toList());

          dto.add(ChannelMetadataType.getFieldByKey(type).getField(), metadataList);
        });

    return dto;
  }

  /**
   * Create domain name and link with this customer.
   *
   * @param dto - object of {@link ChannelMetadataRequestDto}.
   */
  private void createDomainName(ChannelMetadataRequestDto dto) {

    List<String> domainName = dto.getMetadata().stream()
        .map(metadataRequest -> {
          String[] domain = metadataRequest.getValue().split("@");
          return domain[1];
        })
        .collect(Collectors.toList());

    List<DomainNameRequest> domainNameRequests = domainName.stream().distinct().map(s -> {
      return new DomainNameRequest(s);
    }).collect(Collectors.toList());

    CustomerDomainNameRequest customerDomainNameRequest = new CustomerDomainNameRequest(
        dto.getCustomer(), domainNameRequests);

    String hubAuthToken = hubDigitalFlowHelper.getUserHubTokenByKeycloakAdmin(dto.getCustomer(),
        BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()));
    try {
      this.hubDigitalFlow.createDomainNameAndLinkCustomer(customerDomainNameRequest, hubAuthToken);
    } catch (FeignClientException feignClientException) {
      log.error(feignClientException.getMessage(), feignClientException);
      throw new HubDomainNameFailureException("Fail to create domain name in cxm hub-digitalflow");
    }
  }

  /**
   * Method used to update channel metadata & remove metadata that unused.
   *
   * @param dto        - object of {@link ChannelMetadataRequestDto}.
   * @param entityList - collection of {@link ChannelMetadata}.
   * @param idRemoved  - collection
   * @return
   */
  private List<ChannelMetadata> updateChannelMetadata(ChannelMetadataRequestDto dto,
      List<ChannelMetadata> entityList, List<Long> idRemoved) {
    // filter to get final metadata list.
    UserDetail userDetail = this.getUserDetail();

    var copyEntityList = entityList;
    if (!idRemoved.isEmpty()) {
      copyEntityList = entityList.stream().filter(
              channelMetadata -> !idRemoved.contains(channelMetadata.getId()))
          .collect(Collectors.toList());
    }

    List<ChannelMetadata> finalEntityList = copyEntityList;
    dto.getMetadata().forEach(metadataRequest -> {
      // Update old metadata.
      finalEntityList.stream().filter(channelMetadata -> Objects.equals(
              channelMetadata.getId(), metadataRequest.getId()))
          .findFirst().ifPresent(channelMetadata -> {
            channelMetadata.setOrder(metadataRequest.getOrder());
            channelMetadata.setValue(metadataRequest.getValue());
            channelMetadata.setLastModifiedBy(userDetail.getUsername());
          });

      // Add new metadata.
      if (Objects.isNull(metadataRequest.getId()) || metadataRequest.getId() == 0) {
        ChannelMetadata metadata = new ChannelMetadata();
        metadata.setOrder(metadataRequest.getOrder());
        metadata.setValue(metadataRequest.getValue());
        metadata.setCustomer(dto.getCustomer());
        metadata.setType(dto.getType());
        metadata.setCreatedBy(userDetail.getUsername());
        finalEntityList.add(metadata);
      }
    });

    // Remove metadata that unused.
    this.channelMetadataRepository.deleteAllById(idRemoved);
    return this.channelMetadataRepository.saveAll(copyEntityList);
  }

  /**
   * Method used to create new channel metadata.
   *
   * @param dto - object of {@link ChannelMetadataRequestDto}.
   * @return - collection of {@link ChannelMetadata}.
   */
  private List<ChannelMetadata> saveChannelMetadata(ChannelMetadataRequestDto dto) {
    // Create metadata form.
    UserDetail userDetail = this.getUserDetail();
    var channelMetadataListNew = dto.getMetadata().stream().map(metadataRequest ->
    {
      final ChannelMetadata entity = this.modelMapper.map(dto,
          ChannelMetadata.class);
      entity.setCustomer(dto.getCustomer());
      entity.setType(dto.getType());
      entity.setOrder(metadataRequest.getOrder());
      entity.setValue(metadataRequest.getValue());
      entity.setCreatedBy(userDetail.getUsername());
      return entity;
    }).collect(Collectors.toList());
    return this.channelMetadataRepository.saveAll(channelMetadataListNew);
  }

  /**
   * Filter metadata that we will to remove.
   *
   * @param dto        - object of {@link ChannelMetadataRequestDto}.
   * @param entityList - collection of {@link ChannelMetadata}.
   * @return - collection of file metadata ids {@link Long}.
   */
  private List<Long> filterMetadataRemovable(ChannelMetadataRequestDto dto,
      List<ChannelMetadata> entityList) {
    var channelMetadataDtoIds = dto.getMetadata().stream()
        .filter(metadataRequest -> Objects.nonNull(metadataRequest.getId())
            && metadataRequest.getId() != 0)
        .map(ChannelMetadataItem::getId)
        .collect(Collectors.toList());

    var channelMetadataEntityIds = entityList.stream().map(ChannelMetadata::getId)
        .collect(Collectors.toList());

    // If IDs of dto not empty, we filter with IDs of entities.
    if (!channelMetadataDtoIds.isEmpty()) {
      return channelMetadataEntityIds.stream()
          .filter(
              aLong -> !channelMetadataDtoIds.contains(aLong))
          .collect(
              Collectors.toList());
    }

    // We return all ID of entities to remove.
    return channelMetadataEntityIds;
  }

  /**
   * Method used to validate request body of dto.
   *
   * @param dto - object of {@link ChannelMetadataRequestDto}.
   */
  private void validateRequestBody(ChannelMetadataRequestDto dto) {
    // Validate channel metadata type, It will throw error when type not found.
    ChannelMetadataType.getByKey(dto.getType());

    // Validate sender mail or unsubscribe link pattern.
    this.checkPattern(dto);

    // Validate ordering.
    this.checkOrdering(dto.getMetadata());

    // validate unique name.
    this.checkUniqueName(dto.getMetadata());
  }

  private void checkUniqueName(List<ChannelMetadataItem> metadata) {
    var distinctCount = metadata.stream().map(ChannelMetadataItem::getValue).distinct().count();
    if (distinctCount != metadata.size()) {
      throw new ChannelMetaDataNotUniqueException(CHANEL_METADATA_VALUE_NOT_UNIQUE);
    }
  }

  /**
   * Validate pattern of sender mail & unsubscribe link, If one of these has error it will throw
   * error.
   *
   * @param dto - object of {@link ChannelMetadataRequestDto}.
   */
  private void checkPattern(ChannelMetadataRequestDto dto) {
    List<String> typeOfValidated = List.of(ChannelMetaDataTypeConstant.SENDER_MAIL,
        ChannelMetaDataTypeConstant.UNSUBSCRIBE_LINK);

    dto.getMetadata().stream()
        .filter(channelMetadata -> {
          if (typeOfValidated.contains(dto.getType())) {
            return !EmailValidator.isEmail(channelMetadata.getValue());
          }
          if (ChannelMetaDataTypeConstant.SENDER_LABEL.equals(dto.getType())) {
            return !this.validateValueSizeSenderLabel(channelMetadata.getValue());
          }
          return false;
        })
        .findFirst()
        .ifPresent(channelMetadataItem -> {
          if (ChannelMetaDataTypeConstant.SENDER_MAIL.equals(
              dto.getType())) {
            throw new SenderMailInvalidException(SENDER_MAIL_INVALID);
          }
          if (ChannelMetaDataTypeConstant.SENDER_NAME.equals(dto.getType())) {
            throw new UnsubscribeLinkInvalidException(UNSUBSCRIBE_LINK_MAIL_INVALID);
          }
          if (ChannelMetaDataTypeConstant.SENDER_LABEL.equals(dto.getType())) {
            throw new SenderLabelSizeException(SENDER_LABEL_LENGTH);
          }
        });
  }

  /**
   * Validate order of channel metadata, It will throw error if one of these condition is true.
   *
   * @param channelMetadataItems - collection of {@link ChannelMetadataItem}.
   */
  private void checkOrdering(List<ChannelMetadataItem> channelMetadataItems) {
    List<Long> orders = channelMetadataItems.stream().map(ChannelMetadataItem::getOrder)
        .collect(
            Collectors.toList());
    if (orders.isEmpty()) {
      return;
    }

    if (orders.get(0) != 1) {
      throw new ChannelMetaDataNotOrderException(CHANEL_METADATA_NOT_START_FROM_1);
    }

    var distinct = orders.stream().distinct().collect(Collectors.toList());
    if (distinct.size() != orders.size()) {
      throw new ChannelMetaDataNotUniqueException(CHANEL_METADATA_ORDER_NOT_UNIQUE);
    }

    if (!Ordering.natural().isOrdered(orders)) {
      throw new ChannelMetaDataNotOrderException(CHANEL_METADATA_NOT_ORDERING);
    }
  }

  private boolean validateValueSizeSenderLabel(String value) {
    if (value.length() > 11) {
      return false;
    }
    return true;
  }

  /**
   * Get user detail by authorization token.
   *
   * @return - object of {@link UserDetail}.
   */
  private UserDetail getUserDetail() {
    String authorization = BearerAuthentication.PREFIX_TOKEN.concat(
        AuthenticationUtils.getAuthToken());
    return this.profileFeignClient.getUserDetail(authorization);
  }
}
