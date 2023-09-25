package com.tessi.cxm.pfl.ms3.service;

import com.tessi.cxm.pfl.ms3.dto.FlowDepositDto;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.entity.BaseEntity_;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit_;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability_;
import com.tessi.cxm.pfl.ms3.repository.FlowDepositRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.ms3.service.specification.FlowDepositSpecification;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.FlowDepositArea;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Handling business logic of {@link FlowDeposit}.
 *
 * @author Piseth KHON
 * @since 09/29/2022
 */
@Service
public class FlowDepositService extends AbstractCrudService<FlowDepositDto, FlowDeposit, Long> {

  private final FlowDepositRepository flowDepositRepository;

  public FlowDepositService(
      ModelMapper modelMapper,
      FlowDepositRepository flowDepositRepository,
      FlowTraceabilityRepository flowTraceabilityRepository,
      ProfileFeignClient profileFeignClient,
      KeycloakService keycloakService) {
    setRepository(flowTraceabilityRepository);
    setKeycloakService(keycloakService);
    setProfileFeignClient(profileFeignClient);
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    modelMapper
        .createTypeMap(FlowDeposit.class, FlowDepositDto.class)
        .addMapping(
            source -> source.getFlowTraceability().getFlowName(), FlowDepositDto::setFlowName)
        .addMapping(
            source -> source.getFlowTraceability().getFullName(), FlowDepositDto::setFullName)
        .addMapping(
            source -> source.getFlowTraceability().getFlowTraceabilityDetails().isValidation(),
            FlowDepositDto::setValidated)
        .addMapping(
            source -> source.getFlowTraceability().getOwnerId(), FlowDepositDto::setOwnerId);
    this.flowDepositRepository = flowDepositRepository;
    this.modelMapper = modelMapper;
  }

  public Page<FlowDepositDto> findAll(
      FlowFilterCriteria filterCriteria, Pageable pageable, String fileId) {

    // load users related to current invoking user
    var visibilityPrivilege =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
            ProfileConstants.CXM_FLOW_DEPOSIT, FlowDepositArea.LIST_DEPOSITS, true, true);
    var ownerIds = visibilityPrivilege.getRelatedOwners();
    if (CollectionUtils.isEmpty(ownerIds)) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    // initialize objects for list of users
    final FlowDepositSpecification flowDepositSpecification = new FlowDepositSpecification();
    final List<String> attributeNames = new ArrayList<>();
    final List<String> allowedStatuses = new ArrayList<>();
    // for Attribute names
    attributeNames.add(FlowTraceability_.FLOW_NAME);
    attributeNames.add(FlowTraceability_.CHANNEL);
    attributeNames.add(FlowTraceability_.DEPOSIT_MODE);
    attributeNames.add(BaseEntity_.CREATED_BY);

    // for status names
    allowedStatuses.add(FlowTraceabilityStatusConstant.TO_FINALIZE);
    allowedStatuses.add(FlowTraceabilityStatusConstant.DEPOSITED);
    allowedStatuses.add(FlowTraceabilityStatusConstant.TREATMENT);

    if (filterCriteria.getUsers() != null && !filterCriteria.getUsers().isEmpty()) {
      ownerIds =
          ownerIds.stream()
              .parallel()
              .filter(ownerId -> filterCriteria.getUsers().contains(ownerId))
              .collect(Collectors.toUnmodifiableList());
    }

    // build specification
    Specification<FlowDeposit> specification =
        flowDepositSpecification
            .searching(attributeNames, filterCriteria.getFilter())
            .and()
            .matching(FlowTraceability_.CHANNEL, filterCriteria.getChannels())
            .and()
            .matching(FlowTraceability_.SUB_CHANNEL, filterCriteria.getCategories())
            .and()
            .matching(FlowTraceability_.DEPOSIT_MODE, filterCriteria.getDepositModes())
            .and()
            .matching(FlowTraceability_.STATUS, allowedStatuses)
            .and()
            .noneMatching(FlowDeposit_.IS_ACTIVE, false)
            .and()
            .matching(
                FlowDeposit_.FILE_ID, (StringUtils.hasText(fileId) ? List.of(fileId) : List.of()))
            .and()
            .noneMatching(FlowDeposit_.STATUS, List.of(FlowTraceabilityStatusConstant.FINALIZED))
            .end()
            .toSpecification();

    // Filter specification with flow owner ids.
    specification = specification.and(FlowDepositSpecification.flowOwnerIdIn(ownerIds));

    return this.flowDepositRepository.findAll(specification, pageable).map(this::mapData);
  }

  /**
   * To delete a flow by fileId.
   *
   * @param fileId refer to fileId map to a {@link FlowDeposit}
   */
  @Transactional(rollbackFor = Exception.class)
  public void delete(String fileId) {
    this.flowDepositRepository.findByFileId(fileId).ifPresent(this::delete);
  }

  /**
   * To delete a flow by its object.
   *
   * @param flowDeposit refer to object of {@link FlowDeposit}
   */
  public void delete(FlowDeposit flowDeposit) {
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_FLOW_DEPOSIT,
        FlowDepositArea.DELETE_A_DEPOSIT,
        false,
        flowDeposit.getFlowTraceability().getOwnerId());

    flowDeposit.setActive(false);
    this.flowDepositRepository.save(flowDeposit);
  }
}
