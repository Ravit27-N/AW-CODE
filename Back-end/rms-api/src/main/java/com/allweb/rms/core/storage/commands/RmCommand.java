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
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

@Slf4j
public class RmCommand extends AbstractCommand {
  private String[] paramTargets;

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();

    StorageObjectManager[] storageObjectManagers =
        this.getTargetStorageObjectManagers(storage, paramTargets);

    ArrayNode removed = this.getJacksonObjectMapper().createArrayNode();
    Arrays.stream(storageObjectManagers)
        .forEach(
            storageObjectManager -> {
              try {
                storageObjectManager.remove();
                removed.add(storageObjectManager.getBaseStorageObject().getHashKey(true));
              } catch (IOException e) {
                errors.add(Errors.REMOVED.getKey());
                errors.add(storageObjectManager.getBaseStorageObject().getName());
                log.debug(Errors.REMOVED.getMessage(storageObjectManager.getPath()), e);
              }
            });
    result.set(Fields.REMOVED.toString(), removed);
    if (!errors.isEmpty()) {
      result.set(Errors.KEY.getKey(), errors);
    }
    return result;
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    paramTargets = request.getParameterValues(Parameters.TARGETS.toString());
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (ArrayUtils.isEmpty(paramTargets)) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }
}
