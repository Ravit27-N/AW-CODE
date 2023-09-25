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
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;

@Slf4j
public class GetCommand extends AbstractCommand {
  private final Tika tika = new Tika();

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();
    String paramTarget = request.getParameter(Parameters.TARGET.toString());
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
      try {
        String content = this.getFileContent(targetStorageObjectManager);
        result.put(Fields.CONTENT.toString(), content);
      } catch (IOException e) {
        log.debug(e.getMessage(), e);
        errors.add(Errors.READ.getKey());
      }
    }
    if (!errors.isEmpty()) {
      result.set(Errors.KEY.getKey(), errors);
    }
    return result;
  }

  private String getFileContent(StorageObjectManager targetFile) throws IOException {
    String fileType = tika.detect(targetFile.openInputStream());
    if (fileType.equals(MediaType.TEXT_PLAIN.toString())) {
      return IOUtils.toString(targetFile.openInputStream(), StandardCharsets.UTF_8);
    } else {
      byte[] raw = IOUtils.toByteArray(targetFile.openInputStream());
      byte[] encodedRaw = Base64.encodeBase64(raw);
      String base64Encoded = new String(encodedRaw);
      return String.format("data:%s;base64,%s", fileType, base64Encoded);
    }
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
}
