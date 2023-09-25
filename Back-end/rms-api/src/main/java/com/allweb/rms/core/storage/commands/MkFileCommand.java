package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MkFileCommand extends MakeCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(MkFileCommand.class);

  @Override
  protected void make(
      StorageObjectManager targetDirectoryManager, String targetName, ArrayNode errors) {
    try {
      targetDirectoryManager.createFile(targetName);
    } catch (FileAlreadyExistsException e) {
      LOGGER.debug(StorageConstants.Errors.ITEM_EXISTS.getMessage(), targetName);
      errors.add(StorageConstants.Errors.ITEM_EXISTS.getKey());
    } catch (IOException e) {
      LOGGER.debug(e.getMessage(), e);
      errors.add(Errors.MAKE_FILE.getKey());
    }
  }
}
