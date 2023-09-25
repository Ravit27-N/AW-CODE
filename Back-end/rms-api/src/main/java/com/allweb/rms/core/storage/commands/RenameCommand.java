package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageConstants.Parameters;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RenameCommand extends AbstractCommand {
  private String paramTarget;
  private String paramName;

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();

    StorageObjectManager targetStorageObjectManager =
        this.getTargetStorageObjectManager(storage, paramTarget);

    try {
      StorageObject renamed = targetStorageObjectManager.rename(paramName);
      ObjectNode info = this.buildJsonResponseObject(renamed);
      result.set(Fields.ADDED.toString(), info);
      result.put(
          Fields.RENAMED.toString(),
          targetStorageObjectManager.getBaseStorageObject().getHashKey(true));
    } catch (FileAlreadyExistsException e) {
      log.debug(e.getMessage(), e);
      errors.add(Errors.ITEM_EXISTS.getKey());
    } catch (IOException e) {
      log.debug(e.getMessage(), e);
      errors.add(Errors.RENAME.getKey());
    }
    if (!errors.isEmpty()) {
      result.set(Errors.KEY.getKey(), errors);
    }
    return result;
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    paramTarget = request.getParameter(Parameters.TARGET.toString());
    paramName = request.getParameter(Parameters.NAME.toString());
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (paramTarget == null || paramName == null) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }
}
