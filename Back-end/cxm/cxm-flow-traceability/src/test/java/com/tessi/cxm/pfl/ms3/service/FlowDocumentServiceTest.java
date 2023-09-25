package com.tessi.cxm.pfl.ms3.service;

import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_CLIENT_FILLERS_DTO;
import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_FLOW_TRACEABILITY_DETAILS;
import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_USER_PRIVILEGE_DETAILS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.dto.*;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.exception.FlowDocumentStatusNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.SendingChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.exception.SendingSubChannelNotFoundException;
import com.tessi.cxm.pfl.ms3.repository.*;
import com.tessi.cxm.pfl.ms3.service.restclient.HubDigitalFlowFeignClient;
import com.tessi.cxm.pfl.ms3.service.restclient.ServiceGatewayFeignClient;
import com.tessi.cxm.pfl.ms3.util.ConstantProperties;
import com.tessi.cxm.pfl.ms3.util.FlowDocumentChannel;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.Group;
import com.tessi.cxm.pfl.shared.model.SearchCriteria;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.math.BigDecimal;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;


/**
 * @author Sokhour LACH
 */
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {InternalConfig.class})
@Slf4j
class FlowDocumentServiceTest {

  private static final String TEST_USERNAME = "sokhour";
  private static final String MESSAGE = "Result should be not null.";
  private static final String MOCK_RESULT = "mockResult";
  private static final String SEARCH_CRITERIA = "searchCriteria";
  private final ModelMapper modelMapper = new ModelMapper();
  private final FlowDocument flowDocument = ConstantProperties.FLOW_DOCUMENT;
  private final FlowTraceability flowTraceability = ConstantProperties.flowTraceability;
  private final Group keycloakGroup = ConstantProperties.KEYCLOAK_GROUP;
  @Mock private FlowDocumentRepository flowDocumentRepository;
  @Mock private FlowDocumentHistoryRepository flowDocumentHistoryRepository;
  @Mock private KeycloakService keycloakService;
  @Mock private FlowTraceabilityRepository flowTraceabilityRepository;
  @Mock private FlowDocumentDetailsRepository flowDocumentDetailsRepository;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private HubDigitalFlowFeignClient hubDigitalFlowFeignClient;
  @Mock private MessageSource messageSource;
  @Mock private ServiceGatewayFeignClient serviceGatewayFeignClient;
  @Mock ElementAssociationRepository elementAssociationRepository;
  @Mock
  RestTemplate restTemplate;


  private FlowDocumentService flowDocumentService;
  private FlowDocumentDto flowDocumentDto;

  /*
  @BeforeEach
  void setUp() {
    this.flowDocumentService =
        new FlowDocumentService(
            flowDocumentRepository,
            flowDocumentHistoryRepository,
            keycloakService,
            modelMapper,
            flowTraceabilityRepository,
            flowDocumentDetailsRepository,
            hubDigitalFlowFeignClient,
            messageSource,
            serviceGatewayFeignClient, elementAssociationRepository, null);
    this.flowDocumentDto =
        new FlowDocumentDto(
            1L,
            "tessi.pdf",
            1,
            1,
            1,
            1,
            FlowDocumentChannel.POSTAL.getValue(),
            1L,
            "",
            "",
            FlowDocumentStatus.SCHEDULED.getValue(),
            FlowDocumentSubChannel.RECO.getValue(),
            new Date(),
            new Date(),
            "tessi",
            new FlowDocumentDetailsDto());
    this.flowDocumentService.setProfileFeignClient(profileFeignClient);
    this.flowDocumentService.setRepository(flowTraceabilityRepository);
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);


  }
  */


  @Test
  void testReturnPagination() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      when(flowDocumentRepository.findAll(
              ArgumentMatchers.<Specification<FlowDocument>>any(), any(Pageable.class)))
          .thenReturn((Page) this.getObjectWrapper().get(MOCK_RESULT));
      lenient()
          .when(keycloakService.getDepartmentOfUser())
          .thenReturn(new Group("tessi", "department", "/tessi"));
      Pageable pageable = PageRequest.of(1, 10);
      final var filterCriteria = new FlowFilterCriteria();
      filterCriteria.setChannels(List.of());
      filterCriteria.setCategories(List.of());
      filterCriteria.setUsers(List.of());
      filterCriteria.setStartDate("");
      filterCriteria.setEndDate("");
      filterCriteria.setFilter("");
      filterCriteria.setFillers(List.of("Filler1", "Filler2"));
      filterCriteria.setSearchByFiller("M");
      var result = flowDocumentService.findAll(pageable, filterCriteria);
      Assertions.assertNotNull(result, "Must be not null");
    }
  }

  @Test
  void testReturnPaginationWithFlowTraceabilityId() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      Page<FlowDocument> mockResult =
          new PageImpl<>(Collections.singletonList(ConstantProperties.FLOW_DOCUMENT));
      lenient()
          .when(
              flowDocumentRepository.findAll(
                  ArgumentMatchers.<Specification<FlowDocument>>any(), any(Pageable.class)))
          .thenReturn(mockResult);
      final var filterCriteria = FlowFilterCriteria.builder().users(List.of(1L, 2L)).build();
      filterCriteria.setFilter("john-doe.pdf");
      filterCriteria.setStatus(List.of("In process"));
      filterCriteria.setUsers(List.of());
      filterCriteria.setStartDate("");
      filterCriteria.setEndDate("");
      filterCriteria.setFilter("");
      filterCriteria.setFillers(List.of("Filler1", "Filler2"));
      filterCriteria.setSearchByFiller("M");
      var result = flowDocumentService.findAllByFlowId(1, PageRequest.of(0, 10), filterCriteria);
      Assertions.assertNotNull(result, "Must be not null");
    }
  }

  private Map<String, Object> getObjectWrapper() {
    Page<FlowDocument> mockResult = new PageImpl<>(Collections.singletonList(this.flowDocument));
    // set up
    var customQuery = new HashMap<String, String>();
    customQuery.put(FlowTraceabilityConstant.CHANNEL, FlowDocumentChannel.POSTAL.getValue());
    customQuery.put(FlowTraceabilityConstant.SUB_CHANNEL, "Reco");
    customQuery.put(FlowTraceabilityConstant.STATUS, FlowDocumentStatus.IN_PROGRESS.getValue());
    customQuery.put(FlowTraceabilityConstant.SERVICE, "Campaign");
    customQuery.put(FlowTraceabilityConstant.CREATED_BY, TEST_USERNAME);
    customQuery.put(FlowTraceabilityConstant.DEPOSIT_DATE_START, "2021-10-21");
    customQuery.put(FlowTraceabilityConstant.DEPOSIT_DATE_END, "2021-10-23");
    customQuery.put(FlowTraceabilityConstant.STATUS_DATE_START, "2021-10-23");
    customQuery.put(FlowTraceabilityConstant.STATUS_DATE_END, "2021-10-23");
    Pageable pageable = PageRequest.of(0, 10);
    var criticalSearch = new SearchCriteria();
    criticalSearch.setCustomQuery(customQuery);
    criticalSearch.setPageable(pageable);
    criticalSearch.setFilter("");
    return Map.of(SEARCH_CRITERIA, criticalSearch, MOCK_RESULT, mockResult);
  }

  @Test
  void testSaveFlowDocument() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(flowDocumentRepository.save(any(FlowDocument.class))).thenReturn(flowDocument);
      lenient().when(keycloakService.getDepartmentOfUser()).thenReturn(keycloakGroup);
      when(flowTraceabilityRepository.findById(anyLong()))
          .thenReturn(Optional.of(flowTraceability));

      // Call service
      var result = flowDocumentService.save(flowDocumentDto);
      // Expected
      Assertions.assertNotNull(result, MESSAGE);
      log.info("Result expected => {}", result);
    }
  }

  @Test
  void testSaveFlowDocumentSendingChannelNotFound() {
    when(flowTraceabilityRepository.findById(anyLong())).thenReturn(Optional.of(flowTraceability));
    flowDocumentDto.setChannel("Sending channel");
    RuntimeException exception =
        Assertions.assertThrows(
            SendingChannelNotFoundException.class,
            () -> this.flowDocumentService.save(flowDocumentDto));
    Assertions.assertNotNull(exception, MESSAGE);
    log.error("Exception :{}", exception.getMessage());
  }

  @Test
  void testSaveFlowDocumentSendingSubChannelNotFound() {
    when(flowTraceabilityRepository.findById(anyLong())).thenReturn(Optional.of(flowTraceability));
    flowDocumentDto.setSubChannel("Sending sub channel");
    RuntimeException exception =
        Assertions.assertThrows(
            SendingSubChannelNotFoundException.class,
            () -> this.flowDocumentService.save(flowDocumentDto));
    Assertions.assertNotNull(exception, MESSAGE);
    log.error("Exception :{}", exception.getMessage());
  }

  @Test
  void testSaveFlowDocumentStatusNotFound() {
    when(flowTraceabilityRepository.findById(anyLong())).thenReturn(Optional.of(flowTraceability));
    flowDocumentDto.setStatus("Status");
    RuntimeException exception =
        Assertions.assertThrows(
            FlowDocumentStatusNotFoundException.class,
            () -> this.flowDocumentService.save(flowDocumentDto));
    Assertions.assertNotNull(exception, MESSAGE);
    log.error("Exception :{}", exception.getMessage());
  }

  @Test
  void testGetDocumentDetails() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      when(flowDocumentDetailsRepository.findById(anyLong()))
          .thenReturn(Optional.of(ConstantProperties.FLOW_DOCUMENT_DETAILS));
      when(flowTraceabilityRepository.findFlowTraceabilityDetailsById(anyLong()))
          .thenReturn(flowTraceabilityDetails());
      when(profileFeignClient.getAllClientFillers(any())).thenReturn(MOCK_CLIENT_FILLERS_DTO);

      var result = flowDocumentService.getFlowDocumentDetailsById(1L);
      Assertions.assertNotNull(result, MESSAGE);
    }
  }

  @Test
  void whenGetFlowDocumentFillers_thenReturnSuccess() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.getAllClientFillers(any())).thenReturn(MOCK_CLIENT_FILLERS_DTO);

      var result = flowDocumentService.getFlowDocumentFillers();

      Assertions.assertFalse(CollectionUtils.isEmpty(result), MESSAGE);
      assertEquals("Filler1", result.get(0).getKey());
      assertEquals("Filler5", result.get(4).getKey());

      log.info("Actual result ==> {}", result);
    }
  }

  private FlowTraceabilityDetailsProjection flowTraceabilityDetails() {
    return () -> MOCK_FLOW_TRACEABILITY_DETAILS;
  }

  /*
  @Test
  void testDataToCsvFile() throws IOException {
    // Création du mock pour DocumentCsvProjection
    DocumentCsvProjection document = mock(DocumentCsvProjection.class);
    // Définition des valeurs de retour pour les méthodes du mock
    when(document.getId_document()).thenReturn(1L);

    when(document.getDate_production()).thenReturn(null);
    when(document.getDate_distribution()).thenReturn(null);
    when(document.getCanal()).thenReturn("Digital");
    when(document.getCategorie()).thenReturn("email");
    when(document.getRecto_Verso()).thenReturn(0);
    when(document.getNb_pages()).thenReturn(1);
    when(document.getNb_feuilles()).thenReturn(1);
    when(document.getCouleur()).thenReturn(0);
    when(document.getCode_postal()).thenReturn("50600");
    when(document.getEnveloppe_reel()).thenReturn("C5");
    when(document.getCompagnie()).thenReturn("Campaign_15/04/2022");
    when(document.getId_dest()).thenReturn("Mr DUPOND Jean");
    when(document.getFiller_1()).thenReturn(null);
    when(document.getFiller_2()).thenReturn(null);
    when(document.getFiller_3()).thenReturn(null);
    when(document.getFiller_4()).thenReturn(null);
    when(document.getFiller_5()).thenReturn(null);
    when(document.getStatut()).thenReturn("completed");
    when(document.getDoc_name()).thenReturn("1_5360dv-tppcxm-app02batch5360.pdf");
    when(document.getDuree_archivage()).thenReturn("0");
    when(document.getType_agraf()).thenReturn(2L);
    when(document.getUrgence_reelle()).thenReturn("R1 avec AR");
    when(document.getPoids()).thenReturn(BigDecimal.valueOf(10));
    when(document.getTranche_reelle()).thenReturn("Mail");
    when(document.getAffranchissement()).thenReturn(3.02F);
    when(document.getService()).thenReturn("");


// Création de la liste de données et définition de sa valeur de retour
    List<DocumentCsvProjection> data = Collections.singletonList(document);
    when(flowDocumentRepository.dataToCsv()).thenReturn(data.stream());

// Création du flux de sortie pour capturer les données
    OutputStream outputStream = new ByteArrayOutputStream();

// Appel de la méthode à tester
    flowDocumentService.DataToCsvFile(outputStream);

// Conversion du flux de sortie en chaîne de caractères
    String actual = outputStream.toString().trim();

// Définition de la valeur attendue
    String expectedId = "1";
    String expectedAffranchissement="Mail";
// Vérification du résultat
    assertTrue(actual.contains(expectedId));
    assertTrue(actual.contains(expectedAffranchissement));

  }


  @Test
  public void testSendDtoToPrefacLs() throws Exception {
    // Se moquer de la réponse du RestTemplate
    ResponseEntity<String> mockResponse = new ResponseEntity<>(" response body", HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(
            Mockito.anyString(),
            Mockito.eq(HttpMethod.POST),
            Mockito.any(HttpEntity.class),
            Mockito.eq(String.class)
    )).thenReturn(mockResponse);

    // Appelez la méthode testée
    flowDocumentService.sendDtoToPrefacLs();
    Mockito.verify(restTemplate).exchange(
            Mockito.eq("/prefac/api/digital-experience/export"),
            Mockito.eq(HttpMethod.POST),
            Mockito.any(HttpEntity.class),
            Mockito.eq(String.class)
    );
  }
*/

}
