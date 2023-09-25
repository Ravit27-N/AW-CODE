package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponseDTO implements Serializable {

  @Schema(type = "int64", example = "1")
  private long id;

  @Schema(type = "string", example = "User1")
  private String username;

  @Schema(type = "string", example = "User1")
  private String firstName;

  @Schema(type = "string", example = "User1")
  private String lastName;

  @Schema(type = "string", example = "user1@mail.com")
  private String email;

  @JsonProperty("service")
  private DepartmentDto department;

  @Schema(type = "object")
  private List<UserAssignedProfileDTO> profiles;

  @Schema(type = "boolean")
  private Boolean admin;

  private AddressDto userReturnAddress;
}
