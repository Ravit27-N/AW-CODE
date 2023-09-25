package com.allweb.rms.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyProfileDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  private int id;
  @NotEmpty private String title;
  private String description;
  private String address;
  @NotEmpty private String email;
  @NotEmpty private String website;
  @NotEmpty private String telephone;

  @Schema(hidden = true)
  private Date createdAt;

  @Schema(hidden = true)
  private Date updatedAt;
}
