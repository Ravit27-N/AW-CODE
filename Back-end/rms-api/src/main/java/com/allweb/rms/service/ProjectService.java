package com.allweb.rms.service;

import com.allweb.rms.component.ProjectModelAssembler;
import com.allweb.rms.entity.dto.ProjectDTO;
import com.allweb.rms.entity.jpa.Project;
import com.allweb.rms.exception.CandidateStatusNotFoundException;
import com.allweb.rms.exception.ProjectNameConflictException;
import com.allweb.rms.exception.ProjectNotFoundException;
import com.allweb.rms.repository.jpa.DemandRepository;
import com.allweb.rms.repository.jpa.ProjectRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.google.common.base.Strings;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProjectService {
  private final ProjectRepository projectRepository;

  private final DemandRepository demandRepository;
  private final ModelMapper modelMapper;
  private final ProjectModelAssembler assembler;

  @Autowired
  private ProjectService(
      ProjectRepository projectRepository,
      ModelMapper modelMapper,
      ProjectModelAssembler projectModelAssembler,
      DemandRepository demandRepository) {
    this.projectRepository = projectRepository;
    this.demandRepository = demandRepository;
    this.modelMapper = modelMapper;
    this.assembler = projectModelAssembler;
  }

  /**
   * @param projectDTO
   * @return
   */
  public ProjectDTO createProject(ProjectDTO projectDTO) {
    if (validateName(projectDTO.getName()) != 0) {
      throw new ProjectNameConflictException();
    }
    return convertToDTO(projectRepository.save(convertToEntity(projectDTO)));
  }

  public Long validateName(String name) {
    return projectRepository.validateName(name);
  }

  /**
   * @param id
   * @return
   */
  public ProjectDTO getProjectById(int id) {
    Project project =
        projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
    return convertToDTO(project);
  }

  /**
   * @param projectDTO
   * @return
   */
  public ProjectDTO updateProject(ProjectDTO projectDTO) {
    Project project =
        projectRepository
            .findById(projectDTO.getId())
            .orElseThrow(() -> new ProjectNotFoundException(projectDTO.getId()));
    if (validateUpdateName(projectDTO.getId(), projectDTO.getName()) != 0) {
      throw new ProjectNameConflictException();
    }
    project.setName(projectDTO.getName());
    project.setDescription(projectDTO.getDescription());
    return convertToDTO(projectRepository.save(project));
  }

  public Long validateUpdateName(int id, String name) {
    return projectRepository.validateNameOnUpdate(id, name);
  }

  /**
   * @param id
   * @param active
   * @return
   */
  public ProjectDTO updateActiveProject(int id, boolean active) {
    Project project =
        projectRepository.findById(id).orElseThrow(() -> new CandidateStatusNotFoundException(id));
    project.setId(id);
    project.setActive(active);
    return convertToDTO(projectRepository.save(project));
  }

  /**
   * To delete project as soft.
   *
   * @param id refer id of {@link Project}
   * @param isDelete refer to object for updating record of project.
   * @return - object of {@link ProjectDTO}
   */
  public ProjectDTO softDeleteProject(int id, boolean isDelete) {
    Project project =
        projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
    project.setDeleted(isDelete);
    project.setId(id);
    return convertToDTO(projectRepository.save(project));
  }

  /**
   * @param id
   * @throws NotFoundException
   */
  public void hardDeleteProject(int id) {
    int demandId = demandRepository.findProjectNameById(id);
    if (demandId != 0) {
      throw new ProjectNotFoundException(id);
    }
    projectRepository.deleteById(id);
  }

  /**
   * @param page
   * @param pageSize
   * @param filter
   * @param sortDirection
   * @param sortByField
   * @param active
   * @return 1. findAll with active = true 2. findAll non filter with isDeleted = true 3. findAll
   *     non filter with isDeleted = false 4. findAll with isDeleted = true and filter 5. findAll
   *     with isDeleted = false and filter
   */
  public EntityResponseHandler<EntityModel<ProjectDTO>> getAllProject(
      int page,
      int pageSize,
      boolean isDeleted,
      String filter,
      String sortDirection,
      String sortByField,
      boolean active) {
    Pageable pageable =
        PageRequest.of(
            page - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    if (active) {
      return new EntityResponseHandler<>(
          projectRepository
              .findAllByActiveIsTrue(pageable)
              .map(entity -> assembler.toModel(modelMapper.map(entity, ProjectDTO.class))));
    }
    if (isDeleted && Strings.isNullOrEmpty(filter)) {
      return new EntityResponseHandler<>(
          projectRepository
              .findAllByDeletedIsTrue(pageable)
              .map(entity -> assembler.toModel(modelMapper.map(entity, ProjectDTO.class))));
    } else {
      if (!isDeleted && Strings.isNullOrEmpty(filter))
        return new EntityResponseHandler<>(
            projectRepository
                .findAllByDeletedIsFalse(pageable)
                .map(entity -> assembler.toModel(modelMapper.map(entity, ProjectDTO.class))));
    }
    if (isDeleted)
      return new EntityResponseHandler<>(
          projectRepository
              .findAllByNameContainingAndDeletedIsTrue(filter.toLowerCase(), pageable)
              .map(entity -> assembler.toModel(modelMapper.map(entity, ProjectDTO.class))));
    else {
      return new EntityResponseHandler<>(
          projectRepository
              .findAllByNameContaining(filter.toLowerCase(), pageable)
              .map(entity -> assembler.toModel(modelMapper.map(entity, ProjectDTO.class))));
    }
  }

  private Project convertToEntity(ProjectDTO projectDTO) {
    return modelMapper.map(projectDTO, Project.class);
  }

  private ProjectDTO convertToDTO(Project project) {
    return modelMapper.map(project, ProjectDTO.class);
  }
}
