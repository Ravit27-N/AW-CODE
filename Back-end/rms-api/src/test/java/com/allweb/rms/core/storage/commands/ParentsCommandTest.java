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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ParentsCommandTest extends BaseCommand {
    private static final StorageObject TARGET_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FOLDER_NAME = "folder";

    @BeforeAll
    static void init() {
        String hashKey = PathUtils.encodeHashKey(VOLUME_PATH.resolve(FOLDER_NAME)); // test-volume/folder
        String targetStorageObjectHashKey = VOLUME_ID + "_" + hashKey;
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getId()).thenReturn(VOLUME_ID);
        Mockito.when(VOLUME.getAlias()).thenReturn(VOLUME_ID);
        Mockito.when(VOLUME.getStorageObject(hashKey)).thenReturn(TARGET_OBJECT);
        Mockito.when(TARGET_OBJECT.getStorageObjectManager()).thenReturn(STORAGE_OBJECT_MANAGER);

        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(targetStorageObjectHashKey);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.NAME.toString())).thenReturn(FOLDER_NAME);
    }

    @Test
    @Order(1)
    void testFailOnTargetDirectoryNotExist() throws IOException {
        // stub
        Mockito.when(STORAGE_OBJECT_MANAGER.exists()).thenReturn(false);
        // execute
        ObjectNode resultJson = new ParentsCommand().execute(getStorageContext());
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
    void testFailOnTargetIsNotADirectory() throws IOException {
        // stub
        Mockito.when(STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        // execute
        ObjectNode resultJson = new ParentsCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.NOT_DIRECTORY.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Order(3)
    void testSuccessGetParents() throws IOException {
        Mockito.when(STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(STORAGE_OBJECT_MANAGER.getPath()).thenReturn(targetPath);
        Mockito.when(STORAGE_OBJECT_MANAGER.getRoot()).thenReturn(rootStorageObject);
        Mockito.when(rootStorageObject.getStorageObjectManager()).thenReturn(rootStorageObjectManager);
        Mockito.when(rootStorageObjectManager.getChildren(Mockito.eq(targetPath.getNameCount()), Mockito.any(Predicate.class))).thenReturn(Arrays.asList(rootStorageObject, TARGET_OBJECT));
        // stub for buildJsonResponseObject()
        this.stubRootStorageObject();
        this.stubTargetStorageObject();
        // execute
        ObjectNode resultJson = new ParentsCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertTrue(resultJson.has(StorageConstants.Fields.TREE.toString()));
        JsonNode childListJson = resultJson.get(StorageConstants.Fields.TREE.toString());
        Assertions.assertTrue(childListJson.isArray());
        Assertions.assertEquals(2, childListJson.size());
        assertRootStorageObject(childListJson.get(0));
        assertTargetStorageObject(childListJson.get(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    @Order(4)
    void testFailOnGetParents() throws IOException {
        // stub
        Mockito.when(STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(STORAGE_OBJECT_MANAGER.getPath()).thenReturn(targetPath);
        Mockito.when(STORAGE_OBJECT_MANAGER.getRoot()).thenReturn(rootStorageObject);
        Mockito.when(rootStorageObject.getStorageObjectManager()).thenReturn(rootStorageObjectManager);
        Mockito.when(rootStorageObjectManager.getChildren(Mockito.eq(targetPath.getNameCount()), Mockito.any(Predicate.class))).thenThrow(new IOException());
        // execute
        ObjectNode resultJson = new ParentsCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.UNKNOWN.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(resultJson);
        Assertions.assertEquals(expectedJsonResult.toString(), resultJson.toString());
    }

    private final StorageObject rootStorageObject = Mockito.mock(StorageObject.class);
    private final StorageObjectManager rootStorageObjectManager = Mockito.mock(StorageObjectManager.class);
    private final Path rootPath = Paths.get("./test-volume");
    private final String rootStorageObjectHashKey = VOLUME_ID + "_" + PathUtils.encodeHashKey(rootPath);
    private final long rootLastModified = new Date().toInstant().toEpochMilli();
    private final String rootMimeType = "directory";

    private void stubRootStorageObject() throws IOException {
        long rootFileSize = 0L;
        Mockito.when(rootStorageObject.getRootVolume()).thenReturn(VOLUME);
        Mockito.when(rootStorageObjectManager.isRoot()).thenReturn(true);
        Mockito.when(rootStorageObject.getHashKey(true)).thenReturn(rootStorageObjectHashKey);
        Mockito.when(rootStorageObjectManager.getLastModified()).thenReturn(rootLastModified);
        Mockito.when(rootStorageObjectManager.isDirectory()).thenReturn(true);
        Mockito.when(rootStorageObject.getMimeType()).thenReturn(rootMimeType);
        Mockito.when(rootStorageObjectManager.size(true)).thenReturn(rootFileSize);
        Mockito.when(rootStorageObject.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private final Path targetPath = Paths.get("./test-volume/" + FOLDER_NAME);
    private final String targetStorageObjectHashKey = VOLUME_ID + "_" + PathUtils.encodeHashKey(targetPath);
    private final String targetParentHashKey = VOLUME_ID + "_" + PathUtils.encodeHashKey(targetPath);
    private final long targetLastModified = new Date().toInstant().toEpochMilli();
    private final String targetMimeType = "directory";

    private void stubTargetStorageObject() throws IOException {
        long targetFileSize = 0L;
        Mockito.when(STORAGE_OBJECT_MANAGER.isRoot()).thenReturn(false);
        Mockito.when(TARGET_OBJECT.getName()).thenReturn(FOLDER_NAME);
        Mockito.when(STORAGE_OBJECT_MANAGER.getParent()).thenReturn(TARGET_OBJECT);
        Mockito.when(TARGET_OBJECT.getHashKey(true)).thenReturn(targetParentHashKey);
        Mockito.when(TARGET_OBJECT.getHashKey(true)).thenReturn(targetStorageObjectHashKey);
        Mockito.when(STORAGE_OBJECT_MANAGER.getLastModified()).thenReturn(targetLastModified);
        Mockito.when(STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(TARGET_OBJECT.getRootVolume()).thenReturn(VOLUME);
        Mockito.when(TARGET_OBJECT.getMimeType()).thenReturn(targetMimeType);
        Mockito.when(STORAGE_OBJECT_MANAGER.size(true)).thenReturn(targetFileSize);
        Mockito.when(TARGET_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertRootStorageObject(JsonNode rootStorageObjectJson) {
        Assertions.assertNotNull(rootStorageObjectJson);
        Assertions.assertEquals(VOLUME_ID, rootStorageObjectJson.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(rootStorageObjectHashKey, rootStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(rootLastModified, rootStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(rootMimeType, rootStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }

    private void assertTargetStorageObject(JsonNode targetStorageObjectJson) {
        Assertions.assertNotNull(targetStorageObjectJson);
        Assertions.assertEquals(targetStorageObjectHashKey, targetStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(targetParentHashKey, targetStorageObjectJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(targetLastModified, targetStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(targetMimeType, targetStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }
}
