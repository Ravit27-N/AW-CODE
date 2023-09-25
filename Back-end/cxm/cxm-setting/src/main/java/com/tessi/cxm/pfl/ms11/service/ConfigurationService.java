package com.tessi.cxm.pfl.ms11.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms11.constant.ConfigINIFileConstants;
import com.tessi.cxm.pfl.ms11.dto.PostalConfiguration;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.model.Configuration;
import com.tessi.cxm.pfl.shared.model.ConfigurationEntry;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class ConfigurationService {
  private PostalConfiguration defaultConfig;
  private PostalConfiguration defaultPostalConfig;

  private final ObjectMapper objectMapper;

  @Value("classpath:/conf/default_config.json")
  private Resource defaultConfigJSON;

  @Value("classpath:/conf/default_config_postal.json")
  private Resource defaultConfigPostalJSON;

  public ConfigurationService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    FileBasedConfigurationBuilder.setDefaultEncoding(
        INIConfiguration.class, StandardCharsets.UTF_8.name());
  }

  /**
   * Get default {@link PostalConfiguration} of "config.ini" from embedded JSON file.
   *
   * @return {@link PostalConfiguration}
   */
  public PostalConfiguration getDefaultConfig() {
    if (defaultConfig == null) {
      defaultConfig = this.getDefaultConfig(defaultConfigJSON);
    }
    return defaultConfig;
  }

  /**
   * Get cached default {@link PostalConfiguration} of "config_postail.ini" from embedded JSON file.
   *
   * @return {@link PostalConfiguration}
   */
  public PostalConfiguration getCachedDefaultPostalConfig() {
    if (defaultPostalConfig == null) {
      defaultPostalConfig = this.getDefaultConfig(defaultConfigPostalJSON);
    }
    return defaultPostalConfig;
  }

  /**
   * Get default {@link PostalConfiguration} of "config_postail.ini" from embedded JSON file.
   *
   * @return {@link PostalConfiguration}
   */
  public PostalConfiguration getDefaultPostalConfig() {
    return this.getDefaultConfig(defaultConfigPostalJSON);
  }

  /**
   * Load {@link PostalConfiguration} from a {@link Resource}.
   *
   * @param source JSON file containing the configuration
   * @return {@link PostalConfiguration}
   */
  public PostalConfiguration getDefaultConfig(Resource source) {
    try {
      return this.objectMapper.readValue(source.getInputStream(), PostalConfiguration.class);
    } catch (IOException ioEx) {
      throw new FileErrorException("Failed to read default configuration", ioEx);
    }
  }

  /**
   * Write a {@link PostalConfiguration} as INI file.
   *
   * @param sourceConfiguration Source {@link PostalConfiguration}
   * @param destFilePath File to write INI configurations
   */
  public void writeINIConfig(PostalConfiguration sourceConfiguration, String destFilePath) {
    this.writeINIConfig(sourceConfiguration, Path.of(destFilePath));
  }

  /**
   * Write a {@link PostalConfiguration} as INI file.
   *
   * @param sourceConfiguration Source {@link PostalConfiguration}
   * @param destFilePath File to write INI configurations
   */
  public void writeINIConfig(PostalConfiguration sourceConfiguration, Path destFilePath) {
    if (!Files.exists(destFilePath)) {
      try {
        Files.createDirectories(destFilePath.getParent());
        Files.createFile(destFilePath);
      } catch (IOException ex) {
        throw new FileErrorException("Failed to create file", ex);
      }
    }

    var iniConfigHandler = new Configurations().iniBuilder(destFilePath.toFile());
    try {
      INIConfiguration configuration = iniConfigHandler.getConfiguration();
      this.writeToINIConfig(sourceConfiguration, configuration);
      iniConfigHandler.save();
    } catch (ConfigurationException ex) {
      throw new FileErrorException("Failed to write configuration", ex);
    }
  }

  public void modifiedINIConfig(PostalConfiguration sourceConfiguration, Path destFilePath) {
    try {
      if (Files.exists(destFilePath)) {
        // clean file content before write new contents.
        new FileOutputStream(destFilePath.toString()).close();
      }
      this.writeINIConfig(sourceConfiguration, destFilePath);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public Configuration readINIConfig(String sectionName, String iniFilePath) {
    return readINIConfig(sectionName, Path.of(iniFilePath));
  }

  /**
   * Read INI configuration from a file
   *
   * @param iniFilePath Path to INI file
   * @return {@link PostalConfiguration}
   */
  public PostalConfiguration readINIConfig(String iniFilePath) {
    return this.readINIConfig(Path.of(iniFilePath));
  }

  /**
   * Read INI configuration from a file
   *
   * @param iniFilePath {@link Path} to INI file
   * @return {@link PostalConfiguration}
   */
  public Configuration readINIConfig(String sectionName, Path iniFilePath) {
    INIConfiguration configuration = this.getINIFileBaseConfiguration(iniFilePath);
    return this.readINIConfigSection(sectionName, configuration);
  }

  /**
   * Get all section names of a INI configuration names.
   *
   * @param iniFilePath Path to INI configuration file
   * @return List of section names
   */
  public List<String> readINISectionNames(String iniFilePath) {
    return this.readINISectionNames(Path.of(iniFilePath));
  }

  /**
   * Get all section names of a INI configuration names.
   *
   * @param iniFilePath {@link Path} to INI configuration file
   * @return List of section names
   */
  public List<String> readINISectionNames(Path iniFilePath) {
    INIConfiguration configuration = this.getINIFileBaseConfiguration(iniFilePath);
    return new ArrayList<>(configuration.getSections());
  }

  /**
   * Read INI configuration from a file
   *
   * @param iniFilePath {@link Path} to INI file
   * @return {@link PostalConfiguration}
   */
  public PostalConfiguration readINIConfig(Path iniFilePath) {
    INIConfiguration configuration = this.getINIFileBaseConfiguration(iniFilePath);
    return this.getPostalConfig(configuration);
  }

  /**
   * Get a file based {@link INIConfiguration}.
   *
   * @param iniFilePath {@link Path} to INI file
   * @return {@link INIConfiguration}
   */
  private INIConfiguration getINIFileBaseConfiguration(Path iniFilePath) {
    if (!Files.exists(iniFilePath)) {
      throw new FileErrorException("File not found: " + iniFilePath.toAbsolutePath() + ".");
    }
    var iniConfigHandler = new Configurations().iniBuilder(iniFilePath.toFile());
    try {
      return iniConfigHandler.getConfiguration();
    } catch (ConfigurationException ex) {
      throw new FileErrorException("Failed to read configuration", ex);
    }
  }

  /**
   * Read {@link PostalConfiguration} from INI configuration.
   *
   * @param configuration INI configuration
   * @return {@link PostalConfiguration}
   */
  private PostalConfiguration getPostalConfig(INIConfiguration configuration) {
    var go2PDFConfiguration = new PostalConfiguration();
    var configs = go2PDFConfiguration.getConfigurations();

    var iniSectionNames = new ArrayList<>(configuration.getSections());
    IntStream orderedSections = IntStream.range(0, iniSectionNames.size());
    orderedSections.forEach(
        sectionOrder -> {
          var sectionName = iniSectionNames.get(sectionOrder);
          var config = this.readINIConfigSection(sectionName, configuration);
          config.setOrder(sectionOrder);
          configs.add(config);
        });
    return go2PDFConfiguration;
  }

  /**
   * Read a {@link Configuration} from INI configuration section.
   *
   * @param sectionName INI section name
   * @param configuration Reference {@link INIConfiguration}
   * @return {@link Configuration}
   */
  private Configuration readINIConfigSection(String sectionName, INIConfiguration configuration) {
    var configSection = configuration.getSection(sectionName);
    var config = new Configuration(sectionName);
    this.readINIConfigSection(configSection, config);
    return config;
  }

  /**
   * Read INI configuration section of {@link SubnodeConfiguration} into a {@link Configuration}.
   *
   * @param configSection INI configuration Section
   * @param refConfiguration {@link Configuration}
   */
  private void readINIConfigSection(
      SubnodeConfiguration configSection, Configuration refConfiguration) {
    configSection
        .getKeys()
        .forEachRemaining(
            propertyKey -> {
              var nonEscapedKey = this.removeEscapedKey(propertyKey);
              var propertyValue = configSection.get(String.class, propertyKey);
              refConfiguration.add(new ConfigurationEntry(nonEscapedKey, propertyValue));
            });
  }

  /**
   * File INI configuration with properties from {@link PostalConfiguration}.
   *
   * @param payload {@link PostalConfiguration}
   * @param configuration {@link PostalConfiguration}
   */
  private void writeToINIConfig(PostalConfiguration payload, INIConfiguration configuration) {
    var sortedConfigs =
        payload.getConfigurations().stream()
            .sorted(Comparator.comparingInt(Configuration::getOrder));

    configuration.setSeparatorUsedInOutput(ConfigINIFileConstants.SEPARATOR_CHARS);
    sortedConfigs.forEach(
        config -> {
          SubnodeConfiguration configSection = configuration.getSection(config.getName());
          config
              .getEntries()
              .forEach(
                  configEntry -> {
                    var escapedKey = this.escapeKey(configEntry.getKey());
                    configSection.addProperty(escapedKey, configEntry.getValue());
                  });
        });
  }

  /**
   * Escaping special character in key name.
   *
   * @param key Key
   * @return Escaped key
   * @see <a
   *     href="https://commons.apache.org/proper/commons-configuration/userguide/howto_hierarchical.html#Escaping_special_characters">Escaping
   *     special characters </a>
   */
  private String escapeKey(String key) {
    if (StringUtils.hasText(key)) {
      return key.replace(".", "..");
    }
    return key;
  }

  /**
   * Remove the escaped special character in key name.
   *
   * @param key Key
   * @return Escaped key
   * @see <a
   *     href="https://commons.apache.org/proper/commons-configuration/userguide/howto_hierarchical.html#Escaping_special_characters">Escaping
   *     special characters </a>
   */
  private String removeEscapedKey(String key) {
    if (StringUtils.hasText(key)) {
      return key.replace("..", ".");
    }
    return key;
  }

  public boolean checkFileExist(Path sourceFile) {
    return Files.exists(sourceFile);
  }

  public boolean checkFileExist(String sourceFile) {
    return this.checkFileExist(Path.of(sourceFile));
  }
}
