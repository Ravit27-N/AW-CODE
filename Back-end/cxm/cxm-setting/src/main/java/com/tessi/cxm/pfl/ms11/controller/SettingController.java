package com.tessi.cxm.pfl.ms11.controller;

import com.cxm.tessi.pfl.shared.flowtreatment.model.response.DepositValidation;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PreProcessingSettingResponseDto;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.SettingResponse;
import com.tessi.cxm.pfl.ms11.entity.Setting;
import com.tessi.cxm.pfl.ms11.entity.SettingInstruction;
import com.tessi.cxm.pfl.ms11.service.SettingService;
import com.tessi.cxm.pfl.shared.model.PortalSettingConfigStatusDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.ProfileClientSettingRequest;
import com.tessi.cxm.pfl.shared.utils.CustomerDepositModeDto;
import com.tessi.cxm.pfl.shared.utils.SharedConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/setting")
@Tag(name = "Setting", description = "The API endpoints to manage setting.")
public class SettingController {

  private final SettingService settingService;

  /**
   * To get deposit validation of {@link Setting}.
   *
   * @see SettingService#getDepositValidation(String, String, String, String)
   */
  @GetMapping("/deposit-validation/{customer}/{depositType}/{extension}")
  public ResponseEntity<FlowProcessingResponse<DepositValidation>> getDepositValidation(
      @Parameter(
          in = ParameterIn.PATH,
          description = "directory name of the company into flow repository.",
          schema = @Schema(type = "string"),
          example = "ENI")
      @PathVariable
          String customer,
      @Parameter(
          in = ParameterIn.PATH,
          description = "type of deposit mode.",
          schema = @Schema(type = "string"),
          example = "Batch")
      @PathVariable
          String depositType,
      @Parameter(
          in = ParameterIn.PATH,
          description = "file extension.",
          schema = @Schema(type = "string"),
          example = "zip")
      @PathVariable
          String extension,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "connector of the company.",
          schema = @Schema(type = "string"),
          example = "C1")
      @RequestParam(required = false, defaultValue = "")
          String connector) {
    return ResponseEntity.ok(
        settingService.getDepositValidation(customer, depositType, connector, extension));
  }

  /**
   * To get deposit validation of {@link Setting}.
   *
   * @see SettingService#getDepositValidation(String, String)
   */
  @GetMapping("/deposit-validation/{customer}")
  public ResponseEntity<FlowProcessingResponse<DepositValidation>> getDepositValidation(
      @Parameter(
              in = ParameterIn.PATH,
              description = "The name of the customer.",
              schema = @Schema(type = "string"),
              example = "Tessi")
          @PathVariable
          String customer,
      @Parameter(
              in = ParameterIn.QUERY,
              description = "The type of flow.",
              schema = @Schema(type = "string"),
              example = "Tessi/Portal/CampaignSms/csv")
          @RequestParam("flowType")
          String flowType) {
    return ResponseEntity.ok(settingService.getDepositValidation(customer, flowType));
  }

  /**
   * To retrieve channel and sub-channel from setting.
   *
   * @see SettingService#extractSettingChannelAndSubChannel(String, Long, String)
   */
  @Operation(description = "To retrieve channel and sub-channel from setting.")
  @GetMapping("/identification")
  public ResponseEntity<FlowProcessingResponse<SettingResponse>>
  extractSettingChannelAndSubChannel(
      @Parameter(
              in = ParameterIn.QUERY,
              description = "type of flow.",
              schema = @Schema(type = "string"),
              example = "ENI/Batch/C1/zip")
          @RequestParam(value = "flowType")
          String flowType,
      @Parameter(
              in = ParameterIn.QUERY,
              description = "id creator.",
              schema = @Schema(type = "string"),
              example = "XXX")
          @RequestParam(value = "idCreator", defaultValue = "")
          long idCreator,
      @Parameter(
              in = ParameterIn.QUERY,
              description = "name of flow.",
              schema = @Schema(type = "string"))
          @RequestParam(value = "flowName", defaultValue = "")
          String flowName) {
    return ResponseEntity.ok(settingService.extractSetting(flowType, idCreator, flowName));
  }

  /**
   * To retrieve object of instructions from {@link SettingInstruction}.
   *
   * @see SettingService#getInstructionDetails(String, Long, String)
   */
  @GetMapping("/pre-processing/{idCreator}")
  public ResponseEntity<FlowProcessingResponse<PreProcessingSettingResponseDto>>
  getInstructionDetails(
      @Parameter(
          in = ParameterIn.QUERY,
          description = "model name.",
          schema = @Schema(type = "string"),
          example = "MODEL_NAME")
      @RequestParam(value = "modelName", defaultValue = "")
          String modelName,
      @Parameter(
          in = ParameterIn.PATH,
          description = "id creator.",
          schema = @Schema(type = "string"),
          example = "XXX")
      @PathVariable
          long idCreator,
      @Parameter(
          in = ParameterIn.QUERY,
          description = "type of flow.",
          schema = @Schema(type = "string"),
          example = "ENI/Batch/C1/zip")
      @RequestParam(value = "flowType")
          String flowType) {
    return ResponseEntity.ok(
        this.settingService.getInstructionDetails(flowType, modelName, idCreator));
  }

  @GetMapping("/deposit-modes/{customer}")
  public ResponseEntity<List<CustomerDepositModeDto>> getDepositModes(
      @PathVariable("customer") String customer) {
    return ResponseEntity.ok(
        this.settingService.getDepositModes(customer));
}

@PostMapping("/flow-type")
public ResponseEntity<HttpStatus> createClientSetting(
    @RequestBody @Valid ProfileClientSettingRequest profileClientSettingRequest,
    @RequestHeader HttpHeaders headers) {
  this.settingService.createClientSetting(
      profileClientSettingRequest, headers.getFirst(HttpHeaders.AUTHORIZATION));
  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
}

  @PostMapping("/create/{customer}")
  public ResponseEntity<List<CustomerDepositModeDto>> create(
      @PathVariable("customer") String customer,
      @RequestBody List<CustomerDepositModeDto> depositModes,
      @RequestHeader HttpHeaders headers) {
    return ResponseEntity.ok(
        this.settingService.createOrModifiedClientSetting(customer, depositModes,
            headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }

  @PutMapping("/portal/config")
  public ResponseEntity<PortalSettingConfigStatusDto> modifiedPortalSettingStatus(
      @RequestBody PortalSettingConfigStatusDto portalSettingConfigStatusDto,
      @RequestHeader HttpHeaders headers) {
    return ResponseEntity.ok(
        this.settingService.modifiedPortalSettingConfig(portalSettingConfigStatusDto,
            headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }

  @GetMapping("/portal/config")
  public ResponseEntity<PortalSettingConfigStatusDto> getPortalSettingConfig(
      @RequestHeader HttpHeaders headers,
      @RequestParam("customer") String customer) {
    return ResponseEntity.ok(this.settingService.getPortalSettingConfig(customer,
        headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }

  @GetMapping("/portal/configuration")
  public ResponseEntity<PostalConfigurationDto> getPostalConfiguration(
      @RequestHeader HttpHeaders headers,
      @RequestParam("customer") String customer,
      @RequestParam(value = "sessions", required = false) List<String> sessions) {
    return ResponseEntity.ok(
        this.settingService.getPostalConfiguration(
            customer, headers.getFirst(HttpHeaders.AUTHORIZATION), sessions));
  }

  @PutMapping("/portal/configuration")
  public ResponseEntity<PostalConfigurationDto> modifiedINIConfiguration(@RequestHeader HttpHeaders headers,
      @RequestBody PostalConfigurationDto dto) {
    return ResponseEntity.ok(
        this.settingService.modifiedINIConfiguration(dto, headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }

  @GetMapping("/portal/config/file")
  public ResponseEntity<String> downloadPortalConfigFile(
      @RequestParam("clientName") String clientName) {
    return ResponseEntity.ok(this.settingService.getINIFile(clientName));
  }

  @GetMapping("/portal/has-config-signature")
  public ResponseEntity<Boolean> validateSignatureAttr(
      @RequestParam("modelName") String modelName) {
    return ResponseEntity.ok(this.settingService.validateSignatureAttributes(modelName));
  }

  @GetMapping("/portal/last-modified-configuration")
  public ResponseEntity<Map<String, Date>> getLastModifiedConfiguration() {
    return ResponseEntity.ok(
        Map.of(SharedConstants.LAST_MODIFIED, this.settingService.getLastModifiedConfiguration()));
  }
}
