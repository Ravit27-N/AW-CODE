package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryUserResponseDTO implements Serializable {

  @Schema(type = "int64", example = "1")
  private long id;

  @Schema(type = "string", example = "John")
  private String firstName;

  @Schema(type = "string", example = "Doe")
  private String lastName;

  @Schema(type = "string", example = "user1@mail.com")
  private String email;

  @Schema(type = "object")
  private transient ClientAssignedServiceDTO client;

  @Schema(type = "object")
  private transient UserAssignedServiceDTO service;

  @Schema(type = "object")
  private List<UserAssignedProfileDTO> profiles;

  @Schema(type = "boolean")
  private Boolean admin;

  private String returnAddressLevel;
  private AddressDto userReturnAddress;
  @JsonIgnore
  public String getName() {
    return String.format("%s %s", this.firstName, this.lastName);
  }

  public void setService(UserAssignedServiceDTO service) {
    this.service = Objects.requireNonNullElseGet(service, UserAssignedServiceDTO::new);
  }

  // updated new
  public void setServiceByDepartment(Department department) {
    this.service = new UserAssignedServiceDTO(department.getId(), department.getName(),
        department.getDivision().getName(), department.getDivision().getId(),
        department.getDivision().getClient().getName(), department.getDivision().getClient().getId());
  }
}
