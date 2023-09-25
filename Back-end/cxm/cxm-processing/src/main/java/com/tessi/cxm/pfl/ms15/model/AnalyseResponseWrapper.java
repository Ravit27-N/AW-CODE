package com.tessi.cxm.pfl.ms15.model;

import com.tessi.cxm.pfl.shared.filectrl.model.FileDocumentProcessing;
import com.tessi.cxm.pfl.shared.filectrl.model.FileDocumentProduction;
import com.tessi.cxm.pfl.shared.filectrl.model.FileFlowDocument;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.util.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
public class AnalyseResponseWrapper implements Serializable {
  private List<AnalyseResponse> analyseResponses;
  private String nbDocuments;
  private String nbPages;
  private String nbDocumentsKO;
  private int offset = 1;
  private final ModelMapper modelMapper = new ModelMapper();

  public AnalyseResponseWrapper(List<AnalyseResponse> analyseResponses) {
    this.analyseResponses = analyseResponses;
  }

  public String getNbDocuments() {
    return String.valueOf(analyseResponses.size());
  }

  public String getNbPages() {
    return String.valueOf(analyseResponses.stream().mapToInt(AnalyseResponse::getPageNumber).sum());
  }

  public String getNbDocumentsKO() {
    return String.valueOf(analyseResponses.stream().mapToInt(AnalyseResponse::getState).filter(value -> value == 0).count());
  }

  public List<FileFlowDocument> getFlowDocuments() {
    return this.analyseResponses.stream().map(this::mapper).collect(Collectors.toList());
  }

  private FileFlowDocument mapper(AnalyseResponse analyseResponse) {
    this.offset = this.offset + analyseResponse.getPageNumber();
    FileFlowDocument document = modelMapper.map(analyseResponse, FileFlowDocument.class);
    document.setAddress(this.getCapitalizedAddress(analyseResponse));
    document.setOffset(String.valueOf(this.offset));
    final FileDocumentProduction production =
        modelMapper.map(analyseResponse, FileDocumentProduction.class);
    final FileDocumentProcessing processing =
        modelMapper.map(analyseResponse, FileDocumentProcessing.class);
    processing.setDocUuid(UUID.randomUUID().toString());
    document.setProcessing(processing);
    document.setProduction(production);
    document.setUuid(UUID.randomUUID().toString());
    return document;
  }

  private Map<String, String> getCapitalizedAddress(AnalyseResponse analyseResponse) {
    return analyseResponse.getAddress().entrySet().stream()
        .collect(Collectors.toMap(key -> StringUtils.capitalize(key.getKey()), Entry::getValue));
  }
}
