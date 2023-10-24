package com.innovationandtrust.project.service;

import com.innovationandtrust.project.model.dto.ProjectHistoryDTO;
import com.innovationandtrust.project.model.entity.Project;
import com.innovationandtrust.project.model.entity.ProjectHistory;
import com.innovationandtrust.project.repository.ProjectHistoryRepository;
import com.innovationandtrust.share.service.AbstractCrudService;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ProjectHistoryService
    extends AbstractCrudService<ProjectHistoryDTO, ProjectHistory, Long> {

  private static final String NOT_FOUND = "Project history Not Found!";
  private final ProjectHistoryRepository projectHistoryRepository;

  @Autowired
  public ProjectHistoryService(
      ProjectHistoryRepository projectHistoryRepository, ModelMapper modelMapper) {
    super(modelMapper);
    this.projectHistoryRepository = projectHistoryRepository;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected ProjectHistory findEntityById(long id) {
    return this.projectHistoryRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
  }

  /**
   * Retrieves an {@link ProjectHistoryDTO} by its id.
   *
   * @param id must not be {@literal null}.
   * @return the {@link ProjectHistoryDTO} with the given id or {@literal Optional#empty()} if none
   *     found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  @Override
  @Transactional(readOnly = true)
  public ProjectHistoryDTO findById(Long id) {
    return this.mapData(findEntityById(id), new ProjectHistoryDTO());
  }

  /**
   * Returns all instances of {@link ProjectHistoryDTO}.
   *
   * @return all entities
   */
  @Override
  @Transactional(readOnly = true)
  public List<ProjectHistoryDTO> findAll() {
    return this.projectHistoryRepository.findAll().stream()
        .map(e -> this.mapData(e, new ProjectHistoryDTO()))
        .toList();
  }

  /**
   * Insert new signatory record.
   *
   * @param projectHistoryDto refers to the object of {@link ProjectHistoryDTO}
   * @return inserted record ProjectDetailDTO
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ProjectHistoryDTO save(ProjectHistoryDTO projectHistoryDto) {
    var maxOrder = projectHistoryRepository.findMaxSortOrder(projectHistoryDto.getProjectId());
    maxOrder = Objects.nonNull(maxOrder) ? maxOrder : 0;
    projectHistoryDto.setSortOrder((int) (maxOrder + 1));
    var project = new Project();
    project.setId(projectHistoryDto.getProjectId());

    var history = this.mapEntity(projectHistoryDto);
    history.setProject(project);

    return this.mapData(this.projectHistoryRepository.save(history));
  }

  /**
   * Update signatory record.
   *
   * @param projectHistoryDTO that must be contained id
   * @return updated record ProjectDetailDTO
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ProjectHistoryDTO update(ProjectHistoryDTO projectHistoryDTO) {
    return this.mapData(this.projectHistoryRepository.save(this.mapEntity(projectHistoryDTO)));
  }

  @Transactional(rollbackFor = Exception.class)
  public List<ProjectHistoryDTO> saveAll(List<ProjectHistoryDTO> projectHistoryDTOS) {
    var resultEntity =
        this.projectHistoryRepository.saveAll(mapAll(projectHistoryDTOS, ProjectHistory.class));
    return mapAll(resultEntity, ProjectHistoryDTO.class);
  }
}
