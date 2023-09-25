
package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.entity.projection.LoadUserPrivilegeDetails;
import com.tessi.cxm.pfl.ms5.exception.DivisionNameConflictException;
import com.tessi.cxm.pfl.ms5.exception.DivisionNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.DivisionRepository;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.DivisionDto;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.specification.DepartmentSpecification;
import com.tessi.cxm.pfl.ms5.service.specification.DivisionSpecification;
import com.tessi.cxm.pfl.shared.model.DepartmentProjection;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DivisionService extends AbstractCrudService<DivisionDto, Division, Long> {

  private final DivisionRepository divisionRepository;
  private final UserRepository userRepository;
  private final ClientService clientService;

  public DivisionService(
      DivisionRepository divisionRepository,
      UserRepository userRepository,
      ModelMapper modelMapper,
      ClientService clientService) {
    this.divisionRepository = divisionRepository;
    this.userRepository = userRepository;
    this.clientService = clientService;
    this.modelMapper = modelMapper;
  }

  /**
   * Get all division.
   *
   * @return all division
   */
  @Override
  public List<DivisionDto> findAll() {
    return this.divisionRepository.findAll().stream()
        .map(e -> this.mapData(e, new DivisionDto()))
        .collect(Collectors.toList());
  }

  /**
   * Get division By identity.
   *
   * @param id is identity of division
   * @return a value is present
   */
  @Transactional(readOnly = true)
  public Division findEntity(long id) {
    return this.divisionRepository
        .findById(id)
        .orElseThrow(() -> new DivisionNotFoundException(id));
  }

  /**
   * Get division By identity.
   *
   * @param id is identity of division
   * @return a value is present
   */
  @Override
  public DivisionDto findById(Long id) {
    return mapData(this.findEntity(id), new DivisionDto());
  }

  /**
   * Method used to get client by id.
   *
   * @param clientId Client's id
   * @return object of {@link Client}
   */
  private Client getClient(Long clientId) {
    return this.clientService.findEntity(clientId);
  }

  /**
   * Method use to save division.
   *
   * @param dto is division object
   * @return new division
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public DivisionDto save(DivisionDto dto) {
    if (this.validateDuplicateName(0, dto.getClientId(), dto.getName())) {
      throw new DivisionNameConflictException();
    }
    var division = mapEntity(dto, new Division());
    division.setClient(this.getClient(dto.getClientId()));
    return mapData(this.divisionRepository.save(division), new DivisionDto());
  }

  /**
   * Method use to update division.
   *
   * @param dto is division object
   * @return the division that has modified
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public DivisionDto update(DivisionDto dto) {
    if (this.validateDuplicateName(dto.getId(), dto.getClientId(), dto.getName())) {
      throw new DivisionNameConflictException();
    }
    this.getClient(dto.getClientId());

    var getDivision = this.findEntity(dto.getId());
    var entity = mapEntity(dto, getDivision);
    return mapData(this.divisionRepository.save(entity), new DivisionDto());
  }

  /**
   * Delete division By identity.
   *
   * @param id is identity
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    this.divisionRepository.delete(this.findEntity(id));
  }

  /**
   * Get all user in division.
   *
   * @param id is identity of division
   * @return All user in division
   */
  @Transactional(readOnly = true)
  public List<String> getAllUserInDivision(long id) {
    if (!divisionRepository.existsById(id)) {
      throw new DivisionNotFoundException(id);
    }
    return this.userRepository.getAllUsersInDivision(id).stream()
        .map(LoadUserPrivilegeDetails::getUsername)
        .collect(Collectors.toList());
  }

  /**
   * Get all divisions in client.
   *
   * @param clientId is identity of client
   * @return All divisions in client
   */
  public List<DivisionDto> getDivisionByClient(long clientId) {
    clientId = this.clientService.findById(clientId).getId();
    return this.divisionRepository.findAllByClientId(clientId).stream()
        .map(this::mapData)
        .collect(Collectors.toList());
  }

  /**
   * To validate and check duplicate the division's name {@link Division}
   *
   * @param id       refer to {@link Division} id.
   * @param clientId refer to {@link Client} id.
   * @param name     refer to name of {@link Division}
   * @return true if name is duplicated.
   */
  public boolean validateDuplicateName(long id, long clientId, String name) {
    Specification<Division> specification = Specification.where(null);
    if (id != 0) {
      var entity = this.findEntity(id);
      if (entity.getName().equalsIgnoreCase(name) && entity.getClient().getId() == clientId) {
        return false;
      }
      specification = specification.and(DivisionSpecification.notEqualId(id));
    }
    specification = specification.and(DivisionSpecification.equalName(name)
        .and(DivisionSpecification.equalClientId(clientId)));
    return this.divisionRepository.findOne(specification).isPresent();
  }

  // add new
  /**
   * Get all divisions in client.
   *
   * @param clientIds is identity of client
   * @return All divisions in client
   */
  public List<DivisionDto> getDivisionByClient2(long clientId) {
    clientId = this.clientService.getDivisionByClient2(clientId).getId();
    return this.divisionRepository.findAllByClientId(clientId).stream().map(this::mapData).collect(Collectors.toList());
  }
}
