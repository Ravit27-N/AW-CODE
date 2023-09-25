package com.allweb.rms.core.storage.driver.filesystem;

import com.allweb.rms.core.storage.StreamableStorageObjectManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractFileSystemStreamableStorageObjectManager
    implements StreamableStorageObjectManager {
  protected final FileSystemStorageObject baseStorageObject;
  protected final Path currentPath;

  protected AbstractFileSystemStreamableStorageObjectManager(
      FileSystemStorageObject baseStorageObject, Path currentPath) {
    this.baseStorageObject = baseStorageObject;
    this.currentPath = currentPath;
  }

  @Override
  public InputStream openInputStream() throws IOException {
    return Files.newInputStream(this.currentPath);
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    return Files.newOutputStream(this.currentPath);
  }
}
