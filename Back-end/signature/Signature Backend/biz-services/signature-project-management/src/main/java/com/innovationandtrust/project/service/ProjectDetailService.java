package com.innovationandtrust.project.service;

import com.innovationandtrust.project.constant.ProjectDetailTypeConstant;
import com.innovationandtrust.project.model.dto.ProjectDetailDTO;
import com.innovationandtrust.project.model.entity.ProjectDetail;
import com.innovationandtrust.project.repository.ProjectDetailRepository;
import com.innovationandtrust.project.service.specification.ProjectDetailSpecification;
import com.innovationandtrust.share.service.AbstractCrudService;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ProjectDetailService
    extends AbstractCrudService<ProjectDetailDTO, ProjectDetail, Long> {
  private static final String PROJECT_DETAIL_NOT_FOUND = "Project detail Not Found!";
  private final ProjectDetailRepository projectDetailRepository;

  @Autowired
  public ProjectDetailService(
      ProjectDetailRepository projectDetailRepository, ModelMapper modelMapper) {
    super(modelMapper);
    this.projectDetailRepository = projectDetailRepository;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected ProjectDetail findEntityById(long id) {
    return this.projectDetailRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(PROJECT_DETAIL_NOT_FOUND));
  }

  /**
   * Retrieves an {@link ProjectDetailDTO} by its id.
   *
   * @param id must not be {@literal null}.
   * @return the {@link ProjectDetailDTO} with the given id or {@literal Optional#empty()} if none
   *     found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  @Override
  @Transactional(readOnly = true)
  public ProjectDetailDTO findById(Long id) {
    return this.mapData(findEntityById(id), new ProjectDetailDTO());
  }

  /**
   * Returns all instances of {@link ProjectDetailDTO}.
   *
   * @return all entities
   */
  @Override
  @Transactional(readOnly = true)
  public List<ProjectDetailDTO> findAll() {
    return this.projectDetailRepository.findAll().stream().map(this::mapData).toList();
  }

  /**
   * Insert new signatory record.
   *
   * @param projectDetailDTO refers to the object of {@link ProjectDetailDTO}
   * @return inserted record ProjectDetailDTO
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ProjectDetailDTO save(ProjectDetailDTO projectDetailDTO) {
    // check type name in projectDetailDTO
    String[] listAllTypes = {
      ProjectDetailTypeConstant.SIGNATORY,
      ProjectDetailTypeConstant.APPROVAL,
      ProjectDetailTypeConstant.RECIPIENT,
      ProjectDetailTypeConstant.VIEWER
    };
    if (!ArrayUtils.contains(listAllTypes, projectDetailDTO.getType())) {
      throw new EntityNotFoundException("Incorrect type!");
    }
    // find projectDetail by project id and type
    var projectDetail =
        this.projectDetailRepository.findOne(
            Specification.where(
                ProjectDetailSpecification.findByProjectIdAndType(
                    projectDetailDTO.getProjectId(), projectDetailDTO.getType())));

    projectDetail.ifPresent(detail -> projectDetailDTO.setId(detail.getId()));
    return this.mapData(this.projectDetailRepository.save(this.mapEntity(projectDetailDTO)));
  }

  /**
   * Update signatory record.
   *
   * @param projectDetailDTO that must be contained id
   * @return updated record ProjectDetailDTO
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ProjectDetailDTO update(ProjectDetailDTO projectDetailDTO) {
    return this.mapData(this.projectDetailRepository.save(this.mapEntity(projectDetailDTO)));
  }

  /**
   * Save all projectDetail.
   *
   * @param projectDetailDTOS refers to a list of projectDetailDTOS record
   * @return a list of projectDetailDTOS
   */
  @Transactional(rollbackFor = Exception.class)
  public List<ProjectDetailDTO> saveAll(List<ProjectDetailDTO> projectDetailDTOS) {
    return this.projectDetailRepository
        .saveAll(mapAll(projectDetailDTOS, ProjectDetail.class))
        .stream()
        .map(this::mapData)
        .toList();
  }

  /**
   * Find one project detail.
   *
   * @param type refers to name of type
   * @param projectId refers to an ID of a project
   * @return a record of projectDetailDTO
   */
  public ProjectDetailDTO findByType(String type, Long projectId) {
    return this.mapData(
        this.projectDetailRepository
            .findByTypeAndProjectId(type, projectId)
            .orElseThrow(
                () -> new EntityNotFoundException("Project detail with this type is not found!")));
  }
}
