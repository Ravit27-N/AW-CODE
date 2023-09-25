package com.tessi.cxm.pfl.ms3.controller;

import com.tessi.cxm.pfl.ms3.controller.swagger.FlowTraceabilityControllerSwaggerConstants;
import com.tessi.cxm.pfl.ms3.dto.FlowDepositDto;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit_;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability_;
import com.tessi.cxm.pfl.ms3.service.FlowDepositService;
import com.tessi.cxm.pfl.ms3.util.CustomPageableRequest;
import com.tessi.cxm.pfl.shared.document.SharedSwaggerConstants;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Flow Deposit", description = "Manage Deposit flow")
@RestController
@RequestMapping("/v1/flow-deposit")
@RequiredArgsConstructor
public class FlowDepositController {
  private final FlowDepositService flowDepositService;

  @Operation(
      operationId = "getFlowDeposit",
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
            name = "subChannels",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_SUB_CHANNEL_DESCRIPTION,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Reco", "Reco AR", "CSE", "CSE AR", "LRE", "email", "sms"})),
        @Parameter(
            name = "users",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_BY_DESCRIPTION,
            schema = @Schema(type = "array")),
      },
      responses =
          @ApiResponse(
              responseCode = "200",
              description =
                  FlowTraceabilityControllerSwaggerConstants
                      .FIND_ALL_FLOW_TRACEABILITY_RESPONSE_200_DESCRIPTION))
  @GetMapping("/{page}/{pageSize}")
  public ResponseEntity<EntityResponseHandler<FlowDepositDto>> getFlowDeposit(
      @PathVariable("page") int page,
      @PathVariable("pageSize") int pageSize,
      @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField,
      @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
      @RequestParam(value = "filter", defaultValue = "") String filter,
      @RequestParam(value = "channels", required = false) List<String> channels,
      @RequestParam(value = "subChannels", required = false) List<String> subChannels,
      @RequestParam(value = "depositModes", required = false) List<String> depositModes,
      @RequestParam(value = "users", required = false) List<Long> users,
      @RequestParam(value = "fileId", required = false) String fileId) {
    List<String> supProperties =
        Stream.of(
                FlowTraceability_.FLOW_NAME,
                FlowTraceability_.CHANNEL,
                FlowTraceability_.SUB_CHANNEL,
                FlowTraceability_.DEPOSIT_MODE,
                FlowTraceability_.STATUS)
            .map(s -> FlowDeposit_.FLOW_TRACEABILITY.concat(".".concat(s)))
            .collect(Collectors.toList());
    Pageable pageable =
        CustomPageableRequest.from(
            page - 1,
            pageSize,
            Sort.Direction.fromString(sortDirection),
            sortByField,
            supProperties);
    final FlowFilterCriteria flowFilterCriteria = new FlowFilterCriteria();
    flowFilterCriteria.setChannels(channels);
    flowFilterCriteria.setUsers(users);
    flowFilterCriteria.setFilter(filter);
    flowFilterCriteria.setCategories(subChannels);
    flowFilterCriteria.setDepositModes(depositModes);
    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.flowDepositService.findAll(flowFilterCriteria, pageable, fileId)),
        HttpStatus.OK);
  }

  @PatchMapping("/delete/{fileId}")
  public ResponseEntity<Void> deleteFlowDeposit(@PathVariable("fileId") String fileId) {
    this.flowDepositService.delete(fileId);
    return ResponseEntity.ok().build();
  }
}
