package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserParticipantDto implements Serializable {
  private Long id;

  @NotEmpty(message = "First name must be set")
  private String firstName;

  @NotBlank
  @NotEmpty(message = "Last name must be set")
  private String lastName;

  @NotEmpty(message = "Email must be set")
  private String email;

  @NotEmpty(message = "Phone must be set")
  private String phone;

  private Integer sortOrder;

  @NotEmpty(message = "First name must be set")
  private String role;

  private long templateId;

  private long userId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public String getFullName() {
    firstName = firstName != null ? firstName : "";
    lastName = lastName != null ? lastName : "";

    return (firstName + " " + lastName).trim();
  }

  public UserParticipantDto(UserParticipantDto participant) {
    this.id = participant.id;
    this.firstName = participant.firstName;
    this.lastName = participant.lastName;
    this.email = participant.email;
    this.phone = participant.phone;
    this.sortOrder = participant.sortOrder;
    this.role = participant.role;
    this.templateId = participant.templateId;
    this.userId = participant.userId;
  }
}
