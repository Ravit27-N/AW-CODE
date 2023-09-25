package com.tessi.cxm.pfl.ms5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.exception.UserAPIFailureException;
import com.tessi.cxm.pfl.ms5.service.UserHubService;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubAccount;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatchers;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = UserHubController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {UserHubController.class, ProfileGlobalExceptionHandler.class})
@MockBeans({@MockBean(UserHubService.class)})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class UserHubControllerTest {
  private static final String URL = "/v1/user-hub";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private UserHubService userHubService;
  private static final String TEST_USERNAME = "user@testing.com";

  @Test
  @Order(1)
  void testGetUserHubAccount() throws Exception {
    Mockito.when(this.userHubService.getUserHubByTechnicalRef())
        .thenReturn(new UserHubAccount(TEST_USERNAME, TEST_USERNAME));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/user-account")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void testGetUserHubAccountByUsername() throws Exception {
    Mockito.when(this.userHubService.getUserHub(ArgumentMatchers.anyString()))
        .thenReturn(new UserHubAccount(TEST_USERNAME, TEST_USERNAME));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/user-account/{username}", TEST_USERNAME)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(3)
  void testRegisterUserHubAccount() throws Exception {
    Mockito.when(this.userHubService.registerUserHub(any()))
        .thenReturn(new UserHubRequestDto(TEST_USERNAME, TEST_USERNAME, TEST_USERNAME));
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL + "/user-account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new UserHubRequestDto(TEST_USERNAME, TEST_USERNAME, TEST_USERNAME))))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(3)
  void testRegisterUserHubAccountFail() throws Exception {

    Mockito.doThrow(new UserAPIFailureException("Fail to create user API in hub-digitalflow"))
        .when(this.userHubService)
        .registerUserHub(any());
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL + "/user-account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new UserHubRequestDto(TEST_USERNAME, TEST_USERNAME, TEST_USERNAME))))
            .andExpect(status().isInternalServerError())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }
}
