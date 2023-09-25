package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageConstants.Parameters;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.Volume;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class OpenCommand extends AbstractCommand {
  boolean init = false;
  String paramTarget;

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();
    StorageObject targetStorageObject = null;
    boolean validTarget =
        StringUtils.isNotBlank(paramTarget)
            && this.getTargetStorageObjectManager(storage, paramTarget).exists();
    if (init) {
      targetStorageObject =
          validTarget
              ? this.getTargetStorageObject(storage, paramTarget)
              : storage.getDefaultVolume().getBaseStorageObject();
    } else if (validTarget) {
      targetStorageObject = this.getTargetStorageObject(storage, paramTarget);
    } else {
      errors.add(Errors.FILE_NOT_FOUND.getKey());
      result.set(Errors.KEY.getKey(), errors);
      return result;
    }
    try {
      result.set(
          Fields.CURRENT_WORKING_DIRECTORY.toString(),
          this.buildJsonResponseObject(targetStorageObject));
      List<StorageObject> childList = targetStorageObject.getStorageObjectManager().getChildren();
      childList.addAll(
          storage.getVolumes().stream()
              .map(Volume::getBaseStorageObject)
              .collect(Collectors.toList()));
      result.set(Fields.FILES.toString(), this.buildJsonArrayResponseObject(childList));
    } catch (IOException e) {
      log.debug(e.getMessage(), e);
    }
    result.put(Fields.API_VERSION.toString(), "2.1"); // Fix me:
    result.set(
        Fields.NET_DRIVERS.toString(),
        this.getJacksonObjectMapper().createArrayNode()); // Not supported yet.
    this.setRootOptions(result, targetStorageObject);
    return result;
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    String paramInit = request.getParameter(Parameters.INIT.toString());
    init = StringUtils.isNotBlank(paramInit) ? paramInit.trim().equals("1") : Boolean.FALSE;
    this.paramTarget = request.getParameter(Parameters.TARGET.toString());
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (!init && paramTarget == null) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }

  private void setRootOptions(ObjectNode targetInfo, StorageObject targetStorageObject) {
    ObjectNode options = this.getJacksonObjectMapper().createObjectNode();
    options.put(
        Fields.PATH.toString(),
        targetStorageObject.getStorageObjectManager().getPath().normalize().toString());
    options.put(Fields.SEPARATOR.toString(), File.separator);
    options.set(
        Fields.DISABLED.toString(), this.getJacksonObjectMapper().createArrayNode().add("zipdl"));
    options.put(Fields.COPY_OVER_WRITE.toString(), 1);
    options.set(Fields.ARCHIVERS.toString(), this.getJacksonObjectMapper().createArrayNode());
    options.put(Fields.UPLOAD_MAP_CONNECTION.toString(), "-1");
    targetInfo.set(Fields.OPTIONS.toString(), options);
  }
}
