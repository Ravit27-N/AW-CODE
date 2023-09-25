package com.tessi.cxm.pfl.ms11.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms11.config.InternalConfig;
import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataRequestDto;
import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataResponseDto;
import com.tessi.cxm.pfl.ms11.service.ChannelMetaDataService;
import com.tessi.cxm.pfl.ms11.util.ConstantProperties;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(
        value = ChannelMetadataController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
        classes = {
            ChannelMetadataController.class,
            SettingGlobalExceptionHandler.class,
            InternalConfig.class
        })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class ChannelMetadataControllerTest {
    public static final String BASE_URL = "/v1/setting/channel-metadata";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ChannelMetaDataService channelMetaDataService;

    @Test
    @Order(1)
    void testSuccessGetChannelMetadataPerTypes() throws Exception {
        final String customer = "example_client";
        final ChannelMetadataResponseDto channelMetadataResponseDto =
                new ChannelMetadataResponseDto();
        channelMetadataResponseDto.setCustomer(customer);

        when(this.channelMetaDataService.getChannelMetadataPerTypes(anyString(), any(List.class)))
                .thenReturn(channelMetadataResponseDto);

        this.mockMvc
                .perform(
                        get("/v1/setting/channel-metadata")
                                .param("customer", customer)
                                .param("types", "sender_name, unsubscribe_link")
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void testFailGetChannelMetadataPerTypes() throws Exception {
        final String customer = "example_client";
        final ChannelMetadataResponseDto channelMetadataResponseDto =
                new ChannelMetadataResponseDto();
        channelMetadataResponseDto.setCustomer(customer);

        when(this.channelMetaDataService.getChannelMetadataPerTypes(anyString(), any(List.class)))
                .thenReturn(channelMetadataResponseDto);

        this.mockMvc
                .perform(
                        get("/v1/setting/channel-metadata")
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void testSuccessSaveChannelMetadataPerTypes() throws Exception {
        final String customer = "example_client";
        final ChannelMetadataResponseDto channelMetadataResponseDto =
                new ChannelMetadataResponseDto();
        channelMetadataResponseDto.setCustomer(customer);

        when(this.channelMetaDataService.save(any(ChannelMetadataRequestDto.class)))
                .thenReturn(ConstantProperties.CHANNEL_METADATA_REQUEST_DTO);

        this.mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsString(
                                                (ChannelMetadataRequestDto.builder()
                                                        .customer("example")
                                                        .type("unsubscribe_link")
                                                        .build()))))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void testFailSaveChannelMetadataPerTypes() throws Exception {
        final String customer = "example_client";
        final ChannelMetadataResponseDto channelMetadataResponseDto =
                new ChannelMetadataResponseDto();
        channelMetadataResponseDto.setCustomer(customer);

        when(this.channelMetaDataService.save(any(ChannelMetadataRequestDto.class)))
                .thenReturn(ConstantProperties.CHANNEL_METADATA_REQUEST_DTO);

        this.mockMvc
                .perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }
}
