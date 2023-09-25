package com.allweb.rms.service.storage;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface StorageService {

  /**
   * Execute a specific storage command like upload command, mkfile command and many more.
   *
   * @param storageCommand Command Text represent command to execute like upload or mkfile.
   * @param request {@link HttpServletRequest} object that may containing the multipart files.
   * @return Json {@link ObjectNode} of execution result.
   */
  ObjectNode execute(HttpServletRequest request) throws IOException;
}
