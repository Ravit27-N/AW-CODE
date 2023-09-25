package com.tessi.cxm.pfl.ms15.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tessi.cxm.pfl.ms15.config.InternalConfig;
import com.tessi.cxm.pfl.ms15.service.IdentificationService;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = IdentificationController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
    classes = {
        InternalConfig.class,
        IdentificationController.class,
        ProcessingGlobalExceptionHandler.class
    })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class IdentificationControllerTest {

  private static final String URL = "/v1/identification";
  private static final String URL_GET_CHANNEL_SUB_CHANNEL =
      URL + "/process-ctrl/{fileId}/{idCreator}";

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private IdentificationService identificationService;

  @Test
  @Order(1)
  void givenRequest_thenResponseObjectOfChannelAndSubChannel() throws Exception {
    var mockResponse =
        new FlowProcessingResponse<>(
            "Finished",
            HttpStatus.OK,
            ProcessCtrlIdentificationResponse.builder()
                .channel("Digital")
                .subChannel("Email")
                .build());

    given(
        this.identificationService.extractChannelAndSubChannelStep(
            anyString(),anyString(),anyLong(),anyString(),anyString(),anyString()))
        .willReturn(mockResponse);

    this.mockMvc
        .perform(
            get(URL_GET_CHANNEL_SUB_CHANNEL, "FILE_ID", 12)
                .param("flowType", "ENI/Batch/C1/zip")
                .param("funcKey", "")
                .param("privKey", "")
                .contentType(MediaType.APPLICATION_JSON))
        // print response on the console.
        .andDo(print())
        // expected result.
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Finished"))
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").isNotEmpty());
  }

  @Test
  @Order(2)
  void givenRequest_thenResponseEmptyObject() throws Exception {
    var mockResponse =
        new FlowProcessingResponse<ProcessCtrlIdentificationResponse>("Finished", HttpStatus.OK);

    given(
        this.identificationService.extractChannelAndSubChannelStep(
            anyString(), anyString(), anyLong(), anyString(), anyString(), anyString()))
        .willReturn(mockResponse);

    this.mockMvc
        .perform(
            get(URL_GET_CHANNEL_SUB_CHANNEL, "FILE_ID", 1)
                .param("flowType", "ENI/Batch/C1/zip")
                .param("funcKey", "")
                .param("privKey", "")
                .contentType(MediaType.APPLICATION_JSON))
        // print response on the console.
        .andDo(print())
        // expected result.
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Finished"))
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").isEmpty());
  }
}
