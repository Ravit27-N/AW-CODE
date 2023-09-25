package com.allweb.rms.service.storage.filesystem;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageContext;
import com.allweb.rms.core.storage.StorageFactory;
import com.allweb.rms.core.storage.commands.StorageCommand;
import com.allweb.rms.core.storage.commands.StorageCommandFactory;
import com.allweb.rms.service.storage.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileSystemStorageService implements StorageService {
  private final ObjectMapper jsonObjectMapper;

  private final StorageFactory storageFactory;
  private final StorageCommandFactory commandFactory;

  public FileSystemStorageService(
      StorageFactory storageFactory, StorageCommandFactory commandFactory) {
    this.storageFactory = storageFactory;
    this.commandFactory = commandFactory;
    jsonObjectMapper = new ObjectMapper();
  }

  @Override
  public ObjectNode execute(HttpServletRequest request) throws IOException {
    String storageCommand = request.getParameter(StorageConstants.Parameters.COMMAND.toString());
    log.info("Executing command: {}", storageCommand);
    ObjectNode result = jsonObjectMapper.createObjectNode();
    StorageCommand storageCommandObject = commandFactory.getCommand(storageCommand);
    if (storageCommandObject != null) {
      StorageContext storageContext =
          new StorageContext() {

            @Override
            public HttpServletRequest getRequest() {
              return request;
            }

            @Override
            public Storage getStorage() {
              return storageFactory.getStorage();
            }
          };
      return storageCommandObject.execute(storageContext);
    }
    return result;
  }
}
