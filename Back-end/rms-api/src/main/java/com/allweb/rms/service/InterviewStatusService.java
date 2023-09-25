package com.allweb.rms.service;

import com.allweb.rms.component.InterviewStatusAssembler;
import com.allweb.rms.entity.dto.InterviewStatusDTO;
import com.allweb.rms.entity.jpa.InterviewStatus;
import com.allweb.rms.exception.InterviewNotFoundException;
import com.allweb.rms.exception.InterviewStatusInactiveException;
import com.allweb.rms.repository.jpa.InterviewStatusRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.google.common.base.Strings;
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
public class InterviewStatusService {
  private static final String MSG_FORMAT = "Status id %s not found";
  private final InterviewStatusRepository interviewStatusRepository;
  private final ModelMapper modelMapper;
  private final InterviewStatusAssembler assembler;

  @Autowired
  public InterviewStatusService(
      InterviewStatusRepository interviewStatusRepository,
      ModelMapper modelMapper,
      InterviewStatusAssembler assembler) {
    this.interviewStatusRepository = interviewStatusRepository;
    this.modelMapper = modelMapper;
    this.assembler = assembler;
  }

  /**
   * Method use retrieve status info by its id
   *
   * @param id of status
   * @return status properties
   */
  @Transactional(readOnly = true)
  public InterviewStatus getStatusById(int id) {
    return interviewStatusRepository
        .findById(id)
        .orElseThrow(() -> new InterviewNotFoundException(String.format(MSG_FORMAT, id)));
  }
  /**
   * Method use retrieve status info by its id and active is true
   *
   * @param id of status
   * @return status properties
   */
  @Transactional(readOnly = true)
  public InterviewStatus getStatusByIdAndActiveIsTrue(int id) {
    InterviewStatus interviewStatus = getStatusById(id);
    if (interviewStatus.isActive()) {
      return interviewStatus;
    } else {
      throw new InterviewStatusInactiveException(id);
    }
  }

  /**
   * Method use retrieve status info
   *
   * @return status list
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<EntityModel<InterviewStatusDTO>> getStatusList(
      int page, int size, String filter, String sortDirection, String sortByField, boolean active) {
    Pageable pageable =
        PageRequest.of(
            page - 1, size, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    if (active) {
      return new EntityResponseHandler<>(
          interviewStatusRepository
              .findAllByActiveIsTrue(pageable)
              .map(entity -> assembler.toModel(modelMapper.map(entity, InterviewStatusDTO.class))));
    }
    if (Strings.isNullOrEmpty(filter))
      return new EntityResponseHandler<>(
          interviewStatusRepository
              .findAll(pageable)
              .map(entity -> assembler.toModel(modelMapper.map(entity, InterviewStatusDTO.class))));
    return new EntityResponseHandler<>(
        interviewStatusRepository
            .findAllByNameContaining(filter.toLowerCase(), pageable)
            .map(entity -> assembler.toModel(modelMapper.map(entity, InterviewStatusDTO.class))));
  }

  /**
   * Method use to add new status info by its id
   *
   * @param status object
   * @return status properties
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public InterviewStatusDTO save(InterviewStatusDTO status) {
    if (interviewStatusRepository.findByName(status.getName().toLowerCase()).isPresent())
      throw new InterviewNotFoundException(status.getName() + " already exists");
    return modelMapper.map(
        interviewStatusRepository.save(modelMapper.map(status, InterviewStatus.class)),
        InterviewStatusDTO.class);
  }

  /**
   * Method use to update status info by its id
   *
   * @param status object
   * @return status properties
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public InterviewStatusDTO update(InterviewStatusDTO status) {
    InterviewStatus interviewStatus = getStatusById(status.getId());
    interviewStatus.setName(status.getName());
    interviewStatus.setUpdatedAt(status.getUpdatedAt());
    interviewStatus.setActive(status.isActive());
    return modelMapper.map(
        interviewStatusRepository.save(modelMapper.map(interviewStatus, InterviewStatus.class)),
        InterviewStatusDTO.class);
  }

  /**
   * Method use update active status info by its id
   *
   * @param id of interview status
   * @param b is boolean type of interview status
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void updateActive(int id, boolean b) {
    InterviewStatus statusById = getStatusById(id);
    statusById.setActive(b);
    interviewStatusRepository.save(statusById);
  }

  /**
   * Method use delete status info by its id
   *
   * @param id
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void delete(int id) {
    getStatusById(id);
    interviewStatusRepository.deleteById(id);
  }
}
