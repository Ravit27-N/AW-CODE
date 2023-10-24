package com.innovationandtrust.sftp.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum FileProcessingDirectory {
  IN(FileDirectoryConstant.IN),
  PROCESSING(FileDirectoryConstant.PROCESSING),
  DONE(FileDirectoryConstant.DONE),
  ERROR(FileDirectoryConstant.ERROR),
  RECEIVED(FileDirectoryConstant.RECEIVED),
  PROCESSED(FileDirectoryConstant.PROCESSED),
  OUT(FileDirectoryConstant.OUT);

  private String directory;

  FileProcessingDirectory(String directory) {
    this.directory = directory;
  }
}
