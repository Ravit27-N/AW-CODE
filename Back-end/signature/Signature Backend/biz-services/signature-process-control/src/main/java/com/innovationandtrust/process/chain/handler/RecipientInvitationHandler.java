package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.SignatoryFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecipientInvitationHandler extends AbstractExecutionHandler {
  private final TemplateEngine templateEngine;
  private final SignatoryFeignClient signatoryFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final EmailService emailService;
  private Resource logoFile;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);

    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    this.logoFile =
        this.corporateProfileFeignClient.viewFileContent(project.getCorporateInfo().getLogo());
    this.inviteRecipient(project);

    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private void inviteRecipient(Project project) {
    if (project.hasNextParticipant()) {
      return;
    }

    var company = project.getCorporateInfo();
    var recipients = new ArrayList<SignatoryRequest>();
    List<MailRequest> mailRequests = new ArrayList<>();

    project
        .getParticipantsToInviteBy(RoleConstant.ROLE_RECEIPT)
        .forEach(
            participant -> {
              recipients.add(
                  new SignatoryRequest(
                      participant.getId(), InvitationStatus.SENT, participant.getUuid()));
              participant.setInvited(true);
              participant.setInvitationDate(new Date());

              var request =
                  this.emailService.prepareParticipantMail(
                      project, participant, company, this.logoFile);
              mailRequests.add(request.getMailRequest(templateEngine));
            });

    this.emailService.sendInvitationMail(mailRequests, project.getCorporateInfo().getLogo());

    this.signatoryFeignClient.updateStatus(project.getId(), recipients);
  }
}
