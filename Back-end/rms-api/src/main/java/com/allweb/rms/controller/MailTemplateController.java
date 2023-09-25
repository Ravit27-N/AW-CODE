package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.MailTemplateDTO;
import com.allweb.rms.service.MailTemplateService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/mail/template")
public class MailTemplateController {
  private final MailTemplateService mailTemplateService;

  @Autowired
  public MailTemplateController(MailTemplateService mailTemplateService) {
    this.mailTemplateService = mailTemplateService;
  }

  @Operation(
      operationId = "getMailTemplateById",
      description = "Get MailTemplate By Id",
      tags = "Mail Template",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailTemplate Id")
      })
  @GetMapping("/{id}")
  public ResponseEntity<MailTemplateDTO> getMailTemplateById(@PathVariable("id") int id) {
    return new ResponseEntity<>(mailTemplateService.getMailTemplateById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "getMailTemplates",
      description = "Get mailTemplates",
      tags = "Mail Template",
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
  public ResponseEntity<EntityResponseHandler<EntityModel<MailTemplateDTO>>> getMailTemplates(
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
        mailTemplateService.getMailTemplates(
            page, size, filter, sortDirection, sortByField, selectType.toLowerCase()),
        HttpStatus.OK);
  }

  @Operation(
      operationId = "createMailTemplate",
      description = "Create new MailTemplate",
      tags = "Mail Template")
  @PostMapping
  public ResponseEntity<MailTemplateDTO> createMailTemplate(
      @Valid @RequestBody MailTemplateDTO mailTemplate) {
    return new ResponseEntity<>(
        mailTemplateService.saveMailTemplate(mailTemplate), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateMailTemplate",
      description = "Update existed mailTemplate",
      tags = "Mail Template")
  @PutMapping
  public ResponseEntity<MailTemplateDTO> updateMailTemplate(
      @Valid @RequestBody MailTemplateDTO mailTemplate) {
    return new ResponseEntity<>(
        mailTemplateService.updateMailTemplate(mailTemplate), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateActive",
      description = "Update existed active mailTemplate",
      tags = "Mail Template",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailTemplate Id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "active",
            description = "Boolean type of field active mailTemplate allow value true or false")
      })
  @PatchMapping("/{id}/active/{active}")
  public ResponseEntity<HttpStatus> updateActive(
      @PathVariable("id") int id, @PathVariable("active") boolean active) {
    mailTemplateService.updateActiveMailTemplate(id, active);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateDeleted",
      description = "Update existed deleted mailTemplate",
      tags = "Mail Template",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailTemplate Id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "deleted",
            description = "Boolean type of field deleted mailTemplate allow value true or false")
      })
  @PatchMapping("/{id}/deleted/{deleted}")
  public ResponseEntity<HttpStatus> updateDeleted(
      @PathVariable("id") int id, @PathVariable("deleted") boolean deleted) {
    mailTemplateService.updateDeleted(id, deleted);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateIsAbleDelete",
      description = "Update existed IsAbleDelete mailTemplate",
      tags = "Mail Template",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailTemplate Id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "isDeleteable",
            description =
                "Boolean type of field IsAbleDelete mailTemplate allow value true or false")
      })
  @PatchMapping("/{id}/isAbleDelete/{isDeleteable}")
  public ResponseEntity<HttpStatus> updateIsAbleDelete(
      @PathVariable("id") int id, @PathVariable("isDeleteable") boolean isAbleDelete) {
    mailTemplateService.updateIsAbleDelete(id, isAbleDelete);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteMailTemplate",
      description = "Delete MailTemplate by id",
      tags = "Mail Template",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "MailTemplate id")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteMailTemplate(@PathVariable("id") int id) {
    mailTemplateService.deleteMailTemplate(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
