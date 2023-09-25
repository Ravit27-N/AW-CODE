package com.tessi.cxm.pfl.ms11.util;

import static com.tessi.cxm.pfl.shared.utils.ConfigurationConstants.CHANNEL_DIGITAL;
import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_CAMPAIGN;
import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_FLOW_DEPOSIT;
import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_SMS_CAMPAIGN;

import com.tessi.cxm.pfl.ms11.constant.ClientSettingFunctionalities;
import com.tessi.cxm.pfl.ms11.constant.ConfigINIFileConstants;
import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataItem;
import com.tessi.cxm.pfl.ms11.dto.ChannelMetadataRequestDto;
import com.tessi.cxm.pfl.shared.model.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms11.entity.ChannelMetadata;
import com.tessi.cxm.pfl.ms11.entity.CriteriaDistribution;
import com.tessi.cxm.pfl.ms11.entity.PortalSetting;
import com.tessi.cxm.pfl.ms11.entity.ResourceLibrary;
import com.tessi.cxm.pfl.ms11.entity.Setting;
import com.tessi.cxm.pfl.ms11.entity.SettingInstruction;
import com.tessi.cxm.pfl.shared.model.Configuration;
import com.tessi.cxm.pfl.shared.model.ConfigurationEntry;
import com.tessi.cxm.pfl.shared.model.PortalSettingConfigStatusDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.ProfileClientSettingRequest;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.UserInfoResponse;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerDomainNameRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.CustomerRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.DomainNameRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubAccount;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionRequest;
import com.tessi.cxm.pfl.shared.model.setting.criteria.Preference;

import java.io.File;
import java.nio.file.Path;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConstantProperties {

  public static final ResourceLibraryDto RESOURCE_LIBRARY_DTO =
      ResourceLibraryDto.builder()
          .id(1L)
          .label("test resource")
          .type("Background")
          .fileId("abc2a04d-0119-419c-897c-233003627580")
          .fileName("test.pdf")
          .fileSize(3600L)
          .pageNumber(15)
          .ownerId(1L)
          .createdAt(new Date())
          .lastModified(new Date())
          .createdBy("john.doe@example.com")
          .lastModifiedBy("super.admin@example.com")
          .build();

  public static final ResourceLibrary RESOURCE_LIBRARY =
      ResourceLibrary.builder()
          .id(1L)
          .label("test resource")
          .type("Background")
          .fileId("abc2a04d-0119-419c-897c-233003627580")
          .fileName("test.pdf")
          .fileSize(3600L)
          .ownerId(1L)
          .clientId(1L)
          .build();

  public static final UserPrivilegeDetails USER_PRIVILEGE_DETAILS =
      new UserPrivilegeDetails("vis", "service", false, Collections.singletonList(1L));

  public static final String CUSTOMER_NAME = "client_digital";
  public static final List<Setting> SETTING_LIST =
      IntStream.range(0, ClientSettingFunctionalities.values().length)
          .mapToObj(
              index ->
                  Tuples.of(
                      1 + index,
                      ClientSettingFunctionalities.values()[index]))
          .map(
              tuple2 ->
                  Setting.builder()
                      .id((long) tuple2.getT1())
                      .depositType("Portal")
                      .flowType(
                          tuple2.getT2()
                              .getPrefixClientValue(CUSTOMER_NAME))
                      .customer(CUSTOMER_NAME)
                      .extension(tuple2.getT2().getExtension())
                      .scanActivation(true)
                      .build())
          .collect(Collectors.toList());

  public static final UserInfoResponse USER_INFO_RESPONSE = new UserInfoResponse(true, true);
  public static final String CONFIG_FILE =
      String.format(
          "/apps/cxm/config/go2pdf/%s/%s",
          CUSTOMER_NAME, ConfigINIFileConstants.CONFIG_INI);
  public static final PortalSetting PORTAL_SETTING =
      PortalSetting.builder()
          .id(SETTING_LIST.get(2).getId())
          .isActive(true)
          .setting(SETTING_LIST.get(2))
          .configPath(CONFIG_FILE)
          .section(ConfigINIFileConstants.PORENILIBRE)
          .build();

  public static final List<SettingInstruction> SETTING_INSTRUCTIONS =
      List.of(
          SettingInstruction.builder()
              .id(1L)
              .channel(CHANNEL_DIGITAL)
              .subChannel(
                  ClientSettingFunctionalities.EMAILING_CAMPAIGN.getSubValue())
              .build(),
          SettingInstruction.builder()
              .id(2L)
              .channel(CHANNEL_DIGITAL)
              .subChannel(ClientSettingFunctionalities.SMS_CAMPAIGN.getSubValue())
              .build());

  public static final List<ProfileClientSettingRequest.Functionality> FUNCTIONALITIES =
      List.of(
          new ProfileClientSettingRequest.Functionality(CXM_FLOW_DEPOSIT, true),
          new ProfileClientSettingRequest.Functionality(CXM_CAMPAIGN, true),
          new ProfileClientSettingRequest.Functionality(CXM_SMS_CAMPAIGN, true));

  public static final PortalSettingConfigStatusDto PORTAL_SETTING_CONFIG_STATUS_DTO =
      PortalSettingConfigStatusDto.builder().clientName(CUSTOMER_NAME).isActive(true).build();

  public static final UserInfoResponse SUPER_ADMIN_INFO =
      UserInfoResponse.builder().superAdmin(false).platformAdmin(false).build();

  public static final Configuration DEFAULT_CONFIG =
      new Configuration(
          1,
          "DEFAULT",
          List.of(
              new ConfigurationEntry("ServerName", "DOCKER_TESI-POST"),
              new ConfigurationEntry("ComputerName", "DOCKER_TESSI-POST")));

  public static final Configuration PORTAIL_CONFIG =
      new Configuration(
          2,
          "PORTAIL",
          List.of(
              new ConfigurationEntry("Modele", "portail_gc_default"),
              new ConfigurationEntry(
                  "PathChargement",
                  "/preprocessing/appli/reception_seul/reception_seul.pl")));

  public static final Configuration PORTAIL_ANALYSE_CONFIG =
      new Configuration(
          3,
          "PORTAIL_ANALYSE",
          List.of(
              new ConfigurationEntry("Modele", "PORTAIL_ANALYSE"),
              new ConfigurationEntry(
                  "PathChargement",
                  "/preprocessing/appli/reception_seul/reception_seul.pl")));

  public static final Configuration PORTAIL_PREVIEW_CONFIG =
      new Configuration(
          3,
          "PORTAIL_ANALYSE",
          List.of(
              new ConfigurationEntry("Modele", "PORTAIL_PREVIEW"),
              new ConfigurationEntry(
                  "PathChargement",
                  "/preprocessing/appli/reception_seul/reception_seul.pl")));

  public static final List<Configuration> CONFIGURATION_LIST =
      List.of(DEFAULT_CONFIG, PORTAIL_CONFIG, PORTAIL_ANALYSE_CONFIG, PORTAIL_PREVIEW_CONFIG);

  public static final PostalConfigurationDto POSTAL_CONFIGURATION_DTO =
      PostalConfigurationDto.builder()
          .client("client_test")
          .configurations(CONFIGURATION_LIST)
          .build();

  public static final Setting SETTING =
      Setting.builder()
          .id(1L)
          .customer("client_test")
          .depositType("Portal")
          .connector("")
          .extension("Pdf")
          .flowType("client10/Portal/pdf")
          .idCreator(null)
          .portalSetting(PORTAL_SETTING)
          .build();

  public static final UserInfoResponse ADMIN_USER =
      UserInfoResponse.builder().superAdmin(true).platformAdmin(true).build();

  public static final UserInfoResponse NORMAL_USER =
      UserInfoResponse.builder().superAdmin(false).platformAdmin(false).build();

  public static final CriteriaDistributionRequest CRITERIA_DISTRIBUTION_REQUEST =
      CriteriaDistributionRequest.builder()
          .customer("client 1")
          .preference(
              Preference.builder()
                  .active(false)
                  .name("Digital")
                  .enabled(true)
                  .build())
          .build();

  public static final CriteriaDistribution CRITERIA_DISTRIBUTION =
      CriteriaDistribution.builder()
          .name("Digital")
          .customer("Client 1")
          .isActive(false)
          .build();

  public static final CustomerRequest CUSTOMER_REQUEST =
      CustomerRequest.builder().label("Customer").name("Customer").build();

  public static final ChannelMetadataRequestDto CHANNEL_METADATA_REQUEST_DTO =
      ChannelMetadataRequestDto.builder()
          .customer("example_customer")
          .type("unsubscribe_link")
          .metadata(List.of(new ChannelMetadataItem(1L, "example@example.com", 1L)))
          .build();

  public static final ChannelMetadata CHANNEL_METADATA =
      ChannelMetadata.builder()
          .id(1L)
          .customer("example@example.com")
          .value("dsdssd")
          .type("unsubscribe_link")
          .order(1L)
          .build();

  public static final UserDetail USER_DETAIL =
      UserDetail.builder()
          .clientId(1L)
          .clientName("example_client")
          .divisionId(1L)
          .serviceId(1L)
          .ownerId(1L)
          .username("example@example.com")
          .technicalRef("1056a87f-5ef0-47bc-8f27-d5350f56d329")
          .firstName("John")
          .lastName("Doe")
          .build();

  public static final UserHubAccount USER_HUB_ACCOUNT =
      UserHubAccount.builder()
          .username("example@example.com")
          .password("$Password123")
          .build();

  public static final CustomerDomainNameRequest CUSTOMER_DOMAIN_NAME_REQUEST =
      new CustomerDomainNameRequest(
          "example_client", List.of(new DomainNameRequest("example.com")));

  public static final Path PATH_REQUEST = Path.of("/var/data/cxm/cxm-setting");

  public static final File FILE_REQUEST = new File("data.pdf");
  
}
