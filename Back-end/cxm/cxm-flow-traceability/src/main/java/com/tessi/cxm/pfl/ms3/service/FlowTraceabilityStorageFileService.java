package com.tessi.cxm.pfl.ms3.service;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms3.exception.Base64FileContentNotFoundException;
import com.tessi.cxm.pfl.ms3.service.restclient.FileManagerFeignClient;
import com.tessi.cxm.pfl.shared.exception.FileNotFoundException;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.FlowDepositArea;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.FlowDocument;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.FlowTraceability;
import com.tessi.cxm.pfl.shared.utils.SerialExecutor;
import feign.FeignException.FeignClientException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handle file processing.
 *
 * @author Piseth KHON
 * @see FileManagerResource
 * @see FileService
 */
@Slf4j
@Service
public class FlowTraceabilityStorageFileService {

  private static final String TMP_PATH = "temporary-file";
  private final String csvExtension = ".".concat(FlowTreatmentConstants.CSV_EXTENSION);
  private final String zipExtension = ".".concat(FlowTreatmentConstants.ZIP_EXTENSION);
  private final FileService fileService;
  private final FileManagerFeignClient fileManagerFeignClient;

  public FlowTraceabilityStorageFileService(
      FileService fileService, FileManagerFeignClient fileManagerFeignClient) {
    this.fileService = fileService;
    this.fileManagerFeignClient = fileManagerFeignClient;
  }

  @Autowired
  public void setPrivilegeValidationUtil(ProfileFeignClient profileFeignClient) {
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
  }

  /**
   * Get base64 from File Manager server.
   *
   * @param fileId refer to identity of file
   * @return base64 value of {@link String}.
   */
  private String getBase64File(String fileId, String token, String type) {
    String funcKey = ProfileConstants.CXM_FLOW_TRACEABILITY;
    String privKey =
        type.equalsIgnoreCase("flowTraceability") ? FlowTraceability.DOWNLOAD : FlowDocument.DOWNLOAD_DOCUMENT;

    if(type.equalsIgnoreCase("FlowDeposit")){
      funcKey = ProfileConstants.CXM_FLOW_DEPOSIT;
      privKey = FlowDepositArea.LIST_DEPOSITS;
    }

    try {
      return this.fileManagerFeignClient.getFile(fileId, funcKey, privKey, token).getContent();

    } catch (FeignClientException e) {
      throw new Base64FileContentNotFoundException("Fail to get base64 file content", e);
    }
  }

  /**
   * Store base64 file.
   *
   * @param fileId refer to identity of file
   * @return path location of file
   * @see FileService#decodeBase64ToFile(String, String)
   */
  private Path storeBase64File(String fileId, String token, String type) {
    final Path destinationPath =
        this.fileService.getPath(TMP_PATH).resolve(fileId.concat(zipExtension));
    this.fileService.decodeBase64ToFile(getBase64File(fileId, token, type), destinationPath.toString());
    return destinationPath;
  }

  /**
   * Convert file to base64.
   *
   * @param sourcePath refer to source directories of file
   * @return base64
   * @see FileService#encodeFileToBase64(String)
   */
  private String getEncodeFile(String sourcePath) {
    return this.fileService.encodeFileToBase64(sourcePath);
  }

  /**
   * Unzip standard file.
   *
   * @param source refer to source directories of file
   * @see FileService#unZipCommand(Path, Path)
   */
  private void unZipStandardFile(Path source) {
    this.fileService.unZipCommand(source, source.resolveSibling(""));
  }

  /**
   * Get csv file.
   *
   * @param folder refer to source directories of file
   * @return path of file.
   * @see FileService#getFileInfo(String, String[])
   */
  private String getBaseFileFromBase64(Path folder) {
    return this.fileService.getFileInfo(folder.toString(), new String[]{csvExtension}).stream()
        .filter(f -> !f.getFileName().startsWith(FlowTreatmentConstants.CSV_MODIFY_PREFIX))
        .findFirst()
        .orElseThrow(() -> new FileNotFoundException("file not found!"))
        .getFilePath();
  }

  /**
   * Deletes a file, never throwing an exception. If file is a directory, delete it and all
   * subdirectories.
   *
   * @see FileService#deleteDirectoryQuietly(Path)
   */
  private void cleanResourceFile() {
    new SerialExecutor(Executors.newSingleThreadExecutor())
        .execute(() -> this.fileService.deleteDirectoryQuietly(this.fileService.getPath(TMP_PATH)));
  }

  /**
   * Get a file content as text in UTF-8 format.
   *
   * @return File content as text in UTF-8 format.
   */
  public String getFileContent(String fileId, String flowType, String token, String type) {
    if (FlowDocumentSubChannel.digitalEmailSMSSubChannels().contains(flowType.toLowerCase())) {
      final Path source = storeBase64File(fileId, token, type);
      unZipStandardFile(source);
      final String baseFileFromBase64 = getBaseFileFromBase64(source.resolveSibling(""));
      cleanResourceFile();
      return getEncodeFile(baseFileFromBase64);
    }
    return getBase64File(fileId, token, type);
  }
}
