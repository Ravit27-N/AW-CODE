package com.tessi.cxm.pfl.ms32.utils;

import com.tessi.cxm.pfl.ms32.constant.DepositModeResponseDto;
import com.tessi.cxm.pfl.ms32.constant.DepositModeVolumeResponseDto;
import com.tessi.cxm.pfl.ms32.constant.FlowDocumentReportMetaData;
import com.tessi.cxm.pfl.ms32.dto.DocumentTotalDto;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentEvolutionReportData;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentEvolutionReportData.ValueByDate;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentReportData;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentReportDto;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentReportMetadata;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.ProductionDetails;
import com.tessi.cxm.pfl.ms32.dto.StatisticExportingRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.UserFilterPreferenceDto;
import com.tessi.cxm.pfl.ms32.entity.projection.FlowDocumentTimeSeriesProjection;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.UserInfoResponse;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner.UserDetailsOwner;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatusConstant;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Tuple;

public class ConstantProperties {

  public static final FlowDocumentEvolutionReportData FLOW_DOCUMENT_EVOLUTION_REPORT_DATA;
  public static final ValueByDate VALUE_BY_DATE;
  public static final UserInfoResponse USER_INFO_RESPONSE;
  public static final UserPrivilegeDetails USER_PRIVILEGE_DETAILS;
  public static final UserFilterPreferenceDto USER_FILTER_PREFERENCE_DTO;
  public static final FlowDocumentTimeSeriesProjection FLOW_DOCUMENT_TIME_SERIES_PROJECTION;
  public static final DepositModeResponseDto DEPOSIT_MODE_RESPONSE_DTO;
  public static final DepositModeVolumeResponseDto DEPOSIT_MODE_VOLUME_RESPONSE_DTO;
  public static final FlowDocumentReportDto FLOW_DOCUMENT_REPORT_DTO;
  public static final FlowDocumentReportMetadata FLOW_DOCUMENT_REPORT_METADATA;
  public static final FlowDocumentReportData FLOW_DOCUMENT_REPORT_DATA;
  public static final DocumentTotalDto DOCUMENT_TOTAL_DTO;
  public static final List<DocumentTotalDto> LIST_DOCUMENT_TOTAL_DTO;
  public static final ProductionDetails PRODUCTION_DETAIL_SMS;
  public static final ProductionDetails PRODUCTION_DETAIL_POSTAL;
  public static final ProductionDetails PRODUCTION_DETAIL_POSTAL_COMPLETED;
  public static final GlobalStatisticRequestFilter BASE_FILTER_DOCUMENT_REPORT;
  public static final StatisticRequestFilter BASE_FILTER_DOCUMENT_REPORT_POSTAL;
  public static final StatisticRequestFilter BASE_FILTER_DOCUMENT_REPORT_EMAIL;
  public static final StatisticRequestFilter BASE_FILTER_DOCUMENT_REPORT_SMS;
  public static final List<String> CATEGORIES;
  public static final List<DepositModeResponseDto> NON_DISTRIBUTED_VOLUME_RECEIVED;
  public static final List<MockTuple> DOCUMENT_DETAIL_SUMMARY_PND;
  public static final List<Tuple> TUPLES;
  public static final CriteriaDistributionsResponse CRITERIA_DISTRIBUTIONS_RESPONSE;
  public static final StatisticExportingRequestFilter STATISTIC_EXPORTING_REQUEST_FILTER;
  public static final UserPrivilegeDetailsOwner USER_PRIVILEGE_DETAILS_OWNER;
  public static final UserDetailsOwner USER_DETAILS_OWNER;
  public static final List<SharedClientFillersDTO> SHARED_CLIENT_FILLERS_DTOS;

  static {
    List<DepositModeResponseDto> NON_DISTRIBUTED_VOLUME_RECEIVEDS1;
    var dateformat = new SimpleDateFormat("yyyy-MM-dd");

    VALUE_BY_DATE = new ValueByDate(dateformat.toString(), 0L);

    FLOW_DOCUMENT_EVOLUTION_REPORT_DATA =
        FlowDocumentEvolutionReportData.builder()
            .channel("Postal")
            .data(List.of(VALUE_BY_DATE))
            .build();

    USER_INFO_RESPONSE = UserInfoResponse.builder().superAdmin(false).platformAdmin(true).build();

    USER_PRIVILEGE_DETAILS =
        UserPrivilegeDetails.builder()
            .privilegeType("visibility")
            .level("service")
            .nonLevelPrivilege(false)
            .relatedOwners(List.of(1L, 2L, 47L))
            .build();

    USER_FILTER_PREFERENCE_DTO =
        UserFilterPreferenceDto.builder()
            .selectDateType("4")
            .customEndDate(new Date())
            .customStartDate(new Date())
            .build();

    FLOW_DOCUMENT_TIME_SERIES_PROJECTION =
        new FlowDocumentTimeSeriesProjection() {
          @Override
          public Long getCounter() {
            return 1L;
          }

          @Override
          public java.sql.Date getTimeline() {
            return new java.sql.Date(10);
          }
        };

    DEPOSIT_MODE_RESPONSE_DTO = DepositModeResponseDto.builder().key("Portal").value(10.0).build();
    DEPOSIT_MODE_VOLUME_RESPONSE_DTO =
        DepositModeVolumeResponseDto.builder().key("Portal").value(10.0).volume(0).build();

    CRITERIA_DISTRIBUTIONS_RESPONSE = new CriteriaDistributionsResponse();

    FLOW_DOCUMENT_REPORT_METADATA =
        FlowDocumentReportMetadata.builder()
            .col(FlowDocumentReportMetaData.VOLUME.getValue())
            .label(FlowDocumentReportMetaData.VOLUME.getKey())
            .type(FlowDocumentReportMetaData.VOLUME.getDataType())
            .build();

    FLOW_DOCUMENT_REPORT_DATA = FlowDocumentReportData.builder().volume(10).build();

    FLOW_DOCUMENT_REPORT_DTO =
        FlowDocumentReportDto.builder()
            .metaData(List.of(FLOW_DOCUMENT_REPORT_METADATA))
            .result(List.of(FLOW_DOCUMENT_REPORT_DATA))
            .build();
    DOCUMENT_TOTAL_DTO = new DocumentTotalDto("Digital", "SMS", 1L);

    LIST_DOCUMENT_TOTAL_DTO = new ArrayList<>();
    LIST_DOCUMENT_TOTAL_DTO.add(new DocumentTotalDto("Digital", "SMS", 1L));
    LIST_DOCUMENT_TOTAL_DTO.add(new DocumentTotalDto("Digital", "Email", 1L));
    LIST_DOCUMENT_TOTAL_DTO.add(new DocumentTotalDto("Postal", "Postal", 10L));

    BASE_FILTER_DOCUMENT_REPORT =
        GlobalStatisticRequestFilter.builder()
            .startDate(new Date())
            .endDate(new Date())
            .requestedAt(new Date())
            .build();
    BASE_FILTER_DOCUMENT_REPORT_POSTAL =
        StatisticRequestFilter.builder()
            .startDate(new Date())
            .endDate(new Date())
            .requestedAt(new Date())
            .build();
    BASE_FILTER_DOCUMENT_REPORT_EMAIL =
        StatisticRequestFilter.builder()
            .startDate(new Date())
            .endDate(new Date())
            .requestedAt(new Date())
            .build();
    BASE_FILTER_DOCUMENT_REPORT_SMS =
        StatisticRequestFilter.builder()
            .startDate(new Date())
            .endDate(new Date())
            .requestedAt(new Date())
            .build();
    CATEGORIES = new ArrayList<>();
    CATEGORIES.add("Sms");
    CATEGORIES.add("Email");

    PRODUCTION_DETAIL_SMS =
        new ProductionDetails() {
          @Override
          public String getChannel() {
            return "Digital";
          }

          @Override
          public String getSubChannel() {
            return "SMS";
          }

          @Override
          public String getStatus() {
            return "Hard bounce";
          }

          @Override
          public Long getTotal() {
            return 1L;
          }
        };

    PRODUCTION_DETAIL_POSTAL =
        new ProductionDetails() {
          @Override
          public String getChannel() {
            return "Postal";
          }

          @Override
          public String getSubChannel() {
            return "ecopli";
          }

          @Override
          public String getStatus() {
            return "Refused";
          }

          @Override
          public Long getTotal() {
            return 2L;
          }
        };

    NON_DISTRIBUTED_VOLUME_RECEIVEDS1 =
        List.of(
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.ACCESS_OR_ADDRESSING_FAILURE)
                .value(20)
                .build(),
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.RECIPIENT_UNKNOWN)
                .value(20)
                .build(),
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.UNCLAIMED)
                .value(20)
                .build(),
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.REFUSED)
                .value(20)
                .build(),
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.NPAI)
                .value(20)
                .build());
    PRODUCTION_DETAIL_POSTAL_COMPLETED =
        new ProductionDetails() {
          @Override
          public String getChannel() {
            return "Postal";
          }

          @Override
          public String getSubChannel() {
            return "ecopli";
          }

          @Override
          public String getStatus() {
            return "Refused";
          }

          @Override
          public Long getTotal() {
            return 5L;
          }
        };

    NON_DISTRIBUTED_VOLUME_RECEIVEDS1 =
        List.of(
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.ACCESS_OR_ADDRESSING_FAILURE)
                .value(20)
                .build(),
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.RECIPIENT_UNKNOWN)
                .value(20)
                .build(),
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.UNCLAIMED)
                .value(20)
                .build(),
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.REFUSED)
                .value(20)
                .build(),
            DepositModeResponseDto.builder()
                .key(FlowDocumentStatusConstant.NPAI)
                .value(20)
                .build());
    NON_DISTRIBUTED_VOLUME_RECEIVED = NON_DISTRIBUTED_VOLUME_RECEIVEDS1;
  }

  /** Initialize document detail summary for PND. */
  static {
    var npia = new MockTuple();
    npia.setTuple(0, 20L);
    npia.setTuple(1, FlowDocumentStatusConstant.NPAI);

    var addressFailure = new MockTuple();
    addressFailure.setTuple(0, 20L);
    addressFailure.setTuple(1, "Particular");
    addressFailure.setTuple(2, "Marketing");
    addressFailure.setTuple(3, "Lettre Bienvenue");

    var recipientUnknown = new MockTuple();
    recipientUnknown.setTuple(0, 20L);
    recipientUnknown.setTuple(1, "Professional");
    recipientUnknown.setTuple(2, "Computability");
    recipientUnknown.setTuple(3, "Mot de passe");

    var unclaimed = new MockTuple();
    unclaimed.setTuple(0, 20L);
    unclaimed.setTuple(1, "Particular");
    unclaimed.setTuple(2, "Marketing");
    unclaimed.setTuple(3, "Lettre Bienvenue");

    var refused = new MockTuple();
    refused.setTuple(0, 20L);
    refused.setTuple(1, "Professional");
    refused.setTuple(2, "Computability");
    refused.setTuple(3, "Mot de passe");

    DOCUMENT_DETAIL_SUMMARY_PND =
        List.of(npia, addressFailure, recipientUnknown, unclaimed, refused);

    var productionDetailsSummary1 = new MockTuple();
    productionDetailsSummary1.setTuple(0, 10L);
    productionDetailsSummary1.setTuple(1, FlowDocumentStatusConstant.COMPLETED);
    productionDetailsSummary1.setTuple(2, "Particular");
    productionDetailsSummary1.setTuple(3, "Marketing");
    productionDetailsSummary1.setTuple(4, "Lettre Bienvenue");

    var productionDetailsSummary2 = new MockTuple();
    productionDetailsSummary2.setTuple(0, 5L);
    productionDetailsSummary2.setTuple(1, FlowDocumentStatusConstant.IN_PROGRESS);
    productionDetailsSummary2.setTuple(2, "Professional");
    productionDetailsSummary2.setTuple(3, "Computability");
    productionDetailsSummary2.setTuple(4, "Mot de passe");
    TUPLES = List.of(productionDetailsSummary1, productionDetailsSummary2);

    List<SharedClientFillersDTO> sharedClientFillersDTOS = new ArrayList<>();
    SharedClientFillersDTO sharedClientFillersDTO1 = new SharedClientFillersDTO();
    SharedClientFillersDTO sharedClientFillersDTO2 = new SharedClientFillersDTO();
    SharedClientFillersDTO sharedClientFillersDTO3 = new SharedClientFillersDTO();
    sharedClientFillersDTO1.setKey("Filler1");
    sharedClientFillersDTO1.setEnabled(true);
    sharedClientFillersDTO1.setValue("ABC");

    sharedClientFillersDTO2.setKey("Filler2");
    sharedClientFillersDTO2.setEnabled(true);
    sharedClientFillersDTO2.setValue("DEF");

    sharedClientFillersDTO3.setKey("Filler3");
    sharedClientFillersDTO3.setEnabled(true);
    sharedClientFillersDTO3.setValue("GHI");

    sharedClientFillersDTOS.add(sharedClientFillersDTO1);
    sharedClientFillersDTOS.add(sharedClientFillersDTO2);
    sharedClientFillersDTOS.add(sharedClientFillersDTO3);
    SHARED_CLIENT_FILLERS_DTOS = sharedClientFillersDTOS;

    STATISTIC_EXPORTING_REQUEST_FILTER =
        StatisticExportingRequestFilter.builder()
            .channels(List.of("Digital"))
            .categories(List.of("SMS"))
            .fillers(List.of("Filler1"))
            .secondFillerKey("Filler2")
            .thirdFillerKey("Filler3")
            .searchByFiller("2121")
            .secondFillerText("2121")
            .thirdFillerText("2121")
            .timeZone("Asia/Bangkok")
            .exportingType("specific")
            .build();

    USER_DETAILS_OWNER =
        UserDetailsOwner.builder()
            .id(1L)
            .clientId(1L)
            .clientName("Example")
            .divisionId(1L)
            .serviceId(1L)
            .divisionName("Example Division")
            .serviceName("Example Service")
            .username("example@example.com")
            .build();

    USER_PRIVILEGE_DETAILS_OWNER =
        new UserPrivilegeDetailsOwner(
            "visibilityLevel", "client", false, List.of(USER_DETAILS_OWNER));
    ;
  }

  public static RequestFillerMock getRequestFillerMock() throws ParseException {
    // Create a mock request filter
    StatisticExportingRequestFilter requestFilter = new StatisticExportingRequestFilter();
    requestFilter.setTimeZone("UTC");
    requestFilter.setRequestedAt(new Date());
    requestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
    requestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-16"));
    requestFilter.setRequestedAt(
        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-16 07:00:00"));
      requestFilter.setChannels(List.of("Postal"));
      requestFilter.setCategories(List.of("Lettre"));
    // Create a mock user details and a mock resolved request filter
    CriteriaDistributionsResponse criteriaDistributionsResponse =
        new CriteriaDistributionsResponse();
    criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
    criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);
    UserPrivilegeDetailsOwner userDetails = new UserPrivilegeDetailsOwner();
    UserPrivilegeDetailsOwner.UserDetailsOwner userDetailsOwner =
        new UserPrivilegeDetailsOwner.UserDetailsOwner();
    userDetailsOwner.setServiceId(1);
    userDetailsOwner.setClientId(1);
    userDetailsOwner.setDivisionId(1);
    userDetailsOwner.setId(1L);
    userDetails.setUserDetailsOwners(List.of(userDetailsOwner));
      return new RequestFillerMock(requestFilter, criteriaDistributionsResponse, userDetails);
  }

  public static class RequestFillerMock {
    public final StatisticExportingRequestFilter requestFilter;
    public final CriteriaDistributionsResponse criteriaDistributionsResponse;
    public final UserPrivilegeDetailsOwner userDetails;

    public RequestFillerMock(
        StatisticExportingRequestFilter requestFilter,
        CriteriaDistributionsResponse criteriaDistributionsResponse,
        UserPrivilegeDetailsOwner userDetails) {
      this.requestFilter = requestFilter;
      this.criteriaDistributionsResponse = criteriaDistributionsResponse;
      this.userDetails = userDetails;
    }
  }

  public static Object RequestFillerMock() throws ParseException {
    // Create a mock request filter
    StatisticExportingRequestFilter requestFilter = new StatisticExportingRequestFilter();
    requestFilter.setTimeZone("UTC");
    requestFilter.setRequestedAt(new Date());
    requestFilter.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-01"));
    requestFilter.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-16"));
    requestFilter.setRequestedAt(
        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2023-05-16 07:00:00"));
    requestFilter.setChannels(List.of("Postal"));
    requestFilter.setCategories(List.of("Lettre"));
    // Create a mock user details and a mock resolved request filter
    CriteriaDistributionsResponse criteriaDistributionsResponse =
        new CriteriaDistributionsResponse();
    criteriaDistributionsResponse.getCriteria("Postal").setActive(true);
    criteriaDistributionsResponse.getCriteria("Postal").setEnabled(true);

    UserPrivilegeDetailsOwner userDetails = new UserPrivilegeDetailsOwner();
    UserPrivilegeDetailsOwner.UserDetailsOwner userDetailsOwner =
        new UserPrivilegeDetailsOwner.UserDetailsOwner();
    userDetailsOwner.setServiceId(1);
    userDetailsOwner.setClientId(1);
    userDetailsOwner.setDivisionId(1);
    userDetailsOwner.setId(1L);
    userDetails.setUserDetailsOwners(List.of(userDetailsOwner));
    return new RequestFillerMock(requestFilter, criteriaDistributionsResponse, userDetails);
  }


  public static List<SharedClientFillersDTO> getClientFillerMock() {

    List<SharedClientFillersDTO> value = new ArrayList<>();
    value.add(
        SharedClientFillersDTO.builder()
            .id(1L)
            .key("Filler1")
            .value("Filler1")
            .enabled(true)
            .build());
    value.add(
        SharedClientFillersDTO.builder()
            .id(1L)
            .key("Filler2")
            .value("Filler2")
            .enabled(true)
            .build());
    value.add(
        SharedClientFillersDTO.builder()
            .id(1L)
            .key("Filler3")
            .value("Filler3")
            .enabled(true)
            .build());
    value.add(
        SharedClientFillersDTO.builder()
            .id(1L)
            .key("Filler4")
            .value("Filler4")
            .enabled(true)
            .build());
    value.add(
        SharedClientFillersDTO.builder()
            .id(1L)
            .key("Filler5")
            .value("Filler5")
            .enabled(true)
            .build());
    return value;
  }

  public static List<SharedClientFillersDTO> getClientThreeFillerMock() {

    List<SharedClientFillersDTO> value = new ArrayList<>();
    value.add(
        SharedClientFillersDTO.builder()
            .id(1L)
            .key("Filler1")
            .value("Filler1")
            .enabled(true)
            .build());
    value.add(
        SharedClientFillersDTO.builder()
            .id(1L)
            .key("Filler2")
            .value("Filler2")
            .enabled(true)
            .build());
    value.add(
        SharedClientFillersDTO.builder()
            .id(1L)
            .key("Filler3")
            .value("Filler3")
            .enabled(true)
            .build());

    return value;
  }

  public static UserPrivilegeDetailsOwner getUserPrivilegeDetailOwnerMock() {

    UserPrivilegeDetailsOwner.UserDetailsOwner userDetailsOwner = new UserDetailsOwner();
    userDetailsOwner.setId(47L);
    userDetailsOwner.setUsername("test");
    userDetailsOwner.setDivisionName("test");

    UserPrivilegeDetailsOwner userPrivilegeDetailsOwner = new UserPrivilegeDetailsOwner();
    userPrivilegeDetailsOwner.setUserDetailsOwners(List.of(userDetailsOwner));
    userPrivilegeDetailsOwner.setPrivilegeType("visibilityLevel");
    userPrivilegeDetailsOwner.setPrivilegeType("visibilityLevel");

    return userPrivilegeDetailsOwner;
  }
}
