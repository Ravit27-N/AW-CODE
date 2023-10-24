package com.innovationandtrust.profile.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.profile.service.CompanySettingService;
import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CompanySettingControllerTest {
  @InjectMocks CompanySettingController companySettingController;
  @Mock CompanySettingService companySettingService;
  CompanySettingDto companySettingDto;

  List<CompanySettingDto> companySettingDtoList;

  private MockMvc mockMvc;

  private String companyUuid;

  @Resource(name = "feignClientHeader")

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(companySettingController).build();

    companySettingController =
        new CompanySettingController(companySettingService);

    companyUuid = "244eb546-2343-41d4-8c47-9c5d1ec947e0";

    companySettingDto =
        CompanySettingDto.builder()
            .id(1L)
            .signatureLevel(SignatureSettingLevel.SIMPLE.getValue())
            .companyUuid(companyUuid)
            .channelReminder(NotificationConstant.SMS)
            .fileType(new HashSet<>())
            .build();

    companySettingDtoList = Collections.singletonList(companySettingDto);
  }

  @Test
  @DisplayName("Get all company settings")
  void get_all_company_settings() throws Exception {
    // when
    when(companySettingService.getAll(anyString())).thenReturn(companySettingDtoList);

    ResultActions result =
        mockMvc.perform(
            get("/v1/company/settings")
                .param("uuid", companyUuid)
                .accept(MediaType.APPLICATION_JSON));

    result
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().string(new ObjectMapper().writeValueAsString(companySettingDtoList)));
  }

  @Test
  @DisplayName("Save or Update company settings")
  void save_or_update_company_settings() throws Exception {
    // when
    when(companySettingService.save(anyList())).thenReturn(companySettingDtoList);

    ResultActions result =
        mockMvc.perform(
            post("/v1/company/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(companySettingDtoList))
                .accept(MediaType.APPLICATION_JSON));

    result
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().string(new ObjectMapper().writeValueAsString(companySettingDtoList)));
  }

  @Test
  @DisplayName("Update company themes and logo")
  void update_company_themes_and_logo() throws Exception {
    // given
    var corporateSetting = new CorporateSettingDto();
    corporateSetting.setCompanyId(1L);
    corporateSetting.setMainColor("#880808");

    log.info("Main color: " + corporateSetting.getMainColor());

    var logoFile =
        new MockMultipartFile(
            "logoFile", "logoFile.png", null, "logoFile".getBytes(StandardCharsets.UTF_8));

    // when
    when(companySettingService.updateCompanyThemesAndLogo(any(), any()))
        .thenReturn(corporateSetting);

    ResultActions result =
        mockMvc.perform(
            put("/v1/company/settings/themes")
                .flashAttr("logoFile", logoFile)
                .flashAttr("dto", corporateSetting)
                .flashAttr("headers", new HttpHeaders())
                .contentType(MediaType.MULTIPART_FORM_DATA));
    result
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().string(new ObjectMapper().writeValueAsString(corporateSetting)));
  }
}
