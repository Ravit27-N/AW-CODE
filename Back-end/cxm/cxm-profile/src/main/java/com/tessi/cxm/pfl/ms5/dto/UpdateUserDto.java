package com.tessi.cxm.pfl.ms5.dto;

import com.tessi.cxm.pfl.shared.model.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

  @Schema(type = "int64", example = "1")
  private long id;

  @NotEmpty
  @Schema(type = "string", description = "First name of a user.", example = "John")
  private String firstName;

  @NotEmpty
  @Schema(type = "string", description = "Last name of a user.", example = "Doe")
  private String lastName;

  @Schema(type = "int64", example = "1")
  private long serviceId;

  @Schema(type = "string", example = "123")
  private String password;

  @Schema(type = "boolean", example = "false")
  private Boolean admin;

  @NotEmpty private List<Long> profiles;

  private String returnAddressLevel;
  private AddressDto userReturnAddress;
}
