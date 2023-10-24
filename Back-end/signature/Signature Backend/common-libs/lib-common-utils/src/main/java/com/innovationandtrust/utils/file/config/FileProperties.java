package com.innovationandtrust.utils.file.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataUnit;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "signature.file")
public class FileProperties {
  @NotNull @NotEmpty private String basePath;

  @NotNull private Long maxUploadSize;
  @NotEmpty @NotNull private String dataUnit;

  public void setDataUnit(String dataUnit) {
    this.dataUnit = dataUnit;
  }

  public DataUnit getDataUnit() {
    return DataUnit.fromSuffix(dataUnit);
  }
}
