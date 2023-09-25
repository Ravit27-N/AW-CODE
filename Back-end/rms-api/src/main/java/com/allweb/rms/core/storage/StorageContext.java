package com.allweb.rms.core.storage;

import jakarta.servlet.http.HttpServletRequest;

/**
 * An interface to provide information for storage proccessing requested by a user through and
 * specific API end point.
 *
 * @author <a href="mailto:sakal.tum@allweb.com.kh">Sakal TUM</a>
 * @see {@link Storage}
 * @see {@link HttpServletRequest}
 */
public interface StorageContext {

  /**
   * Return the storage instance for processing.
   *
   * @return An specific {@link Storage} object.
   */
  Storage getStorage();

  /**
   * Return an {@code HttpServletRequest} represent the storage proccessing request from a user.
   *
   * @return {@link HttpServletRequest}
   */
  HttpServletRequest getRequest();
}
