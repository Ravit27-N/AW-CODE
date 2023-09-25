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
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LsCommand extends AbstractCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(LsCommand.class);
  private String[] paramIntersect;

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();
    String paramTarget = request.getParameter(Parameters.TARGET.toString());
    paramIntersect = request.getParameterValues(Parameters.INTERSECT.toString());
    StorageObjectManager targetDirectoryManager =
        this.getTargetStorageObjectManager(storage, paramTarget);
    try {
      if (!targetDirectoryManager.exists()) {
        errors.add(Errors.DIRECTORY_NOT_FOUND.getKey());
      } else if (!targetDirectoryManager.isDirectory()) {
        errors.add(Errors.NOT_DIRECTORY.getKey());
      }
    } catch (IOException ioException) {
      LOGGER.debug(ioException.getMessage(), ioException);
      errors.add(Errors.UNKNOWN.getKey());
    }
    if (errors.isEmpty()) {
      try {
        List<StorageObject> childList = this.getChildList(targetDirectoryManager, paramIntersect);
        result.set(Fields.LIST.toString(), this.buildJsonArrayResponseObject(childList));
      } catch (IOException e) {
        LOGGER.debug(e.getMessage(), e);
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

  List<StorageObject> getChildList(StorageObjectManager targetDirectoryManager, String[] intersect)
      throws IOException {
    Supplier<Stream<String>> intersectStreamSupplier = () -> Arrays.stream(intersect);
    return targetDirectoryManager.getChildren(
        1,
        storageObject -> {
          boolean condition = true;
          if (paramIntersect != null && paramIntersect.length > 0) {
            condition =
                intersectStreamSupplier
                    .get()
                    .anyMatch(fileName -> fileName.contains(storageObject.getName()));
          }
          return condition;
        });
  }
}
