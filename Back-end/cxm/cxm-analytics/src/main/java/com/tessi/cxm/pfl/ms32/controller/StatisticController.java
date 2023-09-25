package com.tessi.cxm.pfl.ms32.controller;

import com.tessi.cxm.pfl.ms32.constant.CSVExportingConstant;
import com.tessi.cxm.pfl.ms32.constant.DepositModeResponseDto;
import com.tessi.cxm.pfl.ms32.constant.DepositModeVolumeResponseDto;
import com.tessi.cxm.pfl.ms32.constant.ExportType;
import com.tessi.cxm.pfl.ms32.dto.DocumentDetailSummary;
import com.tessi.cxm.pfl.ms32.dto.DocumentSummary;
import com.tessi.cxm.pfl.ms32.dto.FlowProductionDetailsDto;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticExportingRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.service.DigitalStatisticService;
import com.tessi.cxm.pfl.ms32.service.GlobalStatisticService;
import com.tessi.cxm.pfl.ms32.service.StatisticExportingService;
import com.tessi.cxm.pfl.ms32.service.StatisticService;
import com.tessi.cxm.pfl.ms32.service.specification.PostalStatisticService;
import com.tessi.cxm.pfl.ms32.util.DeleteOnCloseFileInputStream;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/statistic")
@RequiredArgsConstructor
public class StatisticController {
  private final GlobalStatisticService globalStatisticService;
  private final StatisticService statisticService;
  private final PostalStatisticService postalStatisticService;
  private final DigitalStatisticService digitalStatisticService;
  private final StatisticExportingService statisticExportingService;

  // region Global

  @Tag(name = "Global statistic APIs")
  @Operation(
      operationId = "global-production-details",
      description = "This endpoint used to get statistic of production details for Global.")
  @Parameters(
      value = {
        @Parameter(
            name = "channels",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Postal", "Digital"})),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {
                      "Ecopli", "Lettre", "Reco AR", "Reco", "CSE", "CSE AR", "LRE", "Email", "SMS"
                    })),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-ss",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-ss hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(name = "fillers", in = ParameterIn.QUERY, schema = @Schema(type = "array")),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(name = "filter", hidden = true)
      })
  @GetMapping("/global/production-details")
  public ResponseEntity<FlowProductionDetailsDto> getGlobalProductionDetails(
      @Valid GlobalStatisticRequestFilter filter) {

    return ResponseEntity.ok(globalStatisticService.getGlobalProductionDetails(filter));
  }

  @Tag(name = "Global statistic APIs")
  @Operation(
      operationId = "global-volume-received",
      description = "This endpoint used to get statistic of volume received for Global.")
  @Parameters(
      value = {
        @Parameter(
            name = "channels",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Postal", "Digital"})),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {
                      "Ecopli", "Lettre", "Reco AR", "Reco", "CSE", "CSE AR", "LRE", "Email", "SMS"
                    })),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-ss",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-ss hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(name = "fillers", in = ParameterIn.QUERY, schema = @Schema(type = "array")),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(name = "filter", hidden = true)
      })
  @GetMapping("/global/volume-received")
  public ResponseEntity<List<DepositModeVolumeResponseDto>> getVolumeReceived(
      @Valid GlobalStatisticRequestFilter filter) {

    return ResponseEntity.ok(this.globalStatisticService.getVolumeReceived(filter));
  }

  @Tag(name = "Global statistic APIs")
  @Operation(
      operationId = "global-production-progress",
      description = "This endpoint used to get statistic of production progress for Global.")
  @Parameters(
      value = {
        @Parameter(
            name = "channels",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Postal", "Digital"})),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {
                      "Ecopli", "Lettre", "Reco AR", "Reco", "CSE", "CSE AR", "LRE", "Email", "SMS"
                    })),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-ss",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-ss hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(name = "fillers", in = ParameterIn.QUERY, schema = @Schema(type = "array")),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(name = "filter", hidden = true)
      })
  @GetMapping("/global/production-progress")
  public ResponseEntity<List<DepositModeVolumeResponseDto>> getProductionProgress(
      @Valid GlobalStatisticRequestFilter filter) {

    return ResponseEntity.ok(this.globalStatisticService.getProductionProgress(filter));
  }

  // endregion

  // region Shared APIs

  @Tag(
      name = "Shared statistic APIs",
      description = "APIs for use both Global and channel specified")
  @Operation(
      operationId = "production-details",
      description =
          "This endpoint used to get statistic of production details for both Global and channel specified.")
  @Parameters(
      value = {
        @Parameter(
            name = "channels",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Postal", "Digital"})),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            description = "For Postal, categories can be empty.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Ecopli", "Lettre", "Reco AR", "Reco", "Email", "SMS"})),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-ss",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-ss hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        // 1st filler
        @Parameter(
            name = "fillers",
            in = ParameterIn.QUERY,
            description = "For Postal, Email and SMS, fillers should be empty or single element.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        // 2nd filler
        @Parameter(
            name = "secondFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "secondFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        // 3rd filler
        @Parameter(
            name = "thirdFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "thirdFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(
            name = "includeMetadata",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "boolean", defaultValue = "true")),
        @Parameter(name = "filter", hidden = true)
      })
  @GetMapping("/production-details")
  public ResponseEntity<DocumentDetailSummary> getProductionDetails(
      @Valid StatisticRequestFilter filter) {
    return ResponseEntity.ok(this.statisticService.calculateProductionDetails(filter));
  }

  @Tag(
      name = "Shared statistic APIs",
      description = "APIs for use both Global and channel specified")
  @Operation(
      operationId = "distribution-volume-received",
      description =
          "This endpoint used to get statistic of volume received for Global and channel specified.")
  @Parameters(
      value = {
        @Parameter(
            name = "channels",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Postal", "Digital"})),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            description = "For Postal, categories can be empty.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Ecopli", "Lettre", "Reco AR", "Reco", "Email", "SMS"})),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-dd",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-dd",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-dd hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        // 1st filler
        @Parameter(
            name = "fillers",
            in = ParameterIn.QUERY,
            description = "For Postal, Email and SMS, fillers should be empty or single element.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        // 2nd filler
        @Parameter(
            name = "secondFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "secondFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        // 3rd filler
        @Parameter(
            name = "thirdFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "thirdFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(name = "filter", hidden = true)
      })
  @GetMapping("/distribution-volume-received")
  public ResponseEntity<List<DepositModeResponseDto>> getDistributionVolumeReceived(
      @Valid StatisticRequestFilter filter) {
    return ResponseEntity.ok(this.statisticService.calculateDistributionVolumeReceive(filter));
  }

  @Tag(
      name = "Shared statistic APIs",
      description = "APIs for use both Global and channel specified")
  @Operation(
      operationId = "postal-production-delivered",
      description = "This endpoint used to get statistic of Postal production delivered.")
  @Parameters(
      value = {
        @Parameter(
            name = "channels",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Postal", "Digital"})),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            description = "For Postal, categories can be empty.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Ecopli", "Lettre", "Reco AR", "Reco", "Email", "SMS"})),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-dd",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-dd",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-dd hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        // 1st filler
        @Parameter(
            name = "fillers",
            in = ParameterIn.QUERY,
            description = "For Postal, Email and SMS, fillers should be empty or single element.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        // 2nd filler
        @Parameter(
            name = "secondFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "secondFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        // 3rd filler
        @Parameter(
            name = "thirdFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "thirdFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(name = "filter", hidden = true)
      })
  @GetMapping("/production-delivered")
  public ResponseEntity<DocumentSummary> getProductionDeliveredSummary(
      @Valid StatisticRequestFilter filter) {
    return ResponseEntity.ok(
        this.postalStatisticService.calculateProductionDeliveredSummary(filter));
  }

  // endregion

  // region Postal

  @Tag(name = "Postal statistic APIs")
  @Operation(
      operationId = "postal-none-distribution-by-status",
      description = "This endpoint used to get statistic of Postal non distribution by status.")
  @Parameters(
      value = {
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            description = "For Postal, categories can be empty.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Ecopli", "Lettre", "Reco AR", "Reco"})),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-dd",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-dd",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-dd hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        // 1st filler
        @Parameter(
            name = "fillers",
            in = ParameterIn.QUERY,
            description = "For Postal, Email and SMS, fillers should be empty or single element.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        // 2nd filler
        @Parameter(
            name = "secondFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "secondFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        // 3rd filler
        @Parameter(
            name = "thirdFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "thirdFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(name = "filter", hidden = true),
        @Parameter(name = "channels", hidden = true)
      })
  @GetMapping("/postal/none-distribution-by-status")
  public ResponseEntity<List<DepositModeResponseDto>> getNonDistributionVolumeReceived(
      @Valid StatisticRequestFilter filter) {
    return ResponseEntity.ok(
        this.postalStatisticService.calculateNonDistributedDocumentDetailsSummary(filter));
  }

  // endregion

  // region Digital

  @Tag(name = "Digital statistic APIs")
  @Operation(
      operationId = "digital-distribution-by-status",
      description =
          "This endpoint used to get statistic of digital distribution by status for both Email and SMS.")
  @Parameters(
      value = {
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            description = "For Digital, categories can be empty.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Email", "SMS"})),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-dd",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-dd",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-dd hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        // 1st filler
        @Parameter(
            name = "fillers",
            in = ParameterIn.QUERY,
            description = "For Postal, Email and SMS, fillers should be empty or single element.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        // 2nd filler
        @Parameter(
            name = "secondFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "secondFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        // 3rd filler
        @Parameter(
            name = "thirdFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "thirdFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(name = "filter", hidden = true),
        @Parameter(name = "channels", hidden = true)
      })
  @GetMapping("/digital/distribution-by-status")
  public ResponseEntity<List<DepositModeResponseDto>> getDistributionByStatus(
      @Valid StatisticRequestFilter filter) {
    return ResponseEntity.ok(this.digitalStatisticService.getDistributionBySubStatus(filter));
  }

  // endregion

  // region Reporting Export

  @Tag(
      name = "Statistic exporting APIs",
      description = "APIs for use both Global and channel specified")
  @Operation(
      operationId = "production-details",
      description =
          "This endpoint used to get statistic of production details for both Global and channel specified.")
  @Parameters(
      value = {
        @Parameter(
            name = "exportingType",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"global", "specific"})),
        @Parameter(
            name = "channels",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Postal", "Digital"})),
        @Parameter(
            name = "categories",
            in = ParameterIn.QUERY,
            description = "For Postal, categories can be empty.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Ecopli", "Lettre", "Reco AR", "Reco", "Email", "SMS"})),
        @Parameter(
            name = "startDate",
            description = "The start date of the dateStatus. format yyyy-mm-ss",
            example = "2023-04-04",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "endDate",
            example = "2023-04-05",
            description = "The end date of the dateStatus. format yyyy-mm-ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "requestedAt",
            example = "2023-04-05 07:00:00",
            description = "The end date of the dateStatus. format yyyy-mm-ss hh:mm:ss",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string")),
        // 1st filler
        @Parameter(
            name = "fillers",
            in = ParameterIn.QUERY,
            description = "For Postal, Email and SMS, fillers should be empty or single element.",
            schema =
                @Schema(
                    type = "array",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(
            name = "searchByFiller",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        // 2nd filler
        @Parameter(
            name = "secondFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "secondFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        // 3rd filler
        @Parameter(
            name = "thirdFillerText",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string")),
        @Parameter(
            name = "thirdFillerKey",
            in = ParameterIn.QUERY,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"Filler1", "Filler2", "Filler3", "Filler4", "Filler5"})),
        @Parameter(name = "filter", hidden = true)
      })
  @GetMapping("/export")
  public ResponseEntity<Resource> generateAndExport(@Valid StatisticExportingRequestFilter filter)
      throws FileNotFoundException {
    Path exportedFile = this.statisticExportingService.generateAndExport(filter);
    File file = exportedFile.toFile();
    InputStreamResource source = new InputStreamResource(new DeleteOnCloseFileInputStream(file));

    HttpHeaders headers = new HttpHeaders();
    headers.setAccessControlExposeHeaders(Collections.singletonList("Content-Disposition"));
    headers.set("Content-Disposition", "attachment; filename=" + file.getName());
    headers.setContentType(MediaType.parseMediaType(CSVExportingConstant.CONTENT_TYPE));
    return new ResponseEntity<>(source, headers, HttpStatus.OK);
  }

  // endregion
}
