package com.tessi.cxm.pfl.ms3.controller;

import com.tessi.cxm.pfl.ms3.controller.swagger.FlowDocumentControllerSwaggerConstants;
import com.tessi.cxm.pfl.ms3.controller.swagger.FlowTraceabilityControllerSwaggerConstants;
import com.tessi.cxm.pfl.ms3.dto.*;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.service.FlowDocumentService;
import com.tessi.cxm.pfl.ms3.util.FlowDocumentChannel;
import com.tessi.cxm.pfl.shared.document.SharedSwaggerConstants;
import com.tessi.cxm.pfl.shared.model.SharedStatusInfoDto;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/v1/flow-document")
@Tag(
        name = "Document of flow traceability",
        description = "The endpoints to manage flow traceability's documents")
public class FlowDocumentController {

  private final FlowDocumentService flowDocumentService;



  public FlowDocumentController(
          FlowDocumentService flowDocumentService) {
    this.flowDocumentService = flowDocumentService;
  }

  /**
   * To create new document of {@link FlowTraceability}.
   *
   * @param dto refer to object of {@link FlowDocumentDto}
   * @return object of {@link FlowDocumentDto}
   */
  @Operation(
          operationId = "addNew",
          summary = FlowDocumentControllerSwaggerConstants.ADD_FLOW_DOCUMENT_SUMMARY,
          description = FlowDocumentControllerSwaggerConstants.ADD_FLOW_DOCUMENT_DESCRIPTION)
  @PostMapping
  public ResponseEntity<FlowDocumentDto> save(@RequestBody @Validated FlowDocumentDto dto) {
    return new ResponseEntity<>(flowDocumentService.save(dto), HttpStatus.CREATED);
  }

  /**
   * To retrieve the filter criteria of {@link FlowDocumentDto}.
   */
  @Operation(
          operationId = "getFilterCriteria",
          summary = "To get required filter criteria of document.")
  @GetMapping("/filter-criteria")
  public ResponseEntity<FlowDocumentFilterCriteriaDto> getFilterCriteria(
          @RequestParam(value = "channel", defaultValue = "Multiple") String channel) {
    return ResponseEntity.ok(flowDocumentService.getFilterCriteria(channel));
  }

  @Operation(
          operationId = "findAll",
          summary = FlowDocumentControllerSwaggerConstants.FIND_ALL_FLOW_DOCUMENT_SUMMARY,
          description = FlowDocumentControllerSwaggerConstants.FIND_ALL_FLOW_DOCUMENT_DESCRIPTION,
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
                          name = "startDate",
                          in = ParameterIn.QUERY,
                          description =
                                  FlowTraceabilityControllerSwaggerConstants
                                          .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_AT_DATE_START_DESCRIPTION,
                          schema = @Schema(type = "string")),
                  @Parameter(
                          name = "endDate",
                          in = ParameterIn.QUERY,
                          description =
                                  FlowTraceabilityControllerSwaggerConstants
                                          .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_AT_DATE_END_DESCRIPTION,
                          schema = @Schema(type = "string")),
                  @Parameter(
                          name = "status",
                          in = ParameterIn.QUERY,
                          description =
                                  FlowTraceabilityControllerSwaggerConstants
                                          .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_STATUS_DESCRIPTION,
                          schema =
                          @Schema(
                                  type = "array",
                                  enumAsRef = true,
                                  implementation = FlowDocumentStatus.class)),
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
                  description =
                          FlowDocumentControllerSwaggerConstants
                                  .FIND_ALL_FLOW_DOCUMENT_RESPONSE_200_DESCRIPTION))
  @GetMapping(value = "/{page}/{pageSize}")
  public ResponseEntity<EntityResponseHandler<FlowDocumentDto>> findAll(
          @PathVariable("page") int page,
          @PathVariable("pageSize") int pageSize,
          @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField,
          @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
          @RequestParam(value = "filter", defaultValue = "") String filter,
          @RequestParam(value = "channels", required = false) List<String> channels,
          @RequestParam(value = "categories", required = false) List<String> categories,
          @RequestParam(value = "status", required = false) List<String> status,
          @RequestParam(value = "startDate", defaultValue = "") String startDate,
          @RequestParam(value = "endDate", defaultValue = "") String endDate,
          @RequestParam(value = "fillers", required = false) List<String> fillers,
          @RequestParam(value = "searchByFiller", defaultValue = "") String searchByFiller) {
    var criteria = new BaseFilterCriteria();
    criteria.setFilter(filter);
    criteria.setStartDate(startDate);
    criteria.setEndDate(endDate);
    criteria.setChannels(channels);
    criteria.setCategories(categories);
    criteria.setStatus(status);
    criteria.setFillers(fillers);
    criteria.setSearchByFiller(searchByFiller);
    Pageable pageable =
            PageRequest.of(page - 1, pageSize, Sort.Direction.fromString(sortDirection), sortByField);

    return new ResponseEntity<>(
            new EntityResponseHandler<>(flowDocumentService.findAll(pageable, criteria)),
            HttpStatus.OK);
  }

  /**
   * To find all documents by flow traceability identity with pagination and ordering depends on the
   * query and path parameters.
   *
   * @param page          refer to offset
   * @param pageSize      refer to limit size per page
   * @param flowId        refer to the identity of {@link FlowTraceability}
   * @param sortByField   refer to any properties of {@link FlowDocument}
   * @param sortDirection refer to the direction of sorting as ascending or descending
   * @return the object of {@link FlowDocumentDto} wrapped by {@link EntityResponseHandler}
   */
  @Operation(
          operationId = "findAllById",
          summary =
                  "To find all documents by flow traceability identity"
                          + " with pagination and ordering depends on the query and path parameters.",
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
                          name = "flowTraceabilityId",
                          in = ParameterIn.PATH,
                          description = SharedSwaggerConstants.PAGINATION_PAGE_SIZE_DESCRIPTION,
                          schema = @Schema(type = "integer", format = "int64"),
                          example = "1"),
                  @Parameter(
                          name = "startDate",
                          in = ParameterIn.QUERY,
                          description =
                                  FlowTraceabilityControllerSwaggerConstants
                                          .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_AT_DATE_START_DESCRIPTION,
                          schema = @Schema(type = "string")),
                  @Parameter(
                          name = "endDate",
                          in = ParameterIn.QUERY,
                          description =
                                  FlowTraceabilityControllerSwaggerConstants
                                          .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_CREATED_AT_DATE_END_DESCRIPTION,
                          schema = @Schema(type = "string")),
                  @Parameter(
                          name = "status",
                          in = ParameterIn.QUERY,
                          description =
                                  FlowTraceabilityControllerSwaggerConstants
                                          .FIND_ALL_FLOW_TRACEABILITY_REQUEST_PARAMS_STATUS_DESCRIPTION,
                          schema =
                          @Schema(
                                  type = "array",
                                  enumAsRef = true,
                                  implementation = FlowDocumentStatus.class)),
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
  @GetMapping(value = "{flowTraceabilityId}/{page}/{pageSize}")
  public ResponseEntity<EntityResponseHandler<FlowDocumentDto>> findAllById(
          @PathVariable("page") int page,
          @PathVariable("pageSize") int pageSize,
          @PathVariable("flowTraceabilityId") long flowId,
          @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField,
          @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
          @RequestParam(value = "filter", defaultValue = "") String filter,
          @RequestParam(value = "channels", required = false) List<String> channels,
          @RequestParam(value = "categories", required = false) List<String> categories,
          @RequestParam(value = "status", required = false) List<String> status,
          @RequestParam(value = "startDate", defaultValue = "") String startDate,
          @RequestParam(value = "endDate", defaultValue = "") String endDate,
          @RequestHeader HttpHeaders headers,
          @RequestParam(value = "fillers", required = false) List<String> fillers,
          @RequestParam(value = "searchByFiller", defaultValue = "") String searchByFiller) {
    var filterCriteria = new BaseFilterCriteria();
    filterCriteria.setFilter(filter);
    filterCriteria.setChannels(channels);
    filterCriteria.setCategories(categories);
    filterCriteria.setStatus(status);
    filterCriteria.setStartDate(startDate);
    filterCriteria.setEndDate(endDate);
    filterCriteria.setFillers(fillers);
    filterCriteria.setSearchByFiller(searchByFiller);
    Pageable pageable =
            PageRequest.of(page - 1, pageSize, Sort.Direction.fromString(sortDirection), sortByField);

    return new ResponseEntity<>(
            flowDocumentService.findAllByFlowId(
                    headers.getFirst(HttpHeaders.AUTHORIZATION), flowId, pageable, filterCriteria),
            HttpStatus.OK);
  }

  @GetMapping(value = "/ids/{flowTraceabilityId}")
  public ResponseEntity<List<Long>> findFlowDocumentDetailsById(
          @PathVariable("flowTraceabilityId") long flowId,
          @RequestParam(value = "page", defaultValue = "1") int page,
          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
          @RequestParam(value = "sortByField", defaultValue = "dateStatus") String sortByField,
          @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
          @RequestParam(value = "filter", defaultValue = "") String filter,
          @RequestParam(value = "channels", required = false) List<String> channels,
          @RequestParam(value = "categories", required = false) List<String> categories,
          @RequestParam(value = "status", required = false) List<String> status,
          @RequestParam(value = "startDate", defaultValue = "") String startDate,
          @RequestParam(value = "endDate", defaultValue = "") String endDate,
          @RequestParam(value = "fillers", required = false) List<String> fillers,
          @RequestParam(value = "searchByFiller", defaultValue = "") String searchByFiller) {
    var filterCriteria = new BaseFilterCriteria();
    filterCriteria.setFilter(filter);
    filterCriteria.setChannels(channels);
    filterCriteria.setCategories(categories);
    filterCriteria.setStatus(status);
    filterCriteria.setStartDate(startDate);
    filterCriteria.setEndDate(endDate);
    filterCriteria.setFillers(fillers);
    filterCriteria.setSearchByFiller(searchByFiller);
    Pageable pageable =
            PageRequest.of(page - 1, pageSize, Sort.Direction.fromString(sortDirection), sortByField);
    return new ResponseEntity<>(
            flowDocumentService.getDocumentDetailIds(flowId, pageable, filterCriteria),
            HttpStatus.OK);
  }

  /**
   * Endpoint to retrieve sub-channel by {@link FlowDocumentChannel}.
   */
  @Operation(
          operationId = "getSubChannel",
          summary = "To get the sub-channel of the document base on channel")
  @GetMapping("/sub-channel")
  public ResponseEntity<Map<String, Object>> getSubChannelByChannel(
          @Parameter(
                  in = ParameterIn.QUERY,
                  schema = @Schema(type = "array", implementation = FlowDocumentChannel.class))
          @RequestParam(value = "channel", defaultValue = "")
          String channel) {
    return ResponseEntity.ok(flowDocumentService.getSubChannelByChannel(channel));
  }

  /**
   * To retrieve details of a flow document.
   *
   * @param id id of {@link FlowDocument}
   * @return object details of {@link FlowDocument}
   */
  @Operation(
          operationId = "getFlowDocumentDetailsById",
          summary = "To retrieve details of a flow document.",
          description = "Get details of a flow document by id.",
          responses = @ApiResponse(responseCode = "200", description = "Flow document's details."))
  @GetMapping(value = "/details/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LoadFlowDocumentDetailsDto> getFlowDocumentDetailsById(
          @Parameter(
                  in = ParameterIn.PATH,
                  description = "flow document id",
                  schema = @Schema(type = "integer", format = "int64"),
                  example = "1")
          @PathVariable
          long id) {
    return ResponseEntity.ok(flowDocumentService.getFlowDocumentDetailsById(id));
  }

  /**
   * To retrieve fillers configuration of the client.
   *
   * @see FlowDocumentService#getAllClientFillers()
   * @return object of {@link List} of {@link FlowDocumentFiller}
   */
  @GetMapping("/client-fillers")
  public ResponseEntity<List<FlowDocumentFiller>> getClientFillers() {
    return ResponseEntity.ok(this.flowDocumentService.getFlowDocumentFillers());
  }

  @GetMapping("/status-info/{id}")
  public ResponseEntity<SharedStatusInfoDto> getStatusInfo(@PathVariable("id") Long id,
                                                           @RequestParam(value = "locale", defaultValue = "fr", required = false) String locale) {
    return ResponseEntity.ok(this.flowDocumentService.getStatusInfo(id, locale));
  }

}

