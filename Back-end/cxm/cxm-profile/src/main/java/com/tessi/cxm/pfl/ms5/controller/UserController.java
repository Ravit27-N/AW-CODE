package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.constant.SwaggerConstants;
import com.tessi.cxm.pfl.ms5.dto.AssignUsersProfilesRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.BatchUserResponseDto;
import com.tessi.cxm.pfl.ms5.dto.CreateUserRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.CreateUserResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.dto.QueryUserResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.QueryUserResponsesDTO;
import com.tessi.cxm.pfl.ms5.dto.UpdateUserDto;
import com.tessi.cxm.pfl.ms5.dto.UserDepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.UserInfoRequestUpdatePasswordDto;
import com.tessi.cxm.pfl.ms5.entity.UserEntity_;
import com.tessi.cxm.pfl.ms5.entity.projection.UserInfoProjection;
import com.tessi.cxm.pfl.ms5.exception.InvalidUserAssignedProfileException;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.BatchUserService;
import com.tessi.cxm.pfl.ms5.service.UserService;
import com.tessi.cxm.pfl.shared.document.SharedSwaggerConstants;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.UserInfoResponse;
import com.tessi.cxm.pfl.shared.model.UserServiceResponseDto;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/v1/users")
@Tag(name = "User Management", description = "The API endpoint to manage the users.")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final BatchUserService batchUserService;
 
  /**
   * Endpoint used to assign user to service.
   *
   * @param id        refer to userIdentity
   * @param serviceId refer to service identity
   */
  @Operation(operationId = "assignServiceToUser", summary = "Endpoint to assign a service to a user by identity of user and identity of service")
  @PutMapping("/{id}/services/{serviceId}")
  public ResponseEntity<UserDepartmentDto> assignServiceToUser(
      @PathVariable String id, @PathVariable long serviceId) {
    return ResponseEntity.ok(userService.assignServiceToUser(id, serviceId));
  }

  /**
   * Endpoint used to assign user to service.
   *
   * @param serviceId refer to service identity
   */
  @Operation(operationId = "assignServiceToMySelf", summary = "Endpoint to assign a service to a user by current user login and identity of service")
  @PutMapping("/services/{serviceId}")
  public ResponseEntity<UserDepartmentDto> assignServiceToMySelf(@PathVariable long serviceId) {
    return ResponseEntity.ok(userService.assignServiceToUser(serviceId));
  }

  /**
   * Endpoint used to assign user to service.
   *
   * @param username  refer to username of user to log in to system
   * @param serviceId refer to service identity
   */
  @Operation(operationId = "assignServiceToUserByUsername", summary = "Endpoint to assign a service to a user by username and identity of service")
  @PutMapping("/username/{username}/services/{serviceId}")
  public ResponseEntity<UserDepartmentDto> assignServiceToUserByUsername(
      @PathVariable String username, @PathVariable long serviceId) {
    return ResponseEntity.ok(userService.assignServiceToUserByUsername(username, serviceId));
  }

  /**
   * Endpoint used to service by user.
   *
   * @return service identity
   */
  @Operation(operationId = "getServiceIdByUser", summary = "Endpoint used to get service by username")
  @GetMapping("/service")
  public ResponseEntity<Long> getServiceByUsername() {
    return new ResponseEntity<>(userService.getServiceIdByUser(Optional.empty()), HttpStatus.OK);
  }

  /**
   * Endpoint used to service by user.
   *
   * @return service identity
   */
  @Operation(operationId = "getServiceIdByUser", summary = "Endpoint used to get service by username")
  @GetMapping("/{username}/service")
  public ResponseEntity<Long> getServiceByUsername(@PathVariable String username) {
    return new ResponseEntity<>(
        userService.getServiceIdByUser(Optional.of(username)), HttpStatus.OK);
  }

  @Operation(operationId = "createUser", summary = "Create a new user.", description = SwaggerConstants.CREATE_USER_DESCRIPTION, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = SwaggerConstants.CREATE_USER_REQUEST_BODY_DESCRIPTION), responses = {
      @ApiResponse(responseCode = "200", description = SwaggerConstants.CREATE_USER_RESPONSE_201_DESCRIPTION)
  })
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CreateUserResponseDTO> createUser(
      @Validated @RequestBody CreateUserRequestDTO createUserRequestDto) {
    log.info("UserApi - Start user creation action");
    validateUserAssignedProfiles(createUserRequestDto.getProfiles());
    var user = this.userService.createUser(createUserRequestDto);
    log.info("UserApi - End user creation action");
    return ResponseEntity.ok(user);
  }

  private void validateUserAssignedProfiles(List<Long> list) {
    if (new HashSet<>(list).size() != list.size()) {
      throw new InvalidUserAssignedProfileException("User's assigned profiles is duplicated.");
    }
    if (list.stream().anyMatch(profileId -> profileId == 0)) {
      throw new InvalidUserAssignedProfileException(
          "User's assigned profiles must be greater of equal to zero.");
    }
  }

  /**
   * Get all users by reference client id of current invoking User.
   */
  @Operation(parameters = {
      @Parameter(name = "page", in = ParameterIn.QUERY, description = SharedSwaggerConstants.PAGINATION_PAGE_DESCRIPTION, schema = @Schema(type = "integer", format = "int32"), example = "1"),
      @Parameter(name = "pageSize", in = ParameterIn.QUERY, description = SharedSwaggerConstants.PAGINATION_PAGE_SIZE_DESCRIPTION, schema = @Schema(type = "integer", format = "int32"), example = "15"),
      @Parameter(name = "sortByField", in = ParameterIn.QUERY, description = SharedSwaggerConstants.SORTING_BY_FIELD_DESCRIPTION, schema = @Schema(type = "string"), example = "createdAt"),
      @Parameter(name = "sortDirection", in = ParameterIn.QUERY, description = SharedSwaggerConstants.SORTING_BY_FIELD_DESCRIPTION, schema = @Schema(type = "string"), example = "desc"),
      @Parameter(name = "userType", in = ParameterIn.QUERY, description = "user type filter: admin, nonadmin", schema = @Schema(type = "string"), example = ""),
      @Parameter(name = "filter", in = ParameterIn.QUERY, description = "Filter.", schema = @Schema(type = "string"))
  })
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EntityResponseHandler<QueryUserResponseDTO>> getUsers(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "pageSize", defaultValue = "0") int pageSize,
      @RequestParam(value = "sortByField", defaultValue = "username") String sortByField,
      @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
      @RequestParam(value = "profileIds", defaultValue = "") List<Long> profileIds,
      @RequestParam(value = "userType", defaultValue = "") List<String> userType,
      @RequestParam(value = "clientIds", defaultValue = "") List<Long> clientIds,
      @RequestParam(value = "divisionIds", defaultValue = "") List<Long> divisionIds,
      @RequestParam(value = "serviceIds", defaultValue = "") List<Long> serviceIds,
      @RequestParam(value = "filter", defaultValue = "") String filter) {

    var pageableSpecs = this.generatePageable(page, pageSize, sortByField, sortDirection);

    return pageableSpecs
        .map(
            pageable -> ResponseEntity.ok(
                new EntityResponseHandler<>(
                    this.userService.getAllUsers(pageable, profileIds, userType, clientIds, divisionIds, serviceIds,
                        filter))))
        .orElseGet(
            () -> ResponseEntity.ok(new EntityResponseHandler<>(
                this.userService.getAllUsers(profileIds, userType, clientIds, divisionIds, serviceIds, filter,
                    this.buildSortBy(sortByField, sortDirection)))));
  }

  @GetMapping("/export-users-csv")
  public ResponseEntity<Resource> exportUsersToCSV(
      @RequestParam(value = "profileIds", defaultValue = "") List<Long> profileIds,
      @RequestParam(value = "userType", defaultValue = "") List<String> userType,
      @RequestParam(value = "clientIds", defaultValue = "") List<Long> clientIds,
      @RequestParam(value = "divisionIds", defaultValue = "") List<Long> divisionIds,
      @RequestParam(value = "serviceIds", defaultValue = "") List<Long> serviceIds,
      @RequestParam(value = "filter", defaultValue = "") String filter ) {

    byte[] csvData = userService.exportUsersToCSV(profileIds, userType, clientIds, divisionIds, serviceIds, filter);
  
    ByteArrayResource resource = new ByteArrayResource(csvData);

     // Generate the filename with the current date/time
    String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String filename = "Exp_utilisateur_" + dateTime + ".csv";
  
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
    headers.setContentType(MediaType.parseMediaType("text/csv"));
  
    return ResponseEntity.ok().headers(headers).contentLength(csvData.length).body(resource);
  }

  private Optional<Pageable> generatePageable(
      int page, int pageSize, String sortByField, String sortDirection) {
    var sortingSpecs = this.buildSortBy(sortByField, sortDirection);
    if (page > 0 && pageSize > 0) {
      return Optional.of(PageRequest.of(page - 1, pageSize, sortingSpecs));
    }
    return Optional.empty();
  }

  public Sort buildSortBy(String sortByField, String sortDirection) {
    var sortByFields = new String[] { sortByField };

    if (sortByField.equals(UserEntity_.USER_PROFILES)) {
      sortByFields = new String[] { "userProfiles.profile.name" };
    }
    // updated new
    if (sortByField.equalsIgnoreCase("service")) {
      sortByFields = new String[] { "department.name" };
      // sortByFields = new String[] { UserEntity_.DEPARTMENT };
    }

    // add new
    // updated new
    if (sortByField.equalsIgnoreCase("client")) {
      sortByFields = new String[] { "department.division.client.name" };
    }
    // add new
    if (sortByField.equalsIgnoreCase("division")) {
      sortByFields = new String[] { "department.division.name" };
    }
    return Sort.by(Sort.Direction.fromString(sortDirection), sortByFields);
  }

  /**
   * Check if a specific user's username is available.
   */
  @GetMapping(value = "/{username}/available", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Boolean> isUsernameAvailable(@PathVariable("username") String username) {
    return ResponseEntity.ok(this.userService.isUsernameAvailable(username));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<QueryUserResponseDTO> getUserById(@PathVariable("id") String userId) {
    return ResponseEntity.ok(this.userService.getUserById(userId));
  }

  /**
   * Update a user with reassign new multiple profiles or reassign multiple
   * profiles to multiple.
   */
  @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<QueryUserResponseDTO> updateUser(
      @Valid @RequestBody UpdateUserDto updateUserRequestDto) {
    this.validateUserAssignedProfiles(updateUserRequestDto.getProfiles());
    return ResponseEntity.ok(this.userService.update(updateUserRequestDto));
  }

  /**
   * Assign multiple profiles to multiple users.
   */
  @PutMapping("/profiles")
  public ResponseEntity<QueryUserResponsesDTO> assignProfilesToUsers(
      @Validated @RequestBody AssignUsersProfilesRequestDTO requestDto) {
    this.validateUserAssignedProfiles(requestDto.getProfiles());
    return ResponseEntity.ok(
        this.userService.assignProfiles(requestDto));
  }

  @DeleteMapping("/{ids}")
  public ResponseEntity<HttpStatus> deleteUsers(@PathVariable List<Long> ids) {
    this.userService.deleteUsers(ids); // delete users
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get user's organization include service, division and client.
   */
  @GetMapping(value = "/assigned-organization", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LoadOrganization> getUserOrganization() {
    return ResponseEntity.ok(this.userService.getCurrentUserOrganization());
  }

  /**
   * Check current user is admin.
   */
  @GetMapping(value = "/check-user-is-admin")
  public ResponseEntity<UserInfoResponse> checkUserIsAdmin() {
    return ResponseEntity.ok(this.userService.checkUserIsAdminOrNormal());
  }

  /**
   * To retrieve data info of user by technical reference into service.
   *
   * @see UserService#getUserInfo(String)
   */
  @GetMapping("/user-info/{id}/service")
  public ResponseEntity<UserInfoProjection> getUserInfo(@PathVariable(value = "id") String technicalRef) {
    // TODO: 12/13/2022 Rename path variable from "id" to "technical-ref"

    return ResponseEntity.ok(this.userService.getUserInfo(technicalRef));
  }

  @GetMapping("/user-info/username/{username}")
  public ResponseEntity<UserInfoProjection> findUserInfoByUsername(
      @PathVariable() String username) {
    return ResponseEntity.ok(this.userService.getUserInfoByUsername(username));
  }

  /**
   * To retrieve user detail.
   *
   * @see UserService#getUserDetail().
   */
  @GetMapping("/user-detail")
  public ResponseEntity<UserDetail> getUserDetail() {
    return ResponseEntity.ok(this.userService.getUserDetail());
  }

  /**
   * To retrieve user detail both the user deleted.
   *
   * @see UserService#getUserDetail().
   */
  @GetMapping("/user-detail/{id}")
  public ResponseEntity<UserDetail> getUserDetailById(@PathVariable long id) {
    return ResponseEntity.ok(this.userService.getUserDetailById(id));
  }

  @GetMapping("/user-details/{id}/service")
  public ResponseEntity<List<UserDetail>> getUserDetails(@PathVariable("id") long serviceId) {
    return ResponseEntity.ok(this.userService.getUserDetails(serviceId));
  }

  @GetMapping("/user-info")
  public ResponseEntity<QueryUserResponseDTO> getUserInfoByToken() {
    return ResponseEntity.ok(this.userService.getUserInfoByToken());
  }

  @PostMapping("/update-user-password")
  public ResponseEntity<UserInfoRequestUpdatePasswordDto> updateUserPassword(
      @RequestBody @Valid UserInfoRequestUpdatePasswordDto user) {
    return ResponseEntity.ok(this.userService.updateUserPassword(user));
  }

  @GetMapping("/user-services")
  public ResponseEntity<List<UserServiceResponseDto>> getUserServiceResponseDto(
      @RequestParam(value = "usernames") List<String> usernames) {
    return ResponseEntity.ok(userService.getUserService(usernames));
  }

  @PostMapping(value = "/batch-users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BatchUserResponseDto> createBatchUsers(@RequestParam("file") MultipartFile multipartFile) {
    return ResponseEntity.ok(this.batchUserService.create(multipartFile));
  }
}
