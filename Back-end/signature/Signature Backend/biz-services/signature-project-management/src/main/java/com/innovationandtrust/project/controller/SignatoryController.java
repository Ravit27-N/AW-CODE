package com.innovationandtrust.project.controller;

import com.innovationandtrust.project.model.UserProjectResponse;
import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.model.dto.SignedDocumentDTO;
import com.innovationandtrust.project.service.SignatoryService;
import com.innovationandtrust.project.service.SignedDocumentService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Signatory controller provides endpoints such as findAll, save, findById, update, delete,
 * updateStatus, findSignDocument, downloadSignDocument.
 */
@RestController
@RequestMapping("/v1/signatories")
public class SignatoryController {
  private final SignatoryService signatoryService;

  private final SignedDocumentService signedDocumentService;

  @Autowired
  public SignatoryController(
      SignatoryService signatoryService, SignedDocumentService signedDocumentService) {
    this.signatoryService = signatoryService;
    this.signedDocumentService = signedDocumentService;
  }

  /**
   * Retrieves findAll of signatory, by pagination and sorting.
   *
   * @param page is optional, by default is 1 and is the index page that we want to retrieve
   * @param pageSize is optional, by default is 10 and limit the number of items per page
   * @param filter is optional and search it with last_name, and first_name
   * @param sortDirection is optional, by default is descending and a sort direction is ascending or
   *     descending
   * @param sortByField is optional, by default, is id, it's value is property name
   * @return the pagination of findAll SignatoryDto
   */
  @GetMapping
  @Tag(
      name = "Get all signatories as pagination",
      description = "To get all signatories as pagination")
  public ResponseEntity<EntityResponseHandler<SignatoryDto>> findAll(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField) {

    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.signatoryService.findAll(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection), filter)),
        HttpStatus.OK);
  }

  /**
   * Insert a new Signatory.
   *
   * @return the inserted SignatoryDto
   */
  @PostMapping
  @Tag(name = "Create signatory", description = "To create signatory")
  public ResponseEntity<SignatoryDto> save(@RequestBody @Valid SignatoryDto projectSignatory) {
    return new ResponseEntity<>(this.signatoryService.save(projectSignatory), HttpStatus.CREATED);
  }

  /**
   * Retrieves a signatory by its id.
   *
   * @param id must not be {@literal null}.
   * @return the SignatoryDto with the given id
   */
  @GetMapping("/{id}")
  @Tag(name = "Get signatory by id", description = "To get signatory by id")
  public ResponseEntity<SignatoryDto> findById(@PathVariable long id) {
    return new ResponseEntity<>(this.signatoryService.findById(id), HttpStatus.OK);
  }

  /**
   * Update a signatory by id.
   *
   * @return the updated SignatoryDto
   */
  @PutMapping
  @Tag(name = "Update signatory", description = "To update signatory")
  public ResponseEntity<SignatoryDto> update(@RequestBody @Valid SignatoryDto projectSignatory) {
    return new ResponseEntity<>(this.signatoryService.update(projectSignatory), HttpStatus.OK);
  }

  // delete a signatory by id
  @DeleteMapping("/{id}")
  @Tag(name = "Delete signatory", description = "To delete signatory")
  public ResponseEntity<Boolean> delete(@PathVariable Long id) {
    this.signatoryService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/{projectId}/status")
  @Tag(name = "Update status", description = "To update status")
  public ResponseEntity<Void> updateStatus(
      @PathVariable("projectId") Long projectId, @RequestBody List<SignatoryRequest> requests) {
    this.signatoryService.updateStatus(projectId, requests);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id}/signed-documents")
  @Tag(name = "Get signed documents", description = "To get signed documents by signatory id")
  public ResponseEntity<List<SignedDocumentDTO>> findSignedDocuments(@PathVariable("id") Long id) {
    return new ResponseEntity<>(this.signedDocumentService.findBySignatoryId(id), HttpStatus.OK);
  }

  /**
   * Download signed document endpoint.
   *
   * @param id refers to document's id
   * @return Resource.
   */
  @GetMapping("/document/{id}/download")
  @Tag(name = "Download signed documents", description = "To download signed documents")
  public ResponseEntity<Resource> downloadSignedDocument(@PathVariable("id") Long id) {
    var response = this.signedDocumentService.downloadSignedDocument(id);
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, response.getContentType());
    headers.setContentLength(response.getContentLength());
    headers.setContentDisposition(
        ContentDisposition.attachment().filename(response.getFileName()).build());
    return new ResponseEntity<>(response.getResource(), headers, HttpStatus.OK);
  }

  /**
   * List projects that contained signatory who is the end user by email.
   *
   * @return list of found project
   */
  @Hidden
  @GetMapping("/projects")
  @Tag(name = "Get all projects by end user", description = "To get all projects by end user")
  public ResponseEntity<UserProjectResponse> endUserProject(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "projectId")
          String sortByField,
      @RequestParam("status") String status) {
    return new ResponseEntity<>(
        this.signatoryService.listEndUserProjects(
            PageUtils.pageable(page, pageSize, sortByField, sortDirection), status),
        HttpStatus.OK);
  }
}
