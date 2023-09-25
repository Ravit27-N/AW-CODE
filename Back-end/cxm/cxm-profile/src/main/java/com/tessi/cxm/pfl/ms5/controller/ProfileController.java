package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.constant.Functionality;
import com.tessi.cxm.pfl.ms5.constant.SwaggerConstants;
import com.tessi.cxm.pfl.ms5.dto.ProfileDetailDto;
import com.tessi.cxm.pfl.ms5.dto.ProfileDto;
import com.tessi.cxm.pfl.ms5.dto.ProfileFilterCriteria;
import com.tessi.cxm.pfl.ms5.dto.UserProfilePrivilege;
import com.tessi.cxm.pfl.ms5.dto.UserProfilesDto;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Privilege;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.service.ClientService;
import com.tessi.cxm.pfl.ms5.service.ProfileService;
import com.tessi.cxm.pfl.ms5.service.UserService;
import com.tessi.cxm.pfl.shared.document.SharedSwaggerConstants;
import com.tessi.cxm.pfl.shared.model.SharedUserEntityDTO;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner;
import com.tessi.cxm.pfl.shared.model.UsersRelatedToPrivilege;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

/**
 * Profile Controller.
 *
 * @author Sokhour LACH
 * @author Pisey CHORN
 * @since 06/12/2021
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/profiles")
@Tag(name = "Profile Management", description = "The API endpoint to manage the profile")
@Log4j2
public class ProfileController {

    private final ProfileService profileService;

    private final ClientService clientService;

    private final UserService userService;

    /**
     * To retrieve an object of profile by id.
     *
     * @see ProfileService#findById(Long id)
     */
    @Operation(operationId = "getProfileById", summary = "To get an object of profile base on profile id", description = SwaggerConstants.GET_PROFILE_BY_ID_DESCRIPTION, parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, description = "profile's id", schema = @Schema(type = "integer", format = "int64"), example = "1")
    }, responses = @ApiResponse(responseCode = "200", description = SwaggerConstants.GET_PROFILE_BY_ID_RESPONSE_200_DESCRIPTION))
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileDto> getProfileById(@PathVariable long id) {
        return ResponseEntity.ok(this.profileService.findById(id));
    }

    /**
     * To retrieve a list of profile.
     *
     * @see ProfileService#findAll()
     */
    @Operation(operationId = "getAllProfile", summary = "To get a list of profile", description = SwaggerConstants.GET_ALL_PROFILE_DESCRIPTION, responses = @ApiResponse(responseCode = "200", description = SwaggerConstants.GET_ALL_PROFILE_RESPONSE_200_DESCRIPTION))
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProfileDto>> getAllProfile() {
        return ResponseEntity.ok(this.profileService.findAll());
    }

    /**
     * To retrieve a list of profile details.
     *
     * @see ProfileService#getProfileDetailsByProfileId(long profileId)
     */
    @Operation(operationId = "getProfileDetailsByProfileId", summary = "To get a list of profile details base on profile's id", description = SwaggerConstants.GET_PROFILE_DETAILS_BY_PROFILE_ID_DESCRIPTION, parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, description = "profile's id", schema = @Schema(type = "integer", format = "int64"), example = "1")
    }, responses = @ApiResponse(responseCode = "200", description = SwaggerConstants.GET_PROFILE_DETAILS_BY_PROFILE_ID_RESPONSE_200_DESCRIPTION))
    @GetMapping(value = "/{id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProfileDetailDto>> getProfileDetailsByProfileId(
            @PathVariable long id) {
        return ResponseEntity.ok(this.profileService.getProfileDetailsByProfileId(id));
    }

    /**
     * To create new {@link Profile}.
     *
     * @param profileDto refer to object of {@link ProfileDto}
     * @return object of {@link ProfileDto}
     */
    @Operation(operationId = "createProfile", summary = "Add a new profile", description = SwaggerConstants.CREATE_PROFILE_DESCRIPTION, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = SwaggerConstants.CREATE_PROFILE_REQUEST_BODY_DESCRIPTION), responses = {
            @ApiResponse(responseCode = "201", description = SwaggerConstants.CREATE_PROFILE_RESPONSE_201_DESCRIPTION)
    })
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileDto> createProfile(@Validated @RequestBody ProfileDto profileDto) {
        return ResponseEntity.ok(this.profileService.createProfile(profileDto));
    }

    /**
     * To delete a profile from database.
     *
     * @see ProfileService#delete(Long)
     */
    @Operation(operationId = "deleteProfile", summary = "To delete a profile base on its id", description = SwaggerConstants.DELETE_PROFILE_BY_ID_DESCRIPTION, parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, description = "Profile's id", schema = @Schema(type = "integer", format = "int64"), example = "1")
    }, responses = @ApiResponse(responseCode = "204", description = SwaggerConstants.DELETE_PROFILE_BY_ID_RESPONSE_204_DESCRIPTION, content = @Content(examples = @ExampleObject(description = "No Content", value = "Delete a profile by id successfully. Status code is 204"))))
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable("id") long id) {
        profileService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * To check duplicated name of profile.
     *
     * @param name     refers to name property of {@link Profile}
     * @param clientId refers to identified property of {@link Client}
     * @return true if name is duplicated
     */
    @Operation(operationId = "isDuplicateName", summary = "To check duplicated name of profile base on its name and id", description = SwaggerConstants.IS_DUPLICATED_NAME_DESCRIPTION, parameters = {
            @Parameter(name = "name", in = ParameterIn.PATH, description = "Profile's name", schema = @Schema(type = "string"), example = "tessi"),
            @Parameter(name = "clientId", in = ParameterIn.PATH, description = "Client's id", schema = @Schema(type = "integer", format = "int64"), example = "1")
    }, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerConstants.IS_DUPLICATED_NAME_RESPONSE_200_DESCRIPTION)
    })
    @GetMapping(value = "/duplicate/{name}/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> isDuplicateName(
            @PathVariable String name, @PathVariable long clientId) {
        return ResponseEntity.ok(this.profileService.isDuplicateName(name, clientId));
    }

    /**
     * The endpoint to check duplicate name of {@link Profile} with specific
     * {@link Client} of current
     * user login token.
     *
     * @param name the name of {@link Profile} to check
     * @return true if duplicate
     */
    @GetMapping(value = "/duplicate/{name}")
    public ResponseEntity<Boolean> isDuplicateName(@PathVariable String name,
            @RequestParam(value = "clientId", required = false) Long clientId) {
        return ResponseEntity.ok(this.profileService.isDuplicateName(name, clientId));
    }

    /**
     * To receive pagination and filtering list of profile.
     *
     * @param page          the data with pagination {@link Page}
     * @param pageSize      refers to the amount of profile object per page
     * @param sortByField   refers to any properties of {@link ProfileDto}
     * @param sortDirection indications sorted direction
     * @param filter        refers to the value for filtering
     * @return pagination list of {@link ProfileDto}
     * @see ProfileService#findAll(Pageable, String filter)
     */
    @Operation(operationId = "findAll", summary = "To get pagination and filtering list of profile", description = SwaggerConstants.FIND_ALL_DESCRIPTION, parameters = {
            @Parameter(name = "page", in = ParameterIn.QUERY, description = SharedSwaggerConstants.PAGINATION_PAGE_DESCRIPTION, schema = @Schema(type = "integer", format = "int32"), example = "1"),
            @Parameter(name = "pageSize", in = ParameterIn.QUERY, description = SharedSwaggerConstants.PAGINATION_PAGE_SIZE_DESCRIPTION, schema = @Schema(type = "integer", format = "int32"), example = "15"),
            @Parameter(name = "sortByField", in = ParameterIn.QUERY, description = SharedSwaggerConstants.SORTING_BY_FIELD_DESCRIPTION, schema = @Schema(type = "string"), example = "createdAt"),
            @Parameter(name = "sortDirection", in = ParameterIn.QUERY, description = SharedSwaggerConstants.SORTING_BY_FIELD_DESCRIPTION, schema = @Schema(type = "string"), example = "desc"),
            @Parameter(name = "filter", in = ParameterIn.QUERY, description = "Filter.", schema = @Schema(type = "string"))
    }, responses = @ApiResponse(responseCode = "200", description = SwaggerConstants.FIND_ALL_RESPONSE_200_DESCRIPTION))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityResponseHandler<ProfileDto>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortByField", defaultValue = "lastModified") String sortByField,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "clientIds", required = false) List<Long> clientIds,
            @RequestParam(value = "filter", defaultValue = "") String filter) {

        log.info("API Profile debut d'appelle - findALL ");
        /* A non-administrator user sees the profiles of his client. */
        /*if(!userService.getUserInfoByToken().getAdmin())
            return ResponseEntity.ok(new EntityResponseHandler<>(this.profileService.findAllWithClientID(page, pageSize, sortDirection, sortByField, filter, userService.getUserInfoByToken().getClient().getId(), clientIds)));
        /* An administrator user can see all the profiles of the platform. */
        return ResponseEntity.ok(new EntityResponseHandler<>(this.profileService.findAll(page, pageSize, sortDirection, sortByField, filter, clientIds)));

    }

    /**
     * To update a {@link Profile}.
     *
     * @param profileDto refer to object of {@link ProfileDto}
     * @return object of {@link ProfileDto}
     */
    @Operation(operationId = "updateProfile", summary = "Update a profile", description = SwaggerConstants.UPDATE_PROFILE_DESCRIPTION, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = SwaggerConstants.UPDATE_PROFILE_REQUEST_BODY_DESCRIPTION), responses = {
            @ApiResponse(responseCode = "200", description = SwaggerConstants.UPDATE_PROFILE_RESPONSE_200_DESCRIPTION)
    })
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileDto> updateProfile(@Validated @RequestBody ProfileDto profileDto) {
        return ResponseEntity.ok(this.profileService.update(profileDto));
    }

    /**
     * To assign profile to client.
     *
     * @param profileId refers to an identified property of {@link Profile}
     * @param clientId  refers to an identified property of {@link Client}
     * @see ProfileService#assignProfileToClient(Long profileId, Long clientId)
     */
    @Operation(operationId = "assignProfileToClient", summary = "To assign profile to client base on profile's id and client's id", description = SwaggerConstants.ASSIGN_PROFILE_DESCRIPTION, parameters = {
            @Parameter(name = "profileId", in = ParameterIn.PATH, description = "Profile's id", schema = @Schema(type = "integer", format = "int64"), example = "1"),
            @Parameter(name = "clientId", in = ParameterIn.PATH, description = "Client's id", schema = @Schema(type = "integer", format = "int64"), example = "1")
    }, responses = @ApiResponse(responseCode = "201", description = SwaggerConstants.ASSIGN_PROFILE_RESPONSE_201_DESCRIPTION, content = @Content(examples = @ExampleObject(description = "OK", value = "Assign profile to client successfully. Status code is 201"))))
    @PostMapping(value = "/{profileId}/assign/client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileDto> assignProfileToClient(
            @PathVariable Long clientId, @PathVariable Long profileId) {
        return ResponseEntity.ok(this.profileService.assignProfileToClient(profileId, clientId));
    }

    /**
     * To assign profile to current user.
     *
     * @param profileId refers to an identified property of {@link Profile}
     * @see ProfileService#assignProfileToCurrentUser(Long profileId)
     */
    @Operation(operationId = "assignProfileToCurrentUser", summary = "To assign profile to current user base on profile's id", description = SwaggerConstants.ASSIGN_PROFILE_TO_CURRENT_USER_DESCRIPTION, parameters = {
            @Parameter(name = "profileId", in = ParameterIn.PATH, description = "Profile's id", schema = @Schema(type = "integer", format = "int64"), example = "1")
    }, responses = @ApiResponse(responseCode = "201", description = SwaggerConstants.ASSIGN_PROFILE_TO_CURRENT_USER_RESPONSE_201_DESCRIPTION, content = @Content(examples = @ExampleObject(description = "OK", value = "Assign profile to current user successfully. Status code is 201"))))
    @PostMapping(value = "/{profileId}/assign/user/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfilesDto> assignProfileToCurrentUser(@PathVariable long profileId) {
        return ResponseEntity.ok(this.profileService.assignProfileToCurrentUser(profileId));
    }

    /**
     * To assign profile to user.
     *
     * @param profileId refers to identified property of {@link Profile}
     * @param username  refers to username property of {@link Profile}
     * @see ProfileService#assignProfileToUser(Long profileId, String username)
     */
    @Operation(operationId = "assignProfileToUser", summary = "To assign profile to user base on profile's id and user's username", description = SwaggerConstants.ASSIGN_PROFILE_TO_USER_DESCRIPTION, parameters = {
            @Parameter(name = "profileId", in = ParameterIn.PATH, description = "Profile's id", schema = @Schema(type = "integer", format = "int64"), example = "1"),
            @Parameter(name = "username", in = ParameterIn.PATH, description = "Username", schema = @Schema(type = "string"), example = "John")
    }, responses = @ApiResponse(responseCode = "201", description = SwaggerConstants.ASSIGN_PROFILE_TO_USER_RESPONSE_201_DESCRIPTION, content = @Content(examples = @ExampleObject(description = "OK", value = "Assign profile to user successfully. Status code is 201"))))
    @PostMapping(value = "/{profileId}/assign/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfilesDto> assignProfileToUser(
            @PathVariable Long profileId, @PathVariable String username) {
        return ResponseEntity.ok(this.profileService.assignProfileToUser(profileId, username));
    }

    /**
     * To retrieve key value pairs from {@link Functionality}.
     *
     * @see ProfileService#getFunctionalityPrivilege(String)
     */
    @Operation(operationId = "getKeyValueEnumFunctionality", summary = "To get key value pairs from functionality", description = SwaggerConstants.GET_KEY_VALUE_ENUM_FUNCTIONALITY_DESCRIPTION, parameters = {
            @Parameter(name = "key", in = ParameterIn.QUERY, description = "Key", schema = @Schema(type = "query"), example = "cxm_template")
    }, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerConstants.GET_KEY_VALUE_ENUM_FUNCTIONALITY_RESPONSE_200_DESCRIPTION, content = @Content(schema = @Schema(type = "object", example = SwaggerConstants.LIST_FUNCTIONALITIES_RESPONSE_EXAMPLE)))
    })
    @GetMapping(value = "/functionalities", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getKeyValueEnumFunctionality(
            @RequestParam(defaultValue = "") String key) {
        return profileService.getFunctionalityPrivilege(key);
    }

    /**
     * To retrieve level of user.
     *
     * @param privilegeKey  refers to privilege key
     * @param functionalKey refers to functional key
     * @return level of user as string
     * @see ProfileService#findTopPrivilegeLevelOfUser(boolean isVisibilityLevel,
     *      String
     *      functionalKey, String privilegeKey)
     */
    @Operation(operationId = "loadLevelOfUser", summary = "To load visibility-level of user base on user's functional and privilege key", description = SwaggerConstants.LOAD_LEVEL_OF_USER_DESCRIPTION, parameters = {
            @Parameter(name = "functionalKey", in = ParameterIn.PATH, description = "User's function key", schema = @Schema(type = "string"), example = "cxm_template"),
            @Parameter(name = "privilegeKey", in = ParameterIn.PATH, description = "User's privilege key", schema = @Schema(type = "string"), example = "cxm_template_create")
    }, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerConstants.LOAD_LEVEL_OF_USER_RESPONSE_200_DESCRIPTION)
    })
    @GetMapping(value = "/visibility-level/{functionalKey}/{privilegeKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loadLevelOfUser(
            @PathVariable String functionalKey, @PathVariable String privilegeKey) {
        return ResponseEntity.ok(
                profileService.findTopPrivilegeLevelOfUser(true, functionalKey, privilegeKey));
    }

    /**
     * To load all username of user by function and privilege key.
     *
     * @param privilegeKey  refers to privilege key
     * @param functionalKey refers to functional key
     * @return username of user as list of String
     */
    @Operation(operationId = "loadAllUsernames", summary = "To load all usernames of users base on user's functional and privilege key", description = SwaggerConstants.LOAD_ALL_USERNAME_OF_USER_DESCRIPTION, parameters = {
            @Parameter(name = "functionalKey", in = ParameterIn.PATH, description = "User's function key", schema = @Schema(type = "string"), example = "cxm_template"),
            @Parameter(name = "privilegeKey", in = ParameterIn.PATH, description = "User's privilege key", schema = @Schema(type = "string"), example = "cxm_template_create")
    }, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerConstants.LOAD_ALL_USERNAME_OF_USER_RESPONSE_200_DESCRIPTION)
    })
    @GetMapping(value = "/usernames/{functionalKey}/{privilegeKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> loadAllUsernames(
            @PathVariable String functionalKey, @PathVariable String privilegeKey) {
        return ResponseEntity.ok(
                profileService.loadAllUsernames(true, Optional.of(functionalKey), privilegeKey, ""));
    }

    @GetMapping(value = "/users/services/{functionalKey}/{privilegeKey}/{isVisibilityLevel}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Long>> loadAllServices(@PathVariable String functionalKey,
            @PathVariable String privilegeKey, @PathVariable boolean isVisibilityLevel) {
        return ResponseEntity.ok(
                profileService.loadAllServices(isVisibilityLevel, Optional.of(functionalKey), privilegeKey,
                        ""));
    }

    /**
     * To get privilege level of user.
     *
     * @param functionalKey refers to functional key property of
     *                      {@link Functionality}
     * @param privilegeKey  refers to privilege key property of {@link Privilege}
     * @see ProfileService#findModificationLevelOfUser(String functionalKey, String
     *      privilegeKey)
     */
    @Operation(operationId = "getPrivileges", summary = "To get privilege level of user base on 'functional key' and 'privilege key'", description = SwaggerConstants.GET_PRIVILEGE_DESCRIPTION, parameters = {
            @Parameter(name = "functionalKey", in = ParameterIn.PATH, description = "Function key", schema = @Schema(type = "string"), example = "cxm_template"),
            @Parameter(name = "privilegeKey", in = ParameterIn.PATH, description = "Privileges key", schema = @Schema(type = "string"), example = "cxm_template_create")
    }, responses = @ApiResponse(responseCode = "200", description = SwaggerConstants.GET_PRIVILEGE_RESPONSE_200_DESCRIPTION))
    @GetMapping(value = "/modification-level/{functionalKey}/{privilegeKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPrivileges(
            @PathVariable("functionalKey") String functionalKey,
            @PathVariable("privilegeKey") String privilegeKey) {
        return ResponseEntity.ok(
                this.profileService.findModificationLevelOfUser(functionalKey, privilegeKey));
    }

    /**
     * To get user privilege.
     *
     * @return an object of {@link UserProfilePrivilege}
     */
    @Operation(operationId = "getUserPrivilege", summary = "To get an user privilege", description = SwaggerConstants.GET_USER_PRIVILEGE_DESCRIPTION, responses = @ApiResponse(responseCode = "200", description = SwaggerConstants.GET_USER_PRIVILEGE_RESPONSE_200_DESCRIPTION, content = @Content(schema = @Schema(type = "object", example = SwaggerConstants.GET_KEY_VALUE_ENUM_FUNCTIONALITY_RESPONSE_EXAMPLE))))
    @GetMapping(value = "/user-privileges", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfilePrivilege> getUserPrivilege(
            @RequestParam(value = "forceToChangePassword", defaultValue = "false", required = false) boolean forceToChangePassword
    ) {
        return ResponseEntity.ok(this.profileService.getUserProfilePrivilege(forceToChangePassword));
    }

    /**
     * Load all users that related with profile by id.
     *
     * @param id refers to identified of {@link Profile}
     * @see ProfileService#loadAllUsersByProfileId(long)
     */
    @Operation(operationId = "loadAllUserById", summary = "Load all user's username by profile's id", description = SwaggerConstants.LOAD_ALL_USERNAME_BY_ID_DESCRIPTION, parameters = {
            @Parameter(name = "id", in = ParameterIn.PATH, description = "Profile's id", schema = @Schema(type = "integer", format = "int64"), example = "1")
    }, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerConstants.LOAD_ALL_USERNAME_BY_ID_RESPONSE_200_DESCRIPTION)
    })
    @GetMapping(value = "/{id}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> loadAllUserById(@PathVariable("id") long id) {
        return ResponseEntity.ok(profileService.loadAllUsersByProfileId(id));
    }

    @Operation(operationId = "getUsersRelatedToPrivilege", summary = "To get all users related to a privilege.")
    @GetMapping(value = "/user-privileges-related/{functionKey}/{privilegeKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersRelatedToPrivilege> getUsersRelatedToPrivilege(
            @Parameter(schema = @Schema(type = "string", enumAsRef = true, implementation = Functionality.class)) @PathVariable String functionKey,
            @PathVariable String privilegeKey,
            @RequestParam("isVisibility") boolean isVisibility) {
        return ResponseEntity.ok(
                profileService.getUserPrivilegeRelated(isVisibility, functionKey, privilegeKey));
    }

    @GetMapping("/contain/privilege/{functionKey}/{privilegeKey}")
    public ResponseEntity<Boolean> checkContainsPrivilegeKey(
            @Parameter(schema = @Schema(type = "string", enumAsRef = true, implementation = Functionality.class)) @PathVariable String functionKey,
            @PathVariable String privilegeKey) {
        return ResponseEntity.ok(!profileService.notContainsPrivilege(functionKey, privilegeKey));
    }

    /**
     * To load the list of profile criteria for filtering users by profile.
     *
     * @return the collection of {@link ProfileFilterCriteria}
     */

    @Operation(operationId = "getProfileFilterCriteria", summary = "To load the list of profile criteria for filtering users by profile.")
    @GetMapping("/criteria")
    public ResponseEntity<List<ProfileFilterCriteria>> getProfileFilterCriteria(
            @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection) {
        return ResponseEntity.ok(profileService.findProfileCriteria(sortDirection));
    }

    @Operation(operationId = "getFunctionalitiesByCurrentInvokedUser", summary = "To load the list of functionalities of current invoked user.")
    @GetMapping("/get-functionalities-by-current-user")
    public ResponseEntity<List<String>> getFunctionalitiesByCurrentInvokedUser(
            @RequestParam(value = "clientId", defaultValue = "0") long clientId) {
        return new ResponseEntity<>(
                this.clientService.getFunctionalitiesByCurrentInvokedUser(clientId), HttpStatus.OK);
    }

    @Operation(operationId = "getUserPrivilegeRelatedOwner", summary = "To get all users related to a privilege.")
    @GetMapping(value = "/users/privilege-related-owners/{functionKey}/{privilegeKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPrivilegeDetails> getUserPrivilegeRelatedOwner(
            @Parameter(schema = @Schema(type = "string", enumAsRef = true, implementation = Functionality.class)) @PathVariable String functionKey,
            @PathVariable String privilegeKey,
            @RequestParam("isVisibility") boolean isVisibility,
            @RequestParam(value = "getRelatedUsers", required = false, defaultValue = "true") boolean getRelatedUsers) {
        return ResponseEntity.ok(
                profileService.getUserPrivilegeDetails(
                        isVisibility, functionKey, privilegeKey, getRelatedUsers));
    }

    /**
     * To load all username of user by function and privilege key.
     *
     * @param privilegeKey  refers to privilege key
     * @param functionalKey refers to functional key
     * @return username of user as list of String
     */
    @Operation(operationId = "loadAllUsernames", summary = "To load all user entities of users base on user's functional and privilege key", parameters = {
            @Parameter(name = "functionalKey", in = ParameterIn.PATH, description = "User's function key", schema = @Schema(type = "string"), example = "cxm_template"),
            @Parameter(name = "privilegeKey", in = ParameterIn.PATH, description = "User's privilege key", schema = @Schema(type = "string"), example = "cxm_template_create")
    }, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerConstants.LOAD_ALL_USERNAME_OF_USER_RESPONSE_200_DESCRIPTION)
    })
    @GetMapping(value = "/users/{functionalKey}/{privilegeKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SharedUserEntityDTO>> getAllUserEntities(
            @PathVariable String functionalKey,
            @PathVariable String privilegeKey,
            @RequestParam(value = "isVisibility", defaultValue = "true") boolean isVisibility) {
        return ResponseEntity.ok(
                profileService.loadAllUsersEntities(functionalKey, privilegeKey, isVisibility));
    }

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<List<ProfileFilterCriteria>> getProfileByServicesId(
            @PathVariable("serviceId") Long serviceId) {
        return ResponseEntity.ok(this.profileService.getAllProfilesCriteria(serviceId));
    }

    // add new
    @GetMapping("/client/{id}")
    public ResponseEntity<List<Profile>> getAllProfilesByClientId(@PathVariable String id) {
        List<Long> clientIds = Arrays.stream(id.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<Profile> profilList = new ArrayList<>();
        for (Long clientId : clientIds) {
            profilList.addAll(this.profileService.getAllProfilesByClientId(clientId));
        }
        return new ResponseEntity<>(profilList, HttpStatus.OK);
    }

    @Operation(operationId = "getUserPrivilegeRelatedOwnerDetails", summary = "To get all users details related to a privilege.")
    @GetMapping(value = "/users/privilege-related-owners-details/{functionKey}/{privilegeKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPrivilegeDetailsOwner> getUserPrivilegeRelatedOwnerDetails(
            @Parameter(schema = @Schema(type = "string", enumAsRef = true, implementation = Functionality.class)) @PathVariable String functionKey,
            @PathVariable String privilegeKey,
            @RequestParam("isVisibility") boolean isVisibility,
            @RequestParam(value = "getRelatedUsers", required = false, defaultValue = "true") boolean getRelatedUsers) {
        return ResponseEntity.ok(
                profileService.getUserPrivilegeDetailsOwner(
                        isVisibility, functionKey, privilegeKey, getRelatedUsers));
    }
}
