package com.innovationandtrust.project.controller;

import com.innovationandtrust.project.model.dto.DocumentContent;
import com.innovationandtrust.project.service.DocumentService;
import com.innovationandtrust.utils.file.exception.FileRequestException;
import com.innovationandtrust.utils.file.model.FileResponse;
import com.innovationandtrust.utils.file.provider.FileProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * File controller provides endpoints like uploadFiles, download, downloadById,
 * downloadSignedById...
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/documents")
@Slf4j
public class FileController {

  private final FileProvider fileProvider;
  private final DocumentService documentService;

  /**
   * View document endpoint.
   *
   * @param files receive files from client request
   * @param dirs used for create directory to store file
   * @return the findAll of FileResponse
   */
  @PostMapping("/uploads")
  @Tag(name = "Upload files", description = "To upload files")
  public ResponseEntity<List<FileResponse>> uploadFiles(
      @Valid @RequestParam("files") MultipartFile[] files,
      @RequestParam(value = "dirs", defaultValue = "") String dirs) {
    return ResponseEntity.ok(fileProvider.uploads(files, dirs));
  }

  /**
   * View document endpoint.
   *
   * @param path the file path
   * @return File to view
   */
  @GetMapping("/download")
  @Tag(name = "Download file", description = "To download file")
  public ResponseEntity<Resource> download(@RequestParam("path") String path) {
    var resource = fileProvider.download(path, true);
    HttpHeaders headers = new HttpHeaders();
    try {
      headers.setContentType(
          MediaType.parseMediaType(resource.getURL().openConnection().getContentType()));
    } catch (IOException exception) {
      throw new FileRequestException("Unable to get url");
    }
    return ResponseEntity.ok().headers(headers).body(resource);
  }

  /**
   * download document endpoint.
   *
   * @param id is document id
   * @return File to view
   */
  @GetMapping("/{id}/download/current")
  @Tag(name = "Upload file by id", description = "To upload file by id")
  public ResponseEntity<Resource> downloadById(@PathVariable("id") Long id) {
    var response = this.documentService.downloadDocument(id);
    return new ResponseEntity<>(response.getResource(), getResourceHeader(response), HttpStatus.OK);
  }

  /**
   * download signed document endpoint.
   *
   * @param id is document id
   * @return File to view
   */
  @GetMapping("/{id}/download/signed")
  @Tag(name = "Download signed document", description = "To download signed document")
  public ResponseEntity<Resource> downloadSignedById(@PathVariable("id") Long id) {
    var response = this.documentService.downloadSignedDocument(id);
    return new ResponseEntity<>(response.getResource(), getResourceHeader(response), HttpStatus.OK);
  }

  /**
   * download original document endpoint.
   *
   * @param id is document id
   * @return File to view
   */
  @GetMapping("/{id}/download/original")
  @Tag(name = "Download original document", description = "To download original document")
  public ResponseEntity<Resource> downloadOrignalById(@PathVariable("id") Long id) {
    var response = this.documentService.downloadOriginalDocument(id);
    return new ResponseEntity<>(response.getResource(), getResourceHeader(response), HttpStatus.OK);
  }

  private HttpHeaders getResourceHeader(DocumentContent response) {
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, response.getContentType());
    headers.setContentLength(response.getContentLength());
    headers.setContentDisposition(
        ContentDisposition.attachment().filename(response.getFileName()).build());
    return headers;
  }
}
