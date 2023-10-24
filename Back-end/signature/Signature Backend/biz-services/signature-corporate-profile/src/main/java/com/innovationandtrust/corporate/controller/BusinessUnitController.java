package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.model.dto.BusinessUnitDto;
import com.innovationandtrust.corporate.service.BusinessUnitService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
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
@RequestMapping("/v1/business-units")
@RequiredArgsConstructor
public class BusinessUnitController {

  private final BusinessUnitService businessUnitService;

  @GetMapping
  @Tag(name = "Get all business unit", description = "To get all business unit")
  public ResponseEntity<EntityResponseHandler<BusinessUnitDto>> findAll(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField,
      @RequestParam(value = "companyId") Long companyId) {

    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.businessUnitService.findAll(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection), filter, companyId)),
        HttpStatus.OK);
  }

  @GetMapping("/dashboard")
  @Tag(
      name = "Get business units for corporate dashboard",
      description = "To get business units for corporate dashboard")
  public ResponseEntity<EntityResponseHandler<BusinessUnitDto>> dashboard(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField,
      @RequestParam(value = "companyId") Long companyId,
      @RequestParam(value = CommonParamsConstant.START_DATE) String startDate,
      @RequestParam(value = CommonParamsConstant.END_DATE) String endDate) {
    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.businessUnitService.corporateDashboard(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection),
                filter,
                companyId,
                startDate,
                endDate)),
        HttpStatus.OK);
  }

  @GetMapping("/services")
  @Tag(name = "Get services in department", description = "To get services in department")
  public ResponseEntity<EntityResponseHandler<BusinessUnitDto>> services(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField,
      @RequestParam(value = "companyId") Long companyId,
      @RequestParam(value = "parentId", required = false) Long parentId) {

    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.businessUnitService.getServices(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection),
                filter,
                companyId,
                parentId)),
        HttpStatus.OK);
  }

  @PostMapping
  @Tag(name = "Create business unit", description = "To create business unit")
  public ResponseEntity<BusinessUnitDto> save(@RequestBody @Valid BusinessUnitDto dto) {
    return new ResponseEntity<>(this.businessUnitService.create(dto), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Tag(name = "Get business unit by id", description = "To get business unit by id")
  public ResponseEntity<BusinessUnitDto> findById(@PathVariable Long id) {
    return new ResponseEntity<>(this.businessUnitService.findById(id), HttpStatus.OK);
  }

  @GetMapping("/parent/{id}")
  @Tag(name = "Get business unit by parent id", description = "To get business unit by parent id")
  public ResponseEntity<List<BusinessUnitDto>> findByParentId(@PathVariable Long id) {
    return new ResponseEntity<>(this.businessUnitService.findByParentId(id), HttpStatus.OK);
  }

  @PutMapping
  @Tag(name = "Update business unit", description = "To update business unit")
  public ResponseEntity<BusinessUnitDto> update(@RequestBody @Valid BusinessUnitDto dto) {
    return new ResponseEntity<>(this.businessUnitService.update(dto), HttpStatus.OK);
  }

  @GetMapping("/user/{id}")
  @Tag(name = "Get business unit by user id", description = "To get business unit by user id")
  public ResponseEntity<BusinessUnitDto> findByUserId(@PathVariable Long id) {
    return new ResponseEntity<>(this.businessUnitService.findByUserId(id), HttpStatus.OK);
  }
}
