package com.innovationandtrust.process.model;

import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.share.model.project.Participant.ValidPhone;
import com.innovationandtrust.share.model.user.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInfo implements Serializable {

  private String flowId;
  private String signatureLevel;
  private String uuid;
  private String projectName;
  private String projectStatus;
  private String signingProcess;
  private User creatorInfo;
  private Date invitationDate;
  private PhoneNumber phoneNumber;
  private OtpInfo otpInfo;
  private Actor actor;
  private List<DocumentInfo> documents;
  private CompanySettingDto setting;
  private boolean isAllSigned;
  private String identityId;
  private Boolean requestToSign;
  private int errorValidationOtp;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PhoneNumber implements Serializable {
    private String removedNumber;

    // The length removed from the phone number
    private int missingLength;
    @Builder.Default private boolean isValidated = false;
    @Builder.Default private int totalAttempts = 0;
    @Builder.Default private String number = "";

    public PhoneNumber(ValidPhone validPhone) {
      this.isValidated = validPhone.isValid();
      this.number = validPhone.getNumber();
      this.totalAttempts = validPhone.getTotalAttempts();
    }
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Actor implements Serializable {
    private String firstName;
    private String lastName;
    private String role;
    // To indicate, the action of the actor is approved or signed the document.
    private boolean processed;
    // To indicate, the action of the actor is refused the document.
    private String comment;
    private String signatureMode;
    private String signatureImage;
    @Builder.Default private boolean documentVerified = false;
    @Builder.Default private boolean isProcessing = false;
    private String eidStatus;
  }
}
