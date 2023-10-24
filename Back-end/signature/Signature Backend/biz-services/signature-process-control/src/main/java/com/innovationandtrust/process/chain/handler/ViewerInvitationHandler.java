package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.SignatoryFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.constant.ParticipantRole;
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
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewerInvitationHandler extends AbstractExecutionHandler {

  private final TemplateEngine templateEngine;
  private final SignatoryFeignClient signatoryFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final EmailService emailService;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    this.sendInvitation(project);
    return ExecutionState.NEXT;
  }

  private void sendInvitation(Project project) {
    var participants = project.getParticipantsToInviteBy(ParticipantRole.VIEWER.getRole());
    if (!participants.isEmpty()) {

      var logo =
          this.corporateProfileFeignClient.viewFileContent(project.getCorporateInfo().getLogo());

      var company = project.getCorporateInfo();
      List<MailRequest> mailRequests = new ArrayList<>();

      participants.forEach(
          person -> {
            person.setInvited(true);
            person.setInvitationDate(new Date());

            var request = this.emailService.prepareParticipantMail(project, person, company, logo);
            mailRequests.add(request.getMailRequest(templateEngine));
          });

      this.emailService.sendInvitationMail(mailRequests, project.getCorporateInfo().getLogo());

      var signatories =
          participants.stream()
              .map(person -> new SignatoryRequest(person.getId(), InvitationStatus.SENT))
              .toList();
      this.signatoryFeignClient.updateStatus(project.getId(), signatories);
    }
  }
}
