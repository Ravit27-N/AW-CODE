package com.tessi.cxm.pfl.ms32.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "cxm.exporting")
public class CSVExportingProperties {
  private int pageSize = 1000;
  private String directory = System.getProperty("java.io.tmpdir");
}
