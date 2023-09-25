package com.allweb.rms.core.storage;

import java.util.List;

/**
 * Represent a specific storage container inside the {@link Storage}.
 *
 * <p>This container may contains files or sub-directories which are represented as {@link
 * StorageObject}.
 *
 * <p>Each and every {@link StorageObject} inside this container has an unique Base64 hash value as
 * key and has the volume's id terminated by "_" as prefix.
 *
 * @author <a href="mailto:sakal.tum@allweb.com.kh">Sakal TUM</a>
 * @see {@link StorageObject}
 * @see {@link StorageObjectManager}
 */
public interface Volume {

  /**
   * The volume's unique identifier.
   *
   * @return the Volume's unique identifier.
   */
  String getId();

  /**
   * Get the volume's alias name.
   *
   * @return Volume's alias name.
   */
  String getAlias();

  StorageObject getBaseStorageObject();

  /**
   * Get all storage objects inside this container.
   *
   * @return {@link List}<{@link StorageObject}> inside this container.
   */
  List<StorageObject> getStorageObjects();

  /**
   * Get a specific storage object by its unique Base64 hash value as key.
   *
   * @param hashKey The unique Base64 hash value.
   * @return {@link StorageObject}
   */
  StorageObject getStorageObject(String hashKey);
}
