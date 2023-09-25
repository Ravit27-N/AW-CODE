package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.SearchOption;
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
import java.util.Collections;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchCommandTest extends BaseCommand {
    // file 1
    private static final StorageObject ROOT_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager ROOT_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String ROOT_HASH_KEY = PathUtils.encodeHashKey(VOLUME_PATH);
    private static final String ROOT_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + ROOT_HASH_KEY;
    // file
    private static final StorageObject FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file1.txt";
    private static final Path FILE_PATH = VOLUME_PATH.resolve(FILE_NAME);
    private static final String FILE_HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private static final String FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FILE_HASH_KEY;

    @BeforeAll
    static void setUp() {
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        //
        Mockito.when(VOLUME.getStorageObject(ROOT_HASH_KEY)).thenReturn(ROOT_STORAGE_OBJECT);
        Mockito.when(ROOT_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(ROOT_STORAGE_OBJECT_MANAGER);
        //
        Mockito.when(VOLUME.getStorageObject(FILE_HASH_KEY)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FILE_STORAGE_OBJECT_MANAGER);
    }

    @Test
    @Order(1)
    void testSuccessSearchFromAllVolumes() throws IOException {
        // stub
        Mockito.reset(HTTP_SERVLET_REQUEST);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.SEARCH_STRING.toString())).thenReturn(FILE_NAME);
        Mockito.when(STORAGE.getVolumes()).thenReturn(Collections.singletonList(VOLUME));
        Mockito.when(VOLUME.getBaseStorageObject()).thenReturn(ROOT_STORAGE_OBJECT);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.search(FILE_NAME, SearchOption.RELEVANT)).thenReturn(Collections.singletonList(FILE_STORAGE_OBJECT));
        stubFileStorageObject();
        // execute
        ObjectNode jsonResult = new SearchCommand().execute(getStorageContext());
        // expect
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.FILES.toString()));
        JsonNode childListJson = jsonResult.get(StorageConstants.Fields.FILES.toString());
        Assertions.assertTrue(childListJson.isArray());
        assertTargetStorageObject(childListJson.get(0));
    }

    @Test
    @Order(2)
    void testFailWhenSearchOnTargetIsNotDirectory() throws IOException {
        // stub
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.SEARCH_STRING.toString())).thenReturn(FILE_NAME);
        //
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        // execute
        ObjectNode jsonResult = new SearchCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.NOT_DIRECTORY.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertEquals(expectedJsonResult.toString(), jsonResult.toString());
    }

    @Test
    @Order(4)
    void testFailWhenSearchOnTargetDirectoryIsNotExist() throws IOException {
        // stub
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(ROOT_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.SEARCH_STRING.toString())).thenReturn(FILE_NAME);
        //
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.exists()).thenReturn(false);
        // execute
        ObjectNode jsonResult = new SearchCommand().execute(getStorageContext());
        // expect
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.DIRECTORY_NOT_FOUND.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertEquals(expectedJsonResult.toString(), jsonResult.toString());
    }

    @Test
    @Order(3)
    void testSuccessSearchOnTargetDirectory() throws IOException {
        // stub
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(ROOT_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.SEARCH_STRING.toString())).thenReturn(FILE_NAME);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.search(FILE_NAME, SearchOption.RELEVANT)).thenReturn(Collections.singletonList(FILE_STORAGE_OBJECT));
        stubFileStorageObject();
        // execute
        ObjectNode jsonResult = new SearchCommand().execute(getStorageContext());
        // expect
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.FILES.toString()));
        JsonNode childListJson = jsonResult.get(StorageConstants.Fields.FILES.toString());
        Assertions.assertTrue(childListJson.isArray());
        assertTargetStorageObject(childListJson.get(0));
    }

    private final long targetLastModified = new Date().toInstant().toEpochMilli();
    private final String targetMimeType = "text/plain";

    private void stubFileStorageObject() throws IOException {
        long targetFileSize = 0L;
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isRoot()).thenReturn(false);
        Mockito.when(FILE_STORAGE_OBJECT.getName()).thenReturn(FILE_NAME);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getParent()).thenReturn(ROOT_STORAGE_OBJECT);
        Mockito.when(ROOT_STORAGE_OBJECT.getHashKey(true)).thenReturn(ROOT_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FILE_STORAGE_OBJECT.getHashKey(true)).thenReturn(FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getLastModified()).thenReturn(targetLastModified);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        Mockito.when(FILE_STORAGE_OBJECT.getMimeType()).thenReturn(targetMimeType);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.size(true)).thenReturn(targetFileSize);
        Mockito.when(FILE_STORAGE_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertTargetStorageObject(JsonNode targetStorageObjectJson) {
        Assertions.assertNotNull(targetStorageObjectJson);
        Assertions.assertEquals(FILE_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(ROOT_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(targetLastModified, targetStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(targetMimeType, targetStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }
}
