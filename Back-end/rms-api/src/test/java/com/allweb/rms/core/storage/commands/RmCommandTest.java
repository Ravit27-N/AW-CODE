package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.PathUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RmCommandTest extends BaseCommand {
    // file 1
    private StorageObject firstStorageObject;
    private StorageObjectManager firstStorageObjectManager;
    private final String FIRST_FILE_NAME = "file1.txt";
    private final Path FIRST_FILE_PATH = VOLUME_PATH.resolve(FIRST_FILE_NAME);
    private final String FIRST_FILE_HASH_KEY = PathUtils.encodeHashKey(FIRST_FILE_PATH);
    private final String FIRST_FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FIRST_FILE_HASH_KEY;
    // file 2
    private StorageObject secondStorageObject;
    private StorageObjectManager secondStorageObjectManager;
    private final String SECOND_FILE_NAME = "file2.txt";
    private final Path SECOND_FILE_PATH = VOLUME_PATH.resolve(SECOND_FILE_NAME);
    private final String SECOND_FILE_HASH_KEY = PathUtils.encodeHashKey(SECOND_FILE_PATH);
    private final String SECOND_FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + SECOND_FILE_HASH_KEY;

    @BeforeEach
    void init() {
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        // file 1
        firstStorageObject = Mockito.mock(StorageObject.class);
        firstStorageObjectManager = Mockito.mock(StorageObjectManager.class);
        Mockito.when(VOLUME.getStorageObject(FIRST_FILE_HASH_KEY)).thenReturn(firstStorageObject);
        Mockito.when(firstStorageObject.getStorageObjectManager()).thenReturn(firstStorageObjectManager);
        Mockito.when(firstStorageObjectManager.getBaseStorageObject()).thenReturn(firstStorageObject);
        // file 2
        secondStorageObject = Mockito.mock(StorageObject.class);
        secondStorageObjectManager = Mockito.mock(StorageObjectManager.class);
        Mockito.when(VOLUME.getStorageObject(SECOND_FILE_HASH_KEY)).thenReturn(secondStorageObject);
        Mockito.when(secondStorageObject.getStorageObjectManager()).thenReturn(secondStorageObjectManager);
        Mockito.when(secondStorageObjectManager.getBaseStorageObject()).thenReturn(secondStorageObject);
        //
        String[] targets = new String[]{FIRST_FILE_STORAGE_OBJECT_HASH_KEY, SECOND_FILE_STORAGE_OBJECT_HASH_KEY};
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.TARGETS.toString())).thenReturn(targets);
    }

    @Test
    @Order(2)
    void testFailWhenRemoveFileOrFolder() throws IOException {
        // stub file 1
        Mockito.when(firstStorageObject.getName()).thenReturn(FIRST_FILE_NAME);
        Mockito.when(firstStorageObjectManager.getPath()).thenReturn(FIRST_FILE_PATH);
        Mockito.doThrow(new IOException("Cannot remove File: " + firstStorageObjectManager.getPath().normalize().toAbsolutePath().toString()))
                .when(firstStorageObjectManager)
                .remove();
        // stub file 2
        Mockito.when(secondStorageObject.getName()).thenReturn(SECOND_FILE_NAME);
        Mockito.when(secondStorageObjectManager.getPath()).thenReturn(SECOND_FILE_PATH);
        Mockito.doThrow(new IOException("Cannot remove File: " + secondStorageObjectManager.getPath().normalize().toAbsolutePath().toString()))
                .when(secondStorageObjectManager)
                .remove();
        // execute command
        ObjectNode jsonResult = new RmCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.REMOVED.toString()));
        Assertions.assertTrue(jsonResult.has(StorageConstants.Errors.KEY.getKey()));
        ArrayNode errors = jsonResult.withArray(StorageConstants.Errors.KEY.getKey());
        Assertions.assertNotNull(errors);
        Assertions.assertTrue(errors.size() > 0);
        Assertions.assertEquals(FIRST_FILE_NAME, errors.get(1).textValue());
        Assertions.assertEquals(SECOND_FILE_NAME, errors.get(3).textValue());
    }

    @Test
    @Order(1)
    void testSuccessWhenRemoveFileOrFolder() throws IOException {
        // stub file 1
        Mockito.when(firstStorageObject.getHashKey(true)).thenReturn(FIRST_FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.doNothing().when(firstStorageObjectManager).remove();
        // stub file 2
        Mockito.when(secondStorageObject.getHashKey(true)).thenReturn(SECOND_FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.doNothing().when(secondStorageObjectManager).remove();
        // execute command
        ObjectNode jsonResult = new RmCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.REMOVED.toString()));
        ArrayNode removedFiles = jsonResult.withArray(StorageConstants.Fields.REMOVED.toString());
        Assertions.assertNotNull(removedFiles);
        Assertions.assertTrue(removedFiles.size() > 0);
        Assertions.assertEquals(FIRST_FILE_STORAGE_OBJECT_HASH_KEY, removedFiles.get(0).textValue());
        Assertions.assertEquals(SECOND_FILE_STORAGE_OBJECT_HASH_KEY, removedFiles.get(1).textValue());
    }


}
