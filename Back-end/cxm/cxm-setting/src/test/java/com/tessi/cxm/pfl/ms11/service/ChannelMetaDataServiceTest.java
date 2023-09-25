package com.tessi.cxm.pfl.ms11.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms11.exception.CustomerNotFoundException;
import com.tessi.cxm.pfl.ms11.repository.ChannelMetadataRepository;
import com.tessi.cxm.pfl.ms11.util.ConstantProperties;
import com.tessi.cxm.pfl.ms11.util.SettingPrivilegeUtil;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerDomainNameRequest;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.HubDigitalFlowHelper;

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
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class ChannelMetaDataServiceTest {
    @Mock private ChannelMetadataRepository channelMetadataRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private ProfileFeignClient profileFeignClient;
    @Mock private HubDigitalFlow hubDigitalFlow;
    @Mock private HubDigitalFlowHelper hubDigitalFlowHelper;
    private ChannelMetaDataService channelMetaDataService;

    @BeforeEach
    void setUp() {
        this.channelMetaDataService =
                new ChannelMetaDataService(
                        channelMetadataRepository,
                        modelMapper,
                        profileFeignClient,
                        hubDigitalFlow,
                        hubDigitalFlowHelper);
        SettingPrivilegeUtil.setProfileFeignClient(profileFeignClient);
    }

    @Test
    @Order(1)
    void testSuccessWhenGetChannelMetadataBySuperAdmin() {
        final String customer = "Client 1";
        final List<String> types = List.of("unsubscribe_link", "sender_label", "sender_name");
        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            // Privilege.
            authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
            when(profileFeignClient.checkUserIsAdmin(anyString()))
                    .thenReturn(ConstantProperties.ADMIN_USER);
            when(profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
                    .thenReturn(true);

            // Get channel metadata by types.
            when(channelMetadataRepository.findAllChannelMetadata(anyString(), any(List.class)))
                    .thenReturn(List.of());
            final var response = channelMetaDataService.getChannelMetadataPerTypes(customer, types);

            Assertions.assertNotNull(response);
        }
    }

    @Test
    @Order(2)
    void testNoCustomerWhenGetChannelMetadataBySuperAdmin() {
        final String customer = "Client 1";
        final List<String> types = List.of("unsubscribe_link", "sender_label", "sender_name");
        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            // Privilege.
            authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
            when(profileFeignClient.checkUserIsAdmin(anyString()))
                    .thenReturn(ConstantProperties.ADMIN_USER);
            when(profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
                    .thenReturn(false);

            Assertions.assertThrows(
                    CustomerNotFoundException.class,
                    () -> channelMetaDataService.getChannelMetadataPerTypes(customer, types));
        }
    }

    @Test
    @Order(3)
    void testNoPrivilegeWhenGetChannelMetadataBySuperAdmin() {
        final String customer = "Client 1";
        final List<String> types = List.of("unsubscribe_link", "sender_label", "sender_name");
        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            // Privilege.
            authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
            when(profileFeignClient.checkUserIsAdmin(anyString()))
                    .thenReturn(ConstantProperties.NORMAL_USER);

            Assertions.assertThrows(
                    UserAccessDeniedExceptionHandler.class,
                    () -> channelMetaDataService.getChannelMetadataPerTypes(customer, types));
        }
    }

    @Test
    @Order(4)
    void testSaveChannelMetadataBySuperAdmin() {
        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            // Privilege.
            authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
            when(profileFeignClient.checkUserIsAdmin(anyString()))
                    .thenReturn(ConstantProperties.ADMIN_USER);
            when(profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
                    .thenReturn(true);

            // Get channel metadata by types.
            when(channelMetadataRepository.findAllByCustomerAndType(anyString(), anyString()))
                    .thenReturn(Collections.singletonList(ConstantProperties.CHANNEL_METADATA));

            // Get user detail.
            when(profileFeignClient.getUserDetail(anyString()))
                    .thenReturn(ConstantProperties.USER_DETAIL);

            // Remove metadata.
            doNothing().when(channelMetadataRepository).deleteAllById(any());

            // Save or update metadata.
            when(channelMetadataRepository.saveAll(any())).thenReturn(Collections.emptyList());

            final var response =
                    channelMetaDataService.save(ConstantProperties.CHANNEL_METADATA_REQUEST_DTO);

            Assertions.assertNotNull(response);
        }
    }

    @Test
    @Order(5)
    void testSaveEmailChannelMetadataBySuperAdmin() {
        final var channelMetadataRequestDto = ConstantProperties.CHANNEL_METADATA_REQUEST_DTO;
        channelMetadataRequestDto.setType("sender_mail");
        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            // Privilege.
            authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
            when(profileFeignClient.checkUserIsAdmin(anyString()))
                    .thenReturn(ConstantProperties.ADMIN_USER);
            when(profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
                    .thenReturn(true);

            // Get channel metadata by types.
            when(channelMetadataRepository.findAllByCustomerAndType(anyString(), anyString()))
                    .thenReturn(Collections.singletonList(ConstantProperties.CHANNEL_METADATA));

            // Get user detail.
            when(profileFeignClient.getUserDetail(anyString()))
                    .thenReturn(ConstantProperties.USER_DETAIL);

            // Remove metadata.
            doNothing().when(channelMetadataRepository).deleteAllById(any());

            // Save or update metadata.
            when(channelMetadataRepository.saveAll(any())).thenReturn(Collections.emptyList());

            // Create domain name and link with this customer.
            lenient()
                    .when(profileFeignClient.getUserHubAccount(anyString(), anyString()))
                    .thenReturn(ConstantProperties.USER_HUB_ACCOUNT);
            when(hubDigitalFlowHelper.getUserHubTokenByKeycloakAdmin(anyString(), anyString()))
                    .thenReturn("example_token");
            when(hubDigitalFlow.createDomainNameAndLinkCustomer(
                            any(CustomerDomainNameRequest.class), anyString()))
                    .thenReturn(ConstantProperties.CUSTOMER_DOMAIN_NAME_REQUEST);

            final var response = channelMetaDataService.save(channelMetadataRequestDto);
            Assertions.assertNotNull(response);
        }
    }

    @Test
    @Order(6)
    void testNoCustomerWhenSaveChannelMetadataBySuperAdmin() {
        final String customer = "Client 1";
        final List<String> types = List.of("unsubscribe_link", "sender_label", "sender_name");
        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            // Privilege.
            authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
            when(profileFeignClient.checkUserIsAdmin(anyString()))
                    .thenReturn(ConstantProperties.ADMIN_USER);
            when(profileFeignClient.isClientExist(anyString(), anyLong(), anyString()))
                    .thenReturn(false);

            Assertions.assertThrows(
                    CustomerNotFoundException.class,
                    () -> channelMetaDataService.getChannelMetadataPerTypes(customer, types));
        }
    }

    @Test
    @Order(7)
    void testNoPrivilegeWhenSaveChannelMetadataBySuperAdmin() {
        final String customer = "Client 1";
        final List<String> types = List.of("unsubscribe_link", "sender_label", "sender_name");
        try (MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {

            // Privilege.
            authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("Auth Token");
            when(profileFeignClient.checkUserIsAdmin(anyString()))
                    .thenReturn(ConstantProperties.NORMAL_USER);

            Assertions.assertThrows(
                    UserAccessDeniedExceptionHandler.class,
                    () -> channelMetaDataService.getChannelMetadataPerTypes(customer, types));
        }
    }
}
