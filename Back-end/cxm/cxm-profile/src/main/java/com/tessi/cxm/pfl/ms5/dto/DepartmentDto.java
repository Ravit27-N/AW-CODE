package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartmentDto implements Serializable {

  private static final long serialVersionUID = 1L;
  private long id;

  @NotEmpty(message = "name is required and cannot be empty!")
  private String name;

  @JsonProperty(access = Access.READ_ONLY)
  private long divisionId;

  private AddressDto address;
}
