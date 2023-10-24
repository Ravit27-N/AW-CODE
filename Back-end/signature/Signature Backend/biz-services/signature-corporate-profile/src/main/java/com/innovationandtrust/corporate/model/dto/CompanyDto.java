package com.innovationandtrust.corporate.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto implements Serializable {

  private Long id;
  private String logo;
  private Integer totalEmployees;

  @NotNull @NotEmpty private String name;

  @NotNull @NotEmpty private String siret;

  @JsonProperty(access = Access.READ_ONLY)
  private Long createdBy;

  @JsonProperty(access = Access.READ_ONLY)
  private Long modifiedBy;

  @JsonProperty(access = Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = Access.READ_ONLY)
  private Date modifiedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CompanyDto that)) {
      return false;
    }
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
