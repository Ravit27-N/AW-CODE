package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.ReminderAdvanceFilterRequest;
import com.allweb.rms.entity.dto.ReminderRequest;
import com.allweb.rms.entity.dto.ReminderResponse;
import com.allweb.rms.entity.dto.ReminderResponseList;
import com.allweb.rms.exception.AdvancedSearchBadRequestException;
import com.allweb.rms.service.ReminderService;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.rms.utils.ReminderType;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/reminder")
@Tag(name = "Reminder", description = "Manage reminder.")
public class ReminderController {
  private static final String[] SORTABLE_FIELDS =
      new String[] {"title", "candidate", "dateReminder", "createdAt"};
  private static final String DATE_FORMAT = "dd-MM-yyyy";
  private final ReminderService reminderService;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

  @Autowired
  public ReminderController(ReminderService reminderService) {
    this.reminderService = reminderService;
  }

  @Operation(
      operationId = "getAllReminders",
      description = "Get all Reminders",
      tags = "Reminder",
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page size"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "reminderTypes",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema = @Schema(type = "array", implementation = ReminderType.class))),
            description = "reminderTypes"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "sortByField",
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"title", "candidate", "dateReminder", "createdAt"})),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortDirection",
            description = "sortDirection",
            schema = @Schema(allowableValues = {"desc", "asc"})),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "startDate",
            description = "startDate",
            example = "dd-MM-yyyy"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "endDate",
            description = "endDate",
            example = "dd-MM-yyyy"),
        @Parameter(in = ParameterIn.QUERY, name = "active", description = "active")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = ReminderResponseList.class)))
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<ReminderResponseList>> getReminders(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
      @RequestParam(value = "reminderTypes", required = false) String[] reminderTypes,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "sortByField", required = false, defaultValue = "createdAt")
          String sortByField,
      @RequestParam(value = "sortDirection", required = false, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = "startDate", required = false) String startDate,
      @RequestParam(value = "endDate", required = false) String endDate,
      @RequestParam(value = "active", required = false) Boolean active)
       {
    validateSearchRequest(sortByField, reminderTypes, startDate, endDate);
    sortByField = SORTABLE_FIELDS[1].equals(sortByField) ? "candidate.full_name" : sortByField;
    sortByField = SORTABLE_FIELDS[2].equals(sortByField) ? "date_reminder" : sortByField;
    sortByField = SORTABLE_FIELDS[3].equals(sortByField) ? "created_at" : sortByField;
    ReminderAdvanceFilterRequest request = new ReminderAdvanceFilterRequest();
    request.setReminderTypes(reminderTypes);
    request.setFilter(filter);
    request.setDeleted(false);
    if (active != null) {
      request.setActive(active);
    }
    PageRequest pageable =
        PageRequest.of(
            page - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    try {
      if (StringUtils.isNotBlank(startDate)) {
        request.setFrom(dateFormat.parse(startDate));
      }
      if (StringUtils.isNotBlank(endDate)) {
        request.setTo(dateFormat.parse(endDate));
      }
    } catch (ParseException parseException) {
      log.debug(parseException.getMessage(), parseException);
    }
    EntityResponseHandler<ReminderResponseList> response =
        reminderService.getReminders(request, pageable);

    return ResponseEntity.ok(response);
  }

  private void validateSearchRequest(
      String sortByField, String[] reminderTypes, String startDate, String endDate) {
    if (StringUtils.isNotBlank(sortByField)) {
      if (!ArrayUtils.contains(SORTABLE_FIELDS, sortByField)) {
        throw new AdvancedSearchBadRequestException(
            String.format("Sort by \"%s\" is not supported.", sortByField));
      }
      if (ArrayUtils.isNotEmpty(reminderTypes)
          && !ArrayUtils.contains(reminderTypes, ReminderType.SPECIAL.getValue())
          && SORTABLE_FIELDS[1].equals(sortByField)) {
        throw new AdvancedSearchBadRequestException(
            String.format(
                "Without \"SPECIAL\" reminder type, sort by \"%s\" is not supported.",
                sortByField));
      }
    }
    try {
      Date dateFrom = null;
      Date dateTo = null;
      if (StringUtils.isNotBlank(startDate)) {
        dateFrom = dateFormat.parse(startDate);
      }
      if (StringUtils.isNotBlank(endDate)) {
        dateTo = dateFormat.parse(endDate);
      }
      if (dateFrom != null && dateTo != null && dateFrom.after(dateTo)) {
        throw new AdvancedSearchBadRequestException("Start date must be  the end date.");
      }
    } catch (ParseException e) {
      throw new AdvancedSearchBadRequestException("Incorrect date format.");
    }
  }

  @Operation(
      operationId = "getReminderById",
      description = "Get reminder by id",
      tags = "Reminder",
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Reminder id")},
      responses =
          @ApiResponse(
              responseCode = "200",
              content = @Content(schema = @Schema(implementation = ReminderResponse.class))))
  @GetMapping("/{id}")
  public ResponseEntity<ReminderResponse> getReminderById(@PathVariable("id") int id) {
    return ResponseEntity.ok(reminderService.getReminderById(id));
  }

  @Operation(
      operationId = "addReminder",
      description =
          "Create the reminder related to a candidate or the interviewer to the user whose owned it",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "Reminder detail",
              content = @Content(schema = @Schema(implementation = ReminderRequest.class))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = ReminderResponse.class)))
      })
  @PostMapping
  public ResponseEntity<ReminderResponse> addReminder(
      @Valid @NotNull @NotEmpty @RequestBody ReminderRequest reminderRequest) {
    return ResponseEntity.ok(reminderService.saveReminder(reminderRequest));
  }

  @Operation(
      operationId = "updateReminderById",
      description = "Update reminder by id",
      tags = "Reminder",
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Reminder id")},
      responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = ReminderResponse.class)))
      })
  @PatchMapping("/{id}")
  public ResponseEntity<ReminderResponse> updateReminder(
      @PathVariable("id") int id, @Valid @NotNull @RequestBody ReminderRequest reminderRequest) {
    if (reminderRequest.getReminderType() != null) {
      if (reminderRequest.getReminderType().equals(ReminderType.SPECIAL.getValue())
          && reminderRequest.getCandidateId() <= 0) {
        throw new AdvancedSearchBadRequestException("Invalid candidate id.");
      }
      if (reminderRequest.getReminderType().equals(ReminderType.INTERVIEW.getValue())
          && reminderRequest.getInterviewId() <= 0) {
        throw new AdvancedSearchBadRequestException("Invalid interview id.");
      }
    }
    return ResponseEntity.ok(reminderService.updateReminder(id, reminderRequest));
  }

  @Operation(
      operationId = "changeReminderStatus",
      description = "Change reminder status",
      tags = "Reminder",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Reminder id"),
        @Parameter(in = ParameterIn.PATH, name = "isActive", description = "Reminder active state")
      })
  @PatchMapping("/{id}/status/{isActive}")
  public ResponseEntity<String> changeStatus(
      @PathVariable("id") int id, @PathVariable("isActive") boolean isActive) {
    reminderService.updateReminderStatus(id, isActive);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(
      operationId = "deleteReminderById",
      description = "Delete reminder by id",
      tags = "Reminder",
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Reminder id")})
  @DeleteMapping("/{id}")
  public ResponseEntity<String> softDelete(@PathVariable("id") int id) {
    reminderService.softDeleteReminderById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PutMapping
  public ResponseEntity<?> recoverDelectedReminder() {
    reminderService.recoverDeleteReminder();
    return ResponseEntity.ok().build();
  }

  @Operation(
      operationId = "deleteReminderById",
      description = "Delete reminder by id",
      tags = "Reminder",
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Reminder id")})
  @PostMapping("/{id}/hardDelete")
  public ResponseEntity<String> hardDelete(@PathVariable("id") int id) {
    reminderService.hardDeleteReminderById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
