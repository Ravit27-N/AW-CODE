package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

public abstract class AbstractMultipartFileSystemStorageCommand extends AbstractCommand {

  private static final Logger logger =
      LoggerFactory.getLogger(AbstractMultipartFileSystemStorageCommand.class);

  protected abstract ObjectNode execute(Storage storage, MultipartHttpServletRequest request);

  protected abstract ValidationResult validate(MultipartHttpServletRequest request);

  @Override
  public ObjectNode execute(Storage storage, HttpServletRequest request) {

    // validate and get a result
    ValidationResult validationResult = validate(request);
    if (!validationResult.isValid()) {
      ObjectMapper mapper = new ObjectMapper();
      ObjectNode jsonResult = mapper.createObjectNode();
      jsonResult.set(
          "error", mapper.<ArrayNode>valueToTree(validationResult.getMessages().keySet()));
      return jsonResult;
    }
    MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
    logger.debug("Executing a storage command...");
    // execute if valid or else return validation error result
    return this.execute(storage, multipartHttpServletRequest);
  }

  @Override
  public ValidationResult validate(HttpServletRequest request) {
    if (request instanceof MultipartRequest) {
      return this.validate((MultipartHttpServletRequest) request);
    }
    ValidationResult validationResult = new ValidationResult();
    validationResult.setValid(false);
    validationResult.addError(
        StorageConstants.Errors.UPLOAD_NO_FILE.getKey(),
        StorageConstants.Errors.UPLOAD_NO_FILE.getMessage());
    return validationResult;
  }
}
