package com.innovationandtrust.sftp.controller;

import com.innovationandtrust.sftp.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/folders")
@RequiredArgsConstructor
public class FolderController {

  private final FileService fileService;

  @PostMapping
  public ResponseEntity<Void> createCorporateFolder(@RequestParam String corporateUuid) {
    this.fileService.createCorporateFolder(corporateUuid);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
