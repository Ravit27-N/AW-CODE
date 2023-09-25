package com.tessi.cxm.pfl.ms3.util;

import com.tessi.cxm.pfl.ms3.constant.ElementAssociationConstant;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentDetailsDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentDto;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentFiller;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentHistoryDto;
import com.tessi.cxm.pfl.ms3.dto.FlowHistoryDto;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityValidationDetails;
import com.tessi.cxm.pfl.shared.model.Group;
import com.tessi.cxm.pfl.shared.model.ProcessingResponse;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDTO;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.model.SharedPublicHolidayDTO;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstantProperties {

  public static final FlowTraceability FLOW_TRACEABILITY =
      FlowTraceability.builder()
          .id(1L)
          .dateStatus(new Date())
          .depositMode("API")
          .flowName("tess.pdf")
          .channel(Channel.DIGITAL.getValue())
          .subChannel(SubChannel.DIGITAL.getValue())
          .status("deposited")
          .fileId("7b7ebea3-9d55-4e4a-b4e2-20330b5cd7ce")
          .flowTraceabilityDetails(new FlowTraceabilityDetails())
          .fullName("John Doe")
          .flowHistories(new HashSet<>())
          .modelName("PoXxx")
          .flowTraceabilityDetails(new FlowTraceabilityDetails())
          .flowTraceabilityValidationDetails(new FlowTraceabilityValidationDetails())
          .ownerId(2L)
          .build();
  public static final FlowHistoryDto FLOW_HISTORY_DTO =
      new FlowHistoryDto(1L, "tessi", "tessi", "tessi", 1, new HashMap<>(), new Date(), "tessi");

  public static final FlowTraceabilityDetails MOCK_FLOW_TRACEABILITY_DETAILS =
      new FlowTraceabilityDetails(
          1L,
          1,
          1,
          0,
          1L,
          "Facture_BL_02112021-1KO-Middle.pdf",
          1L,
          FLOW_TRACEABILITY,
          1,
          "eefd6093-e5c9-4e07-b54b-e865061aa42a",
          "PDF",
          "",
          true);

  public static final FlowHistory FLOW_HISTORY = new FlowHistory("tessi", "", new Date());

  public static final FlowTraceability flowTraceability =
      FlowTraceability.builder()
          .id(1L)
          .dateStatus(new Date())
          .depositMode("batch")
          .flowName("letree.pdf")
          .channel(Channel.POSTAL.getValue())
          .subChannel("Reco")
          .status("deposited")
          .fileId("ksdjfijewewew")
          .flowTraceabilityDetails(new FlowTraceabilityDetails())
          .fullName("John Doe")
          .flowHistories(new HashSet<>())
          .flowTraceabilityDetails(new FlowTraceabilityDetails())
          .flowTraceabilityValidationDetails(new FlowTraceabilityValidationDetails())
          .ownerId(2L)
          .modelName("PoXxx")
          .build();

  public static final FlowTraceabilityDto flowTraceabilityDto =
      new FlowTraceabilityDto(
          0L,
          new Date(),
          new Date(),
          "letree.pdf",
          "Jack Son Lue",
          "Pretracc.",
          new Date(),
          "Reco",
          Channel.POSTAL.getValue(),
          "",
          "",
          1L,
          0,

          "tessi",
          "",
          FlowTraceabilityStatus.DEPOSITED.getValue(),
          "",
          new Date(),
          DepositMode.BATCH.getValue(),
          new HashMap<>(),
          new HashMap<>(),
          new HashMap<>(),
          new HashMap<>(),
          "John Doe",
          Set.of(),
          "PoXxx"
          );

  public static final FlowDocument FLOW_DOCUMENT =
      new FlowDocument(
          1L,
          "tessi.pdf",
          1,
          1,
          1,
          "tessi",
          1,
          "tessi",
          FlowDocumentStatus.IN_PROGRESS.getValue(),
          FlowDocumentChannel.POSTAL.getValue(),
          FlowDocumentSubChannel.RECO.getValue(),
          new Date(),
          "",
          new Date(),
          "",
          new Date(),
          "",
          1,
          1,
          "",
          "",
          new Date(),
          new HashSet<>(),
          flowTraceability,
          new FlowDocumentDetails(),
          List.of());

  public static final FlowDocumentDto FLOW_DOCUMENT_DTO =
      new FlowDocumentDto(
          1L,
          "tessi.pdf",
          1,
          1,
          1,
          1,
          FlowDocumentChannel.POSTAL.getValue(),
          1L,
          "relatedItem",
          "recipient",
          FlowDocumentStatus.IN_PROGRESS.getValue(),
          FlowDocumentSubChannel.RECO.getValue(),
          new Date(),
          new Date(),
          "tessi",
          new FlowDocumentDetailsDto());

  public static final ElementAssociation ELEMENT_ASSOCIATION =
      new ElementAssociation(
          1L,
          ElementAssociationConstant.ACCUSE_RECEPTION,
          "http://cxm-tessi.fr",
          "pdf",
          new FlowDocument());

  public static final ElementAssociationDto ELEMENT_ASSOCIATION_DTO =
      new ElementAssociationDto(
          1L, ElementAssociationConstant.ACCUSE_RECEPTION, "http://cxm-tessi.fr", "pdf", 1);

  public static final FlowDocumentHistory FLOW_DOCUMENT_HISTORY =
      new FlowDocumentHistory(
          1L, FlowDocumentStatus.IN_PRODUCTION.getValue(), "service_1", new Date());

  public static final FlowDocumentHistoryDto FLOW_DOCUMENT_HISTORY_DTO =
      new FlowDocumentHistoryDto(
          1L,
          FlowDocumentStatus.IN_PRODUCTION.getValue(),
          "Tessi",
          "service_1",
          1,
          new HashMap<>(),
          new Date(),
          "");

  public static final Group KEYCLOAK_GROUP =
      new Group("ebc7f490-1e22-4510-b740-6dbc1076b891", "tessi", "/path");

  public static final FlowDocumentDetails FLOW_DOCUMENT_DETAILS =
      new FlowDocumentDetails(
          1,
          "",
          "example@presta.tessi.fr",
          "",
          "",
          new String[] {"MonFiller", "", "", "", ""},
          "",
          "",
          "",
          "",
          "",
          "Mail",
          "None",
          "",
          "Non",
          "",
          FLOW_DOCUMENT,
          new ArrayList<>(),
          new ArrayList<>());

  public static final FlowDocumentDetailsDto FLOW_DOCUMENT_DETAILS_DTO = FlowDocumentDetailsDto.builder().id(1L).campaignName("example@presta.tessi.fr").fillers(new ArrayList<>()).enrichment(new ProcessingResponse()).build();

  public static final ListFlowTraceabilityDto LIST_FLOW_TRACEABILITY_DTO =
      ListFlowTraceabilityDto.builder()
          .id(1L)
          .channel(FlowDocumentChannel.POSTAL.getValue())
          .composedId("12h1u2y1oyswawwqwqwt4r3")
          .createdAt(new Date())
          .dateStatus(new Date())
          .depositDate(new Date())
          .flowName("example-deposit.pdf")
          .build();

  public static final List<SharedClientFillersDTO> MOCK_CLIENT_FILLERS_DTO =
      new ArrayList<>(
          Arrays.asList(
              new SharedClientFillersDTO(1L, "Filler1", "Nom", true),
              new SharedClientFillersDTO(2L, "Filler2", "Email", true),
              new SharedClientFillersDTO(2L, "Filler3", "", true),
              new SharedClientFillersDTO(2L, "Filler4", "Phone number", true),
              new SharedClientFillersDTO(2L, "Filler5", "Company", true)));

  public static final List<FlowDocumentFiller> MOCK_FLOW_DOCUMENT_FILLERS =
      new ArrayList<>(
          Arrays.asList(
              new FlowDocumentFiller("Filler1", "Nom", 1),
              new FlowDocumentFiller("Filler2", "Email", 2),
              new FlowDocumentFiller("Filler3", "Filler3", 3),
              new FlowDocumentFiller("Filler4", "Phone number", 4),
              new FlowDocumentFiller("Filler5", "Company", 5)));

  public static final UserPrivilegeDetails MOCK_USER_PRIVILEGE_DETAILS =
      new UserPrivilegeDetails("vis", "service", false, Collections.singletonList(2L));

  public static final FlowTraceability MOCK_FLOW_TRACEABILITY_PAGING =
      new FlowTraceability(
          1L,
          new Date(),
          new Date(),
          "Portal",
          "tessi_doc.pdf",
          "",
          "Postal",
          "Lettre",
          "In process",
          new Date(),
          "Tessi USER",
          1L,
          "PoXxx",
          new Date(),
          "",
          new Date(),
          "",
          Collections.singleton(FLOW_HISTORY),
          new FlowTraceabilityDetails(),
          new FlowTraceabilityValidationDetails());

  public static final UserDetail MOCK_USER_DETAIL =
      new UserDetail(1L, "", "TESSI", "NOM", "TESSI_NOM", "Account", "France", "TESSI", 1L, 1L, 1L);

  public static final FlowDeposit MOCK_FLOW_DEPOSIT =
      new FlowDeposit(
          1L,
          "6dde90f6-df77-49cc-ba9a-cd67de01e4c8",
          "6dde90f6-df77-49cc-ba9a-cd67de01e5c8",
          1,
          true,
          "In progress",
          FLOW_TRACEABILITY);
  public static final SharedClientUnloadDetailsDTO SHARED_CLIENT_UNLOAD_DETAILS_DTO =
      SharedClientUnloadDetailsDTO
          .builder()
          .clientId(1L)
          .clientUnloads(
              List.of(SharedClientUnloadDTO.builder().id(1L).dayOfWeek("Mon").hour(1).minute(1)
                  .zoneId("Asia/Phnom_Penh (UTC+07:00)").build())
          )
          .publicHolidays(List.of(SharedPublicHolidayDTO.builder()
              .id(1L).isFixedDate(false).month(1).day(1)
              .build()))

          .build();
}
