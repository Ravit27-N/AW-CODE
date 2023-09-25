package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.dto.FileMetadata;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserRepresentationNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.specification.ClientSpecification;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class StorageFileService implements AdminService {

  private final FileManagerResource fileManagerResource;
  private final ProfileService profileService;
  private final ClientRepository clientRepository;
  private final UserRepository userRepository;
  private final KeycloakService keycloakService;
  private final SettingFeignClient settingFeignClient;

  @Value("${cxm.user.admin-id}")
  private String adminUserId;
  @Value("${cxm.storage.attachment.max-files-size}")
  private String attachmentMaxFileSize;

  public StorageFileService(
          FileManagerResource fileManagerResource,
          ProfileService profileService,
          ClientRepository clientRepository,
          UserRepository userRepository,
          KeycloakService keycloakService, SettingFeignClient settingFeignClient) {
    this.fileManagerResource = fileManagerResource;
    this.profileService = profileService;
    this.clientRepository = clientRepository;
    this.userRepository = userRepository;
    this.keycloakService = keycloakService;
    this.settingFeignClient = settingFeignClient;
  }

  @Override
  public UserRepository getUserRepository() {
    return this.userRepository;
  }

  /**
   * To get metadata of privacy pdf file of the <strong>Client</strong>
   *
   * @return object of {@link FileMetadata}
   */
  public FileMetadata getFileContent() {
    var client =
        this.clientRepository
            .findOne(
                ClientSpecification.byUserTechnicalRefAndDeletedFalse(getPrincipalIdentifier()))
            .orElseThrow(
                () -> new UserRepresentationNotFoundException("User not found in keycloak server"));

    if (!StringUtils.hasText(client.getFileId())) {
      return null;
    }
    var keycloakAdminToken = BearerAuthentication.PREFIX_TOKEN.concat(
        this.keycloakService.getToken());
    var content = this.fileManagerResource.getFile(client.getFileId(), "", "",
        keycloakAdminToken);
    return FileMetadata.builder()
        .filename(client.getFilename())
        .content(content.getContent())
        .fileSize(client.getFileSize())
        .build();
  }

  /**
   * Upload single file.
   *
   * @return {@link FilePropertiesHandling}
   */
  public FilePropertiesHandling uploadSingleFile(MultipartFile file, String token) {
    handleException();
    this.validateFile(file);
    final UserEntity userEntity =
        this.userRepository
            .findByTechnicalRefAndIsActiveTrue(this.getPrincipalIdentifier())
            .orElseThrow(
                () ->
                    new UserNotFoundException(
                        "Please config user technical Reference for connect to the supper admin"));
    final com.tessi.cxm.pfl.shared.model.FileMetadata fileMetadata =
        this.fileManagerResource.uploadFile(
            file, userEntity.getId(), "", "", "", 0, false, "", "", token);
    FilePropertiesHandling filePropertiesHandling = new FilePropertiesHandling();
    filePropertiesHandling.setFileId(fileMetadata.getUuid());
    filePropertiesHandling.setFileName(fileMetadata.getFileName());
    filePropertiesHandling.setFileSize(fileMetadata.getSize());
    filePropertiesHandling.setExtension(fileMetadata.getExtension());
    return filePropertiesHandling;
  }

  /**
   * Validate that the file size does not exceed 5MB and that it is in PDF format.
   *
   * @param file reference object {@link MultipartFile}.
   */
  private void validateFile(MultipartFile file) {
    if (StringUtils.hasText(file.getOriginalFilename())
        && !FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("pdf")) {
      throw new FileErrorException("Only PDF file are accepted.");
    }
    final long fileSizes = file.getSize();
    final long maxFileSize = DataSize.parse(attachmentMaxFileSize).toBytes();
    if (fileSizes > maxFileSize) {
      throw new FileErrorException(
          "Upload error. The file size cannot exceed 5 MB.");
    }
  }

  /**
   * To delete file from the <strong>File manager</strong> by file id.
   *
   * @param fileId refer to id that generated from <strong>File manager</strong>
   */
  public void deleteFile(String fileId, String token) {
    handleException();

    if (!this.isAdmin()) {
      var client =
          this.clientRepository.findOne(
              Specification.where(ClientSpecification.equalFileId(fileId)));

      client.ifPresent(
          clientOfFile ->
              this.userRepository
                  .loadOrganizationUser(this.getPrincipalIdentifier())
                  .ifPresent(
                      currentUserOrganization -> {
                        if (currentUserOrganization.getClientId() != clientOfFile.getId()) {
                          throw new UserAccessDeniedExceptionHandler();
                        }
                      }));
    }

    this.fileManagerResource.deleteMetadataFile(fileId, "", "", token);
  }

  @Override
  public String getConfiguredUserAdminId() {
    return adminUserId;
  }

  private void handleException() {
    if (!this.isAdmin()
        && profileService.notContainsPrivilege(
        ProfileConstants.CXM_CLIENT_MANAGEMENT,
        ProfileConstants.CXM_CLIENT_MANAGEMENT.concat("_").concat(Privilege.MODIFY))) {
      throw new UserAccessDeniedExceptionHandler();
    }
  }

  protected String getPrincipalIdentifier() {
    return AuthenticationUtils.getPrincipalIdentifier();
  }

  @Override
  public boolean isUserAdministrator(String principalId) {
    try {
      var user = this.userRepository.findByTechnicalRefAndIsActiveTrue(principalId);
      return user.map(UserEntity::isAdmin).orElse(false);
    } catch (Exception e) {
      // none
    }
    return false;
  }

  /** Download client configuration .ini */
  public String downloadConfigurationFile(String clientName, String token) {
    if (!this.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }
    if (!this.clientRepository.existsByNameIgnoreCase(clientName)) {
      throw new ClientNotFoundException(clientName);
    }
    return settingFeignClient.downloadConfigurationFile(clientName, token);
  }
}
