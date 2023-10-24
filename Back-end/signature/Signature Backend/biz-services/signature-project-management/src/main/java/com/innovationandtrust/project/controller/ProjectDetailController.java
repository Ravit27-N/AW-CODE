package com.innovationandtrust.project.controller;

import com.innovationandtrust.project.model.dto.ProjectDetailDTO;
import com.innovationandtrust.project.service.ProjectDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project detail controller provides endpoints such as save project detail, find project detail by
 * type.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/project-details")
public class ProjectDetailController {
  private final ProjectDetailService service;

  @PostMapping
  @Tag(name = "Create project detail", description = "To create project detail")
  public ResponseEntity<ProjectDetailDTO> save(@Valid @RequestBody ProjectDetailDTO dto) {
    return new ResponseEntity<>(this.service.save(dto), HttpStatus.CREATED);
  }

  @GetMapping("/type")
  @Tag(name = "Find project detail by type", description = "To find project detail by type")
  public ResponseEntity<ProjectDetailDTO> findByType(
      @RequestParam(name = "type") String type, @RequestParam(name = "projectId") Long projectId) {
    return new ResponseEntity<>(this.service.findByType(type, projectId), HttpStatus.OK);
  }
}
