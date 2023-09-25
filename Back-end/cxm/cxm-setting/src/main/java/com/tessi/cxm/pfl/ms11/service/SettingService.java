package com.tessi.cxm.pfl.ms11.service;

import static com.tessi.cxm.pfl.ms11.constant.ConfigINIFileConstants.DEFAULT_CONFIG;
import static com.tessi.cxm.pfl.ms11.constant.ConfigINIFileConstants.MODELE_KEY;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.BatchSettingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.DepositValidation;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalSettingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PreProcessingSettingResponseDto;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.SettingResponse;
import com.tessi.cxm.pfl.ms11.config.Go2pdfFileConfig;
import com.tessi.cxm.pfl.ms11.constant.ClientSettingFunctionalities;
import com.tessi.cxm.pfl.ms11.constant.ConfigINIFileConstants;
import com.tessi.cxm.pfl.ms11.dto.PostalConfiguration;
import com.tessi.cxm.pfl.ms11.dto.SettingDto;
import com.tessi.cxm.pfl.ms11.entity.PortalSetting;
import com.tessi.cxm.pfl.ms11.entity.Setting;
import com.tessi.cxm.pfl.ms11.entity.SettingInstruction;
import com.tessi.cxm.pfl.ms11.exception.CustomerNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.DefaultINIConfigNotExistException;
import com.tessi.cxm.pfl.ms11.exception.DefaultSectionIgnoreModelException;
import com.tessi.cxm.pfl.ms11.exception.DepositModeNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.FileNotExistException;
import com.tessi.cxm.pfl.ms11.exception.FunctionalityNotFoundException;
import com.tessi.cxm.pfl.ms11.exception.ModelMetaDataException;
import com.tessi.cxm.pfl.ms11.exception.PortalPdfBadRequestException;
import com.tessi.cxm.pfl.ms11.exception.SectionNotFoundException;
import com.tessi.cxm.pfl.ms11.repository.PortalSettingRepository;
import com.tessi.cxm.pfl.ms11.repository.SettingInstructionRepository;
import com.tessi.cxm.pfl.ms11.repository.SettingRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.FileNotFoundException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.Configuration;
import com.tessi.cxm.pfl.shared.model.ConfigurationEntry;
import com.tessi.cxm.pfl.shared.model.PortalSettingConfigStatusDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.ProfileClientSettingRequest;
import com.tessi.cxm.pfl.shared.model.UserInfoResponse;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import com.tessi.cxm.pfl.shared.service.storage.FileServiceImpl;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.ConfigurationConstants;
import com.tessi.cxm.pfl.shared.utils.CustomerDepositModeDto;
import com.tessi.cxm.pfl.shared.utils.DepositMode;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@Transactional
public class SettingService extends AbstractCrudService<SettingDto, Setting, Long> {
  private final SettingRepository settingRepository;
  private final SettingInstructionRepository settingInstructionRepository;
  private final PortalSettingRepository portalSettingRepository;
  private ConfigurationService configurationService;
  private FileService fileService;
  private FileService portalFileService;

  private Go2pdfFileConfig go2pdfFileConfig;

  public SettingService(
      SettingRepository settingRepository,
      ModelMapper modelMapper,
      SettingInstructionRepository settingInstructionRepository,
      ProfileFeignClient profileFeignClient,
      PortalSettingRepository portalSettingRepository) {
    super(modelMapper, profileFeignClient);
    this.settingRepository = settingRepository;
    this.settingInstructionRepository = settingInstructionRepository;
    this.portalSettingRepository = portalSettingRepository;
  }

  @Autowired
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Autowired
  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }

  @Autowired
  public void setFileService(Go2pdfFileConfig localFileConfig) {
    this.portalFileService = new FileServiceImpl(localFileConfig.getPath(), "");
  }

  @Autowired
  public void setGo2pdfFileConfig(Go2pdfFileConfig go2pdfFileConfig) {
    this.go2pdfFileConfig = go2pdfFileConfig;
  }

  /**
   * To get deposit validation of {@link Setting}.
   *
   * @param customer refer to name of the directory of {@code FlowRepository}.
   * @param depositType refer to type of deposit mode of {@code FlowRepository}.
   * @param connector refer to connector of the {@code FlowRepository}.
   * @param extension refer to extension of files.
   * @return object of {@link DepositValidation} wrapped by {@link FlowProcessingResponse}
   */
  @Transactional(readOnly = true)
  public FlowProcessingResponse<DepositValidation> getDepositValidation(
      String customer, String depositType, String connector, String extension) {
    var validate =
        this.settingRepository.findDepositValidation(customer, depositType, connector, extension);

    if (validate == null) {
      return new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK);
    }
    return new FlowProcessingResponse<>(
        Message.FINISHED, HttpStatus.OK, this.modelMapper.map(validate, DepositValidation.class));
  }

  /**
   * To get deposit validation of {@link Setting}.
   *
   * @param customer refer to name of the directory of {@code FlowRepository}.
   * @param flowType refers to type of flow
   * @return object of {@link DepositValidation} wrapped by {@link FlowProcessingResponse}
   */
  @Transactional(readOnly = true)
  public FlowProcessingResponse<DepositValidation> getDepositValidation(
      String customer, String flowType) {
    var validate = this.settingRepository.findDepositValidationByFlowType(customer, flowType);

    if (validate == null) {
      return new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK);
    }
    return new FlowProcessingResponse<>(
        Message.FINISHED, HttpStatus.OK, this.modelMapper.map(validate, DepositValidation.class));
  }

  /**
   * To extract setting of batch or portal base on flowType.
   *
   * @param flowType refers to type of flow and it is required
   * @param idCreator refers to the identity of user who deposit the flow, and it is required for
   *     setting of batch.
   * @param flowName refers to the name of flow and it is optional
   * @return the implementation object of {@link SettingResponse}
   * @see #extractBatchSetting(String, long, String)
   */
  @Transactional(readOnly = true)
  public FlowProcessingResponse<SettingResponse> extractSetting(
      String flowType, long idCreator, String flowName) {
    log.info("--- Start extractSetting ---");
    log.info("flowType = '" + flowType + "', " +
            "idCreator = '" + idCreator + "', " +
            "flowName = '" + flowName + "', " +
            "PortalDepositType = '" + PortalDepositType.getPortalDepositType(flowType) + "'");
    flowType = flowType.trim();
    if (flowType.contains(FlowTreatmentConstants.IV_DEPOSIT)) {
      return this.extractPortalSetting(flowType);
    }
    if (flowType.contains(FlowTreatmentConstants.PORTAL_DEPOSIT)) {
      if (PortalDepositType.getPortalDepositType(flowType).equals(PortalDepositType.PDF)) {
        return this.extractPortalSetting(flowType);
      }

      if (PortalDepositType.getPortalDepositType(flowType).equals(PortalDepositType.CAMPAIGN_EMAIL)
          || PortalDepositType.getPortalDepositType(flowType)
              .equals(PortalDepositType.CAMPAIGN_SMS)) {
        return this.extractPortalCampaignSetting(flowType);
      }
    }
    if (flowType.contains(FlowTreatmentConstants.BATCH_DEPOSIT) &&
            flowType.contains("pdf")) {
      return this.extractPortalSetting(flowType);
    }

    return this.extractBatchSetting(flowType, idCreator, flowName.trim());
  }

  /**
   * To retrieve channel and sub-channel from setting.
   *
   * @param flowType refer to field of {@link Setting}
   * @param idCreator refer to field of {@link Setting}
   * @param flowName refer to name of flow
   * @return object of {@link SettingResponse} wrapped by {@link FlowProcessingResponse}
   */
  private FlowProcessingResponse<SettingResponse> extractBatchSetting(
      String flowType, long idCreator, String flowName) {
    log.info("--- Start extractBatchSetting ---");
    var result =
        this.settingInstructionRepository.extractBatchSetting(flowType, idCreator, flowName);

    return result
        .<FlowProcessingResponse<SettingResponse>>map(
            bs ->
                new FlowProcessingResponse<>(
                    Message.FINISHED,
                    HttpStatus.OK,
                    this.modelMapper.map(bs, BatchSettingResponse.class)))
        .orElseGet(() -> new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK));
  }

  /**
   * To extract setting information of portal deposit base flowType.
   *
   * @param flowType required parameter and it is the identity to extract setting
   * @return object of {@link PortalSettingResponse}
   */
  private FlowProcessingResponse<SettingResponse> extractPortalSetting(String flowType) {
    var result = this.portalSettingRepository.getPortalSetting(flowType.trim());
    log.info("result = '" + result + "'");
    return result
        .<FlowProcessingResponse<SettingResponse>>map(
            ps ->
                new FlowProcessingResponse<>(
                    Message.FINISHED,
                    HttpStatus.OK,
                    this.modelMapper.map(ps, PortalSettingResponse.class)))
        .orElseGet(() -> new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK));
  }

  /**
   * To extract setting information of campaign via portal mode.
   *
   * @param flowType required parameter and it is the identity to extract setting
   * @return object of {@link PortalSettingResponse}
   */
  private FlowProcessingResponse<SettingResponse> extractPortalCampaignSetting(String flowType) {
    var result = this.portalSettingRepository.getPortalCampaignSetting(flowType.trim());
    return result
        .<FlowProcessingResponse<SettingResponse>>map(
            ps ->
                new FlowProcessingResponse<>(
                    Message.FINISHED,
                    HttpStatus.OK,
                    this.modelMapper.map(ps, PortalSettingResponse.class)))
        .orElseGet(() -> new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK));
  }

  /**
   * To retrieve object of instructions from {@link SettingInstruction}.
   *
   * @param flowType refer to type of flow.
   * @param modelName refer to name of model.
   * @param idCreator refer to id creator.
   * @return object of {@link PreProcessingSettingResponseDto} wrapped by {@link
   *     FlowProcessingResponse}
   */
  public FlowProcessingResponse<PreProcessingSettingResponseDto> getInstructionDetails(
      String flowType, String modelName, long idCreator) {
    log.info("--- Start getInstructionDetails ---");
    log.info("flowType = '" + flowType + "', " +
            "modelName = '" + modelName + "', " +
            "idCreator = '" + idCreator + "'");
    SettingInstruction data;
    if (StringUtils.isEmpty(modelName)) {
      data =
          this.settingInstructionRepository.getLastInstructionDetailsWithoutTemplate(
              flowType, idCreator);
    } else {
      data =
          this.settingInstructionRepository.getInstructionDetails(flowType, modelName, idCreator);
    }

    if (data == null) {
      return new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK);
    }

    return new FlowProcessingResponse<>(
        Message.FINISHED,
        HttpStatus.OK,
        this.modelMapper.map(data, PreProcessingSettingResponseDto.class));
  }

  /**
   * Get collection of {@link CustomerDepositModeDto}.
   *
   * @param customer - value of {@link String}.
   * @return - collection of {@link CustomerDepositModeDto}.
   */
  public List<CustomerDepositModeDto> getDepositModes(String customer) {
    List<String> depositTypes =
        List.of(
            DepositMode.PORTAL.getValue(),
            DepositMode.VIRTUAL_PRINTER.getValue(),
            DepositMode.API.getValue(),
            DepositMode.BATCH.getValue());

    List<String> extensions =
        List.of(FlowTreatmentConstants.PDF_EXTENSION, FlowTreatmentConstants.ZIP_EXTENSION);

    List<Setting> settings =
        this.settingRepository
            .findAllSetting(customer, depositTypes, extensions)
            .orElseThrow(() -> new CustomerNotFoundException(customer));
    List<CustomerDepositModeDto> depositModeDTOs =
        settings.stream()
            .map(
                setting ->
                    CustomerDepositModeDto.builder()
                        .key(this.getDepositMode(setting.getDepositType()).getKey())
                        .value(setting.getDepositType())
                        .scanActivation(setting.isScanActivation())
                        .build())
            .collect(Collectors.toList());

    List<CustomerDepositModeDto> missingDepositModeDTOs =
        depositTypes.stream()
            .filter(
                depositType ->
                    depositModeDTOs.stream()
                        .noneMatch(
                            depositMode -> depositMode.getValue().equalsIgnoreCase(depositType)))
            .map(
                deposit ->
                    CustomerDepositModeDto.builder()
                        .key(DepositMode.valueOfLabel(deposit).getKey())
                        .value(deposit)
                        .scanActivation(false)
                        .build())
            .collect(Collectors.toList());

    // Mapping response dto.
    return Stream.of(missingDepositModeDTOs, depositModeDTOs)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * Get enumeration of {@link DepositMode} by deposit type.
   *
   * @param depositType - value of {@link String}.
   * @return - enumeration of {@link DepositMode}.
   */
  private DepositMode getDepositMode(String depositType) {
    try {
      return DepositMode.valueOfLabel(depositType);
    } catch (Exception e) {
      throw new DepositModeNotFoundException("Deposit mode not found", e);
    }
  }

  public List<Setting> getSettingByFlowTypesAndCustomers(
      List<String> customers, List<String> flowTyps) {
    return this.settingRepository.findByCustomerInIgnoreCaseAndFlowTypeInIgnoreCase(
        customers, flowTyps);
  }

  public void createClientSetting(
      ProfileClientSettingRequest profileClientSettingRequest, String token) {
    UserInfoResponse userInfoResponse = this.profileFeignClient.checkUserIsAdmin(token);
    if (!userInfoResponse.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }
    final String adminUsername = this.getUsername();
    List<Setting> existsSettings = getExistsSetting(profileClientSettingRequest);
    List<Setting> settings = new ArrayList<>();
    for (ProfileClientSettingRequest.Functionality functionality :
        profileClientSettingRequest.getFunctionalities()) {
      ClientSettingFunctionalities csfunctionalities =
          ClientSettingFunctionalities.getByKey(functionality.getFunctionName());
      if (ObjectUtils.isEmpty(csfunctionalities)) {
        throw new FunctionalityNotFoundException(
            "Functionality not found :" + functionality.getFunctionName());
      }
      final String clientName = profileClientSettingRequest.getClientName();
      final String flowType = csfunctionalities.getPrefixClientValue(clientName);
      AtomicReference<Setting> setting =
          new AtomicReference<>(
              getNewSettingClient(csfunctionalities, clientName, flowType, adminUsername));
      existsSettings.stream()
          .filter(
              existsSetting ->
                  existsSetting.getFlowType().equals(flowType)
                      && existsSetting.getCustomer().equals(clientName))
          .forEach(
              existsSetting -> {
                existsSetting.setLastModified(new Date());
                existsSetting.setLastModifiedBy(adminUsername);
                setting.set(existsSetting);
              });

      setting.get().setScanActivation(functionality.isActivate());
      settings.add(setting.get());
    }

    settings = this.settingRepository.saveAll(settings);

    settings.stream()
        .filter(
            setting ->
                ClientSettingFunctionalities.CXM_FLOW_DEPOSIT
                    .getPrefixClientValue(setting.getCustomer())
                    .equals(setting.getFlowType()))
        .findFirst()
        .ifPresent(this::createPortalSetting);
    this.createSettingInstruction(settings);
  }

  public void createSettingInstruction(List<Setting> settings) {
    List<ClientSettingFunctionalities> digitalFlow =
        List.of(
            ClientSettingFunctionalities.EMAILING_CAMPAIGN,
            ClientSettingFunctionalities.SMS_CAMPAIGN);

    List<SettingInstruction> instructions =
        settings.stream()
            .filter(setting -> ObjectUtils.isEmpty(setting.getSettingInstruction()))
            .flatMap(
                setting ->
                    digitalFlow.stream()
                        .filter(
                            csFunc ->
                                csFunc
                                    .getPrefixClientValue(setting.getCustomer())
                                    .equalsIgnoreCase(setting.getFlowType()))
                        .map(
                            csFunc ->
                                SettingInstruction.builder()
                                    .setting(setting)
                                    .channel(ConfigurationConstants.CHANNEL_DIGITAL)
                                    .subChannel(csFunc.getSubValue())
                                    .build()))
            .collect(Collectors.toList());
    this.settingInstructionRepository.saveAll(instructions);
  }

  public void createPortalSetting(Setting setting) {
    var customerName = setting.getCustomer().toLowerCase().replace(' ', '_');
    String normalizePath =
        Paths.get(go2pdfFileConfig.getPath(), customerName, "config.ini").normalize().toString();
    final String finalPath = FilenameUtils.separatorsToUnix(normalizePath);
    Long portalId = setting.getId();
    AtomicReference<PortalSetting> portalSetting = new AtomicReference<>(new PortalSetting());
    portalSetting.get().setConfigPath(finalPath);
    portalSetting.get().setSection(ConfigINIFileConstants.PORENILIBRE);
    portalSetting.get().setSetting(setting);
    portalSetting.get().setCreatedBy(setting.getCreatedBy());
    portalSetting.get().setCreatedAt(setting.getCreatedAt());
    portalSetting.get().setActive(false);
    this.portalSettingRepository
        .findById(portalId)
        .ifPresent(
            existsPortalSetting -> {
              existsPortalSetting.setLastModified(new Date());
              existsPortalSetting.setLastModifiedBy(setting.getLastModifiedBy());
              portalSetting.set(existsPortalSetting);
            });
    this.portalSettingRepository.saveAndFlush(portalSetting.get());
  }

  private Setting getNewSettingClient(
      ClientSettingFunctionalities csfunctionalities,
      String clientName,
      String flowType,
      String createdBy) {
    Setting setting = new Setting();
    setting.setCreatedBy(createdBy);
    setting.setCustomer(clientName);
    setting.setDepositType(csfunctionalities.getDepositType());
    setting.setExtension(csfunctionalities.getExtension());
    setting.setFlowType(flowType);
    return setting;
  }

  public List<Setting> getExistsSetting(ProfileClientSettingRequest profileClientSettingRequest) {
    List<String> flowTypes = new ArrayList<>();
    List<String> customer = new ArrayList<>();
    for (ProfileClientSettingRequest.Functionality functionality :
        profileClientSettingRequest.getFunctionalities()) {
      ClientSettingFunctionalities csfunctionalities =
          ClientSettingFunctionalities.getByKey(functionality.getFunctionName());
      if (ObjectUtils.isEmpty(csfunctionalities)) {
        throw new FunctionalityNotFoundException(
            "Functionality not found :" + functionality.getFunctionName());
      }
      final String clientName = profileClientSettingRequest.getClientName();
      final String flowType = csfunctionalities.getPrefixClientValue(clientName);
      flowTypes.add(flowType);
      customer.add(clientName);
    }
    return this.getSettingByFlowTypesAndCustomers(customer, flowTypes);
  }

  /**
   * Method used to create or modified client setting.
   *
   * @param customer - customer name {@link String}.
   * @param customerDepositModes - collection of {@link CustomerDepositModeDto}.
   */
  public List<CustomerDepositModeDto> createOrModifiedClientSetting(
      String customer, List<CustomerDepositModeDto> customerDepositModes, String token) {
    UserInfoResponse userInfoResponse = this.profileFeignClient.checkUserIsAdmin(token);
    if (!userInfoResponse.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }
    final String adminUsername = this.getUsername();

    // Modified only (Portal, IV and Batch).
    List<DepositMode> destinationModes =
        List.of(DepositMode.PORTAL, DepositMode.VIRTUAL_PRINTER, DepositMode.BATCH);
    List<String> extensions =
        List.of(FlowTreatmentConstants.PDF_EXTENSION, FlowTreatmentConstants.ZIP_EXTENSION);

    List<CustomerDepositModeDto> depositModes =
        customerDepositModes.stream()
            .filter(
                customerDepositModeDto ->
                    destinationModes.stream()
                        .map(DepositMode::getValue)
                        .collect(Collectors.toList())
                        .contains(customerDepositModeDto.getValue()))
            .collect(Collectors.toList());

    List<Setting> settingEntity =
        this.settingRepository.findAllByCustomerAndDepositTypes(
            customer,
            depositModes.stream()
                .map(CustomerDepositModeDto::getValue)
                .collect(Collectors.toList()),
            extensions);

    depositModes.forEach(
        depositMode -> {
          Setting setting = new Setting();
          var optionalSetting =
              settingEntity.stream()
                  .filter(entity -> entity.getDepositType().equals(depositMode.getValue()))
                  .findFirst();
          if (optionalSetting.isPresent()) {
            // Update existing setting.
            setting = optionalSetting.get();
            setting.setScanActivation(depositMode.isScanActivation());
            setting.setLastModifiedBy(adminUsername);
            setting.setLastModified(new Date());
            var settingResponse = this.settingRepository.save(setting);
            // Create or update portal setting only (IV & Portal).
            this.savePortalSetting(settingResponse, depositMode, adminUsername);
          } else {
            // Create new setting if scanActivation equal to true.
            if (depositMode.isScanActivation()) {
              String extension = this.getExtension(depositMode.getValue());
              setting.setCreatedBy(adminUsername);
              setting.setCreatedAt(new Date());
              setting.setConnector(null);
              setting.setCustomer(customer);
              setting.setDepositType(depositMode.getValue());
              setting.setExtension(extension);
              setting.setFlowType(this.getFlowType(customer, depositMode.getValue(), extension));
              setting.setScanActivation(depositMode.isScanActivation());
              var settingResponse = this.settingRepository.save(setting);
              // Create or update portal setting only (IV & Portal).
              this.savePortalSetting(settingResponse, depositMode, adminUsername);
            }
          }
        });
    return customerDepositModes;
  }

  /**
   * Method used to save or modified portal setting (IV & Portal).
   *
   * @param setting - object of {@link Setting}.
   */
  private void savePortalSetting(
      Setting setting, CustomerDepositModeDto depositMode, String adminUsername) {
    List<String> depositTypes =
        List.of(DepositMode.PORTAL.getValue(), DepositMode.VIRTUAL_PRINTER.getValue());

    if (depositTypes.contains(depositMode.getValue())
        && FlowTreatmentConstants.PDF_EXTENSION.equalsIgnoreCase(setting.getExtension())) {
      PortalSetting portalSetting = new PortalSetting();
      if (setting.getPortalSetting() == null) {
        // Create new portal setting.
        var customerName = setting.getCustomer().toLowerCase().replace(' ', '_');
        Path configPath =
            Path.of(go2pdfFileConfig.getPath())
                .resolve(customerName)
                .resolve(ConfigINIFileConstants.CONFIG_INI);
        portalSetting.setSection(ConfigINIFileConstants.PORENILIBRE);
        portalSetting.setConfigPath(configPath.toString());
        portalSetting.setActive(depositMode.isScanActivation());
        portalSetting.setCreatedBy(adminUsername);
        portalSetting.setSetting(setting);
      } else {
        // Modified portal setting.
        boolean isActive = setting.getPortalSetting().isActive() && depositMode.isScanActivation();
        portalSetting = setting.getPortalSetting();
        portalSetting.setLastModifiedBy(adminUsername);
        portalSetting.setActive(isActive);
      }
      this.portalSettingRepository.save(portalSetting);
    }
  }

  /**
   * Get flow type by concatenating between (customer, depositType and extension).
   *
   * @param customer - customer name {@link String}.
   * @param depositType - value of {@link String}.
   * @param extension - value of {@link String}.
   * @return - value of {@link String}.
   */
  private String getFlowType(String customer, String depositType, String extension) {
    return customer.concat("/").concat(depositType).concat("/").concat(extension);
  }

  /**
   * Get extension by deposit type.
   *
   * @param depositType - value of {@link String}.
   * @return - value of {@link String}.
   */
  private String getExtension(String depositType) {
    if (DepositMode.VIRTUAL_PRINTER.getValue().equals(depositType)) {
      return FlowTreatmentConstants.PDF_EXTENSION;
    }
    if (DepositMode.BATCH.getValue().equals(depositType)) {
      return FlowTreatmentConstants.ZIP_EXTENSION;
    }
    return "";
  }

  /**
   * Modified (is_active) of portal setting.
   *
   * @param portalSettingConfigStatusDto - object of {@link PortalSettingConfigStatusDto}.
   * @return - object of {@link PortalSettingConfigStatusDto}.
   */
  public PortalSettingConfigStatusDto modifiedPortalSettingConfig(
      PortalSettingConfigStatusDto portalSettingConfigStatusDto, String token) {
    UserInfoResponse userInfoResponse = this.profileFeignClient.checkUserIsAdmin(token);
    if (!userInfoResponse.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }
    final var adminUsername = this.getUsername();

    List<String> depositTypes =
        List.of(DepositMode.PORTAL.getValue(), DepositMode.VIRTUAL_PRINTER.getValue());
    List<String> extensions = List.of(FlowTreatmentConstants.PDF_EXTENSION);

    List<PortalSetting> portalSettingResponse = new ArrayList<>();
    // Create or modified portal setting (IV and Batch) if not exist.
    this.settingRepository
        .findAllSetting(portalSettingConfigStatusDto.getClientName(), depositTypes, extensions)
        .ifPresent(
            settings ->
                settings.forEach(
                    setting -> {
                      if (setting.getPortalSetting() == null
                          && portalSettingConfigStatusDto.isActive()) {
                        // Create portal setting.
                        var customerName = setting.getCustomer().toLowerCase().replace(' ', '_');
                        Path configPath =
                            Path.of(go2pdfFileConfig.getPath())
                                .resolve(customerName)
                                .resolve(ConfigINIFileConstants.CONFIG_INI);

                        PortalSetting portalSetting = new PortalSetting();
                        portalSetting.setSection(ConfigINIFileConstants.PORENILIBRE);
                        portalSetting.setConfigPath(configPath.toString());
                        portalSetting.setActive(true);
                        portalSetting.setCreatedBy(adminUsername);
                        portalSetting.setSetting(setting);
                        var response = this.portalSettingRepository.save(portalSetting);
                        portalSettingResponse.add(response);
                      } else {
                        // Modified portal setting.
                        PortalSetting portalSetting = setting.getPortalSetting();
                        portalSetting.setActive(portalSettingConfigStatusDto.isActive());
                        portalSetting.setLastModifiedBy(adminUsername);
                        var response = this.portalSettingRepository.save(portalSetting);
                        portalSettingResponse.add(response);
                      }
                    }));

    // Create INI configuration.
    this.createINIConfig(portalSettingResponse);
    return portalSettingConfigStatusDto;
  }

  private void createINIConfig(List<PortalSetting> portalSettings) {
    portalSettings.stream()
        .findFirst()
        .ifPresent(
            portalSetting -> {
              this.createConfigINIFile(portalSetting);
              this.createConfigPortalINIFile(portalSetting);
            });
  }

  private void createConfigINIFile(PortalSetting portalSetting) {
    var configINIPath =
        Path.of(portalSetting.getConfigPath())
            .getParent()
            .resolve(ConfigINIFileConstants.CONFIG_INI);
    if (portalSetting.isActive() && !this.configurationService.checkFileExist(configINIPath)) {
      var defaultConfiguration = this.configurationService.getDefaultConfig();
      defaultConfiguration
          .getConfigurations()
          .forEach(
              configuration ->
                  this.mappingPathINIKEY(configuration, portalSetting.getConfigPath()));
      String[] variables = {ConfigINIFileConstants.VAR_WORKING_PATH};
      String[] values = {go2pdfFileConfig.getWorkingPath()};
      this.getDefaultPostalConfigReplaced(defaultConfiguration, variables, values);
      this.configurationService.writeINIConfig(defaultConfiguration, configINIPath);
    }
  }

  private void createConfigPortalINIFile(PortalSetting portalSetting) {
    var configINIPath = Path.of(portalSetting.getConfigPath()).getParent();
    var configPortalINIPath = configINIPath.resolve(ConfigINIFileConstants.CONFIG_PORTAL_INI);
    if (portalSetting.isActive()
        && !this.configurationService.checkFileExist(configPortalINIPath)) {
      var defaultConfigPortal = this.configurationService.getDefaultPostalConfig();
      String[] variables = {
        ConfigINIFileConstants.VAR_WORKING_PATH,
        ConfigINIFileConstants.VAR_ENRICHMENT_PATH,
        ConfigINIFileConstants.VAR_CLIENT_CONFIG_PATH
      };
      String[] values = {
        go2pdfFileConfig.getWorkingPath(),
        go2pdfFileConfig.getEnrichmentPath(),
        configINIPath.toString()
      };
      this.getDefaultPostalConfigReplaced(defaultConfigPortal, variables, values);
      this.configurationService.writeINIConfig(defaultConfigPortal, configPortalINIPath);
    }
  }

  /**
   * To search variables and replace to the default configuration INI file.
   *
   * @param defaultConfigPortal refer to object of {@link PostalConfiguration}
   * @param searchVariables refer to collection variables
   * @param replacement refer to collection of values to replacement.
   */
  private void getDefaultPostalConfigReplaced(
      PostalConfiguration defaultConfigPortal, String[] searchVariables, String[] replacement) {

    defaultConfigPortal
        .getConfigurations()
        .forEach(
            sectionConfig ->
                sectionConfig
                    .getEntries()
                    .forEach(
                        entry ->
                            entry.setValue(
                                StringUtils.replaceEach(
                                    entry.getValue(), searchVariables, replacement))));
  }

  public PortalSettingConfigStatusDto getPortalSettingConfig(String customer, String token) {
    if (!this.profileFeignClient.checkUserIsAdmin(token).isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    AtomicBoolean isPortalSettingConfigEnable = new AtomicBoolean(false);
    this.settingRepository
        .findPortalSetting(
            customer, DepositMode.PORTAL.getValue(), FlowTreatmentConstants.PDF_EXTENSION)
        .ifPresent(
            portalSetting -> {
              isPortalSettingConfigEnable.getAndSet(portalSetting.isActive());
            });

    return PortalSettingConfigStatusDto.builder()
        .clientName(customer)
        .isActive(isPortalSettingConfigEnable.get())
        .build();
  }

  /**
   * To retrieve the models or configuration file from the config_portal.ini file.
   *
   * <pre>
   *   Reference to the bug tickets: [Anomalie #114650] (https://redmine.tessitechno.fr/issues/114650#note-13) - Configuration of existing customer templates not taken into account on the template configuration screen.
   * </pre>
   *
   * @param customer refer to client name
   * @param token refer to the AUTHORIZATION of the user {@link HttpHeaders#AUTHORIZATION}.
   * @return object of {@link PostalConfigurationDto}
   */
  public PostalConfigurationDto getPostalConfiguration(
      String customer, String token, List<String> sessions) {
    if (!this.profileFeignClient.checkUserIsAdmin(token).isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    PostalConfigurationDto postalConfigurationDto = new PostalConfigurationDto();
    postalConfigurationDto.setClient(customer);

    // Get a setting by customer name and depositType = Portal, extension = Pdf, isActive = true.
    var settingEntity =
        this.settingRepository
            .findSettingByCustomQuery(
                customer, DepositMode.PORTAL.getValue(), FlowTreatmentConstants.PDF_EXTENSION)
            .orElseThrow(() -> new PortalPdfBadRequestException(customer));

    // Validate.
    if (!settingEntity.getPortalSetting().isActive()) {
      throw new PortalPdfBadRequestException(customer);
    }

    // config_portal.ini
    Path configPortalPath =
        Path.of(settingEntity.getPortalSetting().getConfigPath())
            .getParent()
            .resolve(ConfigINIFileConstants.CONFIG_PORTAL_INI);
    this.checkFileExist(configPortalPath);

    var postalINIConfig = this.configurationService.readINIConfig(configPortalPath);

    // Prevent errors when the config_portal.ini file does not have configuration sections.
    if (CollectionUtils.isEmpty(postalINIConfig.getConfigurations())) {
      throw new SectionNotFoundException("Models of config_portal.ini are not null or empty.");
    }
    this.modelMapper.map(postalINIConfig, postalConfigurationDto);
    if (ObjectUtils.isNotEmpty(sessions)) {
      List<Configuration> configurations =
          postalConfigurationDto.getConfigurations().stream()
              .filter(configuration -> this.isSelectModel(sessions, configuration.getName()))
              .collect(Collectors.toList());
      postalConfigurationDto.setConfigurations(configurations);
    }
    return postalConfigurationDto;
  }

  private boolean isSelectModel(List<String> sessions, String modelName) {
    return sessions.stream().anyMatch(modelName::equalsIgnoreCase);
  }

  private void checkFileExist(Path filePath) {
    if (!this.configurationService.checkFileExist(filePath)) {
      throw new FileNotExistException(
          "File not exist -> path: " + filePath.toAbsolutePath().toString());
    }
  }

  /**
   * Get the INI configuration file in Base64 format of a clientName.
   *
   * @param clientName Client name
   * @return Base64 string of INI configuration file
   */
  public String getINIFile(String clientName) {
    final var refBearerToken =
        BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken());
    UserInfoResponse userInfoResponse = this.profileFeignClient.checkUserIsAdmin(refBearerToken);
    if (!userInfoResponse.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    var customerName = clientName.toLowerCase().replace(' ', '_');
    Path iniPath =
        this.portalFileService
            .getPath(customerName)
            .resolve(ConfigINIFileConstants.CONFIG_PORTAL_INI);
    if (!iniPath.toFile().exists()) {
      throw new FileNotFoundException(
          "Postal configuration file of client \"" + clientName + "\" not found");
    }

    return this.fileService.encodeFileToBase64(iniPath.toString());
  }

  /**
   * Modified configuration of INI file, both config.ini & config_postal.ini files.
   *
   * @param dto - object of {@link PostalConfigurationDto}.
   * @param token - authorization token {@link String}.
   * @return object of {@link PostalConfigurationDto}.
   */
  @Transactional(rollbackFor = Exception.class)
  public PostalConfigurationDto modifiedINIConfiguration(PostalConfigurationDto dto, String token) {
    if (!this.profileFeignClient.checkUserIsAdmin(token).isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }
    // Get setting from db.
    var settingEntity =
        this.settingRepository
            .findSettingByCustomQuery(
                dto.getClient(),
                DepositMode.PORTAL.getValue(),
                FlowTreatmentConstants.PDF_EXTENSION)
            .orElseThrow(() -> new PortalPdfBadRequestException(dto.getClient()));

    if (settingEntity.getPortalSetting().isActive()) {
      // Sorting configuration by order.
      var configurationSorted =
          dto.getConfigurations().stream()
              .sorted(Comparator.comparingInt(Configuration::getOrder))
              .collect(Collectors.toList());

      this.validateDefaultConfigModels(configurationSorted);

      // update portal setting when the user modify configuration.
      var portalSetting = settingEntity.getPortalSetting();
      portalSetting.setLastModified(new Date());
      portalSetting.setLastModifiedBy(this.getUsername());
      this.portalSettingRepository.save(portalSetting);

      // Write postal config ini file.
      this.modifiedConfigPostalINIFile(settingEntity, configurationSorted);
    }

    return dto;
  }

  /**
   * To validate attributes of portal signature based on modelName.
   *
   * <pre>
   *     Keys validate: IdBaliseSignature and CoorSignature
   * </pre>
   *
   * @param modelName refer to model name that analyzed from the Go2PDF server.
   * @return if keys present return <strong>true</strong> else return <strong>false</strong>.
   */
  public boolean validateSignatureAttributes(String modelName) {
    List<String> signatureAttributes = List.of("IdBaliseSignature", "CoorSignature");

    Configuration configuration = getPortalClientConfiguration(modelName);

    var attrsPresent =
        configuration.getEntries().stream()
            .filter(config -> signatureAttributes.contains(config.getKey()))
            .count();

    return attrsPresent == signatureAttributes.size();
  }

  private Configuration getPortalClientConfiguration(String modelName) {
    var userDetails = PrivilegeValidationUtil.getUserDetail();
    PrivilegeValidationUtil.validateUserAccessPrivilege(
        ProfileConstants.CXM_ENRICHMENT_MAILING,
        ProfileConstants.EnrichmentMailing.ADD_RESOURCE,
        true,
        userDetails.getOwnerId());

    // Get setting from db.
    var setting =
        this.settingRepository
            .findSettingByCustomQuery(
                userDetails.getClientName(),
                DepositMode.PORTAL.getValue(),
                FlowTreatmentConstants.PDF_EXTENSION)
            .orElseThrow(() -> new PortalPdfBadRequestException(userDetails.getClientName()));

    var configINIPath = Path.of(setting.getPortalSetting().getConfigPath()).getParent();
    var configPortalINIPath = configINIPath.resolve(ConfigINIFileConstants.CONFIG_PORTAL_INI);

    return this.configurationService.readINIConfig(modelName, configPortalINIPath);
  }

  /**
   * Modified config.ini file.
   *
   * @param setting - object of {@link Setting}.
   * @param configurationStream - collection of {@link Configuration}.
   */
  private void modifiedConfigINIFile(Setting setting, List<Configuration> configurationStream) {
    Path configPath = Path.of(setting.getPortalSetting().getConfigPath());
    // Read config INI file per sections.
    PostalConfiguration configINI = new PostalConfiguration();
    configurationStream.forEach(
        configuration -> {
          var config = this.configurationService.readINIConfig(configuration.getName(), configPath);
          config.setName(configuration.getName());
          config.setOrder(configuration.getOrder());
          this.mappingPathINIKEY(config, setting.getPortalSetting().getConfigPath());
          configINI.getConfigurations().add(config);
        });
    // Write config INI file.
    this.configurationService.modifiedINIConfig(configINI, configPath);
  }

  /**
   * Modified config_postal.ini file.
   *
   * @param setting - object of {@link Setting}.
   * @param configurationStream - collection of {@link Configuration}.
   */
  private void modifiedConfigPostalINIFile(
      Setting setting, List<Configuration> configurationStream) {
    // Mapping config postal INI.
    checkConfigurationSectionModelEntry(configurationStream);
    PostalConfiguration configPostalINI = new PostalConfiguration();
    configurationStream.forEach(
        configuration -> {
          defineModelForConfiguration(configuration);
          configPostalINI.getConfigurations().add(configuration);
        });
    Path configPortalPath =
        Path.of(setting.getPortalSetting().getConfigPath())
            .getParent()
            .resolve(ConfigINIFileConstants.CONFIG_PORTAL_INI);
    // write config postal INI.
    this.configurationService.modifiedINIConfig(configPostalINI, configPortalPath);
  }

  /**
   * Define configuration entry of "modele" key to the top position.
   *
   * @param configuration refer to Object of {@link Configuration}.
   */
  private void defineModelForConfiguration(Configuration configuration) {
    if (!configuration.getName().equals(DEFAULT_CONFIG)) {
      List<String> configurationValues =
          configuration.getEntries().stream()
              .map(ConfigurationEntry::getValue)
              .collect(Collectors.toList());
      int modelEntryPosition = configurationValues.indexOf(configuration.getName());
      ConfigurationEntry modelEntry = configuration.getEntries().get(modelEntryPosition);
      configuration.getEntries().remove(modelEntry);
      configuration.getEntries().add(0, modelEntry);
    }
  }

  /**
   * Checking each section has a model, by excepting DEFAULT section should not have model.
   *
   * @param configurationStream refer to the {@link List}<{@link Configuration}>.
   */
  private void checkConfigurationSectionModelEntry(List<Configuration> configurationStream) {
    boolean defaultSectionHasModel =
        configurationStream.stream()
            .filter(configuration -> configuration.getName().equals(DEFAULT_CONFIG))
            .anyMatch(this::hasModelEntryKey);
    if (defaultSectionHasModel) {
      throw new DefaultSectionIgnoreModelException(
          "The default section \"Default\" does not need the configuration model.");
    }
    boolean haveModelEntryKeys =
        configurationStream.stream()
            .filter(configuration -> !configuration.getName().equals(DEFAULT_CONFIG))
            .allMatch(this::hasModelEntryKey);
    if (!haveModelEntryKeys) {
      String message = String.format("The model key is required");
      throw new ModelMetaDataException(message);
    }
  }

  /**
   * Check Section configuration has a Model.
   *
   * @param configuration refer to object {@link Configuration}.
   */
  private boolean hasModelEntryKey(Configuration configuration) {
    return configuration.getEntries().stream()
        .anyMatch(
            entry ->
                ObjectUtils.isNotEmpty(entry) && entry.getKey().equals(MODELE_KEY));
  }

  /**
   * Method used to add or override value of (PathIni key) of Config.ini file.
   *
   * @param configuration - object of {@link Configuration}.
   * @param configPath INI configuration path
   */
  private void mappingPathINIKEY(Configuration configuration, String configPath) {
    String pathIniValue =
        Path.of(configPath)
            .getParent()
            .resolve(ConfigINIFileConstants.CONFIG_PORTAL_INI)
            .toString();

    if (!configuration.getName().equals(DEFAULT_CONFIG)) {
      // Override value of PathIni.
      configuration.getEntries().stream()
          .filter(
              configurationEntry ->
                  configurationEntry.getKey().equals(ConfigINIFileConstants.PATH_INI_KEY))
          .forEach(configurationEntry -> configurationEntry.setValue(pathIniValue));

      // Add new PathIni.
      if (configuration.getEntries().stream()
          .noneMatch(
              configurationEntry ->
                  configurationEntry.getKey().equals(ConfigINIFileConstants.PATH_INI_KEY))) {
        configuration.add(
            new ConfigurationEntry(ConfigINIFileConstants.PATH_INI_KEY, pathIniValue));
      }
    }
  }

  /**
   * To validate the default configuration before save or modify the config_portal.ini file.
   *
   * @param configurationSorted refer to {@link List} of {@link Configuration} that are sorted.
   */
  private void validateDefaultConfigModels(List<Configuration> configurationSorted) {
    List<String> noneMatch = new ArrayList<>();
    var defaultINIConfigs = ConfigINIFileConstants.DEFAULT_INI_CONFIG_MODELS;

    var sectionsConfigs =
        configurationSorted.stream()
            .map(Configuration::getName)
            .limit(defaultINIConfigs.size())
            .collect(Collectors.toList());

    defaultINIConfigs.forEach(
        model -> {
          if (!sectionsConfigs.contains(model)) {
            noneMatch.add(model);
          }
        });

    if (!CollectionUtils.isEmpty(noneMatch)) {
      throw new DefaultINIConfigNotExistException(noneMatch);
    }
  }

  /**
   * To retrieve the last modified configuration based on <strong>Client Name</strong> of the user.
   *
   * @return last modified of configuration.
   */
  public Date getLastModifiedConfiguration() {
    var userDetails = PrivilegeValidationUtil.getUserDetail();

    var setting =
        this.settingRepository
            .findSettingByCustomQuery(
                userDetails.getClientName(),
                DepositMode.PORTAL.getValue(),
                FlowTreatmentConstants.PDF_EXTENSION)
            .orElseThrow(() -> new PortalPdfBadRequestException(userDetails.getClientName()));

    return setting.getPortalSetting().getLastModified();
  }
}
