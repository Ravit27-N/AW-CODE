package com.tessi.cxm.pfl.ms3.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.dto.FlowDepositDto;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.service.FlowDepositService;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(FlowDepositController.class)
@ContextConfiguration(classes = {FlowDepositController.class, InternalConfig.class})
@Slf4j
class FlowDepositControllerTest {
  private static final String URL = "/v1/flow-deposit";
  @MockBean private FlowDepositService flowDepositService;
  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(new FlowDepositController(this.flowDepositService))
            .setControllerAdvice(FlowTraceabilityGlobalExceptionHandler.class)
            .build();
  }

  @Test
  void testFindAllFlowDeposit() throws Exception {
    Page<FlowDepositDto> mockResult =
        new PageImpl<>(Collections.singletonList(new FlowDepositDto()));
    given(this.flowDepositService.findAll(any(FlowFilterCriteria.class), any(Pageable.class),
        anyString())).willReturn(mockResult);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/{page}/{pageSize}", 1, 10)
                    .param("sortByField", "createdAt")
                    .param("sortDirection", "DESC")
                    .param("filter", "")
                    .param("channels", "")
                    .param("categories", "")
                    .param("users", "")
                    .param("fileId", "123"))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Result => {}", result.getResponse().getContentAsString());
  }

  @Test
  void testDeleteFlowDeposit() throws Exception {

    doNothing().when(this.flowDepositService).delete(anyLong());
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.patch(URL + "/delete/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
