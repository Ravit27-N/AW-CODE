package com.tessi.cxm.pfl.ms3.core.batch.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.constant.ElementAssociationConstant;
import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDocumentContext;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentOwnerProjection;
import com.tessi.cxm.pfl.ms3.entity.xml.Document;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.shared.core.batch.task.AbstractTasklet;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.MultipartFileInstance;
import com.tessi.cxm.pfl.shared.utils.SerialExecutor;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation class for process base64 file.
 *
 * @author Piseth KHON
 */
@RequiredArgsConstructor
@Component
@StepScope
@Slf4j
public class TaskletBase64Executor extends AbstractTasklet implements StepExecutionListener {
  private static final String ELEMENT_ASSOCIATION_PATH = "elementsAssociation";
  private final FileService fileService;
  private final FileManagerResource fileManagerResource;

  private final FlowTraceabilityRepository flowTraceabilityRepository;

  private List<Document> documents;
  private List<ElementAssociationDocumentContext> elementAssociations;
  private KeycloakService keycloakService;

  @Autowired
  public void setKeycloakService(KeycloakService keycloakService){
    this.keycloakService = keycloakService;
  }

  /**
   * Given the current context in the form of a step contribution, do whatever is necessary to
   * process this unit inside a transaction. Implementations return {@link RepeatStatus#FINISHED} if
   * finished. If not they return {@link RepeatStatus#CONTINUABLE}. On failure throws an exception.
   *
   * @param contribution mutable state to be passed back to update the current step execution
   * @param chunkContext attributes shared between invocations but not between restarts
   * @return an {@link RepeatStatus} indicating whether processing is continuable. Returning {@code
   *     null} is interpreted as {@link RepeatStatus#FINISHED}
   * @throws Exception thrown if error occurs during execution.
   */
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    log.info("TaskletBase64Executor is executing...");
    var elementsAssociation = new ArrayList<ElementAssociationDocumentContext>();
    log.info("Size of document: {}", this.documents.size());
    var docIds = this.documents.stream().map(Document::getId).collect(Collectors.toList());
    var flowDocumentOwners = this.flowTraceabilityRepository.findFlowDocOwnerByDocIds(docIds);
    for (var doc : this.documents) {
      final var ownerId =
          flowDocumentOwners.stream()
              .filter(flowDocument -> flowDocument.getDocumentId() == doc.getId())
              .map(FlowDocumentOwnerProjection::getOwnerId)
              .findFirst()
              .orElse(0L);

      final Path base64Path =
          this.storeBase64File(doc.getNotification().getAccuseReception(), doc.getIdDoc());
      log.info("Temp path yo store base64: {}", base64Path);
      var fileProperties = this.composeToFileManager(base64Path, ownerId);
      elementsAssociation.add(getElementAssociation(doc, fileProperties));
    }

    this.elementAssociations = elementsAssociation;
    return RepeatStatus.FINISHED;
  }

  private ElementAssociationDocumentContext getElementAssociation(
      Document doc, FilePropertiesHandling propertiesFile) {
    return ElementAssociationDocumentContext.builder()
        .fileId(propertiesFile.getFileId())
        .elementName(ElementAssociationConstant.ACCUSE_RECEPTION)
        .documentId(doc.getId())
        .extension(FilenameUtils.getExtension(propertiesFile.getFileName()))
        .build();
  }

  /**
   * Initialize the state of the listener with the {@link StepExecution} from the current scope.
   *
   * @param stepExecution instance of {@link StepExecution}.
   */
  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.documents = this.getDocuments(this.getJobExecutionContext(stepExecution));
  }

  /**
   * Set elementsAssociation to context. Give a listener a chance to modify the exit status from a
   * step. The value returned will be combined with the normal exit status using {@link
   * ExitStatus#and(ExitStatus)}.
   *
   * <p>Called after execution of step's processing logic (both successful or failed). Throwing
   * exception in this method has no effect, it will only be logged.
   *
   * @param stepExecution {@link StepExecution} instance.
   * @return an {@link ExitStatus} to combine with the normal value. Return {@code null} to leave
   *     the old value unchanged.
   */
  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    stepExecution
        .getJobExecution()
        .getExecutionContext()
        .put(FlowTraceabilityConstant.ELEMENT_ASSOCIATION_CONTEXT, this.elementAssociations);
    log.info(
        "TaskletBase64Executor is completed with status : {}", ExitStatus.COMPLETED.getExitCode());
    new SerialExecutor(Executors.newCachedThreadPool()).execute(this::clearResourceFile);
    return ExitStatus.COMPLETED;
  }

  /** Convert object from context to collection of document. */
  public List<Document> getDocuments(ExecutionContext executionContext) {
    var mapper = new ObjectMapper();
    return mapper.convertValue(
        executionContext.get(FlowTraceabilityConstant.DOCUMENTS_ELEMENT_ASSOCIATION_CONTEXT),
        new TypeReference<>() {});
  }

  /**
   * store file base64.
   *
   * @return path of file
   */
  private Path storeBase64File(String base64, String docId) {
    String pdfExtension = ".pdf";
    var base64Path =
        this.fileService.getPath(ELEMENT_ASSOCIATION_PATH).resolve(docId.concat(pdfExtension));
    this.fileService.decodeBase64ToFile(base64.replaceAll("\\s+", ""), base64Path.toString());
    log.info("Base64 Path : {}", base64Path.toString());
    return base64Path;
  }

  /** Upload file to file manager. */
  public FilePropertiesHandling composeToFileManager(Path sourcePath, Long ownerId) {
    final File file = sourcePath.toFile();
    FilePropertiesHandling filePropertiesHandling;
    var fileMetadataResponse =
        this.fileManagerResource.uploadFile(
            new MultipartFileInstance(file),
            ownerId,
            "",
            "",
            "",
            0,
            false,
            null,
            null,
            BearerAuthentication.PREFIX_TOKEN.concat(this.keycloakService.getToken()));
    filePropertiesHandling =
        FilePropertiesHandling.builder()
            .fileId(fileMetadataResponse.getUuid())
            .fileName(fileMetadataResponse.getFileName())
            .originalName(fileMetadataResponse.getFileName())
            .fileSize(fileMetadataResponse.getSize())
            .filePath(fileMetadataResponse.getFileUrl())
            .fileUrl(fileMetadataResponse.getFileUrl())
            .extension(fileMetadataResponse.getExtension())
            .build();
    return filePropertiesHandling;
  }

  /** delete file. */
  private void clearResourceFile() {
    this.fileService.deleteDirectoryQuietly(this.fileService.getPath(ELEMENT_ASSOCIATION_PATH));
  }
}
