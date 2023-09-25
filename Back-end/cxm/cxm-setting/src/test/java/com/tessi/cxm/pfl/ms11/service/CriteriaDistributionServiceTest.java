package com.tessi.cxm.pfl.ms11.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms11.exception.CustomerNotFoundException;
import com.tessi.cxm.pfl.ms11.repository.CriteriaDistributionRepository;
import com.tessi.cxm.pfl.ms11.util.ConstantProperties;
import com.tessi.cxm.pfl.ms11.util.SettingPrivilegeUtil;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerRequest;
import com.tessi.cxm.pfl.shared.model.setting.criteria.Preference;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.HubDigitalFlowHelper;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
class CriteriaDistributionServiceTest {
  @Mock private CriteriaDistributionRepository criteriaDistributionRepository;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private HubDigitalFlow hubDigitalFlow;
  @Mock private HubDigitalFlowHelper hubDigitalFlowHelper;

  private CriteriaDistributionService criteriaDistributionService;

  @BeforeEach
  void setUp() {
    this.criteriaDistributionService =
        new CriteriaDistributionService(
            this.criteriaDistributionRepository,
            this.hubDigitalFlow,
            this.hubDigitalFlowHelper,
            this.profileFeignClient);
    SettingPrivilegeUtil.setProfileFeignClient(profileFeignClient);
  }

  @Test
  @Order(1)
  void testSuccessGetCriteriaDistribution() {
    var customer = Optional.of("Client 1");
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

      // Privilege
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
      when(this.profileFeignClient.checkUserIsAdmin(anyString()))
          .thenReturn(ConstantProperties.ADMIN_USER);
      when(this.profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
          .thenReturn(true);
      // Get existing criteria distribution
      when(this.criteriaDistributionRepository.findAllCriteria(anyString(), any()))
          .thenReturn(List.of());

      var response = this.criteriaDistributionService.getCriteriaDistribution(customer);

      Assertions.assertNotNull(response);
      Assertions.assertEquals(7, response.getCriteria().size());
    }
  }

  @Test
  @Order(2)
  void testNoPrivilegeToGetCriteriaDistribution() {
    var customer = Optional.of("Client 1");
    when(this.profileFeignClient.getUserDetail(anyString()))
        .thenReturn(ConstantProperties.USER_DETAIL);
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

      // Privilege
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
      when(this.profileFeignClient.checkUserIsAdmin(anyString()))
          .thenReturn(ConstantProperties.NORMAL_USER);

      Assertions.assertThrows(
          UserAccessDeniedExceptionHandler.class,
          () -> this.criteriaDistributionService.getCriteriaDistribution(customer));
    }
  }

  @Test
  @Order(3)
  void testNoCustomerWhenGetCriteriaDistribution() {
    var customer = Optional.of("Client 1");
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

      // Privilege
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
      when(this.profileFeignClient.checkUserIsAdmin(anyString()))
          .thenReturn(ConstantProperties.ADMIN_USER);
      when(this.profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
          .thenReturn(false);

      Assertions.assertThrows(
          CustomerNotFoundException.class,
          () -> this.criteriaDistributionService.getCriteriaDistribution(customer));
    }
  }

  @Test
  @Order(4)
  void testDisableCriteriaDistributions() {
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

      // Privilege
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
      when(this.profileFeignClient.checkUserIsAdmin(anyString()))
          .thenReturn(ConstantProperties.ADMIN_USER);
      when(this.profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
          .thenReturn(true);

      // Get existing criteria distribution
      when(this.criteriaDistributionRepository.findCriteria(anyString(), anyString()))
          .thenReturn(Optional.ofNullable(ConstantProperties.CRITERIA_DISTRIBUTION));
      when(this.criteriaDistributionRepository.save(any()))
          .thenReturn(ConstantProperties.CRITERIA_DISTRIBUTION);

      var response =
          this.criteriaDistributionService.updateCriteriaDistributions(
              ConstantProperties.CRITERIA_DISTRIBUTION_REQUEST);

      Assertions.assertNotNull(response);
    }
  }

  @Test
  @Order(5)
  void testEnableCriteriaDistributions() {
    final var request = ConstantProperties.CRITERIA_DISTRIBUTION_REQUEST;
    request.setPreference(Preference.builder().enabled(true).active(true).name("Digital").build());

    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

      // Privilege
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
      when(this.profileFeignClient.checkUserIsAdmin(anyString()))
          .thenReturn(ConstantProperties.ADMIN_USER);
      when(this.profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
          .thenReturn(true);

      // Get existing criteria distribution
      when(this.criteriaDistributionRepository.findCriteria(anyString(), anyString()))
          .thenReturn(Optional.ofNullable(ConstantProperties.CRITERIA_DISTRIBUTION));
      when(this.criteriaDistributionRepository.save(any()))
          .thenReturn(ConstantProperties.CRITERIA_DISTRIBUTION);
      lenient()
          .when(this.hubDigitalFlow.createCustomer(any(CustomerRequest.class), anyString()))
          .thenReturn(ConstantProperties.CUSTOMER_REQUEST);

      var response = this.criteriaDistributionService.updateCriteriaDistributions(request);

      Assertions.assertNotNull(response);
    }
  }

  @Test
  @Order(6)
  void testNoPrivilegeToUpdateCriteriaDistributions() {
    var customer = Optional.of("Client 1");
    when(this.profileFeignClient.getUserDetail(anyString()))
        .thenReturn(ConstantProperties.USER_DETAIL);
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

      // Privilege
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
      when(this.profileFeignClient.checkUserIsAdmin(anyString()))
          .thenReturn(ConstantProperties.NORMAL_USER);

      Assertions.assertThrows(
          UserAccessDeniedExceptionHandler.class,
          () -> this.criteriaDistributionService.getCriteriaDistribution(customer));
    }
  }

  @Test
  @Order(7)
  void testNoCustomerWhenUpdateCriteriaDistributions() {
    var customer = Optional.of("Client 1");
    try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

      // Privilege
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
      when(this.profileFeignClient.checkUserIsAdmin(anyString()))
          .thenReturn(ConstantProperties.ADMIN_USER);
      when(this.profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
          .thenReturn(false);

      Assertions.assertThrows(
          CustomerNotFoundException.class,
          () -> this.criteriaDistributionService.getCriteriaDistribution(customer));
    }
  }
}
