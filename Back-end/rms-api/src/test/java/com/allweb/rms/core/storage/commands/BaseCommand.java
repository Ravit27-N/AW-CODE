package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageContext;
import com.allweb.rms.core.storage.Volume;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
@Getter
public abstract class BaseCommand {
    protected static final String VOLUME_ID = "A";
    protected static final Path VOLUME_PATH = Paths.get("./test-volume");
    protected static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();
    protected static final HttpServletRequest HTTP_SERVLET_REQUEST = Mockito.mock(HttpServletRequest.class);
    protected static final Storage STORAGE = Mockito.mock(Storage.class);
    private static final StorageContext STORAGE_CONTEXT_INSTANCE;
    protected static final Volume VOLUME = Mockito.mock(Volume.class);

    static {
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        STORAGE_CONTEXT_INSTANCE = new StorageContext() {
            @Override
            public Storage getStorage() {
                return STORAGE;
            }

            @Override
            public HttpServletRequest getRequest() {
                return HTTP_SERVLET_REQUEST;
            }
        };
    }

    protected static StorageContext getStorageContext() {
        return STORAGE_CONTEXT_INSTANCE;
    }
}
