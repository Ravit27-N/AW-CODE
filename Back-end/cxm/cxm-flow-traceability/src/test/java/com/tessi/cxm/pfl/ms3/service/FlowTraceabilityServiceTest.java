package com.tessi.cxm.pfl.ms3.service;

import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.FLOW_TRACEABILITY;
import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_FLOW_TRACEABILITY_PAGING;
import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_USER_DETAIL;
import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_USER_PRIVILEGE_DETAILS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.dto.ListFlowTraceabilityDto;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails;
import com.tessi.cxm.pfl.ms3.exception.FlowTraceabilityNotFoundException;
import com.tessi.cxm.pfl.ms3.repository.FlowCampaignDetailRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDepositRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityDetailRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.ms3.util.ConstantProperties;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.IdNotFoundException;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {InternalConfig.class})
@Slf4j
class FlowTraceabilityServiceTest {

  private static final String TEST_USERNAME = "sokhour";
  private static final String MESSAGE = "Result should be not null.";

  private final ModelMapper modelMapper = new ModelMapper();
  @Mock KeycloakService keycloakService;
  @Mock private FlowTraceabilityRepository flowTraceabilityRepository;
  @Mock private FlowTraceabilityDetailRepository flowTraceabilityDetailRepository;
  @Mock private ProfileFeignClient profileFeignClient;

  @Mock FlowCampaignDetailRepository flowCampaignDetailRepository;
  @Mock FlowDepositRepository flowDepositRepository;
  private FlowTraceabilityService flowTraceabilityService;
  private FlowTraceabilityDto flowTraceabilityDto;
  private FlowTraceability flowTraceability;
  private FlowDocumentRepository flowDocumentRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    modelMapper.addMappings(
        new PropertyMap<FlowTraceability, ListFlowTraceabilityDto>() {
          @Override
          protected void configure() {
            map().setStep(source.getFlowTraceabilityDetails().getStep());
            map().setComposedId(source.getFlowTraceabilityDetails().getComposedId());
            map().setDepositType(source.getDepositType());
            map().setCampaignName(source.getFlowTraceabilityDetails().getCampaignName());
            map().setCampaignFilename(source.getFlowTraceabilityDetails().getCampaignFilename());
          }
        });

    this.flowTraceabilityService =
        spy(
            new FlowTraceabilityService(
                flowTraceabilityRepository,
                modelMapper,
                keycloakService,
                profileFeignClient,
                flowDocumentRepository,
                flowCampaignDetailRepository,
                flowDepositRepository));
    this.flowTraceabilityService.setRepository(flowTraceabilityRepository);
    this.flowTraceabilityService.setFlowTraceabilityDetailRepository(
        flowTraceabilityDetailRepository);

    // set up
    this.flowTraceability = ConstantProperties.flowTraceability;
    this.flowTraceabilityDto = ConstantProperties.flowTraceabilityDto;
  }

  @AfterEach
  void tearDown() {
    this.flowTraceabilityDto = null;
    this.flowTraceability = null;
  }

  @Test
  @Order(1)
  void checkNullObject() {
    Assertions.assertNotNull(this.flowTraceabilityService, "Flow traceability should be not null.");
  }

  @Test
  @Order(3)
  void whenFindFlowTraceabilityById_thenReturnSuccess() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);
      when(profileFeignClient.getUserInfoDetail(anyLong(), any())).thenReturn(MOCK_USER_DETAIL);

      lenient()
          .when(flowTraceabilityRepository.findById(anyLong()))
          .thenReturn(Optional.of(FLOW_TRACEABILITY));
      // Mock find all repository of the flow traceability.
      lenient()
          .when(
              this.flowTraceabilityRepository.findAll(
                  ArgumentMatchers.<Specification<FlowTraceability>>any(), any(Pageable.class)))
          .thenReturn(new PageImpl<>(Collections.emptyList()));

      // Call service.
      final var result = this.flowTraceabilityService.findById(1L);

      // Expected the result.
      Assertions.assertNotNull(result, MESSAGE);
      log.info("Result expected => {}", result);
    }
  }

  @Test
  @Order(4)
  @SuppressWarnings("unchecked")
  void testListFlowTraceabilityWithPagination() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      lenient()
          .when(
              profileFeignClient.loadUsersInVisibilityLevel(anyString(), anyString(), anyString()))
          .thenReturn(List.of("john-doe@example.com"));

      Pageable pageable = PageRequest.of(0, 10);
      var filterCriteria = new FlowFilterCriteria();
      filterCriteria.setFilter("");
      filterCriteria.setUsers(List.of());
      filterCriteria.setChannels(List.of());
      filterCriteria.setCategories(List.of());
      filterCriteria.setStatus(List.of());
      filterCriteria.setDepositModes(List.of());
      filterCriteria.setStartDate("");
      filterCriteria.setEndDate("");

      Page<FlowTraceability> mockResult =
          new PageImpl<>(Collections.singletonList(MOCK_FLOW_TRACEABILITY_PAGING));
      lenient()
          .when(
              this.flowTraceabilityRepository.findAll(
                  any(Specification.class), any(Pageable.class)))
          .thenReturn(mockResult);

      // call service
      var result = this.flowTraceabilityService.findAll(pageable, filterCriteria);
      // expected
      Assertions.assertNotNull(result.getContent(), MESSAGE);
      log.info("Result expected => {}", result.getContent());
    }
  }

  @Test
  @Order(5)
  void testFindFlowTraceabilityByIdNotFound() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      lenient()
          .when(flowTraceabilityRepository.findBaseEntityById(1L))
          .thenReturn(Optional.empty());
      RuntimeException exception =
          Assertions.assertThrows(
              FlowTraceabilityNotFoundException.class, () -> this.flowTraceabilityService.findById(1L));
      Assertions.assertNotNull(exception, MESSAGE);
      log.error("Exception :{}", exception.getMessage());
    }
  }

  @Test
  @Order(6)
  void testUpdateStatusTraceability() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);
      when(this.flowTraceabilityRepository.findById(anyLong()))
          .thenReturn(Optional.of(this.flowTraceability));
      when(this.flowTraceabilityRepository.save(any(FlowTraceability.class)))
          .thenReturn(flowTraceability);
      var result =
          this.flowTraceabilityService.updateFlowStatus(
              1, FlowTraceabilityStatus.SCHEDULED.getValue(), "tessi");
      Assertions.assertNotNull(result, MESSAGE);
      log.info("Result => {}", result);
    }
  }

  @Test
  @Order(7)
  void testGetStepOfFlowDeposit() {
    when(flowTraceabilityRepository.findFlowTraceabilityDetailsById(anyLong()))
        .thenReturn(
            () -> {
              var details = new FlowTraceabilityDetails();
              details.setStep(2);
              details.setComposedId(UUID.randomUUID().toString());
              return details;
            });

    var result = this.flowTraceabilityService.getDepositFlowInfo(1);
    Assertions.assertNotNull(result, MESSAGE);
    log.info("Result => {}", result);
  }

  @Test
  @Order(8)
  void testUpdateStepFlowDeposit() {
    final var composedId = UUID.randomUUID().toString();
    final int step = 4;
    var details = new FlowTraceabilityDetails();
    details.setStep(step);
    details.setComposedId(composedId);
    when(flowTraceabilityRepository.findFlowTraceabilityDetailsById(anyLong()))
        .thenReturn(() -> details);
    when(flowTraceabilityDetailRepository.save(any())).thenReturn(details);
    var result = this.flowTraceabilityService.updateDepositFlow(1, step, composedId, false);
    Assertions.assertNotNull(result, MESSAGE);
    log.info("Result => {}", result);
  }
}
