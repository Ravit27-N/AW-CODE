package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.InterviewElasticsearchRequest;
import com.allweb.rms.entity.dto.InterviewRequest;
import com.allweb.rms.entity.dto.InterviewResponse;
import com.allweb.rms.exception.AdvancedSearchBadRequestException;
import com.allweb.rms.service.InterviewService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interview")
@Slf4j
public class InterviewController {

  private static final String[] SUPPORTED_SORT_DIRECTIONS =
      new String[] {"title", "status", "candidate", "dateTime", "createdAt"};
  private final InterviewService interviewService;
  private final SimpleDateFormat dateFormatter;

  @Autowired
  public InterviewController(
      InterviewService interviewService, @Value("${pattern.date.format}") String dateFormat) {
    this.interviewService = interviewService;
    dateFormatter = new SimpleDateFormat(dateFormat);
  }

  @Operation(
      operationId = "getInterview",
      description = "Get all interview",
      tags = {"Interview"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page Size"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "filter",
            description = "Filter in candidate's full name and description"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortDirection",
            description = "Direction Sort",
            content =
                @Content(
                    schema =
                        @Schema(
                            defaultValue = "asc",
                            allowableValues = {"asc", "desc"}))),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "Name field that to sort",
            content =
                @Content(
                    schema =
                        @Schema(
                            defaultValue = "title",
                            allowableValues = {
                              "title",
                              "status",
                              "candidate",
                              "dateTime",
                              "createdAt"
                            }))),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "status",
            description =
                "Status of interview allow value lowercase Ex.['missed','attended','in progress',....]"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "startDate",
            description = "Date start of interview",
            example = "dd-MM-yyyy"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "endDate",
            description = "Date end of interview",
            example = "dd-MM-yyyy"),
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<InterviewResponse>> getInterviews(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int size,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "sortDirection", required = false, defaultValue = "asc")
          String sortDirection,
      @RequestParam(value = "sortByField", required = false, defaultValue = "dateTime")
          String sortByField,
      @RequestParam(value = "status", required = false) String[] status,
      @RequestParam(value = "startDate", required = false) String startDate,
      @RequestParam(value = "endDate", required = false) String endDate,
      @RequestParam(value = "title", required = false) String title)
      throws ParseException {
    this.validateSearchRequest(sortByField, startDate, endDate);
    sortByField =
        SUPPORTED_SORT_DIRECTIONS[2].equalsIgnoreCase(sortByField)
            ? "candidate.full_name"
            : sortByField;
    sortByField =
        SUPPORTED_SORT_DIRECTIONS[3].equalsIgnoreCase(sortByField) ? "date_time" : sortByField;
    sortByField =
        SUPPORTED_SORT_DIRECTIONS[4].equalsIgnoreCase(sortByField) ? "created_at" : sortByField;
    InterviewElasticsearchRequest request =
        InterviewElasticsearchRequest.builder()
            .filter(filter)
            .status(status)
            .title(title)
            .startDate(
                StringUtils.isNotBlank(startDate) ? this.dateFormatter.parse(startDate) : null)
            .endDate(StringUtils.isNotBlank(endDate) ? this.dateFormatter.parse(endDate) : null)
            .pageable(
                PageRequest.of(
                    page - 1, size, Sort.by(Sort.Direction.fromString(sortDirection), sortByField)))
            .build();
    return ResponseEntity.ok(this.interviewService.findAllByElasticsearch(request));
  }

  private void validateSearchRequest(
      String sortByField, String startDateString, String endDateString) {
    if (!ArrayUtils.contains(SUPPORTED_SORT_DIRECTIONS, sortByField)) {
      throw new AdvancedSearchBadRequestException(
          String.format("Sort by \"%s\" is not supported.", sortByField));
    }
    Date startDate = null;
    Date endDate = null;
    try {
      if (StringUtils.isNotBlank(startDateString)) {
        startDate = dateFormatter.parse(startDateString);
      }
      if (StringUtils.isNotBlank(endDateString)) {
        endDate = dateFormatter.parse(endDateString);
      }
    } catch (ParseException parseException) {
      throw new AdvancedSearchBadRequestException("Incorrect date format.");
    }
    if (startDate != null && endDate != null && startDate.after(endDate)) {
      throw new AdvancedSearchBadRequestException("Start date must be  the end date.");
    }
  }

  @Operation(
      operationId = "getInterviewId",
      description = "Get interview by ID",
      tags = {"Interview"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Interview id")})
  @GetMapping("/{id}")
  public ResponseEntity<InterviewResponse> getInterviewById(@PathVariable("id") int id) {
    return new ResponseEntity<>(interviewService.getInterviewById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "createInterview",
      description = "Create interview ",
      tags = {"Interview"})
  @PostMapping
  public ResponseEntity<InterviewResponse> createInterview(
      @Valid @RequestBody InterviewRequest interviewRequest) {
    return new ResponseEntity<>(
        interviewService.saveInterview(interviewRequest), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateInterview",
      description = "Update interview by id",
      tags = {"Interview"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Interview id")})
  @PatchMapping("/{id}")
  public ResponseEntity<InterviewResponse> updateInterview(
      @Valid @RequestBody InterviewRequest interview, @PathVariable("id") int id) {
    return new ResponseEntity<>(
        interviewService.updateInterview(interview, id), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "deleteSoftInterview",
      description = "Delete soft interview by id",
      tags = {"Interview"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Interview id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "isDeleted",
            description = "Boolean type allow true or false")
      })
  @PatchMapping("/{id}/delete/{isDeleted}")
  public ResponseEntity<HttpStatus> deleteSoftInterview(
      @PathVariable("id") int id, @PathVariable("isDeleted") boolean isDeleted) {
    interviewService.deleteSoftInterview(id, isDeleted);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateInterviewStatus",
      description = "Update interview status by id",
      tags = {"Interview"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Interview id"),
        @Parameter(in = ParameterIn.PATH, name = "statusId", description = "Interview status id")
      })
  @PatchMapping("/{id}/status/{statusId}")
   public ResponseEntity<InterviewResponse> updateInterviewStatus(
      @PathVariable("id") int id, @PathVariable("statusId") int statusId) {
    return new ResponseEntity<>(
        interviewService.updateStatusInterview(statusId, id), HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteInterview",
      description = "Delete interview by id",
      tags = {"Interview"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Interview id")})
  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteInterview(@PathVariable("id") int id) {
    interviewService.deleteInterview(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
