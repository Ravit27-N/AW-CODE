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
import java.util.Collections;
import java.util.Date;
import java.util.function.Predicate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LsCommandTest extends BaseCommand {
    private static final StorageObject targetObject = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager targetObjectManager = Mockito.mock(StorageObjectManager.class);

    @BeforeAll
    static void init() {
        String folderHashKey = PathUtils.encodeHashKey(VOLUME_PATH.resolve("folder"));
        String targetStorageObjectHashKey = VOLUME_ID + "_" + folderHashKey;
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getStorageObject(folderHashKey)).thenReturn(targetObject);
        Mockito.when(targetObject.getStorageObjectManager()).thenReturn(targetObjectManager);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(targetStorageObjectHashKey);
    }

    @Test
    @Order(1)
    void testFailOnFolderNotExist() {
        // stub
        Mockito.when(targetObjectManager.exists()).thenReturn(false);
        // execute
        ObjectNode result = new LsCommand().execute(getStorageContext());
        // expected
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.DIRECTORY_NOT_FOUND.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedJsonResult.toString(), result.toString());
    }

    @Test
    @Order(4)
    void testFailOnTargetIsNotAFolder() throws IOException {
        // stub
        Mockito.when(targetObjectManager.exists()).thenReturn(true);
        Mockito.when(targetObjectManager.isDirectory()).thenReturn(false);
        // execute
        ObjectNode result = new LsCommand().execute(getStorageContext());
        // expected
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.NOT_DIRECTORY.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedJsonResult.toString(), result.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Order(3)
    void testFailOnGetChildrenListInFolder() throws IOException {
        // stub
        Mockito.when(targetObjectManager.exists()).thenReturn(true);
        Mockito.when(targetObjectManager.isDirectory()).thenReturn(true);
        Mockito.when(targetObjectManager.getChildren(Mockito.eq(1), Mockito.any(Predicate.class))).thenThrow(new IOException());
        // execute
        ObjectNode result = new LsCommand().execute(getStorageContext());
        // expected
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.UNKNOWN.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedJsonResult.toString(), result.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Order(2)
    void testSuccessOnGetChildrenListInFolder() throws IOException {
        String childFileName = "file.txt";
        String childStorageObjectHashKey = PathUtils.encodeHashKey(Paths.get("./test-volume/" + childFileName));
        String parentHashKey = PathUtils.encodeHashKey(Paths.get("./test-volume"));
        long lastModified = new Date().toInstant().toEpochMilli();
        String mimeType = "text/plain";
        long fileSize = 0L;
        SecurityConstraints securityConstraints = new SecurityConstraints();
        StorageObject childObject = Mockito.mock(StorageObject.class);
        StorageObjectManager childObjectManager = Mockito.mock(StorageObjectManager.class);
        // stub
        Mockito.when(targetObjectManager.exists()).thenReturn(true);
        Mockito.when(targetObjectManager.isDirectory()).thenReturn(true);
        Mockito.when(targetObjectManager.getChildren(Mockito.eq(1), Mockito.any(Predicate.class))).thenReturn(Collections.singletonList(childObject));
        Mockito.when(childObject.getStorageObjectManager()).thenReturn(childObjectManager);
        // stub for buildJsonResponseObject()
        Mockito.when(childObjectManager.isRoot()).thenReturn(false);
        Mockito.when(childObject.getName()).thenReturn(childFileName);
        Mockito.when(targetObject.getName()).thenReturn(childFileName);
        Mockito.when(childObjectManager.getParent()).thenReturn(targetObject);
        Mockito.when(targetObject.getHashKey(true)).thenReturn(parentHashKey);
        Mockito.when(childObject.getHashKey(true)).thenReturn(childStorageObjectHashKey);
        Mockito.when(childObjectManager.getLastModified()).thenReturn(lastModified);
        Mockito.when(childObjectManager.isDirectory()).thenReturn(false);
        Mockito.when(childObject.getMimeType()).thenReturn(mimeType);
        Mockito.when(childObjectManager.size(true)).thenReturn(fileSize);
        Mockito.when(childObject.getSecurityConstraints()).thenReturn(securityConstraints);
        // execute
        ObjectNode result = new LsCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.has(StorageConstants.Fields.LIST.toString()));
        JsonNode childListJson = result.get(StorageConstants.Fields.LIST.toString());
        Assertions.assertTrue(childListJson.isArray());
        JsonNode firstChildJson = childListJson.get(0);
        Assertions.assertEquals(childFileName, firstChildJson.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(childStorageObjectHashKey, firstChildJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(parentHashKey, firstChildJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(lastModified, firstChildJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(mimeType, firstChildJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }

}
