package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.ActivityRequest;
import com.allweb.rms.entity.dto.ActivityResponse;
import com.allweb.rms.entity.jpa.Activity;
import com.allweb.rms.service.ActivityService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activity")
public class ActivityController {
  private final ActivityService activityService;

  @Autowired
  public ActivityController(ActivityService activityService) {
    this.activityService = activityService;
  }

  @Operation(
      operationId = "getActivityById",
      description = "Get Activity by ID",
      tags = {"Activity"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Activity id")})
  @GetMapping("/{id}")
  public ResponseEntity<ActivityResponse> getActivityById(@PathVariable("id") int id) {
    return new ResponseEntity<>(activityService.getActivityById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "getActivities",
      description = "Get Activities",
      tags = {"Activity"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page Size"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter"),
        @Parameter(in = ParameterIn.QUERY, name = "sortDirection", description = "Direction Sort"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "Name field that to sort")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<ActivityResponse>> getActivities(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "pageSize", defaultValue = "10") int size,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "sortDirection", required = false, defaultValue = "asc")
          String sortDirection,
      @RequestParam(value = "sortByField", required = false, defaultValue = "createdAt")
          String sortByField) {
    return new ResponseEntity<>(
        activityService.getActivities(page, size, filter, sortDirection, sortByField),
        HttpStatus.OK);
  }

  @Operation(
      operationId = "createActivity",
      description = "Create Activity ",
      tags = {"Activity"})
  @PostMapping
  public ResponseEntity<ActivityResponse> createActivity(
      @Valid @RequestBody ActivityRequest activityRequest) {
    return new ResponseEntity<>(activityService.save(activityRequest), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateActivity",
      description = "Update Activity ",
      tags = {"Activity"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Activity Id")})
  @PatchMapping("/{id}")
  public ResponseEntity<ActivityResponse> updateActivity(
      @Valid @RequestBody ActivityRequest activityRequest, @PathVariable("id") int id) {
    return new ResponseEntity<>(activityService.update(activityRequest, id), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "deleteActivity",
      description = "Delete Activity",
      tags = {"Activity"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Activity Id")})
  @DeleteMapping("/{id}")
  public ResponseEntity<ActivityResponse> deleteActivity(@PathVariable("id") int id) {
    activityService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * @return pagination by default @Test
   */
  @Operation(
      operationId = "getActivityPageTest",
      description = "This URL create to test pagination it will return default content result",
      tags = {"Activity"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "size", description = "Page Size"),
      })
  @GetMapping("/test")
  public ResponseEntity<Page<Activity>> getActivityPageTest(
      @RequestParam("page") int page,
      @RequestParam("size") int size,
      @RequestParam(value = "filter", required = false) String filter) {
    return new ResponseEntity<>(activityService.getPage(page, size), HttpStatus.OK);
  }
}
