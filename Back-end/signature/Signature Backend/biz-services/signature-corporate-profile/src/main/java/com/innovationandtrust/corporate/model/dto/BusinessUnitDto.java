package com.innovationandtrust.corporate.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BusinessUnitDto extends AbstractBusinessDto implements Serializable {
  @Min(1)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long companyId;

  @Min(1)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long parentId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<EmployeeDTO> employees;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<FolderDto> folders;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long totalProjects = 0L;

  // Completed project percentage
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Double percentage = 0D;
}
