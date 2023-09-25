package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Group implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String name;

  @Schema(hidden = true)
  private Map<String, List<String>> clientRoles;
}
