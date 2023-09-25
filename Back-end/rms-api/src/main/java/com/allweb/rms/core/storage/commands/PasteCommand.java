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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

@Slf4j
public class PasteCommand extends AbstractCommand {
  private static final String DEFAULT_SUFFIX = "~";

  private String[] paramTargets;

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode added = this.getJacksonObjectMapper().createArrayNode();
    ArrayNode removed = this.getJacksonObjectMapper().createArrayNode();
    String paramDestination = request.getParameter(Parameters.DESTINATION.toString());
    paramTargets = request.getParameterValues(Parameters.TARGETS.toString());
    boolean paramIsCut =
        ServletRequestUtils.getIntParameter(request, Parameters.IS_CUT.toString(), 0) == 1;
    String[] paramRenames = request.getParameterValues(Parameters.RENAMES.toString());
    String suffix =
        ServletRequestUtils.getStringParameter(
            request, Parameters.SUFFIX.toString(), DEFAULT_SUFFIX);

    this.performCopyPast(
        storage, paramDestination, paramRenames, suffix, paramIsCut, added, removed);

    result.set(Fields.ADDED.toString(), added);
    result.set(Fields.REMOVED.toString(), removed);
    return result;
  }

  private void performCopyPast(
      Storage storage,
      String destination,
      String[] paramRenames,
      String suffix,
      boolean isCut,
      ArrayNode added,
      ArrayNode removed) {
    StorageObjectManager targetStorageObjectManager =
        this.getTargetStorageObjectManager(storage, destination);
    for (int i = 0; i < paramTargets.length; i++) {
      StorageObject copyingStorageObject = this.getTargetStorageObject(storage, paramTargets[i]);
      String renameTo = ArrayUtils.isNotEmpty(paramRenames) ? paramRenames[i] : "";
      StorageObject destinationCopyingStorageObject =
          this.buildDestinationCopyingObject(
              targetStorageObjectManager, copyingStorageObject, renameTo, suffix);
      this.copyPast(destinationCopyingStorageObject, copyingStorageObject, isCut, added, removed);
    }
  }

  private void copyPast(
      StorageObject destinationCopyingStorageObject,
      StorageObject copyingStorageObject,
      boolean isCut,
      ArrayNode added,
      ArrayNode removed) {
    try {
      StorageObjectManager targetCopyingStorageObjectManager =
          copyingStorageObject.getStorageObjectManager();
      StorageObject copied =
          targetCopyingStorageObjectManager.copyTo(destinationCopyingStorageObject);
      added.add(this.buildJsonResponseObject(copied));
      if (isCut) {
        targetCopyingStorageObjectManager.remove();
        removed.add(copyingStorageObject.getHashKey(true));
      }
    } catch (IOException e) {
      log.debug(e.getMessage(), e);
    }
  }

  private StorageObject buildDestinationCopyingObject(
      StorageObjectManager destinationStorageObjectManager,
      StorageObject copyingStorageObject,
      String rename,
      String suffix) {
    if (StringUtils.isNotEmpty(rename)) {
      String extension = FilenameUtils.getExtension(rename);
      extension =
          StringUtils.isNotEmpty(extension)
              ? extension
              : FilenameUtils.getExtension(copyingStorageObject.getName());
      String newName =
          String.format("%s%s.%s", suffix, FilenameUtils.getBaseName(rename), extension);
      return destinationStorageObjectManager.getChild(newName);
    } else {
      return destinationStorageObjectManager.getChild(copyingStorageObject.getName());
    }
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (request.getParameter(Parameters.DESTINATION.toString()) == null) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }
}
