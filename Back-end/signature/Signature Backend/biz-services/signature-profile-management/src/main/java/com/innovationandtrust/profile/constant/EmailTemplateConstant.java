package com.innovationandtrust.profile.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmailTemplateConstant {
    public static final String RESET_PASSWORD_INVITATION = "reset-password-invitation-template.html";
    public static final String RESET_PASSWORD_SUCCESS = "reset-password-successfully-template.html";
    public static final String END_USER_INVITATION = "end-user-invitation-template.html";
    public static final String CORPORATE_USER_INVITATION = "corporate-user-invitation-template.html";
    public static final String CONFIRM_CHANG_MAIL = "change-mail-confirm-template.html";
}
