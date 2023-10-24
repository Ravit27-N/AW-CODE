package com.innovationandtrust.project.controller;

import com.innovationandtrust.project.model.dto.DocumentDTO;
import com.innovationandtrust.project.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Document controller provides endpoints such as findById. */
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
  private final DocumentService documentService;

  @GetMapping("/{id}")
  public ResponseEntity<DocumentDTO> findById(@PathVariable("id") Long id) {
    return new ResponseEntity<>(this.documentService.findById(id), HttpStatus.OK);
  }
}
