package com.innovationandtrust.corporate.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractBusinessDto {
  private Long id;
  @NotEmpty private String unitName;
  private Integer sortOrder;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long companyDetailId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long createdBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long modifiedBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<BusinessUnitChild> children;
}
