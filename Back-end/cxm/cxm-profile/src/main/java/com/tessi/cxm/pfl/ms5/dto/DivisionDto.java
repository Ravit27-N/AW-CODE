package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
public class DivisionDto implements Serializable {

  private static final long serialVersionUID = 1L;
  private long id;

  @NotEmpty(message = "name is required and cannot be empty!")
  private String name;

  @JsonProperty(access = Access.READ_ONLY)
  private long clientId;

  @NotEmpty(message = "name is required and cannot be empty!")
  private List<DepartmentDto> services = new ArrayList<>();

  private AddressDto address;
  public List<DepartmentDto> getServices() {
    if (this.services == null) {
      return List.of();
    }
    return services.stream()
        .sorted(Comparator.comparingLong(DepartmentDto::getId))
        .collect(Collectors.toList());
  }

  public DivisionDto(long id, @NotEmpty(message = "name is required and cannot be empty!") String name) {
    this.id = id;
    this.name = name;
  }

}
