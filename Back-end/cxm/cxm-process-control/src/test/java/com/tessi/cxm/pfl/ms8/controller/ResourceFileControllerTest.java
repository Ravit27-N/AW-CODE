package com.tessi.cxm.pfl.ms8.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ConstantProperties;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataRequestDto;
import com.tessi.cxm.pfl.ms8.service.ResourceFileService;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryBootstrapConfiguration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(ProcessControlController.class)
@TestPropertySource("classpath:bootstrap.yml")
@ContextConfiguration(
    classes = {
        ServiceDiscoveryAutoConfigurationImportSelector.class,
        ServiceDiscoveryBootstrapConfiguration.class
    })
@Slf4j
class ResourceFileControllerTest {

  private static final String URL = "/v1/background-file";

  @MockBean
  private ResourceFileService resourceFileService;
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(
            new ResourceFileController(resourceFileService)
        ).setControllerAdvice(ProcessControlGlobalExceptionHandler.class)
        .build();
  }

  @Test
  void testSaveBackgroundFile_ThenReturnSuccess()
      throws Exception {
    var requestDto = ConstantProperties.RESOURCE_FILE_META_DATA_REQUEST_DTO;
    requestDto.setId(null);

    when(this.resourceFileService.save(any(ResourceFileMetaDataRequestDto.class)))
        .thenReturn(ConstantProperties.RESOURCE_FILE_META_DATA_REQUEST_DTO);

    var result = this.mockMvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isOk())
        .andReturn()
        .getResponse();
    log.info("Result: {}", result.getContentAsString());
  }

  @Test
  void testModifiedBackgroundFile_ThenReturnSuccess()
      throws Exception {
    when(this.resourceFileService.save(any(ResourceFileMetaDataRequestDto.class)))
        .thenReturn(ConstantProperties.RESOURCE_FILE_META_DATA_REQUEST_DTO);

    var result = this.mockMvc.perform(
            post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    ConstantProperties.RESOURCE_FILE_META_DATA_REQUEST_DTO))
        ).andExpect(status().isOk())
        .andReturn()
        .getResponse();
    log.info("Result: {}", result.getContentAsString());
  }

  @Test
  void testGetBackgroundFileByFileId_ThenReturnSuccess() throws Exception {
    long fileId = 1;
    when(this.resourceFileService.getResourceFile(anyLong()))
        .thenReturn(ConstantProperties.BACKGROUND_FILE_META_DATA_RESPONSE_DTO);

    var result = this.mockMvc.perform(
            get(URL + "/" + fileId)
        ).andExpect(status().isOk())
        .andReturn().getResponse();
    log.info("Result: {}", result.getContentAsString());
  }

//  @Test
//  void testGetAllBackgroundFileByFlowId_ThenReturnSuccess() throws Exception {
//    String flowId = "1";
//    when(this.resourceFileService.getResourceFilesByFlowId(anyString()))
//        .thenReturn(List.of(ConstantProperties.BACKGROUND_FILE_META_DATA_RESPONSE_DTO));
//
//    var result = this.mockMvc.perform(
//            get(URL + "/" + flowId + "/all")
//        ).andExpect(status().isOk())
//        .andReturn().getResponse();
//
//    log.info("Result: {}", result.getContentAsString());
//  }

  @Test
  void testDeleteBackgroundFileByFileId_ThenReturnSuccess() throws Exception {
    long fileId =1;
    doNothing().when(this.resourceFileService).deleteResourceFile(anyLong());

   this.mockMvc.perform(
            delete(URL + "/" + fileId)
        ).andExpect(status().isOk())
        .andReturn().getResponse();
  }
}
