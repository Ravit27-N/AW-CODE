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
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PastCommandTest extends BaseCommand {
    private static final String DEFAULT_SUFFIX = "~";
    // folder
    private static final StorageObject FOLDER_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FOLDER_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FOLDER_FOLDER_NAME = "folder";
    private static final Path FOLDER_PATH = Paths.get("./test-volume/" + FOLDER_FOLDER_NAME);
    private static final String FOLDER_HASH_KEY = PathUtils.encodeHashKey(FOLDER_PATH);
    private static final String FOLDER_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FOLDER_HASH_KEY;
    // file
    private static final StorageObject FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file1.txt";
    private static final Path FILE_PATH = Paths.get("./test-volume/" + FILE_NAME);
    private static final String FILE_HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private static final String FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FILE_HASH_KEY;
    // copied file
    private static final StorageObject COPIED_FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager COPIED_FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String COPIED_FILE_NAME = "file1.txt";
    private static final Path COPIED_FILE_PATH = Paths.get("./test-volume/" + COPIED_FILE_NAME);
    private static final String COPIED_FILE_HASH_KEY = PathUtils.encodeHashKey(COPIED_FILE_PATH);
    private static final String COPIED_FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + COPIED_FILE_HASH_KEY;
    // copied and renamed file
    private static final StorageObject RENAMED_FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager RENAMED_FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String RENAMED_FILE_NAME = "file1-renamed.txt";
    private static final Path RENAMED_FILE_PATH = Paths.get("./test-volume/" + RENAMED_FILE_NAME);
    private static final String RENAMED_FILE_HASH_KEY = PathUtils.encodeHashKey(RENAMED_FILE_PATH);
    private static final String RENAMED_FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + RENAMED_FILE_HASH_KEY;

    @BeforeAll
    static void setUp() {
        // Folder
        Mockito.when(VOLUME.getStorageObject(FOLDER_HASH_KEY)).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FOLDER_STORAGE_OBJECT_MANAGER);
        Mockito.when(FOLDER_STORAGE_OBJECT.getHashKey(true)).thenReturn(FOLDER_STORAGE_OBJECT_HASH_KEY);
        // File
        Mockito.when(VOLUME.getStorageObject(FILE_HASH_KEY)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FILE_STORAGE_OBJECT_MANAGER);
        Mockito.when(FILE_STORAGE_OBJECT.getName()).thenReturn(FILE_NAME);
        Mockito.when(FILE_STORAGE_OBJECT.getHashKey(true)).thenReturn(FILE_STORAGE_OBJECT_HASH_KEY);
        // Copied file
        Mockito.when(COPIED_FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(COPIED_FILE_STORAGE_OBJECT_MANAGER);
        // Copied and renamed file
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(RENAMED_FILE_STORAGE_OBJECT_MANAGER);
        // Request
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.DESTINATION.toString())).thenReturn(FOLDER_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.TARGETS.toString())).thenReturn(new String[]{FILE_STORAGE_OBJECT_HASH_KEY});
    }

    // Copy
    @Test
    @Order(1)
    void testSuccessCopyFile() throws IOException {
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChild(FILE_NAME)).thenReturn(COPIED_FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.copyTo(COPIED_FILE_STORAGE_OBJECT)).thenReturn(COPIED_FILE_STORAGE_OBJECT);
        stubCopiedStorageObjectForJsonBuilding();
        // execute
        ObjectNode jsonResult = new PasteCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.ADDED.toString()));
        JsonNode addedListJson = jsonResult.get(StorageConstants.Fields.ADDED.toString());
        Assertions.assertTrue(addedListJson.isArray());
        Assertions.assertEquals(1, addedListJson.size());
        assertCorrectCopiedStorageObjectJson(addedListJson.get(0));
    }

    // Copy and Rename
    @Test
    @Order(2)
    void testSuccessCopyFileWithRename() throws IOException {
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.RENAMES.toString())).thenReturn(new String[]{RENAMED_FILE_NAME});
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChild(DEFAULT_SUFFIX + RENAMED_FILE_NAME)).thenReturn(RENAMED_FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.copyTo(RENAMED_FILE_STORAGE_OBJECT)).thenReturn(RENAMED_FILE_STORAGE_OBJECT);
        stubRenamedStorageObjectForJsonBuilding();
        // execute
        ObjectNode jsonResult = new PasteCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.ADDED.toString()));
        JsonNode addedListJson = jsonResult.get(StorageConstants.Fields.ADDED.toString());
        Assertions.assertTrue(addedListJson.isArray());
        Assertions.assertEquals(1, addedListJson.size());
        assertCorrectRenameStorageObjectJson(addedListJson.get(0));
    }

    // Cut
    @Test
    @Order(3)
    void testSuccessMoveFile() throws IOException {
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.RENAMES.toString())).thenReturn(null);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.IS_CUT.toString())).thenReturn("1");
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChild(FILE_NAME)).thenReturn(COPIED_FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.copyTo(COPIED_FILE_STORAGE_OBJECT)).thenReturn(COPIED_FILE_STORAGE_OBJECT);
        Mockito.doNothing().when(FILE_STORAGE_OBJECT_MANAGER).remove();
        stubCopiedStorageObjectForJsonBuilding();
        // execute
        ObjectNode jsonResult = new PasteCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.ADDED.toString()));
        JsonNode addedListJson = jsonResult.get(StorageConstants.Fields.ADDED.toString());
        Assertions.assertTrue(addedListJson.isArray());
        Assertions.assertEquals(1, addedListJson.size());
        assertCorrectCopiedStorageObjectJson(addedListJson.get(0));
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.REMOVED.toString()));
        JsonNode removedListJson = jsonResult.get(StorageConstants.Fields.REMOVED.toString());
        Assertions.assertTrue(removedListJson.isArray());
        Assertions.assertEquals(1, removedListJson.size());
        Assertions.assertEquals(FILE_STORAGE_OBJECT_HASH_KEY, removedListJson.get(0).textValue());
    }

    // Cut and Rename
    @Test
    @Order(4)
    void testSuccessMoveFileWithRename() throws IOException {
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.RENAMES.toString())).thenReturn(new String[]{RENAMED_FILE_NAME});
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.IS_CUT.toString())).thenReturn("1");
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChild(DEFAULT_SUFFIX + RENAMED_FILE_NAME)).thenReturn(RENAMED_FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.copyTo(RENAMED_FILE_STORAGE_OBJECT)).thenReturn(RENAMED_FILE_STORAGE_OBJECT);
        stubRenamedStorageObjectForJsonBuilding();
        // execute
        ObjectNode jsonResult = new PasteCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.ADDED.toString()));
        JsonNode addedListJson = jsonResult.get(StorageConstants.Fields.ADDED.toString());
        Assertions.assertTrue(addedListJson.isArray());
        Assertions.assertEquals(1, addedListJson.size());
        assertCorrectRenameStorageObjectJson(addedListJson.get(0));
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.REMOVED.toString()));
        JsonNode removedListJson = jsonResult.get(StorageConstants.Fields.REMOVED.toString());
        Assertions.assertTrue(removedListJson.isArray());
        Assertions.assertEquals(1, removedListJson.size());
        Assertions.assertEquals(FILE_STORAGE_OBJECT_HASH_KEY, removedListJson.get(0).textValue());
    }

    // Copy
    @Test
    @Order(5)
    void verifyNothingAddedOrRemovedWhenCopyFail() throws IOException {
        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.RENAMES.toString())).thenReturn(null);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.IS_CUT.toString())).thenReturn(null);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChild(FILE_NAME)).thenReturn(COPIED_FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.copyTo(COPIED_FILE_STORAGE_OBJECT)).thenThrow(new IOException());
        stubCopiedStorageObjectForJsonBuilding();
        // execute
        ObjectNode jsonResult = new PasteCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.ADDED.toString()));
        JsonNode addedListJson = jsonResult.get(StorageConstants.Fields.ADDED.toString());
        Assertions.assertTrue(addedListJson.isArray());
        Assertions.assertEquals(0, addedListJson.size());
        JsonNode removedListJson = jsonResult.get(StorageConstants.Fields.REMOVED.toString());
        Assertions.assertTrue(removedListJson.isArray());
        Assertions.assertEquals(0, removedListJson.size());
    }

    private final long targetLastModified = new Date().toInstant().toEpochMilli();
    private final String targetMimeType = "text/plain";

    private void stubCopiedStorageObjectForJsonBuilding() throws IOException {
        long targetFileSize = 0L;
        Mockito.when(COPIED_FILE_STORAGE_OBJECT_MANAGER.isRoot()).thenReturn(false);
        Mockito.when(COPIED_FILE_STORAGE_OBJECT.getName()).thenReturn(FILE_NAME);
        Mockito.when(COPIED_FILE_STORAGE_OBJECT_MANAGER.getParent()).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(COPIED_FILE_STORAGE_OBJECT.getHashKey(true)).thenReturn(COPIED_FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(COPIED_FILE_STORAGE_OBJECT_MANAGER.getLastModified()).thenReturn(targetLastModified);
        Mockito.when(COPIED_FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        Mockito.when(COPIED_FILE_STORAGE_OBJECT.getMimeType()).thenReturn(targetMimeType);
        Mockito.when(COPIED_FILE_STORAGE_OBJECT_MANAGER.size(true)).thenReturn(targetFileSize);
        Mockito.when(COPIED_FILE_STORAGE_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertCorrectCopiedStorageObjectJson(JsonNode targetStorageObjectJson) {
        Assertions.assertNotNull(targetStorageObjectJson);
        Assertions.assertEquals(COPIED_FILE_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(FOLDER_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(targetLastModified, targetStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(targetMimeType, targetStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }

    private void stubRenamedStorageObjectForJsonBuilding() throws IOException {
        long targetFileSize = 0L;
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT_MANAGER.isRoot()).thenReturn(false);
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT.getName()).thenReturn(FILE_NAME);
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT_MANAGER.getParent()).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT.getHashKey(true)).thenReturn(RENAMED_FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT_MANAGER.getLastModified()).thenReturn(targetLastModified);
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT.getMimeType()).thenReturn(targetMimeType);
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT_MANAGER.size(true)).thenReturn(targetFileSize);
        Mockito.when(RENAMED_FILE_STORAGE_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertCorrectRenameStorageObjectJson(JsonNode targetStorageObjectJson) {
        Assertions.assertNotNull(targetStorageObjectJson);
        Assertions.assertEquals(RENAMED_FILE_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(FOLDER_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(targetLastModified, targetStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(targetMimeType, targetStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }
}
