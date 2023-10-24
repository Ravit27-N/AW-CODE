package com.innovationandtrust.project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.innovationandtrust.share.constant.ParticipantRole;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SignatoryDto is a class that used for creating a new signatory, and response back to the client
 * when they request it.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatoryDto implements Serializable, Comparable<SignatoryDto> {

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

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long projectId;

  public SignatoryDto(
      Long id,
      String firstName,
      String lastName,
      String role,
      String email,
      String phone,
      Integer sortOrder) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
    this.email = email;
    this.phone = phone;
    this.sortOrder = sortOrder;
  }

  public void setRole(String role) {
    if (!ParticipantRole.isValidRole(role)) {
      throw new IllegalArgumentException("Invalid role");
    }
    this.role = role;
  }

  @Override
  public int compareTo(SignatoryDto dto) {
    if (Objects.nonNull(this.sortOrder)) {
      return this.sortOrder.compareTo(dto.getSortOrder());
    }
    return 0;
  }

  /**
   * Sorting signatory if record no sentDate must be at the last.
   *
   * @param dto signatory.
   * @return sort old record to new record by sentDate, null means the new record.
   */
  public int compareToSentDate(SignatoryDto dto) {
    if (Objects.isNull(this.sentDate)) {
      return 1;
    }
    return Objects.isNull(dto.getSentDate()) ? -1 : this.sentDate.compareTo(dto.getSentDate());
  }

  /**
   * Sorting signatory if record no dateStatus must be at the last.
   *
   * @param dto signatory.
   * @return sort old record to new record by dateStatus, null means the new record.
   */
  public int compareToDateStatus(SignatoryDto dto) {
    if (Objects.isNull(this.dateStatus)) {
      return 1;
    }
    return Objects.isNull(dto.getDateStatus()) ? -1 : this.dateStatus.compareTo(dto.getDateStatus());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SignatoryDto that)) {
      return false;
    }
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getProjectId(), that.getProjectId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getProjectId());
  }
}
