package com.innovationandtrust.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SignatoryDTO is a class that used for creating a new signatory, and response back to the client
 * when they request it.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatoryProject implements Serializable, Comparable<SignatoryProject> {
  private Long id;
  @NotEmpty private String firstName;
  @NotEmpty private String lastName;
  private String role;
  private String email;
  private String phone;
  private Integer sortOrder;
  private String comment;
  private Date dateStatus;
  private Date sentDate;

  @JsonProperty(access = Access.READ_ONLY)
  private String invitationStatus;

  @JsonProperty(access = Access.READ_ONLY)
  private String documentStatus;

  @JsonProperty(access = Access.READ_ONLY)
  private String uuid;

  @JsonProperty(access = Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = Access.READ_ONLY)
  private Date modifiedAt;

  @JsonProperty(access = Access.READ_ONLY)
  private ProjectSignatory project;

  @Override
  public int compareTo(SignatoryProject signatoryDTO) {
    return this.sortOrder.compareTo(signatoryDTO.getSortOrder());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SignatoryProject that)) return false;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
