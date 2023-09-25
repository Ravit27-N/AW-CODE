package com.allweb.rms.entity.dto;

import com.allweb.rms.utils.SystemConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SystemConfigDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(name = "id", type = "int", description = "Not required when create new")
  private int id;

  @Schema(
      type = "string",
      required = true,
      maximum = "50",
      name = "configKey",
      description = "Note.. Don't include system.config cuz system will generate this")
  @NotEmpty
  private String configKey;

  @Schema(type = "string", required = true, maximum = "200", name = "configValue")
  @NotEmpty
  private String configValue;

  @Schema(type = "string", maximum = "200", name = "description")
  private String description;

  @Schema(type = "string", name = "createdAt")
  private Date createdAt;

  @Schema(type = "string", name = "updatedAt")
  private Date updatedAt;

  private boolean active;

  public String getConfigKey() {
    if (configKey.startsWith(SystemConstant.KEY_PREFIX)) return configKey;
    return SystemConstant.KEY_PREFIX + "" + configKey;
  }

  public void setConfigKey(String configKey) {
    if (configKey.startsWith(SystemConstant.KEY_PREFIX)) this.configKey = configKey;
    else this.configKey = SystemConstant.KEY_PREFIX + "" + configKey;
  }
}
