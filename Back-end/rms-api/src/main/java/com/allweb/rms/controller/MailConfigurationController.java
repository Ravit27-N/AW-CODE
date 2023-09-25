package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.MailConfigurationDTO;
import com.allweb.rms.service.MailConfigurationService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import javax.mail.internet.AddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail/configuration")
public class MailConfigurationController {

  private final MailConfigurationService mailConfigurationService;

  @Autowired
  public MailConfigurationController(MailConfigurationService mailConfigurationService) {
    this.mailConfigurationService = mailConfigurationService;
  }

  @Operation(
      operationId = "getMailConfigurationById",
      description = "Get mailConfiguration by ID",
      tags = "Mail Configuration",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailConfiguration id")
      })
  @GetMapping("/{id}")
  public ResponseEntity<MailConfigurationDTO> getMailConfigurationById(@PathVariable("id") int id) {
    return new ResponseEntity<>(
        mailConfigurationService.getMailConfigurationById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "getMailConfigurations",
      description = "Get mailConfigurations",
      tags = "Mail Configuration",
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
  public ResponseEntity<EntityResponseHandler<EntityModel<MailConfigurationDTO>>>
      getMailConfigurations(
          @RequestParam(value = "page", required = false, defaultValue = "1") int page,
          @RequestParam(value = "pageSize", required = false, defaultValue = "10") int size,
          @RequestParam(value = "filter", required = false) String filter,
          @RequestParam(value = "sortDirection", required = false, defaultValue = "asc")
              String sortDirection,
          @RequestParam(value = "sortByField", required = false, defaultValue = "createdAt")
              String sortByField,
          @RequestParam(value = "selectType", required = false, defaultValue = "All")
              String selectType) {
    return new ResponseEntity<>(
        mailConfigurationService.getMailConfigurations(
            page, size, filter, sortDirection, sortByField, selectType),
        HttpStatus.OK);
  }

  @Operation(
      operationId = "createMailConfiguration",
      description = "Create new MailConfiguration",
      tags = "Mail Configuration")
  @PostMapping
  public ResponseEntity<MailConfigurationDTO> createMailConfiguration(
      @Valid @RequestBody MailConfigurationDTO mailConfiguration) throws AddressException {
    return new ResponseEntity<>(
        mailConfigurationService.save(mailConfiguration), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateMailConfiguration",
      description = "Update MailConfiguration",
      tags = "Mail Configuration")
  @PutMapping
  public ResponseEntity<MailConfigurationDTO> updateMailConfiguration(
      @Valid @RequestBody MailConfigurationDTO mailConfiguration) throws AddressException {
    return new ResponseEntity<>(
        mailConfigurationService.update(mailConfiguration), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateActive",
      description = "Update active of mailConfiguration",
      tags = "Mail Configuration",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailConfiguration id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "active",
            description = "Boolean type of field active allow value true or false")
      })
  @PatchMapping("/{id}/active/{active}")
  public ResponseEntity<HttpStatus> updateActive(
      @PathVariable("id") int id, @PathVariable("active") boolean active) {
    mailConfigurationService.updateActiveMailConfiguration(id, active);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateDeleted",
      description = "Update Deleted of mailConfiguration",
      tags = "Mail Configuration",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailConfiguration Id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "deleted",
            description = "Boolean type of field deleted allow value true or false")
      })
  @PatchMapping("/{id}/deleted/{deleted}")
  public ResponseEntity<HttpStatus> updateDeleted(
      @PathVariable("id") int id, @PathVariable("deleted") boolean deleted) {
    mailConfigurationService.updateDeleted(id, deleted);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteMailConfiguration",
      description = "Delete mailConfiguration by id",
      tags = "Mail Configuration",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailConfiguration id")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteMailConfiguration(@PathVariable("id") int id) {
    mailConfigurationService.deleteMailConfiguration(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
