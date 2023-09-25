package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.utils.PathUtils;
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

import java.io.IOException;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DimCommandTest extends BaseCommand {
    private static final String FILE_NAME = "image.png";
    private static final Path FILE_PATH = VOLUME_PATH.resolve(FILE_NAME);
    private static final String FILE_HASH_KEY = PathUtils.encodeHashKey(FILE_PATH);
    private static final String FILE_STORAGE_OBJECT_HASH_KEY = VOLUME_ID + "_" + FILE_HASH_KEY;
    private static final StorageObject FILE_STORAGE_OBJECT = Mockito.mock(StorageObject.class);
    private static final StorageObjectManager FILE_STORAGE_OBJECT_MANAGER = Mockito.mock(StorageObjectManager.class);

    @BeforeAll
    static void init() {
        Mockito.when(STORAGE.getVolume(VOLUME_ID)).thenReturn(VOLUME);
        Mockito.when(VOLUME.getStorageObject(FILE_HASH_KEY)).thenReturn(FILE_STORAGE_OBJECT);
        Mockito.when(FILE_STORAGE_OBJECT.getStorageObjectManager()).thenReturn(FILE_STORAGE_OBJECT_MANAGER);
        Mockito.when(HTTP_SERVLET_REQUEST.getParameter(StorageConstants.Parameters.TARGET.toString())).thenReturn(FILE_STORAGE_OBJECT_HASH_KEY);
    }

    @Test
    @Order(1)
    void testSuccessExtractImageDimension() throws IOException {
        // setup
        ClassPathResource imageResource = new ClassPathResource("sample/image.png");
        // stub
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isFile()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.openInputStream()).thenReturn(imageResource.getInputStream());
        // execute
        DimCommand dimCommand = new DimCommand();
        ObjectNode result = dimCommand.execute(getStorageContext());
        // assert
        String dimensions = "16x16";
        ObjectNode expected = JACKSON_OBJECT_MAPPER.createObjectNode();
        expected.put(StorageConstants.Fields.DIMENSION.toString(), dimensions);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected.toString(), result.toString());
    }

    @Test
    @Order(2)
    void testFailOnExtractNoneImageFileDimension() throws IOException {
        // stub
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isFile()).thenReturn(false);
        // execute
        DimCommand dimCommand = new DimCommand();
        ObjectNode result = dimCommand.execute(getStorageContext());
        // assert
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expected = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.NOT_FILE.getKey());
        expected.set(StorageConstants.Errors.KEY.getKey(), errors);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected.toString(), result.toString());
    }

    @Test
    @Order(3)
    void testFailOnExtractNoneExistImageFileDimension() throws IOException {
        // stub
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isFile()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.exists()).thenReturn(false);
        // execute
        DimCommand dimCommand = new DimCommand();
        ObjectNode result = dimCommand.execute(getStorageContext());
        // assert
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expected = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.FILE_NOT_FOUND.getKey());
        expected.set(StorageConstants.Errors.KEY.getKey(), errors);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected.toString(), result.toString());
    }

    @Test
    @Order(4)
    void testFailOnExtractImageFileDimension() throws IOException {
        // stub
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.isFile()).thenReturn(true);
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.exists()).thenReturn(true);
        ClassPathResource imageResource = new ClassPathResource("sample/sample-test.txt");
        Mockito.when(FILE_STORAGE_OBJECT_MANAGER.openInputStream()).thenReturn(imageResource.getInputStream());
        // execute
        DimCommand dimCommand = new DimCommand();
        ObjectNode result = dimCommand.execute(getStorageContext());
        // assert
        ArrayNode errors = JACKSON_OBJECT_MAPPER.createArrayNode();
        ObjectNode expected = JACKSON_OBJECT_MAPPER.createObjectNode();
        errors.add(StorageConstants.Errors.UNKNOWN.getKey());
        expected.set(StorageConstants.Errors.KEY.getKey(), errors);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected.toString(), result.toString());
    }

}
