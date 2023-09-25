package com.allweb.rms.core.storage.driver.filesystem;

import static com.allweb.rms.core.storage.StorageConstants.MIME_TYPE_DIRECTORY;

import com.allweb.rms.core.storage.SearchOption;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.MimeTypeUtils;
import com.allweb.rms.core.storage.utils.PathUtils;
import com.allweb.rms.utils.CheckedExceptionFunctionAdapter;
import com.allweb.rms.utils.RethrowableExceptionConsumerAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemStorageObjectManager extends AbstractChildFileSystemStorageObjectManager
    implements StorageObjectManager {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(FileSystemStorageObjectManager.class);

  FileSystemStorageObjectManager(FileSystemStorageObject fileSystemStorageObject) {
    super(fileSystemStorageObject, fileSystemStorageObject.getPath());
  }

  @Override
  public boolean isRoot() {
    return ((FileSystemVolume) this.baseStorageObject.getRootVolume())
        .getPath()
        .equals(this.getPath());
  }

  @Override
  public boolean isDirectory() throws IOException {
    return MIME_TYPE_DIRECTORY.equals(this.getBaseStorageObject().getMimeType());
  }

  @Override
  public boolean isFile() throws IOException {
    return !this.isDirectory();
  }

  @Override
  public boolean exists() {
    return Files.exists(this.getPath());
  }

  @Override
  public boolean exists(String child) {
    return Files.exists(this.getPath().resolve(Paths.get(child)).normalize());
  }

  @Override
  public boolean hasChild() {
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentPath)) {
      return directoryStream.iterator().hasNext();
    } catch (IOException e) {
      LOGGER.debug(e.getMessage(), e);
    }
    return false;
  }

  @Override
  public StorageObject getBaseStorageObject() {
    return this.baseStorageObject;
  }

  @Override
  public StorageObject getRoot() {
    if (this.isRoot()) {
      return this.getBaseStorageObject();
    }
    FileSystemVolume rootVolume = (FileSystemVolume) this.baseStorageObject.getRootVolume();
    Path rootPath = (rootVolume).getPath();
    return new FileSystemStorageObject(
        rootPath.getFileName().toString(), MIME_TYPE_DIRECTORY, rootVolume, Paths.get(""));
  }

  @Override
  public StorageObject getParent() {
    Path parent = this.getPath().getParent();
    if (this.isRoot()) {
      return this.getBaseStorageObject();
    } else if (parent.equals(this.getRoot().getStorageObjectManager().getPath())) {
      return this.getRoot();
    } else if (this.baseStorageObject.getPath().startsWith(parent)) {
      Path rootVolumePath =
          ((FileSystemVolume)
                  FileSystemStorageObjectManager.this.getBaseStorageObject().getRootVolume())
              .getPath();
      Path parentName = PathUtils.removeParentPath(rootVolumePath, parent);

      return new FileSystemStorageObject(
          parent.getFileName().toString(),
          MIME_TYPE_DIRECTORY,
          this.baseStorageObject.getRootVolume(),
          parentName);
    }
    return this.getBaseStorageObject();
  }

  @Override
  public Path getPath() {
    return this.currentPath;
  }

  @Override
  public URI getURI() {
    return this.getPath().toUri();
  }

  @Override
  public void create() throws IOException {
    if (this.isDirectory()) {
      Files.createDirectories(this.getPath());
    } else {
      Files.createFile(this.getPath());
    }
  }

  @Override
  public StorageObject rename(String newName) throws IOException {
    String currentName = this.getPath().toFile().getName();
    if (currentName.equals(newName)) {
      return this.getBaseStorageObject();
    }

    Files.move(this.getPath(), this.getPath().resolveSibling(newName));

    return this.getParent().getStorageObjectManager().getChild(newName);
  }

  @Override
  public List<StorageObject> search(String key, SearchOption searchOption) {
    return search(
        path -> {
          if (SearchOption.EXACT_MATCH.equals(searchOption)) {
            return path.getFileName().toString().equals(key);
          } else if (SearchOption.RELEVANT.equals(searchOption)) {
            return path.getFileName().toString().contains(key);
          }
          return false;
        });
  }

  @Override
  public List<StorageObject> search(Predicate<Path> filterPredicate) {
    final Path rootVolumePath =
        ((FileSystemVolume) this.getBaseStorageObject().getRootVolume()).getPath();
    try (Stream<Path> fileWalker = Files.walk(this.getPath())) {
      return fileWalker
          .filter(filterPredicate)
          .map(
              CheckedExceptionFunctionAdapter.applyChecked(
                  path ->
                      FileSystemStorageObject.builder()
                          .alias(path.getFileName().toString())
                          .volume(this.baseStorageObject.getRootVolume())
                          .mimeType(MimeTypeUtils.getMimeType(path))
                          .path(PathUtils.removeParentPath(rootVolumePath, path))
                          .build()))
          .collect(Collectors.toList());
    } catch (IOException e) {
      LOGGER.debug(e.getMessage(), e);
    }
    return new ArrayList<>();
  }

  @Override
  public long getLastModified() {
    return this.getPath().toFile().lastModified();
  }

  @Override
  public long size(boolean recursively) throws IOException {
    if (this.isFile()) {
      return Files.size(this.getPath());
    } else if (recursively) { // if it is a directory
      try (Stream<Path> childPathStream = Files.walk(this.getPath())) {
        return childPathStream
            .filter(Files::isRegularFile)
            .map(CheckedExceptionFunctionAdapter.applyChecked(Files::size))
            .reduce(0L, Long::sum);
      }
    }
    return 0L;
  }

  @Override
  public StorageObject copyTo(StorageObject target) throws IOException {
    if (this.isDirectory()) {
      return this.copyDirectories(target);
    } else {
      return this.copyToFile(target);
    }
  }

  /**
   * Copy current file represent by this current object to the target destination.
   *
   * @param destinationFile The {@link StorageObject} represent the target destination include its
   *     name and its parent directory.
   * @return The {@link StorageObject} represent the target destination
   * @throws IOException I/O exception.
   */
  StorageObject copyToFile(StorageObject destinationFile) throws IOException {
    StorageObjectManager destinationFileStorageObjectManager =
        destinationFile.getStorageObjectManager();
    StorageObjectManager destinationParentStorageObjectManager =
        destinationFile.getStorageObjectManager().getParent().getStorageObjectManager();

    if (!destinationParentStorageObjectManager.exists()) {
      destinationParentStorageObjectManager.create();
    }
    if (!destinationFileStorageObjectManager.exists()) {
      destinationParentStorageObjectManager.createFile(destinationFile.getName());
    }

    try (OutputStream out = destinationFileStorageObjectManager.openOutputStream()) {
      try (InputStream in = this.openInputStream()) {
        IOUtils.copyLarge(in, out);
      }
    }

    return destinationFile;
  }

  StorageObject copyDirectories(StorageObject destinationDirectory) throws IOException {
    StorageObjectManager destinationDirectoryStorageObjectManager =
        destinationDirectory.getStorageObjectManager();

    if (!destinationDirectoryStorageObjectManager.exists()) {
      destinationDirectoryStorageObjectManager.create();
    }

    try (Stream<Path> walker = Files.walk(this.getPath())) {
      walker
          .filter(path -> !path.equals(this.getPath()))
          .forEach(
              RethrowableExceptionConsumerAdapter.acceptMayThrow(
                  path -> {
                    Path destination =
                        destinationDirectoryStorageObjectManager
                            .getPath()
                            .resolve(path.getFileName().toString());
                    Files.copy(path, destination);
                  }));
    }

    return destinationDirectory;
  }

  @Override
  public void remove() throws IOException {
    if (this.isDirectory()) {
      FileUtils.deleteDirectory(this.getPath().toFile());
    } else {
      Files.delete(this.getPath());
    }
  }
}
