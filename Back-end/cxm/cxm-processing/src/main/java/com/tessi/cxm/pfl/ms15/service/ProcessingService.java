package com.tessi.cxm.pfl.ms15.service;

import static com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant.COMPOSED_PDF_PATH;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalSettingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms15.constant.ProcessingConstant;
import com.tessi.cxm.pfl.ms15.core.chain.FlowDepositPDFProcessingChain;
import com.tessi.cxm.pfl.ms15.core.handler.FileManagerHandler;
import com.tessi.cxm.pfl.ms15.model.AnalyseRequest;
import com.tessi.cxm.pfl.ms15.model.AnalyseResponse;
import com.tessi.cxm.pfl.ms15.model.ResponseDocumentWrapper;
import com.tessi.cxm.pfl.ms15.service.restclient.Go2pdfResource;
import com.tessi.cxm.pfl.ms15.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionManager;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProcessing;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.SerialExecutor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handle process transform composed file.
 *
 * @since 01/04/22
 */
@Service
@Slf4j
public class ProcessingService {
  private final FileService fileService;
  private final FileManagerResource fileManagerResource;
  private final Go2pdfResource go2pdfFeignClient;
  private final SettingFeignClient settingFeignClient;
  private final String processingComposed = "processing-composed";
  private final String newProcessingComposedFile = "processing-composed-rename";
  private final String zipExtension = ".".concat(FlowTreatmentConstants.ZIP_EXTENSION);
  private final String pdfExtension = ".".concat(FlowTreatmentConstants.PDF_EXTENSION);
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

  public ProcessingService(
      FileService fileService,
      FileManagerResource fileManagerResource,
      Go2pdfResource go2pdfFeignClient,
      SettingFeignClient settingFeignClient) {
    this.fileService = fileService;
    this.fileManagerResource = fileManagerResource;
    this.go2pdfFeignClient = go2pdfFeignClient;
    this.settingFeignClient = settingFeignClient;
  }

  /**
   * Generate short UUID (20 characters)
   *
   * @return short UUID
   */
  private String getUUID() {
    return UUID.randomUUID().toString().substring(0, 20);
  }

  public String getBase64File(String fileId, String funcKey, String privKey, String token) {
    return this.fileManagerResource.getFile(fileId, funcKey, privKey, token).getContent();
  }

  /**
   * Upload composed file to File manager server.
   *
   * @param refFile id of default file
   * @param idCreator
   * @return id of composed file
   */
  public String composeToFileManager(
      String refFile, Path sources, Long idCreator, String funcKey, String privKey, String token) {
    final File zip =
        sources.resolveSibling(newProcessingComposedFile.concat(zipExtension)).toFile();
    return composeToFileManager(refFile, "", zip, idCreator, funcKey, privKey, token);
  }

  public String composeToFileManager(
      String refFile,
      String fileId,
      File sources,
      Long idCreator,
      String funcKey,
      String privKey,
      String token) {
    try {
      final MultipartFile multipartFile = this.fileService.fileToMultipartFile(sources);

      return this.fileManagerResource
          .uploadFile(
              multipartFile, idCreator, fileId, refFile, "", 0, false, funcKey, privKey, token)
          .getUuid();
    } catch (IOException e) {
      log.error("Unable to convert file multipartFile because %s", e);
      throw new FileErrorException(
          String.format("Unable to convert file multipartFile because %s", e.getMessage()));
    }
  }

  /** Compress composed file. */
  private Path compressComposedFile() {
    final Path sourcePath = this.fileService.getPath(processingComposed, newProcessingComposedFile);
    this.fileService.compressFile(sourcePath);
    return sourcePath;
  }

  /** Store temp file. */
  public void storeBase64(String fileId, String funcKey, String privKey, String token) {
    var processingFilePath =
        this.fileService
            .getPath(processingComposed)
            .resolve(UUID.randomUUID().toString().concat(zipExtension));
    final String composedBase64 = getBase64File(fileId, funcKey, privKey, token);
    this.fileService.decodeBase64ToFile(composedBase64, processingFilePath.toString());
    this.fileService.unZipCommand(processingFilePath, processingFilePath);
  }

  /**
   * Transform exist composed file to new.
   *
   * @return document/processing with new composedId
   */
  public FlowProcessingResponse<ResponseDocumentWrapper> transformComposedFile(
      String composedFileId,
      Long idCreator,
      ProcessingRequest processingRequest,
      String funcKey,
      String privKey,
      String token) {
    PortalFlowFileControl flowFileControl = processingRequest.getFileFlowTracing();

    processResourceFiles(
        flowFileControl, composedFileId, processingRequest, funcKey, privKey, token);

    this.storeBase64(composedFileId, funcKey, privKey, token);
    var docPressings =
        flowFileControl.getFlow().getFlowDocuments().stream()
            .map(doc -> this.updateDocumentProcessing(doc, flowFileControl.getCustomer()))
            .collect(Collectors.toList());
    final Path compressFile = this.compressComposedFile();
    final String newComposedFileId =
        this.composeToFileManager(
            flowFileControl.getUuid(), compressFile, idCreator, funcKey, privKey, token);
    if (StringUtils.isBlank(composedFileId)) {
      return new FlowProcessingResponse<>(Message.FAILED, HttpStatus.BAD_REQUEST);
    }
    new SerialExecutor(Executors.newSingleThreadExecutor()).execute(this::deleteResourceFile);
    return new FlowProcessingResponse<>(
        Message.FINISHED,
        HttpStatus.OK,
        new ResponseDocumentWrapper(docPressings, newComposedFileId));
  }

  /**
   * Update document/pressing.
   *
   * @return document/processing
   */
  private PortalFileDocumentProcessing updateDocumentProcessing(
      PortalFileFlowDocument document, String company) {
    var proc = document.getProcessing();
    var idDoc = getUUID();
    final var fileProperties = renameFile(proc.getDocName(), getFileName(company, idDoc));
    proc.setIdDoc(idDoc);
    proc.setDocName(fileProperties.getFileName());
    proc.setSize(String.valueOf(fileProperties.getFileSize()));
    proc.setCreationDate(dateFormat.format(new Date()));
    return proc;
  }

  /**
   * Rename existed file.
   *
   * @return fileProperties
   */
  private FilePropertiesHandling renameFile(String originalName, String newName) {
    var source = Path.of(processingComposed).resolve(originalName);
    final long fileSize = FileUtils.sizeOf(this.fileService.getPath(source.toString()).toFile());
    final Path newFile = source.resolveSibling(newProcessingComposedFile);
    this.fileService.moveFile(source.toString(), newFile.resolve(newName).toString());
    return FilePropertiesHandling.builder().fileName(newName).fileSize(fileSize).build();
  }

  /**
   * Generate filename combine with uuid.
   *
   * @param companyName refer to name of company
   * @param uuid random string
   * @return filename
   */
  public String getFileName(String companyName, String uuid) {
    return companyName.replaceAll("\\s+", "_").concat("." + uuid + pdfExtension);
  }

  /** Delete all file. */
  public void deleteResourceFile() {
    this.fileService.deleteDirectoryQuietly(fileService.getPath(processingComposed));
  }

  public void processResourceFiles(
      PortalFlowFileControl portalFlowFileControl,
      String composedFileId,
      ProcessingRequest processingRequest,
      String funcKey,
      String privKey,
      String token) {
    final String fileId = portalFlowFileControl.getUuid();
    final long idCreator = Long.parseLong(portalFlowFileControl.getUserId());
    final String modelName = portalFlowFileControl.getFlow().getModelName();
    final String flowType = portalFlowFileControl.getFlow().getType();
    var convertor = new ObjectMapper();
    var setting =
        convertor.convertValue(
            this.settingFeignClient.extractSetting(modelName, idCreator, flowType, token).getData(),
            PortalSettingResponse.class);
    String base64File = getBase64File(fileId, funcKey, privKey, token);
    getAnalyseDocuments(
        portalFlowFileControl,
        composedFileId,
        setting.getConfigPath(),
        base64File,
        fileId,
        processingRequest,
        idCreator,
        funcKey,
        privKey,
        token);
  }

  private void getAnalyseDocuments(
      PortalFlowFileControl portalFlowFileControl,
      String composedFileId,
      String configFile,
      String base64,
      String fileId,
      ProcessingRequest processingRequest,
      long idCreator,
      String funcKey,
      String privKey,
      String token) {
    final Path composedPath = fileService.getPath(COMPOSED_PDF_PATH, fileId);
    String filename = fileId.concat(".".concat(FlowTreatmentConstants.PDF_EXTENSION));
    final String fileSeparator = File.separator;
    var analyseRequest =
        AnalyseRequest.builder()
            .simpleFile(base64)
            .configFile(configFile)
            .outputDir(composedPath.toString().concat(fileSeparator))
            .configName("PORTAIL_ANALYSE")
            .fileName(filename)
            .background(processingRequest.getBackgroundPage())
                .filigrane(processingRequest.getFiligrane());
    Attachments attachments = removeEmptyStringFromAttachment(processingRequest.getAttachments());
    if (Objects.nonNull(attachments)) {
      analyseRequest.attachmentDto(attachments);
    }
    if (StringUtils.isNotBlank(processingRequest.getSignature())) {
      analyseRequest.signature(processingRequest.getSignature());
    }
    List<AnalyseResponse> analyseResponses =
        this.go2pdfFeignClient.analyse(analyseRequest.build(), token);
    if (!analyseResponses.isEmpty()) {
      modifyResourceFilename(analyseResponses, portalFlowFileControl, composedPath);
      compressComposedFile(composedPath);
      final String zipExtension = ".".concat(FlowTreatmentConstants.ZIP_EXTENSION);
      Path path = composedPath.resolveSibling(fileId.concat(zipExtension));
      composeToFileManager(
          fileId, composedFileId, path.toFile(), idCreator, funcKey, privKey, token);
    }
  }

  private void modifyResourceFilename(
      List<AnalyseResponse> analyseResponses,
      PortalFlowFileControl portalFlowFileControl,
      Path outputFile) {
    List<PortalFileDocumentProcessing> fileDocumentProcessings =
        portalFlowFileControl.getFlow().getFlowDocuments().stream()
            .map(PortalFileFlowDocument::getProcessing)
            .collect(Collectors.toList());
    final String xmlExtension = ".xml";
    IntStream.range(0, analyseResponses.size())
        .forEach(
            index -> {
              AnalyseResponse analyseResponse = analyseResponses.get(index);
              PortalFileDocumentProcessing portalFileDocumentProcessing =
                  fileDocumentProcessings.get(index);
              Path source = outputFile.resolve(analyseResponse.getDocName());
              this.fileService.moveFile(
                  source.toString(),
                  source.resolveSibling(portalFileDocumentProcessing.getDocName()).toString());
              String existXmlFile =
                  FilenameUtils.getBaseName(portalFileDocumentProcessing.getDocName())
                      .concat(xmlExtension);
              String newXmlFile =
                  FilenameUtils.getBaseName(analyseResponse.getDocName()).concat(xmlExtension);
              this.fileService.moveFile(
                  source.resolveSibling(newXmlFile).toString(),
                  source.resolveSibling(existXmlFile).toString());
            });
  }

  private void compressComposedFile(Path file) {
    this.fileService.compressFile(file);
  }

  private Attachments removeEmptyStringFromAttachment(Attachments attachments) {
    if (Objects.isNull(attachments)) {
      return null;
    }
    try {
      Attachments newAttachment = new Attachments();
      for (Field oldDeclaredField : attachments.getClass().getDeclaredFields()) {
        Field newAttachmentField =
            newAttachment.getClass().getDeclaredField(oldDeclaredField.getName());
        newAttachmentField.setAccessible(true);
        oldDeclaredField.setAccessible(true);
        final String fieldValue = String.valueOf(oldDeclaredField.get(attachments));
        if (StringUtils.isNotBlank(fieldValue)) {
          newAttachmentField.set(newAttachment, fieldValue);
        }
      }
      return newAttachment;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      log.error("{0}", e);
      return null;
    }
  }

  private ExecutionManager executionManager;

  @Autowired
  public void setFlowDepositPDFProcessingChain(
      FlowDepositPDFProcessingChain flowDepositPDFProcessingChain) {
    this.executionManager = flowDepositPDFProcessingChain;
  }

  @SuppressWarnings("unchecked")
  public FlowProcessingResponse<ResponseDocumentWrapper> process(
      String composedFileId,
      Long idCreator,
      ProcessingRequest processingRequest,
      String funcKey,
      String privKey,
      String token) {

    PortalFlowFileControl fileFlowTracing = processingRequest.getFileFlowTracing();
    final String modelName = fileFlowTracing.getFlow().getModelName();
    final String flowType = fileFlowTracing.getFlow().getType();
    final String flowId = fileFlowTracing.getUuid();
    ExecutionContext context = new ExecutionContext();
    context.put(ProcessingConstant.UPLOAD_FILE_ID, composedFileId);
    context.put(ProcessingConstant.ID_CREATOR, idCreator);
    context.put(ProcessingConstant.PROCESSING_REQUEST, processingRequest);
    context.put(ProcessingConstant.FUNC_KEY, funcKey);
    context.put(ProcessingConstant.PRIV_KEY, privKey);
    context.put(ProcessingConstant.TOKEN_KEY, token);
    context.put(ProcessingConstant.MODEL_NAME, modelName);
    context.put(ProcessingConstant.FLOW_TYPE, flowType);
    context.put(ProcessingConstant.FILE_ID, flowId);
    context.put(FileManagerHandler.OPTION_KEY, FileManagerHandler.Option.GET);
    context.put(ProcessingConstant.PROCESSING_FILE_CONTROL, fileFlowTracing);

    try {
      this.executionManager.execute(context);
    } catch (Exception e) {
      if (log.isDebugEnabled()) {
        log.debug("Unable to process flowDeposit pdf.", e);
      }
      log.error("Unable to process flowDeposit pdf.", e);
      return new FlowProcessingResponse<>(Message.FAILED, HttpStatus.OK);
    }
    String composedFile = context.get(ProcessingConstant.UPLOAD_FILE_ID, String.class);
    var docPressings = context.get(ProcessingConstant.PROCESSING_FILE_CONTROL, List.class);
    return new FlowProcessingResponse<>(
        Message.FINISHED, HttpStatus.OK, new ResponseDocumentWrapper(docPressings, composedFile));
  }
}
