package com.innovationandtrust.process.constant;

import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public enum EmailDefaultValue {
  APPROVAL(
      RoleConstant.ROLE_APPROVAL,
      Map.of(
          Type.SUBJECT,
          MessageConstant.APPROVAL_INVITE_SUBJECT,
          Type.MESSAGE,
          MessageConstant.APPROVAL_INVITE_MESSAGE,
          Type.THEME,
          MessageConstant.APPROVAL_INVITE_THEME,
          Type.TEMPLATE,
          InvitationTemplateConstant.EMAIL_APPROVAL_TEMPLATE)),
  SIGNER(
      RoleConstant.ROLE_SIGNATORY,
      Map.of(
          Type.SUBJECT,
          MessageConstant.SIGNER_INVITE_SUBJECT,
          Type.MESSAGE,
          MessageConstant.SIGNER_INVITE_MESSAGE,
          Type.THEME,
          MessageConstant.SIGNER_INVITE_THEME,
          Type.TEMPLATE,
          InvitationTemplateConstant.EMAIL_SIGNING_TEMPLATE)),
  VIEWER(
      RoleConstant.ROLE_VIEWER,
      Map.of(
          Type.SUBJECT,
          MessageConstant.VIEWER_INVITE_SUBJECT,
          Type.MESSAGE,
          MessageConstant.VIEWER_INVITE_MESSAGE,
          Type.TEMPLATE,
          InvitationTemplateConstant.EMAIL_VIEWER_TEMPLATE)),
  RECIPIENT(
      RoleConstant.ROLE_RECEIPT,
      Map.of(
          Type.SUBJECT,
          MessageConstant.RECIPIENT_INVITE_SUBJECT,
          Type.MESSAGE,
          MessageConstant.RECIPIENT_INVITE_MESSAGE,
          Type.TEMPLATE,
          InvitationTemplateConstant.EMAIL_RECIPIENT_TEMPLATE)),
  SIGN_COMPLETED(
      ProcessStatus.SIGN_COMPLETED,
      Map.of(
          Type.SUBJECT,
          MessageConstant.SIGN_COMPLETED_SUBJECT,
          Type.MESSAGE,
          MessageConstant.SIGN_COMPLETED_MESSAGE,
          Type.TEMPLATE,
          InvitationTemplateConstant.SIGN_COMPLETED_TEMPLATE)),
  VIDEO_ACCEPTED(
      ProcessStatus.EID_ACCEPTED,
      Map.of(
          Type.SUBJECT,
          MessageConstant.VIDEO_ACCEPTED_SUBJECT,
          Type.MESSAGE,
          MessageConstant.VIDEO_ACCEPTED_MESSAGE,
          Type.TEMPLATE,
          InvitationTemplateConstant.VIDEO_ACCEPTED_TEMPLATE)),
  VIDEO_REJECTED(
      ProcessStatus.EID_REJECTED,
      Map.of(
          Type.SUBJECT,
          MessageConstant.VIDEO_REJECTED_SUBJECT,
          Type.MESSAGE,
          MessageConstant.VIDEO_REJECTED_MESSAGE,
          Type.TEMPLATE,
          InvitationTemplateConstant.VIDEO_REJECTED_TEMPLATE));

  private static final Map<String, EmailDefaultValue> BY_ROLE = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_ROLE.put(v.role, v));
  }

  private final String role;
  private final Map<Type, String> type;

  EmailDefaultValue(String role, Map<Type, String> type) {
    this.role = role;
    this.type = type;
  }

  public static String getSubject(String role) {
    return getByRole(role).type.get(Type.SUBJECT);
  }

  public static String getMessage(String role) {
    return getByRole(role).type.get(Type.MESSAGE);
  }

  public static String getTemplate(String role) {
    return getByRole(role).type.get(Type.TEMPLATE);
  }

  public static String getTheme(String role) {
    return getByRole(role).type.get(Type.THEME);
  }

  private static EmailDefaultValue getByRole(String role) {
    return Optional.of(BY_ROLE.get(role))
        .orElseThrow(() -> new IllegalArgumentException("Invalid role..."));
  }

  @Getter
  private enum Type {
    SUBJECT,
    MESSAGE,
    THEME,
    TEMPLATE
  }
}
