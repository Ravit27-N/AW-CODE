package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.service.CompanyDetailService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.corporateprofile.CompanyDetailDTO;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/v1/company/details")
@RequiredArgsConstructor
public class CompanyDetailController {
  private final CompanyDetailService companyDetailService;

  @GetMapping
  @Tag(name = "Get all company details", description = "To get all company details")
  public ResponseEntity<EntityResponseHandler<CompanyDetailDTO>> list(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField) {

    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.companyDetailService.findAll(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection))),
        HttpStatus.OK);
  }

  @PostMapping
  @Tag(name = "Create company detail", description = "To create company detail")
  public ResponseEntity<CompanyDetailDTO> save(@RequestBody @Valid CompanyDetailDTO dto) {
    return new ResponseEntity<>(this.companyDetailService.save(dto), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Tag(name = "Get company detail by id", description = "To get company detail by id")
  public ResponseEntity<CompanyDetailDTO> findById(@PathVariable Long id) {
    return new ResponseEntity<>(this.companyDetailService.findById(id), HttpStatus.OK);
  }

  @PutMapping
  @Tag(name = "Update company detail", description = "To update company detail")
  public ResponseEntity<CompanyDetailDTO> update(@RequestBody @Valid CompanyDetailDTO dto) {
    return new ResponseEntity<>(this.companyDetailService.update(dto), HttpStatus.OK);
  }

  @GetMapping("/business-unit/{id}")
  @Tag(
      name = "Get company detail by business unit id",
      description = "To get company detail by business unit id")
  public ResponseEntity<Long> findByBusinessUnitId(@PathVariable("id") Long businessUnitId) {
    return new ResponseEntity<>(
        this.companyDetailService.getCompanyIdByBusinessUnitId(businessUnitId), HttpStatus.OK);
  }

  @GetMapping("/info/{userId}")
  @Tag(name = "Get corporate info", description = "To get corporate info")
  public ResponseEntity<CorporateInfo> findCorporateInfo(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(this.companyDetailService.findCorporateInfo(userId));
  }

  @GetMapping("/company/{id}")
  @Tag(
      name = "Get company detail by company id",
      description = "To get company detail by company id")
  public ResponseEntity<CompanyDetailDTO> findByCompanyId(@PathVariable Long id) {
    return ResponseEntity.ok(this.companyDetailService.findByCompanyId(id));
  }
}
