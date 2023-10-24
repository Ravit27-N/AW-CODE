package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.model.dto.CompanyEmployee;
import com.innovationandtrust.corporate.model.dto.CompanyIdListDto;
import com.innovationandtrust.corporate.service.EmployeeService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeResponseDto;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
  private final EmployeeService employeeService;

  @GetMapping("/user/ids")
  @Tag(name = "Get employees by user ids", description = "To get employees by user ids.")
  public ResponseEntity<List<EmployeeResponseDto>> getEmployeesByUserIds(
      @RequestParam(value = "userIds") List<Long> userIds) {
    return new ResponseEntity<>(this.employeeService.findByUserIds(userIds), HttpStatus.OK);
  }

  @GetMapping
  @Tag(name = "Get all employees", description = "To get all employees.")
  public ResponseEntity<EntityResponseHandler<EmployeeDTO>> list(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField,
      @RequestParam(value = "businessUnitId", required = false) Long businessUnitId,
      @RequestParam(value = "companyId") Long companyId,
      @RequestParam(value = "unitName", defaultValue = "") String unitName,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter) {

    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.employeeService.findAll(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection),
                businessUnitId,
                companyId,
                unitName,
                filter)),
        HttpStatus.OK);
  }

  @GetMapping("/dashboard")
  @Tag(
      name = "Get all employees display on dashboard",
      description = "To get all employees on dashboard.")
  public ResponseEntity<EntityResponseHandler<EmployeeDTO>> dashboard(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField,
      @RequestParam(value = "businessUnitId", required = false) Long businessUnitId,
      @RequestParam(value = "companyId") Long companyId,
      @RequestParam(value = "unitName", defaultValue = "") String unitName,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.START_DATE) String startDate,
      @RequestParam(value = CommonParamsConstant.END_DATE) String endDate) {

    return new ResponseEntity<>(
        this.employeeService.findEmployeeDashboard(
            PageUtils.pageable(page, pageSize, sortByField, sortDirection),
            businessUnitId,
            companyId,
            unitName,
            filter,
            startDate,
            endDate),
        HttpStatus.OK);
  }

  @PostMapping
  @Tag(name = "Create employee", description = "To create employee.")
  public ResponseEntity<EmployeeDTO> save(@RequestBody @Valid EmployeeDTO dto) {
    return new ResponseEntity<>(this.employeeService.save(dto), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Tag(name = "Get employee by id", description = "To get employee by id.")
  public ResponseEntity<EmployeeDTO> findById(@PathVariable Long id) {
    return new ResponseEntity<>(this.employeeService.findById(id), HttpStatus.OK);
  }

  @PutMapping
  @Tag(name = "Update employee", description = "To update employee.")
  public ResponseEntity<EmployeeDTO> update(@RequestBody @Valid EmployeeDTO dto) {
    return new ResponseEntity<>(this.employeeService.update(dto), HttpStatus.CREATED);
  }

  @GetMapping("/corporate/{id}")
  @Tag(name = "Get employees by corporate id", description = "To get employees by corporate id.")
  public ResponseEntity<List<EmployeeDTO>> listByCorporate(@PathVariable Long id) {
    return new ResponseEntity<>(this.employeeService.findAllByCorporateId(id), HttpStatus.OK);
  }

  @PostMapping("/company")
  @Tag(
      name = "Get company employee by list of companies id",
      description = "To get company employee by list of companies id.")
  public ResponseEntity<List<CompanyEmployee>> listByCompany(@RequestBody CompanyIdListDto dto) {
    return new ResponseEntity<>(this.employeeService.findAllByCompanyId(dto), HttpStatus.OK);
  }

  @Hidden
  @PutMapping("/{id}")
  @Tag(name = "Delete employee", description = "To delete employee.")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    this.employeeService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/user/{id}")
  @Tag(name = "Get employees by user", description = "To get employees by user.")
  public ResponseEntity<List<EmployeeDTO>> getEmployeesByUser(@PathVariable Long id) {
    return new ResponseEntity<>(this.employeeService.findByUserId(id), HttpStatus.OK);
  }

  @GetMapping("/user")
  @Tag(name = "Get employee by user id", description = "To get employee data by user id")
  public ResponseEntity<EmployeeDTO> getEmployeeByUserId(@RequestParam Long id) {
    return new ResponseEntity<>(this.employeeService.findEmployeeByUserId(id), HttpStatus.OK);
  }
}
