package com.innovationandtrust.profile.controller;

import com.innovationandtrust.profile.model.dto.CompanyDto;
import com.innovationandtrust.profile.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyPublicController {
  private final CompanyService companyService;

  @GetMapping("/name")
  @Tag(name = "Get company by name", description = "To get company information by company's name")
  public ResponseEntity<CompanyDto> findByName(@RequestParam("name") String name) {
    return ResponseEntity.ok(this.companyService.findByName(name));
  }

  @GetMapping("/uuid")
  @Tag(name = "Get company by uuid", description = "To get company information by company's uuid")
  public ResponseEntity<CompanyDto> findByUuid(@RequestParam("uuid") String uuid) {
    return ResponseEntity.ok(this.companyService.findByUuid(uuid));
  }
}
