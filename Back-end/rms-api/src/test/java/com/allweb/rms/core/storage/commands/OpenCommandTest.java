package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.SecurityConstraints;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.PathUtils;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OpenCommandTest extends BaseCommand {
    // root
    private static final StorageObject ROOT_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager ROOT_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String ROOT_HASH_KEY = PathUtils.encodeHashKey(VOLUME_PATH);
    private static final String ROOT_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + ROOT_HASH_KEY;
    private static final long rootLastModified = new Date().toInstant().toEpochMilli();
    private static final String rootMimeType = "directory";
    // folder
    private static final StorageObject FOLDER_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FOLDER_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FOLDER_NAME = "folder";
    private static final Path FOLDER_PATH = Paths.get("./test-volume/" + FOLDER_NAME);
    private static final String FOLDER_HASH_KEY = PathUtils.encodeHashKey(FOLDER_PATH);
    private static final String FOLDER_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FOLDER_HASH_KEY;
    // file
    private static final StorageObject FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file1.txt";
    private static final Path FILE_PATH = VOLUME_PATH.resolve(FILE_NAME);
    private static final String FILE_HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private static final String FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FILE_HASH_KEY;

    @BeforeAll
    static void setUp() {
        Mockito.when(VOLUME.getBaseStorageObject()).thenReturn(ROOT_STORAGE_OBJECT);
        Mockito.when(VOLUME.getAlias()).thenReturn(VOLUME_ID);
        Mockito.when(VOLUME.getId()).thenReturn(VOLUME_ID);
        Mockito.when(STORAGE.getDefaultVolume()).thenReturn(VOLUME);
        Mockito.when(STORAGE.getVolumes()).thenReturn(Collections.singletonList(VOLUME));
        //
        Mockito.when(ROOT_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(ROOT_STORAGE_OBJECT_MANAGER);
        Mockito.when(ROOT_STORAGE_OBJECT.getRootVolume()).thenReturn(VOLUME);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.getPath()).thenReturn(VOLUME_PATH);
        // Folder
        Mockito.when(VOLUME.getStorageObject(FOLDER_HASH_KEY)).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FOLDER_STORAGE_OBJECT_MANAGER);
        Mockito.when(FOLDER_STORAGE_OBJECT.getHashKey(true)).thenReturn(FOLDER_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getPath()).thenReturn(FOLDER_PATH);
        //
        Mockito.when(FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FILE_STORAGE_OBJECT_MANAGER);
    }

    @Test
    @Order(1)
    void testSuccessInitStorageFromRootVolume() throws IOException {
        //
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.INIT.toString())).thenReturn("1");
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(null);
        //
        List<StorageObject> storageObjectList = new ArrayList<>();
        storageObjectList.add(FILE_STORAGE_OBJECT);
        Mockito.when(ROOT_STORAGE_OBJECT_MANAGER.getChildren()).thenReturn(storageObjectList);

        stubRootStorageObject();
        stubFileStorageObject();
        // execute
        ObjectNode jsonResult = new OpenCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.CURRENT_WORKING_DIRECTORY.toString()));
        JsonNode currentWorkingDirectoryJson = jsonResult.get(StorageConstants.Fields.CURRENT_WORKING_DIRECTORY.toString());
        assertRootStorageObject(currentWorkingDirectoryJson);
        //
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.FILES.toString()));
        JsonNode fileListJson = jsonResult.get(StorageConstants.Fields.FILES.toString());
        Assertions.assertTrue(fileListJson.isArray());
        Assertions.assertEquals(2, fileListJson.size());
        assertTargetStorageObject(fileListJson.get(0));
        assertRootStorageObject(fileListJson.get(1));
    }

    @Test
    @Order(2)
    void testSuccessInitStorageFromSpecifiedTarget() throws IOException {
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.INIT.toString())).thenReturn("1");
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(FOLDER_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(VOLUME.getStorageObject(ROOT_HASH_KEY)).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        List<StorageObject> storageObjectList = new ArrayList<>();
        storageObjectList.add(FILE_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChildren()).thenReturn(storageObjectList);

        stubRootStorageObject();
        stubFolderStorageObject();
        stubFileStorageObject();
        // execute
        ObjectNode jsonResult = new OpenCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.CURRENT_WORKING_DIRECTORY.toString()));
        JsonNode currentWorkingDirectoryJson = jsonResult.get(StorageConstants.Fields.CURRENT_WORKING_DIRECTORY.toString());
        assertFolderStorageObject(currentWorkingDirectoryJson);
        //
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.FILES.toString()));
        JsonNode fileListJson = jsonResult.get(StorageConstants.Fields.FILES.toString());
        Assertions.assertTrue(fileListJson.isArray());
        Assertions.assertEquals(2, fileListJson.size());
        assertTargetStorageObject(fileListJson.get(0));
        assertRootStorageObject(fileListJson.get(1));
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

    private void assertRootStorageObject(JsonNode rootStorageObjectJson) {
        Assertions.assertNotNull(rootStorageObjectJson);
        Assertions.assertEquals(VOLUME_ID, rootStorageObjectJson.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(ROOT_STORAGE_OBJECT_HASH_KEY, rootStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(rootLastModified, rootStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(rootMimeType, rootStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }

    private void stubFolderStorageObject() throws IOException {
        long fileSize = 0L;
        Mockito.when(FOLDER_STORAGE_OBJECT.getName()).thenReturn(FOLDER_NAME);
        Mockito.when(FOLDER_STORAGE_OBJECT.getRootVolume()).thenReturn(VOLUME);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.isRoot()).thenReturn(false);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getParent()).thenReturn(ROOT_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT.getHashKey(true)).thenReturn(FOLDER_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getLastModified()).thenReturn(rootLastModified);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(FOLDER_STORAGE_OBJECT.getMimeType()).thenReturn(rootMimeType);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.size(true)).thenReturn(fileSize);
        Mockito.when(FOLDER_STORAGE_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertFolderStorageObject(JsonNode rootStorageObjectJson) {
        Assertions.assertNotNull(rootStorageObjectJson);
        Assertions.assertEquals(FOLDER_NAME, rootStorageObjectJson.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(FOLDER_STORAGE_OBJECT_HASH_KEY, rootStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(rootLastModified, rootStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(rootMimeType, rootStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
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
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT.getRootVolume()).thenReturn(VOLUME);
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
