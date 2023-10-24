package com.innovationandtrust.profile.controller;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.innovationandtrust.profile.service.CompanySettingService;
import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

/** Company settings controller. */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "v1/company/settings", produces = APPLICATION_JSON_VALUE)
public class CompanySettingController {

  private final CompanySettingService companySettingService;

  @GetMapping
  @Tag(
      name = "Get company settings by uuid",
      description = "To get company settings detail by company's uuid")
  public ResponseEntity<List<CompanySettingDto>> getAll(
      @RequestParam(value = "uuid") String companyUuid) {
    return new ResponseEntity<>(this.companySettingService.getAll(companyUuid), HttpStatus.OK);
  }

  @PostMapping
  @Tag(
      name = "Create/Update company settings",
      description = "Create/Update company settings details")
  @PreAuthorize("hasRole('SUPER-ADMIN')")
  public ResponseEntity<List<CompanySettingDto>> save(
      @RequestBody List<CompanySettingDto> settingDtoList) {
    return new ResponseEntity<>(this.companySettingService.save(settingDtoList), HttpStatus.OK);
  }

  /**
   * to update a company setting, themes and logo.
   *
   * @param logoFile refers to logo file.
   * @param dto refers to company setting dto object.
   */
  @PutMapping("/themes")
  @PreAuthorize("hasRole('SUPER-ADMIN')")
  @Tag(name = "Update company themes", description = "To update company themes (logo, color)")
  public ResponseEntity<CorporateSettingDto> updateCompanyThemesAndLogo(
      @ModelAttribute("logoFile") MultipartFile logoFile, @ModelAttribute CorporateSettingDto dto) {
    return new ResponseEntity<>(
        this.companySettingService.updateCompanyThemesAndLogo(dto, logoFile), HttpStatus.OK);
  }

  @GetMapping("/{uuid}")
  @Tag(
      name = "Get company setting by company uuid",
      description = "To get company setting by company's uuid and level")
  public ResponseEntity<CompanySettingDto> getCompanySettingByLevel(
      @PathVariable("uuid") String companyUuid,
      @RequestParam("signatureLevel") String signatureLevel) {
    return new ResponseEntity<>(
        this.companySettingService.getSettingByLevel(companyUuid, signatureLevel), HttpStatus.OK);
  }
}
