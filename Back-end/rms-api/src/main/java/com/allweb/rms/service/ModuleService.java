package com.allweb.rms.service;

import com.allweb.rms.component.ModuleModelAssembler;
import com.allweb.rms.entity.dto.ModuleDTO;
import com.allweb.rms.entity.jpa.Module;
import com.allweb.rms.exception.ModuleNotFoundException;
import com.allweb.rms.exception.RelationDatabaseException;
import com.allweb.rms.repository.jpa.ModuleRepository;
import com.allweb.rms.repository.jpa.UserRoleDetailRepository;
import com.allweb.rms.utils.EntityResponseHandler;
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
public class ModuleService {

  private final ModuleRepository moduleRepository;
  private final ModelMapper modelMapper;
  private final ModuleModelAssembler assembler;
  private final UserRoleDetailRepository userRoleDetailRepository;

  @Autowired
  public ModuleService(
      ModuleRepository moduleRepository,
      ModelMapper modelMapper,
      ModuleModelAssembler assembler,
      UserRoleDetailRepository userRoleDetailRepository) {
    this.moduleRepository = moduleRepository;
    this.modelMapper = modelMapper;
    this.assembler = assembler;
    this.userRoleDetailRepository = userRoleDetailRepository;
  }

  // convert entity to dto
  public ModuleDTO convertToDTO(Module module) {
    return modelMapper.map(module, ModuleDTO.class);
  }

  // convert dto to entity
  public Module convertToEntity(ModuleDTO moduleDTO) {
    return modelMapper.map(moduleDTO, Module.class);
  }

  private EntityModel<ModuleDTO> convertDTOToEntityViewModel(Module module) {
    return assembler.toModel(modelMapper.map(module, ModuleDTO.class));
  }

  // create module
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public ModuleDTO createModule(ModuleDTO moduleDTO) {
    return convertToDTO(moduleRepository.save(convertToEntity(moduleDTO)));
  }

  // update module
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public ModuleDTO updateModule(ModuleDTO moduleDTO) {
    Module module =
        moduleRepository
            .findById(moduleDTO.getId())
            .orElseThrow(() -> new ModuleNotFoundException(moduleDTO.getId()));
    moduleDTO.setCreatedAt(module.getCreatedAt());
    return convertToDTO(moduleRepository.save(convertToEntity(moduleDTO)));
  }

  // get module by id
  @Transactional(readOnly = true)
  public ModuleDTO findModuleById(int id) {
    return convertToDTO(
        moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException(id)));
  }

  // update or soft active
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public ModuleDTO updateActive(int id, boolean isActive) {
    Module module =
        moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException(id));
    module.setActive(isActive);
    return convertToDTO(moduleRepository.save(module));
  }

  @Transactional
  public EntityResponseHandler<EntityModel<ModuleDTO>> getAllModules(
      int page, int pageSize, String sortDirection, String sortByField, String filter) {
    if (sortByField.equals("createdAt")) {
      sortByField = "created_at";
    }
    Pageable pageable =
        PageRequest.of(
            page - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    if (filter == null) {
      filter = "all";
    }
    return new EntityResponseHandler<>(
        moduleRepository.getAllModules(filter, pageable).map(this::convertDTOToEntityViewModel));
  }

  public void deleteModuleById(int id) {
    if (userRoleDetailRepository.countAllByModuleId(id) != 0) {
      throw new RelationDatabaseException("Module id " + id + " is using with another!");
    }
    moduleRepository.deleteById(id);
  }
}
