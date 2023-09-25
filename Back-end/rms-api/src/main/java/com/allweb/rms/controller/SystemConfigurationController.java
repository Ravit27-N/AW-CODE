package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.CandidateDTO;
import com.allweb.rms.entity.dto.InterviewRequest;
import com.allweb.rms.entity.dto.SystemConfigDTO;
import com.allweb.rms.service.SystemConfigurationService;
import com.allweb.rms.service.mail.MailService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v1/systemConfig")
public class SystemConfigurationController {
  private final SystemConfigurationService systemConfigurationService;
  private final MailService mailService;

  @Autowired
  public SystemConfigurationController(
      SystemConfigurationService systemConfigurationService, MailService mailService) {
    this.systemConfigurationService = systemConfigurationService;
    this.mailService = mailService;
  }

  @Operation(
      operationId = "getSystemConfigById",
      description = "get a System Configuration by id",
      tags = {"System Configuration"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "System Configuration id")
      })
  @GetMapping("/{id}")
  public ResponseEntity<SystemConfigDTO> getSystemConfigById(@PathVariable("id") int id) {
    return new ResponseEntity<>(systemConfigurationService.getSystemConfigById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "getSystemConfig",
      description = "Get mailProperties",
      tags = "System Configuration",
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page Size"),
        @Parameter(in = ParameterIn.QUERY, name = "sortDirection", description = "Direction Sort"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "Name field that to sort")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<SystemConfigDTO>> getSystemConfig(
      @RequestParam(value = "page", required = false, defaultValue = "0") int page,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int size,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "sortDirection", required = false, defaultValue = "asc")
          String sortDirection,
      @RequestParam(value = "sortByField", required = false, defaultValue = "createdAt")
          String sortByField) {
    return new ResponseEntity<>(
        systemConfigurationService.getSystemMailConfigs(
            page, size, sortDirection, sortByField, filter),
        HttpStatus.OK);
  }

  @Operation(
      operationId = "updateSystemConfigs",
      description = "update System Configuration",
      tags = {"System Configuration"})
  @PutMapping
  public ResponseEntity<SystemConfigDTO> updateSystemConfigs(
      @Valid @RequestBody SystemConfigDTO systemMailConfig) {
    return new ResponseEntity<>(
        systemConfigurationService.save(systemMailConfig), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "delete",
      description = "delete mail",
      tags = {"System Configuration"},
      parameters =
          @Parameter(in = ParameterIn.PATH, name = "id", description = "System Configuration id"))
  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
    systemConfigurationService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "setStatusChange",
      description = "Set StatusChange",
      tags = {"Set StatusChange"})
  @PostMapping("/setStatusChange")
  public ResponseEntity<HttpStatus> setStatusChange(@Valid @RequestBody CandidateDTO candidateDTO) {
    mailService.setMailStatusChange(candidateDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "setMailInterview",
      description = "Set MailInterview",
      tags = {"Set MailInterview"})
  @PostMapping("/setMailInterview")
  public ResponseEntity<HttpStatus> setMailInterview(
      @Valid @RequestBody InterviewRequest interviewRequest) {
    mailService.setMailInterview(interviewRequest);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "sendByGmail",
      description = "Send Mail By Gmail",
      tags = {"Send Mail"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "from", description = "Email from"),
        @Parameter(in = ParameterIn.QUERY, name = "to", description = "Email to"),
        @Parameter(in = ParameterIn.QUERY, name = "cc", description = "Email to cc"),
        @Parameter(in = ParameterIn.QUERY, name = "subject", description = "Subject"),
        @Parameter(in = ParameterIn.QUERY, name = "body", description = "Content or Body")
      })
  @PostMapping("/sendMail")
  public ResponseEntity<HttpStatus> sendEmail(
      @RequestParam(value = "from", required = false) String from,
      @RequestParam(value = "to", required = false) Set<String> to,
      @RequestParam(value = "cc", required = false) Set<String> cc,
      @RequestParam(value = "subject", required = false) String subject,
      @RequestParam(value = "body", required = false) String body) {
    mailService.sender(from, to, cc == null ? new HashSet<>() : cc, subject, body);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateActive",
      description = "update Active",
      tags = {"System Configuration"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "System config id"),
        @Parameter(in = ParameterIn.PATH, name = "active", description = "System config active")
      })
  @PatchMapping("/{id}/active/{active}")
  public ResponseEntity<SystemConfigDTO> updateActive(
      @PathVariable("id") int id, @PathVariable("active") boolean active) {
    return new ResponseEntity<>(systemConfigurationService.updateActive(id, active), HttpStatus.OK);
  }

  @Operation(
      operationId = "getByConfigKey",
      description = "get System configuration By configKey",
      tags = {"System Configuration"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "configKey", description = "System config key"),
      })
  @PatchMapping("/configKey/{configKey}")
  public ResponseEntity<SystemConfigDTO> getByConfigKey(
      @PathVariable("configKey") String configKey) {
    return new ResponseEntity<>(
        systemConfigurationService.getByConfigKey(configKey), HttpStatus.OK);
  }
}
