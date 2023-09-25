package com.tessi.cxm.pfl.ms3.service;

import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDto;
import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.exception.ElementAssociationNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentNotFoundException;
import com.tessi.cxm.pfl.ms3.repository.ElementAssociationRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.ms3.service.specification.ElementAssociationSpecification;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handle business logic of {@link ElementAssociationService} and control {@link Transactional}.
 * This class specific extension of {@link AbstractCrudService}.
 *
 * @since 03/11/21
 * @author Piseth Khon
 */
@Transactional
@Service
@Slf4j
public class ElementAssociationService
    extends AbstractCrudService<ElementAssociationDto, ElementAssociation, Long> {
  private final ElementAssociationRepository associationRepository;
  private final FlowDocumentRepository flowDocumentRepository;
  private final FlowTraceabilityRepository flowTraceabilityReposity;

  /**
   * Initialize require bean for {@link ElementAssociation}.
   *
   * @param flowDocumentRepository refer to {@link FlowDocumentRepository}
   * @param keycloakService refer to {@link KeycloakService}
   * @param modelMapper refer to {@link ModelMapper}
   * @param associationRepository refer to {@link ElementAssociationRepository}
   */
  public ElementAssociationService(
      ElementAssociationRepository associationRepository,
      FlowDocumentRepository flowDocumentRepository,
      ModelMapper modelMapper,
      KeycloakService keycloakService,
      FlowTraceabilityRepository flowTraceabilityReposity) {
    this.associationRepository = associationRepository;
    this.flowDocumentRepository = flowDocumentRepository;
    this.modelMapper = modelMapper;
    this.keycloakService = keycloakService;
    this.flowTraceabilityReposity = flowTraceabilityReposity;
  }

  /** Setter Injection. */
  @Autowired
  @Override
  public void setProfileFeignClient(ProfileFeignClient profileFeignClient) {
    super.setProfileFeignClient(profileFeignClient);
  }

  /**
   * Method use to get {@link FlowDocument} by its id.
   *
   * @param id refer to its {@link FlowDocument} identity.
   * @return {@link FlowDocument} else throw exception.
   */
  private FlowDocument getFlowDocument(long id) {
    return flowDocumentRepository
        .findById(id)
        .orElseThrow(() -> new FlowDocumentNotFoundException(id));
  }

  /**
   * Method use to get {@link ElementAssociation} by its id.
   *
   * @param id refer to its {@link FlowDocument} identity.
   * @return {@link ElementAssociation} else throw exception.
   */
  private ElementAssociation findEntity(long id) {
    return associationRepository
        .findById(id)
        .orElseThrow(() -> new ElementAssociationNotFoundException(id));
  }

  /**
   * Method use to get {@link ElementAssociationDto} by its id.
   *
   * @param id refer to its {@link FlowDocument} identity.
   * @return {@link ElementAssociationDto} else throw exception.
   */
  @Override
  @Transactional(readOnly = true)
  public ElementAssociationDto findById(Long id) {
    return mapData(this.findEntity(id), new ElementAssociationDto());
  }

  /**
   * Method used to store object of {@link ElementAssociation}.
   *
   * @see ElementAssociationRepository#save(Object)
   * @param dto refer to object of {@link ElementAssociationDto}
   * @return object of {@link ElementAssociationDto}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ElementAssociationDto save(ElementAssociationDto dto) {
    var flowDocument = this.getFlowDocument(dto.getFlowDocumentId()); // get FlowDocument reference.
    ElementAssociation elementAssociation = mapEntity(dto, new ElementAssociation());
    elementAssociation.setFlowDocument(flowDocument); // set flowDocument to elementAssociation.
    elementAssociation.setCreatedBy(this.getUsername());
    return mapData(associationRepository.save(elementAssociation), new ElementAssociationDto());
  }

  /**
   * Method use to get {@link ElementAssociation} by {@link FlowDocument} id.
   *
   * @param documentId refer to {@link FlowDocument} identity
   * @return {@link List} of {@link ElementAssociationDto} instead of {@link ElementAssociation}
   */
  @Transactional(readOnly = true)
  public List<ElementAssociationDto> findAll(long documentId) {

    var flowDocument = this.getFlowDocument(documentId);

    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_FLOW_TRACEABILITY,
        Privilege.FlowDocument.SELECT_AND_OPEN,
        true,
        flowDocument.getFlowTraceability().getOwnerId());

    return associationRepository
        .findAll(
            Specification.where(ElementAssociationSpecification.containFlowDocumentId(documentId)))
        .stream()
        .map(e -> this.mapData(e, new ElementAssociationDto()))
        .collect(Collectors.toList());
  }
}
