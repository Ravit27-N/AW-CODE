package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.model.email.EmailInvitationModel;
import com.innovationandtrust.process.model.email.EmailInvitationRequest;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.mail.model.MailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

@ExtendWith(SpringExtension.class)
class ViewerInvitationHandlerTest {
  private ViewerInvitationHandler viewerInvitationHandler;
  private ExecutionContext context;
  @Mock private EmailInvitationRequest emailInvitationRequest;
  private MailRequest mailRequest;
  @Mock private TemplateEngine templateEngine;
  @Mock private EmailInvitationModel emailInvitationModel;
  @Mock private EmailService emailService;
  @Mock private ProjectFeignClient projectFeignClient;

  @BeforeEach
  public void setup() {
    viewerInvitationHandler =
        spy(
            new ViewerInvitationHandler(
                new TemplateEngine(),
                projectFeignClient,
                emailService,
                mock(ValidateCorporateSettingHandler.class)));
    context = UnitTestProvider.getContext();
    emailInvitationRequest.setRole(RoleConstant.ROLE_VIEWER);
    mailRequest = UnitTestProvider.getMailRequest();
  }

  @Test
  @Order(1)
  @DisplayName("Viewer invitation handler test")
  void execute() {
    // when
    when(this.emailService.prepareParticipantMail(any(), any(), any()))
        .thenReturn(emailInvitationRequest);
    when(this.emailInvitationRequest.getMailRequest(any())).thenReturn(mailRequest);
    String mailBody = "Mail Body";
    when(this.emailInvitationModel.getBody(any())).thenReturn(mailBody);
    when(this.templateEngine.process(anyString(), any())).thenReturn(mailBody);

    this.viewerInvitationHandler.execute(context);

    verify(this.viewerInvitationHandler, times(1)).execute(context);
  }
}
