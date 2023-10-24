package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.SignatoryFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.model.project.Participant;
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
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParticipantOrderInvitationHandler extends AbstractExecutionHandler {
  private final TemplateEngine templateEngine;
  private final SignatoryFeignClient signatoryFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final EmailService emailService;

  private Resource logoFile;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = ProcessControlUtils.getProject(context);

    var company = project.getCorporateInfo();
    ProcessControlUtils.checkCompanyInfo(company, project.getFlowId());

    this.logoFile =
        this.corporateProfileFeignClient.viewFileContent(project.getCorporateInfo().getLogo());

    if (!project.getTemplate().isOrderApprove()) {
      this.sendApprovalInvitation(project);
      if (project.isReadyToSign()) {
        this.sendSignInvitation(project);
      }
    } else {
      this.sendSignInvitation(project);
    }

    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private void sendSignInvitation(Project project) {
    var company = project.getCorporateInfo();
    List<Participant> participantsToUpdate = new ArrayList<>();

    project
        .getParticipantToInviteWithOrder()
        .ifPresent(
            person -> {
              person.setInvited(true);
              person.setInvitationDate(new Date());
              participantsToUpdate.add(person);

              log.info("The next person to invite {}", person.getFullName());
              if (!person.isEndUser()) {
                Executors.newSingleThreadExecutor()
                    .execute(
                        () -> {
                          if (person.isSigner() || person.isApprover()) {
                            var request =
                                this.emailService.prepareParticipantMail(
                                    project, person, company, this.logoFile);
                            request.setExpireDate(project.getDetail().getExpireDate());
                            this.emailService.sendInvitationMail(
                                request.getMailRequest(templateEngine), company.getLogo());
                          }
                        });
              } else {
                log.info("This person {} is an end user ...!", person.getFullName());
              }
            });

    this.updateParticipants(project.getId(), participantsToUpdate);
  }

  private void sendApprovalInvitation(Project project) {
    var company = project.getCorporateInfo();
    var participants = project.getParticipantsToInviteBy(ParticipantRole.APPROVAL.getRole());
    List<Participant> participantsToUpdate = new ArrayList<>();
    List<MailRequest> mailRequests = new ArrayList<>();

    participants.forEach(
        person -> {
          person.setInvited(true);
          person.setInvitationDate(new Date());
          participantsToUpdate.add(person);

          var request =
              this.emailService.prepareParticipantMail(project, person, company, this.logoFile);

          if (Objects.nonNull(request)) {
            request.setRole(person.getRole());
            request.setExpireDate(project.getDetail().getExpireDate());
            mailRequests.add(request.getMailRequest(templateEngine));
          }
        });

    this.emailService.sendInvitationMail(mailRequests, project.getCorporateInfo().getLogo());

    this.updateParticipants(project.getId(), participantsToUpdate);
  }

  private void updateParticipants(Long projectId, List<Participant> participants) {
    Executors.newSingleThreadExecutor()
        .execute(
            () ->
                this.signatoryFeignClient.updateStatus(
                    projectId,
                    participants.stream()
                        .map(
                            person ->
                                new SignatoryRequest(
                                    person.getId(), InvitationStatus.SENT, person.getUuid()))
                        .toList()));
  }
}
