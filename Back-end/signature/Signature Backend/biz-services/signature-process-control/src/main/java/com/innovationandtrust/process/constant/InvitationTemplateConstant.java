package com.innovationandtrust.process.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvitationTemplateConstant {
  public static final String FAILED_VALIDATE_PHONE = "failed-validate-phone.html";
  public static final String PROJECT_EXPIRED_NOTI = "project-expired-noti-template.html";
  public static final String SIGN_SMS_REMINDER_TEMPLATE = "sign-reminder-sms-template.html";
  public static final String APPROVE_SMS_REMINDER_TEMPLATE = "approve-reminder-sms-template.html";
  public static final String EMAIL_APPROVAL_TEMPLATE = "approve-invitation-mail-template.html";
  public static final String EMAIL_SIGNING_TEMPLATE = "sign-invitation-mail-template.html";
  public static final String EMAIL_RECIPIENT_TEMPLATE = "recipient-mail-template.html";
  public static final String EMAIL_REFUSE_TEMPLATE = "refuse-mail-template.html";
  public static final String EMAIL_REFUSE_CONFIRM_TEMPLATE =
      "signer-refused-confirm-mail-template.html";
  public static final String EMAIL_REFUSE_INFORM_TEMPLATE =
      "signatories-refused-inform-mail-template.html";
  public static final String EMAIL_VIEWER_TEMPLATE = "viewer-invitation-mail-template.html";
  public static final String APPROVE_REMINDER_TEMPLATE = "approve-reminder-mail-template.html";
  public static final String SIGN_REMINDER_TEMPLATE = "sign-reminder-mail-template.html";
  public static final String MODIFICATION_PROJECT_TEMPLATE =
      "modification-project-mail-template.html";
  public static final String SIGN_COMPLETED_TEMPLATE = "sign-completed-mail-template.html";
  public static final String VIDEO_ACCEPTED_TEMPLATE = "video-accepted-mail-template.html";
  public static final String VIDEO_REJECTED_TEMPLATE = "video-rejected-mail-template.html";
  public static final String SFTP_INSERT_FILE_FAIL = "sftp-insert-file-fail-mail-template.html";
}
