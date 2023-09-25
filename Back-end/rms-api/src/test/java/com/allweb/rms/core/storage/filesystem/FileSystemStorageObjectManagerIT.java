package com.allweb.rms.core.storage.filesystem;

import com.allweb.rms.core.storage.SearchOption;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.driver.filesystem.FileSystemStorageObject;
import com.allweb.rms.core.storage.driver.filesystem.FileSystemStorageObjectManager;
import com.allweb.rms.core.storage.driver.filesystem.FileSystemVolume;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileSystemStorageObjectManagerIT {
    private static final Path TEST_VOLUME_PATH = Paths.get("./test-volume");
    private static FileSystemVolume TEST_VOLUME;
    private static FileSystemStorageObjectManager TEST_VOLUME_MANAGER;

    @BeforeAll
    static void setUp() throws IOException {
        if (!Files.exists(TEST_VOLUME_PATH)) {
            Files.createDirectory(TEST_VOLUME_PATH);
        }
        TEST_VOLUME = new FileSystemVolume("A", "root-test-volume", TEST_VOLUME_PATH);
        TEST_VOLUME_MANAGER = (FileSystemStorageObjectManager) TEST_VOLUME.getBaseStorageObject().getStorageObjectManager();
    }

    @AfterAll
    static void cleanUp() throws IOException {
        FileUtils.deleteDirectory(TEST_VOLUME_PATH.toFile());
    }

    @Test
    @Order(1)
    void testSuccessGetExistChildFile() throws IOException {
        String testFolder = "test-folder";
        String testFile = "file.txt";
        Path testFolderPath = TEST_VOLUME.getPath().resolve(testFolder);
        Files.createDirectory(testFolderPath);
        Files.createFile(testFolderPath.resolve(testFile));
        StorageObjectManager childFileManager = TEST_VOLUME_MANAGER.getChild(testFolder + "/" + testFile)
                .getStorageObjectManager();

        Assertions.assertTrue(childFileManager.exists());
        Assertions.assertTrue(childFileManager.isFile());
        FileUtils.deleteDirectory(testFolderPath.toFile());
    }

    @Test
    @Order(1)
    void testSuccessGetExistChildFolder() throws IOException {
        String testFolder = "test-folder";
        Path testFolderPath = TEST_VOLUME.getPath().resolve(testFolder);
        Files.createDirectory(testFolderPath);
        StorageObjectManager childFileManager = TEST_VOLUME.getBaseStorageObject()
                .getStorageObjectManager()
                .getChild(testFolder)
                .getStorageObjectManager();

        Assertions.assertTrue(childFileManager.exists());
        Assertions.assertTrue(childFileManager.isDirectory());
        FileUtils.deleteDirectory(testFolderPath.toFile());
    }

    @Test
    @Order(1)
    void testSuccessGetNoneExistChildFile() throws IOException {
        String testFolder = "test-folder";
        Path testFolderPath = TEST_VOLUME.getPath().resolve(testFolder);
        Files.createDirectory(testFolderPath);
        StorageObjectManager childFileManager = TEST_VOLUME_MANAGER.getChild(testFolder)
                .getStorageObjectManager()
                .getChild("not-exist-file.txt")
                .getStorageObjectManager();
        Assertions.assertFalse(childFileManager.exists());
        Assertions.assertTrue(childFileManager.isFile());
        FileUtils.deleteDirectory(testFolderPath.toFile());
    }

    @Test
    @Order(1)
    void testSuccessGetNoneExistChildFolder() throws IOException {
        StorageObjectManager childFileManager = TEST_VOLUME.getBaseStorageObject()
                .getStorageObjectManager()
                .getChild("not-exist-folder")
                .getStorageObjectManager();

        Assertions.assertFalse(childFileManager.exists());
        Assertions.assertTrue(childFileManager.isDirectory());
    }

    @Test
    void testSuccessExtractParent() throws IOException {
        String testFolder = "test-folder";
        String testFile = "file.txt";
        Path testFolderPath = TEST_VOLUME.getPath().resolve(testFolder);
        Path testFilePath = testFolderPath.resolve(testFile);
        Files.createDirectory(testFolderPath);
        Files.createFile(testFilePath);
        // get root parent will return itself
        FileSystemStorageObject root = (FileSystemStorageObject) TEST_VOLUME.getBaseStorageObject()
                .getStorageObjectManager()
                .getParent();
        Assertions.assertEquals(TEST_VOLUME.getPath(), root.getPath());
        // root as a parent
        FileSystemStorageObject testFolderParent = (FileSystemStorageObject) TEST_VOLUME_MANAGER.getChild(testFolder)
                .getStorageObjectManager()
                .getParent();
        Assertions.assertEquals(TEST_VOLUME.getPath(), testFolderParent.getPath());
        // non root parent
        FileSystemStorageObject testFileParent = (FileSystemStorageObject) testFolderParent.getStorageObjectManager()
                .getChild(testFolder + "/" + testFile)
                .getStorageObjectManager()
                .getParent();
        Assertions.assertEquals(testFolderPath, testFileParent.getPath());
        FileUtils.deleteDirectory(testFolderPath.toFile());
    }

    @Test
    void testSuccessSelfFileCreation() throws IOException {
        String testFolder = "test-folder";
        String childFileName = "childFile.txt";
        Path testFolderPath = TEST_VOLUME.getPath().resolve(testFolder);
        Files.createDirectory(testFolderPath);
        StorageObjectManager childFileManager = TEST_VOLUME_MANAGER.getChild(testFolder + "/" + childFileName)
                .getStorageObjectManager();
        IOException exception = null;
        try {
            childFileManager.create();
        } catch (IOException ioException) {
            exception = ioException;
        }

        Assertions.assertNull(exception);
        Assertions.assertTrue(childFileManager.exists());
        Assertions.assertFalse(childFileManager.isDirectory());
        FileUtils.deleteDirectory(testFolderPath.toFile());
    }

    @Test
    void testSuccessSelfFolderCreation() throws IOException {
        String childFolderName = "childFolder";
        String testFolder = "test-folder";
        Path testFolderPath = TEST_VOLUME.getPath().resolve(testFolder);
        Files.createDirectory(testFolderPath);
        StorageObjectManager childFolderManager = TEST_VOLUME_MANAGER.getChild(testFolder + "/" + childFolderName)
                .getStorageObjectManager();
        IOException exception = null;
        try {
            childFolderManager.create();
        } catch (IOException ioException) {
            exception = ioException;
        }
        Assertions.assertNull(exception);
        Assertions.assertTrue(childFolderManager.exists());
        Assertions.assertTrue(childFolderManager.isDirectory());
        FileUtils.deleteDirectory(testFolderPath.toFile());
    }

    // Rename
    @Test
    void testSuccessSelfFileRename() throws IOException {
        String testFileName = "a.txt";
        String newFileName = "b.txt";
        StorageObjectManager testFileManager = TEST_VOLUME_MANAGER.getChild(testFileName).getStorageObjectManager();
        StorageObjectManager renamedFileManager = TEST_VOLUME_MANAGER.getChild(newFileName).getStorageObjectManager();
        testFileManager.create();
        testFileManager.rename(newFileName);

        Assertions.assertFalse(testFileManager.exists());
        Assertions.assertTrue(renamedFileManager.exists());
        Files.deleteIfExists(TEST_VOLUME.getPath().resolve(testFileName));
        Files.deleteIfExists(TEST_VOLUME.getPath().resolve(newFileName));
    }

    @Test
    void testSuccessSelfEmptyFolderRename() throws IOException {
        String testFolderName = "a";
        String newFolderName = "b";
        StorageObjectManager testFolderManager = TEST_VOLUME_MANAGER.getChild(testFolderName).getStorageObjectManager();
        StorageObjectManager renamedFolderManager = TEST_VOLUME_MANAGER.getChild(newFolderName).getStorageObjectManager();
        testFolderManager.create();
        testFolderManager.rename(newFolderName);

        Assertions.assertFalse(testFolderManager.exists());
        Assertions.assertTrue(renamedFolderManager.exists());
        FileUtils.deleteDirectory(renamedFolderManager.getPath().toFile());
    }

    @Test
    void testSuccessSelfNoneEmptyFolderRename() throws IOException {
        String testFolderName = "a";
        String newFolderName = "b";
        String testFileName = "a.txt";
        StorageObjectManager testFolderManager = TEST_VOLUME_MANAGER.getChild(testFolderName).getStorageObjectManager();
        StorageObjectManager testFileManager = testFolderManager.getChild(testFileName).getStorageObjectManager();
        StorageObjectManager renamedFolderManager = TEST_VOLUME_MANAGER.getChild(newFolderName).getStorageObjectManager();
        StorageObjectManager renamedFileManager = renamedFolderManager.getChild(testFileName).getStorageObjectManager();
        testFolderManager.create();
        testFileManager.create();
        testFolderManager.rename(newFolderName);

        Assertions.assertFalse(testFolderManager.exists());
        Assertions.assertTrue(renamedFolderManager.exists());
        Assertions.assertFalse(testFileManager.exists());
        Assertions.assertTrue(renamedFileManager.exists());
        FileUtils.deleteDirectory(renamedFolderManager.getPath().toFile());
    }

    @ParameterizedTest
    @MethodSource("getExactNamesForSearch")
    void testSearchExactMatchName(String searchName) throws IOException {
        createTestFilesForSearch();
        List<StorageObject> searchResultList = TEST_VOLUME_MANAGER.search(searchName, SearchOption.EXACT_MATCH);

        Assertions.assertEquals(1, searchResultList.size());

        FileUtils.deleteDirectory(TEST_VOLUME.getPath().resolve("a").toFile());
    }

    @ParameterizedTest
    @MethodSource("getSearchFileNames")
    void testSearchRelevantName(String searchName) throws IOException {
        createTestFilesForSearch();
        List<StorageObject> searchResultList = TEST_VOLUME_MANAGER.search(searchName, SearchOption.RELEVANT);
        long folderCount = searchResultList.stream().filter(storageObject -> {
            try {
                return StorageConstants.MIME_TYPE_DIRECTORY.equals(storageObject.getMimeType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }).count();
        long fileCount = searchResultList.size() - folderCount;

        Assertions.assertEquals(2, searchResultList.size());
        Assertions.assertEquals(1, folderCount);
        Assertions.assertEquals(1, fileCount);

        FileUtils.deleteDirectory(TEST_VOLUME.getPath().resolve("a").toFile());
    }

    private void createTestFilesForSearch() throws IOException {
        String directories = "a/b/c";
        String testFile1 = "a/a.txt";
        String testFile2 = "a/b/b.txt";
        String testFile3 = "a/b/c/c.txt";
        Files.createDirectories(TEST_VOLUME.getPath().resolve(directories));
        Files.createFile(TEST_VOLUME.getPath().resolve(testFile1));
        Files.createFile(TEST_VOLUME.getPath().resolve(testFile2));
        Files.createFile(TEST_VOLUME.getPath().resolve(testFile3));
    }

    private static Stream<String> getExactNamesForSearch() {
        return Stream.of("a.txt", "b.txt", "c.txt");
    }

    private static Stream<String> getSearchFileNames() {
        return Stream.of("a", "b", "c");
    }

    @Test
    void testCorrectFileSizeCalculation() throws IOException {
        String sampleFolder = "sample";
        String sampleTestFile = "sample-test.txt";
        StorageObjectManager sampleFolderManager = TEST_VOLUME_MANAGER.createDirectory(sampleFolder).getStorageObjectManager();
        ClassPathResource sampleSourceFile = new ClassPathResource(sampleFolder + "/" + sampleTestFile);
        int byteCount = 0;
        for (int i = 1; i <= 3; i++) {
            String fileName = String.format("%s-%d.%s", FilenameUtils.getBaseName(sampleTestFile), i, FilenameUtils.getExtension(sampleTestFile));
            OutputStream destination = sampleFolderManager.createFile(fileName).getStorageObjectManager().openOutputStream();
            byteCount += Math.max(IOUtils.copy(sampleSourceFile.getInputStream(), destination), 0);

        }
        long totalSampleFileSizeInByte = sampleFolderManager.size(true);

        Assertions.assertEquals(byteCount, totalSampleFileSizeInByte);

        FileUtils.deleteDirectory(sampleFolderManager.getPath().toFile());
    }

    @Test
    void testCorrectEmptyFolderSizeCalculation() throws IOException {
        String sampleFolder = "sample";
        StorageObjectManager sampleFolderManager = TEST_VOLUME_MANAGER.createDirectory(sampleFolder).getStorageObjectManager();
        long totalSampleFileSizeInByte = sampleFolderManager.size(true);

        Assertions.assertEquals(0, totalSampleFileSizeInByte);

        FileUtils.deleteDirectory(sampleFolderManager.getPath().toFile());
    }

    @Test
    void testSuccessCopyFileToExistDestinationDirectory() throws IOException {
        String sourceFolder = "a";
        String destinationFolder = "b";
        String sourceFile = sourceFolder + "/" + "a.txt";
        String destinationFile = destinationFolder + "/" + "a.txt";
        Path sourceFolderPath = TEST_VOLUME_MANAGER.getPath().resolve(sourceFolder);
        Path destinationFolderPath = TEST_VOLUME_MANAGER.getPath().resolve(destinationFolder);
        Files.createDirectory(sourceFolderPath);
        Files.createDirectory(destinationFolderPath);
        Files.createFile(TEST_VOLUME.getPath().resolve(sourceFile));
        StorageObject destinationCopyingFile = TEST_VOLUME_MANAGER.getChild(destinationFile);

        Assertions.assertFalse(Files.exists(destinationCopyingFile.getStorageObjectManager().getPath()));

        TEST_VOLUME_MANAGER.getChild(sourceFile)
                .getStorageObjectManager()
                .copyTo(destinationCopyingFile);

        Assertions.assertTrue(Files.exists(destinationCopyingFile.getStorageObjectManager().getPath()));

        FileUtils.deleteDirectory(sourceFolderPath.toFile());
        FileUtils.deleteDirectory(destinationFolderPath.toFile());
    }

    @Test
    void testSuccessCopyFolderToExistDestinationDirectory() throws IOException {
        String sourceFolder = "a";
        String destinationFolder = "b";
        String sourceFile = sourceFolder + "/" + "a.txt";
        Path sourceFolderPath = TEST_VOLUME_MANAGER.getPath().resolve(sourceFolder);
        Path destinationFolderPath = TEST_VOLUME_MANAGER.getPath().resolve(destinationFolder);
        Files.createDirectory(sourceFolderPath);
        Files.createDirectory(destinationFolderPath);
        Files.createFile(TEST_VOLUME.getPath().resolve(sourceFile));
        StorageObject destinationCopyingFolder = TEST_VOLUME_MANAGER.getChild(destinationFolder + "/" + sourceFolder);

        Assertions.assertFalse(Files.exists(destinationCopyingFolder.getStorageObjectManager().getPath()));

        TEST_VOLUME_MANAGER.getChild(sourceFolder)
                .getStorageObjectManager()
                .copyTo(destinationCopyingFolder);

        Assertions.assertTrue(Files.exists(destinationCopyingFolder.getStorageObjectManager().getPath()));

        FileUtils.deleteDirectory(sourceFolderPath.toFile());
        FileUtils.deleteDirectory(destinationFolderPath.toFile());
    }

    @Test
    void testSuccessCopyFileToNoneExistDestinationDirectory() throws IOException {
        String sourceFolder = "a";
        String destinationFolder = "b";
        String sourceFile = sourceFolder + "/" + "a.txt";
        String destinationFile = destinationFolder + "/" + "a.txt";
        Path sourceFolderPath = TEST_VOLUME_MANAGER.getPath().resolve(sourceFolder);
        Path destinationFolderPath = TEST_VOLUME_MANAGER.getPath().resolve(destinationFolder);
        Files.createDirectory(sourceFolderPath);
        Files.createFile(TEST_VOLUME.getPath().resolve(sourceFile));
        StorageObject destinationCopyingFile = TEST_VOLUME_MANAGER.getChild(destinationFile);

        Assertions.assertFalse(Files.exists(destinationCopyingFile.getStorageObjectManager().getPath()));

        TEST_VOLUME_MANAGER.getChild(sourceFile)
                .getStorageObjectManager()
                .copyTo(destinationCopyingFile);

        Assertions.assertTrue(Files.exists(destinationCopyingFile.getStorageObjectManager().getPath()));

        FileUtils.deleteDirectory(sourceFolderPath.toFile());
        FileUtils.deleteDirectory(destinationFolderPath.toFile());
    }

    @Test
    void testSuccessCopyFolderToNoneExistDestinationDirectory() throws IOException {
        String sourceFolder = "a";
        String destinationFolder = "b";
        String sourceFile = sourceFolder + "/" + "a.txt";
        Path sourceFolderPath = TEST_VOLUME_MANAGER.getPath().resolve(sourceFolder);
        Path destinationFolderPath = TEST_VOLUME_MANAGER.getPath().resolve(destinationFolder);
        Files.createDirectory(sourceFolderPath);
        Files.createFile(TEST_VOLUME.getPath().resolve(sourceFile));
        StorageObject destinationCopyingFolder = TEST_VOLUME_MANAGER.getChild(destinationFolder + "/" + sourceFolder);

        Assertions.assertFalse(Files.exists(destinationCopyingFolder.getStorageObjectManager().getPath()));

        TEST_VOLUME_MANAGER.getChild(sourceFolder)
                .getStorageObjectManager()
                .copyTo(destinationCopyingFolder);

        Assertions.assertTrue(Files.exists(destinationCopyingFolder.getStorageObjectManager().getPath()));

        FileUtils.deleteDirectory(sourceFolderPath.toFile());
        FileUtils.deleteDirectory(destinationFolderPath.toFile());
    }

    @Test
    void testSuccessCreateNonExistDirectories() throws IOException {
        String directory = "a/b/c";
        Exception exception = null;
        StorageObjectManager directoryManager = null;
        try {
            directoryManager = TEST_VOLUME_MANAGER.createDirectory(directory).getStorageObjectManager();
        } catch (IOException ioException) {
            exception = ioException;
        }
        Assertions.assertNull(exception);
        Assertions.assertNotNull(directoryManager);
        Assertions.assertTrue(directoryManager.exists());

        FileUtils.deleteDirectory(TEST_VOLUME_MANAGER.getPath().resolve("a").toFile());
    }

    @Test
    void testSuccessCreateFile() throws IOException {
        String file = "a.txt";
        Exception exception = null;
        StorageObjectManager fileManager = null;
        try {
            fileManager = TEST_VOLUME_MANAGER.createFile(file, true).getStorageObjectManager();
        } catch (IOException ioException) {
            exception = ioException;
        }
        Assertions.assertNull(exception);
        Assertions.assertTrue(Files.exists(fileManager.getPath()));
        Assertions.assertEquals("text/plain", fileManager.getBaseStorageObject().getMimeType());

        Files.delete(fileManager.getPath());
    }

    @Test
    void testSuccessCreateFileWithNonexistentParentDirectories() throws IOException {
        String file = "a/b/c/a.txt";
        Exception exception = null;
        StorageObjectManager fileManager = null;
        try {
            fileManager = TEST_VOLUME_MANAGER.createFile(file, true).getStorageObjectManager();
        } catch (IOException ioException) {
            exception = ioException;
        }
        Assertions.assertNull(exception);
        Assertions.assertTrue(Files.exists(fileManager.getPath()));
        Assertions.assertEquals("text/plain", fileManager.getBaseStorageObject().getMimeType());

        FileUtils.deleteDirectory(TEST_VOLUME_MANAGER.getPath().resolve("a").toFile());
    }

    @Test
    void testGetChildren() throws IOException {
        initTestChildren();

        List<StorageObject> children = TEST_VOLUME_MANAGER.getChildren(-1, null);
        long folderCount = children.stream().filter(storageObject -> {
            try {
                return StorageConstants.MIME_TYPE_DIRECTORY.equals(storageObject.getMimeType());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return false;
        }).count();

        Assertions.assertEquals(4, children.size());
        Assertions.assertEquals(2, folderCount);
        Assertions.assertEquals(2, children.size() - folderCount);

        cleanTestChildren();
    }

    @Test
    void testGetDirectChildren() throws IOException {
        initTestChildren();

        List<StorageObject> children = TEST_VOLUME_MANAGER.getChildren(1, null);
        long folderCount = children.stream().filter(storageObject -> {
            try {
                return StorageConstants.MIME_TYPE_DIRECTORY.equals(storageObject.getMimeType());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return false;
        }).count();

        Assertions.assertEquals(2, children.size());
        Assertions.assertEquals(2, folderCount);

        cleanTestChildren();
    }

    @Test
    void testGetChildrenWithSpecifiedDirectoryLevel() throws IOException {
        initTestChildren();

        List<StorageObject> children = TEST_VOLUME_MANAGER.getChildren(2, null);
        long folderCount = children.stream().filter(storageObject -> {
            try {
                return StorageConstants.MIME_TYPE_DIRECTORY.equals(storageObject.getMimeType());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return false;
        }).count();

        Assertions.assertEquals(4, children.size());
        Assertions.assertEquals(2, folderCount);

        cleanTestChildren();
    }

    private final String testFolder1 = "a";
    private final String testFolder2 = "b";

    private void initTestChildren() throws IOException {
        String testFile2 = "b/b.txt";
        String testFile1 = "a/a.txt";
        Files.createDirectories(TEST_VOLUME_MANAGER.getPath().resolve(testFolder1));
        Files.createDirectories(TEST_VOLUME_MANAGER.getPath().resolve(testFolder2));
        Files.createFile(TEST_VOLUME_MANAGER.getPath().resolve(testFile1));
        Files.createFile(TEST_VOLUME_MANAGER.getPath().resolve(testFile2));
    }

    private void cleanTestChildren() throws IOException {
        FileUtils.deleteDirectory(TEST_VOLUME_MANAGER.getPath().resolve(testFolder1).toFile());
        FileUtils.deleteDirectory(TEST_VOLUME_MANAGER.getPath().resolve(testFolder2).toFile());
    }

    @ParameterizedTest
    @MethodSource("getDirectoryCreationCondition")
    void testSuccessMoveFile(boolean shouldCreateDirectory) throws IOException {
        String file = "a.txt";
        String destinationFolder = "a";
        Files.createFile(TEST_VOLUME_MANAGER.getPath().resolve(file));
        if (shouldCreateDirectory) {
            Files.createDirectory(TEST_VOLUME_MANAGER.getPath().resolve(destinationFolder));
        }
        StorageObject sourceFileObject = TEST_VOLUME_MANAGER.getChild(file);
        StorageObject destinationFileObject = TEST_VOLUME_MANAGER.getChild(destinationFolder + "/" + file);
        Exception exception = null;
        try {
            TEST_VOLUME_MANAGER.move(sourceFileObject, destinationFileObject).getStorageObjectManager();
        } catch (IOException ioException) {
            exception = ioException;
        }

        Assertions.assertNull(exception);
        Assertions.assertFalse(Files.exists(sourceFileObject.getStorageObjectManager().getPath()));
        Assertions.assertTrue(Files.exists(destinationFileObject.getStorageObjectManager().getPath()));

        FileUtils.deleteDirectory(TEST_VOLUME_MANAGER.getPath().resolve(destinationFolder).toFile());
        Files.deleteIfExists(TEST_VOLUME_MANAGER.getPath().resolve(file));
    }

    @ParameterizedTest
    @MethodSource("getDirectoryCreationCondition")
    void testSuccessMoveFolder(boolean shouldCreateDirectory) throws IOException {
        String folder = "a";
        String file = "a/a.txt";
        String destinationFolder = "b";
        Files.createDirectory(TEST_VOLUME_MANAGER.getPath().resolve(folder));
        Files.createFile(TEST_VOLUME_MANAGER.getPath().resolve(file));
        if (shouldCreateDirectory) {
            Files.createDirectory(TEST_VOLUME_MANAGER.getPath().resolve(destinationFolder));
        }
        StorageObject sourceFolderObject = TEST_VOLUME_MANAGER.getChild(folder);
        StorageObject destinationFolderObject = TEST_VOLUME_MANAGER.getChild(destinationFolder + "/" + folder);
        Exception exception = null;
        try {
            TEST_VOLUME_MANAGER.move(sourceFolderObject, destinationFolderObject).getStorageObjectManager();
        } catch (IOException ioException) {
            exception = ioException;
        }

        Assertions.assertNull(exception);
        Assertions.assertFalse(Files.exists(sourceFolderObject.getStorageObjectManager().getPath()));
        Assertions.assertTrue(Files.exists(destinationFolderObject.getStorageObjectManager().getPath()));

        FileUtils.deleteDirectory(TEST_VOLUME_MANAGER.getPath().resolve(folder).toFile());
        FileUtils.deleteDirectory(TEST_VOLUME_MANAGER.getPath().resolve(destinationFolder).toFile());
        Files.deleteIfExists(TEST_VOLUME_MANAGER.getPath().resolve(file));
    }

    static Stream<Boolean> getDirectoryCreationCondition() {
        return Stream.of(false, true);
    }
}
