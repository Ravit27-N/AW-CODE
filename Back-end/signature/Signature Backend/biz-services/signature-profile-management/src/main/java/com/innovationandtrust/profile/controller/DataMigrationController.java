package com.innovationandtrust.profile.controller;

import com.innovationandtrust.profile.model.projection.DataMigrationResult;
import com.innovationandtrust.profile.service.DataMigrationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Migration data such as company, corporate, and end-users. */
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/data-migrations")
public class DataMigrationController {

  private final DataMigrationService dataMigrationService;

  @GetMapping
  @PreAuthorize("hasRole('SUPER-ADMIN')")
  public ResponseEntity<Map<String, DataMigrationResult>> migrateData() {
    return new ResponseEntity<>(this.dataMigrationService.migrateOldData(), HttpStatus.OK);
  }
}
