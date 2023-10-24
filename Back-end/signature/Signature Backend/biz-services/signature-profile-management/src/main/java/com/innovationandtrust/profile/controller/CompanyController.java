package com.innovationandtrust.profile.controller;

import com.innovationandtrust.profile.model.dto.CompanyDto;
import com.innovationandtrust.profile.model.dto.LogoResponse;
import com.innovationandtrust.profile.service.CompanyService;
import com.innovationandtrust.profile.service.CreateCompanyAndApiFacadeService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

/** Company rest controller. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies")
public class CompanyController {
  private final CompanyService companyService;
  private final CreateCompanyAndApiFacadeService createCompanyAndApiFacadeService;


  @Tag(
          name = "Validate company's name",
          description = "To validate company's name when create new company")
  @GetMapping("/validate/name")
  public ResponseEntity<Boolean> validateName(@RequestParam("name") String name) {
    return ResponseEntity.ok(this.companyService.validateName(name));
  }

  @PostMapping(value = "/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Tag(name = "Upload logo", description = "Upload company logo")
  public ResponseEntity<LogoResponse> uploadLogo(@ModelAttribute MultipartFile logoFile) {
    return ResponseEntity.ok(this.companyService.uploadLogo(logoFile));
  }

  @PostMapping
  @Tag(name = "Create company", description = "First upload the logo, then create a company")
  @PreAuthorize("hasRole('SUPER-ADMIN')")
  public ResponseEntity<CompanyDto> save(@Valid @RequestBody CompanyDto dto) {
    return ResponseEntity.ok(this.createCompanyAndApiFacadeService.create(dto));
  }

  @PutMapping
  @Tag(name = "Update company", description = "Update company information")
  public ResponseEntity<CompanyDto> update(@Valid @RequestBody CompanyDto dto) {
    return ResponseEntity.ok(this.companyService.update(dto));
  }

  @GetMapping("/{id}")
  @Tag(name = "Get company by id", description = "To get company by company id")
  public ResponseEntity<CompanyDto> findById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.companyService.findById(id));
  }

  @GetMapping("/name")
  @Tag(name = "Get company by name", description = "To get company information by company's name")
  public ResponseEntity<CompanyDto> findByName(@RequestParam("companyName") String companyName) {
    return ResponseEntity.ok(this.companyService.findByName(companyName));
  }

  /** To get all companies registered. */
  @GetMapping
  @Tag(
      name = "Get companies",
      description = "To get companies list options(pageable, search by name)")
  public ResponseEntity<EntityResponseHandler<CompanyDto>> findAll(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "15") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id") String sortField,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String search) {
    return ResponseEntity.ok(
        new EntityResponseHandler<>(
            this.companyService.findAll(
                PageUtils.pageable(page, pageSize, sortField, sortDirection), search)));
  }

  @GetMapping("/all")
  @Tag(name = "Get all companies", description = "To get companies list")
  public ResponseEntity<List<CompanyDto>> listAll() {
    return ResponseEntity.ok(this.companyService.listAll());
  }

  @GetMapping("/corporate/{id}")
  @Tag(name = "Get company by admin id", description = "To get company by corporate admin id")
  public ResponseEntity<CompanyDto> findCorporateId(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.companyService.findByCorporateId(id));
  }
}
