package com.tessi.cxm.pfl.ms8.service;

import static java.util.stream.Collectors.groupingBy;

import com.tessi.cxm.pfl.ms8.config.LocalFileConfig;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataRequestDto;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponse;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponseDto;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponseItem;
import com.tessi.cxm.pfl.ms8.dto.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.exception.BackgroundFileDuplicatedException;
import com.tessi.cxm.pfl.ms8.exception.BackgroundFileJDBCException;
import com.tessi.cxm.pfl.ms8.exception.BackgroundFileNotFoundException;
import com.tessi.cxm.pfl.ms8.exception.FileNotFoundException;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.ms8.util.BackgroundFileUtil;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.FileMetadata;
import com.tessi.cxm.pfl.shared.model.FileStatusDto;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.AttachmentPosition;
import com.tessi.cxm.pfl.shared.utils.BackgroundFileValidatorUtil;
import com.tessi.cxm.pfl.shared.utils.BackgroundPosition;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.ClientSettingCriteriaDistributionValidationUtil;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.EnrichmentMailing;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.FlowDepositArea;
import com.tessi.cxm.pfl.shared.utils.ResourceFileType;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ResourceFileService {

  private static final String BACKGROUND_FILE_NOT_ACCEPTABLE =
      "Background file size must be equal or smaller than 3";
  private static final String BACKGROUND_FILE_DUPLICATED = "Background file is duplicated";
  private static final String ATTACHMENT_FILE_DUPLICATED = "Attachment file is duplicated";
  private static final String SIGNATURE_FILE_DUPLICATED = "Signature file is duplicated";
  private final FileManagerResource fileManagerResource;
  private final ResourceFileRepository resourceFileRepository;
  private final ModelMapper modelMapper;
  private final LocalFileConfig localFileConfig;
  private final ProfileFeignClient profileFeignClient;
  private final SettingFeignClient settingFeignClient;

  public ResourceFileService(
      FileManagerResource fileManagerResource,
      ResourceFileRepository resourceFileRepository,
      ModelMapper modelMapper,
      LocalFileConfig localFileConfig,
      ProfileFeignClient profileFeignClient,
      SettingFeignClient settingFeignClient) {
    this.fileManagerResource = fileManagerResource;
    this.resourceFileRepository = resourceFileRepository;
    this.modelMapper = modelMapper;
    this.localFileConfig = localFileConfig;
    this.profileFeignClient = profileFeignClient;
    this.settingFeignClient = settingFeignClient;
    ClientSettingCriteriaDistributionValidationUtil.setSettingFeignClient(settingFeignClient);
  }

  @Autowired
  public void setPrivilegeValidationUtil(ProfileFeignClient profileFeignClient) {
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
  }

  /**
   * Update background file in database and modified file.
   *
   * @param dto - object of {@link ResourceFileMetaDataResponseDto}.
   * @return - object of {@link ResourceFileMetaDataResponseDto}.
   */
  @Transactional(rollbackFor = Exception.class)
  public ResourceFileMetaDataRequestDto update(ResourceFileMetaDataRequestDto dto) {

    ResourceFile resourceFile =
        this.resourceFileRepository.findById(dto.getId()).orElse(new ResourceFile());
    if (resourceFile.getId().equals(0L)) {
      return dto;
    }

    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_ENRICHMENT_MAILING,
        getPrivKey(dto.getSource(), false, resourceFile.isDefault()),
        false,
        false);

    if (!resourceFile.getPosition().equalsIgnoreCase(dto.getPosition())) {
      this.applyValidation(
          dto.getSource(), dto.getPosition(), resourceFile.getFlowId(), dto.getType());
    }
    if (dto.getSource().equals(ResourceFileType.ONE_TIME_UPLOAD.getValue())) {
      // Remove a old background file in file-manager.
      // deleteFileOnFileManager(resourceFile);
      // Modified background file.
      this.createOrModifiedBackgroundFile(dto, resourceFile);
    } else {
      // Mapping dto to entity.
      this.mapDtoToEntity(dto, resourceFile);
      dto.setId(this.createOrUpdateResourceLibrary(resourceFile).getId());
    }

    return dto;
  }

  private void deleteFileOnFileManager(ResourceFile resourceFile) {
    try {
      fileManagerResource.deleteMetadataFile(
          resourceFile.getFileId(),
          ProfileConstants.CXM_ENRICHMENT_MAILING,
          EnrichmentMailing.MODIFY_RESOURCE_DEFAULT,
          getToken());
    } catch (Exception e) {
      log.error("", e);
    }
  }

  /**
   * Save background file to database & upload to file-manager.
   *
   * @param dto - object of {@link ResourceFileMetaDataResponseDto}.
   * @return - object of {@link ResourceFileMetaDataResponseDto}.
   */
  @Transactional(rollbackFor = Exception.class)
  public ResourceFileMetaDataRequestDto save(ResourceFileMetaDataRequestDto dto) {
    // Apply privileges.
    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_ENRICHMENT_MAILING, getPrivKey(dto.getSource(), true), false, false);

    // Apply validation.
    if (ResourceType.contain(dto.getType())
        .equals(ResourceType.SIGNATURE.getValue())) {
      //validate for signature
      this.applyValidationSignature(dto.getSource(), dto.getFlowId(), dto.getType());
    } else {
      //validate for Attachment and background
      this.applyValidation(dto.getSource(), dto.getPosition(), dto.getFlowId(), dto.getType());
    }

    ResourceFile entity = new ResourceFile();
    if (ResourceFileType.ONE_TIME_UPLOAD.getValue().equals(dto.getSource())) {
      // Create background file.
      this.createOrModifiedBackgroundFile(dto, new ResourceFile());
    } else {
      this.mapDtoToEntity(dto, entity);
      dto.setId(this.createOrUpdateResourceLibrary(entity).getId());
    }
    return dto;
  }

  /**
   * Get background file by using id {@link String}. It will throw exception when id not found.
   *
   * @param id - value of {@link String}.
   * @return - object of {@link ResourceFileMetaDataResponseDto}.
   */
  @Transactional(rollbackFor = Exception.class)
  public ResourceFileMetaDataResponseDto getResourceFile(long id) {
    var entity =
        this.resourceFileRepository
            .findById(id)
            .orElseThrow(() -> new BackgroundFileNotFoundException("Resource file not found"));

    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_ENRICHMENT_MAILING,
        EnrichmentMailing.MODIFY_RESOURCE_CUSTOMER,
        true,
        entity.getOwnerId());
    // Get file metadata from file-manager.
    final FileMetadata fileMetadata = this.getFileMetaData(entity, this.getToken());

    // Mapping entity to dto.
    var dto = this.modelMapper.map(entity, ResourceFileMetaDataResponseDto.class);
    dto.setBase64(fileMetadata.getContent());
    dto.setFileSize(fileMetadata.getSize());
    dto.setSource(entity.getSource());
    return dto;
  }

  /**
   * Delete background file in a database.
   *
   * @param resourceFile - object of {@link ResourceFile}.
   */
  @Transactional(rollbackFor = Exception.class)
  public void deleteResourceFile(ResourceFile resourceFile) {
    try {
      this.resourceFileRepository.delete(resourceFile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new BackgroundFileJDBCException("Fail to remove background file");
    }
  }

  /**
   * Create or update background file of type (ONE_TIME_UPLOAD).
   *
   * @param dto          - object of {@link ResourceFile}.
   * @param resourceFile - object of {@link ResourceFile}
   */
  private void createOrModifiedBackgroundFile(
      ResourceFileMetaDataRequestDto dto, ResourceFile resourceFile) {
    // Mapping dto to entity.
    this.mapDtoToEntity(dto, resourceFile);

    // Get a new background file from local storage.
    var multipartFile = this.getResourceFile(dto);

    // Upload a new background file to file manager.
    this.uploadToFileManager(multipartFile, dto.getFileId(), this.getToken());
    // Mapping file properties to entity.
    this.mappingFilePropertiesToEntity(resourceFile, multipartFile, dto.getType());
    // Save to database.
    dto.setId(this.resourceFileRepository.save(resourceFile).getId());
  }

  /**
   * Mapping dto of {@link ResourceFileMetaDataRequestDto} to entity of {@link ResourceFile}.
   *
   * @param dto          - object of {@link ResourceFile}.
   * @param resourceFile - object of {@link ResourceFile}
   */
  private void mapDtoToEntity(ResourceFileMetaDataRequestDto dto, ResourceFile resourceFile) {
    if (Objects.isNull(dto.getId())) {
      resourceFile.setId(null);
    }
    if (Objects.isNull(resourceFile.getOwnerId())) {
      resourceFile.setOwnerId(this.getUserDetail(this.getToken()).getOwnerId());
    }
    resourceFile.setFileId(dto.getFileId());
    resourceFile.setFlowId(dto.getFlowId());
    resourceFile.setSource(dto.getSource());
    resourceFile.setType(dto.getType());
    resourceFile.setPosition(dto.getPosition());
  }

  /**
   * Create or update background file of type (Resource Library).
   *
   * @param resourceFile - object of {@link ResourceFile}.
   * @return - object of {@link ResourceFile}.
   */
  private ResourceFile createOrUpdateResourceLibrary(ResourceFile resourceFile) {
    ResourceLibraryDto resource =
        settingFeignClient.getResource(resourceFile.getFileId(), this.getToken());
    resourceFile.setOriginalName(resource.getLabel());
    resourceFile.setExtension(FilenameUtils.getExtension(resource.getFileName()));
    resourceFile.setNumberOfPages(resource.getPageNumber());
    return this.resourceFileRepository.save(resourceFile);
  }

  private String getPrivKey(String type, boolean isCreate) {
    if (isCreate) {
      String privKey = EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE;
      if (type.equals(ResourceFileType.LIBRARY.getValue())) {
        privKey = EnrichmentMailing.USE_RESOURCE_IN_LIBRARY;
      }
      return privKey;
    } else {
      return EnrichmentMailing.MODIFY_RESOURCE_CUSTOMER;
    }
  }

  private String getPrivKey(String type, boolean isCreate, boolean isDefault) {
    if (isCreate) {
      String privKey = EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE;
      if (type.equals(ResourceFileType.LIBRARY.getValue())) {
        privKey = EnrichmentMailing.USE_RESOURCE_IN_LIBRARY;
      }
      return privKey;
    } else {
      if(isDefault){
        return EnrichmentMailing.MODIFY_RESOURCE_DEFAULT;
      }else{
        return  EnrichmentMailing.MODIFY_RESOURCE_CUSTOMER;
      }
    }
  }

  private void mappingFilePropertiesToEntity(ResourceFile entity, MultipartFile multipartFile,
      String type) {
    entity.setOriginalName(splitOriginalFilename(entity.getFileId(), multipartFile));
    entity.setExtension(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
    ResourceType resourceType = ResourceType.resourceContain(type);
    if (resourceType.equals(ResourceType.BACKGROUND) || resourceType.equals(
        ResourceType.ATTACHMENT)) {
      entity.setNumberOfPages(BackgroundFileValidatorUtil.getNumberOfPages(multipartFile));
    } else {
      //signature is image
      entity.setNumberOfPages(0);
    }

  }

  private String splitOriginalFilename(String fileId, MultipartFile multipartFile) {
    return Objects.requireNonNull(multipartFile.getOriginalFilename())
        .replaceAll(fileId.concat("_"), "");
  }

  private UserDetail getUserDetail(String token) {
    return this.profileFeignClient.getUserDetail(token);
  }

  private void applyValidation(String source, String position, String flowId, String type) {

    //validate resource file type
    ResourceFileType.checkingValue(source);
    //validate resource type
    ResourceType resource = ResourceType.resourceContain(type);

    //validate type
    if (resource.equals(ResourceType.ATTACHMENT)) {
      AttachmentPosition.checkingValue(position);
      //validate resourcePosition
    } else if (resource.equals(ResourceType.BACKGROUND)) {
      BackgroundPosition.checkingValue(position);
      //validate resourcePosition
    }

    this.validateResourcePosition(flowId, position, resource);
  }

  /**
   * Get background file from local storage.
   *
   * @param dto - object of {@link ResourceFileMetaDataRequestDto}.
   * @return - object of {@link MultipartFile}.
   */
  private MultipartFile getResourceFile(ResourceFileMetaDataRequestDto dto) {
    try {
      Path path = Paths.get(localFileConfig.getPath()).resolve(dto.getFlowId());
      return BackgroundFileUtil.findMultipartFileWithPrefix(path, dto.getFileId().concat("_"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new FileNotFoundException("Fail to get internal background file", e);
    }
  }

  /**
   * Upload file to file-manager.
   *
   * @param multipartFile - object of {@link MultipartFile}.
   * @param fileId        - value of {@link String}.
   * @param token         - authorization header {@link String}.
   */
  private void uploadToFileManager(MultipartFile multipartFile, String fileId, String token) {
    this.fileManagerResource.uploadFile(
        multipartFile,
        this.getUserDetail(token).getOwnerId(),
        fileId,
        null,
        null,
        30,
        false,
        ProfileConstants.CXM_ENRICHMENT_MAILING,
        EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE,
        token);
  }

  /**
   * Get file metadata from file-manager.
   *
   * @param token - authorization token {@link String}.
   * @return - base64 content {@link String}.
   */
  private FileMetadata getFileMetaData(ResourceFile resourceFile, String token) {
    try {
      if (resourceFile.isDefault()) {
        String base64 =
            this.fileManagerResource.getResourceFile(
                resourceFile.getFileId().concat(".".concat(resourceFile.getExtension())), token);
        return FileMetadata.builder().content(base64).build();
      }
      return this.fileManagerResource.getFile(
          resourceFile.getFileId(),
          ProfileConstants.CXM_ENRICHMENT_MAILING,
          EnrichmentMailing.MODIFY_RESOURCE_DEFAULT,
          token);
    } catch (Exception e) {
      log.error("Unable to get resource file content: {}", e.getMessage());
      log.error("", e);
      return FileMetadata.builder().content("").size(0).build();
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteResourceFile(long id) {

    ResourceFile resourceFile = this.resourceFileRepository.findById(id).orElse(new ResourceFile());
    if (resourceFile.getId().equals(0L)) {
      return;
    }

    PrivilegeValidationUtil.getUserPrivilegeDetails(
        ProfileConstants.CXM_ENRICHMENT_MAILING,
        getPrivKey(resourceFile.getSource(), false, resourceFile.isDefault()),
        false,
        false);

    if (resourceFile.isDefault()) {
      PrivilegeValidationUtil.validateUserAccessPrivilege(
          ProfileConstants.CXM_ENRICHMENT_MAILING,
          EnrichmentMailing.DELETE_RESOURCE_DEFAULT,
          true,
          resourceFile.getOwnerId());

      this.deleteResourceFile(resourceFile);
    } else {

      // check privilege
      PrivilegeValidationUtil.validateUserAccessPrivilege(
          ProfileConstants.CXM_ENRICHMENT_MAILING,
          EnrichmentMailing.DELETE_RESOURCE_CUSTOMER,
          true,
          resourceFile.getOwnerId());

      if (ResourceFileType.ONE_TIME_UPLOAD.getValue().equals(resourceFile.getType())) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
              @Override
              public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                  if (!resourceFile.isDefault()) {
                    deleteFileOnFileManager(resourceFile.getFileId(), getToken());
                  }
                }
              }
            });
      }
    }
  }

  /**
   * Delete file in file manager.
   *
   * @param fileId - value of {@link String}.
   * @param token  - authorization token {@link String}.
   */
  private void deleteFileOnFileManager(String fileId, String token) {
    try {
      this.fileManagerResource.deleteMetadataFile(
          fileId, ProfileConstants.CXM_FLOW_DEPOSIT, FlowDepositArea.MODIFY_A_DEPOSIT, token);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * Delete a directory or file by file path.
   */
  private void internalDeleteFile(ResourceFile resourceFile) {
    try {
      Path path = Paths.get(localFileConfig.getPath()).resolve(resourceFile.getFlowId());
      FileUtils.forceDelete(path.toFile());
//      BackgroundFileUtil.deleteFileWithPrefix(path, resourceFile.getFileId().concat("_"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private String getToken() {
    return BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken());
  }

  @Transactional(readOnly = true)
  public boolean checkResourceIsMissing(String flowId) {
    var resourceFiles = this.resourceFileRepository.findAllByFlowId(flowId);
    if (resourceFiles.isEmpty()) {
      return false;
    }
    var filenames = resourceFiles.stream().map(ResourceFile::getOriginalName);
    var fileIds =
        resourceFiles.stream()
            .map(
                backgroundFile ->
                    backgroundFile.getFileId().concat(".".concat(backgroundFile.getExtension())));
    List<FileStatusDto> fileExists =
        this.fileManagerResource
            .checkFileExists(Stream.concat(filenames, fileIds).toArray(String[]::new))
            .stream()
            .filter(FileStatusDto::isExist)
            .collect(Collectors.toList());
    List<ResourceFileMetaDataResponseItem> resourceFileMetaDataResponseItems =
        resourceFiles.stream()
            .map(
                resourceFile -> {
                  var obj =
                      this.modelMapper.map(resourceFile, ResourceFileMetaDataResponseItem.class);
                  if (fileExists.stream()
                      .filter(
                          fileStatusDto ->
                              fileStatusDto.getFilename().equals(resourceFile.getOriginalName())
                                  || fileStatusDto
                                  .getFilename()
                                  .equals(
                                      resourceFile
                                          .getFileId()
                                          .concat(".".concat(resourceFile.getExtension()))))
                      .findFirst()
                      .isEmpty()) {
                    obj.setMissing(true);
                  }
                  return obj;
                })
            .collect(Collectors.toList());
    return resourceFileMetaDataResponseItems.stream()
        .anyMatch(ResourceFileMetaDataResponseItem::isMissing);
  }

  @Transactional(readOnly = true)
  public ResourceFileMetaDataResponse getResourceFilesByFlowId(String flowId) {
    var backgroundFiles = this.resourceFileRepository.findAllByFlowId(flowId);

    var filenames = backgroundFiles.stream().map(ResourceFile::getOriginalName);

    var fileIds =
        backgroundFiles.stream()
            .map(
                backgroundFile ->
                    backgroundFile.getFileId().concat(".".concat(backgroundFile.getExtension())));

    List<FileStatusDto> fileExists =
        this.fileManagerResource
            .checkFileExists(Stream.concat(filenames, fileIds).toArray(String[]::new))
            .stream()
            .filter(FileStatusDto::isExist)
            .collect(Collectors.toList());

    List<String> types = Arrays.stream(ResourceType.values()).map(ResourceType::getValue).collect(
        Collectors.toList());

    // Mapping entities to dto.
    ResourceFileMetaDataResponse dto = new ResourceFileMetaDataResponse();

    // Initialized metadata.
    types.forEach(
        type -> dto.add(ResourceType.contain(type), new ArrayList<>()));

    // Prefill metadata.
    backgroundFiles.stream().collect(groupingBy(ResourceFile::getType))
        .forEach((s, resourceFiles) -> {
          var resourceFileList = resourceFiles.stream()
              .map(resourceFile -> {
                    var obj = this.modelMapper.map(resourceFile,
                        ResourceFileMetaDataResponseItem.class);

                    if (fileExists.stream()
                        .filter(
                            fileStatusDto ->
                                fileStatusDto.getFilename().equals(resourceFile.getOriginalName())
                                    || fileStatusDto
                                    .getFilename()
                                    .equals(
                                        resourceFile
                                            .getFileId()
                                            .concat(".".concat(resourceFile.getExtension()))))
                        .findFirst()
                        .isEmpty()) {
                      obj.setMissing(true);
                    }

                    return obj;
                  }
              ).collect(Collectors.toList());
          List<ResourceFileMetaDataResponseItem> resourceFileOrder = new ArrayList<>();
          if (ResourceType.BACKGROUND.getValue().equalsIgnoreCase(s)) {
            resourceFileOrder = Arrays.stream(
                    BackgroundPosition.values())
                .flatMap(
                    position ->
                        resourceFileList.stream()
                            .filter(bfR -> position.value.equalsIgnoreCase(bfR.getPosition())))
                .collect(Collectors.toList());
          } else if (ResourceType.ATTACHMENT.getValue().equalsIgnoreCase(s)) {
            resourceFileOrder = Arrays.stream(
                    AttachmentPosition.values())
                .flatMap(
                    position ->
                        resourceFileList.stream()
                            .filter(bfR -> position.value.equalsIgnoreCase(bfR.getPosition())))
                .collect(Collectors.toList());
          } else if (ResourceType.SIGNATURE.getValue().equalsIgnoreCase(s) && !resourceFileList.isEmpty()) {
            resourceFileOrder.add(resourceFileList.get(0));
          }
          dto.add(ResourceType.contain(s), resourceFileOrder);
        });

    return dto;
  }

  private void validateResourcePosition(String flowId, String position, ResourceType resource) {
    //get background by flowId
    List<ResourceFile> resourceFiles = this.resourceFileRepository.findAllByFlowId(flowId);

    if (resourceFiles.isEmpty()) {
      return;
    }
    Map<String, List<ResourceFile>> resouceMap = resourceFiles.stream()
        .collect(groupingBy(ResourceFile::getType));
    List<ResourceFile> backgroundResource = resouceMap.get(ResourceType.BACKGROUND.getValue());
    // get existPosition
    List<String> existPosition =
        resourceFiles.stream().map(ResourceFile::getPosition).collect(Collectors.toList());
    if (resource.equals(ResourceType.ATTACHMENT) && existPosition.contains(position)) {
      throw new BackgroundFileDuplicatedException(ATTACHMENT_FILE_DUPLICATED);
    } else if (resource.equals(ResourceType.BACKGROUND) && !CollectionUtils.isEmpty(
        backgroundResource) && backgroundResource.size() > 1) {
      if (existPosition.contains(BackgroundPosition.ALL_PAGES.value)
          || existPosition.contains(position)) {
        throw new BackgroundFileDuplicatedException(BACKGROUND_FILE_DUPLICATED);
      }
    }
  }

  @Transactional(readOnly = true)
  public List<ResourceFile> getResourceBackground(String flowId) {
    return this.resourceFileRepository.findAllByFlowId(flowId);
  }

  public void deleteResourceFileByFlowId(String flowId) {
    List<ResourceFile> resourceFiles = this.resourceFileRepository.findAllByFlowId(flowId);
    try {
      Path path = Paths.get(localFileConfig.getPath(), flowId);
      FileUtils.forceDelete(path.toFile());
    } catch (Exception e) {
      log.error("{0}", e);
    }
    this.resourceFileRepository.deleteAll(resourceFiles);
  }

  private void applyValidationSignature(String source, String flowId, String type) {

    ResourceFileType.checkingValue(source);
    var resourceFile = this.resourceFileRepository.findFirstByFlowIdAndTypeIgnoreCase(flowId, type);
    //validate resource file type

    if (resourceFile.isPresent()) {
      throw new BackgroundFileDuplicatedException(SIGNATURE_FILE_DUPLICATED);
    }
  }
}
