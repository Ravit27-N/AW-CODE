package com.allweb.rms.core.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public interface ChildStorageObjectManager {
  /**
   * Create a child file.
   *
   * @param childFile The child file name without sub parent directories.
   * @return {@link StorageObject} object represent this created file.
   * @throws IOException I/O exception.
   */
  StorageObject createFile(String childFile) throws IOException;

  /**
   * Create a child file.
   *
   * @param childFile The child file name which may include its sub-parent directories.
   * @param createNonexistentParentDirectories {@code True} to create its sub-parent directories if
   *     its not exist.
   * @return {@link StorageObject} object represent this created file for further use.
   * @throws IOException I/O exception
   */
  StorageObject createFile(String childFile, boolean createNonexistentParentDirectories)
      throws IOException;

  /**
   * Create a child folder.
   *
   * @param childFolder Child directory's name.
   * @return {@link StorageObject} object represent this created directory.
   * @throws IOException I/O exception
   */
  StorageObject createDirectory(String childFolder) throws IOException;

  /**
   * Move a child file or folder to the target destination.
   *
   * @param source {@link Path} to child source file or directory.
   * @param destination {@link Path} to destination file or directory.
   * @return The already moved destination {@link StorageObject}.
   * @return {@link StorageObject}
   */
  StorageObject move(StorageObject source, StorageObject destination) throws IOException;

  /**
   * Delete child file or folder
   *
   * @param childPath {@link StorageObject} represent a deleting child file or directory.
   * @param recursively {@code true} if {@code childPath} is a folder and contains files or folders
   *     to recursively delete all child files and folders.
   * @throws IOException I/O exception
   */
  void remove(String childPath, boolean recursively) throws IOException;

  /**
   * Rename child file or folder.
   *
   * @param child The original name of the renaming child file or directory.
   * @param newName The new name of the child file or directory.
   * @return The renamed {@link StorageObject}.
   * @throws IOException I/O exception
   */
  StorageObject renameChild(String child, String newName) throws IOException;

  /**
   * Get {@link StorageObject} for accessing the child file or folder.
   *
   * @param child File or directory name.
   * @return {@link StorageObject}
   * @throws IOException I/O Exception.
   */
  StorageObject getChild(String child);

  /**
   * Get all {@link StorageObject} of child files and folders.
   *
   * @return {@link List}<{@link StorageObject}>
   * @throws IOException
   */
  List<StorageObject> getChildren() throws IOException;

  /**
   * Get all {@link StorageObject} of child files and folders.
   *
   * @param maxDept The directory level from this current path to the end its sub-directories. Use
   *     {@value -1} or {@value Integer.MAX_VALUE} for accessing all child files and
   *     sub-directories.
   * @param predicate The filter operation on each path. Accept null value as no filtering operation
   *     is applied.
   * @return {@link List}<{@link StorageObject}>
   * @throws IOException
   */
  List<StorageObject> getChildren(int maxDept, Predicate<StorageObject> predicate)
      throws IOException;
}
