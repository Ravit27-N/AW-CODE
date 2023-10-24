package com.innovationandtrust.profile.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.profile.model.dto.CorporateUserDto;
import com.innovationandtrust.profile.service.CorporateUserService;
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
class CorporateUserControllerTests {
    @InjectMocks CorporateUserController controller;
    @Mock
    CorporateUserService corporateUserService;
    @Mock
    CorporateUserDto dto;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    @Test
    @DisplayName("Save corporate user api test")
    void save_corporate_user_api_test() throws Exception {
        corporateUserService.save(dto);
        mockMvc
                .perform(
                        post("/v1/corporate/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"companyId\":\"1\",\"userId\":\"1\"}"))
                .andExpect(status().isOk());

        // then
        verify(corporateUserService, times(1)).save(dto);
    }
    @Test
    @DisplayName("Update corporate user api test")
    void update_corporate_user_api_test() throws Exception {
        corporateUserService.update(dto);
        mockMvc
                .perform(
                        put("/v1/corporate/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"companyId\":\"1\",\"userId\":\"1\"}"))
                .andExpect(status().isOk());

        // then
        verify(corporateUserService, times(1)).update(dto);
    }
}
