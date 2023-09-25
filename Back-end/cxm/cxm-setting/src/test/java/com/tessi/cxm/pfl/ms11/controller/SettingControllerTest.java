package com.tessi.cxm.pfl.ms11.controller;

import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.CUSTOMER_NAME;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.FUNCTIONALITIES;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.SETTING_LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.will;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cxm.tessi.pfl.shared.flowtreatment.model.response.BatchSettingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.DepositValidation;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.DepositValidation.Content;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PreProcessingSettingResponseDto;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.SettingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms11.config.InternalConfig;
import com.tessi.cxm.pfl.ms11.exception.CustomerNotFoundException;
import com.tessi.cxm.pfl.ms11.service.SettingService;
import com.tessi.cxm.pfl.ms11.util.ConstantProperties;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.PortalSettingConfigStatusDto;
import com.tessi.cxm.pfl.shared.model.ProfileClientSettingRequest;
import com.tessi.cxm.pfl.shared.utils.CustomerDepositModeDto;
import com.tessi.cxm.pfl.shared.utils.DepositModeConstant;
import java.util.List;
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
    value = SettingController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
    classes = {SettingController.class, SettingGlobalExceptionHandler.class, InternalConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class SettingControllerTest {

  private static final String URL = "/v1/setting";
  private static final String URL_DEPOSIT_VALIDATION =
      URL + "/deposit-validation/{customer}/{depositType}/{extension}";
  private static final String URL_EXTRACT_CHANNEL_SUB_CHANNEL = URL + "/identification";
  private static final String URL_DOCUMENT_INSTRUCTION = URL + "/pre-processing/{idCreator}";

  private static final String MSG_FINISHED = "Finished";

  @MockBean SettingService settingService;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  @Order(1)
  void testGetDepositValidation() throws Exception {
    var depositValidation =
        new FlowProcessingResponse<>(
            MSG_FINISHED,
            HttpStatus.OK,
            new DepositValidation(true,true, new Content("ENI/Batch/zip", 1L)));

    given(
            this.settingService.getDepositValidation(
                anyString(), anyString(), anyString(), anyString()))
        .willReturn(depositValidation);

    var result =
        this.mockMvc
            .perform(
                get(URL_DEPOSIT_VALIDATION, "eni", "batch", "zip")
                    .param("connector", "")
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(depositValidation)))
            .andReturn()
            .getResponse();

    this.loggingActualResult(result.getContentAsString());
  }

  @Test
  @Order(2)
  void testGetDepositValidation_WithEmptyObject() throws Exception {
    given(
            this.settingService.getDepositValidation(
                anyString(), anyString(), anyString(), anyString()))
        .willReturn(new FlowProcessingResponse<>(MSG_FINISHED, HttpStatus.OK));

    var result =
        this.mockMvc
            .perform(
                get(URL_DEPOSIT_VALIDATION, "eni", "batch", "zip")
                    .param("connector", "")
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(MSG_FINISHED))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").isEmpty())
            .andReturn()
            .getResponse();

    this.loggingActualResult(result.getContentAsString());
  }

  @Test
  @Order(3)
  void testExtractedChannelAndSubChannel() throws Exception {
    var mockResponse =
        new FlowProcessingResponse<SettingResponse>(
            MSG_FINISHED, HttpStatus.OK, new BatchSettingResponse("Digital", "Email", ""));

    given(this.settingService.extractSetting(anyString(), anyLong(), anyString()))
        .willReturn(mockResponse);

    var result =
        this.mockMvc
            .perform(
                get(URL_EXTRACT_CHANNEL_SUB_CHANNEL)
                    .param("flowType", "ENI/Batch/C1/zip")
                    .param("idCreator", "1")
                    .param("flowName", "Flow name test")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsBytes(mockResponse)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    this.loggingActualResult(result.getContentAsString());
  }

  @Test
  @Order(4)
  void testGetInstructionDetails() throws Exception {

    var mockResponse =
        new FlowProcessingResponse<>(
            MSG_FINISHED,
            HttpStatus.OK,
            PreProcessingSettingResponseDto.builder()
                .emailObject("Column 7")
                .address("18 to 21")
                .pjs("17 and 11 to 14")
                .idBreakingPage("/XXX/")
                .build());
    given(this.settingService.getInstructionDetails(anyString(), anyString(), anyLong()))
        .willReturn(mockResponse);

    var result =
        this.mockMvc
            .perform(
                get(URL_DOCUMENT_INSTRUCTION, "1")
                    .param("flowType", "ENI/Batch/C1/zip")
                    .param("modelName", "XXX")
                    .param("idCreator", "1")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsBytes(mockResponse)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    this.loggingActualResult(result.getContentAsString());
  }

  /** To show logging results. */
  private void loggingActualResult(String result) {
    log.info("Actual result: {}.", result);
  }

  @Test
  @Order(5)
  void createFlowType() throws Exception {
    willDoNothing().given(settingService).createClientSetting(any(), anyString());
    ProfileClientSettingRequest profileClientSettingRequest =
        new ProfileClientSettingRequest(CUSTOMER_NAME, FUNCTIONALITIES);
    var result =
        this.mockMvc
            .perform(
                post(URL + "/flow-type")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsBytes(profileClientSettingRequest)))
            .andExpect(status().isNoContent());
    log.info("Request :{}", profileClientSettingRequest);
    log.info("Response :{}", SETTING_LIST);
  }

  @Test
  @Order(6)
  void testSuccessModifyConfiguration() throws Exception {
    var payload =
        PortalSettingConfigStatusDto.builder().clientName("Client 1").isActive(true).build();

    when(this.settingService.modifiedPortalSettingConfig(any(), anyString())).thenReturn(payload);

    this.mockMvc
        .perform(
            put(URL + "/portal/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isOk())
        .andReturn();
  }

  @Test
  @Order(7)
  void testNoPrivilegeToModifyConfiguration() throws Exception {
    var payload =
        PortalSettingConfigStatusDto.builder().clientName("Client 1").isActive(true).build();

    when(this.settingService.modifiedPortalSettingConfig(any(), any()))
        .thenThrow(new UserAccessDeniedExceptionHandler());

    this.mockMvc
        .perform(
            put(URL + "/portal/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetDepositMode() throws Exception {
    CustomerDepositModeDto customerDepositModeDto = new CustomerDepositModeDto();
    customerDepositModeDto.setKey("flow.traceability.deposit.mode.portal");
    customerDepositModeDto.setValue(DepositModeConstant.PORTAL_DEPOSIT);
    customerDepositModeDto.setScanActivation(true);
    given(this.settingService.getDepositModes(anyString()))
        .willReturn(List.of(customerDepositModeDto));

    var result =
        this.mockMvc
            .perform(
                get(URL + "/deposit-modes/{customer}", CUSTOMER_NAME)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsBytes(List.of(customerDepositModeDto))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    this.loggingActualResult(result.getContentAsString());
  }

  @Test
  void testGetDepositModeNotFound() throws Exception {
    CustomerDepositModeDto customerDepositModeDto = new CustomerDepositModeDto();
    customerDepositModeDto.setKey("flow.traceability.deposit.mode.portal");
    customerDepositModeDto.setValue(DepositModeConstant.PORTAL_DEPOSIT);
    customerDepositModeDto.setScanActivation(true);
    given(this.settingService.getDepositModes(anyString()))
        .willReturn(List.of(customerDepositModeDto));
    willThrow(new CustomerNotFoundException(CUSTOMER_NAME))
        .given(this.settingService)
        .getDepositModes(anyString());

    var result =
        this.mockMvc
            .perform(
                get(URL + "/deposit-modes/{customer}", CUSTOMER_NAME)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsBytes(List.of(customerDepositModeDto))))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse();

    this.loggingActualResult(result.getContentAsString());
  }

  @Test
  void testSavePortal() throws Exception {
    CustomerDepositModeDto customerDepositModeDto = new CustomerDepositModeDto();
    customerDepositModeDto.setKey("flow.traceability.deposit.mode.portal");
    customerDepositModeDto.setValue(DepositModeConstant.PORTAL_DEPOSIT);
    customerDepositModeDto.setScanActivation(true);
    given(this.settingService.createOrModifiedClientSetting(anyString(), anyList(), anyString()))
        .willReturn(List.of(customerDepositModeDto));
    var result =
        this.mockMvc
            .perform(
                post(URL + "/create/{customer}", CUSTOMER_NAME)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsBytes(List.of(customerDepositModeDto))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
    this.loggingActualResult(result.getContentAsString());

    }
    @Test
  void testModifiedPortalConfiguration_ThenReturnSuccess() throws Exception {
    given(this.settingService.modifiedINIConfiguration(any(), anyString()))
        .willReturn(ConstantProperties.POSTAL_CONFIGURATION_DTO);

    var result = this.mockMvc.perform(
            put(URL + "/portal/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsBytes(ConstantProperties.POSTAL_CONFIGURATION_DTO))
        ).andExpect(status().isOk())
        .andReturn().getResponse();

    this.loggingActualResult(result.getContentAsString());
  }
}
