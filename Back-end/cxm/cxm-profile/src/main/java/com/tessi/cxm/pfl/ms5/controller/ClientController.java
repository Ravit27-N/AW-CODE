package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.constant.SwaggerConstants;
import com.tessi.cxm.pfl.ms5.dto.ClientCriteriaDto;
import com.tessi.cxm.pfl.ms5.dto.ClientDto;
import com.tessi.cxm.pfl.ms5.dto.ClientServiceDetailsDTO;
import com.tessi.cxm.pfl.ms5.dto.LoadClient;
import com.tessi.cxm.pfl.ms5.dto.PublicHolidayDto;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Functionalities;
import com.tessi.cxm.pfl.ms5.service.ClientService;
import com.tessi.cxm.pfl.ms5.service.StorageFileService;
import com.tessi.cxm.pfl.shared.model.PortalSettingConfigStatusDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationVersion;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationVersionDto;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Client Management", description = "The API endpoint to manage the client")
public class ClientController {

  private final ClientService clientService;
  private final StorageFileService storageFileService;

  /**
   * To retrieve a list of {@link ClientDto}
   *
   * @return a list of {@link ClientDto}
   * @see ClientService#findAll()
   */
  @Operation(
      operationId = "getAllClient",
      summary = "To get a list of client",
      description = SwaggerConstants.GET_ALL_CLIENT_DESCRIPTION,
      responses =
      @ApiResponse(
          responseCode = "200",
          description =
              SwaggerConstants.GET_ALL_CLIENT_RESPONSE_200_DESCRIPTION))
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{page}/{pageSize}")
  public ResponseEntity<EntityResponseHandler<LoadClient>> getAllClient(
      @Parameter(
          in = ParameterIn.PATH,
          schema = @Schema(type = "integer"),
          example = "1")
      @PathVariable int page,
      @Parameter(
          in = ParameterIn.PATH,
          schema = @Schema(type = "integer"),
          example = "10")
      @PathVariable int pageSize,
      @Parameter(
          in = ParameterIn.QUERY,
          schema = @Schema(type = "string"),
          example = "lastModified")
      @RequestParam(value = "sortByField", defaultValue = "lastModified") String sortByField,
      @Parameter(
          in = ParameterIn.QUERY,
          schema = @Schema(type = "string"),
          example = "desc")
      @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
      @RequestParam(value = "filter", defaultValue = "") String filter) {
    Pageable pageable =
        PageRequest.of(page - 1, pageSize, Sort.Direction.fromString(sortDirection), sortByField);
    return new ResponseEntity<>(
        new EntityResponseHandler<>(this.clientService.loadAllClients(pageable, filter)), HttpStatus.OK);
  }

  /**
   * To create a new object of {@link ClientDto}
   *
   * @return an object of {@link ClientDto}
   * @see ClientService#save(ClientDto dto)
   */
  @Operation(
      operationId = "createClient",
      summary = "To create client",
      description = SwaggerConstants.CREATE_CLIENT_DESCRIPTION,
      responses =
      @ApiResponse(
          responseCode = "201",
          description =
              SwaggerConstants.CREATE_CLIENT_RESPONSE_201_DESCRIPTION))
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientDto> createClient(@RequestBody @Validated ClientDto clientDto) {
    return new ResponseEntity<>(this.clientService.save(clientDto), HttpStatus.CREATED);
  }

  /**
   * To update an existing object of {@link ClientDto}
   *
   * @return an updated object of {@link ClientDto}
   * @see ClientService#update(ClientDto dto)
   */
  @Operation(
      operationId = "updateClient",
      summary = "To update client",
      description = SwaggerConstants.UPDATE_CLIENT_DESCRIPTION,
      responses =
      @ApiResponse(
          responseCode = "200",
          description =
              SwaggerConstants.UPDATE_CLIENT_DESCRIPTION_200_DESCRIPTION))
  @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ClientDto> updateClient(@RequestBody @Validated ClientDto clientDto) {
    return new ResponseEntity<>(this.clientService.update(clientDto), HttpStatus.OK);
  }

  /**
   * To delete an existing object of {@link ClientDto}
   *
   * @param id refers to identified property of {@link ClientDto}
   * @see ClientService#delete(Long id)
   */
  @Operation(
      operationId = "delete",
      summary = "To delete a client base on its id",
      description = SwaggerConstants.DELETE_CLIENT_DESCRIPTION,
      parameters = {
          @Parameter(
              name = "id",
              in = ParameterIn.PATH,
              description = "Client's id",
              schema = @Schema(type = "integer"),
              example = "1")
      },
      responses =
      @ApiResponse(
          responseCode = "204",
          description = SwaggerConstants.DELETE_CLIENT_204_DESCRIPTION,
          content =
          @Content(
              examples =
              @ExampleObject(
                  description = "No Content",
                  value =
                      "Delete an existed client by id successfully. Status code is 204."))))
  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HttpStatus> delete(@PathVariable long id) {
    this.clientService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * To retrieve all username of users in the client or the organization.
   *
   * @see ClientService#getAllUsersInClient(long)
   */
  @Operation(
      operationId = "getAllUsersInClient",
      summary = "To retrieve all username of users in the client or the organization.")
  @GetMapping("/client/{id}/users")
  public ResponseEntity<List<String>> getAllUsersInClient(
      @Parameter(
          description = "Client's id",
          schema = @Schema(type = "integer", format = "int64"),
          example = "1")
      @PathVariable
      long id) {
    return ResponseEntity.ok(clientService.getAllUsersInClient(id));
  }

  /**
   * To check if the {@link Client} of current invoking user has at least one profile set.
   *
   * @return True is at least one profile is existed, otherwise false.
   */
  @Operation(
      operationId = "isClientActiveProfileNotEmpty",
      summary =
          "To check if the client of current login user has at least one active profile configured.")
  @GetMapping(value = "/profiles/available", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Boolean> isClientActiveProfileNotEmpty() {
    return ResponseEntity.ok(
        this.clientService.isClientProfileNotEmpty(Optional.empty()));
  }

  @GetMapping("/is-duplicate")
  public ResponseEntity<Boolean> validateDuplicateName(
      @RequestParam(value = "name") String name,
      @RequestParam(value = "id", defaultValue = "0") long id) {
    return ResponseEntity.ok(this.clientService.validateDuplicateName(id, name));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClientDto> getClientById(@PathVariable long id) {
    return ResponseEntity.ok(clientService.findById(id));
  }

  @Operation(
          operationId = "getFunctionalities",
          summary = "To load all client functionalities by client id.")
  @GetMapping("/{clientId}/functionalities")
  public ResponseEntity<List<String>> getFunctionalities(@PathVariable long clientId) {
    return new ResponseEntity<>(
        this.clientService.getFunctionalitiesByClientId(clientId).stream()
            .map(Functionalities::getKey)
            .collect(Collectors.toList()),
        HttpStatus.OK);
  }

  @GetMapping("/public-holiday")
  public ResponseEntity<List<PublicHolidayDto>> getAllNationalHolidayEvents() {
    return ResponseEntity.ok(this.clientService.getAllNationalHolidayEvents());
  }

  @GetMapping("/client-unloads")
  public ResponseEntity<SharedClientUnloadDetailsDTO> getClientUnloads() {
    return ResponseEntity.ok(this.clientService.getClientUnloads());
  }

  @GetMapping("/client-fillers")
  public ResponseEntity<List<SharedClientFillersDTO>> getAllClientFillers(
      @RequestParam(value = "clientId", required = false) Long clientId,
      @RequestParam(value = "resolve-value", defaultValue = "false") boolean isResolveValue) {
    return ResponseEntity.ok(this.clientService.getAllClientFillers(clientId, isResolveValue));
  }

  @GetMapping("/client-criteria")
  public ResponseEntity<List<ClientCriteriaDto>> getClientCriteria(
      @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection
  ) {
    return ResponseEntity.ok(this.clientService.getClientCriteria(sortDirection));
  }

  @GetMapping("/services")
  public ResponseEntity<ClientServiceDetailsDTO> getServices(
      @RequestParam(name = "clientId") Optional<Long> clientId) {
    return ResponseEntity.ok(this.clientService.getServices(clientId));
  }

  @PutMapping("/portal-setting-config")
  public ResponseEntity<PortalSettingConfigStatusDto> modifiedPortalSettingConfig(
      @RequestBody PortalSettingConfigStatusDto dto) {
    return ResponseEntity.ok(this.clientService.modifiedPortalSettingConfig(dto));
  }

  @GetMapping("/portal/configuration")
  public ResponseEntity<PostalConfigurationDto> getPostalConfiguration(
      @RequestParam("clientName") String clientName) {
    return ResponseEntity.ok(this.clientService.getPostalConfiguration(clientName));
  }

  @PutMapping("/portal/configuration")
  public ResponseEntity<PostalConfigurationDto> modifiedINIConfiguration(
      @RequestBody PostalConfigurationDto dto) {
    return ResponseEntity.ok(this.clientService.modifiedINIConfiguration(dto));
  }

  /**
   * Get a file content as text in UTF-8 format of configuration file (.ini)).
   *
   * @return File content as text in UTF-8 format.
   */
  @GetMapping("/portal/configuration/file")
  public ResponseEntity<String> downloadConfigFile(
      @RequestParam("clientName") String clientName, @RequestHeader HttpHeaders headers) {
    var fileContent =
        this.storageFileService.downloadConfigurationFile(
            clientName, headers.getFirst(HttpHeaders.AUTHORIZATION));
    return fileContent != null
        ? ResponseEntity.ok(fileContent)
        : ResponseEntity.noContent().build();
  }

  @GetMapping("/portal/configuration/versions")
  public ResponseEntity<List<PostalConfigurationVersion>> getPostalConfigurationVersions(
      @RequestParam("clientName") String clientName) {
    return ResponseEntity.ok(this.clientService.getPostalConfigurationVersions(clientName));
  }

  @GetMapping("/portal/configuration/version")
  public ResponseEntity<PostalConfigurationVersionDto> getPostalConfigurationVersion(
      @RequestParam("clientName") String clientName,
      @RequestParam("version") int version) {
    return ResponseEntity.ok(this.clientService.getPostalConfigurationVersion(clientName, version));
  }

  @PostMapping("/portal/configuration/version/revert")
  public ResponseEntity<PostalConfigurationVersion> revertPostalConfiguration(
      @RequestParam("clientName") String clientName,
      @RequestParam("referenceVersion") int referenceVersion) {
    return ResponseEntity.ok(
        this.clientService.revertPostalConfiguration(clientName, referenceVersion));
  }
}
