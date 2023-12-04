package com.innovationandtrust.process.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.InvitationMessage;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectDetail;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EmailServiceTest {
  private EmailService emailService;
  @Mock private ImpersonateTokenService impersonateTokenService;
  @Mock private NotificationFeignClient notificationFeignClient;
  private final ProcessControlProperty property = UnitTestProvider.getProperty();
  private Project project;
  private Participant participant;
  private CorporateInfo corporateInfo;
  private String link;
  private String token;

  @BeforeEach
  public void setup() {
    emailService =
        spy(new EmailService(impersonateTokenService, notificationFeignClient, property));

    String flowId = "022e2923-924b-4745-a2a8-250077141b83";
    String companyUuid = "244eb546-2343-41d4-8c47-9c5d1ec947e0";
    token =
        "DUCBtmFRpgN0Ya8McWP7K0C6jSqxC9UITjL-vS-pW_qCNb3vYqBI0hngHZGs_nxgygRrDNQZJn_hlNm772p73_aAjjAuEPXzfxivUJktqEtCZGVzieZyvMGgWJHR18XrIZkPNLyZlyV_4tQmiN1fJstgW5qxVeR6xVWhkpCXe6c";

    InvitationMessage invitationMessage =
        InvitationMessage.builder()
            .invitationSubject("Subject")
            .invitationMessage("Message")
            .build();
    ProjectDetail detail =
        ProjectDetail.builder()
            .invitationMessages(Collections.singletonList(invitationMessage))
            .build();

    project = Project.builder().flowId(flowId).detail(detail).build();
    participant =
        Participant.builder()
            .email("signature@certigna.com")
            .lastName("SIGNER")
            .firstName("OME")
            .role(RoleConstant.ROLE_SIGNATORY)
            .build();
    corporateInfo = CorporateInfo.builder().companyUuid(companyUuid).build();
    link = getLink();
  }

  private String getLink() {
    String slash = "\\";
    return String.format(
        "%s%s%s%s%s%s?token=%s",
        property.getFrontEndUrl(),
        slash,
        property.getInvitationContextPath(),
        slash,
        corporateInfo.getCompanyUuid(),
        slash,
        token);
  }

  @Test
  @DisplayName("Prepare sign mail project test")
  void testPrepareSignedCompleteMailTest() {
    when(this.impersonateTokenService.getTokenUrlParam(
            anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(link);
    this.emailService.prepareSignCompleteMail(project, participant, corporateInfo);
    verify(this.emailService, times(1))
        .prepareSignCompleteMail(project, participant, corporateInfo);
  }

  @Test
  @DisplayName("Prepare participant mail project test")
  void testPrepareParticipantMailTest() {
    when(this.impersonateTokenService.getTokenUrlParam(
            anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(link);
    this.emailService.prepareParticipantMail(project, participant, corporateInfo);
    verify(this.emailService, times(1))
        .prepareParticipantMail(project, participant, corporateInfo);
  }

  @Test
  @DisplayName("Send multiple mail test")
  void testSendMultipleMailTest() {
    final MailRequest mailRequest = new MailRequest("signature@certigna.com", "Subject", "Body");
    this.emailService.sendInvitationMail(Collections.singletonList(mailRequest), "logo.png");
    verify(this.emailService, times(1))
        .sendInvitationMail(Collections.singletonList(mailRequest), "logo.png");
  }

  @Test
  @DisplayName("Send single mail test")
  void testSendSingleMailTest() {
    final MailRequest mailRequest = new MailRequest("signature@certigna.com", "Subject", "Body");
    this.emailService.sendInvitationMail(mailRequest, "logo.png");
    verify(this.emailService, times(1)).sendInvitationMail(mailRequest, "logo.png");
  }
}
