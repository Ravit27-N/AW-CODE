package com.innovationandtrust.process.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.service.ApprovalProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class ExternalApprovingProcessControllerTests {
  @InjectMocks ExternalApprovingProcessController controller;
  @Mock ApprovalProcessingService approvalProcessingService;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @DisplayName("Approve processing")
  @Test
  void testApproveProcessing() throws Exception {
    approvalProcessingService.approveExternal(
        UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    mockMvc
        .perform(
            post("/approval/approve/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(approvalProcessingService, times(2))
        .approveExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }

  @DisplayName("Read external")
  @Test
  void testReadExternal() throws Exception {
    approvalProcessingService.readExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
    mockMvc
        .perform(
            post("/approval/read/{companyUuid}", UnitTestConstant.COMPANY_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", UnitTestConstant.TOKEN))
        .andExpect(status().isOk());

    // then
    verify(approvalProcessingService, times(2))
        .readExternal(UnitTestConstant.COMPANY_UUID, UnitTestConstant.TOKEN);
  }
}
