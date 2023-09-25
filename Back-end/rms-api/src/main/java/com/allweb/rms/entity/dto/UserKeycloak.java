package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserKeycloak implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  @NotEmpty @NotNull private String username;
  private String firstName;
  private String lastName;
  private String email;
  private boolean enabled;

  @Schema(hidden = true)
  private Date createdTimestamp;
}
