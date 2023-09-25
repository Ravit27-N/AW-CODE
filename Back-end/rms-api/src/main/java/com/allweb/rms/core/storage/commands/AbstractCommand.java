package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.SecurityConstraints;
import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageContext;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.Volume;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCommand implements StorageCommand {
  private static final String SEPARATOR = "_";

  @Getter private final ObjectMapper jacksonObjectMapper = new ObjectMapper();

  protected abstract ObjectNode execute(Storage storage, HttpServletRequest request);

  /**
   * Command must be implements this validation method to validate the request if the require
   * information is provided.
   *
   * <p>This method will be executed before the execution method to consider if the request is valid
   * fo invoke the command's execution method.
   *
   * @param request {@link HttpServletRequest}
   * @return {@link ValidationResult}
   * @see {@link StorageConstants}
   * @see {@link AbstractCommand#execute(Storage, HttpServletRequest)}
   * @see {@link ValidationResult}
   */
  protected abstract ValidationResult validate(HttpServletRequest request);

  @Override
  public ObjectNode execute(StorageContext storageContext) {
    // validate and get a result
    ValidationResult validationResult = this.validate(storageContext.getRequest());
    if (!validationResult.isValid()) {
      ObjectMapper mapper = new ObjectMapper();
      ObjectNode jsonResult = mapper.createObjectNode();
      jsonResult.set(
          "error", mapper.<ArrayNode>valueToTree(validationResult.getMessages().keySet()));
      return jsonResult;
    }
    HttpServletRequest request = storageContext.getRequest();
    Storage storage = storageContext.getStorage();
    log.debug("Executing a storage command...");
    // execute if valid or else return validation error result
    return this.execute(storage, request);
  }

  protected StorageObjectManager[] getTargetStorageObjectManagers(
      Storage storage, String[] targets) {
    StorageObject[] storageObjects = this.getTargetStorageObjects(storage, targets);
    if (storageObjects.length > 0) {
      return Arrays.stream(storageObjects)
          .map(StorageObject::getStorageObjectManager)
          .toArray(StorageObjectManager[]::new);
    }
    return new StorageObjectManager[0];
  }

  protected StorageObject[] getTargetStorageObjects(Storage storage, String[] targets) {
    if (targets != null && targets.length > 0) {
      return Arrays.stream(targets)
          .map(target -> getTargetStorageObject(storage, target))
          .toArray(StorageObject[]::new);
    }
    return new StorageObject[0];
  }

  protected StorageObjectManager getTargetStorageObjectManager(Storage storage, String target) {
    StorageObject targetStorageObject = getTargetStorageObject(storage, target);
    return targetStorageObject.getStorageObjectManager();
  }

  protected StorageObject getTargetStorageObject(Storage storage, String target) {
    String volumeId = this.extractVolumeId(target);
    String hashKey = this.extractHashKey(target);

    Volume fileSystemVolume = storage.getVolume(volumeId);
    return fileSystemVolume.getStorageObject(hashKey);
  }

  protected String extractVolumeId(String hashKey) {
    int volumeSeparatorIndex = hashKey.indexOf(SEPARATOR);
    if (volumeSeparatorIndex > -1) {
      return hashKey.substring(0, volumeSeparatorIndex);
    }
    return hashKey;
  }

  protected String extractHashKey(String hashKey) {
    int volumeSeparatorIndex = hashKey.indexOf(SEPARATOR);
    if (volumeSeparatorIndex > -1) {
      return hashKey.substring(volumeSeparatorIndex + 1);
    }
    return hashKey;
  }

  protected ArrayNode buildJsonArrayResponseObject(List<StorageObject> storageObjectList)
      throws IOException {
    ArrayNode result = this.getJacksonObjectMapper().createArrayNode();
    for (StorageObject storageObject : storageObjectList) {
      result.add(this.buildJsonResponseObject(storageObject));
    }
    return result;
  }

  protected ObjectNode buildJsonResponseObject(StorageObject targetStorageObject)
      throws IOException {
    ObjectNode targetInfo = this.getJacksonObjectMapper().createObjectNode();

    StorageObjectManager storageObjectManager = targetStorageObject.getStorageObjectManager();

    if (storageObjectManager.isRoot()) {
      targetInfo.put(Fields.NAME.toString(), targetStorageObject.getRootVolume().getAlias());
    } else {
      targetInfo.put(Fields.NAME.toString(), targetStorageObject.getName());
      targetInfo.put(
          Fields.PARENT_HASH.toString(), storageObjectManager.getParent().getHashKey(true));
    }

    targetInfo.put(
        Fields.HASH.toString(), targetStorageObject.getHashKey(true)); // include volume id

    targetInfo.put(Fields.TIMESTAMP.toString(), storageObjectManager.getLastModified());
    targetInfo.put(
        Fields.DIRECTORY_HAS_CHILD.toString(),
        storageObjectManager.isDirectory() && storageObjectManager.hasChild() ? 1 : 0);
    if (storageObjectManager.isDirectory()) {
      targetInfo.put(Fields.VOLUME_ID.toString(), targetStorageObject.getRootVolume().getId());
    }
    setMimeInfo(targetInfo, targetStorageObject);
    setSizeInfo(targetInfo, targetStorageObject);
    setSecurityConstraints(targetInfo, targetStorageObject);
    if (storageObjectManager.isRoot()) {
      setOptions(targetInfo);
    }
    return targetInfo;
  }

  private void setOptions(ObjectNode targetInfo) {
    targetInfo.set(Fields.OPTIONS.toString(), this.getJacksonObjectMapper().createObjectNode());
  }

  private void setSecurityConstraints(ObjectNode targetInfo, StorageObject targetStorageObject) {
    SecurityConstraints securityConstraints = targetStorageObject.getSecurityConstraints();
    targetInfo.put(Fields.READABLE.toString(), securityConstraints.isReadable() ? 1 : 0);
    targetInfo.put(Fields.WRITABLE.toString(), securityConstraints.isWritable() ? 1 : 0);
    targetInfo.put(Fields.IS_LOCKED.toString(), securityConstraints.isLocked() ? 1 : 0);
  }

  private void setMimeInfo(ObjectNode targetInfo, StorageObject targetStorageObject) {
    try {
      targetInfo.put(Fields.MIME.toString(), targetStorageObject.getMimeType());
    } catch (IOException e) {
      log.debug(e.getMessage(), e);
      targetInfo.put(Fields.UNKNOWN.toString(), 0L);
    }
  }

  private void setSizeInfo(ObjectNode targetInfo, StorageObject targetStorageObject) {
    try {
      targetInfo.put(
          Fields.SIZE.toString(), targetStorageObject.getStorageObjectManager().size(true));
    } catch (IOException e) {
      log.debug(e.getMessage(), e);
      targetInfo.put(Fields.SIZE.toString(), 0L);
    }
  }
}
