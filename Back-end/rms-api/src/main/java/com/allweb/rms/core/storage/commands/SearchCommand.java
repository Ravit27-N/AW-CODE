package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.SearchOption;
import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageConstants.Parameters;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.Volume;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SearchCommand extends AbstractCommand {
  private String paramSearchString;

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();
    StorageObject targetStorageObject;

    String paramTarget = request.getParameter(Parameters.TARGET.toString());
    String[] paramMimes = request.getParameterValues(Parameters.MIMES.toString());

    List<StorageObject> searchList = new ArrayList<>();
    if (StringUtils.isNotEmpty(paramTarget)) {
      targetStorageObject = this.getTargetStorageObject(storage, paramTarget);
      searchList.addAll(
          this.searchOnTarget(targetStorageObject, paramSearchString, paramMimes, errors));
    } else {
      List<Volume> volumes = storage.getVolumes();
      volumes.forEach(
          volume -> {
            StorageObject volumeStorageObject = volume.getBaseStorageObject();
            searchList.addAll(this.search(volumeStorageObject, paramSearchString, paramMimes));
          });
    }
    if (errors.isEmpty()) {
      ArrayNode files = null;
      try {
        files = this.buildJsonArrayResponseObject(searchList);
        result.set(Fields.FILES.toString(), files);
      } catch (IOException ioException) {
        log.debug(ioException.getMessage(), ioException);
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
    paramSearchString = request.getParameter(Parameters.SEARCH_STRING.toString());
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (paramSearchString == null) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }

  private List<StorageObject> searchOnTarget(
      StorageObject targetStorageObject,
      String searchString,
      String[] mimeTypes,
      ArrayNode errors) {
    StorageObjectManager targetStorageObjectManager = targetStorageObject.getStorageObjectManager();
    List<StorageObject> searchList = new ArrayList<>();
    try {
      if (!targetStorageObjectManager.isDirectory()) {
        errors.add(Errors.NOT_DIRECTORY.getKey());
      } else if (!targetStorageObjectManager.exists()) {
        errors.add(Errors.DIRECTORY_NOT_FOUND.getKey());
      } else {
        searchList.addAll(this.search(targetStorageObject, searchString, mimeTypes));
      }
    } catch (IOException ioException) {
      log.debug(ioException.getMessage(), ioException);
      errors.add(Errors.UNKNOWN.getKey());
    }
    return searchList;
  }

  private List<StorageObject> search(
      StorageObject targetSearching, String searchString, String[] mimes) {
    StorageObjectManager targetSearchingStorageObjectManager =
        targetSearching.getStorageObjectManager();
    List<StorageObject> searchList = new ArrayList<>();
    List<String> filterMimes = new ArrayList<>();
    if (ArrayUtils.isNotEmpty(mimes)) {
      filterMimes.addAll(Arrays.asList(mimes));
    }

    if (filterMimes.isEmpty()) {
      searchList.addAll(
          targetSearchingStorageObjectManager.search(searchString, SearchOption.RELEVANT));
    } else {
      searchList.addAll(
          targetSearchingStorageObjectManager.search(
              path -> {
                try {
                  log.debug(Files.probeContentType(path));
                  return FilenameUtils.getBaseName(path.getFileName().toString())
                          .contains(searchString)
                      && (Files.isRegularFile(path)
                          ? filterMimes.contains(Files.probeContentType(path))
                          : filterMimes.contains(StorageConstants.MIME_TYPE_DIRECTORY));

                } catch (IOException e) {
                  log.debug(e.getMessage(), e);
                  return false;
                }
              }));
    }

    return searchList;
  }
}
