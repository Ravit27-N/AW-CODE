package com.allweb.rms.service;

import static com.allweb.rms.utils.SystemConfigurationConstants.getEnumList;

import com.allweb.rms.entity.dto.SystemConfigDTO;
import com.allweb.rms.entity.jpa.SystemConfiguration;
import com.allweb.rms.exception.SystemMailConfigurationNotFoundException;
import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.rms.utils.SystemConstant;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@CacheConfig(cacheNames = "configurationCaching")
public class SystemConfigurationService {
  private static final String MSG_FORMAT = "System config id %s not found";
  private static final String SYMBOL_STAR = "********";
  private final SystemConfigurationRepository repository;
  private final ModelMapper modelMapper;

  @Autowired
  public SystemConfigurationService(
      SystemConfigurationRepository systemMailConfigurationRepository, ModelMapper modelMapper) {
    this.repository = systemMailConfigurationRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Method use retrieve System mail config info by its id
   *
   * @param id of mailProperty
   * @return mailProperty
   */
  @Transactional(readOnly = true)
  public SystemConfigDTO getSystemConfigById(int id) {
    return convertToDTO(
        repository
            .findByIdAndActiveIsTrue(id)
            .orElseThrow(
                () -> new SystemMailConfigurationNotFoundException(String.format(MSG_FORMAT, id))));
  }

  /**
   * Method use retrieve System config info by configKey
   *
   * @param configKey of system Config
   * @return System config
   */
  @Transactional(readOnly = true)
  public SystemConfigDTO getByConfigKey(String configKey) {
    String message = "System configuration configKey %s not found";
    SystemConfiguration systemConfiguration;
    if (configKey.startsWith(SystemConstant.KEY_PREFIX)) {
      Optional<SystemConfiguration> byConfigKey =
          repository.findByConfigKeyAndActiveIsTrue(configKey);
      systemConfiguration =
          byConfigKey.orElseGet(
              () ->
                  repository
                      .findByConfigKeyAndActiveIsTrue(configKey.split(SystemConstant.KEY_PREFIX)[1])
                      .orElseThrow(
                          () ->
                              new SystemMailConfigurationNotFoundException(
                                  String.format(message, configKey))));
    } else {
      String newKey = SystemConstant.KEY_PREFIX + "" + configKey;
      Optional<SystemConfiguration> byConfigKey = repository.findByConfigKeyAndActiveIsTrue(newKey);
      systemConfiguration =
          byConfigKey.orElseGet(
              () ->
                  repository
                      .findByConfigKeyAndActiveIsTrue(configKey)
                      .orElseThrow(
                          () ->
                              new SystemMailConfigurationNotFoundException(
                                  String.format(message, configKey))));
    }
    return convertToDTO(systemConfiguration);
  }

  /**
   * Method use retrieve System mail config info by its id
   *
   * @param id of Property
   * @return Property
   */
  @Transactional(readOnly = true)
  public SystemConfigDTO getSystemPropertyById(int id) {
    return convertToDTO(
        repository
            .findByIdAndConfigKeyNotIn(id, getEnumList())
            .orElseThrow(
                () -> new SystemMailConfigurationNotFoundException(String.format(MSG_FORMAT, id))));
  }

  /**
   * Method use to get pagination result
   *
   * @param size is number of page
   * @param page is number of page index
   * @param sortByField is field of Activity
   * @param sortDirection is direction sort ex(ASC,DESC)
   * @return SystemMailConfig
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public EntityResponseHandler<SystemConfigDTO> getSystemMailConfigs(
      int page, int size, String sortDirection, String sortByField, String filter) {

    if (page == 0) {
      List<SystemConfigDTO> systemConfigDTOS;
      Pageable pageable =
          PageRequest.of(0, size, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
      if (Strings.isNullOrEmpty(filter)) {
        systemConfigDTOS =
            repository.findAll(pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
      } else {
        systemConfigDTOS =
            repository.findAll(filter.toLowerCase(), pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
      }
      this.setResultStar(systemConfigDTOS);
      return new EntityResponseHandler<>(systemConfigDTOS);
    } else {
      Pageable pageable =
          PageRequest.of(
              page - 1, size, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
      Page<SystemConfigDTO> dtoPage;
      if (Strings.isNullOrEmpty(filter)) {
        dtoPage = repository.findAll(pageable).map(this::convertToDTO);
      } else {
        dtoPage =
            repository
                .findAllByConfigKeyOrConfigValue(filter.toLowerCase(), pageable)
                .map(this::convertToDTO);
      }

      List<SystemConfigDTO> content = new ArrayList<>(dtoPage.getContent());
      this.setResultStar(content);
      return new EntityResponseHandler<>(
          new PageImpl<>(content, pageable, dtoPage.getTotalElements()));
    }
  }
  // set symbol star for password and api key
  private void setResultStar(List<SystemConfigDTO> content) {
    String index = "index";
    Stream<ImmutableMap<String, Integer>> indices =
        IntStream.range(0, content.size())
            .filter(i -> content.get(i).getId() == 5 || content.get(i).getId() == 7)
            .mapToObj(i -> ImmutableMap.of(index, i));
    indices.forEach(i -> content.get(i.get(index)).setConfigValue(SYMBOL_STAR));
  }

  /**
   * Method use to perform save and update of SystemMailConfiguration
   *
   * @param systemMailConfiguration is object of SystemMailConfiguration
   * @return mailProperties object result
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public SystemConfigDTO save(SystemConfigDTO systemMailConfiguration) {
    if (systemMailConfiguration.getId() != 0) {
      SystemConfigDTO systemConfigDTO = update(systemMailConfiguration);
      this.setResultStar(Collections.singletonList(systemConfigDTO));
      return systemConfigDTO;
    }
    return convertToDTO(repository.save(convertToEntity(systemMailConfiguration)));
  }

  @CachePut(key = "#systemConfig.id")
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public SystemConfigDTO update(SystemConfigDTO systemConfig) {
    SystemConfiguration configuration =
        repository
            .findById(systemConfig.getId())
            .orElseThrow(
                () ->
                    new SystemMailConfigurationNotFoundException(
                        String.format(MSG_FORMAT, systemConfig.getId())));
    configuration.setConfigKey(systemConfig.getConfigKey().split(SystemConstant.KEY_PREFIX)[1]);
    configuration.setActive(systemConfig.isActive());
    configuration.setDescription(systemConfig.getDescription());
    if (!systemConfig.getConfigValue().startsWith(SYMBOL_STAR)) {
      configuration.setConfigValue(systemConfig.getConfigValue());
    }
    return convertToDTO(repository.save(configuration));
  }

  /**
   * Method use to delete mailProperties
   *
   * @param id of System Config
   * @return void
   */
  @CacheEvict(key = "#id")
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void delete(int id) {
    if (repository.findById(id).isPresent()) {
      if (repository.deleteByIdAndConfigKeyNotIn(id, getEnumList()) == 0)
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN, String.format("Can not delete Default Setting of id %s", id));
    } else throw new SystemMailConfigurationNotFoundException(String.format(MSG_FORMAT, id));
  }

  /**
   * Method use to update active
   *
   * @param id of System Config
   * @return SystemMailConfigDTO
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public SystemConfigDTO updateActive(int id, boolean isActive) {
    SystemConfigDTO dto =
        convertToDTO(
            repository
                .findById(id)
                .orElseThrow(
                    () ->
                        new SystemMailConfigurationNotFoundException(
                            String.format(MSG_FORMAT, id))));
    dto.setActive(isActive);
    return save(dto);
  }

  public SystemConfigDTO convertToDTO(SystemConfiguration systemMailConfig) {
    return modelMapper.map(systemMailConfig, SystemConfigDTO.class);
  }

  public SystemConfiguration convertToEntity(SystemConfigDTO systemMailConfig) {
    return modelMapper.map(systemMailConfig, SystemConfiguration.class);
  }

  @Cacheable
  @Transactional(readOnly = true)
  public Map<String, String> config() {
    return repository.findAllByConfigKeyIn(getEnumList()).stream()
        .collect(
            Collectors.toMap(
                SystemConfiguration::getConfigKey, SystemConfiguration::getConfigValue));
  }
}
