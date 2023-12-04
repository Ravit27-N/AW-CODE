package com.innovationandtrust.sftp.controller;

import com.innovationandtrust.sftp.service.ProjectService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v1/sign")
public class SignatureController {
  private final ProjectService projectService;

  public SignatureController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @Hidden
  @PostMapping("/insert/signed-files")
  public ResponseEntity<Void> insertFile(
      @Valid @ModelAttribute("signedFiles") MultipartFile[] signedFiles,
      @Valid @ModelAttribute("manifestFile") MultipartFile manifestFile,
      @RequestParam("zipFile") String zipFile,
      @RequestParam("corporateUuid") String corporateUuid) {
    this.projectService.insertFileToZipped(corporateUuid, zipFile, signedFiles, manifestFile);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
