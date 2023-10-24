package com.innovationandtrust.profile.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
class MailServiceTests {
  private MailService mailService;
  @Mock private
  TemplateEngine templateEngine;

  @BeforeEach
  void setup() {}

  @Test
  @DisplayName("Send reset password successfully template")
  void send_reset_password_successfully_template() {
    // when
    mailService.sendResetPasswordSuccessfullyTemplate("Her", "herman@gmail.com");

    // then
    verify(mailService, times(1)).sendResetPasswordSuccessfullyTemplate("Her", "herman@gmail.com");
  }

  @Test
  @DisplayName("Send reset password link")
  void send_reset_password_link() {
    // when
    mailService.sendResetPasswordLink(
        "Herman", "herman@gmail.com", "123token", new Date(),true);

    // then
    verify(mailService, times(1))
        .sendResetPasswordLink(
            "Herman", "herman@gmail.com", "123token", new Date(),true);
  }
}
