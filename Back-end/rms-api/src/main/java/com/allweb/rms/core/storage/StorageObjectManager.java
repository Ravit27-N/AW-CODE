package com.allweb.rms.core.storage;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represent the storage Manager own by {@link StorageObject}.
 *
 * <p>This interface is intended to use for managing the file or directory and also its child files
 * and sub-directories represented by {@link StorageObject} which owned this object.
 *
 * @author <a href="mailto:sakal.tum@allweb.com.kh">Sakal TUM</a>
 * @see StorageObject
 */
public interface StorageObjectManager
    extends ChildStorageObjectManager, StreamableStorageObjectManager {

  /**
   * Check if the file or directory represented by the owning {@link StorageObject} is a root
   * directory.
   *
   * @return {@code true} if the current path is the same as the path of the {@link Volume} its
   *     lives in or otherwise {@link false}.
   */
  boolean isRoot();

  /**
   * Check if the file or directory represented by the owning {@link StorageObject} is exactly a
   * folder.
   *
   * @return {@link true} if its exactly a folder or otherwise {@link false}.
   */
  boolean isDirectory() throws IOException;

  /**
   * Check if current storage object is exactly a file.
   *
   * @return True if its exactly a file or otherwise false.
   */
  boolean isFile() throws IOException;

  /**
   * Check if the file or directory represented by the owning {@link StorageObject} is already
   * exists.
   *
   * @return {@code true} if it is already exists or otherwise {@code false}.
   */
  boolean exists();

  /**
   * Check if current storage is already exists.
   *
   * @param child Child file or folder.
   * @return {@code true} if it is already exists or otherwise {@code false}.
   */
  boolean exists(String child);

  /**
   * Check if current storage hash at lease a single file or folder.
   *
   * @return {@code true} it has any files or folders or otherwise {@code false}.
   */
  boolean hasChild();

  /**
   * Get the {@link StorageObject} owning this current object.
   *
   * @return {@link StorageObject}.
   */
  StorageObject getBaseStorageObject();

  /**
   * Get the root {@link StorageObject}.
   *
   * @return {@link StorageObject} represent
   */
  StorageObject getRoot();

  /**
   * Get a parent directory as {@link StorageObject}.
   *
   * @return {@link StorageObject} of parent directory.
   */
  StorageObject getParent();

  /**
   * Get current {@link Path} of the current {@link StorageObject} owning this current object.
   *
   * @return {@link Path}
   */
  Path getPath();

  /**
   * Get Url represent the current path.
   *
   * @return {@link URL}
   */
  URI getURI();

  /**
   * Get lash modified timestamp of a file or directory.
   *
   * @return Timestamp as long integer.
   */
  long getLastModified();

  /**
   * Self create with the {@link Path } represent in {@link #getPath()}.
   *
   * @throws IOException I/O Exception.
   */
  void create() throws IOException;

  /**
   * Self delete.
   *
   * @throws IOException I/O exception
   */
  void remove() throws IOException;

  /**
   * Self renaming.
   *
   * @param newName The new name to be renamed to.
   * @return The renamed {@link StorageObject}.
   * @throws IOException I/O exception
   */
  StorageObject rename(String newName) throws IOException;

  /**
   * Copy current file or directory to the target destination.
   *
   * @param target The target destination where the file or directory will be copied to.
   * @return {@link StorageObject} of the copied file or directory.
   * @throws IOException I/O exception
   */
  StorageObject copyTo(StorageObject target) throws IOException;

  /**
   * Search for child files and directories.
   *
   * @param key File or directory name.
   * @param searchOption {@link SearchOption} which may be {@link SearchOption#EXACT_MATCH} or
   *     {@link SearchOption#RELEVANT}.
   * @return All matched child files and folder name. Null if current storage is file or not found
   *     any child match to the given key.
   */
  List<StorageObject> search(String key, SearchOption searchOption);

  /**
   * Search for child files and directories.
   *
   * <p>This method provide a filter predicate which is applied on each file or directory iteration
   * to determine if it should be considered for a matched.
   *
   * @param filterPredicate The filter operation of type {@link Predicate}<{@link Path}> on each
   *     files or directories iteration.
   * @return {@link List}<{@link StorageObject}> All matched child files and directories name
   *     specified by filterPredicate. Empty if the current search operation not found any child
   *     match to the specified filterPredicate.
   */
  List<StorageObject> search(Predicate<Path> filterPredicate);

  /*
   * Search for child files and directories.
   *
   * @param key          File or directory name.
   * @param extensions   File extensions to filter.
   * @param searchOption {@link SearchOption} which may be
   *                     {@link SearchOption#EXACT_MATCH} or
   *                     {@link SearchOption#RELEVANT}.
   * @return All matched child files and folder name. Null if current storage is
   *         file or not found any child match to the given key.
   */

  /**
   * Get a total size in bytes a file or files if the current object is a directory.
   *
   * @return Total size in bytes.
   * @throws IOException I/O exception
   */
  long size(boolean recursively) throws IOException;
}
