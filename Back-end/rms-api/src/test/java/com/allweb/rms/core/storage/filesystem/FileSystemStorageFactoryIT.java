package com.allweb.rms.core.storage.filesystem;

import com.allweb.rms.config.StorageConfig;
import com.allweb.rms.core.storage.StorageFactory;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.driver.filesystem.FileSystemStorage;
import com.allweb.rms.core.storage.driver.filesystem.FileSystemStorageObject;
import com.allweb.rms.core.storage.driver.filesystem.FileSystemVolume;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StorageConfig.class)
@TestPropertySource("classpath:filesystem-storage.properties")
class FileSystemStorageFactoryIT {

    @Autowired
    private StorageFactory fileSystemStorageFactory;

    @Test
    void verifyDefaultVolume() {
        FileSystemVolume defaultVolume = getDefaultVolume();
        Assertions.assertEquals("testVolume", defaultVolume.getAlias());
        Assertions.assertEquals("A", defaultVolume.getId());
        Assertions.assertEquals("testVolume", defaultVolume.getPath().toString());
        Assertions.assertNotEquals(0, defaultVolume.getStorageObjects().size());
    }

    @Test
    void verifyTestFolder() throws IOException {
        List<StorageObject> storageObjectList = getDefaultVolume().getStorageObjects();
        Assertions.assertNotNull(storageObjectList);
        Assertions.assertNotEquals(0, storageObjectList.size());
        FileSystemStorageObject testFolder = (FileSystemStorageObject) storageObjectList.get(0);
        Assertions.assertEquals("testFolder", testFolder.getAlias());
        Assertions.assertEquals("testFolder", testFolder.getName());
        Assertions.assertEquals("testVolume" + File.separator + "testFolder", testFolder.getPath().toString());
        Assertions.assertEquals("directory", testFolder.getMimeType());
        Assertions.assertEquals(this.getDefaultVolume(), testFolder.getRootVolume());
    }

    @AfterAll
    static void cleanUp() throws IOException {
        FileUtils.deleteDirectory(Paths.get("./testVolume").toFile());
    }

    private FileSystemVolume getDefaultVolume() {
        FileSystemStorage fileSystemStorage = (FileSystemStorage) fileSystemStorageFactory.getStorage();
        return (FileSystemVolume) fileSystemStorage.getDefaultVolume();
    }
}
