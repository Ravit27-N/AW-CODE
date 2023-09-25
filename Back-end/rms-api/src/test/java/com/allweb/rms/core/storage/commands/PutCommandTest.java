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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PutCommandTest extends BaseCommand {
    private static final StorageObject TARGET_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager TARGET_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file.txt";
    private static final Path FILE_PATH = VOLUME_PATH.resolve(FILE_NAME);
    private static final String TEST_CONTENT = "Test content.";
    private static final String UTF_8 = "utf-8";

    @BeforeAll
    static void init() {
        String hashKey = PathUtils.encodeHashKey(FILE_PATH);
        String targetStorageObjectHashKey = VOLUME_ID + "_" + hashKey;
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getStorageObject(hashKey)).thenReturn(TARGET_OBJECT);
        Mockito.when(TARGET_OBJECT.getStorageObjectManager()).thenReturn(TARGET_OBJECT_MANAGER);
        Mockito.when(TARGET_OBJECT_MANAGER.getBaseStorageObject()).thenReturn(TARGET_OBJECT);
        //
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(targetStorageObjectHashKey);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.CONTENT.toString())).thenReturn(TEST_CONTENT);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.ENCODING.toString())).thenReturn(UTF_8);
    }

    @Test
    @Order(1)
    void testFailOnTargetFileNotExist() {
        // stub
        Mockito.when(TARGET_OBJECT_MANAGER.exists()).thenReturn(false);

        // execute
        ObjectNode resultJson = new PutCommand().execute(getStorageContext());
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
        Mockito.when(TARGET_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(TARGET_OBJECT_MANAGER.isFile()).thenReturn(false);
        // execute
        ObjectNode resultJson = new PutCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.NOT_FILE.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    @Test
    @Order(3)
    void testSuccessWriteContentIntoFile() throws IOException {
        //
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // stub
        Mockito.when(TARGET_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(TARGET_OBJECT_MANAGER.isFile()).thenReturn(true);

        Mockito.when(TARGET_OBJECT_MANAGER.openOutputStream()).thenReturn(outStream);
        stubTargetStorageObject();
        // execute
        ObjectNode resultJson = new PutCommand().execute(getStorageContext());
        // expect
        Assertions.assertNotNull(resultJson);
        Assertions.assertTrue(resultJson.has(StorageConstants.Fields.CHANGED.toString()));
        JsonNode childListJson = resultJson.get(StorageConstants.Fields.CHANGED.toString());
        Assertions.assertTrue(childListJson.isArray());
        assertTargetStorageObject(childListJson.get(0));
    }

    @Test
    @Order(4)
    void testFailWhenWriteContentIntoFile() throws IOException {
        //
        ByteArrayOutputStream outStream = Mockito.mock(ByteArrayOutputStream.class);
        // stub
        Mockito.when(TARGET_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(TARGET_OBJECT_MANAGER.isFile()).thenReturn(true);

        Mockito.when(TARGET_OBJECT_MANAGER.openOutputStream()).thenReturn(outStream);
        Mockito.doThrow(new IOException()).when(outStream).write(Mockito.any(byte[].class));
        // execute
        ObjectNode resultJson = new PutCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.WRITE.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    private final Path targetPath = Paths.get("./test-volume/" + FILE_NAME);
    private final String targetStorageObjectHashKey = VOLUME_ID + "_" + PathUtils.encodeHashKey(targetPath);
    private final String targetParentHashKey = VOLUME_ID + "_" + PathUtils.encodeHashKey(targetPath);
    private final long targetLastModified = new Date().toInstant().toEpochMilli();
    private final String targetMimeType = "text/plain";

    private void stubTargetStorageObject() throws IOException {
        long targetFileSize = 0L;
        Mockito.when(TARGET_OBJECT_MANAGER.isRoot()).thenReturn(false);
        Mockito.when(TARGET_OBJECT.getName()).thenReturn(FILE_NAME);
        Mockito.when(TARGET_OBJECT_MANAGER.getParent()).thenReturn(TARGET_OBJECT);
        Mockito.when(TARGET_OBJECT.getHashKey(true)).thenReturn(targetParentHashKey);
        Mockito.when(TARGET_OBJECT.getHashKey(true)).thenReturn(targetStorageObjectHashKey);
        Mockito.when(TARGET_OBJECT_MANAGER.getLastModified()).thenReturn(targetLastModified);
        Mockito.when(TARGET_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        Mockito.when(TARGET_OBJECT.getMimeType()).thenReturn(targetMimeType);
        Mockito.when(TARGET_OBJECT_MANAGER.size(true)).thenReturn(targetFileSize);
        Mockito.when(TARGET_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertTargetStorageObject(JsonNode targetStorageObjectJson) {
        Assertions.assertNotNull(targetStorageObjectJson);
        Assertions.assertEquals(targetStorageObjectHashKey, targetStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(targetParentHashKey, targetStorageObjectJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(targetLastModified, targetStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(targetMimeType, targetStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }

}
