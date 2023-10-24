package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.config.FrontEndProperty;
import com.innovationandtrust.profile.model.email.ConfirmChangeMail;
import com.innovationandtrust.profile.model.email.CorporateUserInvitation;
import com.innovationandtrust.profile.model.email.EndUserInvitation;
import com.innovationandtrust.profile.model.email.ResetPasswordInvitation;
import com.innovationandtrust.profile.model.email.ResetPasswordSuccess;
import com.innovationandtrust.share.model.processcontrol.EmailTheme;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
@RequiredArgsConstructor
public class MailService {
  private final TemplateEngine templateEngine;
  private final FrontEndProperty frontEndProperty;
  private final NotificationFeignClient notificationFeignClient;

  /**
   * To send reset password, successful mail.
   *
   * @param firstName refers to the first name of the user
   * @param emailRecipient refers to the email of user that we sent to
   */
  public void sendResetPasswordSuccessfullyTemplate(String firstName, String emailRecipient) {
    var request = new ResetPasswordSuccess(firstName, emailRecipient, null);
    this.notificationFeignClient.sendMail(request.getMailRequest(templateEngine), null);
  }

  /**
   * To send reset password mail to user.
   *
   * @param username refers to the username of the user.
   * @param emailRecipient refers to the email of user that we sent to.
   * @param resetToken is the token that use to reset password.
   * @param expiryDate refers to expiry datetime of the reset password link.
   */
  public void sendResetPasswordLink(
      String username,
      String emailRecipient,
      String resetToken,
      Date expiryDate,
      boolean isSuperAdmin) {
    var request =
        new ResetPasswordInvitation(
            username,
            emailRecipient,
            isSuperAdmin
                ? frontEndProperty.getSuperAdminUrl()
                : frontEndProperty.getFrontEndBaseUrl(),
            null,
            resetToken,
            expiryDate);
    this.notificationFeignClient.sendMail(request.getMailRequest(templateEngine), null);
  }

  public void endUserInvitation(
      String fullName, String emailRecipient, String password, EmailTheme theme, String token) {
    var request =
        new EndUserInvitation(
            fullName,
            emailRecipient,
            frontEndProperty.getFrontEndBaseUrl(),
            theme,
            password,
            token);
    this.notificationFeignClient.sendMail(request.getMailRequest(templateEngine), null);
  }

  public void corporateUserInvitation(
      String fullName, String emailRecipient, String password, EmailTheme theme, String token) {
    var request =
        new CorporateUserInvitation(
            fullName,
            emailRecipient,
            frontEndProperty.getFrontEndBaseUrl(),
            theme,
            password,
            token);

    this.notificationFeignClient.sendMail(request.getMailRequest(templateEngine), null);
  }

  public void confirmChangeMail(
      String fullName, String emailRecipient, EmailTheme theme, String token) {
    var request =
        new ConfirmChangeMail(
            fullName, emailRecipient, frontEndProperty.getFrontEndBaseUrl(), theme, token);
    this.notificationFeignClient.sendMail(request.getMailRequest(templateEngine), null);
  }
}
