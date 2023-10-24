package com.innovationandtrust.corporate.service;

import com.innovationandtrust.corporate.model.dto.BusinessUnitDto;
import com.innovationandtrust.corporate.model.entity.BusinessUnit;
import com.innovationandtrust.corporate.model.entity.BusinessUnit_;
import com.innovationandtrust.corporate.model.entity.CompanyDetail;
import com.innovationandtrust.corporate.model.entity.CompanyDetail_;
import com.innovationandtrust.corporate.model.entity.Employee_;
import com.innovationandtrust.corporate.repository.BusinessUnitRepository;
import com.innovationandtrust.corporate.service.restclient.ProjectFeignClient;
import com.innovationandtrust.corporate.service.specification.BusinessSpecification;
import com.innovationandtrust.share.model.corporateprofile.CompanyDetailDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** Business unit logical. */
@Slf4j
@Service
public class BusinessUnitService extends CommonCrudService<BusinessUnitDto, BusinessUnit, Long> {
  private static final String NOT_FOUND = "Business Unit Not Found!";
  private final BusinessUnitRepository businessUnitRepository;
  private final CompanyDetailService companyDetailService;
  private final ProjectFeignClient projectFeignClient;

  protected BusinessUnitService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      BusinessUnitRepository businessUnitRepository,
      CompanyDetailService companyDetailService,
      ProjectFeignClient projectFeignClient) {
    super(modelMapper, keycloakProvider);
    this.businessUnitRepository = businessUnitRepository;
    this.companyDetailService = companyDetailService;
    this.projectFeignClient = projectFeignClient;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected BusinessUnit findEntityById(long id) {
    return businessUnitRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
  }

  /**
   * Find businessUnit by its id.
   *
   * @param id refers to businessUnit's id that client wants to see.
   * @return BusinessUnitDto
   */
  @Override
  @Transactional(readOnly = true)
  public BusinessUnitDto findById(Long id) {
    return mapData(this.findEntityById(id));
  }

  @Transactional(readOnly = true)
  public List<BusinessUnitDto> findByIds(List<Long> ids) {
    return this.mapAll(
        this.businessUnitRepository.findAll(
            Specification.where(BusinessSpecification.findByIds(ids))),
        BusinessUnitDto.class);
  }

  /**
   * Find businessUnit by its parent id.
   *
   * @param id refers to businessUnit's parentId that client wants to see.
   * @return List of BusinessUnitDto
   */
  @Transactional(readOnly = true)
  public List<BusinessUnitDto> findByParentId(Long id) {
    return this.businessUnitRepository.findBusinessUnitByParentId(id).stream()
        .map(this::mapData)
        .toList();
  }

  /**
   * To list all businessUnits.
   *
   * @return a list of BusinessUnitDto
   */
  @Override
  public List<BusinessUnitDto> findAll() {
    return this.mapAll(this.businessUnitRepository.findAll(), BusinessUnitDto.class);
  }

  /**
   * List all businessUnit in pagination.
   *
   * @param pageable refers to parameter that uses to returns a page object.
   * @param filter refers to a string that the client wants to filter.
   * @param companyId refers to an id of the company that client wants to see its businessUnits.
   * @return a page of BusinessUnitDtoListRes.
   */
  @Transactional(readOnly = true)
  public Page<BusinessUnitDto> findAll(Pageable pageable, String filter, Long companyId) {
    return getDepartments(pageable, filter, companyId);
  }

  /**
   * To get all business unit among a company with counted project created by employees in that
   * business or company between date.
   *
   * @param pageable refers to parameter that uses to returns a page object.
   * @param filter for filter business unit by its name contain filter
   * @param companyId refers to company id
   * @return custom response
   */
  public Page<BusinessUnitDto> corporateDashboard(
      Pageable pageable, String filter, Long companyId, String startDate, String endDate) {
    Specification<BusinessUnit> spec = this.businessUnitSpec(filter, companyId);
    var departments =
        new ArrayList<>(
            this.businessUnitRepository.findAll(spec).stream().map(super::mapData).toList());

    List<EmployeeDTO> employeeDtoList =
        departments.stream()
            .map(businessUnit -> businessUnit.getEmployees().stream().toList())
            .flatMap(List::stream)
            .filter(employee -> Objects.nonNull(employee.getUserId()))
            .toList();

    if (!employeeDtoList.isEmpty()) {
      var employeeDtoListRes =
          this.projectFeignClient.countEmployeesProject(employeeDtoList, startDate, endDate);

      var companyProjects =
          employeeDtoListRes.stream().mapToLong(EmployeeDTO::getTotalProjects).sum();

      departments.forEach(
          businessUnit -> {
            var employees =
                employeeDtoListRes.stream()
                    .filter(
                        employeeDTO ->
                            Objects.equals(employeeDTO.getBusinessUnitId(), businessUnit.getId()))
                    .toList();
            long totalProjects = employees.stream().mapToLong(EmployeeDTO::getTotalProjects).sum();

            businessUnit.setEmployees(employees);
            businessUnit.setTotalProjects(totalProjects);
            businessUnit.setPercentage(((double) totalProjects * 100) / companyProjects);
          });
    }

    departments.sort(
        (unitFirst, unitSecond) ->
            Long.compare(unitSecond.getTotalProjects(), unitFirst.getTotalProjects()));

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), departments.size());
    return new PageImpl<>(departments.subList(start, end), pageable, departments.size());
  }

  private Specification<BusinessUnit> businessUnitSpec(String filter, Long companyId) {
    var filters = new ArrayList<Filter>();
    filters.add(
        Filter.builder()
            .referenceField(Arrays.asList(BusinessUnit_.COMPANY_DETAIL, CompanyDetail_.COMPANY_ID))
            .value(companyId.toString())
            .operator(QueryOperator.EQUALS)
            .build());

    filters.add(
        Filter.builder()
            .referenceField(Arrays.asList(BusinessUnit_.PARENT, BusinessUnit_.ID))
            .operator(QueryOperator.IS_NULL)
            .build());

    if (StringUtils.hasText(filter)) {
      filters.add(
          Filter.builder()
              .field(BusinessUnit_.UNIT_NAME)
              .operator(QueryOperator.LIKE)
              .value(filter)
              .build());
    }

    return AdvancedFilter.searchByFields(filters);
  }

  private Page<BusinessUnitDto> getDepartments(Pageable pageable, String filter, Long companyId) {
    Specification<BusinessUnit> spec = this.businessUnitSpec(filter, companyId);
    return this.businessUnitRepository.findAll(spec, pageable).map(super::mapData);
  }

  /**
   * To get all services in a company or in a department.
   *
   * @param pageable refers to parameter that uses to returns a page object
   * @param filter to filter services by its name contain filter String
   * @param companyId refers to company id
   * @param parentId refers to department id user want to search in
   * @return page of Business unit dto
   */
  public Page<BusinessUnitDto> getServices(
      Pageable pageable, String filter, Long companyId, Long parentId) {
    var filters = new ArrayList<Filter>();
    filters.add(
        Filter.builder()
            .referenceField(Arrays.asList(BusinessUnit_.COMPANY_DETAIL, CompanyDetail_.COMPANY_ID))
            .value(companyId.toString())
            .operator(QueryOperator.EQUALS)
            .build());

    if (Objects.nonNull(parentId)) {
      filters.add(
          Filter.builder()
              .referenceField(Arrays.asList(BusinessUnit_.PARENT, BusinessUnit_.ID))
              .operator(QueryOperator.EQUALS)
              .value(parentId.toString())
              .build());
    } else {
      filters.add(
          Filter.builder()
              .referenceField(Arrays.asList(BusinessUnit_.PARENT, BusinessUnit_.ID))
              .operator(QueryOperator.IS_NOT_NULL)
              .build());
    }

    if (StringUtils.hasText(filter)) {
      filters.add(
          Filter.builder()
              .field(BusinessUnit_.UNIT_NAME)
              .operator(QueryOperator.LIKE)
              .value(filter)
              .build());
    }

    Specification<BusinessUnit> spec = AdvancedFilter.searchByFields(filters);

    return this.businessUnitRepository.findAll(spec, pageable).map(super::mapData);
  }

  /**
   * Save an entity record.
   *
   * @param businessUnitDto object @Conditions check parentId and companyDetailId
   * @return BusinessUnitDto
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public BusinessUnitDto save(BusinessUnitDto businessUnitDto) {
    var entity = this.mapEntity(businessUnitDto);

    // Check if businessUnitDTO has parentId
    if (businessUnitDto.getParentId() != null) {
      var parent = new BusinessUnit();
      parent.setId(businessUnitDto.getParentId());
      entity.setParent(parent);
    }

    // Check if businessUnitDTO has companyDetailId
    if (businessUnitDto.getCompanyDetailId() != null) {
      var companyDetail = new CompanyDetail();
      companyDetail.setId(businessUnitDto.getCompanyDetailId());
      entity.setCompanyDetail(companyDetail);
    }
    return mapData(this.businessUnitRepository.save(entity));
  }

  /**
   * To create new business unit (Department or services).
   *
   * @param businessUnitDto refers object to save to database
   * @return saved business unit
   */
  @Transactional(rollbackFor = Exception.class)
  public BusinessUnitDto create(BusinessUnitDto businessUnitDto) {
    businessUnitDto.setCreatedBy(this.getUserId());
    CompanyDetailDTO companyDetailDto =
        companyDetailService.findByCompanyId(businessUnitDto.getCompanyId());
    businessUnitDto.setCompanyDetailId(companyDetailDto.getId());
    return this.save(businessUnitDto);
  }

  /**
   * Update BusinessUnit.
   *
   * @param businessUnitDto refers to businessUnitDTO data
   * @return BusinessUnitDto
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public BusinessUnitDto update(BusinessUnitDto businessUnitDto) {
    var businessUnit = this.findEntityById(businessUnitDto.getId());
    businessUnit.setModifiedBy(this.getUserId());
    businessUnit.setSortOrder(businessUnitDto.getSortOrder());
    if (businessUnitDto.getCompanyId() != null) {
      CompanyDetailDTO companyDetailDto =
          companyDetailService.findByCompanyId(businessUnitDto.getCompanyId());
      businessUnit.setCompanyDetail(this.modelMapper.map(companyDetailDto, CompanyDetail.class));
    }
    return this.mapData(this.businessUnitRepository.save(businessUnit));
  }

  /**
   * Get business unit by its id.
   *
   * @param id id of business unit (Department or service) to find.
   * @return business unit dto
   */
  @Transactional(rollbackFor = Exception.class)
  public BusinessUnitDto findByUserId(Long id) {
    var businessUnit =
        this.businessUnitRepository.findOne(
            AdvancedFilter.searchByField(
                Filter.builder()
                    .referenceField(Arrays.asList(BusinessUnit_.EMPLOYEES, Employee_.USER_ID))
                    .value(id.toString())
                    .operator(QueryOperator.EQUALS)
                    .build()));
    return businessUnit.map(this::mapData).orElse(null);
  }
}
