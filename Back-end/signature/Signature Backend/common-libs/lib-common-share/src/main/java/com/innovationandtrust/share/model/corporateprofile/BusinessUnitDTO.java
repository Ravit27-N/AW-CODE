package com.innovationandtrust.share.model.corporateprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BusinessUnitDTO implements Serializable {
  private Long id;
  @NotEmpty private String unitName;
  private Integer sortOrder;

  @Min(1)
  private Long companyId;

  @Min(1)
  private Long companyDetailId;

  @Min(1)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long parentId;

  @Min(1)
  private Long createdBy;

  @Min(1)
  private Long modifiedBy;

  private Set<BusinessUnitDTO> children = new HashSet<>();
}
