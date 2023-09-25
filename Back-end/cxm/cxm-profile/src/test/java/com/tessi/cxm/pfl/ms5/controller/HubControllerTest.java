package com.tessi.cxm.pfl.ms5.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.service.HubService;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

@WebMvcTest(value = HubController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {HubController.class, ProfileGlobalExceptionHandler.class})
@MockBeans({@MockBean(HubController.class)})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class HubControllerTest {

    @MockBean private HubService hubService;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void successGetServiceProvider() throws Exception {
        final List<String> channels = List.of("MAIL", "SMS");

        // Stub
        Mockito.when(this.hubService.getServiceProvider(channels))
                .thenReturn(ProfileUnitTestConstants.SERVICE_PROVIDER_RESPONSE);

        // Call & verify
        var result =
                this.mockMvc
                        .perform(
                                MockMvcRequestBuilders.get("/v1/hub/configuration/service-provider")
                                        .param("channel", "MAIL, SMS")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();

        log.info("{}", result.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void successGetCustomerServiceProvider() throws Exception {
        final String client = "example_client";

        // Stub
        Mockito.when(this.hubService.getCustomerServiceProvider(client))
                .thenReturn(ProfileUnitTestConstants.CUSTOMER_SERVICE_PROVIDERS_DTO);

        // Call & verify
        var result =
                this.mockMvc
                        .perform(
                                MockMvcRequestBuilders.get(
                                                "/v1/hub/configuration/customer-service-provider")
                                        .param("customer", "example_client")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();

        log.info("{}", result.getResponse().getContentAsString());
    }

    @Test
    @Order(3)
    void successSaveCustomerServiceProvider() throws Exception {
        // Stub
        Mockito.when(
                        this.hubService.saveCustomerServiceProvider(
                                ProfileUnitTestConstants.CUSTOMER_SERVICE_PROVIDERS_DTO))
                .thenReturn(ProfileUnitTestConstants.CUSTOMER_SERVICE_PROVIDERS_DTO);

        // Call & verify
        var result =
                this.mockMvc
                        .perform(
                                MockMvcRequestBuilders.post(
                                                "/v1/hub/configuration/customer-service-provider")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        ProfileUnitTestConstants
                                                                .CUSTOMER_SERVICE_PROVIDERS_DTO)))
                        .andExpect(status().isOk())
                        .andReturn();

        log.info("{}", result.getResponse().getContentAsString());
    }
}
