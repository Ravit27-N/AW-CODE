package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.SecurityConstraints;
import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageContext;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.Volume;
import com.allweb.rms.core.storage.utils.PathUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UploadCommandTest {
    private static final StorageObject FOLDER_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FOLDER_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FOLDER_FOLDER_NAME = "folder";
    private static final Path FOLDER_PATH = Paths.get("./test-volume/" + FOLDER_FOLDER_NAME);
    private static final String FOLDER_HASH_KEY = PathUtils.encodeHashKey(FOLDER_PATH);
    protected static final String VOLUME_ID = "A";
    private static final String FOLDER_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FOLDER_HASH_KEY;

    protected static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();
    protected static final MultipartHttpServletRequest MULTIPART_HTTP_SERVLET_REQUEST = Mockito.mock(MultipartHttpServletRequest.class);
    protected static final Storage STORAGE = Mockito.mock(Storage.class);
    private static final StorageContext STORAGE_CONTEXT_INSTANCE;
    protected static final Volume VOLUME = Mockito.mock(Volume.class);
    // file
    private static final StorageObject FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);
    private static final String FILE_NAME = "file1.txt";
    private static final Path FILE_PATH = FOLDER_PATH.resolve(FILE_NAME);
    private static final String FILE_HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private static final String FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FILE_HASH_KEY;

    static {
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        STORAGE_CONTEXT_INSTANCE = new StorageContext() {
            @Override
            public Storage getStorage() {
                return STORAGE;
            }

            @Override
            public HttpServletRequest getRequest() {
                return MULTIPART_HTTP_SERVLET_REQUEST;
            }
        };
    }

    private static StorageContext getStorageContext() {
        return STORAGE_CONTEXT_INSTANCE;
    }

    @BeforeAll
    static void setUp() {
        // Root
        Mockito.when(VOLUME.getStorageObject(FOLDER_HASH_KEY)).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FOLDER_STORAGE_OBJECT_MANAGER);
        Mockito.when(FOLDER_STORAGE_OBJECT.getRootVolume()).thenReturn(VOLUME);
        // File
        Mockito.when(FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FILE_STORAGE_OBJECT_MANAGER);
        // Request
        Mockito.when(MULTIPART_HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(FOLDER_STORAGE_OBJECT_HASH_KEY);
    }

    @Test
    @Order(1)
    void testFailWhenDirectoryNotExist() {
        // stub
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.exists()).thenReturn(false);
        // execute
        ObjectNode jsonResult = new UploadCommand().execute(getStorageContext());
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
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        // execute
        ObjectNode jsonResult = new UploadCommand().execute(getStorageContext());
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
    void testSuccessUploadAFile() throws IOException {
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        //
        ClassPathResource imageResource = new ClassPathResource("sample/sample-test.txt");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        MultiValueMap<String, MultipartFile> multiValueMap = Mockito.mock(MultiValueMap.class);
        MultipartFile uploadingFile = Mockito.mock(MultipartFile.class);
        Mockito.when(MULTIPART_HTTP_SERVLET_REQUEST.getMultiFileMap()).thenReturn(multiValueMap);
        Mockito.when(multiValueMap.isEmpty()).thenReturn(false);
        Mockito.when(multiValueMap.get(StorageConstants.Parameters.UPLOAD_FILES.toString())).thenReturn(Collections.singletonList(uploadingFile));
        Mockito.when(uploadingFile.getOriginalFilename()).thenReturn(FILE_NAME);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.createFile(FILE_NAME)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChild(FILE_NAME)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.openOutputStream()).thenReturn(outStream);
        Mockito.when(uploadingFile.getInputStream()).thenReturn(imageResource.getInputStream());
        stubTargetStorageObject();
        // execute
        ObjectNode jsonResult = new UploadCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertTrue(jsonResult.has(StorageConstants.Fields.ADDED.toString()));
        JsonNode childListJson = jsonResult.get(StorageConstants.Fields.ADDED.toString());
        Assertions.assertTrue(childListJson.isArray());
        Assertions.assertEquals(1, childListJson.size());
        assertTargetStorageObject(childListJson.get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    @Order(4)
    void testFailOnUploadAFile() throws IOException {
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(true);
        //
        MultiValueMap<String, MultipartFile> multiValueMap = Mockito.mock(MultiValueMap.class);
        MultipartFile uploadingFile = Mockito.mock(MultipartFile.class);
        Mockito.when(MULTIPART_HTTP_SERVLET_REQUEST.getMultiFileMap()).thenReturn(multiValueMap);
        Mockito.when(multiValueMap.isEmpty()).thenReturn(false);
        Mockito.when(multiValueMap.get(StorageConstants.Parameters.UPLOAD_FILES.toString())).thenReturn(Collections.singletonList(uploadingFile));
        Mockito.when(uploadingFile.getOriginalFilename()).thenReturn(FILE_NAME);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.createFile(FILE_NAME)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT_MANAGER.getChild(FILE_NAME)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.openOutputStream()).thenThrow(new IOException());
        // execute
        ObjectNode jsonResult = new UploadCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(jsonResult);
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.UPLOAD_FILE.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(jsonResult);
        Assertions.assertEquals(expectedJsonResult.toString(), jsonResult.toString());
    }

    private final long targetLastModified = new Date().toInstant().toEpochMilli();
    private final String targetMimeType = "text/plain";

    private void stubTargetStorageObject() throws IOException {
        long targetFileSize = 0L;
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isRoot()).thenReturn(false);
        Mockito.when(FILE_STORAGE_OBJECT.getName()).thenReturn(FILE_NAME);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getParent()).thenReturn(FOLDER_STORAGE_OBJECT);
        Mockito.when(FOLDER_STORAGE_OBJECT.getHashKey(true)).thenReturn(FOLDER_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FILE_STORAGE_OBJECT.getHashKey(true)).thenReturn(FILE_STORAGE_OBJECT_HASH_KEY);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.getLastModified()).thenReturn(targetLastModified);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isDirectory()).thenReturn(false);
        Mockito.when(FILE_STORAGE_OBJECT.getRootVolume()).thenReturn(VOLUME);
        Mockito.when(FILE_STORAGE_OBJECT.getMimeType()).thenReturn(targetMimeType);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.size(true)).thenReturn(targetFileSize);
        Mockito.when(FILE_STORAGE_OBJECT.getSecurityConstraints()).thenReturn(new SecurityConstraints());
    }

    private void assertTargetStorageObject(JsonNode targetStorageObjectJson) {
        Assertions.assertNotNull(targetStorageObjectJson);
        Assertions.assertEquals(FILE_NAME, targetStorageObjectJson.get(StorageConstants.Fields.NAME.toString()).textValue());
        Assertions.assertEquals(FILE_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.HASH.toString()).textValue());
        Assertions.assertEquals(FOLDER_STORAGE_OBJECT_HASH_KEY, targetStorageObjectJson.get(StorageConstants.Fields.PARENT_HASH.toString()).textValue());
        Assertions.assertEquals(targetLastModified, targetStorageObjectJson.get(StorageConstants.Fields.TIMESTAMP.toString()).longValue());
        Assertions.assertEquals(targetMimeType, targetStorageObjectJson.get(StorageConstants.Fields.MIME.toString()).textValue());
    }
}
