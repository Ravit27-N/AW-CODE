package com.tessi.cxm.pfl.ms11.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms11.config.InternalConfig;
import com.tessi.cxm.pfl.ms11.service.CriteriaDistributionService;
import com.tessi.cxm.pfl.ms11.util.ConstantProperties;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
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

@WebMvcTest(
    value = CriteriaDistributionController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(
    classes = {
      CriteriaDistributionController.class,
      SettingGlobalExceptionHandler.class,
      InternalConfig.class
    })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CriteriaDistributionControllerTest {
  public static final String BASE_URL = "/v1/setting/criteria-distribution";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private CriteriaDistributionService criteriaDistributionService;

  @Test
  @Order(1)
  void testSuccessGetCriteriaDistribution() throws Exception {
    when(this.criteriaDistributionService.getCriteriaDistribution(any()))
        .thenReturn(new CriteriaDistributionsResponse());

    this.mockMvc
        .perform(
            get(BASE_URL)
                .param("clientName", "Client 1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }


  @Test
  @Order(2)
  void testSuccessUpdateCriteriaDistribution() throws Exception {
    when(this.criteriaDistributionService.updateCriteriaDistributions(
        ConstantProperties.CRITERIA_DISTRIBUTION_REQUEST))
        .thenReturn(ConstantProperties.CRITERIA_DISTRIBUTION_REQUEST);

    this.mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString((
                    ConstantProperties.CRITERIA_DISTRIBUTION_REQUEST))))
        .andExpect(status().isOk());
  }

  @Test
  @Order(3)
  void testFailUpdateCriteriaDistribution() throws Exception {
    when(this.criteriaDistributionService.updateCriteriaDistributions(
        ConstantProperties.CRITERIA_DISTRIBUTION_REQUEST))
        .thenReturn(ConstantProperties.CRITERIA_DISTRIBUTION_REQUEST);

    this.mockMvc
        .perform(
            post(BASE_URL).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());
  }
}
