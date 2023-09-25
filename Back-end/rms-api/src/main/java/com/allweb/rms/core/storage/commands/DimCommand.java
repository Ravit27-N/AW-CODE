package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants.Errors;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.core.storage.StorageConstants.Parameters;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DimCommand extends AbstractCommand {

  @Override
  protected ObjectNode execute(Storage storage, HttpServletRequest request) {
    ObjectNode result = this.getJacksonObjectMapper().createObjectNode();
    ArrayNode errors = this.getJacksonObjectMapper().createArrayNode();
    String paramTarget = request.getParameter(Parameters.TARGET.toString());
    StorageObjectManager targetStorageObjectManager =
        this.getTargetStorageObjectManager(storage, paramTarget);
    try {
      if (!targetStorageObjectManager.isFile()) {
        errors.add(Errors.NOT_FILE.getKey());
      } else if (!targetStorageObjectManager.exists()) {
        errors.add(Errors.FILE_NOT_FOUND.getKey());
      }
    } catch (IOException ioException) {
      errors.add(Errors.UNKNOWN.getKey());
      log.debug(ioException.getMessage(), ioException);
    }
    if (errors.isEmpty()) {
      try {
        BufferedImage image = ImageIO.read(targetStorageObjectManager.openInputStream());
        String dimensions = String.format("%dx%d", image.getWidth(), image.getHeight());
        result.put(Fields.DIMENSION.toString(), dimensions);
      } catch (Exception e) {
        errors.add(Errors.UNKNOWN.getKey());
        log.debug(e.getMessage(), e);
      }
    }
    if (!errors.isEmpty()) {
      result.set(Errors.KEY.getKey(), errors);
    }
    return result;
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
