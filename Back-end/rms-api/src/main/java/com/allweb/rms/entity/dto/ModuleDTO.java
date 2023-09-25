package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;
  private String name;
  private boolean active;
  private String description;

  @Schema(hidden = true)
  private Date createdAt;

  @Schema(hidden = true)
  private Date updatedAt;
}
