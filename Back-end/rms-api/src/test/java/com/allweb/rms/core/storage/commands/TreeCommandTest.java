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
import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TreeCommandTest extends BaseCommand {
    private static final StorageObject ROOT_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager ROOT_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String ROOT_HASH_KEY = PathUtils.encodeHashKey(VOLUME_PATH);
    private static final String ROOT_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + ROOT_HASH_KEY;
    private final long rootLastModified = new Date().toInstant().toEpochMilli();
    private final String rootMimeType = "directory";
    // file
    private static final StorageObject FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file1.txt";
    private static final Path FILE_PATH = VOLUME_PATH.resolve(FILE_NAME);
    private static final String FILE_HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private static final String FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FILE_HASH_KEY;

    @BeforeAll
    static void setUp() {
        // Root
        Mockito.when(VOLUME.getStorageObject(ROOT_HASH_KEY)).thenReturn(ROOT_STORAGE_OBJECT);
        Mockito.when(ROOT_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(ROOT_STORAGE_OBJECT_MANAGER);
        Mockito.when(ROOT_STORAGE_OBJECT.getRootVolume()).thenReturn(VOLUME);
        Mockito.when(VOLUME.getAlias()).thenReturn(VOLUME_ID);
        // File
        Mockito.when(VOLUME.getStorageObject(FILE_HASH_KEY)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FILE_STORAGE_OBJECT_MANAGER);
        // Request
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(ROOT_STORAGE_OBJECT_HASH_KEY);
    }

    @Test
    @Order(1)
    void testFailWhenDirectoryNotExist() {
        // stub
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.exists()).thenReturn(false);
        // execute
        ObjectNode jsonResult = new TreeCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.DIRECTORY_NOT_FOUND.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertEquals(expectedJsonResult.toString(), jsonResult.toString());
    }

    @Test
    @Order(2)
    void testFailWhenTargetIsNotDirectory() throws IOException {
        // stub
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        // execute
        ObjectNode jsonResult = new TreeCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.NOT_DIRECTORY.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertEquals(expectedJsonResult.toString(), jsonResult.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Order(3)
    void testSuccessChildFiles() throws IOException {
        // stub
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.getChildren(Mockito.eq(Integer.MAX_VALUE), Mockito.any(Predicate.class))).thenReturn(Arrays.asList(ROOT_STORAGE_OBJECT, FILE_STORAGE_OBJECT));
        // stub for buildJsonResponseObject()
        this.stubRootStorageObject();
        this.stubTargetStorageObject();
        // execute
        ObjectNode jsonResult = new TreeCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.TREE.toString()));
        JsonNode childListJson = jsonResult.get(StorageConstants.Fields.TREE.toString());
        Assertions.assertTrue(childListJson.isArray());
        Assertions.assertEquals(2, childListJson.size());
        assertRootStorageObject(childListJson.get(0));
        assertTargetStorageObject(childListJson.get(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    @Order(4)
    void testFailWhenTarget() throws IOException {
        // stub
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.getChildren(Mockito.eq(Integer.MAX_VALUE), Mockito.any(Predicate.class))).thenThrow(new IOException());
        // execute
        ObjectNode jsonResult = new TreeCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.UNKNOWN.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertEquals(expectedJsonResult.toString(), jsonResult.toString());
    }

    private void stubRootStorageObject() throws IOException {
        long rootFileSize = 0L;
        Mockito.when(ROOT_STORAGE_OBJECT.getRootVolume()).thenReturn(VOLUME);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.isRoot()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT.getHashKey(true)).thenReturn(ROOT_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.getLastModified()).thenReturn(rootLastModified);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT.getMimeType()).thenReturn(rootMimeType);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.size(true)).thenReturn(rootFileSize);
        Mockito.when(ROOT_STORAGE_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private final long targetLastModified = new Date().toInstant().toEpochMilli();
    private final String targetMimeType = "text/plain";

    private void stubTargetStorageObject() throws IOException {
        long targetFileSize = 0L;
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isRoot()).thenReturn(false);
        Mockito.when(FILE_STORAGE_OBJECT.getName()).thenReturn(FILE_NAME);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getParent()).thenReturn(ROOT_STORAGE_OBJECT);
        Mockito.when(ROOT_STORAGE_OBJECT.getHashKey(true)).thenReturn(ROOT_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FILE_STORAGE_OBJECT.getHashKey(true)).thenReturn(FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getLastModified()).thenReturn(targetLastModified);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT.getRootVolume()).thenReturn(VOLUME);
        Mockito.when(FILE_STORAGE_OBJECT.getMimeType()).thenReturn(targetMimeType);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.size(true)).thenReturn(targetFileSize);
        Mockito.when(FILE_STORAGE_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertRootStorageObject(JsonNode rootStorageObjectJson) {
        Assertions.assertNotNull(rootStorageObjectJson);
        Assertions.assertEquals(VOLUME_ID, rootStorageObjectJson.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(ROOT_STORAGE_OBJECT_HASH_KEY, rootStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(rootLastModified, rootStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(rootMimeType, rootStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }

    private void assertTargetStorageObject(JsonNode targetStorageObjectJson) {
        Assertions.assertNotNull(targetStorageObjectJson);
        Assertions.assertEquals(FILE_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(ROOT_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(targetLastModified, targetStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(targetMimeType, targetStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }
}
