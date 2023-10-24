package com.innovationandtrust.corporate.service;

import com.innovationandtrust.corporate.model.dto.CompanyEmployee;
import com.innovationandtrust.corporate.model.dto.CompanyIdListDto;
import com.innovationandtrust.corporate.model.entity.BusinessUnit;
import com.innovationandtrust.corporate.model.entity.BusinessUnit_;
import com.innovationandtrust.corporate.model.entity.CompanyDetail_;
import com.innovationandtrust.corporate.model.entity.Employee;
import com.innovationandtrust.corporate.model.entity.Employee_;
import com.innovationandtrust.corporate.repository.BusinessUnitRepository;
import com.innovationandtrust.corporate.repository.EmployeeRepository;
import com.innovationandtrust.corporate.repository.UserAccessRepository;
import com.innovationandtrust.corporate.service.restclient.ProfileFeignClient;
import com.innovationandtrust.corporate.service.restclient.ProjectFeignClient;
import com.innovationandtrust.corporate.service.specification.EmployeeSpecification;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeResponseDto;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
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

/** Employee logical. */
@Slf4j
@Service
public class EmployeeService extends CommonCrudService<EmployeeDTO, Employee, Long> {
  private final EmployeeRepository employeeRepository;
  private final ProjectFeignClient projectFeignClient;
  private final BusinessUnitRepository businessUnitRepository;
  private final UserAccessRepository userAccessRepository;
  private final ProfileFeignClient profileFeignClient;
  private final FolderService folderService;

  protected EmployeeService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      EmployeeRepository employeeRepository,
      ProjectFeignClient projectFeignClient,
      BusinessUnitRepository businessUnitRepository,
      UserAccessRepository userAccessRepository,
      ProfileFeignClient profileFeignClient,
      FolderService folderService) {
    super(modelMapper, keycloakProvider);
    this.employeeRepository = employeeRepository;
    this.projectFeignClient = projectFeignClient;
    this.businessUnitRepository = businessUnitRepository;
    this.userAccessRepository = userAccessRepository;
    this.profileFeignClient = profileFeignClient;
    this.folderService = folderService;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected Employee findEntityById(long id) {
    return employeeRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Employee with this id is not found!"));
  }

  /**
   * Find employee by id.
   *
   * @param id refers to employee's id
   * @return EmployeeDTO
   */
  @Override
  @Transactional(readOnly = true)
  public EmployeeDTO findById(Long id) {
    return mapData(this.findEntityById(id));
  }

  /**
   * Find all employees where active.
   *
   * @return a list of employeeDTO
   */
  @Override
  @Transactional(readOnly = true)
  public List<EmployeeDTO> findAll() {
    return this.mapAll(this.employeeRepository.findAll(), EmployeeDTO.class);
  }

  /**
   * Get all employees with pagination.
   *
   * @param pageable for pagination
   * @return list of employees with pagination.
   */
  @Transactional(readOnly = true)
  public Page<EmployeeDTO> findAll(
      Pageable pageable, Long businessUnitId, Long companyId, String unitName, String search) {

    return getEmployees(
        pageable, businessUnitId, companyId, unitName, search, false, RoleConstant.NORMAL_USER);
  }

  /**
   * Find all employee by corporate id.
   *
   * @param corporateId refers to a corporate id
   * @return a list of EmployeeDTO
   */
  @Transactional(readOnly = true)
  public List<EmployeeDTO> findAllByCorporateId(Long corporateId) {
    return this.mapAll(
        this.employeeRepository.findAll(
            Specification.where(EmployeeSpecification.findByCorporateId(corporateId))),
        EmployeeDTO.class);
  }

  /**
   * To get all employees with counted projects they created among a company, a business and between
   * date.
   *
   * @param pageable page to get
   * @param businessUnitId id of department
   * @param companyId company id
   * @param unitName unique department's name
   * @param search filter employees by their name
   * @return Page of EmployeeDto
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<EmployeeDTO> findEmployeeDashboard(
      Pageable pageable,
      Long businessUnitId,
      Long companyId,
      String unitName,
      String search,
      String startDate,
      String endDate) {

    var employeePage =
        getEmployees(pageable, businessUnitId, companyId, unitName, search, false, RoleConstant.NORMAL_USER);
    var response = new EntityResponseHandler<>(employeePage);

    if (!employeePage.isEmpty()) {

      var employeesDto = employeePage.stream().filter(e -> Objects.nonNull(e.getUserId())).toList();

      var employeesCountedProject =
          this.projectFeignClient.countEmployeesProject(employeesDto, startDate, endDate);

      var employeesLoginHistory =
          this.profileFeignClient.getLoginHistoriesByUsers(
              employeesDto.stream().map(EmployeeDTO::getUserId).toList());

      employeesCountedProject.forEach(
          employeeDto ->
              employeeDto.setLoginCount(
                  employeesLoginHistory.stream()
                      .filter(
                          history -> Objects.equals(employeeDto.getUserId(), history.getUserId()))
                      .count()));

      employeesCountedProject.sort(
          (empFirst, empSecond) ->
              Long.compare(empSecond.getTotalProjects(), empFirst.getTotalProjects()));
      response.setContents(employeesCountedProject);
    }

    return response;
  }

  /**
   * To get all employees among a company.
   *
   * @param pageable page to get
   * @param businessUnitId id of department
   * @param companyId company id
   * @param unitName unique department's name
   * @param search filter employees by their name
   * @return Page of EmployeeDto
   */
  public Page<EmployeeDTO> getEmployees(
      Pageable pageable,
      Long businessUnitId,
      Long companyId,
      String unitName,
      String search,
      Boolean isDeleted,
      String role) {

    var filters = new ArrayList<Filter>();
    filters.add(
        Filter.builder()
            .referenceField(Arrays.asList(BusinessUnit_.COMPANY_DETAIL, CompanyDetail_.COMPANY_ID))
            .value(companyId.toString())
            .operator(QueryOperator.EQUALS)
            .build());

    if (Objects.nonNull(businessUnitId) && businessUnitId != 0) {
      filters.add(
          Filter.builder()
              .field(BusinessUnit_.ID)
              .operator(QueryOperator.EQUALS)
              .value(businessUnitId.toString())
              .build());
    }

    if (StringUtils.hasText(unitName)) {
      filters.add(
          Filter.builder()
              .field(BusinessUnit_.UNIT_NAME)
              .operator(QueryOperator.LIKE)
              .value(unitName)
              .build());
    }

    Specification<BusinessUnit> spec = AdvancedFilter.searchByFields(filters);

    var businessUnitsIds =
        businessUnitRepository.findAll(spec).stream()
            .map(BusinessUnit::getId)
            .map(String::valueOf)
            .toList();

    filters.clear();
    filters.add(
        Filter.builder()
            .referenceField(Arrays.asList(Employee_.BUSINESS_UNIT, BusinessUnit_.ID))
            .operator(QueryOperator.IN)
            .values(businessUnitsIds)
            .build());

    if (Boolean.FALSE.equals(!isDeleted)) {
      filters.add(
          Filter.builder().field(Employee_.DELETED).operator(QueryOperator.IS_FALSE).build());
    }

    Specification<Employee> empSpec = AdvancedFilter.searchByFields(filters);

    if (StringUtils.hasText(search)) {
      empSpec = empSpec.and(EmployeeSpecification.search(search));
    }

    if (StringUtils.hasText(role)) {
      Page<EmployeeDTO> employees =
          new PageImpl<>(this.employeeRepository.findAll(empSpec)).map(super::mapData);

      var endUsersIds =
          this.profileFeignClient.getUserIdsByRole(
              role, employees.stream().map(EmployeeDTO::getUserId).toList());

      empSpec =
          empSpec.and(
              AdvancedFilter.searchByField(
                  Filter.builder()
                      .field(Employee_.USER_ID)
                      .operator(QueryOperator.IN)
                      .values(endUsersIds.stream().map(String::valueOf).toList())
                      .build()));
    }

    return pageable.isUnpaged()
        ? new PageImpl<>(this.employeeRepository.findAll(empSpec)).map(super::mapData)
        : this.employeeRepository.findAll(empSpec, pageable).map(super::mapData);
  }

  /**
   * Save an employee record.
   *
   * @param dto object.
   * @return EmployeeDTO.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmployeeDTO save(EmployeeDTO dto) {
    var userAccess =
        this.userAccessRepository
            .findById(dto.getUserAccessId())
            .orElseThrow(
                () -> new EntityNotFoundException("User access with this id is not found!"));
    var businessUnit =
        this.businessUnitRepository
            .findById(dto.getBusinessUnitId())
            .orElseThrow(
                () -> new EntityNotFoundException("Business Unit with this id is not found!"));
    var entity = this.mapEntity(dto);
    entity.setBusinessUnit(businessUnit);
    entity.setUserAccess(userAccess);
    entity.setCreatedBy(this.getUserId());
    var employee = this.employeeRepository.save(entity);
    return this.findById(employee.getId());
  }

  /**
   * Update an employee record.
   *
   * @param employeeDto object.
   * @return EmployeeDTO.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmployeeDTO update(EmployeeDTO employeeDto) {
    // find out the user is existing or not
    // if not existing, will create new one
    var userId = employeeDto.getUserId();
    var employee =
        this.employeeRepository
            .findByUserId(userId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "There are no employee with this user id." + userId));

    if (Objects.nonNull(employeeDto.getUserAccessId())) {
      var userAccess =
          this.userAccessRepository
              .findById(employeeDto.getUserAccessId())
              .orElseThrow(
                  () -> new EntityNotFoundException("User access with this id is not found!"));
      employee.setUserAccess(userAccess);
    }
    if (Objects.nonNull(employeeDto.getBusinessUnitId())
        && RoleConstant.isPrivilegeUser(this.getUserRoles())) {
      var businessUnit =
          this.businessUnitRepository
              .findById(employeeDto.getBusinessUnitId())
              .orElseThrow(
                  () -> new EntityNotFoundException("Business Unit with this id is not found!"));

      var companyId = employee.getBusinessUnit().getCompanyDetail().getCompanyId();
      if (!Objects.equals(businessUnit.getCompanyDetail().getCompanyId(), companyId)) {
        throw new InvalidRequestException("This business is not the same company.");
      }

      employee.setBusinessUnit(businessUnit);
      this.folderService.updateBusinessId(employeeDto.getUserId(), businessUnit.getId());
    }
    employee.setModifiedBy(this.getUserId());
    this.modelMapper.map(employeeDto, employee);
    return this.mapData(this.employeeRepository.save(employee));
  }

  /**
   * Delete an employee record by its id.
   *
   * @param id use for search the employee in a database
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    this.employeeRepository.deleteById(id);
  }

  /**
   * Find employee by company id.
   *
   * @param companyIdListDto refers to the list of company's id
   * @return a list of CompanyEmployee.
   */
  @Transactional(readOnly = true)
  public List<CompanyEmployee> findAllByCompanyId(CompanyIdListDto companyIdListDto) {
    var employees =
        this.employeeRepository.findAll(
            Specification.where(
                EmployeeSpecification.findByCompanyId(companyIdListDto.getCompanyIds())));
    return employees.stream()
        .map(
            emp -> {
              var dto = this.modelMapper.map(emp, CompanyEmployee.class);
              dto.setCompanyId(emp.getBusinessUnit().getCompanyDetail().getCompanyId());
              return dto;
            })
        .toList();
  }

  /**
   * Update employee to be deleted.
   *
   * @param id refers to the id of the user created to delete
   */
  @Transactional(rollbackFor = Exception.class)
  public void deleteUser(Long id) {
    this.employeeRepository
        .findByUserId(id)
        .ifPresent(
            employee -> {
              employee.setDeleted(true);
              this.employeeRepository.save(employee);
            });
  }

  /**
   * Get all employees among the business unit. Use an employee to get business id
   *
   * @param id refers to the id of the user created
   */
  public List<EmployeeDTO> findByUserId(Long id) {
    var employee =
        this.employeeRepository
            .findByUserId(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee is not found!"));
    var employees = this.findAllEmployeesByBusinessUnit(employee.getBusinessUnit().getId());
    return this.mapAll(employees, EmployeeDTO.class);
  }

  private List<Employee> findAllEmployeesByBusinessUnit(Long businessUnitId) {
    return this.employeeRepository.findAll(
        Specification.where(EmployeeSpecification.findByBusinessUnit(businessUnitId)));
  }

  public List<EmployeeResponseDto> findByUserIds(List<Long> userIds) {
    var employees =
        this.employeeRepository.findAll(
            Specification.where(EmployeeSpecification.findByUserIds(userIds)));

    return this.mapAll(employees, EmployeeResponseDto.class);
  }

  public EmployeeDTO findEmployeeByUserId(Long id) {
    var employee =
        this.employeeRepository
            .findByUserId(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Employee with this user id is not found!"));

    return this.mapData(employee);
  }
}
