package com.tessi.cxm.pfl.ms15.core.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignPreProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.CampaignPreProcessingResponse;
import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.tessi.cxm.pfl.ms15.model.CampaignDocumentFieldSet;
import com.tessi.cxm.pfl.shared.core.batch.RegexLineSplittingTokenizer;
import com.tessi.cxm.pfl.shared.core.batch.reader.CsvItemReader;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.exception.FileNotFoundException;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProduction;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileFlowDocument;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.HtmlUtils;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Handling process of reading csv of campaign.
 *
 * @author Vichet CHANN
 * @version 1.5.0
 * @since 01 Jun 2022
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class ReadCampaignCsvFileHandler extends AbstractExecutionHandler {

  private final FileService fileService;
  @Setter
  private Resource resource;

  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *                for storing all the current state changed.
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    return this.readCsv(context);
  }

  private ExecutionState readCsv(ExecutionContext context) {
    try {
      var fileReader = this.initCsvProperties(context);
      fileReader.setEncoding("UTF-8");
      List<PortalFileFlowDocument> documents = new ArrayList<>();
      while (true) {
        var doc = fileReader.read();
        if (doc == null) {
          break;
        }
        if (StringUtils.hasText(doc.getDocUUID())) {
          this.mappingDocument(doc, context);
          documents.add(doc);
        }
      }
      fileReader.close();
      context.put(
          PreProcessingConstant.CAMPAIGN_PRE_PROCESSING_RESPONSE,
          new CampaignPreProcessingResponse(documents));
      this.fileService.deleteDirectoryQuietly(this.resource.getFile().toPath());
      return ExecutionState.NEXT;
    } catch (Exception e) {
      log.error("Failed to read csv file", e);
      return ExecutionState.END;
    }
  }

  /**
   * Reference method for mapping document of {@link PortalFileFlowDocument} with context
   * {@link ExecutionContext}.
   *
   * @param doc     - object of {@link PortalFileFlowDocument}.
   * @param context - object of {@link ExecutionContext}.
   */
  private void mappingDocument(PortalFileFlowDocument doc, ExecutionContext context) {
    var campaignPreProcessingRequest =
        context.get(
            PreProcessingConstant.CAMPAIGN_PRE_PROCESSING_REQUEST,
            CampaignPreProcessingRequest.class);

    var attachment =
        context.get(
            PreProcessingConstant.HUB_ATTACHMENT_FILE_RESPONSE,
            PortalFileDocumentProduction.Attachment.class);
    addSubjectMail(doc, campaignPreProcessingRequest);
    doc.getProduction().setAttachment(attachment);
  }

  private void addSubjectMail(
      PortalFileFlowDocument document, CampaignPreProcessingRequest campaignPreProcessingRequest) {
    if (StringUtils.hasText(campaignPreProcessingRequest.getSubjectMail())) {
      final List<String> values =
          document.getProduction().getData().getDataValue().stream()
              .flatMap(map -> map.values().stream())
              .collect(Collectors.toList());
      document.setEmailObject(
          HtmlUtils.replaceHtmlContentWithVariables(
              campaignPreProcessingRequest.getSubjectMail(),
              Arrays.asList(campaignPreProcessingRequest.getVariables()),
              values));
    }
  }

  private CsvItemReader<PortalFileFlowDocument> initCsvProperties(ExecutionContext context) {

    var request =
        context.get(
            PreProcessingConstant.CAMPAIGN_PRE_PROCESSING_REQUEST,
            CampaignPreProcessingRequest.class);
    final var portalCampaignType =
        request.getFlowType().contains(FlowTreatmentConstants.PORTAL_CAMPAIGN_SMS)
            ? FlowTreatmentConstants.PORTAL_CAMPAIGN_SMS
            : FlowTreatmentConstants.PORTAL_CAMPAIGN_EMAIL;
    var filePath = context.get(PreProcessingConstant.FILE_PATH, Path.class);
    String flowName = request.getFlowName().replaceAll("\\s", "_");
    var fileName = flowName.concat(".").concat(FlowTreatmentConstants.CSV_EXTENSION);
    if (request.isRemoveDuplicate()) {
      fileName =
          FlowTreatmentConstants.CSV_MODIFY_PREFIX
              .concat("_")
              .concat(flowName)
              .concat(".")
              .concat(FlowTreatmentConstants.CSV_EXTENSION);
    }
    this.resource = new FileSystemResource(this.getCsvFile(filePath, fileName));
    // Prepare reader
    CampaignDocumentFieldSet campaignDocumentFieldSet =
        new CampaignDocumentFieldSet(
            portalCampaignType, request.getChannel(), request.getSubChannel(),
            request.getVariables());
    var fileReader =
        new CsvItemReader<>(
            resource,
            "Read Campaign Csv",
            request.isCsvHeader() ? 1 : 0,
            campaignDocumentFieldSet,
            this.getLineMapper(campaignDocumentFieldSet));
    fileReader.open(new org.springframework.batch.item.ExecutionContext());
    return fileReader;
  }

  // To get file by filename
  private File getCsvFile(Path filePath, String filename) {
    log.info("Getting filename : {}", filename);
    return FileUtils.listFiles(
            filePath.toFile(), new String[]{FlowTreatmentConstants.CSV_EXTENSION}, true)
        .stream()
        .filter(v -> filename.equals(FilenameUtils.getName(v.getPath())))
        .findFirst()
        .orElseThrow(() -> new FileNotFoundException("File not found!"));
  }

  private LineMapper<PortalFileFlowDocument> getLineMapper(
      FieldSetMapper<PortalFileFlowDocument> fieldSetMapper) {
    final var defaultLineMapper = new DefaultLineMapper<PortalFileFlowDocument>();
    final var regexLineTokenizer = new RegexLineSplittingTokenizer();
    regexLineTokenizer.setRegex("[,;]");
    defaultLineMapper.setLineTokenizer(regexLineTokenizer);
    defaultLineMapper.setFieldSetMapper(fieldSetMapper);
    return defaultLineMapper;
  }
}
