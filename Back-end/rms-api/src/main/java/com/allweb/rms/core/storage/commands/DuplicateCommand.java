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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class DuplicateCommand extends AbstractCommand {

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode added = this.getJacksonObjectMapper().createArrayNode();
    String[] paramTargets = request.getParameterValues(Parameters.TARGETS.toString());
    StorageObject[] targetStorageObjects = this.getTargetStorageObjects(storage, paramTargets);
    for (StorageObject targetStorageObject : targetStorageObjects) {
      try {
        StorageObjectManager targetStorageObjectManager =
            targetStorageObject.getStorageObjectManager();
        StorageObjectManager parentStorageObjectManager =
            targetStorageObjectManager.getParent().getStorageObjectManager();

        String newName = this.generateNewName(targetStorageObject);

        StorageObject duplicated =
            targetStorageObjectManager.copyTo(parentStorageObjectManager.getChild(newName));

        added.add(this.buildJsonResponseObject(duplicated));
      } catch (IOException e) {
        log.debug(e.getMessage(), e);
      }
    }
    return result.set(Fields.ADDED.toString(), added);
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (request.getParameterValues(Parameters.TARGETS.toString()) == null) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }

  private String generateNewName(StorageObject targetDuplicating) {
    StorageObjectManager targetStorageObjectManager = targetDuplicating.getStorageObjectManager();
    StorageObject parent = targetStorageObjectManager.getParent();

    String name = targetStorageObjectManager.getPath().getFileName().toString();
    String baseName = FilenameUtils.getBaseName(name);
    String extension = FilenameUtils.getExtension(name);

    baseName = baseName.replaceAll("\\(\\d+\\)$", "");
    int i = 1;
    while (true) {
      String newName =
          String.format(
              "%s(%d)%s", baseName, i, (StringUtils.isEmpty(extension) ? "" : "." + extension));
      StorageObject newStorageObject = parent.getStorageObjectManager().getChild(newName);
      if (!newStorageObject.getStorageObjectManager().exists()) {
        return newName;
      }
      i++;
    }
  }
}
