package com.tessi.cxm.pfl.ms5.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.util.HubUtil;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerServiceProvidersDto;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class HubServiceProviderTest {

    @Mock private HubDigitalFlow hubDigitalFlow;
    @Mock private HubUtil hubUtil;
    private HubService hubService;

    @BeforeEach
    void beforeAll() {
        this.hubService = new HubService(hubDigitalFlow, hubUtil);
    }

    @Test
    @Order(1)
    void testSuccess_getCustomerServiceProvider() {
        final String username = "example_username";
        final String customer = "example_customer";

        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            authUtils.when(AuthenticationUtils::getPreferredUsername).thenReturn(username);

            // Mock hub token
            when(this.hubUtil.getHubTokenByUser(anyString())).thenReturn("token");

            // Mock customer service providers
            when(hubDigitalFlow.getCustomerServiceProvider(anyString(), anyString()))
                    .thenReturn(ProfileUnitTestConstants.CUSTOMER_SERVICE_PROVIDERS_DTO);

            var result = hubService.getCustomerServiceProvider(customer);
            Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
        }
    }

    @Test
    @Order(2)
    void testSuccess_getServiceProvider() {
        final String username = "example_username";
        final List<String> channels = List.of("MAIL", "SMS");

        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            authUtils.when(AuthenticationUtils::getPreferredUsername).thenReturn(username);

            // Mock hub token
            when(this.hubUtil.getHubTokenByUser(anyString())).thenReturn("token");

            // Mock service provider criteria
            when(hubDigitalFlow.getServiceProvider(any(), anyString()))
                    .thenReturn(ProfileUnitTestConstants.SERVICE_PROVIDER_RESPONSE);

            var result = hubService.getServiceProvider(channels);
            Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
        }
    }

    @Test
    @Order(3)
    void testSuccess_saveCustomerServiceProvider() {
        final String username = "example_username";

        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            authUtils.when(AuthenticationUtils::getPreferredUsername).thenReturn(username);

            // Mock hub token
            when(this.hubUtil.getHubTokenByUser(anyString())).thenReturn("token");

            // Mock service provider criteria
            when(hubDigitalFlow.saveCustomerServiceProvider(
                            any(CustomerServiceProvidersDto.class), anyString()))
                    .thenReturn(ProfileUnitTestConstants.CUSTOMER_SERVICE_PROVIDERS_DTO);

            var result =
                    hubService.saveCustomerServiceProvider(
                            ProfileUnitTestConstants.CUSTOMER_SERVICE_PROVIDERS_DTO);

            Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
        }
    }
}
