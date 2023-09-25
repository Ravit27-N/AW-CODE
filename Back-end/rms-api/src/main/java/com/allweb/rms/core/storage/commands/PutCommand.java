package com.allweb.rms.core.storage.commands;

import static com.allweb.rms.core.storage.StorageConstants.CHARSET_UTF8;
import static com.allweb.rms.core.storage.StorageConstants.ENCODING_UTF8;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageConstants.Parameters;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

@Slf4j
public class PutCommand extends AbstractCommand {

  private String paramTarget;
  private String paramContent;
  private String paramEncoding;

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();
    StorageObjectManager targetStorageObjectManager =
        this.getTargetStorageObjectManager(storage, paramTarget);
    try {
      if (!targetStorageObjectManager.exists()) {
        errors.add(Errors.ITEM_EXISTS.getKey());
      } else if (!targetStorageObjectManager.isFile()) {
        errors.add(Errors.NOT_FILE.getKey());
      }
    } catch (IOException ioException) {
      errors.add(Errors.UNKNOWN.getKey());
    }
    if (errors.isEmpty()) {
      this.put(targetStorageObjectManager, paramContent, result, errors);
    }
    if (!errors.isEmpty()) {
      result.set(Errors.KEY.getKey(), errors);
    }
    return result;
  }

  private void put(
      StorageObjectManager targetStorageObjectManager,
      String content,
      ObjectNode result,
      ArrayNode errors) {
    ArrayNode changed = this.getJacksonObjectMapper().createArrayNode();
    try (OutputStream outputStream = targetStorageObjectManager.openOutputStream()) {
      if (paramEncoding.equalsIgnoreCase(ENCODING_UTF8) || paramEncoding.isEmpty()) { // text data
        IOUtils.write(content, outputStream, CHARSET_UTF8);
        changed.add(
            this.buildJsonResponseObject(targetStorageObjectManager.getBaseStorageObject()));
        result.set(Fields.CHANGED.toString(), changed);
      } // for other encoding not implemented
    } catch (IOException e) {
      log.debug(e.getMessage(), e);
      errors.add(Errors.WRITE.getKey());
    }
  }

  @Override
  protected ValidationResult validate(HttpServletRequest request) {
    paramTarget = request.getParameter(Parameters.TARGET.toString());
    paramContent = request.getParameter(Parameters.CONTENT.toString());
    paramEncoding = request.getParameter(Parameters.ENCODING.toString());
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(true);
    if (paramTarget == null || paramContent == null || paramEncoding == null) {
      validationResult.setValid(false);
      validationResult.addError(Errors.INVALID_COMMAND_PARAMS);
    }
    return validationResult;
  }
}
