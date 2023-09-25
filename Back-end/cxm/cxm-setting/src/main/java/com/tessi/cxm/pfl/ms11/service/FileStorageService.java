package com.tessi.cxm.pfl.ms11.service;

import com.tessi.cxm.pfl.ms11.config.Go2pdfFileConfig;
import com.tessi.cxm.pfl.shared.model.ResourceLibraryDto;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.exception.FileNotFoundException;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.service.storage.FileServiceImpl;
import com.tessi.cxm.pfl.shared.utils.AttachmentFileValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.BackgroundFileValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import com.tessi.cxm.pfl.shared.utils.ResourceValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.SignatureFileValidator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import javax.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileStorageService {

  private final String pdfExtension = "pdf";
  private final String pngExtension = "png";
  private final FileService fileService;
  private final FileService portalFileService;

  public FileStorageService(FileService fileService, Go2pdfFileConfig localFileConfig) {
    this.fileService = fileService;
    this.portalFileService = new FileServiceImpl(localFileConfig.getPath(), "");
  }

  public String getFilename(String uuid, String extention) {
    return uuid.concat(FilenameUtils.EXTENSION_SEPARATOR + extention);
  }

  public String getBase64File(String fileId, String extension) {
    Path filePath = this.fileService.getPath().resolve(this.getFilename(fileId, extension));
    if (!filePath.toFile().exists()) {
      log.error("File not found : {}", fileId);
      throw new FileNotFoundException("File not found : " + fileId);
    }
    return this.fileService.encodeFileToBase64(filePath.toString());
  }

  public void deleteFile(String uuid, String extension) {
    this.fileService.delete(this.getFilename(uuid, extension));
  }

  public void deleteTemporaryFile(String fileId) {
    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
        ProfileConstants.Privilege.CREATE,
        true,
        true);
    try {
      deleteFile(fileId, pdfExtension);
    } catch (Exception ignored) {
      log.error("File not found :{}", fileId);
    }
  }

  public ResourceLibraryDto storeFile(MultipartFile file, String type) {
    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
        ProfileConstants.Privilege.CREATE,
        true,
        true);

    ResourceType resourceType = ResourceType.resourceContain(type);
    //count max page
    int maxPageCount = 0;
    final String uuid = UUID.randomUUID().toString();
    String filename = "";

    if (resourceType.equals(ResourceType.ATTACHMENT)) {
      //get Number of page and validation Pdf
      maxPageCount = AttachmentFileValidatorUtil.getNumberOfPages(file);
      ResourceValidatorUtil.validateSizeOfFile(file, "20MB");
      filename = getFilename(uuid, pdfExtension);
    } else if (resourceType.equals(ResourceType.BACKGROUND)) {
      //get Number of page and validation Pdf
      maxPageCount = BackgroundFileValidatorUtil.getNumberOfPagesWithValidation(file, 1);
      ResourceValidatorUtil.validateSizeOfFile(file, "20MB");
      filename = getFilename(uuid, pdfExtension);
    } else if (resourceType.equals(ResourceType.SIGNATURE)) {
      SignatureFileValidator.validateExtension(file);
      SignatureFileValidator.validatePNGFile(file);
      SignatureFileValidator.validateSizeOfFile(file, "1MB");
      filename = getFilename(uuid, pngExtension);
    }

    if (!FilenameUtils.isExtension(file.getOriginalFilename(), pdfExtension) && (
        resourceType.equals(ResourceType.BACKGROUND) || resourceType.equals(
            ResourceType.ATTACHMENT))) {
      String newFilename =
          Objects.requireNonNull(file.getOriginalFilename())
              .replace(FilenameUtils.getExtension(file.getOriginalFilename()), pdfExtension);
      final String contentType = new MimetypesFileTypeMap().getContentType(newFilename);
      try {
        file = fileService.fileToMultipartFile(file.getInputStream(), contentType, newFilename);
      } catch (IOException e) {
        log.error("File error: ", e);
        throw new FileErrorException("Fail to store file :" + file.getOriginalFilename());
      }
    }

    FilePropertiesHandling filePropertiesHandling =
        fileService.uploadSingleFile(file, false, filename);
    return ResourceLibraryDto.builder()
        .fileId(uuid)
        .fileName(file.getOriginalFilename())
        .fileSize(filePropertiesHandling.getFileSize())
        .pageNumber(maxPageCount)
        .build();
  }

  public String getIniFile(String customer) {
    Path iniPath = this.portalFileService.getPath(customer).resolve("config.ini");
    if (!iniPath.toFile().exists()) {
      return null;
    }
    return this.fileService.encodeFileToBase64(iniPath.toString());
  }
}
