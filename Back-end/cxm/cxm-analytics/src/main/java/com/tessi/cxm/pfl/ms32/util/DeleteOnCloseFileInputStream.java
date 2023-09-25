package com.tessi.cxm.pfl.ms32.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class DeleteOnCloseFileInputStream extends FileInputStream {

  private final File file;

  public DeleteOnCloseFileInputStream(File file) throws FileNotFoundException {
    super(file);
    this.file = file;
  }

  @Override
  public void close() throws IOException {
    super.close();
    FileUtils.deleteQuietly(file);
    log.debug("CSV file has deleted : {}", file.getAbsolutePath());
  }
}
