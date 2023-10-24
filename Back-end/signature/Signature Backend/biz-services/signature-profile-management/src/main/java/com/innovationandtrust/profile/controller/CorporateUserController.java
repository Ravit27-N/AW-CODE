package com.innovationandtrust.profile.controller;

import com.innovationandtrust.profile.model.dto.CorporateUserDto;
import com.innovationandtrust.profile.service.CorporateUserService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.profile.CorporateUser;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/corporate/users")
@PreAuthorize("hasRole('SUPER-ADMIN') or hasRole('CORPORATE-ADMIN')")
public class CorporateUserController {
  private final CorporateUserService corporateUserService;

  @PreAuthorize("hasRole('SUPER-ADMIN')")
  @PostMapping
  @Tag(name = "Create corporate admin", description = "To create new corporate admin for a company")
  public ResponseEntity<CorporateUserDto> save(@Valid @RequestBody CorporateUserDto dto) {
    return ResponseEntity.ok(this.corporateUserService.save(dto));
  }

  @PutMapping
  @Tag(name = "Update corporate admin", description = "To update new corporate admin for a company")
  public ResponseEntity<CorporateUserDto> update(@Valid @RequestBody CorporateUserDto dto) {
    return ResponseEntity.ok(this.corporateUserService.update(dto));
  }

  @GetMapping("/{id}")
  @Tag(
      name = "Get the corporate user",
      description = "To get corporate admin information by user id")
  public ResponseEntity<CorporateUserDto> findById(@PathVariable Long id) {
    return ResponseEntity.ok(this.corporateUserService.findById(id));
  }

  @GetMapping("/uuid/{id}")
  @Tag(
      name = "Get the corporate user",
      description =
          "To get corporate admin information by uuid and get end-user information by uuid")
  public ResponseEntity<CorporateUser> findByUserEntityId(
      @PathVariable String id, @RequestParam(name = "userId", defaultValue = "") String userId) {
    return ResponseEntity.ok(this.corporateUserService.findByUserEntityId(id, userId));
  }

  @GetMapping("/info")
  @Tag(name = "Get own info", description = "To get currently login corporate admin information")
  public ResponseEntity<CorporateUserDto> info() {
    return ResponseEntity.ok(this.corporateUserService.findAuthor());
  }

  @PreAuthorize("hasRole('SUPER-ADMIN')")
  @GetMapping("/company/{uuid}")
  @Tag(
      name = "Get corporate admins",
      description =
          "To get list of corporate admins by company uuid with options(pageable, search by corporate admins name) ")
  public ResponseEntity<EntityResponseHandler<CorporateUserDto>> findByCompany(
      @PathVariable("uuid") String uuid,
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String search,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField) {
    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.corporateUserService.findByCompany(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection), search, uuid)),
        HttpStatus.OK);
  }

  @PreAuthorize("hasRole('SUPER-ADMIN')")
  @DeleteMapping("/{id}")
  @Tag(
      name = "Delete corporate admin",
      description = "To soft delete corporate admin user by super admin")
  public ResponseEntity<Void> delete(
      @PathVariable("id") Long id,
      @RequestParam(name = "assignTo", required = false) Long assignTo) {
    this.corporateUserService.deleteUser(id, assignTo);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PreAuthorize("hasRole('SUPER-ADMIN')")
  @PutMapping("/activate/{id}")
  @Tag(name = "Activate corporate admin", description = "To enable/disable corporate login")
  public ResponseEntity<CorporateUserDto> active(
      @PathVariable("id") Long id, @RequestParam("active") Boolean active) {
    return ResponseEntity.ok(this.corporateUserService.active(id, active));
  }
}
