package com.innovationandtrust.corporate.service;

import com.innovationandtrust.corporate.model.dto.TemplateDetailDto;
import com.innovationandtrust.corporate.model.entity.TemplateDetail;
import com.innovationandtrust.corporate.repository.TemplateDetailRepository;
import com.innovationandtrust.share.service.AbstractCrudService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TemplateDetailService
    extends AbstractCrudService<TemplateDetailDto, TemplateDetail, Long> {
  private static final String NOT_FOUND = "TemplateDetail Not Found!";
  private final TemplateDetailRepository templateDetailRepository;

  protected TemplateDetailService(
      ModelMapper modelMapper, TemplateDetailRepository templateDetailRepository) {
    super(modelMapper);
    this.templateDetailRepository = templateDetailRepository;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected TemplateDetail findEntityById(long id) {
    return templateDetailRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
  }

  /**
   * Retrieves a templateDetailDTO by its id.
   *
   * @param id must not be {@literal null}.
   * @return the templateDetailDTO with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  @Override
  @Transactional(readOnly = true)
  public TemplateDetailDto findById(Long id) {
    return mapData(this.findEntityById(id));
  }

  /**
   * Retrieves all templateDetailDTOS.
   *
   * @return the list of templatesDetailDTO
   */
  @Override
  @Transactional(readOnly = true)
  public List<TemplateDetailDto> findAll() {
    return this.mapAll(this.templateDetailRepository.findAll(), TemplateDetailDto.class);
  }

  /**
   * Retrieves a pagination of templateDetailDTO.
   *
   * @param pageable must not be {@literal null}.
   * @return a page of templateDetailDTO.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<TemplateDetailDto> findAll(Pageable pageable) {
    if (pageable.isUnpaged()) {
      return new PageImpl<>(this.findAll());
    }
    return templateDetailRepository.findAll(pageable).map(super::mapData);
  }

  /**
   * Insert templateDetailDTO
   *
   * @param templateDetailDTO refers to the templateDetailDTO object.
   * @return a record of templateDetailDTO.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public TemplateDetailDto save(TemplateDetailDto templateDetailDTO) {
    return mapData(this.templateDetailRepository.save(this.mapEntity(templateDetailDTO)));
  }

  /**
   * Update templateDetailDTO
   *
   * @param templateDetailDTO refers to the templateDetailDTO object.
   * @return a record of updated templateDetailDTO.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public TemplateDetailDto update(TemplateDetailDto templateDetailDTO) {
    // find out the id is passed or not
    var entityById = this.findEntityById(templateDetailDTO.getId());
    this.mapEntity(templateDetailDTO, entityById);
    return mapData(this.templateDetailRepository.save(entityById));
  }

  /**
   * Delete templateDetailDTO
   *
   * @param id refers templateDetail's id.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    this.templateDetailRepository.deleteById(id);
  }
}
