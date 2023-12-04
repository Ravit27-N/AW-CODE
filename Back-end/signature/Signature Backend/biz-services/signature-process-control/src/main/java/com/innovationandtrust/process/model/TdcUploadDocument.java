package com.innovationandtrust.process.model;

import com.innovationandtrust.share.model.tdc.TdcJsonFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TdcUploadDocument {
  private byte[] fileContent;
  private TdcJsonFile tdcJsonFile;
  private String filename;

  public TdcUploadDocument(byte[] fileContent, TdcJsonFile tdcJsonFile, String filename) {
    this.fileContent = fileContent.clone();
    this.tdcJsonFile = tdcJsonFile;
    this.filename = filename;
  }
}
