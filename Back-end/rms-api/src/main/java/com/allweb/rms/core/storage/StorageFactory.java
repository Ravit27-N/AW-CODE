package com.allweb.rms.core.storage;

/**
 * An interface providing a factory method for accessing a specific storage object.
 *
 * @author <a href="mailto:sakal.tum@allweb.com.kh">Sakal TUM</a>
 */
public interface StorageFactory {

  /**
   * Factory method for accessing the storage object.
   *
   * @return {@link Storage}
   */
  Storage getStorage();
}
