package com.innovationandtrust.project.controller;

import com.innovationandtrust.project.service.ProjectXmlService;
import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.utils.file.model.FileResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ProjectXml Controller provides endpoints to create a project from xml file that has been uploaded
 * from sftp service, and upload the document from that uploaded file.
 */
@RestController
@RequestMapping("/v1/project-xml")
@RequiredArgsConstructor
@Slf4j
public class ProjectXmlController {

  private final ProjectXmlService projectXmlService;

  @PostMapping
  public ResponseEntity<Boolean> createProjectFromXml(
      @RequestBody @Valid ProjectModel projectModel) {
    this.projectXmlService.createProjectFromXml(projectModel);
    return new ResponseEntity<>(true, HttpStatus.CREATED);
  }

  @PostMapping(value = "/upload-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public List<FileResponse> uploadDocument(
      @RequestPart("files") List<MultipartFile> files,
      @RequestParam(value = "dirs", defaultValue = "") String... dirs) {
    return this.projectXmlService.uploadDocFiles(files, dirs);
  }
}
