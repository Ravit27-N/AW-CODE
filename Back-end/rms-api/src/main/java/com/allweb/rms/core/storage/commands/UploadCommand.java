package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Slf4j
public class UploadCommand extends AbstractMultipartFileSystemStorageCommand {
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public ObjectNode execute(Storage storage, MultipartHttpServletRequest request) {
    ObjectNode result = mapper.createObjectNode();
    ArrayNode errors = mapper.createArrayNode();

    String target = request.getParameter(StorageConstants.Parameters.TARGET.toString());

    StorageObjectManager directoryManager = this.getTargetStorageObjectManager(storage, target);
    try {
      if (!directoryManager.exists()) {
        errors.add(StorageConstants.Errors.DIRECTORY_NOT_FOUND.getKey());
      } else if (!directoryManager.isDirectory()) {
        errors.add(StorageConstants.Errors.NOT_DIRECTORY.getKey());
      }
    } catch (IOException ioException) {
      log.debug(ioException.getMessage(), ioException);
    }

    if (errors.isEmpty()) {
      ArrayNode uploadedFileInfos =
          performUpload(directoryManager, request.getMultiFileMap(), errors);
      if (!errors.isEmpty()) {
        // Rollback uploaded files if any.
        boolean isRollbackSuccess = roleBackUploadedFiles(directoryManager, uploadedFileInfos);
        if (!isRollbackSuccess) {
          log.debug("Rollback performed with errors.");
        }
        result.set(StorageConstants.Errors.KEY.getKey(), errors);
        return result;
      }
      result.set(StorageConstants.Fields.ADDED.toString(), uploadedFileInfos);
    } else {
      result.set(StorageConstants.Errors.KEY.getKey(), errors);
    }

    return result;
  }

  @Override
  public ValidationResult validate(MultipartHttpServletRequest request) {
    ValidationResult validationResult = new ValidationResult();
    String target = request.getParameter(StorageConstants.Parameters.TARGET.toString());
    if (StringUtils.isBlank(target)) {
      validationResult.setValid(false);
      validationResult.addError(
          StorageConstants.Errors.INVALID_COMMAND_PARAMS.getKey(),
          StorageConstants.Errors.INVALID_COMMAND_PARAMS.getMessage(
              StorageConstants.Parameters.TARGET));
    }
    return validationResult;
  }

  private ArrayNode performUpload(
      StorageObjectManager directoryManager,
      MultiValueMap<String, MultipartFile> multiValueMap,
      ArrayNode errors) {
    ArrayNode uploadedFileInfos = mapper.createArrayNode();
    if (!multiValueMap.isEmpty()) {
      List<MultipartFile> files =
          multiValueMap.get(StorageConstants.Parameters.UPLOAD_FILES.toString());
      for (MultipartFile file : files) {
        ObjectNode uploadedFileInfo = this.upload(directoryManager, file, errors);
        if (!errors.isEmpty()) {
          break;
        }
        uploadedFileInfos.add(uploadedFileInfo);
      }
    } else {
      errors.add(StorageConstants.Errors.UPLOAD_NO_FILE.getKey());
    }

    return uploadedFileInfos;
  }

  private ObjectNode upload(
      StorageObjectManager directoryManager, MultipartFile file, ArrayNode errors) {
    try {
      // Create a file if not already exists, otherwise FileAlreadyExistsException
      // will be thrown.
      directoryManager.createFile(file.getOriginalFilename());
      StorageObject targetUploading = directoryManager.getChild(file.getOriginalFilename());
      // Perform file upload.
      try (OutputStream out = targetUploading.getStorageObjectManager().openOutputStream()) {
        try (InputStream in = file.getInputStream()) {
          IOUtils.copy(in, out);
          // Add uploaded file info into "uploadedFileInfos".
          return this.buildJsonResponseObject(targetUploading);
        }
      }
    } catch (FileAlreadyExistsException e) {
      errors.add(StorageConstants.Errors.ITEM_EXISTS.getKey());
      log.debug(e.getMessage(), e);
    } catch (IOException e) {
      errors.add(StorageConstants.Errors.UPLOAD_FILE.getKey());
      log.debug(e.getMessage(), e);
    }
    return null;
  }

  private boolean roleBackUploadedFiles(
      StorageObjectManager directoryManager, ArrayNode uploadedFiles) {
    final AtomicInteger roleBackFileCount = new AtomicInteger();
    if (!uploadedFiles.isEmpty()) {
      uploadedFiles.forEach(
          (JsonNode uploadedFile) -> {
            String uploadedFileName =
                uploadedFile.get(StorageConstants.Fields.NAME.toString()).asText();
            if (directoryManager.exists(uploadedFileName)) {
              try {
                directoryManager.remove(uploadedFileName, false);
                roleBackFileCount.getAndIncrement();
              } catch (IOException e) {
                log.error(e.getMessage(), e);
              }
            }
          });
    }
    return uploadedFiles.size() == roleBackFileCount.get();
  }
}
