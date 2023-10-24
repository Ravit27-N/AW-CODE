package com.innovationandtrust.share.model.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.enums.SignatureMode;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Handling the information of the person in charge who is responsible for the approving or signing
 * documents process.
 *
 * @author Vichet CHANN
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant implements Serializable {

  private Long id;
  private String uuid;
  private int order;

  private String firstName;

  private String lastName;

  private String role;

  private String email;

  private String phone;

  private String comment;

  @Builder.Default private String actorUrl = "";

  @Builder.Default private boolean isInvited = false;

  private Date invitationDate;

  private GeneratedOTP otp;

  // required property before sign the documents
  private CaCgu caCgu;

  // The certificate url for signing the documents
  @Builder.Default private String certificate = "";

  @Builder.Default private ValidPhone validPhone = new ValidPhone();
  // For role signatory
  @Builder.Default private boolean isSigned = false;

  private Date signedDate;

  // For role approval
  @Builder.Default private boolean isApproved = false;

  @Builder.Default private boolean isReceived = false;

  @Builder.Default private boolean isRefused = false;

  @Builder.Default private boolean isEndUser = false;

  private String dossierId;

  @Builder.Default private boolean documentVerified = false;

  private String signatureMode = SignatureMode.WRITE.name();

  private String signatureImage;

  @JsonIgnore
  public long getActorId() {
    return Long.parseLong(actorUrl.substring(actorUrl.lastIndexOf("/") + 1));
  }

  @JsonIgnore
  public String getFullName() {
    return String.format("%s %s", this.firstName, this.lastName);
  }

  @JsonIgnore
  public String getOtpCode() {
    if (Objects.isNull(this.getOtp())) {
      return "";
    }

    return this.otp.getOtp();
  }

  /**
   * To verify, the person in charge has role signatory.
   *
   * @return true if signatory role nor false
   */
  @JsonIgnore
  public boolean isSigner() {
    return ParticipantRole.getByRole(this.role).equals(ParticipantRole.SIGNATORY);
  }

  /**
   * To verify, the person in charge has role approval.
   *
   * @return true if approval role nor false
   */
  @JsonIgnore
  public boolean isApprover() {
    return ParticipantRole.getByRole(this.role).equals(ParticipantRole.APPROVAL);
  }

  @JsonIgnore
  public boolean isViewer() {
    return ParticipantRole.getByRole(this.role).equals(ParticipantRole.VIEWER);
  }

  /**
   * To verify, the person in charge has role recipient.
   *
   * @return true if recipient role nor false
   */
  @JsonIgnore
  public boolean isReceiver() {
    return ParticipantRole.getByRole(this.role).equals(ParticipantRole.RECEIPT);
  }

  @JsonIgnore
  public boolean getProcessed() {
    if (this.isSigner()) {
      return this.isSigned || this.isRefused;
    } else if (this.isApprover() || this.isViewer()) {
      return this.isApproved || this.isRefused;
    } else {
      return this.isReceived;
    }
  }

  public Participant(String uuid) {
    this.uuid = uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Participant that)) return false;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getUuid(), that.getUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUuid());
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CaCgu implements Serializable {
    private String authority;
    private String downloadUrl;
    private String token;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ValidPhone implements Serializable {
    private boolean isValid = false;
    private int totalAttempts = 0;
    private String number = "";
    private int missingLength = 0;
  }
}
