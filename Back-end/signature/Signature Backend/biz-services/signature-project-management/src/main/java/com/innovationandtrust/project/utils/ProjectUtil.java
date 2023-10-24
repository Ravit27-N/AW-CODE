package com.innovationandtrust.project.utils;

import com.innovationandtrust.project.constant.DocumentDetailType;
import com.innovationandtrust.project.constant.ProjectBadRequestConstant;
import com.innovationandtrust.project.exception.InvalidProjectArgException;
import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.model.entity.Project;
import com.innovationandtrust.project.model.entity.Signatory;
import com.innovationandtrust.project.restclient.ProfileFeignClient;
import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.enums.SignatureFormat;
import com.innovationandtrust.share.enums.SignatureLevel;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.DocumentDetail;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProjectUtil {
  private final ProfileFeignClient profileFeignClient;
  private final ModelMapper modelMapper;

  public ProjectUtil(ProfileFeignClient profileFeignClient, ModelMapper modelMapper) {
    this.profileFeignClient = profileFeignClient;
    this.modelMapper = modelMapper;
  }

  public Template getValidTemplate(Project project) {
    var template = this.getTemplate(project);
    if (Objects.nonNull(project.getTemplateId())) {
      this.validateTemplate(project.getSignatories(), template);
    }
    return template;
  }

  public Template getTemplate(Project project) {
    Template template;
    if (project.getTemplateId() == null) {
      template =
          Template.builder()
              .signProcess(project.isOrderSign() ? ScenarioStep.COUNTER_SIGN : ScenarioStep.COSIGN)
              .approvalProcess(ScenarioStep.APPROVAL)
              .level(SignatureLevel.LT)
              .format(SignatureFormat.PA_DES)
              .notificationService(NotificationConstant.SMS)
              .build();
      template.setOrderApprove(project.isOrderApprove());
      template.setOrderSign(project.isOrderSign());
      return template;
    }
    var tmp = this.profileFeignClient.getTemplateById(project.getTemplateId());
    if (tmp.isPresent()) {
      template = tmp.get();
      if (template.getApprovalProcess() == null) {
        template.setApprovalProcess(ScenarioStep.APPROVAL);
        template.setOrderApprove(project.isOrderApprove());
        template.setOrderSign(project.isOrderSign());
      }
      return template;
    }
    return null;
  }

  public void validateTemplate(Set<Signatory> signatories, Template template) {
    if (this.getSignatoriesByRole(signatories, RoleConstant.ROLE_APPROVAL).size()
        != template.getApproval()) {
      throw new InvalidProjectArgException(
          "Approval allowed only " + template.getApproval(), ProjectBadRequestConstant.TEMPLATE);
    } else if (this.getSignatoriesByRole(signatories, RoleConstant.ROLE_SIGNATORY).size()
        != template.getSignature()) {
      throw new InvalidProjectArgException(
          "Signatory allowed only " + template.getSignature(), ProjectBadRequestConstant.TEMPLATE);
    } else if (this.getSignatoriesByRole(signatories, RoleConstant.ROLE_VIEWER).size()
        != template.getViewer()) {
      throw new InvalidProjectArgException(
          "Viewer allowed only " + template.getViewer(), ProjectBadRequestConstant.TEMPLATE);
    } else if (this.getSignatoriesByRole(signatories, RoleConstant.ROLE_RECEIPT).size()
        != template.getRecipient()) {
      throw new InvalidProjectArgException(
          "Recipient allowed only " + template.getRecipient(), ProjectBadRequestConstant.TEMPLATE);
    }
  }

  public List<SignatoryDto> getSignatoriesByRole(Set<Signatory> signatories, String role) {
    if (signatories.isEmpty()) {
      return List.of();
    }
    return signatories.stream()
        .filter(
            signatory ->
                ParticipantRole.getByRole(role)
                    .equals(ParticipantRole.getByRole(signatory.getRole())))
        .map(signatory -> modelMapper.map(signatory, SignatoryDto.class))
        .toList();
  }

  public List<DocumentDetail> prepareDocumentDetail(Document doc) {
    return doc.getDetails().stream()
        .filter(
            dt ->
                DocumentDetailType.SIGNATORY.equalsIgnoreCase(dt.getType().split("-")[0])
                    || DocumentDetailType.PARAPH.equalsIgnoreCase(dt.getType().split("-")[0]))
        .map(
            dt -> {
              doc.getDetails().stream()
                  .filter(
                      nextDetail ->
                          DocumentDetailType.APPROVAL.equalsIgnoreCase(
                                  nextDetail.getType().split("-")[0])
                              && (Objects.equals(dt.getDocumentId(), nextDetail.getDocumentId())
                                  && Objects.equals(
                                      dt.getSignatoryId(), nextDetail.getSignatoryId())))
                  .findAny()
                  .ifPresent(value -> dt.setText(value.getText()));
              return this.modelMapper.map(dt, DocumentDetail.class);
            })
        .toList();
  }

  /**
   * check if the participant is end-user allowed only approve or signatory.
   *
   * @param role participant's role to check this role is allowed to this
   * @param email or not.
   * @throws InvalidProjectArgException if the end-user's role does not approve or signatory.
   */
  public void checkParticipantsRole(String role, String email) {
    if (!isActor(role) && (this.profileFeignClient.checkUserEmail(email))) {
      throw new InvalidProjectArgException(
          "The " + email + " is allowed only in signatory/approve role",
          ProjectBadRequestConstant.END_USER_AS_PARTICIPANT);
    }
  }

  private boolean isActor(String role) {
    return Objects.equals(role, RoleConstant.ROLE_SIGNATORY)
        || Objects.equals(role, RoleConstant.ROLE_APPROVAL);
  }
}
