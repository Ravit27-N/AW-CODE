package com.tessi.cxm.pfl.ms5.dto;

import com.tessi.cxm.pfl.ms5.exception.InvalidUserException;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {

  private UserDto user;

  @NotEmpty private List<Long> profiles;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserDto {

    @NotEmpty
    @Schema(type = "string", example = "1")
    private String id;

    @Schema(type = "string", example = "User1")
    private String fullName;

    @Schema(type = "string", example = "user1@mail.com")
    private String email;

    @JsonIgnore
    public String getFirstname() {
      return this.getPathOfFullName(0);
    }

    @JsonIgnore
    public String getLastname() {
      return this.getPathOfFullName(1);
    }

    private String getPathOfFullName(int part) {
      var splitFullName = this.fullName.trim().split(" ");
      if (splitFullName.length != 2) {
        throw new InvalidUserException("Full name must consist of firstname and lastname.");
      }
      return splitFullName[part];
    }
  }
}
