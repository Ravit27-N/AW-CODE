package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.model.DocumentContent;
import com.innovationandtrust.corporate.service.CorporateSettingService;
import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/corporate-settings")
@RequiredArgsConstructor
public class CorporateSettingPublicController {
  private final CorporateSettingService corporateSettingService;

  @GetMapping("/company")
  @Tag(
      name = "Get corporate setting by company ID",
      description = "To get corporate setting by company ID.")
  public ResponseEntity<List<CorporateSettingDto>> findByCompanyId(
      @RequestParam(value = "uuid", defaultValue = "") String uuid) {
    return new ResponseEntity<>(
        this.corporateSettingService.findByCompanyUuid(uuid), HttpStatus.OK);
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Tag(name = "Upload file", description = "To upload file.")
  public ResponseEntity<String> uploadFile(@ModelAttribute("file") MultipartFile file) {
    return ResponseEntity.ok().body(this.corporateSettingService.uploadFile(file));
  }

  @GetMapping("/view")
  @Tag(name = "View file as base64", description = "To view file as base64.")
  public ResponseEntity<String> viewFile(
      @RequestParam(value = "fileName", defaultValue = "") String fileName) {
    return ResponseEntity.ok().body(this.corporateSettingService.viewFile(fileName));
  }

  @GetMapping("/view/content")
  @Tag(name = "View file as resource", description = "To view file as resource.")
  public ResponseEntity<Resource> viewFileContent(
      @RequestParam(value = "fileName", defaultValue = "") String fileName) {
    DocumentContent documentContent = this.corporateSettingService.viewFileContent(fileName);
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, documentContent.getContentType());

    return ResponseEntity.ok().headers(headers).body(documentContent.getResource());
  }
}
