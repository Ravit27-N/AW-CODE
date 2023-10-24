package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.model.dto.CompanyIdListDto;
import com.innovationandtrust.corporate.model.dto.CorporateSettingRequest;
import com.innovationandtrust.corporate.service.CorporateSettingService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import com.innovationandtrust.share.model.profile.Company;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/corporate/settings")
@RequiredArgsConstructor
public class CorporateSettingController {
  private final CorporateSettingService corporateSettingService;

  @GetMapping
  @Tag(name = "Get all corporate settings", description = "To get all corporate settings")
  public ResponseEntity<EntityResponseHandler<CorporateSettingDto>> list(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField) {

    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.corporateSettingService.findAll(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection))),
        HttpStatus.OK);
  }

  @PostMapping("/company")
  @Tag(
      name = "Get corporate setting by companies",
      description = "To get corporate setting by companies")
  public ResponseEntity<List<CorporateSettingDto>> findByCompany(
      @RequestBody CompanyIdListDto companies) {
    return new ResponseEntity<>(
        this.corporateSettingService.findByCompany(companies), HttpStatus.OK);
  }

  @PostMapping
  @Tag(name = "Create corporate setting", description = "To create corporate setting")
  public ResponseEntity<CorporateSettingDto> save(
      @Valid @ModelAttribute("logoFile") MultipartFile logoFile,
      @ModelAttribute @Valid CorporateSettingRequest corporateSettingRequest) {
    return new ResponseEntity<>(
        this.corporateSettingService.save(corporateSettingRequest, logoFile), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Tag(name = "Get corporate setting by ID", description = "To get corporate setting by ID")
  public ResponseEntity<CorporateSettingDto> findById(@PathVariable Long id) {
    return new ResponseEntity<>(this.corporateSettingService.findById(id), HttpStatus.OK);
  }

  @GetMapping("/company/{id}")
  @Tag(
      name = "Get corporate setting by company ID",
      description = "To get corporate setting by company ID")
  public ResponseEntity<List<CorporateSettingDto>> findByCompanyId(@PathVariable Long id) {
    return new ResponseEntity<>(this.corporateSettingService.findByCompanyId(id), HttpStatus.OK);
  }

  @GetMapping("/themes")
  @Tag(
      name = "Get corporate setting by user that logged in",
      description = "To get corporate setting by user that logged in")
  public ResponseEntity<Company> findOwnTheme() {
    return new ResponseEntity<>(
        this.corporateSettingService.getCorporateSettingByUser(null), HttpStatus.OK);
  }

  @GetMapping("/themes/{userId}")
  @Tag(name = "Get corporate setting by user", description = "To get corporate setting by user")
  public ResponseEntity<Company> findOwnTheme(@PathVariable("userId") Long userId) {
    return new ResponseEntity<>(
        this.corporateSettingService.getCorporateSettingByUser(userId), HttpStatus.OK);
  }

  // *** WILL BE REMOVED ***
  @GetMapping("/company/reset")
  @Tag(name = "Reset corporate setting", description = "To reset corporate setting")
  public ResponseEntity<List<CorporateSettingDto>> resetByCompany() {
    return new ResponseEntity<>(this.corporateSettingService.resetByCompany(), HttpStatus.OK);
  }

  @PutMapping("/save-or-update")
  @Tag(
      name = "Save or update corporate setting",
      description = "To save or update corporate setting")
  public ResponseEntity<CorporateSettingDto> update(
      @ModelAttribute("logoFile") MultipartFile logoFile,
      @ModelAttribute @Valid CorporateSettingDto dto) {
    return new ResponseEntity<>(
        this.corporateSettingService.saveOrUpdate(dto, logoFile), HttpStatus.OK);
  }

  /** This is internal use, Super admin update corporate setting. */
  @Hidden
  @PutMapping("/update")
  @PreAuthorize("hasRole('SUPER-ADMIN')")
  @Tag(name = "Update corporate setting", description = "Super admin update corporate setting")
  public ResponseEntity<CorporateSettingDto> updateSetting(
      @RequestBody @Valid CorporateSettingDto dto,
      @RequestParam(value = "oldFile") String oldFile) {
    return new ResponseEntity<>(
        this.corporateSettingService.updateSetting(dto, oldFile), HttpStatus.OK);
  }

  @Hidden
  @PostMapping("/levels")
  @PreAuthorize("hasRole('SUPER-ADMIN')")
  @Tag(name = "Save corporate setting", description = "Super admin save corporate setting")
  public ResponseEntity<Void> saveCompanySetting(
      @RequestBody List<CompanySettingDto> settingDtoList) {
    this.corporateSettingService.saveCompanySetting(settingDtoList);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/levels")
  @PreAuthorize("hasRole('CORPORATE-ADMIN')")
  @Tag(
      name = "Update setting option",
      description =
          "Example super admin provides two options for corporate admin to choose for their end-user, so corporate admin use this endpoint to update the setting option.")
  public ResponseEntity<List<CompanySettingDto>> updateCompanySettingMessages(
      @RequestBody List<CompanySettingDto> settingDtoList) {
    return new ResponseEntity<>(
        this.corporateSettingService.updateCompanySettings(settingDtoList), HttpStatus.OK);
  }

  @GetMapping("/levels")
  @Tag(
      name = "Get list of company settings",
      description = "To get list of company settings that is update false.")
  public ResponseEntity<List<CompanySettingDto>> getCompanySetting(
      @RequestParam(value = "uuid") String companyUuid) {
    return new ResponseEntity<>(
        this.corporateSettingService.getCompanySettings(companyUuid), HttpStatus.OK);
  }

  @GetMapping("/level/{uuid}")
  @Tag(
      name = "Get company setting by company uuid",
      description = "To get company settings by company's uuid")
  public ResponseEntity<CompanySettingDto> getCompanySettingByLevel(
      @PathVariable("uuid") String companyUuid,
      @RequestParam("signatureLevel") String signatureLevel) {
    return new ResponseEntity<>(
        this.corporateSettingService.getSettingByLevel(companyUuid, signatureLevel), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  @Tag(name = "Delete company setting", description = "To delete company setting.")
  public void delete(@PathVariable Long id) {
    this.corporateSettingService.delete(id);
  }
}
