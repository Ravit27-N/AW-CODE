package com.tessi.cxm.pfl.ms15.service;

import com.tessi.cxm.pfl.ms15.model.DocumentFieldSet;
import com.tessi.cxm.pfl.ms15.model.DocumentInstructions.DocumentInstructionData;
import com.tessi.cxm.pfl.shared.core.batch.reader.CsvItemReader;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.filectrl.model.FileFlowDocument;
import com.tessi.cxm.pfl.shared.utils.SerialExecutor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Handle process extract file.
 */
@Slf4j
public class DocumentExtractorService {

  public static final String CSV_EXTENSION = "csv";
  private static final String READER_CONTEXT_NAME = "csvBatch_eni";
  @Getter
  private long rowCount;
  @Getter
  private List<FileFlowDocument> documents;
  @Setter
  private Resource resource;
  @Setter
  private boolean deletedResourceAfterRead;

  /**
   * Check resource and extension file.
   */
  private void validateResource() {
    Assert.notNull(this.resource, "Resource must not be null");
    Assert.isTrue(
        Objects.equals(
            FilenameUtils.getExtension(this.resource.getFilename()),
            DocumentExtractorService.CSV_EXTENSION),
        "Invalid resource file extension. Extension file resource must be "
            + DocumentExtractorService.CSV_EXTENSION);
  }

  /**
   * Read csv with resource file.
   */
  public void readCsv(DocumentInstructionData instructions, String channel, String subChannel) {
    log.info("--- Start readCsv ---");
    log.info("instructions = '" + instructions + "', " +
            "channel = '" + channel + "', " +
            "subChannel = '" + subChannel + "'");
    this.validateResource();
    try {
      var fileReader = this.initCsvReaderProp(instructions, channel, subChannel);
      int row = 0;
      var documents = new ArrayList<FileFlowDocument>();
      while (true) {
        var document = fileReader.read();
        if (document == null) {
          break;
        }
        row++;
        documents.add(document);
      }
      this.rowCount = row;
      this.documents = documents;
      fileReader.close();
    } catch (Exception e) {
      log.error("Failed to read csv file", e);
      throw new FileErrorException(String.format("Unable to read file because %s", e.getMessage()));
    }

    if (this.deletedResourceAfterRead) {
      this.deleteResourceAfterReadQuietly();
    }
  }

  /**
   * Initial CsvReader.
   *
   * @param channel      refer to channel of document
   * @param subChannel   refer to subChannel of document
   * @param instructions refer to identity of document data
   * @return {@link CsvItemReader}
   */
  private CsvItemReader<FileFlowDocument> initCsvReaderProp(
      DocumentInstructionData instructions, String channel, String subChannel) {
    log.info("Document instruction: {}", instructions);
    var fileReader =
        new CsvItemReader<>(
            this.resource,
            READER_CONTEXT_NAME,
            0,
            new DocumentFieldSet(instructions, channel, subChannel),
            null);
    fileReader.open(new ExecutionContext());
    return fileReader;
  }

  /**
   * Delete resource file without throw any exception.
   */
  private void deleteResource() {
    try {
      if (this.resource.getFile().delete()) {
        log.info("Resource file has been deleted successfully.");
      } else {
        log.error("Failed to delete file !");
      }
    } catch (IOException e) {
      log.error("Unable to delete file because {0}", e);
    }
  }

  /**
   * Delete resource as synchronized execute.
   */
  private void deleteResourceAfterReadQuietly() {
    var executor = new SerialExecutor(Executors.newSingleThreadExecutor());
    executor.execute(this::deleteResource);
  }
}
