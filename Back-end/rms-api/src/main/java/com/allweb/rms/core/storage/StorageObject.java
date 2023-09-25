package com.allweb.rms.core.storage;

import com.allweb.rms.core.storage.driver.filesystem.FileSystemStorageObject;
import java.io.IOException;

/**
 * Represent a specific object of target storage like file or directory.
 *
 * <p>This {@link StorageObject} contains at least a {@link StorageObjectManager} which may use in
 * managing the the specific storage target like create or delete target file or directory
 * represented by this {@link StorageObject}.
 *
 * @author Sakal TUM
 * @see {@link FileSystemStorageObject}
 */
public interface StorageObject {

  String getAlias();

  String getName();

  /**
   * Get mime type of current storage object.
   *
   * @return Mime type.
   */
  String getMimeType() throws IOException;

  /**
   * The root {@link Volume} containing this object.
   *
   * @return {@link Volume}
   */
  Volume getRootVolume();

  /**
   * The encoded hash string value which represent the specific storage target of this current
   * object. {@link StorageObject} object.
   *
   * <p>The storage target maybe a file of a directory.
   *
   * @param includeVolumeId {@value true} if include volume id at the start of this hash value,
   *     otherwise false.
   * @return Hash key.
   */
  String getHashKey(boolean includeVolumeId);

  /**
   * Get current {@link StorageObjectManager} of this current {@link StorageObject} which may use to
   * process storage related operation.
   *
   * @return {@link StorageObjectManager} object use to manage this current {@link StorageObject}
   */
  StorageObjectManager getStorageObjectManager();

  /**
   * @return
   */
  SecurityConstraints getSecurityConstraints();
}
