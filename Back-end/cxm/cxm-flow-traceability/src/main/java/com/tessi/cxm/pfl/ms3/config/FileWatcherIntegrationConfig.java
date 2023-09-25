package com.tessi.cxm.pfl.ms3.config;

import com.tessi.cxm.pfl.ms3.service.FlowTraceabilityNotificationService;
import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.config.FileWatcherIntegrationProperty;
import com.tessi.cxm.pfl.shared.service.storage.FileService;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileReadingMessageSource.WatchEventType;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.IgnoreHiddenFileListFilter;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.messaging.MessageChannel;

/**
 * File detection configuration for flow-traceability notification.
 *
 * @author Vichet CHANN
 */
@Log4j2
@Configuration
@EnableConfigurationProperties(FileWatcherIntegrationProperty.class)
@RequiredArgsConstructor
@EnableIntegration
public class FileWatcherIntegrationConfig {

  private final FileWatcherIntegrationProperty fileWatcherIntegrationProperty;
  private final FlowTraceabilityNotificationService notificationService;
  private final FileService fileService;

  @Bean(FlowTraceabilityConstant.FLOW_NOTIFICATION_INPUT_CHANNEL)
  public MessageChannel fileInputChannel() {
    return new DirectChannel();
  }

  @Bean
  @InboundChannelAdapter(
      value = FlowTraceabilityConstant.FLOW_NOTIFICATION_INPUT_CHANNEL,
      poller = @Poller(fixedDelay = "1000"))
  public MessageSource<File> fileReadingMessageSource() {
    var fileConfig = this.fileWatcherIntegrationProperty.getFile();
    ChainFileListFilter<File> filter =
        new ChainFileListFilter<>(
            List.of(
                new IgnoreHiddenFileListFilter(),
                new RegexPatternFileListFilter(fileConfig.getRegexFilterFile()),
                new AcceptOnceFileListFilter<>(),
                new LastModifiedFileListFilter()));

    var scanner = new RecursiveDirectoryScanner();
    scanner.setFilter(filter);
    this.fileService.getPath(true, fileConfig.getSourceDir());
    FileReadingMessageSource reader = new FileReadingMessageSource();
    reader.setDirectory(new File(fileConfig.getSourceDir()));
    reader.setUseWatchService(true);
    reader.setWatchEvents(WatchEventType.CREATE, WatchEventType.MODIFY, WatchEventType.DELETE);
    return reader;
  }

  @Bean
  public IntegrationFlow processFlowTraceabilityNotification() {
    log.info("Notification file detection start");
    return IntegrationFlows.from(FlowTraceabilityConstant.FLOW_NOTIFICATION_INPUT_CHANNEL)
        .handle(
            handle -> {
              var headers = handle.getHeaders();
              if (!headers.isEmpty()) {
                try {
                  // replace path file with backslash
                  var relativePath =
                      Objects.requireNonNull(headers.get(FileHeaders.ORIGINAL_FILE)).toString();
                  log.info("Call detection path file: {}", relativePath);

                  // call notification service
                  notificationService.launchNotification(Path.of(relativePath).normalize());

                } catch (Exception e) {
                  log.debug(e.getMessage(), e);
                }
              }
            })
        .get();
  }
}
