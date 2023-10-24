package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.SignatoryFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.enums.ScenarioStep;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class ParticipantUnorderedInvitationHandler extends AbstractExecutionHandler {

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

    this.sendInvitation(project);

    context.put(SignProcessConstant.PROJECT_KEY, project);
    if (project.getTemplate().getSignProcess().equals(ScenarioStep.INDIVIDUAL_SIGN)) {
      context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.CREATE);
    } else {
      context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    }
    return ExecutionState.NEXT;
  }

  private void sendInvitation(Project project) {
    var approval = project.getParticipantsToInviteBy(ParticipantRole.APPROVAL.getRole());
    List<Participant> participants = new ArrayList<>();
    if (!approval.isEmpty()) {
      participants =
          project.getTemplate().isOrderApprove()
              ? project.getParticipantToInviteWithOrder().stream().toList()
              : project.getParticipantsToInviteBy(ParticipantRole.APPROVAL.getRole());
      this.sendInvitationMail(project, participants);
    }
    if (project.isReadyToSign()) {
      participants.addAll(project.getParticipantsToInviteBy(ParticipantRole.SIGNATORY.getRole()));
      this.sendInvitationMail(project, participants);
    }
  }

  private void sendInvitationMail(Project project, List<Participant> participants) {
    var company = project.getCorporateInfo();
    List<Participant> participantsToUpdate = new ArrayList<>();
    List<MailRequest> mailRequests = new ArrayList<>();

    participants.forEach(
        person -> {
          person.setInvited(true);
          person.setInvitationDate(new Date());

          var request =
              this.emailService.prepareParticipantMail(project, person, company, this.logoFile);
          request.setExpireDate(project.getDetail().getExpireDate());
          mailRequests.add(request.getMailRequest(templateEngine));

          participantsToUpdate.add(person);
        });

    this.emailService.sendInvitationMail(mailRequests, company.getLogo());

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
