package com.tessi.cxm.pfl.ms15.core.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms15.constant.ProcessingConstant;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProcessing;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDocumentProcessingHandler extends AbstractExecutionHandler {
  private final FileService fileService;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return {@link ExecutionState}
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var uuid = UUID.randomUUID().toString();
    var fileId = context.get(ProcessingConstant.FILE_ID, String.class);
    var tmpPath = context.get(ProcessingConstant.TMP_FILE, Path.class).resolve(fileId);
    var fileControl =
        context.get(ProcessingConstant.PROCESSING_FILE_CONTROL, PortalFlowFileControl.class);
    var docPressings =
        fileControl.getFlow().getFlowDocuments().stream()
            .map(
                doc -> this.updateDocumentProcessing(doc, fileControl.getCustomer(), tmpPath, uuid))
            .collect(Collectors.toList());
    compressComposedFile(tmpPath.resolve(uuid));
    context.put(ProcessingConstant.PROCESSING_FILE_CONTROL, docPressings);
    context.put(ProcessingConstant.TMP_FILE, tmpPath);
    context.put(
        ProcessingConstant.TMP_FILE_NAME,
        uuid.concat(".".concat(FlowTreatmentConstants.ZIP_EXTENSION)));
    context.put(ProcessingConstant.UPLOAD_FILE_ID, uuid);
    context.put(FileManagerHandler.OPTION_KEY, FileManagerHandler.Option.POST);
    return ExecutionState.NEXT;
  }

  private PortalFileDocumentProcessing updateDocumentProcessing(
      PortalFileFlowDocument document, String company, Path source, String uuid) {
    var proc = document.getProcessing();
    var idDoc = getUUID();
    var fileName = getFileName(company, idDoc);
    final var fileProperties =
        renameFile(source.resolve(proc.getDocName()), source.resolve(uuid).resolve(fileName));
    proc.setIdDoc(idDoc);
    proc.setDocName(fileProperties.getFileName());
    proc.setSize(String.valueOf(fileProperties.getFileSize()));
    proc.setCreationDate(dateFormat.format(new Date()));
    log.info("Document name :{}", fileName);
    return proc;
  }

  /**
   * Rename existed file.
   *
   * @return fileProperties
   */
  private FilePropertiesHandling renameFile(Path source, Path newFile) {
    final long fileSize = FileUtils.sizeOf(source.toFile());
    this.fileService.moveFile(source.toString(), newFile.toString());
    return FilePropertiesHandling.builder()
        .fileName(FilenameUtils.getName(newFile.toString()))
        .fileSize(fileSize)
        .build();
  }

  /**
   * Generate short UUID (20 characters)
   *
   * @return short UUID
   */
  private String getUUID() {
    return UUID.randomUUID().toString().substring(0, 20);
  }

  /**
   * Generate filename combine with uuid.
   *
   * @param companyName refer to name of company
   * @param uuid random string
   * @return filename
   */
  public String getFileName(String companyName, String uuid) {
    return companyName
        .replaceAll("\\s+", "_")
        .concat("." + uuid + ".".concat(FlowTreatmentConstants.PDF_EXTENSION));
  }

  /** Compress composed file. */
  private void compressComposedFile(Path sourcePath) {
    log.info("ComposedFile path {}", sourcePath.toFile());
    this.fileService.compressFile(sourcePath);
  }
}
