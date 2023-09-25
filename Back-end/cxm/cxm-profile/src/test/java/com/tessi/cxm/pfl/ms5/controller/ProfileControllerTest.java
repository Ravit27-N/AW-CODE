package com.tessi.cxm.pfl.ms5.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.ProfileDto;
import com.tessi.cxm.pfl.ms5.exception.ProfileNotFoundException;
import com.tessi.cxm.pfl.ms5.service.ClientService;
import com.tessi.cxm.pfl.ms5.service.ProfileService;
import com.tessi.cxm.pfl.ms5.service.UserService;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner.UserDetailsOwner;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * To testing functionality of {@link ProfileController}
 *
 * @author Sakal TUM
 * @author Sokhour LACH
 * @see 16/12/2021
 */
@WebMvcTest(
    value = ProfileController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {ProfileController.class, ProfileGlobalExceptionHandler.class})
@MockBeans({@MockBean(ProfileService.class), @MockBean(ClientService.class)})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class ProfileControllerTest {

  private static final long CLIENT_ID = 1L;
  private static final long PROFILE_ID = 1L;
  private static final String PROFILE_ID_JSON_PATH = "$[0].id";
  private static final String URL = "/v1/profiles";
  private static final String URL_GET_PROFILE_DETAILS_BY_ID = URL + "/{id}/details";
  private static final String URL_CREATE_PROFILE = URL + "/create";
  private static final String URL_UPDATE_PROFILE = URL + "/update";
  private static final String URL_FIND_PROFILE_BY_ID = URL + "/{id}";
  private static final String URL_GET_FUNCTIONALITIES =
      URL + "/get-functionalities-by-current-user";
  private final ProfileDto profileDto = ProfileUnitTestConstants.SAMPLE_PROFILE_DTO;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private ProfileService profileService;
  @MockBean private ClientService clientService;

  @MockBean UserService userService;

  @Test
  @Order(1)
  void successGetProfileDetailsByID() throws Exception {
    // Stub
    Mockito.when(this.profileService.getProfileDetailsByProfileId(PROFILE_ID))
        .thenReturn(List.of(ProfileUnitTestConstants.SAMPLE_PROFILE_DETAILS_DTO));
    // Call & verify
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                        URL_GET_PROFILE_DETAILS_BY_ID, String.valueOf(PROFILE_ID))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath(PROFILE_ID_JSON_PATH, Matchers.equalTo((int) PROFILE_ID)))
            .andReturn();

    log.info("{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void successGetProfileDetailsByIDReturnEmptyArrayWhenProfileIsNotFound() throws Exception {
    // Stub
    Mockito.when(this.profileService.getProfileDetailsByProfileId(PROFILE_ID))
        .thenReturn(Collections.emptyList());
    // Call & verify
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL_GET_PROFILE_DETAILS_BY_ID, String.valueOf(PROFILE_ID))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", Matchers.hasSize(0)))
        .andReturn();
  }

  @Test
  @Order(3)
  @DisplayName("test create a profile with status 200")
  void testCreateProfile() throws Exception {
    given(profileService.createProfile(profileDto)).willReturn(profileDto);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(URL_CREATE_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.profileDto)))
        .andExpect(status().isOk());
  }

  @Test
  @Order(4)
  @DisplayName("test create a profile with status 400")
  void testCreateProfile_withBadRequest() throws Exception {
    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL_CREATE_PROFILE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(new ProfileDto())))
            .andExpect(status().isBadRequest())
            .andReturn();

    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(5)
  @DisplayName("test update profile with status 200")
  void testUpdateProfile_withSuccess() throws Exception {
    given(profileService.update(profileDto)).willReturn(profileDto);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.put(URL_UPDATE_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.profileDto)))
        .andExpect(status().isOk());
  }

  @Test
  @Order(6)
  @DisplayName("test update a profile with status 404")
  void testUpdateProfile_withNotFound() throws Exception {
    BDDMockito.willThrow(new ProfileNotFoundException(1L)).given(this.profileService).update(any());

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL_UPDATE_PROFILE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(this.profileDto)))
            .andExpect(status().isNotFound())
            .andReturn();

    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(7)
  @DisplayName("Test find a profile by id with status 404")
  void testFindProfileById_withNotFound() throws Exception {
    given(this.profileService.findById(anyLong())).willThrow(new ProfileNotFoundException(1L));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL_FIND_PROFILE_BY_ID, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(this.profileDto)))
            .andExpect(status().isNotFound())
            .andReturn();

    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(8)
  @DisplayName("Test get all functionalities of the client.")
  void testFindAllFunctionalities() throws Exception {
    var mockFunctionalities =
        List.of("cxm_client_management", "cxm_user_management", "cxm_template");
    // Stub
    Mockito.when(this.clientService.getFunctionalitiesByCurrentInvokedUser(CLIENT_ID))
        .thenReturn(mockFunctionalities);
    // Call & verify
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(URL_GET_FUNCTIONALITIES)
                .param("clientId", "1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", Matchers.hasSize(3)))
        .andDo(MockMvcResultHandlers.print())
        .andReturn();
  }

  @Test
  @Order(9)
  @DisplayName("Test get privilege and related owner by function and privilege key")
  void testGetPrivilegeDetail() throws Exception {
    UserPrivilegeDetails userPrivilegeDetails =
        new UserPrivilegeDetails("visibilityLevel", "client", false, List.of(1L));
    // Stub
    Mockito.when(
            this.profileService.getUserPrivilegeDetails(
                anyBoolean(), anyString(), anyString(), anyBoolean()))
        .thenReturn(userPrivilegeDetails);
    // Call & verify
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    URL + "/users/{functionalKey}/{privilegeKey}",
                    "cxm_template",
                    "cxm_template_create_from_scratch")
                .param("isVisibility", "true")
                .param("getRelatedUsers", "true")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @Order(10)
  @DisplayName("Test get privilege and related owner with details by function and privilege key")
  void testSuccessGetRelatedOwnerDetails(boolean isGettingRelatedOwners) throws Exception {
    final var functionKey = "cxm_template";
    final var privilegeKey = "cxm_template_create_from_scratch";
    var userDetails1 = UserDetailsOwner.builder().build();
    UserPrivilegeDetailsOwner userPrivilegeDetails =
        new UserPrivilegeDetailsOwner("visibilityLevel", "client", false, List.of(userDetails1));
    UserPrivilegeDetailsOwner userPrivilegeDetailsWithEmptyUsers =
        new UserPrivilegeDetailsOwner("visibilityLevel", "client", false, List.of());
    // Stub
    Mockito.when(
            this.profileService.getUserPrivilegeDetailsOwner(
                anyBoolean(), anyString(), anyString(), eq(true)))
        .thenReturn(userPrivilegeDetails);
    Mockito.when(
            this.profileService.getUserPrivilegeDetailsOwner(
                anyBoolean(), anyString(), anyString(), eq(false)))
        .thenReturn(userPrivilegeDetailsWithEmptyUsers);
    // Call & verify
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    URL + "/users/privilege-related-owners-details/{functionalKey}/{privilegeKey}",
                    functionKey,
                    privilegeKey)
                .param("isVisibility", "true")
                .param("getRelatedUsers", String.valueOf(isGettingRelatedOwners))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.privilegeType").value(userPrivilegeDetails.getPrivilegeType()))
        .andExpect(jsonPath("$.level").value(userPrivilegeDetails.getLevel()))
        .andExpect(
            jsonPath("$.nonLevelPrivilege").value(userPrivilegeDetails.isNonLevelPrivilege()))
        .andExpect(
            jsonPath("$.userDetailsOwners", Matchers.hasSize(isGettingRelatedOwners ? 1 : 0)));
  }
}
