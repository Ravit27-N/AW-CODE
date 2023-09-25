package com.allweb.rms.core.storage.driver.filesystem;

import static com.allweb.rms.core.storage.StorageConstants.MIME_TYPE_DIRECTORY;

import com.allweb.rms.core.storage.ChildStorageObjectManager;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.MimeTypeUtils;
import com.allweb.rms.core.storage.utils.PathUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public abstract class AbstractChildFileSystemStorageObjectManager
    extends AbstractFileSystemStreamableStorageObjectManager implements ChildStorageObjectManager {

  protected AbstractChildFileSystemStorageObjectManager(
      FileSystemStorageObject baseStorageObject, Path currentPath) {
    super(baseStorageObject, currentPath);
  }

  @Override
  public StorageObject createDirectory(String childFolder) throws IOException {
    Files.createDirectories(this.currentPath.resolve(childFolder));
    return new FileSystemStorageObject(
        childFolder,
        MIME_TYPE_DIRECTORY,
        this.baseStorageObject.getRootVolume(),
        resolveChildPath(childFolder));
  }

  @Override
  public StorageObject createFile(String childFile) throws IOException {
    Path childFilePath = this.currentPath.resolve(childFile);
    Files.createFile(childFilePath);
    Path resolvedChildFilePath = resolveChildPath(childFile);
    return FileSystemStorageObject.builder()
        .alias(childFile)
        .mimeType(MimeTypeUtils.getMimeType(childFilePath))
        .volume(this.baseStorageObject.getRootVolume())
        .path(resolvedChildFilePath)
        .build();
  }

  @Override
  public StorageObject createFile(String childFile, boolean createNonexistentParentDirectories)
      throws IOException {
    Path file = this.currentPath.resolve(childFile).normalize().toAbsolutePath();
    if (createNonexistentParentDirectories) {
      Path parentFolder = this.currentPath.resolve(file.getParent());
      if (!Files.exists(parentFolder)) {
        Files.createDirectories(parentFolder);
      }
      Files.createFile(file);
      return FileSystemStorageObject.builder()
          .alias(childFile)
          .mimeType(MimeTypeUtils.getMimeType(file))
          .volume(this.baseStorageObject.getRootVolume())
          .path(this.resolveChildPath(childFile))
          .build();
    } else {
      return this.createFile(childFile);
    }
  }

  @Override
  public StorageObject getChild(String child) {
    Path childPath = this.currentPath.resolve(child);
    String mimeType = null;
    try {
      mimeType = MimeTypeUtils.getMimeType(childPath);
    } catch (IOException ioException) {
      log.debug(ioException.getMessage(), ioException);
    }
    return FileSystemStorageObject.builder()
        .alias(child)
        .mimeType(mimeType)
        .volume(this.baseStorageObject.getRootVolume())
        .path(this.resolveChildPath(child))
        .build();
  }

  @Override
  public List<StorageObject> getChildren() throws IOException {
    return this.getChildren(1, null);
  }

  @Override
  public List<StorageObject> getChildren(int maxDept, Predicate<StorageObject> predicate)
      throws IOException {
    Path rootVolumePath = ((FileSystemVolume) this.baseStorageObject.getRootVolume()).getPath();
    try (Stream<Path> fileWalker =
        Files.walk(this.currentPath, maxDept < 1 ? Integer.MAX_VALUE : maxDept)) {
      return fileWalker
          .filter(path -> !this.currentPath.equals(path))
          .map(
              path ->
                  FileSystemStorageObject.builder()
                      .alias(path.toString())
                      .volume(this.baseStorageObject.getRootVolume())
                      .path(PathUtils.removeParentPath(rootVolumePath, path))
                      .build())
          .filter(storageObject -> predicate == null ? Boolean.TRUE : predicate.test(storageObject))
          .collect(Collectors.toList());
    }
  }

  @Override
  public StorageObject move(StorageObject source, StorageObject destination) throws IOException {
    Path sourcePath = source.getStorageObjectManager().getPath().normalize();
    Path destinationPath = destination.getStorageObjectManager().getPath().normalize();
    StorageObjectManager parentManager =
        destination.getStorageObjectManager().getParent().getStorageObjectManager();
    if (source.getStorageObjectManager().isFile()) {
      if (!parentManager.exists()) {
        parentManager.create();
      }
      FileUtils.moveFile(sourcePath.toFile(), destinationPath.toFile());
    } else if (source.getStorageObjectManager().isDirectory()) {
      FileUtils.moveDirectory(sourcePath.toFile(), destinationPath.toFile());
    }
    return FileSystemStorageObject.builder()
        .alias(destinationPath.toString())
        .volume(this.baseStorageObject.getRootVolume())
        .path(destinationPath)
        .build();
  }

  @Override
  public void remove(String childPath, boolean recursively) throws IOException {
    if (recursively) {
      FileUtils.deleteDirectory(this.currentPath.resolve(childPath).toFile());
    } else {
      Files.deleteIfExists(this.currentPath.resolve(childPath));
    }
  }

  @Override
  public StorageObject renameChild(String child, String newName) throws IOException {
    if (Files.exists(this.currentPath)) {
      throw new IOException("Current StorageObject is not existed.");
    }
    if (Files.isDirectory(this.currentPath)) {
      throw new IOException("Current StorageObject is not a directory.");
    }
    if (Files.exists(this.currentPath.resolve(child))) {
      throw new IOException("Child is not existed.");
    }
    String currentName = this.currentPath.toFile().getName();
    if (currentName.equals(newName)) {
      return this.baseStorageObject;
    }
    return this.getChild(child).getStorageObjectManager().rename(newName);
  }

  Path resolveChildPath(String child) {
    Path childPath = this.currentPath.resolve(Paths.get(child));
    int subPathBeginIndex =
        ((FileSystemVolume) this.baseStorageObject.getRootVolume()).getPath().getNameCount();
    int subPathEndIndex = childPath.getNameCount();
    return childPath.subpath(subPathBeginIndex, subPathEndIndex);
  }
}
