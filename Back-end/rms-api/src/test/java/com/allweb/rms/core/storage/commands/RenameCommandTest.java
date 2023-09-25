package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.SecurityConstraints;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.PathUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RenameCommandTest extends BaseCommand {
    private final String VOLUME_ID = "A";
    private final String FILE_NAME = "file.txt";
    private final Path FILE_PATH = VOLUME_PATH.resolve(FILE_NAME);
    private final String NEW_FILE_NAME = "file(1).txt";
    private final String HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private final String Target_Hash_KEY = VOLUME_ID + "_" + HASH_KEY;
    private StorageObject storageObject;
    private StorageObjectManager storageObjectManager;

    @Mock
    private StorageObject VOLUME_STORAGE_OBJECT;

    @BeforeEach
    void init() {
        storageObject = Mockito.mock(StorageObject.class);
        storageObjectManager = Mockito.mock(StorageObjectManager.class);
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getStorageObject(HASH_KEY)).thenReturn(storageObject);
        Mockito.when(storageObject.getStorageObjectManager()).thenReturn(storageObjectManager);
        //
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(Target_Hash_KEY);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.NAME.toString())).thenReturn(NEW_FILE_NAME);
    }

    @Test
    @Order(1)
    void testFailOnTargetExist() throws IOException {
        // stub
        Mockito.when(storageObjectManager.rename(NEW_FILE_NAME)).thenThrow(new FileAlreadyExistsException(NEW_FILE_NAME));
        // execute
        ObjectNode resultJson = new RenameCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.ITEM_EXISTS.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    @Test
    @Order(2)
    void testFailOnTargetIsNotFile() throws IOException {
        // stub
        Mockito.when(storageObjectManager.rename(NEW_FILE_NAME)).thenThrow(new IOException());
        // execute
        ObjectNode resultJson = new RenameCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.RENAME.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    @Test
    @Order(3)
    void testSuccessRenameFile() throws IOException {
        // stub
        Mockito.when(storageObjectManager.getBaseStorageObject()).thenReturn(storageObject);
        Mockito.when(storageObject.getHashKey(true)).thenReturn(Target_Hash_KEY);
        stubRenamedStorageObject();
        Mockito.when(storageObjectManager.rename(NEW_FILE_NAME)).thenReturn(renamedObject);
        // execute
        ObjectNode resultJson = new RenameCommand().execute(getStorageContext());
        // expect
        Assertions.assertNotNull(resultJson);
        Assertions.assertTrue(resultJson.has(StorageConstants.Fields.ADDED.toString()));
        Assertions.assertTrue(resultJson.has(StorageConstants.Fields.RENAMED.toString()));
        Assertions.assertEquals(Target_Hash_KEY, resultJson.get(StorageConstants.Fields.RENAMED.toString()).textValue());
        assertRenamedStorageObject(resultJson.get(StorageConstants.Fields.ADDED.toString()));
    }

    @Mock
    private StorageObject renamedObject;
    @Mock
    private StorageObjectManager renamedObjectManager;
    private final Path renamedPath = Paths.get("./test-volume/" + NEW_FILE_NAME);
    private final String renamedStorageObjectHashKey = VOLUME_ID + "_" + PathUtils.encodeHashKey(renamedPath);
    private final String renamedStorageObjectParentHashKey = VOLUME_ID + "_" + PathUtils.encodeHashKey(Paths.get("./test-volume/"));
    private final long renamedLastModified = new Date().toInstant().toEpochMilli();
    private final String renamedMimeType = "text/plain";

    private void stubRenamedStorageObject() throws IOException {
        long targetFileSize = 0L;
        Mockito.when(renamedObject.getStorageObjectManager()).thenReturn(renamedObjectManager);
        Mockito.when(renamedObjectManager.isRoot()).thenReturn(false);
        Mockito.when(renamedObject.getName()).thenReturn(NEW_FILE_NAME);
        Mockito.when(renamedObjectManager.getParent()).thenReturn(VOLUME_STORAGE_OBJECT);
        Mockito.when(VOLUME_STORAGE_OBJECT.getHashKey(true)).thenReturn(renamedStorageObjectParentHashKey);
        Mockito.when(renamedObject.getHashKey(true)).thenReturn(renamedStorageObjectHashKey);
        Mockito.when(renamedObjectManager.getLastModified()).thenReturn(renamedLastModified);
        Mockito.when(renamedObjectManager.isDirectory()).thenReturn(false);
        Mockito.when(renamedObject.getMimeType()).thenReturn(renamedMimeType);
        Mockito.when(renamedObjectManager.size(true)).thenReturn(targetFileSize);
        Mockito.when(renamedObject.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertRenamedStorageObject(JsonNode targetStorageObjectJson) {
        Assertions.assertNotNull(targetStorageObjectJson);
        Assertions.assertEquals(renamedStorageObjectHashKey, targetStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(renamedStorageObjectParentHashKey, targetStorageObjectJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(renamedLastModified, targetStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(renamedMimeType, targetStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }
}
