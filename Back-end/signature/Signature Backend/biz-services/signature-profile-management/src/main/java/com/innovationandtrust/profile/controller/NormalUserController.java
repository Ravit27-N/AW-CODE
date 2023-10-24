package com.innovationandtrust.profile.controller;

import com.innovationandtrust.profile.model.dto.NormalUserDto;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.service.NormalUserService;
import com.innovationandtrust.profile.service.UserCsvService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.profile.UserCompany;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import com.innovationandtrust.utils.keycloak.model.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Slf4j
public class NormalUserController {

  private final NormalUserService userService;

  private final UserCsvService userCsvService;

  @GetMapping("/validate/{uuid}")
  @Tag(name = "Validate user", description = "To validate user with their uuid")
  public ResponseEntity<String> validate(@PathVariable String uuid) {
    return this.userService.validate(uuid);
  }

  @GetMapping
  @Tag(name = "Get users", description = "To get list of users options(pageable, search by name)")
  public ResponseEntity<EntityResponseHandler<NormalUserDto>> findAll(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "15") int pageSize,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id") String sortField,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String search) {
    return ResponseEntity.ok(
        new EntityResponseHandler<>(
            this.userService.findAll(
                PageUtils.pageable(page, pageSize, sortField, sortDirection), search)));
  }

  @GetMapping("/{id}")
  @Tag(name = "Get user", description = "To get user by id")
  public ResponseEntity<NormalUserDto> findByIdDeletedFalse(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.userService.findByIdDeletedFalse(id));
  }

  @Hidden
  @GetMapping("/optional/{id}")
  public ResponseEntity<Optional<NormalUserDto>> findByIdOptional(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.userService.findByIdOptional(id));
  }

  @GetMapping("/{id}/info")
  @Tag(name = "Get user info by id", description = "To get user info by id")
  public ResponseEntity<NormalUserDto> getUserInfo(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.userService.findById(id));
  }

  @GetMapping("/company/{id}")
  @Tag(
      name = "Get user and user's company by ",
      description = "To get user info and user's company")
  public ResponseEntity<UserCompany> findUserCompany(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.userService.findUserCompanyById(id));
  }

  @GetMapping("/end-user")
  @Tag(name = "Get user info", description = "To get currently login user information")
  public ResponseEntity<NormalUserDto> getOwnInfo() {
    return ResponseEntity.ok(this.userService.findById());
  }

  @GetMapping("/uuid/{id}")
  @Tag(name = "Get user by uuid", description = "To get user by uuid")
  public ResponseEntity<NormalUserDto> findByKeycloakId(@PathVariable String id) {
    return ResponseEntity.ok(this.userService.findByUserEntityId(id));
  }

  @GetMapping("/{id}/company")
  @Tag(
      name = "Get user and company",
      description = "To get user information with company information")
  public ResponseEntity<NormalUserDto> findCompanyById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.userService.findWithCompanyById(id));
  }

  @GetMapping("/{id}/users-company")
  @Tag(name = "Get users id in a company", description = "To get users id in the same company")
  public ResponseEntity<List<Long>> findUserInTheSameCompany(@PathVariable("id") Long id) {
    return ResponseEntity.ok(this.userService.findUserInTheSameCompany(id));
  }

  @PostMapping
  @Tag(name = "Create End-User", description = "To create new End-User")
  public ResponseEntity<NormalUserDto> save(@Valid @RequestBody NormalUserDto dto) {
    return ResponseEntity.ok(this.userService.save(dto));
  }

  @PutMapping
  @Tag(name = "Update End-User", description = "To update new End-User")
  public ResponseEntity<NormalUserDto> update(@RequestBody NormalUserDto dto) {
    return ResponseEntity.ok(this.userService.update(dto));
  }

  @PutMapping("/active")
  @Tag(name = "Enable End-User login", description = "To enable End-User can login system")
  public ResponseEntity<NormalUserDto> isActive(
      @RequestParam Long id, @RequestParam Boolean active) {
    return ResponseEntity.ok(this.userService.isActive(id, active));
  }

  @PostMapping(
      value = "/csv",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Tag(name = "Create End-User form CSV file", description = "To create many users from CSV file")
  public ResponseEntity<String> saveFromCSV(@Valid @RequestParam("file") MultipartFile file) {
    return new ResponseEntity<>(this.userCsvService.saveFromCSV(file), HttpStatus.OK);
  }

  @GetMapping("/email")
  @Tag(
      name = "Get End-User by registered email",
      description = "To get End-user by registered email")
  public ResponseEntity<Boolean> findUserByEmail(@RequestParam String email) {
    return ResponseEntity.ok(this.userService.isEmailExist(email));
  }

  @GetMapping("/phone")
  @Tag(
      name = "Get End-User by registered phone",
      description = "To get End-user by registered phone")
  public ResponseEntity<Boolean> findUserByPhone(@RequestParam String phone) {
    return ResponseEntity.ok(this.userService.isPhoneExist(phone));
  }

  @GetMapping("/valid-user")
  @Tag(name = "Validate End-User email", description = "To validate user email exist in system")
  public ResponseEntity<Boolean> findValidUser(@RequestParam String email) {
    return ResponseEntity.ok(this.userService.isValidUser(email));
  }

  @GetMapping("/ids")
  @Tag(
      name = "Get End-Users information",
      description = "To get list of End-Users information by list of users id")
  public ResponseEntity<List<NormalUserDto>> findValidUser(@RequestParam("ids") List<String> ids) {
    return ResponseEntity.ok(this.userService.findUsers(ids));
  }

  @PutMapping("/password/reset")
  @Tag(
      name = "End-User reset their password",
      description = "User reset their password with current password")
  public ResponseEntity<Void> resetPassword(
      @RequestBody ResetPasswordRequest resetPasswordRequest) {
    this.userService.resetPassword(resetPasswordRequest);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * To delete user.
   *
   * @param id use for delete user by id
   * @param assignTo is user id of an end user. Him/Her will see projects assigned to him/her
   */
  @DeleteMapping("/delete/{id}")
  @Tag(
      name = "Delete End-User",
      description =
          "To delete End-user (soft delete) and assign him/her projects to another End-User")
  public ResponseEntity<Void> deleteUser(
      @PathVariable("id") Long id, @RequestParam(required = false) Long assignTo) {
    this.userService.deleteUser(id, assignTo);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * @param role refers to role want to
   * @param userIds refers to employees id
   * @return list of users ids
   */
  @Hidden
  @PostMapping("/role/ids")
  public ResponseEntity<List<Long>> filterByRole(
      @RequestParam("role") String role, @RequestBody List<Long> userIds) {
    return new ResponseEntity<>(this.userService.getUserIds(userIds, role), HttpStatus.OK);
  }

  /**
   * This use for case corporate admin delete end-user (project creator) to sign with the same
   * company facade, must use corporate token instead
   */
  @Hidden
  @GetMapping("/corporate-role/{companyId}")
  public ResponseEntity<Optional<User>> getActiveUserByRole(
      @PathVariable("companyId") Long companyId, @RequestParam("role") String role) {
    return new ResponseEntity<>(
        this.userService.getAnActiveUserByRole(companyId, role), HttpStatus.OK);
  }
}
