package com.allweb.rms.core.storage;

import java.util.List;

/**
 * Represent the Top-Level storage object that map to the sever's relative path.
 *
 * <p>This storage object compose of {@link Volume}s which is sometimes refer to folders or
 * directories that may contains files or sub-folders.
 *
 * <p>The files or sub-folders inside the volume are represented as {@link StorageObject} which has
 * an internal {@link StorageObjectManager} which is used for managing its child files or
 * sub-folders.
 *
 * @author <a href="mailto:sakal.tum@allweb.com.kh">Sakal TUM</a>
 * @see {@link Volume}
 * @see {@link StorageObject}
 * @see {@link StorageObjectManager}
 */
public interface Storage {

  /**
   * The alias name for {@link Storage} object.
   *
   * @return String represent the {@link Storage}'s alias.
   */
  String getAlias();

  /**
   * @return {@link List}<{@link Storage}> available in this Storage.
   */
  List<Volume> getVolumes();

  /**
   * Get {@link Volume} by unique volume id, must start with [a-z].
   *
   * @param id Unique volume id represent the {@link Volume} object;
   * @return {@link Volume}
   */
  Volume getVolume(String id);

  Volume getDefaultVolume();

  SecurityConstraints getSecurityConstraints();
}
