package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.PathUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GetCommandTest extends BaseCommand {
    private static final StorageObject targetObject = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager targetObjectManager = Mockito.mock(StorageObjectManager.class);

    @BeforeAll
    static void init() {
        String hashKey = PathUtils.encodeHashKey(VOLUME_PATH.resolve("file.txt"));
        String targetStorageObjectHashKey = VOLUME_ID + "_" + hashKey;
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getStorageObject(hashKey)).thenReturn(targetObject);
        Mockito.when(targetObject.getStorageObjectManager()).thenReturn(targetObjectManager);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(targetStorageObjectHashKey);
    }

    @Test
    @Order(1)
    void testFailOnGetContentOfNoneExistFile() {
        // stub
        Mockito.when(targetObjectManager.exists()).thenReturn(false);
        // execute
        ObjectNode result = new GetCommand().execute(getStorageContext());
        // expected
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.ITEM_EXISTS.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedJsonResult.toString(), result.toString());
    }

    @Test
    @Order(2)
    void testFailOnGetContentOfNoneFileType() throws IOException {
        // stub
        Mockito.when(targetObjectManager.isFile()).thenReturn(false);
        // execute
        ObjectNode result = new GetCommand().execute(getStorageContext());
        // expected
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.ITEM_EXISTS.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedJsonResult.toString(), result.toString());
    }

    @Test
    @Order(3)
    void testSuccessOnGetFileContent() throws IOException {
        // stub
        ClassPathResource imageResource = new ClassPathResource("sample/sample-test.txt");
        Mockito.when(targetObjectManager.exists()).thenReturn(true);
        Mockito.when(targetObjectManager.isFile()).thenReturn(true);
        Mockito.when(targetObjectManager.openInputStream()).thenReturn(imageResource.getInputStream());
        // execute
        ObjectNode result = new GetCommand().execute(getStorageContext());
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.has(StorageConstants.Fields.CONTENT.toString()));
        Assertions.assertEquals(IOUtils.toString(imageResource.getInputStream(), StandardCharsets.UTF_8), result.get(StorageConstants.Fields.CONTENT.toString()).textValue());
    }

    @Test
    @Order(4)
    void testFailOnReadingFileContent() throws IOException {
        // stub
        Mockito.when(targetObjectManager.exists()).thenReturn(true);
        Mockito.when(targetObjectManager.isFile()).thenReturn(true);
        Mockito.when(targetObjectManager.openInputStream()).thenThrow(new IOException());
        // execute
        ObjectNode result = new GetCommand().execute(getStorageContext());
        // expected
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expectedJsonResult = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.READ.getKey());
        expectedJsonResult.set(StorageConstants.Errors.KEY.getKey(), errors);
        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedJsonResult.toString(), result.toString());
    }
}
