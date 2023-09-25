package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.SecurityConstraints;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.PathUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
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
class DuplicateCommandTest extends BaseCommand {
    private final String fileName = "image.png";
    private final Path filePath = VOLUME_PATH.resolve(fileName);
    private final String fileHashKey = PathUtils.encodeHashKey(filePath);
    private final String fileStorageObjectHashKey = VOLUME_ID + "_" + fileHashKey;
    private final StorageObject targetObject = Mockito.mock(StorageObject.class);
    private final StorageObjectManager targetObjectManager = Mockito.mock(StorageObjectManager.class);
    private final StorageObject parentObject = Mockito.mock(StorageObject.class);
    private final StorageObjectManager parentObjectManager = Mockito.mock(StorageObjectManager.class);
    private final StorageObject duplicateObject = Mockito.mock(StorageObject.class);
    private final StorageObjectManager duplicateObjectManager = Mockito.mock(StorageObjectManager.class);
    private final StorageObject parentDuplicateObject = Mockito.mock(StorageObject.class);

    @Test
    @Order(1)
    void testSuccessDuplicateFile() throws IOException {
        String duplicateFileName = "image(1).png";
        long imageSize = 0L;
        SecurityConstraints securityConstraints = new SecurityConstraints();
        long lastModified = new Date().toInstant().toEpochMilli();
        String duplicateObjectHashKey = PathUtils.encodeHashKey(Paths.get("./test-volume/" + duplicateFileName));
        String parentHashKey = PathUtils.encodeHashKey(Paths.get("./test-volume"));
        String mimeType = "image/png";
        //
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getStorageObject(fileHashKey)).thenReturn(targetObject);
        Mockito.when(targetObject.getStorageObjectManager()).thenReturn(targetObjectManager);
        Mockito.when(targetObjectManager.getParent()).thenReturn(parentObject);
        Mockito.when(parentObject.getStorageObjectManager()).thenReturn(parentObjectManager);
        Mockito.when(targetObjectManager.getPath()).thenReturn(Paths.get("./test-volume/image.png"));
        Mockito.when(parentObjectManager.getChild(duplicateFileName)).thenReturn(duplicateObject);
        Mockito.when(duplicateObject.getStorageObjectManager()).thenReturn(duplicateObjectManager);
        Mockito.when(duplicateObjectManager.exists()).thenReturn(false);
        Mockito.when(targetObjectManager.copyTo(Mockito.any(StorageObject.class))).thenReturn(duplicateObject);
        // stub for buildJsonResponseObject()
        Mockito.when(duplicateObjectManager.isRoot()).thenReturn(false);
        Mockito.when(duplicateObject.getName()).thenReturn(duplicateFileName);
        Mockito.when(targetObject.getName()).thenReturn(duplicateFileName);
        Mockito.when(duplicateObjectManager.getParent()).thenReturn(parentDuplicateObject);
        Mockito.when(parentDuplicateObject.getHashKey(true)).thenReturn(parentHashKey);
        Mockito.when(duplicateObject.getHashKey(true)).thenReturn(duplicateObjectHashKey);
        Mockito.when(duplicateObjectManager.getLastModified()).thenReturn(lastModified);
        Mockito.when(duplicateObjectManager.isDirectory()).thenReturn(false);
        Mockito.when(duplicateObjectManager.hasChild()).thenReturn(false);
        Mockito.when(duplicateObject.getMimeType()).thenReturn(mimeType);
        Mockito.when(duplicateObjectManager.size(true)).thenReturn(imageSize);
        Mockito.when(duplicateObject.getSecurityConstraints()).thenReturn(securityConstraints);

        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.TARGETS.toString())).thenReturn(new String[]{fileStorageObjectHashKey});

        DuplicateCommand command = new DuplicateCommand();
        ObjectNode result = command.execute(getStorageContext());

        Assertions.assertTrue(result.has("added"));
        JsonNode addedDuplicatedFileJsonNode = result.get("added");
        Assertions.assertTrue(addedDuplicatedFileJsonNode.isArray());
        Assertions.assertEquals(1, addedDuplicatedFileJsonNode.size());
        JsonNode duplicatedFileJsonNode = addedDuplicatedFileJsonNode.get(0);
        Assertions.assertEquals(duplicateFileName, duplicatedFileJsonNode.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(duplicateObjectHashKey, duplicatedFileJsonNode.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(parentHashKey, duplicatedFileJsonNode.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(lastModified, duplicatedFileJsonNode.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(mimeType, duplicatedFileJsonNode.get(StorageConstants.Fields.MIME.toString()).textValue());
    }

    @Test
    @Order(2)
    void testSuccessDuplicateDirectory() throws IOException {
        String duplicateFolderName = "folder(1)";
        long imageSize = 0L;
        SecurityConstraints securityConstraints = new SecurityConstraints();
        long lastModified = new Date().toInstant().toEpochMilli();
        Path duplicateObjectPath = Paths.get("./test-volume/" + duplicateFolderName);
        String duplicateObjectHashKey = PathUtils.encodeHashKey(duplicateObjectPath);
        String parentHashKey = PathUtils.encodeHashKey(Paths.get("./test-volume"));
        String mimeType = "directory";
        //
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getStorageObject(fileHashKey)).thenReturn(targetObject);
        Mockito.when(targetObject.getStorageObjectManager()).thenReturn(targetObjectManager);
        Mockito.when(targetObjectManager.getParent()).thenReturn(parentObject);
        Mockito.when(parentObject.getStorageObjectManager()).thenReturn(parentObjectManager);
        Mockito.when(targetObjectManager.getPath()).thenReturn(duplicateObjectPath);
        Mockito.when(parentObjectManager.getChild(duplicateFolderName)).thenReturn(duplicateObject);
        Mockito.when(duplicateObject.getStorageObjectManager()).thenReturn(duplicateObjectManager);
        Mockito.when(duplicateObjectManager.exists()).thenReturn(false);
        Mockito.when(targetObjectManager.copyTo(Mockito.any(StorageObject.class))).thenReturn(duplicateObject);
        // stub for buildJsonResponseObject()
        Mockito.when(duplicateObjectManager.isRoot()).thenReturn(false);
        Mockito.when(duplicateObject.getName()).thenReturn(duplicateFolderName);
        Mockito.when(targetObject.getName()).thenReturn(duplicateFolderName);
        Mockito.when(duplicateObjectManager.getParent()).thenReturn(parentDuplicateObject);
        Mockito.when(parentDuplicateObject.getHashKey(true)).thenReturn(parentHashKey);
        Mockito.when(duplicateObject.getHashKey(true)).thenReturn(duplicateObjectHashKey);
        Mockito.when(duplicateObjectManager.getLastModified()).thenReturn(lastModified);
        Mockito.when(duplicateObjectManager.isDirectory()).thenReturn(false);
        Mockito.when(duplicateObjectManager.hasChild()).thenReturn(false);
        Mockito.when(duplicateObject.getMimeType()).thenReturn(mimeType);
        Mockito.when(duplicateObjectManager.size(true)).thenReturn(imageSize);
        Mockito.when(duplicateObject.getSecurityConstraints()).thenReturn(securityConstraints);

        Mockito.when(HTTP_SERVLET_REQUEST.getParameterValues(StorageConstants.Parameters.TARGETS.toString())).thenReturn(new String[]{fileStorageObjectHashKey});

        DuplicateCommand command = new DuplicateCommand();
        ObjectNode result = command.execute(getStorageContext());

        Assertions.assertTrue(result.has("added"));
        JsonNode addedDuplicatedFileJsonNode = result.get("added");
        Assertions.assertTrue(addedDuplicatedFileJsonNode.isArray());
        Assertions.assertEquals(1, addedDuplicatedFileJsonNode.size());
        JsonNode duplicatedFileJsonNode = addedDuplicatedFileJsonNode.get(0);
        Assertions.assertEquals(duplicateFolderName, duplicatedFileJsonNode.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(duplicateObjectHashKey, duplicatedFileJsonNode.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(parentHashKey, duplicatedFileJsonNode.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(lastModified, duplicatedFileJsonNode.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(mimeType, duplicatedFileJsonNode.get(StorageConstants.Fields.MIME.toString()).textValue());
    }
}
