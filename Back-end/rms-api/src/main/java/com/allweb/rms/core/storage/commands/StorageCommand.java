package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.StorageContext;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface StorageCommand {

  /**
   * Execute and return a result in json format.
   *
   * @param storageContext
   */
  ObjectNode execute(StorageContext storageContext);
}
