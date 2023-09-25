package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.PathUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileCommandTest extends BaseCommand {
    private static final StorageObject FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file1.txt";
    private static final Path FILE_PATH = Paths.get("./test-volume/" + FILE_NAME);
    private static final String FILE_HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private static final String FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FILE_HASH_KEY;

    @BeforeAll
    static void setUp() throws IOException {
        Mockito.when(VOLUME.getStorageObject(FILE_HASH_KEY)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FILE_STORAGE_OBJECT_MANAGER);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getBaseStorageObject()).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT.getMimeType()).thenReturn("text/plain");
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(FILE_STORAGE_OBJECT_HASH_KEY);
    }

    @Test
    @Order(1)
    void testFailWhenTargetFileIsNotExists() {
        // stub
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.exists()).thenReturn(false);
        // execute
        ObjectNode result = new FileCommand().execute(getStorageContext());
        // expected
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.ITEM_EXISTS.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedJsonResult.toString(), result.toString());
    }


    @Test
    @Order(2)
    void testFailWhenTargetIsNotFile() throws IOException {
        // stub
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        // execute
        ObjectNode result = new FileCommand().execute(getStorageContext());
        // expected
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.NOT_FILE.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedJsonResult.toString(), result.toString());
    }

    @Test
    @Order(3)
    void testSuccessGetFileURI() throws IOException {
        URI fileURI = FILE_PATH.toUri();
        // stub
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getURI()).thenReturn(fileURI);
        // execute
        ObjectNode result = new FileCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(fileURI.toString(), result.get(StorageConstants.Fields.RESOURCE_FILE_URI.toString()).textValue());
    }
}
