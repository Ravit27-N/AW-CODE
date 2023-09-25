package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tessi.cxm.pfl.ms5.constant.AddressType;
import com.tessi.cxm.pfl.ms5.dto.enumeration.UseActionType;
import com.tessi.cxm.pfl.ms5.validators.PasswordMatches;
import com.tessi.cxm.pfl.ms5.validators.ValidPassword;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
//@PasswordMatches
public class CreateUserRequestDTO implements Serializable {

  @NotEmpty
  @Email
  @Schema(
      type = "string",
      description = "User's email use as a username.",
      example = "user1@gmail.com")
  private String email;

  @NotEmpty
  @Schema(type = "string", description = "Firstname of a user.", example = "John")
  private String firstName;

  @NotEmpty
  @Schema(type = "string", description = "Lastname of a user.", example = "Doe")
  private String lastName;


  @Schema(type = "string", description = "User's password.", example = "123")
  //@ValidPassword
  private String password;

  @Schema(type = "string", description = "User's confirmed password.", example = "123")
  /*@NotNull
  @Size(min = 1)*/
  private String confirmedPassword;

  @Schema(type = "boolean", description = "If user is admin user", example = "false")
  private Boolean admin;

  @Min(1)
  @Schema(
      type = "integer",
      format = "int64",
      description = "The service to which the user is attached.",
      example = "1")
  private Long serviceId;

  @NotEmpty
  @ArraySchema(
      schema =
          @Schema(
              type = "integer",
              format = "int64",
              description = "The profiles to which the user is attached.",
              example = "[1,2,3]"))
  private List<Long> profiles;
  private UseActionType userAction = UseActionType.OTHER;
  private String returnAddressLevel;
  private AddressDto userReturnAddress;
}
