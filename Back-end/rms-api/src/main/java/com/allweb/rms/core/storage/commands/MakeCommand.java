package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MakeCommand extends AbstractCommand {

  protected abstract void make(
      StorageObjectManager targetDirectoryManager, String targetName, ArrayNode errors);

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();

    String paramTarget = request.getParameter(StorageConstants.Parameters.TARGET.toString());
    String paramName = request.getParameter(StorageConstants.Parameters.NAME.toString());

    StorageObjectManager targetDirectoryManager =
        this.getTargetStorageObjectManager(storage, paramTarget);
    try {
      if (targetDirectoryManager.isDirectory() && !targetDirectoryManager.exists()) {
        // Folder must be exists before upload. Use "open" command to open a directory.
        // Use "mkDir" to create a directory.
        errors.add(StorageConstants.Errors.TARGET_FOLDER_NOT_FOUND.getKey());

      } else if (!targetDirectoryManager.isDirectory()) {
        errors.add(StorageConstants.Errors.INVALID_DIRECTORY_NAME.getKey());
      }
      if (errors.isEmpty()) {
        this.make(targetDirectoryManager, paramName, errors);
      }
      if (errors.isEmpty()) {
        ArrayNode info = this.getJacksonObjectMapper().createArrayNode();
        info.add(this.buildJsonResponseObject(targetDirectoryManager.getChild(paramName)));

        result.set(Fields.ADDED.toString(), info);
      }
    } catch (IOException ioException) {
      log.debug(ioException.getMessage(), ioException);
      errors.add(StorageConstants.Errors.UNKNOWN.getKey());
    }
    if (!errors.isEmpty()) {
      result.set(StorageConstants.Errors.KEY.getKey(), errors);
    }
    return result;
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    boolean isTargetParameterFound =
        request.getParameter(StorageConstants.Parameters.TARGET.toString()) != null;
    boolean isDirectoryNameParameterFound =
        request.getParameter(StorageConstants.Parameters.NAME.toString()) != null;
    if (!isTargetParameterFound || !isDirectoryNameParameterFound) {
      validationResult.setValid(false);
      validationResult.addError(StorageConstants.Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }
}
