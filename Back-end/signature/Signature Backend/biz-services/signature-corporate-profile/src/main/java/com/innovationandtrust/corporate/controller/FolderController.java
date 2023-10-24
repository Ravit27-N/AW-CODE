package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.model.dto.FolderDto;
import com.innovationandtrust.corporate.service.FolderService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/folders")
@RequiredArgsConstructor
public class FolderController {

  private final FolderService folderService;

  @GetMapping("/business/{id}")
  public ResponseEntity<List<FolderDto>> findByBusinessId(@PathVariable("id") long id) {
    return new ResponseEntity<>(this.folderService.findByBusinessId(id), HttpStatus.OK);
  }

  @GetMapping("/company/{companyId}")
  public ResponseEntity<List<FolderDto>> findByCompanyId(
      @PathVariable("companyId") long companyId) {
    return new ResponseEntity<>(this.folderService.findByCompanyId(companyId), HttpStatus.OK);
  }

  @GetMapping("/user")
  public ResponseEntity<EntityResponseHandler<FolderDto>> findByOwner(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "15") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id") String sortField,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String search,
      @RequestHeader HttpHeaders headers) {
    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.folderService.findByOwner(
                PageUtils.pageable(page, pageSize, sortField, sortDirection), search, headers)),
        HttpStatus.OK);
  }

  @GetMapping("/users")
  public ResponseEntity<List<FolderDto>> findByCorporateId(
      @RequestParam("ids") List<Long> usersIds) {
    return new ResponseEntity<>(this.folderService.findByCorporateId(usersIds), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FolderDto> findById(@PathVariable("id") long id) {
    return new ResponseEntity<>(this.folderService.findById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<FolderDto> save(
      @RequestBody @Valid FolderDto dto, @RequestHeader HttpHeaders headers) {
    return new ResponseEntity<>(this.folderService.save(dto, headers), HttpStatus.CREATED);
  }

  @PutMapping
  public ResponseEntity<FolderDto> update(
      @RequestBody @Valid FolderDto dto, @RequestHeader HttpHeaders headers) {
    return new ResponseEntity<>(this.folderService.update(dto, headers), HttpStatus.CREATED);
  }
}
