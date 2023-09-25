package com.tessi.cxm.pfl.ms5.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.ClientDto;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.service.ClientService;
import com.tessi.cxm.pfl.shared.model.Configuration;
import com.tessi.cxm.pfl.shared.model.ConfigurationEntry;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationVersion;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationVersionDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    value = ClientController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {ClientController.class, ProfileGlobalExceptionHandler.class})
@MockBeans({@MockBean(ClientService.class)})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class ClientControllerTest {

  private final String URL = "/v1/clients";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private ClientService clientService;

  @Test
  @Order(1)
  void testCreateClient() throws Exception {
    when(clientService.save(any(ClientDto.class))).thenReturn(
        ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post(URL)
                    .content(
                        objectMapper.writeValueAsString(
                            ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(2)
  void testUpdateClient() throws Exception {
    when(clientService.update(any(ClientDto.class))).thenReturn(
        ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO);

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put(URL)
                    .content(
                        objectMapper.writeValueAsString(
                            ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(3)
  void testUpdateClientNotFound() throws Exception {
    when(clientService.update(any(ClientDto.class))).thenThrow(new ClientNotFoundException(1));
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.put(URL)
                .content(
                    objectMapper.writeValueAsString(ProfileUnitTestConstants.SAMPLE_CLIENT_1_DTO))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(4)
  void testDeleteClient() throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1))
        .andExpect(status().isNoContent());
  }

  @Test
  @Order(5)
  void testDeleteClientNotFound() throws Exception {
    doThrow(new ClientNotFoundException(1)).when(clientService).delete(anyLong());
    this.mockMvc
        .perform(MockMvcRequestBuilders.delete(URL + "/{id}", 1))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(6)
  void testGetAllClient() throws Exception {
    when(clientService.loadAllClients(any(Pageable.class), anyString())).thenReturn(
        new PageImpl<>(List.of(ProfileUnitTestConstants.LOAD_CLIENT)));

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/{page}/{pageSize}", 1, 10)
                .param("filter", "")
                .param("sortByField", "lastModified")
                .param("sortDirection", "desc")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(7)
  void testValidateDuplicateNameWithIdReturnTrue() throws Exception {
    when(clientService.validateDuplicateName(anyLong(), anyString())).thenReturn(true);

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/is-duplicate")
                .param("id", "1")
                .param("name", "example")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(8)
  void testValidateDuplicateNameWithIdReturnFail() throws Exception {
    when(clientService.validateDuplicateName(anyLong(), anyString())).thenReturn(false);

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/is-duplicate")
                .param("id", "1")
                .param("name", "example")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(9)
  void testValidateDuplicateNameWithoutIdReturnTrue() throws Exception {
    when(clientService.validateDuplicateName(anyLong(), anyString())).thenReturn(true);

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/is-duplicate")
                .param("name", "example")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(10)
  void testValidateDuplicateNameWithoutIdReturnFail() throws Exception {
    when(clientService.validateDuplicateName(anyLong(), anyString())).thenReturn(false);

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/is-duplicate")
                .param("name", "example")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(11)
  void testGetClientFillers_thenReturnSuccess() throws Exception {
    when(clientService.getAllClientFillers(anyLong()))
        .thenReturn(List.of(ProfileUnitTestConstants.SHARED_CLIENT_FILLERS_DTO));

    var result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get(URL + "/client-fillers")
                    .param("clientId", String.valueOf(1)))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(12)
  void testGetClientFillers_thenReturnEmpty() throws Exception {
    when(clientService.getAllClientFillers(anyLong())).thenReturn(Collections.emptyList());

    var result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get(URL + "/client-fillers"))
            .andExpect(status().isOk())
            .andReturn();

    log.info("Response :{}", result.getResponse().getContentAsString());
  }

  @Test
  @Order(13)
  void testModifiedINIConfiguration_thenReturnSuccess() throws Exception {
    when(this.clientService.modifiedINIConfiguration(any(PostalConfigurationDto.class)))
        .thenReturn(ProfileUnitTestConstants.POSTAL_CONFIGURATION_DTO);

    var result = this.mockMvc.perform(
        MockMvcRequestBuilders.put(URL + "/portal/configuration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(ProfileUnitTestConstants.POSTAL_CONFIGURATION_DTO))
    ).andExpect(status().isOk())
        .andReturn();

    log.info("Response: {}", result.getResponse().getContentAsString());
  }

  @ParameterizedTest
  @MethodSource({"configurationVersionsParamsSuccess"})
  @Order(14)
  void testGetPostalConfigurationVersions_ThenReturnSuccess(String url, String clientName,
      ResultMatcher expected)
      throws Exception {
    var postalConfigurationVersion = PostalConfigurationVersion.builder()
        .id(1L)
        .version(1)
        .fileId("c9eafa34-8ad2-4868-950f-2012669e4d85")
        .ownerId(1L)
        .createdBy("ADMIN_TEST")
        .createdAt(new Date())
        .build();

    when(this.clientService.getPostalConfigurationVersions(clientName))
        .thenReturn(List.of(postalConfigurationVersion));

    this.mockMvc.perform(
            get(url)
                .param("clientName", clientName)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(expected)
        .andReturn()
        .getResponse();
  }

  @ParameterizedTest
  @MethodSource({"configurationVersionsParamsFail"})
  @Order(15)
  void testGetPostalConfigurationVersions_ThenReturnFail(String url, String clientName,
      ResultMatcher expected)
      throws Exception {
    var postalConfigurationVersion = PostalConfigurationVersion.builder()
        .id(1L)
        .version(1)
        .fileId("c9eafa34-8ad2-4868-950f-2012669e4d85")
        .ownerId(1L)
        .createdBy("ADMIN_TEST")
        .createdAt(new Date())
        .build();

    when(this.clientService.getPostalConfigurationVersions(clientName))
        .thenReturn(List.of(postalConfigurationVersion));

    this.mockMvc.perform(
            get(url)
                .param("clientName", clientName)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(expected)
        .andReturn()
        .getResponse();
  }

  @ParameterizedTest
  @MethodSource({"configurationVersionByVersionNumberParamsSuccess"})
  @Order(16)
  void testGetPostalConfigurationVersionByVersionNumber_ThenReturnSuccesses(String url,
      String clientName,
      Integer version,
      ResultMatcher expected)
      throws Exception {
    var configVersion = PostalConfigurationVersion.builder()
        .id(1L)
        .version(version)
        .fileId("c9eafa34-8ad2-4868-950f-2012669e4d85")
        .ownerId(1L)
        .createdBy("ADMIN_TEST")
        .createdAt(new Date())
        .build();

    List<ConfigurationEntry> configurationEntries = new ArrayList<>();
    configurationEntries.add(new ConfigurationEntry("PathIn",
        "/apps/cxm/common/logidoc/Go2PDF/acquisition/go2pdf/" + "DEFAULT" + "/in/"));

    configurationEntries.add(new ConfigurationEntry("PathTemp",
        "/apps/cxm/common/logidoc/Go2PDF/acquisition/go2pdf/" + "PORTAIL" + "/tmp/"));

    Configuration configuration = new Configuration(1, "PORTAIL_ANALYSE", configurationEntries);
    PostalConfigurationVersionDto postalConfigurationVersionDto = new PostalConfigurationVersionDto(
        configVersion, List.of(configuration));

    when(this.clientService.getPostalConfigurationVersion(clientName, version))
        .thenReturn(postalConfigurationVersionDto);

    this.mockMvc.perform(
            get(url)
                .param("clientName", clientName)
                .param("version", String.valueOf(version))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(expected)
        .andReturn()
        .getResponse();
  }

  @ParameterizedTest
  @MethodSource({"configurationVersionByVersionNumberParamsFail"})
  @Order(16)
  void testGetPostalConfigurationVersionByVersionNumber_ThenReturnFail(String url,
      String clientName,
      Integer version,
      ResultMatcher expected)
      throws Exception {
    var configVersion = PostalConfigurationVersion.builder()
        .id(1L)
        .fileId("c9eafa34-8ad2-4868-950f-2012669e4d85")
        .ownerId(1L)
        .createdBy("ADMIN_TEST")
        .createdAt(new Date())
        .build();
    if (version != null) {
      configVersion.setVersion(version);
    }

    List<ConfigurationEntry> configurationEntries = new ArrayList<>();
    configurationEntries.add(new ConfigurationEntry("PathIn",
        "/apps/cxm/common/logidoc/Go2PDF/acquisition/go2pdf/" + "DEFAULT" + "/in/"));

    configurationEntries.add(new ConfigurationEntry("PathTemp",
        "/apps/cxm/common/logidoc/Go2PDF/acquisition/go2pdf/" + "PORTAIL" + "/tmp/"));

    Configuration configuration = new Configuration(1, "PORTAIL_ANALYSE", configurationEntries);
    PostalConfigurationVersionDto postalConfigurationVersionDto = new PostalConfigurationVersionDto(
        configVersion, List.of(configuration));

    if(version != null){
      when(this.clientService.getPostalConfigurationVersion(clientName, version))
          .thenReturn(postalConfigurationVersionDto);
    }

    this.mockMvc.perform(
            get(url)
                .param("clientName", clientName)
                .param("version", String.valueOf(version))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(expected)
        .andReturn()
        .getResponse();
  }

  public static Stream<Arguments> configurationVersionByVersionNumberParamsFail(){
    String customer = "CUSTOMER_TEST";
    return Stream.of(
        Arguments.arguments("/v1/clients/portal/configuration/version-test", customer, 1, status().isNotFound()),
        Arguments.arguments("/v1/clients/portal/configuration/version", null, 1, status().isBadRequest()),
        Arguments.arguments("/v1/clients/portal/configuration/version", customer, null, status().isBadRequest())
    );
  }
  public static Stream<Arguments> configurationVersionByVersionNumberParamsSuccess(){
    String URL = "/v1/clients/portal/configuration/version";
    String customer = "CUSTOMER_TEST";
    int version = 1;
    ResultMatcher expected = status().isOk();

    return Stream.of(
        Arguments.arguments(URL, customer, version, expected)
    );
  }

  private static Stream<Arguments> configurationVersionsParamsFail() {
    String clientName = "CUSTOMER_TEST";

    return Stream.of(
        Arguments.arguments("/v1/clients/portal/configuration/versions", clientName,
            status().isOk()),
        Arguments.arguments("/v1/clients/portal/configuration/versions-test", clientName,
            status().isNotFound()),
        Arguments.arguments("/v1/clients/portal/configuration/versions", null,
            status().isBadRequest())
    );
  }

  private static Stream<Arguments> configurationVersionsParamsSuccess() {
    String URL = "/v1/clients/portal/configuration/versions";
    String clientName = "CUSTOMER_TEST";
    ResultMatcher expected = status().isOk();

    return Stream.of(
        Arguments.arguments(URL, clientName, expected)
    );
  }
}
