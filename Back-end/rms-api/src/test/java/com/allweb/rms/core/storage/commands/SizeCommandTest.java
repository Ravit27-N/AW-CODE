package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageConstants.Fields;
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
import java.util.Collections;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SizeCommandTest extends BaseCommand {
    private static final StorageObject FOLDER_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FOLDER_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FOLDER_NAME = "test-folder";
    private static final Path FOLDER_PATH = VOLUME_PATH.resolve(FOLDER_NAME);
    private static final String FOLDER_HASH_KEY = PathUtils.encodeHashKey(FOLDER_PATH);
    private static final String FOLDER_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FOLDER_HASH_KEY;
    // file
    private static final StorageObject FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file1.txt";
    private static final Path FILE_PATH = FOLDER_PATH.resolve(FILE_NAME);
    private static final String FILE_HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private static final String FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FILE_HASH_KEY;
    private static final long FILE_SIZE = 10L;

    @BeforeAll
    static void setUp() throws IOException {
        //
        Mockito.when(VOLUME.getStorageObject(FOLDER_HASH_KEY)).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FOLDER_STORAGE_OBJECT_MANAGER);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getBaseStorageObject()).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT.getHashKey(true)).thenReturn(FOLDER_STORAGE_OBJECT_HASH_KEY);
        //
        Mockito.when(VOLUME.getStorageObject(FILE_HASH_KEY)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FILE_STORAGE_OBJECT_MANAGER);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getBaseStorageObject()).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT.getHashKey(true)).thenReturn(FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isFile()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.size(false)).thenReturn(FILE_SIZE);
    }

    @Test
    @Order(1)
    void testSuccessGetEmptyFolderSize() throws IOException {
        int dirCount = 1;
        int fileCount = 0;
        int fileSize = 0;
        // Stub
        String[] targets = new String[]{FOLDER_STORAGE_OBJECT_HASH_KEY};
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.TARGETS.toString())).thenReturn(targets);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.isFile()).thenReturn(false);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChildren()).thenReturn(Collections.emptyList());
        // execute
        ObjectNode resultJson = new SizeCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(resultJson);
        this.assertCorrectSize(resultJson, dirCount, fileCount, fileSize);
        Assertions.assertTrue(resultJson.has(Fields.SIZES.toString()));
        JsonNode sizesJson = resultJson.get(Fields.SIZES.toString());
        Assertions.assertNotNull(sizesJson);
        Assertions.assertTrue(sizesJson.has(FOLDER_STORAGE_OBJECT_HASH_KEY));
        this.assertCorrectSize(sizesJson.get(FOLDER_STORAGE_OBJECT_HASH_KEY), dirCount, fileCount, fileSize);
    }

    @Test
    @Order(2)
    void testSuccessGetFileSize() {
        int dirCount = 0;
        int fileCount = 1;
        // Stub
        String[] targets = new String[]{FILE_STORAGE_OBJECT_HASH_KEY};
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.TARGETS.toString())).thenReturn(targets);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        // execute
        ObjectNode resultJson = new SizeCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(resultJson);
        this.assertCorrectSize(resultJson, dirCount, fileCount, FILE_SIZE);
        Assertions.assertTrue(resultJson.has(Fields.SIZES.toString()));
        JsonNode sizesJson = resultJson.get(Fields.SIZES.toString());
        Assertions.assertNotNull(sizesJson);
        Assertions.assertTrue(sizesJson.has(FILE_STORAGE_OBJECT_HASH_KEY));
        this.assertCorrectSize(sizesJson.get(FILE_STORAGE_OBJECT_HASH_KEY), dirCount, fileCount, FILE_SIZE);
    }

    @Test
    @Order(3)
    void testSuccessGetFolderSizeWithChildren() throws IOException {
        int fileCount = 1;
        int dirCount = 1;
        // Stub
        String[] targets = new String[]{FOLDER_STORAGE_OBJECT_HASH_KEY};
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.TARGETS.toString())).thenReturn(targets);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.isFile()).thenReturn(false);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChildren()).thenReturn(Collections.singletonList(FILE_STORAGE_OBJECT));
        // execute
        ObjectNode resultJson = new SizeCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(resultJson);
        this.assertCorrectSize(resultJson, dirCount, fileCount, FILE_SIZE);
        Assertions.assertTrue(resultJson.has(Fields.SIZES.toString()));
        JsonNode sizesJson = resultJson.get(Fields.SIZES.toString());
        Assertions.assertNotNull(sizesJson);
        Assertions.assertTrue(sizesJson.has(FOLDER_STORAGE_OBJECT_HASH_KEY));
        this.assertCorrectSize(sizesJson.get(FOLDER_STORAGE_OBJECT_HASH_KEY), dirCount, fileCount, FILE_SIZE);
    }

    private void assertCorrectSize(JsonNode json, int directoryCount, int fileCount, long size) {
        Assertions.assertTrue(json.has(Fields.DIRECTORY_COUNT.toString()));
        Assertions.assertTrue(json.has(Fields.FILE_COUNT.toString()));
        Assertions.assertTrue(json.has(Fields.SIZE.toString()));
        Assertions.assertEquals(directoryCount, json.get(Fields.DIRECTORY_COUNT.toString()).intValue());
        Assertions.assertEquals(fileCount, json.get(Fields.FILE_COUNT.toString()).intValue());
        Assertions.assertEquals(size, json.get(Fields.SIZE.toString()).longValue());
    }
}
