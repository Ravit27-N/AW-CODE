package com.innovationandtrust.corporate.service;

import com.innovationandtrust.corporate.model.entity.CompanyDetail;
import com.innovationandtrust.corporate.model.entity.CorporateSetting;
import com.innovationandtrust.corporate.repository.CompanyDetailRepository;
import com.innovationandtrust.corporate.repository.CorporateSettingRepository;
import com.innovationandtrust.corporate.service.specification.CompanyDetailSpecification;
import com.innovationandtrust.share.model.corporateprofile.CompanyDetailDTO;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.service.AbstractCrudService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class CompanyDetailService
    extends AbstractCrudService<CompanyDetailDTO, CompanyDetail, Long> {
  private static final String NOT_FOUND = "CompanyDetail Not Found!";
  private final CompanyDetailRepository companyDetailRepository;
  private final CorporateSettingRepository corporateSettingRepository;

  protected CompanyDetailService(
      ModelMapper modelMapper,
      CompanyDetailRepository companyDetailRepository,
      CorporateSettingRepository corporateSettingRepository) {
    super(modelMapper);
    this.companyDetailRepository = companyDetailRepository;
    this.corporateSettingRepository = corporateSettingRepository;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected CompanyDetail findEntityById(long id) {
    return companyDetailRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
  }

  /**
   * Find CompanyDetailDTO by its id.
   *
   * @param id refers to companyDetail's id that client wants to see.
   * @return CompanyDetailDTO
   */
  @Override
  @Transactional(readOnly = true)
  public CompanyDetailDTO findById(Long id) {
    return mapData(this.findEntityById(id));
  }

  /**
   * Find CompanyDetailDTO by company's id.
   *
   * @param companyId refers to the company's id that client wants to see.
   * @return CompanyDetailDTO
   */
  @Transactional(readOnly = true)
  public CompanyDetailDTO findByCompanyId(Long companyId) {
    CompanyDetail companyDetail =
        this.companyDetailRepository
            .findAll(Specification.where(CompanyDetailSpecification.findByCompanyId(companyId)))
            .stream()
            .min(Comparator.comparingLong(CompanyDetail::getId))
            .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
    return mapData(companyDetail);
  }

  /**
   * List all company details.
   *
   * @return a list of CompanyDetailDTO
   */
  @Override
  @Transactional(readOnly = true)
  public List<CompanyDetailDTO> findAll() {
    return this.mapAll(this.companyDetailRepository.findAll(), CompanyDetailDTO.class);
  }

  /**
   * List all companyDetailDTO in pagination
   *
   * @param pageable refers to parameter that uses to returns a page object.
   * @return a page of companyDetailDTO.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<CompanyDetailDTO> findAll(Pageable pageable) {
    if (pageable.isUnpaged()) {
      return new PageImpl<>(this.findAll());
    }
    return companyDetailRepository.findAll(pageable).map(super::mapData);
  }

  /**
   * Create companyDetailDTO.
   *
   * @param companyDetailDTO refers an object of companyDetailDTO that request to create a company
   *     detail.
   * @return CompanyDetailDTO
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public CompanyDetailDTO save(CompanyDetailDTO companyDetailDTO) {
    var detail = this.findEntityByCompanyId(companyDetailDTO.getCompanyId());
    if (detail.isEmpty()) {
      // save a default company setting
      this.corporateSettingRepository.save(
          new CorporateSetting(
              companyDetailDTO.getCompanyId(), companyDetailDTO.getFilename(), true));

      return mapData(this.companyDetailRepository.save(this.mapEntity(companyDetailDTO)));
    }
    return detail.map(super::mapData).orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
  }

  /**
   * Update companyDetailDTO.
   *
   * @param companyDetailDTO refers an object of companyDetailDTO that request to update a company
   *     detail.
   * @return CompanyDetailDTO
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public CompanyDetailDTO update(CompanyDetailDTO companyDetailDTO) {
    // find out the id is passed or not
    var companyDetail = this.findEntityByCompanyId(companyDetailDTO.getCompanyId());
    if (companyDetail.isPresent()) {
      this.mapEntity(companyDetailDTO, companyDetail.get());
      return this.mapData(this.companyDetailRepository.save(companyDetail.get()));
    }
    return this.mapData(this.companyDetailRepository.save(this.mapEntity(companyDetailDTO)));
  }

  private Optional<CompanyDetail> findEntityByCompanyId(Long companyId) {
    return this.companyDetailRepository
        .findAll(Specification.where(CompanyDetailSpecification.findByCompanyId(companyId)))
        .stream()
        .min(Comparator.comparingLong(CompanyDetail::getId));
  }

  /**
   * Retrieved company detail by employee id.
   *
   * @param employeeId refers an employee's id
   * @return CompanyDetailDTO
   */
  public CompanyDetailDTO getCompanyDetailByEmployeeId(Long employeeId) {
    var companyDetail =
        this.companyDetailRepository
            .findOne(Specification.where(CompanyDetailSpecification.findUserId(employeeId)))
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "This user does not in any company, please check your flow"));
    return this.mapData(companyDetail);
  }

  /**
   * Retrieved company detail by business unit id.
   *
   * @param businessUnitId refers to business unit's id
   * @return CompanyDetailDTO
   */
  @Transactional(readOnly = true)
  public Long getCompanyIdByBusinessUnitId(Long businessUnitId) {
    var companyDetail =
        this.companyDetailRepository.findOne(
            Specification.where(CompanyDetailSpecification.findByBusinessUnitId(businessUnitId)));
    return companyDetail.map(CompanyDetail::getCompanyId).orElse(null);
  }

  @Transactional(readOnly = true)
  public CorporateInfo findCorporateInfo(Long userId) {
    return this.companyDetailRepository.findCorporateInfo(userId).stream()
        .max(Comparator.comparing(CorporateInfo::getSettingId))
        .orElse(new CorporateInfo());
  }
}
