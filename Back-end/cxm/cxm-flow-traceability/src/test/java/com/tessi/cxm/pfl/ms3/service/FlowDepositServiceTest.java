package com.tessi.cxm.pfl.ms3.service;

import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_FLOW_DEPOSIT;
import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_USER_PRIVILEGE_DETAILS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms3.dto.FlowDepositDto;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import com.tessi.cxm.pfl.ms3.repository.FlowDepositRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.UsersRelatedToPrivilege;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;

/**
 * @author Piseth KHON
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class FlowDepositServiceTest {
  private static final String TEST_USERNAME = "sokhour";
  private static final String MESSAGE = "Result should be not null.";
  private final ModelMapper modelMapper = new ModelMapper();
  private static final String FILE_ID = UUID.randomUUID().toString();
  @Mock KeycloakService keycloakService;
  @Mock private FlowTraceabilityRepository flowTraceabilityRepository;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private FlowDepositRepository flowDepositRepository;
  private FlowDepositService flowDepositService;
  FlowFilterCriteria filterCriteria;
  UsersRelatedToPrivilege usersRelatedToPrivilege;

  @BeforeEach
  void setUp() {
    this.flowDepositService =
        new FlowDepositService(
            modelMapper,
            flowDepositRepository,
            flowTraceabilityRepository,
            profileFeignClient,
            keycloakService);

    usersRelatedToPrivilege =
        UsersRelatedToPrivilege.builder()
            .privilegeType("Visibility")
            .level("Client")
            .relatedUsers(List.of("john-doe@example.com"))
            .build();
    filterCriteria = FlowFilterCriteria.builder().users(Collections.singletonList(2L)).build();

    filterCriteria.setDepositModes(new ArrayList<>());
    filterCriteria.setChannels(new ArrayList<>());
    filterCriteria.setCategories(new ArrayList<>());
    filterCriteria.setFilter("john-doe@example.com");

    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
  }

  @Test
  @Order(1)
  void testFindAll() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      when(this.flowDepositRepository.findAll(
              ArgumentMatchers.<Specification<FlowDeposit>>any(), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(MOCK_FLOW_DEPOSIT), Pageable.unpaged(), 1));

      final Page<FlowDepositDto> result =
          this.flowDepositService.findAll(filterCriteria, Pageable.unpaged(), FILE_ID);
      log.info("Actual result: {}", result);
      Assertions.assertNotNull(result, MESSAGE);
    }
  }

  @Test
  @Order(2)
  void testFindAllWithNoPrivilege() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(this.profileFeignClient.getUserPrivilegeRelatedOwner(anyString(), anyString(),
          anyString(), anyBoolean(), anyBoolean())).thenReturn(MOCK_USER_PRIVILEGE_DETAILS);
      var result =
          Assertions.assertThrows(
              Exception.class,
              () -> this.flowDepositService.findAll(filterCriteria, Pageable.unpaged(), FILE_ID));
      Assertions.assertNotNull(result, "Must be not null");
      log.info("Exception throw {}", result.getMessage());
    }
  }

  @Test
  @Order(3)
  void testFindAllWithNoUserPrivilege() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);
      when(this.flowDepositRepository.findAll(
              ArgumentMatchers.<Specification<FlowDeposit>>any(), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(MOCK_FLOW_DEPOSIT), Pageable.unpaged(), 1));

      final Page<FlowDepositDto> result =
          this.flowDepositService.findAll(filterCriteria, Pageable.unpaged(), FILE_ID);
      log.info("Actual result: {}", result);
      Assertions.assertNotNull(result, MESSAGE);
    }
  }

  @Test
  @Order(4)
  void testDelete() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      // set up
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn(TEST_USERNAME);

      FlowDeposit flowDeposit = new FlowDeposit();
      flowDeposit.setId(1L);
      flowDeposit.setCreatedBy("john-doe@example.com");
      this.flowDepositService.delete(1L);
      Assertions.assertNotNull(flowDeposit, MESSAGE);
    }
  }
}
