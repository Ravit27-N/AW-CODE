package com.tessi.cxm.pfl.ms8.service;

import com.tessi.cxm.pfl.ms8.config.LocalFileConfig;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponseDto;
import com.tessi.cxm.pfl.ms8.exception.Base64NotSupporterException;
import com.tessi.cxm.pfl.ms8.util.BackgroundFileUtil;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.utils.AttachmentFileValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.ResourceFileType;
import com.tessi.cxm.pfl.shared.utils.BackgroundFileValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.EnrichmentMailing;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import com.tessi.cxm.pfl.shared.utils.ResourceValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.SignatureFileValidator;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

  private static final String PDF_EXTENSION = "pdf";
  private final FileService fileService;
  private final LocalFileConfig localFileConfig;
  private final ModelMapper modelMapper;

  @Autowired
  public void setPrivilegeValidationUtil(ProfileFeignClient profileFeignClient) {
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
  }

  /**
   * Upload background file to local storage.
   *
   * @param multipartFile - object of {@link MultipartFile}.
   * @param flowId        - value of {@link String}.
   * @return - object of {@link ResourceFileMetaDataResponseDto}.
   */
  public ResourceFileMetaDataResponseDto uploadResource(
      MultipartFile multipartFile, String flowId, String type) {
    // Apply privileges.
    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_ENRICHMENT_MAILING,
        EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE,
        false,
        false);

    // Apply validation for background file.
    AtomicInteger numberOfPages = new AtomicInteger(0);
    ResourceType resourceType = ResourceType.resourceContain(type);

    if (resourceType.equals(ResourceType.ATTACHMENT)) {
      //get Number of page and validation Pdf
      ResourceValidatorUtil.validateSizeOfFile(multipartFile, "20MB");
      numberOfPages.getAndSet(AttachmentFileValidatorUtil.getNumberOfPages(multipartFile));
    } else if (resourceType.equals(ResourceType.BACKGROUND)) {
      //get Number of page and validation Pdf
      numberOfPages.getAndSet(
          BackgroundFileValidatorUtil.getNumberOfPagesWithValidation(multipartFile, 1));
      ResourceValidatorUtil.validateSizeOfFile(multipartFile, "20MB");
    } else if (resourceType.equals(ResourceType.SIGNATURE)) {
      SignatureFileValidator.validateExtension(multipartFile);
      SignatureFileValidator.validateSizeOfFile(multipartFile, "1MB");
      SignatureFileValidator.validatePNGFile(multipartFile);

    }

    // Generate file id.
    String fileId = UUID.randomUUID().toString();

    // Concatenate directory path.
    String dir = Path.of(localFileConfig.getPath()).resolve(flowId).toString();

    // Upload background file to storage.
    final FilePropertiesHandling fileProperties = this.internalUpload(multipartFile, dir, fileId);

    return this.mappingResponse(multipartFile, fileId, flowId, fileProperties, numberOfPages.get());
  }

  private ResourceFileMetaDataResponseDto mappingResponse(
      MultipartFile multipartFile,
      String fileId,
      String flowId,
      FilePropertiesHandling filePropertiesHandling,
      int numberOfPages) {
    final ResourceFileMetaDataResponseDto responseDto =
        this.modelMapper.map(filePropertiesHandling, ResourceFileMetaDataResponseDto.class);

    responseDto.setOriginalName(multipartFile.getOriginalFilename());
    responseDto.setFileId(fileId);
    responseDto.setBase64(this.getBase64(multipartFile));
    responseDto.setExtension(filePropertiesHandling.getExtension());
    responseDto.setNumberOfPages(numberOfPages);
    responseDto.setType(ResourceFileType.ONE_TIME_UPLOAD.getValue());
    responseDto.setFlowId(flowId);
    return responseDto;
  }

  /**
   * Validate background file {@link MultipartFile}. It will throw exception when background not
   * valid.
   *
   * @param multipartFile - object of {@link MultipartFile}.
   */
  private void applyingValidation(MultipartFile multipartFile, AtomicInteger numberOfPages) {
    BackgroundFileValidatorUtil.checkPDFFile(multipartFile);
    final int numberOfPage =
        BackgroundFileValidatorUtil.getNumberOfPagesWithValidation(multipartFile, 1);
    numberOfPages.getAndSet(numberOfPage);
  }

  /**
   * Get base64 form {@link MultipartFile}.
   *
   * @param multipartFile - object of {@link MultipartFile}
   * @return - base64 {@link String}.
   */
  private String getBase64(MultipartFile multipartFile) {
    try {
      return Base64.getEncoder().encodeToString(multipartFile.getBytes());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new Base64NotSupporterException("Fail to convert file to base64");
    }
  }

  /**
   * Update {@link MultipartFile} to local storage.
   *
   * @param multipartFile - object of {@link MultipartFile}.
   * @param dir           - directory path {@link String}.
   * @return - object of {@link FilePropertiesHandling}
   */
  private FilePropertiesHandling internalUpload(
      MultipartFile multipartFile, String dir, String fileId) {
    String filename =
        fileId.concat("_".concat(Objects.requireNonNull(multipartFile.getOriginalFilename())));

    return this.fileService.uploadSingleFile(multipartFile, true, filename, dir);
  }

  public void removeFile(String fileId, String flowId) {
    // Apply privilege.
    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_ENRICHMENT_MAILING,
        EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE,
        false,
        false);

    try {
      // Concatenate directory path.
      Path dir = Path.of(localFileConfig.getPath()).resolve(flowId);
      BackgroundFileUtil.deleteFileWithPrefix(dir, fileId.concat("_"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
