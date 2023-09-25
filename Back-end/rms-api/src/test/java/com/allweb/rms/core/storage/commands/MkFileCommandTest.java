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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MkFileCommandTest extends BaseCommand {
    private static final StorageObject TARGET_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager TARGET_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file.txt";

    @BeforeAll
    static void init() {
        String hashKey = PathUtils.encodeHashKey(VOLUME_PATH.resolve(FILE_NAME));
        String base64TargetString = VOLUME_ID + "_" + hashKey;
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getStorageObject(hashKey)).thenReturn(TARGET_OBJECT);
        Mockito.when(TARGET_OBJECT.getStorageObjectManager()).thenReturn(TARGET_OBJECT_MANAGER);

        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(base64TargetString);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.NAME.toString())).thenReturn(FILE_NAME);
    }

    @Test
    @Order(1)
    void testFailOnTargetIsNotADirectory() throws IOException {
        // stub
        Mockito.when(TARGET_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        // execute
        ObjectNode resultJson = new MkFileCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.INVALID_DIRECTORY_NAME.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    @Test
    @Order(2)
    void testFailOnTargetDirectoryNotExist() throws IOException {
        // stub
        Mockito.when(TARGET_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(TARGET_OBJECT_MANAGER.exists()).thenReturn(false);

        // execute
        ObjectNode resultJson = new MkFileCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.TARGET_FOLDER_NOT_FOUND.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    @Test
    @Order(4)
    void testFailOnUnableToCreateTargetFile() throws IOException {
        // stub
        Mockito.when(TARGET_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(TARGET_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(TARGET_OBJECT_MANAGER.createFile(FILE_NAME)).thenThrow(new IOException());
        // execute
        ObjectNode resultJson = new MkFileCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.MAKE_FILE.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    @Test
    @Order(3)
    void testSuccessOnCreateTargetFile() throws IOException {
        String childStorageObjectHashKey = PathUtils.encodeHashKey(Paths.get("./test-volume/" + FILE_NAME));
        String parentHashKey = PathUtils.encodeHashKey(Paths.get("./test-volume"));
        long lastModified = new Date().toInstant().toEpochMilli();
        String mimeType = "text/plain";
        long fileSize = 0L;
        SecurityConstraints securityConstraints = new SecurityConstraints();
        StorageObject childObject = Mockito.mock(StorageObject.class);
        StorageObjectManager childObjectManager = Mockito.mock(StorageObjectManager.class);
        // stub
        Mockito.when(TARGET_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(TARGET_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(TARGET_OBJECT_MANAGER.createFile(FILE_NAME)).thenReturn(childObject);
        Mockito.when(TARGET_OBJECT_MANAGER.getChild(FILE_NAME)).thenReturn(childObject);
        Mockito.when(childObject.getStorageObjectManager()).thenReturn(childObjectManager);
        // stub for buildJsonResponseObject()
        Mockito.when(childObjectManager.isRoot()).thenReturn(false);
        Mockito.when(childObject.getName()).thenReturn(FILE_NAME);
        Mockito.when(TARGET_OBJECT.getName()).thenReturn(FILE_NAME);
        Mockito.when(childObjectManager.getParent()).thenReturn(TARGET_OBJECT);
        Mockito.when(TARGET_OBJECT.getHashKey(true)).thenReturn(parentHashKey);
        Mockito.when(childObject.getHashKey(true)).thenReturn(childStorageObjectHashKey);
        Mockito.when(childObjectManager.getLastModified()).thenReturn(lastModified);
        Mockito.when(childObjectManager.isDirectory()).thenReturn(false);
        Mockito.when(childObject.getMimeType()).thenReturn(mimeType);
        Mockito.when(childObjectManager.size(true)).thenReturn(fileSize);
        Mockito.when(childObject.getSecurityConstraints()).thenReturn(securityConstraints);
        // execute
        ObjectNode resultJson = new MkFileCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertTrue(resultJson.has(StorageConstants.Fields.ADDED.toString()));
        JsonNode childListJson = resultJson.get(StorageConstants.Fields.ADDED.toString());
        Assertions.assertTrue(childListJson.isArray());
        JsonNode firstChildJson = childListJson.get(0);
        Assertions.assertEquals(FILE_NAME, firstChildJson.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(childStorageObjectHashKey, firstChildJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(parentHashKey, firstChildJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(lastModified, firstChildJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(mimeType, firstChildJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }

}
