package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageConstants.Parameters;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SizeCommand extends AbstractCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(SizeCommand.class);

  String[] paramTargets;

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();

    StorageObjectManager[] targetStorageObjectManagers =
        this.getTargetStorageObjectManagers(storage, paramTargets);
    SizeInfo sizeInfo = this.calculateSize(targetStorageObjectManagers);

    result.put(Fields.DIRECTORY_COUNT.toString(), sizeInfo.totalDirectoryCount.get());
    result.put(Fields.FILE_COUNT.toString(), sizeInfo.totalFileCount.get());
    result.put(Fields.SIZE.toString(), sizeInfo.totalSize.get());
    if (sizeInfo.totalDirectoryCount.get() + sizeInfo.totalFileCount.get() > 0) {
      result.set(Fields.SIZES.toString(), sizeInfo.toJsonObjectNode());
    } else {
      result.set(Fields.SIZES.toString(), this.getJacksonObjectMapper().createObjectNode());
    }

    return result;
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    paramTargets = request.getParameterValues(Parameters.TARGETS.toString());
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (paramTargets == null || paramTargets.length == 0) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }

  private SizeInfo calculateSize(StorageObjectManager[] targetStorageObjectManagers) {
    SizeInfo sizeController = new SizeInfo();
    Arrays.stream(targetStorageObjectManagers)
        .parallel()
        .forEach(
            targetStorageObjectManager -> {
              if (targetStorageObjectManager.exists()) {
                AtomicInteger directoryCount = new AtomicInteger(0);
                AtomicInteger fileCount = new AtomicInteger(0);
                AtomicLong s = new AtomicLong(0L);
                List<StorageObjectManager> storageObjectManagerList = new ArrayList<>(1);
                storageObjectManagerList.add(targetStorageObjectManager);
                size(storageObjectManagerList, directoryCount, fileCount, s);
                sizeController.addSizeNode(
                    targetStorageObjectManager.getBaseStorageObject().getHashKey(true),
                    directoryCount.get(),
                    fileCount.get(),
                    s.get());
              }
            });
    return sizeController;
  }

  private void size(
      List<StorageObjectManager> storageObjectManagerList,
      AtomicInteger totalDirectory,
      AtomicInteger totalFile,
      AtomicLong totalSize) {
    storageObjectManagerList.forEach(
        storageObjectManager -> {
          try {
            if (storageObjectManager.isFile()) {
              totalSize.set(totalSize.get() + storageObjectManager.size(false));
              totalFile.incrementAndGet();
            } else {
              totalDirectory.incrementAndGet();
              size(
                  storageObjectManager.getChildren().stream()
                      .map(StorageObject::getStorageObjectManager)
                      .collect(Collectors.toList()),
                  totalDirectory,
                  totalFile,
                  totalSize);
            }
          } catch (IOException e) {
            LOGGER.debug(e.getMessage(), e);
          }
        });
  }

  private class SizeInfo {

    final ObjectNode jsonObjectNode;
    @Getter final AtomicInteger totalDirectoryCount = new AtomicInteger(0);
    @Getter final AtomicInteger totalFileCount = new AtomicInteger(0);
    @Getter final AtomicLong totalSize = new AtomicLong(0L);

    public SizeInfo() {
      this.jsonObjectNode = getJacksonObjectMapper().createObjectNode();
    }

    void addSizeNode(String hashKey, int directoryCount, int fileCount, long totalSize) {
      ObjectNode res = getJacksonObjectMapper().createObjectNode();
      res.put(Fields.DIRECTORY_COUNT.toString(), directoryCount);
      res.put(Fields.FILE_COUNT.toString(), fileCount);
      res.put(Fields.SIZE.toString(), totalSize);
      jsonObjectNode.set(hashKey, res);
      //
      totalDirectoryCount.set(totalDirectoryCount.get() + directoryCount);
      totalFileCount.set(totalFileCount.get() + fileCount);
      this.totalSize.set(this.totalSize.get() + totalSize);
    }

    public ObjectNode toJsonObjectNode() {
      return jsonObjectNode;
    }
  }
}
