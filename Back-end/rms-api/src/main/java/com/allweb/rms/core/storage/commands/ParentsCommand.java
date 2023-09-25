package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageConstants.Parameters;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.utils.ThrowablePredicateAdapter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParentsCommand extends AbstractCommand {

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
      } else if (!targetStorageObjectManager.isDirectory()) {
        errors.add(Errors.NOT_DIRECTORY.getKey());
      }
    } catch (IOException ioException) {
      log.debug(ioException.getMessage(), ioException);
      errors.add(Errors.UNKNOWN.getKey());
    }
    if (errors.isEmpty()) {
      int targetParentDirectoryLevel = targetStorageObjectManager.getPath().getNameCount();
      StorageObject rootStorageObject = targetStorageObjectManager.getRoot();
      StorageObjectManager rootStorageObjectManager = rootStorageObject.getStorageObjectManager();
      try {
        List<StorageObject> parentStorageObjectList =
            rootStorageObjectManager.getChildren(
                targetParentDirectoryLevel,
                ThrowablePredicateAdapter.createThrowablePredicate(
                    storageObject -> storageObject.getStorageObjectManager().isDirectory()));
        result.set(
            Fields.TREE.toString(), this.buildJsonArrayResponseObject(parentStorageObjectList));
      } catch (IOException ex) {
        log.debug(ex.getMessage(), ex);
        errors.add(Errors.UNKNOWN.getKey());
      }
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
