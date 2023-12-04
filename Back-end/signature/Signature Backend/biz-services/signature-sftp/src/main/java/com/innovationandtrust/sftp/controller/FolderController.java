package com.innovationandtrust.sftp.controller;

import com.innovationandtrust.sftp.service.SFTPGoService;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderResponse;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderRequest;
import com.innovationandtrust.sftp.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/folders")
@RequiredArgsConstructor
public class FolderController {

  private final FileService fileService;
  private final SFTPGoService sftpGoService;

  @PostMapping
  public ResponseEntity<Void> createCorporateFolder(@RequestParam String corporateUuid) {
    this.fileService.createCorporateFolder(corporateUuid);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/sftpgo/user-folder")
  public ResponseEntity<SFTPUserFolderResponse> createCorporateUser(
      @Valid @RequestBody SFTPUserFolderRequest request) {
    return ResponseEntity.ok(this.sftpGoService.createUserAndFolderInSFTPGo(request));
  }
}
