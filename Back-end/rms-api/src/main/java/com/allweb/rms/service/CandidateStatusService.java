package com.allweb.rms.service;

import com.allweb.rms.component.CandidateStatusModelAssembler;
import com.allweb.rms.entity.dto.CandidateStatusDTO;
import com.allweb.rms.entity.jpa.CandidateStatus;
import com.allweb.rms.exception.CandidateStatusNotFoundException;
import com.allweb.rms.exception.CandidateStatusTitleConflictException;
import com.allweb.rms.exception.RelationDatabaseException;
import com.allweb.rms.repository.jpa.CandidateStatusRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CandidateStatusService {

  private final CandidateStatusRepository candidateStatusRepository;
  private final ModelMapper modelMapper;
  private final CandidateStatusModelAssembler assembler;

  @Autowired
  public CandidateStatusService(
      CandidateStatusRepository candidateStatusRepository,
      ModelMapper modelMapper,
      CandidateStatusModelAssembler assembler) {
    this.candidateStatusRepository = candidateStatusRepository;
    this.modelMapper = modelMapper;
    this.assembler = assembler;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CandidateStatusDTO createStatusCandidate(CandidateStatusDTO statusCandidate) {
    // validate title candidate status on create
    if (candidateStatusRepository.validateTitle(statusCandidate.getTitle()) != 0) {
      throw new CandidateStatusTitleConflictException();
    }
    return convertToDTO(candidateStatusRepository.save(convertToEntity(statusCandidate)));
  }

  @Transactional
  public CandidateStatusDTO getCandidateStatusById(int id) {
    CandidateStatus statusCandidate =
        candidateStatusRepository
            .findById(id)
            .orElseThrow(() -> new CandidateStatusNotFoundException(id));
    return convertToDTO(statusCandidate);
  }

  /**
   * get all candidate status
   *
   * @param status
   * @param page
   * @param pageSize
   * @param sortDirection
   * @param sortByField
   * @param filter
   * @return
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<EntityModel<CandidateStatusDTO>> getCandidateStatus(
      String status,
      int page,
      int pageSize,
      String sortDirection,
      String sortByField,
      String filter) {
    if (status == null || status.equals("all")) {
      status = "all";
    }
    if (sortByField.equals("createdAt")) {
      sortByField = "created_at";
    }

    if (pageSize == 0) {
      return this.getCandidateStatusNoPagination(filter);
    }

    Pageable pageable =
        PageRequest.of(
            page - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    if (!Strings.isNullOrEmpty(filter)) {
      return new EntityResponseHandler<>(
          candidateStatusRepository
              .findAllByFilterStatusAndFilterAll(status, filter, pageable)
              .map(this::convertToEntityModel));
    }
    return new EntityResponseHandler<>(
        candidateStatusRepository
            .findAllByFilterStatus(status, pageable)
            .map(this::convertToEntityModel));
  }

  @Transactional(readOnly = true)
  public EntityResponseHandler<EntityModel<CandidateStatusDTO>> getCandidateStatusNoPagination(
      String filter) {
    if (Strings.isNullOrEmpty(filter)) {
      filter = "";
    }
    return new EntityResponseHandler<>(
        candidateStatusRepository.findAllNoPagination(filter).stream()
            .map(this::convertToEntityModel)
            .collect(Collectors.toList()));
  }

  /**
   * delete a candidate status by id but just update value in column deleted
   *
   * @param id
   * @param isDelete
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CandidateStatusDTO deleteCandidateStatus(int id, boolean isDelete) {
    // check status id in candidate when delete
    Integer count = candidateStatusRepository.getCandidateByStatusIdOnDeleteStatus(id);
    if (count != 0) {
      throw new RelationDatabaseException(
          "Candidate status id " + id + " is already in use with " + count + " candidates!");
    }
    CandidateStatus statusCandidate =
        candidateStatusRepository
            .findById(id)
            .orElseThrow(() -> new CandidateStatusNotFoundException(id));

    statusCandidate.setDeleted(isDelete);
    statusCandidate.setId(id);
    return convertToDTO(candidateStatusRepository.save(statusCandidate));
  }

  /**
   * Update active a candidate status by id
   *
   * @param id
   * @param active
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CandidateStatusDTO updateActiveCandidateStatus(int id, boolean active) {
    CandidateStatus statusCandidate1 =
        candidateStatusRepository
            .findById(id)
            .orElseThrow(() -> new CandidateStatusNotFoundException(id));
    statusCandidate1.setId(id);
    statusCandidate1.setActive(active);
    return convertToDTO(candidateStatusRepository.save(statusCandidate1));
  }

  /**
   * change value of isDeletable in candidate status to true or false
   *
   * @param id
   * @param isDeletable
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CandidateStatusDTO updateDeletableCandidateStatus(int id, boolean isDeletable) {
    CandidateStatus statusCandidate =
        candidateStatusRepository
            .findById(id)
            .orElseThrow(() -> new CandidateStatusNotFoundException(id));
    statusCandidate.setId(id);
    statusCandidate.setDeletable(isDeletable);
    return convertToDTO(candidateStatusRepository.save(statusCandidate));
  }

  /**
   * update all values in field of candidate status
   *
   * @param statusCandidate
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CandidateStatusDTO updateCandidateStatus(CandidateStatusDTO statusCandidate) {
    CandidateStatus statusCandidate1 =
        candidateStatusRepository
            .findById(statusCandidate.getId())
            .orElseThrow(() -> new CandidateStatusNotFoundException(statusCandidate.getId()));
    // validate title on update candidate status
    if (candidateStatusRepository.validateTitleOnUpdate(
            statusCandidate.getId(), statusCandidate.getTitle())
        != 0) {
      throw new CandidateStatusTitleConflictException();
    }
    statusCandidate1.setActive(statusCandidate.isActive());
    statusCandidate1.setDescription(statusCandidate.getDescription());
    statusCandidate1.setTitle(statusCandidate.getTitle());
    return convertToDTO(candidateStatusRepository.save(statusCandidate1));
  }

  @Transactional
  public Map<String, Object> findAllByMailConfigurationNotUsed(String filter) {
    Map<String, Object> map = new HashMap<>();
    if (Strings.isNullOrEmpty(filter)) {
      filter = "";
    }
    JsonNode response = candidateStatusRepository.findAllByMailConfigurationNotUsed(filter);
    map.put("candidateStatus", response == null ? JsonNodeFactory.instance.arrayNode() : response);
    return map;
  }

  public CandidateStatus convertToEntity(CandidateStatusDTO statusCandidateDTO) {
    return modelMapper.map(statusCandidateDTO, CandidateStatus.class);
  }

  public CandidateStatusDTO convertToDTO(CandidateStatus statusCandidate) {
    return modelMapper.map(statusCandidate, CandidateStatusDTO.class);
  }

  public EntityModel<CandidateStatusDTO> convertToEntityModel(CandidateStatus statusCandidate) {
    return assembler.toModel(modelMapper.map(statusCandidate, CandidateStatusDTO.class));
  }
}
