package com.tessi.cxm.pfl.ms8.constant;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.config.LocalFileConfig;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataRequestDto;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponseDto;
import com.tessi.cxm.pfl.ms8.dto.WatermarkDto;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.entity.UnloadingScheduleJob;
import com.tessi.cxm.pfl.ms8.entity.Watermark;
import com.tessi.cxm.pfl.ms8.model.FlowDocumentProduction;
import com.tessi.cxm.pfl.ms8.model.SwitchFlowResponse;
import com.tessi.cxm.pfl.shared.model.FileMetadata;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import com.tessi.cxm.pfl.shared.model.FileStatusDto;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.model.SharedPublicHolidayDTO;
import com.tessi.cxm.pfl.shared.model.SharedPublicHolidayDetailsDTO;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.UsersRelatedToPrivilege;
import com.tessi.cxm.pfl.shared.utils.ResourceFileType;
import com.tessi.cxm.pfl.shared.utils.BackgroundPosition;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class ConstantProperties {

  public static final SwitchFlowResponse SWITCH_FLOW_RESPONSE;
  public static final FlowDocumentProduction FLOW_DOCUMENT_PRODUCTION;
  public static final User USER;
  public static final UsersRelatedToPrivilege USERS_RELATED_TO_PRIVILEGE;
  public static final SharedClientUnloadDetailsDTO SHARED_CLIENT_UNLOAD_DETAILS_DTO;
  public static final SharedClientUnloadDTO SHARED_CLIENT_UNLOAD_DTO;
  public static final SharedPublicHolidayDTO SHARED_PUBLIC_HOLIDAY_DTO;
  public static final SharedPublicHolidayDetailsDTO SHARED_PUBLIC_HOLIDAY_DETAILS_DTO;
  public static final UnloadingScheduleJob UNLOADING_SCHEDULE_JOB;
  public static final DepositedFlowLaunchRequest DEPOSITED_FLOW_LAUNCH_REQUEST;
  public static final ResourceFileMetaDataRequestDto RESOURCE_FILE_META_DATA_REQUEST_DTO;
  public static final ResourceFileMetaDataResponseDto BACKGROUND_FILE_META_DATA_RESPONSE_DTO;
  public static final ResourceFile RESOURCE_FILE;
  public static final UserDetail USER_DETAIL;
  public static final LocalFileConfig LOCAL_FILE_CONFIG;
  public static final MockMultipartFile MOCK_MULTIPART_FILE;
  public static final FileMetadata FILE_METADATA;
  public static final FileStatusDto FILE_STATUS_DTO;
  public static final FilePropertiesHandling FILE_PROPERTIES_HANDLING;
  public static final WatermarkDto WATERMARK_DTO;
  public static final UserPrivilegeDetails USER_PRIVILEGE_DETAILS;

  public static final Watermark WATERMARK;

  static {
    FLOW_DOCUMENT_PRODUCTION =
        FlowDocumentProduction.builder().color("0").urgency("Letter").recto("R").wrap("0").build();

    SWITCH_FLOW_RESPONSE =
        SwitchFlowResponse.builder()
            .filename("8cd0e247-ceef-42b8-9a40-570e20c83d76")
            .nbDocuments(1)
            .nbPages(1)
            .productions(List.of(FLOW_DOCUMENT_PRODUCTION))
            .build();

    USER = new User("1", "dev@gmail.com", "Dev", "TEST", "dev@gmail.com", new Date(), "123");

    USERS_RELATED_TO_PRIVILEGE =
        UsersRelatedToPrivilege.builder()
            .privilegeType("Modification")
            .level("Client")
            .relatedUsers(List.of("dev@gmail.com"))
            .relatedServices(List.of(1L))
            .build();

    SHARED_CLIENT_UNLOAD_DTO =
        SharedClientUnloadDTO.builder().id(1L).dayOfWeek("MON").hour(10).minute(10).build();

    SHARED_PUBLIC_HOLIDAY_DETAILS_DTO =
        SharedPublicHolidayDetailsDTO.builder().id(1L).day(10).month(10).year(2022).build();

    SHARED_PUBLIC_HOLIDAY_DTO =
        SharedPublicHolidayDTO.builder()
            .id(1L)
            .isFixedDate(true)
            .day(10)
            .month(10)
            .publicHolidayDetails(List.of(SHARED_PUBLIC_HOLIDAY_DETAILS_DTO))
            .build();

    SHARED_CLIENT_UNLOAD_DETAILS_DTO =
        SharedClientUnloadDetailsDTO.builder()
            .clientId(1L)
            .clientUnloads(List.of(SHARED_CLIENT_UNLOAD_DTO))
            .publicHolidays(List.of(SHARED_PUBLIC_HOLIDAY_DTO))
            .build();

    UNLOADING_SCHEDULE_JOB =
        UnloadingScheduleJob.builder()
            .id(1L)
            .flowId("8cd0e247-ceef-42b8-9a40-570e20c83d76")
            .documentIds(List.of("8cd0e247-ceef-42b8-9a40-570e20c83d76"))
            .composedFileId("8cd0e247-ceef-42b8-9a40-570e20c83d76")
            .idCreator("8cd0e247-ceef-42b8-9a40-570e20c83d76")
            .createdDate(new Date())
            .clientId(1L)
            .validation(false)
            .build();

    DEPOSITED_FLOW_LAUNCH_REQUEST =
        DepositedFlowLaunchRequest.builder()
            .depositDate(new Date())
            .flowType("tessi/IV/pdf")
            .customer("tessi")
            .depositType("IV")
            .extension("pdf")
            .serviceId("cxm-test")
            .fileId("FILE_ID")
            .flowType("")
            .uuid("8cd0e247-ceef-42b8-9a40-570e20c83d76")
            .serverName("")
            .idCreator(1L)
            .serviceId("1")
            .serviceName("service-test")
            .userName("test")
            .fullName("test")
            .isNew(false)
            .modelName("modelName")
            .campaignName("campaignName")
            .build();

    RESOURCE_FILE_META_DATA_REQUEST_DTO = ResourceFileMetaDataRequestDto.builder()
        .id(1L)
        .fileId("8cd0e247-ceef-42b8-9a40-570e20c83d76")
        .flowId("1")
        .source(ResourceFileType.ONE_TIME_UPLOAD.getValue())
        .position(BackgroundPosition.FIRST_PAGE.value)
        .build();

    BACKGROUND_FILE_META_DATA_RESPONSE_DTO = ResourceFileMetaDataResponseDto.builder()
        .id(1L)
        .fileId("8cd0e247-ceef-42b8-9a40-570e20c83d76")
        .flowId("1")
        .type(ResourceFileType.ONE_TIME_UPLOAD.getValue())
        .position(BackgroundPosition.FIRST_PAGE.value)
        .originalName("test.pdf")
        .extension("pdf")
        .numberOfPages(1)
        .base64("Base64")
        .isMissing(false)
        .build();

    RESOURCE_FILE = ResourceFile.builder()
        .id(1L)
        .fileId("8cd0e247-ceef-42b8-9a40-570e20c83d76")
        .flowId("1")
        .type(ResourceFileType.ONE_TIME_UPLOAD.getValue())
        .position(BackgroundPosition.ALL_PAGES.value)
        .originalName("test.pdf")
        .extension("pdf")
        .numberOfPages(1)
        .ownerId(1L)
        .build();

    USER_DETAIL = UserDetail.builder()
        .ownerId(1L)
        .technicalRef("a62c4fc3-8eb4-48ae-92d6-ce448087adca")
        .firstName("Tessi")
        .lastName("TEST")
        .username("tessi.test@gmail.com")
        .serviceName("Service A")
        .serviceId(1L)
        .divisionName("Division A")
        .divisionId(1L)
        .clientId(1L)
        .build();

    LOCAL_FILE_CONFIG  = new LocalFileConfig();
    LOCAL_FILE_CONFIG.setPath("tmp");

    MOCK_MULTIPART_FILE = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE,
        "Background file".getBytes(
            StandardCharsets.UTF_8));

    FILE_METADATA = FileMetadata.builder()
        .idCreator(1L)
        .uuid("8cd0e247-ceef-42b8-9a40-570e20c83d76")
        .fileName("test.pdf")
        .contentType(MediaType.APPLICATION_PDF_VALUE)
        .content("base64...")
        .build();

    FILE_STATUS_DTO = FileStatusDto.builder()
        .filename("test.pdf")
        .isExist(true)
        .build();

    FILE_PROPERTIES_HANDLING = FilePropertiesHandling.builder()
        .fileName("test.pdf")
        .originalName("test.pdf")
        .extension("pdf")
        .fileSize(10)
        .fileId("8cd0e247-ceef-42b8-9a40-570e20c83d76")
        .build();

    WATERMARK_DTO =
        WatermarkDto.builder()
            .id(0L)
            .text("test")
            .size(1)
            .position("ALL_PAGES")
            .rotation(15)
            .color("#FF0000")
            .flowId("test")
            .build();

    USER_PRIVILEGE_DETAILS =
        UserPrivilegeDetails.builder()
            .privilegeType("visibility")
            .level("service")
            .nonLevelPrivilege(false)
            .relatedOwners(List.of(1L, 2L, 47L))
            .build();

    WATERMARK =
        Watermark.builder()
            .id(1L)
            .text("test")
            .position("ALL_PAGES")
            .size(1)
            .rotation(2)
            .color("#FF0000")
            .build();
  }
}
