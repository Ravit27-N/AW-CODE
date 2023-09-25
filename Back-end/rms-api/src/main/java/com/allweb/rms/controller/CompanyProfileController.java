package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.CompanyProfileDTO;
import com.allweb.rms.service.CompanyProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/company/profile")
public class CompanyProfileController {

  private final CompanyProfileService companyProfileService;

  @Autowired
  public CompanyProfileController(CompanyProfileService companyProfileService) {
    this.companyProfileService = companyProfileService;
  }

  @Operation(
      operationId = "createCompanyProfile",
      description = "create company profile",
      tags = {"Company Profile"})
  @PostMapping
  public ResponseEntity<CompanyProfileDTO> createCompanyProfile(
      @Valid @RequestBody CompanyProfileDTO companyProfile) {
    return new ResponseEntity<>(
        companyProfileService.createCompanyProfile(companyProfile), HttpStatus.OK);
  }

  @Operation(
      operationId = "updateCompanyProfile",
      description = "update company profile",
      tags = {"Company Profile"})
  @PutMapping
  public ResponseEntity<CompanyProfileDTO> updateCompanyProfile(
      @Valid @RequestBody CompanyProfileDTO companyProfile) {
    return new ResponseEntity<>(
        companyProfileService.updateCompanyProfile(companyProfile), HttpStatus.OK);
  }

  @Operation(
      operationId = "getCompanyProfile",
      description = "get company profile",
      tags = {"Company Profile"})
  @GetMapping
  public ResponseEntity<CompanyProfileDTO> getCompanyProfile() {
    return new ResponseEntity<>(companyProfileService.getCompanyProfile(), HttpStatus.OK);
  }

  @Operation(
      operationId = "uploadLogo",
      description = "upload logo of company profile",
      tags = {"Company Profile"})
  @PutMapping(value = "/upload/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadLogo(@RequestPart MultipartFile companyLogoUrl) {
    companyProfileService.uploadLogo(companyLogoUrl);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "loadCompanyLogo",
      description = "load or view logo of company profile",
      tags = {"Company Profile"})
  @GetMapping("/upload/view")
  public ResponseEntity<Resource> loadCompanyLogo(HttpServletRequest request) throws IOException {
    Resource resource = companyProfileService.loadCompanyLogo();
    String contentType =
        request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    // Fallback to the default content type if type could not be determined
    if (contentType == null) {
      contentType = "application/octet-stream"; // unknown binary file
    }
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
  }
}
