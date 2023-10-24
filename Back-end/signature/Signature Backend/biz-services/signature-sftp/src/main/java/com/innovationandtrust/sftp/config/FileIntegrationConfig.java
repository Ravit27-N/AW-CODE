package com.innovationandtrust.sftp.config;

import com.innovationandtrust.sftp.component.CustomRegexPatternFileListFilter;
import com.innovationandtrust.sftp.constant.FileIntegrationConstant;
import com.innovationandtrust.sftp.constant.FileProcessingDirectory;
import com.innovationandtrust.sftp.service.FileDetectionService;
import com.innovationandtrust.utils.file.config.FileProperties;
import com.innovationandtrust.utils.file.utils.FileUtils;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileReadingMessageSource.WatchEventType;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.IgnoreHiddenFileListFilter;
import org.springframework.messaging.MessageChannel;

@Slf4j
@Configuration
@EnableIntegration
@EnableIntegrationManagement
@EnableConfigurationProperties(value = {FileIntegrationProperty.class})
public class FileIntegrationConfig {

  private static final String REGEX_WIN_FILE_VALIDATION =
      "[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}\\\\%s\\\\[^\\\\]+$";

  private static final String REGEX_LINUX_FILE_VALIDATION =
      "[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}\\/%s\\/[^\\/]+$";

  private static final String REGEX_WIN_DIRECTORY_DETECTION =
      "(.*[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}\\\\%s\\\\.*$)";

  private static final String REGEX_LINUX_DIR_DETECTION =
      "(^.*[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}\\/%s\\/.*$)";

  private final FileDetectionService fileDetectionService;

  private final FileProperties fileProperties;

  private final FileIntegrationProperty integrationProperty;

  @Autowired
  public FileIntegrationConfig(
      FileDetectionService fileDetectionService,
      FileProperties fileProperties,
      FileIntegrationProperty integrationProperty) {
    this.fileDetectionService = fileDetectionService;
    this.fileProperties = fileProperties;
    this.integrationProperty = integrationProperty;
  }

  @Bean(FileIntegrationConstant.INPUT_MESSAGE_SOURCE_CHANNEL_BEAN)
  public MessageChannel defaultInputChannel(
      @Qualifier(FileIntegrationConstant.ASYNC_TASK_EXECUTOR_BEAN) Executor executor) {
    return new ExecutorChannel(executor);
  }

  @Bean
  @InboundChannelAdapter(
      channel = FileIntegrationConstant.INPUT_MESSAGE_SOURCE_CHANNEL_BEAN,
      poller = @Poller(fixedDelay = "1000"))
  public MessageSource<File> fileReadingMessageSource() {
    var scanner = new RecursiveDirectoryScanner();
    // File filters
    var filters =
        new ChainFileListFilter<>(
            List.of(
                new IgnoreHiddenFileListFilter(),
                new CustomRegexPatternFileListFilter(
                    Pattern.compile(
                        String.format(
                            FileUtils.isUnix()
                                ? REGEX_LINUX_DIR_DETECTION
                                : REGEX_WIN_DIRECTORY_DETECTION,
                            FileProcessingDirectory.IN.getDirectory()),
                        Pattern.MULTILINE)),
                new AcceptOnceFileListFilter<>()));
    scanner.setFilter(filters);
    FileReadingMessageSource reader = new FileReadingMessageSource();
    reader.setScanner(scanner);
    var basePath = Path.of(fileProperties.getBasePath(), integrationProperty.getBasePathIn());
    log.info("Detect file base path: {}", basePath);
    FileUtils.createDirectories(basePath);
    reader.setDirectory(basePath.toFile());
    reader.setWatchEvents(WatchEventType.CREATE, WatchEventType.MODIFY, WatchEventType.DELETE);
    return reader;
  }

  @Bean(FileIntegrationConstant.INTEGRATION_FLOW_BEAN)
  public IntegrationFlow integrationFlow() {
    log.info("File detection start");
    return IntegrationFlow.from(FileIntegrationConstant.INPUT_MESSAGE_SOURCE_CHANNEL_BEAN)
        .handle(
            handler -> {
              var headers = handler.getHeaders();
              if (!headers.isEmpty()) {
                try {
                  var relativePath =
                      Objects.requireNonNull(headers.get(FileHeaders.RELATIVE_PATH)).toString();
                  log.info("Detected file path: {}", relativePath);
                  final Pattern pattern =
                      Pattern.compile(
                          String.format(
                              FileUtils.isUnix()
                                  ? REGEX_LINUX_FILE_VALIDATION
                                  : REGEX_WIN_FILE_VALIDATION,
                              FileProcessingDirectory.IN.getDirectory()),
                          Pattern.MULTILINE);
                  if (Path.of(
                              Objects.requireNonNull(headers.get(FileHeaders.ORIGINAL_FILE))
                                  .toString())
                          .toFile()
                          .isFile()
                      && pattern.matcher(relativePath).find()) {
                    log.info("Processing file: {}", relativePath);
                    this.fileDetectionService.receiveFile(
                        Path.of(relativePath), headers.getTimestamp());
                  }
                } catch (Exception e) {
                  log.error("Failed during detecting file", e);
                }
              }
            })
        .get();
  }
}
