package com.tessi.cxm.pfl.ms11.service;

import com.tessi.cxm.pfl.ms11.config.LocalFileConfig;
import com.tessi.cxm.pfl.ms11.constant.Language;
import com.tessi.cxm.pfl.shared.model.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms11.dto.ResourceParam;
import com.tessi.cxm.pfl.ms11.entity.ResourceLibrary;
import com.tessi.cxm.pfl.ms11.entity.ResourceLibrary_;
import com.tessi.cxm.pfl.ms11.entity.ResourceTypeTranslate_;
import com.tessi.cxm.pfl.ms11.exception.ResourceLibraryNotFoundException;
import com.tessi.cxm.pfl.ms11.repository.ResourceLibraryRepository;
import com.tessi.cxm.pfl.ms11.service.specification.ResourceLibrarySpecification;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.exception.ResourceNotSupportException;
import com.tessi.cxm.pfl.shared.exception.ResourceTypeNotFoundException;
import com.tessi.cxm.pfl.shared.model.FileMetadata;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.AttachmentFileValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.BackgroundFileValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.FileExtensionUtils;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import com.tessi.cxm.pfl.shared.utils.ResourceStatusCode;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import com.tessi.cxm.pfl.shared.utils.ResourceValidatorUtil;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link ResourceLibraryService} to handle business service of <strong>Setting Resource</strong>.
 *
 * @author Sokhour LACH
 * @version 1.9.0
 * @since 30/01/2023
 */
@Slf4j
@Service
@Transactional
public class ResourceLibraryService
    extends AbstractCrudService<ResourceLibraryDto, ResourceLibrary, Long> {

  private final ResourceLibraryRepository resourceLibraryRepository;
  private final FileManagerResource fileManagerResource;
  private final FileStorageService fileStorageService;

  private final LocalFileConfig localFileConfig;

  public ResourceLibraryService(
      ResourceLibraryRepository resourceLibraryRepository,
      ModelMapper modelMapper,
      ProfileFeignClient profileFeignClient,
      FileManagerResource fileManagerResource,
      FileStorageService fileStorageService,
      LocalFileConfig localFileConfig) {
    super(modelMapper, profileFeignClient);
    this.fileManagerResource = fileManagerResource;
    this.fileStorageService = fileStorageService;
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
    this.resourceLibraryRepository = resourceLibraryRepository;
    this.localFileConfig = localFileConfig;
  }

  @Override
  @Transactional(readOnly = true)
  public ResourceLibraryDto findById(Long id) {
    var resourceLibrary =
        this.resourceLibraryRepository
            .findById(id)
            .map(this::mapData)
            .orElseThrow(() -> new ResourceLibraryNotFoundException(id));

    // validate the privilege of the current user invoked.
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
        Privilege.LIST,
        false,
        resourceLibrary.getOwnerId());

    return resourceLibrary;
  }

  public ResourceLibraryDto getResourceByFileId(String fileId) {
    var resourceLibrary =
        this.resourceLibraryRepository
            .findByFileId(fileId)
            .map(this::mapData)
            .orElseThrow(() -> new ResourceLibraryNotFoundException(fileId));

    // validate the privilege of the current user invoked.
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
        Privilege.LIST,
        false,
        resourceLibrary.getOwnerId());

    return resourceLibrary;
  }

  /**
   * Get all resources with pagination and sorting.
   *
   * @param resourceParam {@code ResourceParam}
   * @return {@code ResourceLibraryDto}
   */
  @Transactional(readOnly = true)
  public Page<ResourceLibraryDto> findAll(ResourceParam resourceParam) {
    String language = null;
    if (resourceParam.getSortByField().equals("type")) {
      if (resourceParam.getLanguage().isEmpty()) {
        throw new BadRequestException("Language require for sort by type");
      }

      // set rt stand for resourceTypeTranslate
      resourceParam.setSortByField(
          ResourceLibrary_.RESOURCE_TYPE_TRANSLATE.concat("." + ResourceTypeTranslate_.TRANSLATE));
      language = Language.contain(resourceParam.getLanguage());
    }

    Pageable pageable =
        PageRequest.of(
            resourceParam.getPage(),
            resourceParam.getPageSize(),
            Sort.Direction.fromString(resourceParam.getSortDirection()),
            resourceParam.getSortByField());

    Specification<ResourceLibrary> querySpecs =
        buildResourceQuerySpecs(resourceParam.getFilter(), resourceParam.getTypes(), language);
    return this.resourceLibraryRepository.findAll(querySpecs, pageable).map(this::mapData);
  }

  /**
   * Get all resources with filtering and sorting.
   *
   * @param sort Sort option
   * @param filter Value to search or filter
   * @param types Resource types
   * @return List of resource
   */
  @Transactional(readOnly = true)
  public List<ResourceLibraryDto> findAll(Sort sort, String filter, List<String> types) {
    Specification<ResourceLibrary> querySpecs = buildResourceQuerySpecs(filter, types, null);
    return this.resourceLibraryRepository.findAll(querySpecs, sort).stream()
        .map(this::mapData)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResourceLibraryDto save(ResourceLibraryDto dto) {
    // validate the privilege of the current user invoked.
    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE, Privilege.CREATE, true, true);
    UserDetail userDetail = PrivilegeValidationUtil.getUserDetail();
    final long ownerId = userDetail.getOwnerId();
    final long clientId = userDetail.getClientId();

    String extension = "";
    // check and validate resource type.
    var type = ResourceType.contain(dto.getType());
    if (StringUtils.isEmpty(type)) {
      throw new ResourceTypeNotFoundException(dto.getType());
    }
    if (this.resourceLibraryRepository.existsByLabelIgnoreCaseAndClientIdAndType(
        dto.getLabel(), clientId, type)) {
      throw new BadRequestException("A resource with this name already exists.");
    }
    int pageSize = dto.getPageNumber();
    // get File
    Path path;
    // get file path and extension
    if (type.equals(ResourceType.BACKGROUND.getValue())
        || type.equals(ResourceType.ATTACHMENT.getValue())) {
      extension = FileExtensionUtils.PDF_EXTENSION;
      path =
          Paths.get(localFileConfig.getPath())
              .resolve(dto.getFileId().concat(".").concat(FileExtensionUtils.PDF_EXTENSION));
      File file = path.toFile();

      // validation file size
      ResourceValidatorUtil.validateSizeOfFile(file, "20MB");

      // validation
      if (type.equals(ResourceType.BACKGROUND.getValue())) {
        BackgroundFileValidatorUtil.getNumberOfPagesWithValidation(file, 1);
      } else {
        AttachmentFileValidatorUtil.validateFilePdf(file);
      }
    } else if (type.equals(ResourceType.SIGNATURE.getValue())) {
      // validate filename extension

      if (!FilenameUtils.getExtension(dto.getFileName()).equals(FileExtensionUtils.PNG_EXTENSION)) {
        throw new ResourceNotSupportException(
            ResourceStatusCode.FILE_NOT_SUPPORT.getReasonPhrase(
                FileExtensionUtils.PNG_EXTENSION, ResourceType.SIGNATURE));
      }
      extension = FileExtensionUtils.PNG_EXTENSION;
      path =
          Paths.get(localFileConfig.getPath())
              .resolve(dto.getFileId().concat(".").concat(FileExtensionUtils.PNG_EXTENSION));
      File file = path.toFile();
      // validation signature file size
      ResourceValidatorUtil.validateSizeOfFile(file, "1MB");
      pageSize = 1;
    }
    // get base64 file
    String base64 = fileStorageService.getBase64File(dto.getFileId(), extension);
    FileMetadata fileMetadata = uploadFileToFileManager(dto, ownerId, base64);

    var entity = this.mapEntity(dto);
    entity.setCreatedBy(this.getUsername());
    entity.setOwnerId(ownerId);
    entity.setClientId(clientId);
    entity.setType(type);
    entity.setFileSize(fileMetadata.getSize());
    entity.setPageNumber((long) pageSize);

    ResourceLibrary resourceLibrary = this.resourceLibraryRepository.save(entity);
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (STATUS_ROLLED_BACK == status) {
              final var adminToken = ResourceLibraryService.this.getAuthTokenWithPrefix();
              ResourceLibraryService.this.fileManagerResource.deleteMetadataFile(
                  dto.getFileId(),
                  ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
                  Privilege.CREATE,
                  adminToken);
            }
            if (STATUS_COMMITTED == status) {
              ResourceLibraryService.this.fileStorageService.deleteFile(
                  dto.getFileId(), FilenameUtils.getExtension(dto.getFileName()));
              log.info("Temporary file already deleted [{}]", dto.getFileId());
            }
          }
        });

    return this.mapData(resourceLibrary);
  }

  private FileMetadata uploadFileToFileManager(
      ResourceLibraryDto dto, long ownerId, String base64) {
    final String contentType = new MimetypesFileTypeMap().getContentType(dto.getFileName());
    FileMetadata fileMetadata =
        FileMetadata.builder()
            .idCreator(ownerId)
            .uuid(dto.getFileId())
            .fileName(dto.getFileName())
            .content(base64)
            .contentType(contentType)
            .size(dto.getFileSize())
            .build();
    return this.fileManagerResource
        .uploadFiles(
            Collections.singletonList(fileMetadata),
            ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
            Privilege.CREATE,
            this.getAuthTokenWithPrefix())
        .get(0);
  }

  public boolean checkDuplicateLabel(String label, String type) {
    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE, Privilege.CREATE, true, true);
    UserDetail userDetail = PrivilegeValidationUtil.getUserDetail();
    if (StringUtils.isEmpty(type) || Objects.isNull(ResourceType.contain(type))) {
      throw new ResourceTypeNotFoundException(type);
    }

    return this.resourceLibraryRepository.existsByLabelIgnoreCaseAndClientIdAndType(
        label, userDetail.getClientId(), type);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteResourceByFileId(String fileId, String token) {
    var resourceLibrary =
        this.resourceLibraryRepository
            .findByFileId(fileId)
            .orElseThrow(() -> new ResourceLibraryNotFoundException(fileId));
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
        Privilege.DELETE,
        false,
        resourceLibrary.getOwnerId());
    this.resourceLibraryRepository.delete(resourceLibrary);
    try {
      this.fileManagerResource.deleteMetadataFile(
          fileId, ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE, Privilege.DELETE, token);
    } catch (Exception ex) {
      log.warn("File not found {}", ex.getMessage());
    }
  }

  public String getFileContent(String fileId, String token) {
    var resourceLibrary =
        this.resourceLibraryRepository
            .findByFileId(fileId)
            .orElseThrow(() -> new ResourceLibraryNotFoundException(fileId));
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
        Privilege.LIST,
        true,
        resourceLibrary.getOwnerId());
    return this.fileManagerResource
        .getFile(fileId, ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE, Privilege.LIST, token)
        .getContent();
  }

  private Specification<ResourceLibrary> buildResourceQuerySpecs(
      String filter, List<String> types, String language) {
    // validate the privilege of the current user invoked.
    var ownIds =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
                ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE, Privilege.LIST, true, true)
            .getRelatedOwners();
    Specification<ResourceLibrary> querySpecs;
    if (StringUtils.isNotBlank(language)) {
      querySpecs =
          Specification.where(ResourceLibrarySpecification.joinTranslate(language, ownIds));
    } else {
      querySpecs = Specification.where(ResourceLibrarySpecification.containsOwnerIds(ownIds));
    }
    querySpecs = ResourceLibrarySpecification.containByFilter(filter, querySpecs);
    querySpecs = ResourceLibrarySpecification.containByTypes(types, querySpecs);
    return querySpecs;
  }

  public Map<String, String> getResourceTechnicalName(String fileId) {
    var resourceLibrary =
        this.resourceLibraryRepository
            .findByFileId(fileId)
            .map(this::mapData)
            .orElseThrow(() -> new ResourceLibraryNotFoundException(fileId));

    // validate the privilege of the current user invoked.
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
        Privilege.LIST,
        false,
        resourceLibrary.getOwnerId());
    FileMetadata file =
        this.fileManagerResource.getFile(
            fileId,
            ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE,
            Privilege.LIST,
            getAuthTokenWithPrefix());
    return Map.of("technicalName", file.getUuid().concat(".").concat(file.getExtension()));
  }

  public List<ResourceLibraryDto> findAll(List<String> fileIds) {
    // validate the privilege of the current user invoked.
    var ownIds =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
                ProfileConstants.CXM_MANAGEMENT_LIBRARY_RESOURCE, Privilege.LIST, true, true)
            .getRelatedOwners();

    Specification<ResourceLibrary> query =
        Specification.where(ResourceLibrarySpecification.containsOwnerIds(ownIds));
    query = query.and(ResourceLibrarySpecification.containFileIds(fileIds));

    return this.resourceLibraryRepository.findAll(query).stream()
        .map(this::mapData)
        .collect(Collectors.toList());
  }
}
