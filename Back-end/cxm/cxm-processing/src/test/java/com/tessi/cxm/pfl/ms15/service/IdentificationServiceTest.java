package com.tessi.cxm.pfl.ms15.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms15.service.restclient.Go2pdfResource;
import com.tessi.cxm.pfl.ms15.service.restclient.SettingFeignClient;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.BatchSettingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.SettingResponse;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@Slf4j
class IdentificationServiceTest {

  @Mock
  private FileManagerResource fileManagerResource;
  @Mock
  private SettingFeignClient settingFeignClient;
  @Mock
  private Go2pdfResource go2PdfResource;
  @Mock
  private KeycloakService keycloakService;
  @InjectMocks
  IdentificationService identificationService;

  @BeforeEach
  void setUp() {
    ModelMapper modelMapper = new ModelMapper();
    this.identificationService =
        new IdentificationService(
            settingFeignClient, modelMapper, keycloakService, go2PdfResource, fileManagerResource);
  }

  @Test
  void shouldReturnChannelAndSubChannel_whenGetRequest() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      // mock objects
      var mockResponse =
          new FlowProcessingResponse<SettingResponse>(
              "Finished", HttpStatus.OK, new BatchSettingResponse("Digital", "Email", ""));

      // when
      when(this.keycloakService.getToken()).thenReturn("Token");
      when(this.settingFeignClient.extractSetting(
          anyString(), anyLong(), anyString(), anyString()))
          .thenReturn(mockResponse);

      var result =
          this.identificationService.extractChannelAndSubChannelStep(
              "FILE_ID", "FLOW_NAME", 1L, "ENI/Batch/C1/zip", "", "");

      // expected result.
      var response = (ProcessCtrlIdentificationResponse) result.getData();
      assertEquals("Finished", result.getMessage());
      assertEquals(200, result.getStatus());
      assertEquals("Digital", response.getChannel());
      assertEquals("Email", response.getSubChannel());

      log.info("Actual response: {}.", result);
    }
  }

  @Test
  void shouldReturnEmptyObject_whenGetRequest() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      // mock objects
      var mockResponse = new FlowProcessingResponse<SettingResponse>("Finished", HttpStatus.OK);

      // when
      when(this.keycloakService.getToken()).thenReturn("Token");
      when(this.settingFeignClient.extractSetting(
          anyString(), anyLong(), anyString(), anyString()))
          .thenReturn(mockResponse);

      var result =
          this.identificationService.extractChannelAndSubChannelStep(
              "FILE_ID", "FLOW_NAME", 1L, "ENI/Batch/C1/zip", "", "");

      // expected result.
      assertEquals("Finished", result.getMessage());
      assertEquals(200, result.getStatus());
      assertEquals("{}", result.getData().toString());

      log.info("Actual response: {}.", result);
    }
  }
}
