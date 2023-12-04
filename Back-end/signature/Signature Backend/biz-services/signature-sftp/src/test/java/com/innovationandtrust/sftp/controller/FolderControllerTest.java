package com.innovationandtrust.sftp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.sftp.constant.Constant;
import com.innovationandtrust.sftp.service.SFTPGoService;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration("classpath:application-test.yml")
@Slf4j
class FolderControllerTest {
  @InjectMocks FolderController folderController;
  @Mock
  SFTPGoService sftpGoService;
  private MockMvc mockMvc;

  private static final String URL = "/v1/folders";

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(folderController).build();
  }

  @Test
  @Order(1)
  @DisplayName("Create corporate folder")
  void testCreateCorporateFolder() throws Exception {
    var result =
        mockMvc
            .perform(
                post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("corporateUuid", "1313c0e7-7703-45e8-b409-b3fd58e18beb"))
            .andExpect(status().isCreated())
            .andReturn();

    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  @DisplayName("Create corporate user")
  void testCreateCorporateUserSuccess() throws Exception {
    when(sftpGoService.createUserAndFolderInSFTPGo(any(SFTPUserFolderRequest.class)))
        .thenReturn(Constant.getUserFolderResponse());
    var result =
        mockMvc
            .perform(
                post(URL + "/sftpgo/user-folder")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        new ObjectMapper().writeValueAsString(Constant.getUserFolderRequest())))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(3)
  @DisplayName("Create corporate user")
  void testCreateCorporateUserBadRequest() throws Exception {
    var req = Constant.getUserFolderRequest();
    req.setUsername(null);
    var result =
        mockMvc
            .perform(
                post(URL + "/sftpgo/user-folder")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andReturn();

    log.info("ApiError :{}", result.getResponse().getContentAsString());
  }
}
