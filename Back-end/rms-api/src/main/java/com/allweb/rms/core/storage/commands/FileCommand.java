package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageConstants.Parameters;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileCommand extends AbstractCommand {

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();
    String paramTarget = request.getParameter(Parameters.TARGET.toString());
    StorageObjectManager targetStorageObjectManager =
        this.getTargetStorageObjectManager(storage, paramTarget);
    try {
      if (!targetStorageObjectManager.exists()) {
        errors.add(Errors.ITEM_EXISTS.getKey());
      } else if (targetStorageObjectManager.isDirectory()) {
        errors.add(Errors.NOT_FILE.getKey());
      } else {
        result.put(
            Fields.RESOURCE_FILE_URI.toString(), targetStorageObjectManager.getURI().toString());
        result.put(
            Fields.MIME.toString(),
            targetStorageObjectManager.getBaseStorageObject().getMimeType());
      }
    } catch (IOException ioException) {
      log.debug(ioException.getMessage(), ioException);
      errors.add(Errors.UNKNOWN.getKey());
    }
    if (!errors.isEmpty()) {
      result.set(Errors.KEY.getKey(), errors);
    }
    return result;
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (request.getParameter(Parameters.TARGET.toString()) == null) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }
}
