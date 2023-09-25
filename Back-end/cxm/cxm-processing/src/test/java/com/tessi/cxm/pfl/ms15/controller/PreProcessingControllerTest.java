package com.tessi.cxm.pfl.ms15.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tessi.cxm.pfl.ms15.config.InternalConfig;
import com.tessi.cxm.pfl.ms15.service.PreProcessingService;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.shared.filectrl.model.Document;
import com.tessi.cxm.pfl.shared.filectrl.model.FileFlowDocument;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    value = PreProcessingController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
    classes = {
      InternalConfig.class,
      PreProcessingController.class,
      ProcessingGlobalExceptionHandler.class
    })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class PreProcessingControllerTest {

  private static final String URL = "/v1/pre-processing";
  private static final String URL_GET_DOCUMENT = URL + "/document";

  @Autowired private MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean private PreProcessingService preProcessingService;

  @Test
  @Order(1)
  void givenRequest_willReturnDocumentObject() throws Exception {
    List<FileFlowDocument> flowDocuments =
        new ArrayList<>(
            Collections.singletonList(
                FileFlowDocument.builder()
                    .emailObject("Column 7")
                    .address(Map.of("Line18", "DEST_ADR2"))
                    .emailObject("ENI : Le r??glement de votre facture")
                    .channel("Digital")
                    .subChannel("Email")
                    .build()));
    var mockDocument = Document.builder().nbDocuments("0").flowDocuments(flowDocuments).build();
    log.info("mockDocument: {}", mockDocument);
    var mockResponse = new FlowProcessingResponse<>("Finished", HttpStatus.OK, mockDocument);
    given(
            this.preProcessingService.getDocuments(
                anyString(),
                anyString(),
                anyString(),
                anyLong(),
                anyString(),
                anyString(),
                anyString(),
                anyString()))
        .willReturn(mockResponse);

    this.mockMvc
        .perform(
            get(URL_GET_DOCUMENT)
                .param("modelName", "MODEL_NAME")
                .param("flowType", "ENI/Batch/C1/zip")
                .param("fileId", "FILE_ID")
                .param("idCreator", "123")
                .param("channel", "Digital")
                .param("subChannel", "Email")
                .param("funcKey", "")
                .param("privKey", "")
                .contentType(MediaType.APPLICATION_JSON))
        // print response to the console.
        .andDo(print())
        // expected response object.
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("Finished"))
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(jsonPath("$.data.DOCUMENT").isNotEmpty())
        .andExpect(jsonPath("$.data.DOCUMENT[0].Filiere").value("Digital"))
        .andReturn()
        .getResponse();
  }

  @Test
  @Order(2)
  void givenRequest_willReturnEmptyDocumentObject() throws Exception {
    var mockResponse = new FlowProcessingResponse<Document>("Finished", HttpStatus.OK);
    given(
            this.preProcessingService.getDocuments(
                anyString(),
                anyString(),
                anyString(),
                anyLong(),
                anyString(),
                anyString(),
                anyString(),
                anyString()))
        .willReturn(mockResponse);

    this.mockMvc
        .perform(
            get(URL_GET_DOCUMENT)
                .param("modelName", "MODEL_NAME")
                .param("fileId", "FILE_ID")
                .param("idCreator", "123")
                .param("channel", "Digital")
                .param("subChannel", "Email")
                .param("flowType", "ENI/Batch/C1/zip")
                .param("funcKey", "")
                .param("privKey", "")
                .contentType(MediaType.APPLICATION_JSON))
        // print response to the console.
        .andDo(print())
        // expected response object.
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("Finished"))
        .andExpect(jsonPath("$.data").isEmpty())
        .andReturn()
        .getResponse();
  }
}
