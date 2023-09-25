package com.tessi.cxm.pfl.ms5.dto;

import com.tessi.cxm.pfl.ms5.entity.Privilege;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Functionality implements Serializable {

  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      implementation = com.tessi.cxm.pfl.ms5.constant.Functionality.class)
  private String key;

  private String value;
  private String visibilityLevel;
  private String modificationLevel;
  private List<Privilege> privileges;
}
