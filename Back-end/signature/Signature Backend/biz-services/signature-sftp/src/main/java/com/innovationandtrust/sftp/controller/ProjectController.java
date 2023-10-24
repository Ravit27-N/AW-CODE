package com.innovationandtrust.sftp.controller;

import com.innovationandtrust.sftp.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("projects")
@RequiredArgsConstructor
public class ProjectController {

  private final ProjectService projectService;

  @GetMapping("/sample-xml")
  public ResponseEntity<Resource> getSample() {
    Resource resource = this.projectService.getSampleXml();
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
    headers.setContentDisposition(ContentDisposition.attachment().build());
    return ResponseEntity.ok().headers(headers).body(resource);
  }

}
