package com.innovationandtrust.sftp.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "signature.integration")
public class FileIntegrationProperty implements Serializable {

  @NotNull @NotEmpty private String basePathIn;

  @NotNull @NotEmpty private String basePathOut;

  private int delayTime;
}
