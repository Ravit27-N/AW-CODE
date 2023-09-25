package com.tessi.cxm.pfl.ms5.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.UserInfoRequestUpdatePasswordDto;
import com.tessi.cxm.pfl.ms5.dto.UserRequestResetPasswordDto;
import com.tessi.cxm.pfl.ms5.dto.UserResetPasswordDto;
import com.tessi.cxm.pfl.ms5.exception.EmailInvalidPatternException;
import com.tessi.cxm.pfl.ms5.exception.KeycloakUserNotFound;
import com.tessi.cxm.pfl.ms5.exception.TokenExpiredDateException;
import com.tessi.cxm.pfl.ms5.exception.UserRepresentationNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.BatchUserService;
import com.tessi.cxm.pfl.ms5.service.UserService;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryAutoConfigurationImportSelector;
import com.tessi.cxm.pfl.shared.discovery.config.auto.ServiceDiscoveryBootstrapConfiguration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(value = { UserPasswordController.class,
        UserController.class }, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        UserPasswordController.class,
        UserController.class,
        ProfileGlobalExceptionHandler.class,
        ServiceDiscoveryAutoConfigurationImportSelector.class,
        ServiceDiscoveryBootstrapConfiguration.class
})
@MockBeans({ @MockBean(UserService.class) })
@Slf4j
class UserControllerTest {

    private final String PUBLIC_URL = "/public/users";
    private final String USER_URL = "/v1/users";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BatchUserService batchUserService;
    @Mock
    UserController userController = new UserController(userService,userRepository, batchUserService);

    @Test
    @Order(1)
    void testRequestResetPassword() throws Exception {
        final UserRequestResetPasswordDto userRequestResetPasswordDto = new UserRequestResetPasswordDto(
                "pisey@gmail.com");
        when(userService.requestForResetPassword(any(UserRequestResetPasswordDto.class)))
                .thenReturn(userRequestResetPasswordDto);
        final MvcResult result = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post(PUBLIC_URL + "/request/reset-password")
                                .content(objectMapper.writeValueAsString(""))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        log.info("Response :{}", result.getResponse().getContentAsString());
    }

    @Test
    @Order(2)
    void testResetPassword() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        final UserResetPasswordDto userRequestResetPasswordDto = new UserResetPasswordDto(uuid, uuid);
        when(userService.resetPassword(any(UserResetPasswordDto.class)))
                .thenReturn(userRequestResetPasswordDto);

        final MvcResult result = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post(PUBLIC_URL + "/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestResetPasswordDto)))
                .andExpect(status().isOk())
                .andReturn();
        log.info("Response :{}", result.getResponse().getContentAsString());
    }

  @Test
  @Order(3)
  void testValidateInvalidEmail() throws Exception {
    when(this.userService.requestForResetPassword(any(UserRequestResetPasswordDto.class)))
        .thenThrow(new EmailInvalidPatternException("Email is invalid pattern"));
    final MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(PUBLIC_URL + "/request/reset-password")
                    .content(objectMapper.writeValueAsString(""))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(4)
  void testRequestResetPasswordWithUserNotFound() throws Exception {
    when(this.userService.requestForResetPassword(any(UserRequestResetPasswordDto.class)))
        .thenThrow(new UserRepresentationNotFoundException("User not found in keycloak server"));
    final MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(PUBLIC_URL + "/request/reset-password")
                    .content(objectMapper.writeValueAsString(""))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

    @Test
    @Order(5)
    void testResetPasswordWithUserNotFound() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        final UserResetPasswordDto userRequestResetPasswordDto = new UserResetPasswordDto(uuid, uuid);
        when(userService.resetPassword(any(UserResetPasswordDto.class)))
                .thenThrow(new KeycloakUserNotFound("Keycloak user not found"));

        final MvcResult result = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post(PUBLIC_URL + "/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestResetPasswordDto)))
                .andExpect(status().isNotFound())
                .andReturn();
        log.info("Response :{}", result.getResponse().getContentAsString());
    }

    @Test
    @Order(6)
    void testResetPasswordWithTokenExpired() throws Exception {
        final String uuid = UUID.randomUUID().toString();
        final UserResetPasswordDto userRequestResetPasswordDto = new UserResetPasswordDto(uuid, uuid);
        when(userService.resetPassword(any(UserResetPasswordDto.class)))
                .thenThrow(new TokenExpiredDateException("token is expired"));

        final MvcResult result = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post(PUBLIC_URL + "/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestResetPasswordDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        log.info("Response :{}", result.getResponse().getContentAsString());
    }

  @Test
  @Order(7)
  void testGetUserInfoSuccess() throws Exception {
    when(userService.getUserInfoByToken())
        .thenReturn(ProfileUnitTestConstants.QUERY_USER_RESPONSE_DTO);

    final MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(USER_URL + "/user-info")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(8)
  void testGetUserInfoFail() throws Exception {
    when(userService.getUserInfoByToken())
        .thenThrow(new KeycloakUserNotFound("Keycloak user not found"));

    final MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(USER_URL + "/user-info")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();

    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(9)
  void testUpdateUserPasswordSuccess() throws Exception {
        when(this.userService.updateUserPassword(any(UserInfoRequestUpdatePasswordDto.class)))
            .thenReturn(ProfileUnitTestConstants.USER_INFO_REQ_UPDATE_PASSWORD_DTO);

    final MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(USER_URL + "/update-user-password")
                    .content(
                        objectMapper.writeValueAsString(
                            ProfileUnitTestConstants.USER_INFO_REQ_UPDATE_PASSWORD_DTO))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(9)
  void testUpdateUserPasswordFail() throws Exception {
    when(this.userService.updateUserPassword(any(UserInfoRequestUpdatePasswordDto.class)))
        .thenThrow(new KeycloakUserNotFound("Keycloak user not found"));

    final MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(USER_URL + "/update-user-password")
                    .content(
                        objectMapper.writeValueAsString(
                            ProfileUnitTestConstants.USER_INFO_REQ_UPDATE_PASSWORD_DTO))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();

    log.info("Response: {}", result.getResponse().getContentAsString());
  }

    // add new
    @Test
    @Order(10)
    void testbuildSortBy() {

        assertNotNull(userController.buildSortBy("test", "desc"));

        String sortByField = "test";
        String sortDirection = "asc";
        Sort expectedSort = Sort.by(Sort.Direction.ASC, "test");

        Sort actualSort = userController.buildSortBy(sortByField, sortDirection);

        assertEquals(expectedSort, actualSort);
    }
}
