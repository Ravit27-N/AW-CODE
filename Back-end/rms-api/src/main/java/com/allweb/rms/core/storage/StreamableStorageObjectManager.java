package com.allweb.rms.core.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamableStorageObjectManager {
  /**
   * If its is file, open for reading, not support for folder.
   *
   * @return {@link InputStream}
   * @throws IOException I/O exception
   */
  InputStream openInputStream() throws IOException;

  /**
   * If its is file, open for writing, not support for folder.
   *
   * @return {@link OutputStream}
   * @throws IOException {@inheritDoc IOException}
   */
  OutputStream openOutputStream() throws IOException;
}
