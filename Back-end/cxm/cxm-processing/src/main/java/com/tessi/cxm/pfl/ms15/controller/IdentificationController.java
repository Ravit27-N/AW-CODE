package com.tessi.cxm.pfl.ms15.controller;

import com.tessi.cxm.pfl.ms15.service.IdentificationService;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/identification")
@RequiredArgsConstructor
@Tag(name = "Identification", description = "Manage the identification of the Flow")
public class IdentificationController {

  private final IdentificationService identificationService;

  /**
   * To retrieve channel and sub-channel that extracted from {@code Setting}.
   *
   * @see IdentificationService#extractChannelAndSubChannelStep(String, String, Long,
   *     String,String,String)
   */
  @GetMapping("/process-ctrl/{fileId}/{idCreator}")
  public ResponseEntity<FlowProcessingResponse<ProcessCtrlIdentificationResponse>>
      getChannelAndSubChannel(
          @Parameter(
                  in = ParameterIn.PATH,
                  description = "id of file that generated from file manager.",
                  schema = @Schema(type = "string"),
                  example = "file_id")
              @PathVariable
              String fileId,
          @Parameter(
                  in = ParameterIn.QUERY,
                  description = "type of flow.",
                  schema = @Schema(type = "string"),
                  example = "ENI/Batch/C1/zip")
              @RequestParam
              String flowType,
          @Parameter(
                  in = ParameterIn.PATH,
                  description = "id creator.",
                  schema = @Schema(type = "integer"),
                  example = "XXX")
              @PathVariable
              Long idCreator,
          @Parameter(
                  in = ParameterIn.QUERY,
                  description = "name of flow.",
                  schema = @Schema(type = "string"),
                  example = "Flow_X56BL1")
              @RequestParam(value = "flowName", defaultValue = "")
              String flowName,
          @RequestParam(value = "funcKey", required = false) String funcKey,
          @RequestParam(value = "privKey", required = false) String privKey) {
    return ResponseEntity.ok(
        identificationService.extractChannelAndSubChannelStep(
            fileId, flowName, idCreator, flowType, funcKey, privKey));
  }
}
