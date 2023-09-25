package com.allweb.rms.controller;

import com.allweb.rms.entity.jpa.CompanyProfile;
import com.allweb.rms.entity.dto.CompanyProfileDTO;
import com.allweb.rms.security.AuthenticationEntryPointJwt;
import com.allweb.rms.service.CompanyProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.ws.rs.core.MediaType;
import java.util.Date;

@Log4j2
@ExtendWith(SpringExtension.class)
@WebMvcTest(CompanyProfileController.class)
@ContextConfiguration(classes = AuthenticationEntryPointJwt.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompanyProfileControllerTest {

    @MockBean
    CompanyProfileService service;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final CompanyProfileDTO request = new CompanyProfileDTO(1, "ALLWEB Co., Ltd", "Description", "Phnom Penh", "099999999", "admin@allweb.com.kh", "www.allweb.com", new Date(), new Date());
    private final CompanyProfile response = new CompanyProfile(1, "ALLWEB Co., Ltd", "Description", "Phnom Penh", "099999999", "admin@allweb.com.kh", "www.allweb.com");
    // Url
    private static final String URL = "/api/v1/company/profile";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CompanyProfileController(service)).build();
    }

    @Test
    @Order(1)
    @DisplayName("Should fetch first company profile")
    void shouldFetchFirst() throws Exception {
        BDDMockito.given(service.getCompanyProfile()).willReturn(request);
        MockHttpServletResponse response = this.mockMvc.perform(MockMvcRequestBuilders.get(URL).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        log.info("{}", response.getContentAsString());
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("when save then return status 200")
    void shouldSaveThenReturn200() throws Exception {
        BDDMockito.given(service.createCompanyProfile(request)).willReturn(request);
        BDDMockito.given(service.convertToEntity(request)).willReturn(this.response);
        MockHttpServletResponse response = this.mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.response))).andReturn().getResponse();
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @Order(3)
    @DisplayName("when valid field required then return status 400")
    void whenValidFieldRequiredIsNotEmptyThenReturns400() throws Exception {
        CompanyProfile res = new CompanyProfile(1, null, "Description", "Phnom Penh", null, null, null);
        BDDMockito.given(service.convertToDTO(res)).willReturn(this.request);
        BDDMockito.given(service.createCompanyProfile(this.request)).willReturn(request);

        MockHttpServletResponse response = this.mockMvc.perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(res))).andReturn().getResponse();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }
}
