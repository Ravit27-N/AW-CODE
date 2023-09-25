package com.tessi.cxm.pfl.ms3.controller;

import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_FLOW_TRACEABILITY;

import com.tessi.cxm.pfl.ms3.controller.swagger.FlowTraceabilityControllerSwaggerConstants;
import com.tessi.cxm.pfl.ms3.dto.DepositFlowInfoDto;
import com.tessi.cxm.pfl.ms3.dto.FlowCampaignDto;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteriaDto;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.service.FlowTraceabilityService;
import com.tessi.cxm.pfl.shared.document.SharedSwaggerConstants;
import com.tessi.cxm.pfl.shared.model.SharedUserEntityDTO;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.utils.ComputerSystemProduct;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.FlowDepositArea;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Flow Traceability", description = "Manage Flow Traceability")
@RestController
@RequestMapping("/v1/flow-traceability")
@RequiredArgsConstructor
public class FlowTraceabilityController {

  private final FlowTraceabilityService flowTraceabilityService;

  /**
   * Endpoint use to get pagination {@link FlowTraceabilityDto}.
   *
   * @see FlowTraceabilityService#findAll(Pageable, FlowFilterCriteria)
   */
  @Operation(
      operationId = "findAllFlowTraceability",
      summary = FlowTraceabilityControllerSwaggerConstants.FIND_ALL_FLOW_TRACEABILITY_SUMMARY,
      description =
          FlowTraceabilityControllerSwaggerConstants.FIND_ALL_FLOW_TRACEABILITY_DESCRIPTION,
      parameters = {
        @Parameter(
            name = "page",
            in = ParameterIn.PATH,
            description = SharedSwaggerConstants.PAGINATION_PAGE_DESCRIPTION,
            schema = @Schema(type = "integer"),
            example = "1"),
        @Parameter(
            name = "pageSize",
            in = ParameterIn.PATH,
            description = SharedSwaggerConstants.PAGINATION_PAGE_SIZE_DESCRIPTION,
            schema = @Schema(type = "integer"),
            example = "15"),
        @Parameter(
            name = "sortByField",
            in = ParameterIn.QUERY,
            description = SharedSwaggerConstants.SORTING_BY_FIELD_DESCRIPTION,
            schema = @Schema(type = "string"),
            example = "createdAt"),
        @Parameter(
            name = "sortDirection",
            in = ParameterIn.QUERY,
            description = SharedSwaggerConstants.SORTING_BY_DIRECTION_DESCRIPTION,
            schema = @Schema(type = "string"),
            example = "desc"),
        @Parameter(
            name = "filter",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_FILTER_DESCRIPTION,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_SUB_CHANNEL_DESCRIPTION,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Reco", "Reco AR", "CSE", "CSE AR", "LRE", "email", "sms"})),
        @Parameter(
            name = "depositModes",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_DEPOSIT_MODE_DESCRIPTION,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"IV", "Portal", "API", "Batch"})),
        @Parameter(
            name = "status",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_STATUS_DESCRIPTION,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {
                      "Deposited",
                      "Canceled",
                      "In Error",
                      "Scheduled",
                      "Completed",
                      "In Process",
                      "To Validate",
                      "To finalize",
                      "Finalized",
                      "Validated",
                      "In progress"
                    }),
            example = "In Creation"),
        @Parameter(
            name = "users",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_BY_DESCRIPTION,
            schema = @Schema(type = "array")),
        @Parameter(
            name = "startDate",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_DEPOSIT_DATE_START_DESCRIPTION,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_DEPOSIT_DATE_END_DESCRIPTION,
            schema = @Schema(type = "string")),
      },
      responses =
          @ApiResponse(
              responseCode = "200",
              description =
                  FlowTraceabilityControllerSwaggerConstants
                      .FIND_ALL_FLOW_TRACEABILITY_RESPONSE_200_DESCRIPTION))
  @GetMapping(value = "/{page}/{pageSize}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EntityResponseHandler<ListFlowTraceabilityDto>> findAll(
      @PathVariable("page") int page,
      @PathVariable("pageSize") int pageSize,
      @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField,
      @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
      @RequestParam(value = "filter", defaultValue = "") String filter,
      @RequestParam(value = "channels", required = false) List<String> channels,
      @RequestParam(value = "categories", required = false) List<String> categories,
      @RequestParam(value = "depositModes", required = false) List<String> depositModes,
      @RequestParam(value = "status", required = false) List<String> status,
      @RequestParam(value = "users", required = false) List<Long> users,
      @RequestParam(value = "startDate", defaultValue = "") String startDate,
      @RequestParam(value = "endDate", defaultValue = "") String endDate) {

    Pageable pageable =
        PageRequest.of(page - 1, pageSize, Sort.Direction.fromString(sortDirection), sortByField);
    var filterCriteria = new FlowFilterCriteria();
    filterCriteria.setFilter(filter);
    filterCriteria.setUsers(users);
    filterCriteria.setChannels(channels);
    filterCriteria.setCategories(categories);
    filterCriteria.setStatus(status);
    filterCriteria.setDepositModes(depositModes);
    filterCriteria.setStartDate(startDate);
    filterCriteria.setEndDate(endDate);
    return new ResponseEntity<>(
        new EntityResponseHandler<>(flowTraceabilityService.findAll(pageable, filterCriteria)),
        HttpStatus.OK);
  }

  /**
   * update status of flow traceability.
   *
   * @see FlowTraceabilityService#updateFlowStatus(long, String, String)
   */
  @Operation(
      operationId = "updateStatusOfFlowTraceability",
      summary =
          FlowTraceabilityControllerSwaggerConstants.UPDATE_STATUS_OF_FLOW_TRACEABILITY_SUMMARY,
      description =
          FlowTraceabilityControllerSwaggerConstants.UPDATE_STATUS_OF_FLOW_TRACEABILITY_DESCRIPTION,
      parameters = {
        @Parameter(
            name = "id",
            in = ParameterIn.PATH,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .UPDATE_STATUS_OF_FLOW_TRACEABILITY_REQUEST_PARAMS_ID_DESCRIPTION,
            schema = @Schema(type = "integer", format = "int64"),
            example = "1"),
        @Parameter(
            name = "status",
            in = ParameterIn.PATH,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .UPDATE_STATUS_OF_FLOW_TRACEABILITY_REQUEST_PARAMS_STATUS_DESCRIPTION,
            schema = @Schema(type = "string"),
            example = "To Validate")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .UPDATE_STATUS_OF_FLOW_TRACEABILITY_RESPONSE_200_DESCRIPTION)
      })
  @PatchMapping(
      value = "/status/{id}/{status}/{server}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FlowTraceabilityDto> updateStatus(
      @PathVariable long id, @PathVariable String status, @PathVariable String server) {
    return ResponseEntity.ok(
        flowTraceabilityService.updateFlowStatus(id, status, ComputerSystemProduct.getDeviceId()));
  }

  /**
   * get status list of flow traceability.
   *
   * @return return {@link Map}
   */
  @Operation(
      operationId = "getStatus",
      summary = FlowTraceabilityControllerSwaggerConstants.GET_ALL_STATUS_SUMMARY,
      description = FlowTraceabilityControllerSwaggerConstants.GET_ALL_STATUS_DESCRIPTION,
      responses =
      @ApiResponse(
          responseCode = "200",
          description =
              FlowTraceabilityControllerSwaggerConstants
                  .GET_ALL_STATUS_RESPONSE_200_DESCRIPTION,
          content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema =
              @Schema(
                  type = "object",
                  example =
                      FlowTraceabilityControllerSwaggerConstants
                          .GET_ALL_STATUS_RESPONSE_200_EXAMPLE))))
  @GetMapping("/status")
  public ResponseEntity<Map<String, Object>> getStatus() {
    return ResponseEntity.ok(
        Map.of(FlowTraceabilityConstant.FLOW_STATUS, FlowTraceabilityStatus.getFilterKeyValue()));
  }

  /**
   * load filter criteria of flow traceability.
   *
   * @see FlowTraceabilityService#loadFlowFilterCriteria()
   */
  @Operation(
      operationId = "loadFlowFilterCriteria",
      summary =
          FlowTraceabilityControllerSwaggerConstants.LOAD_ALL_FLOW_TRACEABILITY_CRITERIA_SUMMARY,
      description =
          FlowTraceabilityControllerSwaggerConstants
              .LOAD_ALL_FLOW_TRACEABILITY_CRITERIA_DESCRIPTION,
      responses =
      @ApiResponse(
          responseCode = "200",
          description =
              FlowTraceabilityControllerSwaggerConstants
                  .LOAD_ALL_FLOW_TRACEABILITY_CRITERIA_RESPONSE_200_DESCRIPTION))
  @GetMapping(value = "/filter-criteria", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FlowFilterCriteriaDto> loadFlowFilterCriteria() {
    return ResponseEntity.ok(flowTraceabilityService.loadFlowFilterCriteria());
  }

  /**
   * To retrieve the users into service that depend on the user logged in and the functionality key
   * and privilege key.
   *
   * @see FlowTraceabilityService#getUsersPrivilege(String, String)
   * @return return list of {@link User}
   */
  @Operation(
      operationId = "getAllUserInService",
      summary = FlowTraceabilityControllerSwaggerConstants.GET_ALL_USERS_SUMMARY,
      description = FlowTraceabilityControllerSwaggerConstants.GET_ALL_USERS_DESCRIPTION,
      responses =
          @ApiResponse(
              responseCode = "200",
              description =
                  FlowTraceabilityControllerSwaggerConstants
                      .GET_ALL_USERS_RESPONSE_200_DESCRIPTION))
  @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SharedUserEntityDTO>> getAllUserInService() {

    return ResponseEntity.ok(
        flowTraceabilityService.getUsersPrivilege(CXM_FLOW_TRACEABILITY, Privilege.LIST));
  }

  /**
   * get all members in department from keycloak in deposit area.
   *
   * @return return list of {@link User}
   */
  @Operation(
      operationId = "getAllMembersInDepartmentInDeposit",
      summary = FlowTraceabilityControllerSwaggerConstants.GET_ALL_USERS_SUMMARY,
      description = FlowTraceabilityControllerSwaggerConstants.GET_ALL_USERS_DESCRIPTION,
      responses =
          @ApiResponse(
              responseCode = "200",
              description =
                  FlowTraceabilityControllerSwaggerConstants
                      .GET_ALL_USERS_RESPONSE_200_DESCRIPTION))
  @GetMapping(value = "/users/deposit", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SharedUserEntityDTO>> getAllMembersInDepartmentInDeposit() {

    return ResponseEntity.ok(
        flowTraceabilityService.getUsersPrivilege(
            ProfileConstants.CXM_FLOW_DEPOSIT, FlowDepositArea.LIST_DEPOSITS));
  }

  /**
   * Endpoint used to get {@link FlowTraceability} by its id.
   *
   * @param id refer to {@link FlowTraceability} identity.
   * @return {@link FlowTraceabilityDto} instead of {@link FlowTraceability}.
   * @see FlowTraceabilityService#findById(Long)
   */
  @GetMapping("/{id}")
  public ResponseEntity<FlowTraceabilityDto> getFlowTraceabilityById(@PathVariable long id) {
    return new ResponseEntity<>(flowTraceabilityService.findById(id), HttpStatus.OK);
  }

  /**
   * Endpoint used to get {@link FlowTraceability} related with campaign by its id.
   *
   * @param id refer to {@link FlowTraceability} identity.
   * @return {@link FlowTraceabilityDto} instead of {@link FlowTraceability}.
   * @see FlowTraceabilityService#findById(Long)
   */
  @GetMapping("/{id}/campaign")
  public ResponseEntity<FlowCampaignDto> getFlowCampaignDetailById(@PathVariable long id) {
    return new ResponseEntity<>(
        flowTraceabilityService.getFlowCampaignDetailById(id), HttpStatus.OK);
  }

  @GetMapping("deposit-info/{id}")
  public ResponseEntity<DepositFlowInfoDto> getComposedFileIdAndStep(@PathVariable long id) {
    return new ResponseEntity<>(flowTraceabilityService.getDepositFlowInfo(id), HttpStatus.OK);
  }

  @PatchMapping("/deposit-info/{id}/{step}/{composedId}")
  public ResponseEntity<DepositFlowInfoDto> updateComposedFileIdAndStep(
      @PathVariable long id,
      @PathVariable int step,
      @PathVariable String composedId,
      @RequestParam(value = "validation", defaultValue = "false") boolean validation) {
    return new ResponseEntity<>(
        flowTraceabilityService.updateDepositFlow(id, step, composedId, validation), HttpStatus.OK);
  }
}
