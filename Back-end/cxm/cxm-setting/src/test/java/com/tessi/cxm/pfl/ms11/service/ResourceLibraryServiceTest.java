package com.tessi.cxm.pfl.ms11.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms11.config.LocalFileConfig;
import com.tessi.cxm.pfl.shared.model.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms11.dto.ResourceParam;
import com.tessi.cxm.pfl.ms11.entity.ResourceLibrary;
import com.tessi.cxm.pfl.ms11.exception.ResourceLibraryNotFoundException;
import com.tessi.cxm.pfl.shared.exception.ResourceTypeNotFoundException;
import com.tessi.cxm.pfl.ms11.repository.ResourceLibraryRepository;
import com.tessi.cxm.pfl.ms11.util.ConstantProperties;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.FileMetadata;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.BackgroundFileValidatorUtil;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
class ResourceLibraryServiceTest {

  @Mock
  ResourceLibraryService resourceLibraryService;
  @Mock
  private ResourceLibraryRepository resourceLibraryRepository;
  @Mock
  private ProfileFeignClient profileFeignClient;
  @Mock
  private FileManagerResource fileManagerResource;
  @Mock
  private FileStorageService fileStorageService;
  @Mock
  private LocalFileConfig localFileConfig;
  private static final String MESSAGE = "Result should be not null.";

  @BeforeEach
  void setUp() {
    TransactionSynchronizationManager.initSynchronization();
    ModelMapper modelMapper = new ModelMapper();
    this.resourceLibraryService =
        new ResourceLibraryService(
            resourceLibraryRepository,
            modelMapper,
            profileFeignClient,
            fileManagerResource,
            fileStorageService, localFileConfig);
  }

  @AfterEach
  public void clear() {
    TransactionSynchronizationManager.clear();
  }

  @Test
  @Order(1)
  void testGetAllResourcesWithPaginationWithSortType() throws Exception {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {

      final ResourceParam resourceParam = ResourceParam.builder().page(1).sortByField("type")
          .language("en").sortDirection("asc").pageSize(1).page(1).build();
      final List<String> types = Collections.singletonList("Background");
      final UserPrivilegeDetails userPrivilegeDetails =
          UserPrivilegeDetails.builder().relatedOwners(Collections.singletonList(15L)).build();
      Page<ResourceLibrary> mockResult =
          new PageImpl<>(Collections.singletonList(ConstantProperties.RESOURCE_LIBRARY));

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(userPrivilegeDetails);

      when(this.resourceLibraryRepository.findAll(
          ArgumentMatchers.<Specification<ResourceLibrary>>any(), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(ConstantProperties.RESOURCE_LIBRARY), Pageable.unpaged(), 1));

      var result = resourceLibraryService.findAll(resourceParam);

      Assertions.assertNotNull(result);
      log.info("Result :{}", result);
    }
  }

  @Test
  @Order(2)
  void testGetResourceByIdSuccess() throws Exception {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {

      final var resourceLibrary = Optional.of(ConstantProperties.RESOURCE_LIBRARY);
      when(resourceLibraryRepository.findById(anyLong())).thenReturn(resourceLibrary);

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      var result = resourceLibraryService.findById(1L);

      Assertions.assertNotNull(result);
      log.info("Result :{}", result);
    }
  }

  @Test
  @Order(3)
  void testGetResourceByIdFail() throws Exception {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      lenient().when(resourceLibraryRepository.findById(1L)).thenReturn(Optional.empty());
      RuntimeException exception =
          Assertions.assertThrows(
              ResourceLibraryNotFoundException.class, () -> resourceLibraryService.findById(1L));
      Assertions.assertNotNull(exception, MESSAGE);
      log.error("Exception :{}", exception.getMessage());
    }
  }

  @Test
  @Order(4)
  void testSaveResources() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(
        AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("test@user.com");
      var pathMock = Mockito.mock(Path.class);
      var pathResolveMock = Mockito.mock(Path.class);
      var fileMock = Mockito.mock(File.class);

      FileMetadata fileMetadata = FileMetadata.builder().size(10).build();
      UserDetail userDetail = new UserDetail();
      userDetail.setOwnerId(1L);
      userDetail.setClientId(1L);
      final UserPrivilegeDetails userPrivilegeDetails =
          UserPrivilegeDetails.builder().relatedOwners(Collections.singletonList(15L)).build();
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(userPrivilegeDetails);
      when(profileFeignClient.getUserDetail(anyString())).thenReturn(userDetail);
      when(fileStorageService.getBase64File(anyString(), anyString())).thenReturn("base64");
      when(fileManagerResource.uploadFiles(anyList(), anyString(), anyString(), anyString()))
          .thenReturn(Collections.singletonList(fileMetadata));
      when(resourceLibraryRepository.save(any())).thenReturn(ConstantProperties.RESOURCE_LIBRARY);
      when(localFileConfig.getPath()).thenReturn("/var/data/cxm/cxm-setting");

      try (MockedStatic<Paths> path = mockStatic(Paths.class)) {

        path
            .when(
                () -> Paths.get(anyString()))
            .thenReturn(pathMock);

        when(pathMock.resolve(anyString())).thenReturn(pathResolveMock);
        when(pathResolveMock.toFile()).thenReturn(fileMock);
        when(fileMock.length()).thenReturn(2L);

        try (MockedStatic<BackgroundFileValidatorUtil> resourceGetFile =
            mockStatic(BackgroundFileValidatorUtil.class)) {

          resourceGetFile
              .when(
                  () -> BackgroundFileValidatorUtil.getNumberOfPages(
                      ConstantProperties.FILE_REQUEST))
              .thenReturn(1);

          ResourceLibraryDto result =
              this.resourceLibraryService.save(ConstantProperties.RESOURCE_LIBRARY_DTO);

          Assertions.assertNotNull(result);
          log.info("Result :{}", result);

        }

      }
    }
  }

  @Test
  @Order(5)
  void testSaveFailResourcesWhenDuplicateLabel() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(
        AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("test@user.com");
      var pathMock = Mockito.mock(Path.class);
      var pathResolveMock = Mockito.mock(Path.class);
      var fileMock = Mockito.mock(File.class);

      UserDetail userDetail = new UserDetail();
      userDetail.setOwnerId(1L);
      userDetail.setClientId(1L);
      final UserPrivilegeDetails userPrivilegeDetails =
          UserPrivilegeDetails.builder().relatedOwners(Collections.singletonList(15L)).build();
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(userPrivilegeDetails);
      when(profileFeignClient.getUserDetail(anyString())).thenReturn(userDetail);
      when(fileStorageService.getBase64File(anyString(), anyString())).thenReturn("base64");
      when(localFileConfig.getPath()).thenReturn("/var/data/cxm/cxm-setting");

      try (MockedStatic<Paths> path = mockStatic(Paths.class)) {

        path
            .when(
                () -> Paths.get(anyString()))
            .thenReturn(pathMock);

        when(pathMock.resolve(anyString())).thenReturn(pathResolveMock);
        when(pathResolveMock.toFile()).thenReturn(fileMock);
        when(fileMock.length()).thenReturn(2L);

        try (MockedStatic<BackgroundFileValidatorUtil> resourceGetFile =
            mockStatic(BackgroundFileValidatorUtil.class)) {

          resourceGetFile
              .when(
                  () -> BackgroundFileValidatorUtil.getNumberOfPages(
                      ConstantProperties.FILE_REQUEST))
              .thenReturn(1);

          IndexOutOfBoundsException indexOutOfBoundsException =
              Assertions.assertThrows(
                  IndexOutOfBoundsException.class,
                  () -> this.resourceLibraryService.save(ConstantProperties.RESOURCE_LIBRARY_DTO));
          log.info("Result :{}", indexOutOfBoundsException.getMessage());

        }

      }
    }
  }

  @Test
  @Order(6)
  void testSaveFailResourcesWhenResourceTypeNotFound() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("test@user.com");
      UserDetail userDetail = new UserDetail();
      userDetail.setOwnerId(1L);
      userDetail.setClientId(1L);
      final UserPrivilegeDetails userPrivilegeDetails =
          UserPrivilegeDetails.builder().relatedOwners(Collections.singletonList(15L)).build();
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(userPrivilegeDetails);
      when(profileFeignClient.getUserDetail(anyString())).thenReturn(userDetail);

      try (MockedStatic<Paths> path = mockStatic(Paths.class)) {

        path
            .when(
                () -> Paths.get(anyString()))
            .thenReturn(ConstantProperties.PATH_REQUEST);

        ResourceLibraryDto resourceLibraryDto = ConstantProperties.RESOURCE_LIBRARY_DTO;
        resourceLibraryDto.setType("test");

        ResourceTypeNotFoundException badRequestException =
            Assertions.assertThrows(
                ResourceTypeNotFoundException.class,
                () -> this.resourceLibraryService.save(resourceLibraryDto));
        log.info("Result :{}", badRequestException.getMessage());


      }

    }
  }

  @Test
  @Order(7)
  void testDeleteResources() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(this.resourceLibraryRepository.findByFileId(anyString()))
          .thenReturn(Optional.of(ConstantProperties.RESOURCE_LIBRARY));
      final UserPrivilegeDetails userPrivilegeDetails =
          UserPrivilegeDetails.builder().relatedOwners(Collections.singletonList(1L)).build();
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(userPrivilegeDetails);
      doNothing().when(resourceLibraryRepository).delete(any());
      doNothing()
          .when(fileManagerResource)
          .deleteMetadataFile(any(), anyString(), anyString(), anyString());
      this.resourceLibraryService.deleteResourceByFileId(
          ConstantProperties.RESOURCE_LIBRARY.getFileId(), "");
      log.info("Delete successfully");
    }
  }

  @Test
  @Order(8)
  void testGetAllResourcesWithPaginationWihtoutSortType() throws Exception {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {

      final Pageable pageable = PageRequest.of(1, 10, Sort.Direction.fromString("ASC"), "label");
      final ResourceParam resourceParam = ResourceParam.builder().page(1).sortByField("createdAt")
          .language("en").sortDirection("asc").pageSize(1).page(1).build();
      final List<String> types = Collections.singletonList("Background");
      final UserPrivilegeDetails userPrivilegeDetails =
          UserPrivilegeDetails.builder().relatedOwners(Collections.singletonList(15L)).build();
      Page<ResourceLibrary> mockResult =
          new PageImpl<>(Collections.singletonList(ConstantProperties.RESOURCE_LIBRARY));

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(userPrivilegeDetails);

      when(resourceLibraryRepository.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(mockResult);

      var result = resourceLibraryService.findAll(resourceParam);

      Assertions.assertNotNull(result);
      log.info("Result :{}", result);
    }
  }

}
