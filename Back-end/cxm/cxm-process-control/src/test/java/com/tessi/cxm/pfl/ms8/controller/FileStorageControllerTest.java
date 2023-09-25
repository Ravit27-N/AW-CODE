package com.tessi.cxm.pfl.ms8.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.constant.ConstantProperties;
import com.tessi.cxm.pfl.ms8.service.FileStorageService;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryBootstrapConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(ProcessControlController.class)
@TestPropertySource("classpath:bootstrap.yml")
@ContextConfiguration(
    classes = {
        ServiceDiscoveryAutoConfigurationImportSelector.class,
        ServiceDiscoveryBootstrapConfiguration.class
    })
@Slf4j
 class FileStorageControllerTest {
  private static final String URL = "/v1/storage";
  @MockBean
  private FileStorageService fileStorageService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(
            new FileStorageController(fileStorageService)
        ).setControllerAdvice(ProcessControlGlobalExceptionHandler.class)
        .build();
  }

  @Test
  void testUploadBackgroundFile_ThenReturnSuccess() throws Exception{
    String flowId = "1";

    when(this.fileStorageService.uploadResource(any(MultipartFile.class), anyString(),anyString()))
        .thenReturn(ConstantProperties.BACKGROUND_FILE_META_DATA_RESPONSE_DTO);

    var result = this.mockMvc.perform(
        multipart(URL + "/store/background-file")
            .file(ConstantProperties.MOCK_MULTIPART_FILE)
            .param("flowId", flowId)
    ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    log.info("Result: {}", result);
    Assertions.assertNotNull(result, "Response must be not null");
  }

  @Test
  void testDeleteBackgroundFile_ThenReturnSuccess() throws Exception {
    String fileId = "1";
    String flowId = "1";

    doNothing().when(this.fileStorageService).removeFile(anyString(), anyString());

    this.mockMvc.perform(
        delete(URL + "/store/background-file")
            .param("fileId", fileId)
            .param("flowId", flowId)
    ).andExpect(status().isNoContent());
  }
}
