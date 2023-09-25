package com.tessi.cxm.pfl.ms3.controller;

import com.tessi.cxm.pfl.ms3.controller.swagger.FlowTraceabilityControllerSwaggerConstants;
import com.tessi.cxm.pfl.ms3.dto.ApprovalRequest;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentValidationRequest;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.FlowValidationResponse;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentDetails_;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument_;
import com.tessi.cxm.pfl.ms3.service.FlowTraceabilityValidationService;
import com.tessi.cxm.pfl.ms3.util.FlowDocumentChannel;
import com.tessi.cxm.pfl.shared.document.SharedSwaggerConstants;
import com.tessi.cxm.pfl.shared.model.SummaryFlowValidation;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "Flow Validation", description = "Manage Validation flow")
@RestController
@RequestMapping("/v1/validation")
@RequiredArgsConstructor
public class FlowValidationController {
  private final FlowTraceabilityValidationService flowTraceabilityValidationService;

  @Operation(
      operationId = "findAllFlowTraceabilityValidation",
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
  @GetMapping("/flow/{page}/{pageSize}")
  public ResponseEntity<EntityResponseHandler<ListFlowTraceabilityDto>> getFlowValidation(
      @PathVariable("page") int page,
      @PathVariable("pageSize") int pageSize,
      @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField,
      @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
      @RequestParam(value = "filter", defaultValue = "") String filter,
      @RequestParam(value = "channels", required = false) List<String> channels,
      @RequestParam(value = "categories", required = false) List<String> categories,
      @RequestParam(value = "users", required = false) List<Long> users,
      @RequestParam(value = "startDate", defaultValue = "") String startDate,
      @RequestParam(value = "endDate", defaultValue = "") String endDate) {

    final String sortField =
        sortByField.equalsIgnoreCase("totalRemainingValidationDocument")
            ? "flowTraceabilityValidationDetails.totalDocumentValidation"
            : sortByField;
    Pageable pageable =
        PageRequest.of(page - 1, pageSize, Sort.Direction.fromString(sortDirection), sortField);
    var filterCriteria = new FlowFilterCriteria();
    filterCriteria.setFilter(filter);
    filterCriteria.setUsers(users);
    filterCriteria.setChannels(channels);
    filterCriteria.setCategories(categories);
    filterCriteria.setStartDate(startDate);
    filterCriteria.setEndDate(endDate);
    final Page<ListFlowTraceabilityDto> flowValidationList =
        this.flowTraceabilityValidationService.getFlowValidationList(pageable, filterCriteria);
    return new ResponseEntity<>(new EntityResponseHandler<>(flowValidationList), HttpStatus.OK);
  }

  @PutMapping("/flow")
  public ResponseEntity<FlowValidationResponse> validateFlow(@RequestBody ApprovalRequest request) {
    return ResponseEntity.ok(
        this.flowTraceabilityValidationService.validateFlow(
            request.getFileIds(), request.getStatus()));
  }

  @Operation(
      operationId = "getFlowDocumentValidationList",
      summary =
          "To find all documents by flow traceability identity"
              + " with pagination and ordering depends on the query and path parameters.",
      parameters = {
        @Parameter(
            name = "page",
            in = ParameterIn.QUERY,
            description = SharedSwaggerConstants.PAGINATION_PAGE_DESCRIPTION,
            schema = @Schema(type = "integer"),
            example = "1"),
        @Parameter(
            name = "pageSize",
            in = ParameterIn.QUERY,
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
            name = "flowTraceabilityId",
            in = ParameterIn.QUERY,
            description = SharedSwaggerConstants.PAGINATION_PAGE_SIZE_DESCRIPTION,
            schema = @Schema(type = "integer", format = "int64"),
            example = "1"),
        @Parameter(
            name = "filter",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_FILTER_DESCRIPTION,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "channels",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CHANNEL_DESCRIPTION,
            schema =
                @Schema(
                    type = "array",
                    enumAsRef = true,
                    implementation = FlowDocumentChannel.class)),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            description =
                FlowTraceabilityControllerSwaggerConstants
                    .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_SUB_CHANNEL_DESCRIPTION,
            schema =
                @Schema(
                    type = "array",
                    enumAsRef = true,
                    implementation = FlowDocumentSubChannel.class)),
      },
      responses =
          @ApiResponse(
              responseCode = "200",
              description = "List of Flow document with pagination by Flow Id."))
  @GetMapping("/document")
  public ResponseEntity<EntityResponseHandler<FlowDocumentDto>> getFlowValidation(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "pageSize", defaultValue = "0") int pageSize,
      @RequestParam(value = "flowId", defaultValue = "0") int flowId,
      @RequestParam(value = "filter", defaultValue = "") String filter,
      @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField,
      @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection) {
    if (page == 0) {
      final Page<FlowDocumentDto> flowValidationList =
          this.flowTraceabilityValidationService.getFlowDocumentValidationList(
              filter, flowId, Pageable.unpaged());
      return new ResponseEntity<>(new EntityResponseHandler<>(flowValidationList), HttpStatus.OK);
    }
    if (sortByField.equals(FlowDocumentDetails_.DOC_NAME)) {
      sortByField = FlowDocument_.DETAIL.concat(".".concat(FlowDocumentDetails_.DOC_NAME));
    }
    Pageable pageable =
        PageRequest.of(page - 1, pageSize, Sort.Direction.fromString(sortDirection), sortByField);
    final Page<FlowDocumentDto> flowValidationList =
        this.flowTraceabilityValidationService.getFlowDocumentValidationList(
            filter, flowId, pageable);
    return new ResponseEntity<>(new EntityResponseHandler<>(flowValidationList), HttpStatus.OK);
  }

  @PostMapping("/document")
  public ResponseEntity<Object> validateFlowDocument(
      @RequestBody @Valid FlowDocumentValidationRequest request) {
    this.flowTraceabilityValidationService.validateFlowDocument(request);
    return ResponseEntity.ok(Optional.empty());
  }


  @GetMapping("/remaining")
  public ResponseEntity<SummaryFlowValidation> getUserValidationSpace() {
    final var result = this.flowTraceabilityValidationService.getUserFlowValidation();
    return ResponseEntity.ok(result);
  }
}
