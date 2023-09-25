package com.tessi.cxm.pfl.ms11.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cxm.storage.go2pdf")
@Data
public class Go2pdfFileConfig {
    private String path; // local path of file.
    private String workingPath; // working directory path.
    private String enrichmentPath;
}
