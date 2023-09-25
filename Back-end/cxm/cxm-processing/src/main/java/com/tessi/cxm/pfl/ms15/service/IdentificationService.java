package com.tessi.cxm.pfl.ms15.service;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalSettingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.util.FlowTreatmentUtils;
import com.tessi.cxm.pfl.ms15.model.AnalyseModelResponse;
import com.tessi.cxm.pfl.ms15.model.AnalyseRequest;
import com.tessi.cxm.pfl.ms15.service.restclient.Go2pdfResource;
import com.tessi.cxm.pfl.ms15.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional
public class IdentificationService {

  private final FileManagerResource fileManagerResource;
  private final SettingFeignClient settingFeignClient;
  private final Go2pdfResource go2PdfResource;
  private final ModelMapper modelMapper;
  private final KeycloakService keycloakService;

  public IdentificationService(
      SettingFeignClient settingFeignClient,
      ModelMapper modelMapper,
      KeycloakService keycloakService,
      Go2pdfResource go2PdfResource,
      FileManagerResource fileManagerResource) {
    this.fileManagerResource = fileManagerResource;
    this.go2PdfResource = go2PdfResource;
    this.settingFeignClient = settingFeignClient;
    this.modelMapper = modelMapper;
    this.keycloakService = keycloakService;
  }

  /**
   * To retrieve channel and sub-channel that extracted from {@code Setting}.
   *
   * @param fileId    refer to id of file.
   * @param flowName  refer to flow name.
   * @param idCreator refer to id of creator.
   * @param flowType  refer to type object flow.
   * @return object of {@link ProcessCtrlIdentificationResponse} wrapped by
   * {@link FlowProcessingResponse}
   */
  public FlowProcessingResponse<ProcessCtrlIdentificationResponse> extractChannelAndSubChannelStep(
      String fileId,
      String flowName,
      Long idCreator,
      String flowType,
      String funcKey,
      String privKey) {
    log.info("--- Start extractChannelAndSubChannelStep ---");
    log.info("fileId = '" + fileId + "', " +
            "flowName = '" + flowName + "', " +
            "idCreator = '" + idCreator + "', " +
            "flowType = '" + flowType + "', " +
            "funcKey = '" + funcKey + "', " +
            "privKey = '" + privKey + "'");
    var bearerToken = BearerAuthentication.PREFIX_TOKEN.concat(keycloakService.getToken());
    log.info("bearerToken = '" + bearerToken + "'");
    var response =
        settingFeignClient.extractSetting(flowName, idCreator, flowType, bearerToken).getData();
    log.info("response = '" + response + "'");
    log.info("fileId will work later!! ({})", fileId);
    if (Objects.equals(response.toString(), "{}")) {
      return new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK);
    }
    var identificationResponse =
        this.modelMapper.map(response, ProcessCtrlIdentificationResponse.class);
    log.info("identificationResponse = '" + identificationResponse + "'");
    var getSetting = this.modelMapper.map(response, PortalSettingResponse.class);
    log.info("getSetting = '" + getSetting + "'");
    if ((flowType.contains(FlowTreatmentConstants.IV_DEPOSIT) || flowType.contains(FlowTreatmentConstants.BATCH_DEPOSIT))
        && FlowTreatmentUtils.checkFlowTypeExtension(
        flowType, FlowTreatmentConstants.PDF_EXTENSION)) {
      AnalyseModelResponse analyseModelName =
          getAnalyseModelName(
              fileId,
              flowType,
              flowName,
              bearerToken,
              getSetting.getConfigPath(),
              funcKey,
              privKey);
      log.info("analyseModelName = '" + analyseModelName + "'");
      if (analyseModelName == null) {
        identificationResponse.setModelName("");
      } else {
        identificationResponse.setModelName(analyseModelName.getModelName());
        identificationResponse.setBackground(analyseModelName.getBackground());
        identificationResponse.setAttachments(analyseModelName.getAttachments());
        identificationResponse.setSignature(analyseModelName.getSignature());
      }
      return new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK, identificationResponse);
    }
    if (flowType.contains(FlowTreatmentConstants.PORTAL_DEPOSIT)
        && FlowTreatmentUtils.checkFlowTypeExtension(
        flowType, FlowTreatmentConstants.PDF_EXTENSION)) {
      AnalyseModelResponse analyseModelName =
          getAnalyseModelName(
              fileId,
              flowType,
              flowName,
              bearerToken,
              getSetting.getConfigPath(),
              funcKey,
              privKey);
      if (analyseModelName == null) {
        identificationResponse.setModelName("");
      } else {
        identificationResponse.setModelName(analyseModelName.getModelName());
        identificationResponse.setBackground(analyseModelName.getBackground());
        identificationResponse.setAttachments(analyseModelName.getAttachments());
        identificationResponse.setSignature(analyseModelName.getSignature());
      }
    }
    return new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK, identificationResponse);
  }

  /**
   * Get analyse model.
   *
   * @param configFile refer to location of ini file
   * @return analyse model
   */
  private AnalyseModelResponse getAnalyseModelName(
      String fileId,
      String flowType,
      String flowName,
      String bearerToken,
      String configFile,
      String funcKey,
      String privKey) {
    log.info("--- Start getAnalyseModelName ---");
    var fileMetadata = this.fileManagerResource.getFile(fileId, funcKey, privKey, bearerToken);
    var filename = flowName.concat(".".concat(FlowTreatmentUtils.getFlowTypeExtension(flowType)));
    var analyseRequest =
        AnalyseRequest.builder()
            .simpleFile(fileMetadata.getContent())
            .configFile(configFile)
            .configName("PORTAIL_ANALYSE")
            .fileName(filename)
            .build();
    AnalyseModelResponse analyseModelResponse =
        this.go2PdfResource.analyseModel(analyseRequest, bearerToken);
    if (!analyseModelResponse.isDetectedModel()) {
      return null;
    }
    return analyseModelResponse;
  }
}
