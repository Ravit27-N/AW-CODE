package com.tessi.cxm.pfl.ms15.service;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PreProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalDocumentResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalSettingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.util.EmailValidatorUtils;
import com.cxm.tessi.pfl.shared.flowtreatment.util.FlowTreatmentUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.tessi.cxm.pfl.ms15.model.AnalyseRequest;
import com.tessi.cxm.pfl.ms15.model.AnalyseResponseWrapper;
import com.tessi.cxm.pfl.ms15.model.DocumentInstructions.DocumentInstructionData;
import com.tessi.cxm.pfl.ms15.service.restclient.Go2pdfResource;
import com.tessi.cxm.pfl.ms15.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.filectrl.model.Document;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.model.FileMetadata;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

/** Handle process of get document data with instruction of doc */
@Slf4j
@Service
@Transactional
public class PreProcessingService extends DocumentExtractorService {
  private final FileService fileService;
  private final FileManagerResource fileManagerResource;
  private final SettingFeignClient settingFeignClient;
  private final KeycloakService keycloakService;
  private final Go2pdfResource go2pdfFeignClient;
  private String composedFileId;

  public PreProcessingService(
      FileService fileService,
      FileManagerResource fileManagerResource,
      SettingFeignClient settingFeignClient,
      KeycloakService keycloakService,
      Go2pdfResource go2pdfFeignClient) {
    this.fileService = fileService;
    this.fileManagerResource = fileManagerResource;
    this.settingFeignClient = settingFeignClient;
    this.keycloakService = keycloakService;
    this.go2pdfFeignClient = go2pdfFeignClient;
  }

  /**
   * convert file to resource by using {@link FileSystemResource}
   *
   * @param path a file path
   * @return {@link Resource}
   */
  public Resource getResourceFile(String path) {
    return new FileSystemResource(path);
  }

  /**
   * Store base64 file from fileManagerResource.
   *
   * @param fileId refer to identity of file
   */
  private void storeBase64File(
      String fileId, String flowType, String funcKey, String privKey, String token) {
    final FileMetadata fileMetadata =
        this.fileManagerResource.getFile(fileId, funcKey, privKey, token);
    String tempPath = "temp";
    Path rootPath = this.fileService.getPath(tempPath, fileId);
    var destinationPath =
        rootPath.resolve(
            UUID.randomUUID().toString().concat(".").concat(FlowTreatmentConstants.ZIP_EXTENSION));
    this.fileService.decodeBase64ToFile(fileMetadata.getContent(), destinationPath.toString());
    if (FlowTreatmentUtils.checkFlowTypeExtension(flowType, FlowTreatmentConstants.ZIP_EXTENSION)) {
      this.fileService.unZipCommand(destinationPath, rootPath);
      String[] filename = {FlowTreatmentConstants.CSV_EXTENSION};
      Resource resource =
          this.fileService.getFileInfo(rootPath.toString(), filename).stream()
              .map(e -> this.getResourceFile(e.getFilePath()))
              .findFirst()
              .orElse(null);
      this.setResource(resource);
    }
  }

  /**
   * Get Documents.
   *
   * @param flowType refer to type of flow
   * @param fileId refer to identity of file
   * @param idCreator refer to identity of invoke user
   * @param channel refer to channel of document
   * @param subChannel refer to sub channel of document
   * @return {@link Document}
   */
  public FlowProcessingResponse<Document> getDocuments(
      String modelName,
      String flowType,
      String fileId,
      Long idCreator,
      String channel,
      String subChannel,
      String funcKey,
      String privKey) {
    log.info("--- Start getDocuments ---");
    var token = BearerAuthentication.PREFIX_TOKEN.concat(keycloakService.getToken());
    if (flowType.contains(FlowTreatmentConstants.BATCH_DEPOSIT) &&
            flowType.contains("pdf")) {
      var convertor = new ObjectMapper();
      var setting = convertor.convertValue(
                      this.settingFeignClient.extractSetting(modelName, idCreator, flowType, token).getData(),
                      PortalSettingResponse.class);
      final Document document = getPortalAnalyseDocument(
              fileId, token, setting.getConfigPath(), null, idCreator, funcKey, privKey);
      log.info("document = '" + document + "'");
      return new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK, document);
    }
    // Get instruction.
    var documentInstructions =
        settingFeignClient.getDocumentInstructions(flowType, modelName, idCreator, token);
    log.info(
        "Document instruction : flowType: {} ,idCreator: {}, instruction: {}",
        flowType,
        idCreator,
        documentInstructions);
    final Document document =
        this.extractDocument(
            documentInstructions.getData(),
            channel,
            subChannel,
            fileId,
            flowType,
            funcKey,
            privKey,
            token);
    if (document == null) {
      return new FlowProcessingResponse<>(Message.FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new FlowProcessingResponse<>(documentInstructions.getMessage(), HttpStatus.OK, document);
  }

  /**
   * Get documents extracted from csv.
   *
   * @param instructions refer to identity of document data
   * @return {@link Document}
   */
  public Document extractDocument(
      DocumentInstructionData instructions,
      String channel,
      String subChannel,
      String fileId,
      String flowType,
      String funcKey,
      String privKey,
      String token) {
    if (flowType.contains(FlowTreatmentConstants.BATCH_DEPOSIT)) {
      return getBatchAnalyseDocument(
          fileId, flowType, channel, subChannel, instructions, funcKey, privKey, token);
    } else {
      return null;
    }
  }

  /**
   * Validate field email recipient of document.
   *
   * @return invalid number of email recipient
   */
  private int validateEmailRecipient() {
    return this.getDocuments().stream()
        .mapToInt(
            doc ->
                Boolean.compare(true, EmailValidatorUtils.validateEmail(doc.getEmailRecipient())))
        .sum();
  }

  public Document getBatchAnalyseDocument(
      String fileId,
      String flowType,
      String channel,
      String subChannel,
      DocumentInstructionData instructions,
      String funcKey,
      String privKey,
      String token) {
    log.info("--- Start getBatchAnalyseDocument ---");
    log.info("fileId = '" + fileId + "', " +
            "flowType = '" + flowType + "', " +
            "channel = '" + channel + "', " +
            "subChannel = '" + subChannel + "', " +
            "instructions = '" + instructions + "', " +
            "funcKey = '" + funcKey + "', " +
            "privKey = '" + privKey + "', " +
            "token = '" + token + "',");
    this.storeBase64File(fileId, flowType, funcKey, privKey, token);
    this.setDeletedResourceAfterRead(true);
    this.readCsv(instructions, channel, subChannel);
    return Document.builder()
        .flowDocuments(this.getDocuments())
        .nbDocuments(String.valueOf(this.getRowCount()))
        .nbDocumentsKO(String.valueOf(this.validateEmailRecipient()))
        .nbPages("1")
        .build();
  }

  public Document getPortalAnalyseDocument(
      String fileId,
      String token,
      String configFile,
      PreProcessingRequest preProcessingRequest,
      Long idCreator,
      String funcKey,
      String privKey) {
    log.info("--- Start getPortalAnalyseDocument ---");
    log.info("fileId = '" + fileId + "', " +
            "token = '" + token + "', " +
            "configFile = '" + configFile + "', " +
            "preProcessingRequest = '" + preProcessingRequest + "', " +
            "idCreator = '" + idCreator + "', " +
            "funcKey = '" + funcKey + "', " +
            "privKey = '" + privKey + "'");
    final FileMetadata fileMetadata =
        this.fileManagerResource.getFile(fileId, funcKey, privKey, token);
    final Path composedPath = fileService.getPath(PreProcessingConstant.COMPOSED_PDF_PATH, fileId);
    try {
      Files.createDirectories(composedPath);
    } catch (IOException e) {
      log.error("Impossible to create directories = '" + composedPath + "'");
    }
    log.info("fileMetadata = '" + fileMetadata + "', " +
            "composedPath = '" + composedPath + "'");
    var analyseRequest =
        AnalyseRequest.builder()
            .simpleFile(fileMetadata.getContent())
            .configFile(configFile)
            .outputDir(composedPath.toString())
            .configName("PORTAIL_ANALYSE")
            .fileName(fileId.concat(".".concat(FlowTreatmentConstants.PDF_EXTENSION)))
            .build();
    log.info("analyseRequest = '" + analyseRequest + "'");
    var portalAnalyse =
        new AnalyseResponseWrapper(this.go2pdfFeignClient.analyse(analyseRequest, token));
    compressComposedFile(fileId);
    log.info("portalAnalyse = '" + portalAnalyse + "'");
    this.composedFileId = composeToFileManager(fileId, token, idCreator, funcKey, privKey);
    return Document.builder()
        .flowDocuments(portalAnalyse.getFlowDocuments())
        .nbDocuments(portalAnalyse.getNbDocuments())
        .nbDocumentsKO(portalAnalyse.getNbDocumentsKO())
        .nbPages(portalAnalyse.getNbPages())
        .build();
  }

  /** Compress composed file. */
  private void compressComposedFile(String fileId) {
    this.fileService.compressFile(
        fileService.getPath(PreProcessingConstant.COMPOSED_PDF_PATH, fileId));
  }

  /**
   * Upload composed file to File manager server.
   *
   * @param refFile id of default file
   * @return id of composed file
   */
  public String composeToFileManager(
      String refFile, String token, Long idCreator, String funcKey, String privKey) {
    final String zipExtension = ".".concat(FlowTreatmentConstants.ZIP_EXTENSION);
    final File zip =
        this.fileService
            .getPath(PreProcessingConstant.COMPOSED_PDF_PATH, refFile.concat(zipExtension))
            .toFile();
    try {
      final MultipartFile multipartFile = this.fileService.fileToMultipartFile(zip);
      return this.fileManagerResource
          .uploadFile(multipartFile, idCreator, "", refFile, "", 0, false, funcKey, privKey, token)
          .getUuid();
    } catch (IOException e) {
      log.error("Unable to convert file multipartFile because %s", e);
      throw new FileErrorException(
          String.format("Unable to convert file multipartFile because %s", e.getMessage()));
    }
  }

  public FlowProcessingResponse<PortalDocumentResponse> getDocumentsPortal(
          PreProcessingRequest preProcessingRequest,
      String modelName,
      String flowType,
      String fileId,
      Long idCreator,
      String funcKey,
      String privKey) {
    if (flowType.contains(FlowTreatmentConstants.PORTAL_DEPOSIT)) {
      return getPortalDocumentResponseFlowProcessingResponse(
          modelName, flowType, fileId, preProcessingRequest, idCreator, funcKey, privKey);
    }
    if (flowType.contains(FlowTreatmentConstants.IV_DEPOSIT)) {
      return getPortalDocumentResponseFlowProcessingResponse(
          modelName, flowType, fileId, preProcessingRequest, idCreator, funcKey, privKey);
    }
    return new FlowProcessingResponse<>(Message.FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private FlowProcessingResponse<PortalDocumentResponse>
      getPortalDocumentResponseFlowProcessingResponse(
          String modelName,
          String flowType,
          String fileId,
         PreProcessingRequest preProcessingRequest,
          Long idCreator,
          String funcKey,
          String privKey) {
    var token = BearerAuthentication.PREFIX_TOKEN.concat(keycloakService.getToken());
    var convertor = new ObjectMapper();
    var setting =
        convertor.convertValue(
            this.settingFeignClient.extractSetting(modelName, idCreator, flowType, token).getData(),
            PortalSettingResponse.class);
    final Document portalAnalyseDocument =
        getPortalAnalyseDocument(
            fileId, token, setting.getConfigPath(), preProcessingRequest, idCreator, funcKey, privKey);
    var wrapper = new PortalDocumentResponse();
    wrapper.setComposedFileId(this.composedFileId);
    wrapper.setDocument(portalAnalyseDocument);
    return new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK, wrapper);
  }
}
