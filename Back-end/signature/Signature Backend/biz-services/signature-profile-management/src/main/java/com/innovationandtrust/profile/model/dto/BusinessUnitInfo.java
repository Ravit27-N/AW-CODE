package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessUnitInfo implements Serializable {

  @NotEmpty private Long id;

  @NotEmpty private String unitName;

  private Integer sortOrder;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long companyDetailId;
}
