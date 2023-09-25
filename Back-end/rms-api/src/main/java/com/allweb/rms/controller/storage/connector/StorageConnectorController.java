package com.allweb.rms.controller.storage.connector;

import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageConstants.Fields;
import com.allweb.rms.service.storage.StorageService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/connector")
public class StorageConnectorController {
  private static final Logger LOGGER = LoggerFactory.getLogger(StorageConnectorController.class);
  private final StorageService storageService;

  public StorageConnectorController(StorageService storageService) {
    this.storageService = storageService;
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> connect(
      HttpServletRequest request) { // HttpServletResponse response
    try {
      ObjectNode result = storageService.execute(request);
      return ResponseEntity.status(HttpStatus.OK).body(result);

    } catch (IOException e) {
      LOGGER.debug(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping(produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
  public ResponseEntity<Object> getResource(
      HttpServletRequest request) { // HttpServletResponse response
    try {
      ObjectNode result = storageService.execute(request);
      String downloadParam = request.getParameter(StorageConstants.Parameters.DOWNLOAD.toString());
      boolean forceDownload = StringUtils.isNotBlank(downloadParam) && downloadParam.equals("1");
      URI uri = new URI(result.get(Fields.RESOURCE_FILE_URI.toString()).asText());
      String mimeType = result.get(Fields.MIME.toString()).asText();
      return responseAsResource(uri, mimeType, forceDownload);
    } catch (IOException | URISyntaxException e) {
      LOGGER.debug(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  public ResponseEntity<Object> responseAsResource(
      URI fileURI, String mimeType, boolean forceDownload) throws IOException {
    Path filePath = Paths.get(fileURI);
    InputStream input = Files.newInputStream(filePath);
    Resource resource = new InputStreamResource(input);

    HttpHeaders headers = new HttpHeaders();
    if (forceDownload) {
      headers.add(
          HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + filePath.getFileName() + "\"");
    } else {
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");
    }
    headers.setContentType(MediaType.parseMediaType(mimeType));
    return ResponseEntity.status(HttpStatus.OK)
        .headers(headers)
        .contentLength(filePath.toFile().length())
        .body(resource);
  }
}
