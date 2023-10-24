package com.innovationandtrust.share.model.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.tdc.TdcDocument;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Handling information of a project for processing the documents' validation.
 *
 * @author Vichet CHANN
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable {
  @NotNull private Long id;
  private String flowId;

  @NotNull(message = "The project name is required and cannot be null!")
  @NotEmpty(message = "The project name is required and cannot be empty!")
  private String name;

  private String status;

  @Builder.Default private ProjectDetail detail = new ProjectDetail();

  private Template template;

  private Long createdBy;

  private Date createdAt;

  private String userKeycloakId;

  private Long assignedTo;

  private String signatureLevel;

  @Builder.Default private String paraph = "";

  private CorporateInfo corporateInfo;

  private CompanySettingDto setting;

  @NotEmpty @Builder.Default private List<Document> documents = new ArrayList<>();

  @NotEmpty private List<Participant> participants;

  private boolean autoReminder;

  private Integer reminderOption;

  private Integer reminderChannel;

  private TdcDocument manifestData;

  public Project(String flowId) {
    this.flowId = flowId;
  }

  public void setDocuments(List<Document> documents) {
    if (documents.isEmpty()) {
      throw new IllegalArgumentException("The docuemtn is required and cannot be empty!");
    }
    this.documents = documents;
  }

  public void setParticipants(List<Participant> participants) {
    if (participants.isEmpty()) {
      throw new IllegalArgumentException("The participant is required and cannot be empty!");
    }
    this.participants = participants;
  }

  public void setTemplate(Template template) {
    if (Objects.isNull(template)) {
      throw new IllegalArgumentException("Template is required and cannot be null!");
    }
    this.template = template;
  }

  public void setAutoReminder(boolean autoReminder) {
    this.autoReminder = autoReminder;
  }

  @JsonIgnore
  public Long getSessionId() {
    return Objects.requireNonNull(this.detail).getSessionId();
  }

  @JsonIgnore
  public boolean isAutoRemind() {
    return this.autoReminder;
  }

  public void setReminderOption(Integer reminderOption) {
    if (this.autoReminder && Objects.isNull(reminderOption)) {
      throw new IllegalArgumentException("Invalid reminder option field!");
    }
    this.reminderOption = reminderOption;
  }

  /**
   * To retrieve the next participant for signing or approving the document.
   *
   * @return the object of {@link Participant} if present
   */
  @JsonIgnore
  public Optional<Participant> getParticipantToInviteWithOrder() {
    if (this.getParticipants().isEmpty()) {
      return Optional.empty();
    }
    var approval =
        this.getActors(RoleConstant.ROLE_APPROVAL).stream().filter(p -> !p.isInvited()).toList();
    if (!approval.isEmpty()) {
      return this.findNextToInvite(approval);
    }
    return this.findNextToInvite(this.getActors(RoleConstant.ROLE_SIGNATORY));
  }

  @JsonIgnore
  private Optional<Participant> findNextToInvite(List<Participant> participants) {
    return participants.stream()
        .filter(p -> !p.isInvited())
        .min(Comparator.comparingInt(Participant::getOrder))
        .stream()
        .findAny();
  }

  /**
   * To retrieve the information of the {@link Participant} by the uuid.
   *
   * @param uuid refers to the identity of {@link Participant}
   * @see Participant#getUuid()
   */
  @JsonIgnore
  public Optional<Participant> getParticipantByUuid(String uuid) {
    if (this.getParticipants().isEmpty()) {
      return Optional.empty();
    }
    return this.participants.stream().filter(p -> Objects.equals(p.getUuid(), uuid)).findAny();
  }

  @JsonIgnore
  public List<Participant> getParticipantsToInviteBy(String role) {
    if (this.getParticipants().isEmpty()) {
      return List.of();
    }

    return participants.stream()
        .filter(
            p ->
                ParticipantRole.getByRole(role).equals(ParticipantRole.getByRole(p.getRole()))
                    && !p.isInvited())
        .toList();
  }

  @JsonIgnore
  public List<Participant> getParticipantsByRole(String role) {
    if (this.getParticipants().isEmpty()) {
      return List.of();
    }

    return participants.stream()
        .filter(p -> ParticipantRole.getByRole(role).equals(ParticipantRole.getByRole(p.getRole())))
        .toList();
  }

  @JsonIgnore
  public List<Participant> getParticipantsByRoleAndIsInvited(String role, Boolean isInvited) {
    if (this.getParticipants().isEmpty()) {
      return List.of();
    }

    return this.getParticipantsByRole(role).stream()
        .filter(participant -> participant.isInvited() == isInvited)
        .toList();
  }

  @JsonIgnore
  public boolean canSendReminder(Participant participant) {
    if (Objects.requireNonNull(ParticipantRole.getByRole(participant.getRole()))
        .equals(ParticipantRole.SIGNATORY)) {
      return this.isReadyToSign()
          && (participant.isInvited() && participant.isSigner() && !participant.isSigned());
    }
    return participant.isInvited() && participant.isApprover() && !participant.isApproved();
  }

  @JsonIgnore
  public InvitationMessage getMessageByRole(String role) {
    return detail.getInvitationMessages().stream()
        .filter(
            message ->
                ParticipantRole.getByRole(role)
                    .equals(ParticipantRole.getByRole(message.getType())))
        .findAny()
        .orElse(new InvitationMessage());
  }

  /**
   * To retrieve all document url of API NG from the project.
   *
   * @see Document#getDocUrl()
   * @return collection of url as {@link String}
   */
  @JsonIgnore
  public List<String> getDocumentUrls() {
    if (this.documents.isEmpty()) {
      return List.of();
    }
    return this.getDocuments().stream().map(Document::getDocUrl).toList();
  }

  /**
   * To retrieve document by uuid.
   *
   * @param docId refers to uuid of {@link Document}
   * @see Document#getUuid()
   * @return the object of {@link Document}
   */
  @JsonIgnore
  public Document getDocumentByUuid(String docId) {
    if (this.documents.isEmpty()) {
      return null;
    }
    return this.documents.stream()
        .filter(doc -> Objects.equals(doc.getUuid(), docId))
        .findAny()
        .orElse(null);
  }

  /**
   * To very the next signer of the project.
   *
   * @return true if the project has the next signer nor false
   */
  @JsonIgnore
  public boolean hasNextParticipant() {
    if (this.getParticipants().isEmpty()) {
      return false;
    }
    return this.getParticipants().stream()
        .anyMatch(
            p ->
                p.isInvited()
                    && ((p.isSigner() && !p.isSigned()) || (p.isApprover() && !p.isApproved()))
                    && !p.isRefused());
  }

  /**
   * check have any refuse in the project.
   *
   * @return true if any participants refuse the document.
   */
  @JsonIgnore
  public boolean hasRefused() {
    if (this.getParticipants().isEmpty()) {
      return false;
    }
    return this.getParticipants().stream().anyMatch(p -> p.isInvited() && p.isRefused());
  }

  /**
   * check project is finished, also by complete or refuse.
   *
   * @return true project finished.
   */
  @JsonIgnore
  public boolean isFinished() {
    // cosing tag is required all participants action, project will be finished
    if (!this.hasNextParticipant() && !this.hasNextRecipient()) {
      return true;
    } else {
      // not action of participants, but refusing on counter-sign, project will be finished
      return this.hasRefused()
          && Objects.equals(getTemplate().getSignProcess(), ScenarioStep.COUNTER_SIGN);
    }
  }

  /**
   * To very the next recipient of the project.
   *
   * @return true if the project has the next recipient nor false
   */
  @JsonIgnore
  public boolean hasNextRecipient() {
    if (this.getParticipants().isEmpty()) {
      return false;
    }
    return this.getParticipants().stream()
        .anyMatch(p -> p.isInvited() && (p.isReceiver() && !p.isReceived()));
  }

  @JsonIgnore
  private List<Participant> getActors(String role) {
    if (this.participants.isEmpty()) {
      return List.of();
    }
    return this.participants.stream().filter(p -> Objects.equals(p.getRole(), role)).toList();
  }

  /**
   * To retrieve the actors base on the role.
   *
   * @param role refers the role in {@link ParticipantRole}
   * @return collection of API-NG actor url
   */
  @JsonIgnore
  public List<String> getActorUrls(String role) {
    return this.getActors(role).stream()
        .sorted(Comparator.comparingInt(Participant::getOrder))
        .map(Participant::getActorUrl)
        .toList();
  }

  @JsonIgnore
  public boolean isReadyToSign() {
    if (this.participants.isEmpty()) {
      return false;
    }

    var totalApproval =
        this.participants.stream()
            .filter(
                p ->
                    Objects.equals(
                        ParticipantRole.getByRole(p.getRole()), ParticipantRole.APPROVAL))
            .toList();
    if (totalApproval.isEmpty()) {
      return true;
    }
    return totalApproval.stream().filter(Participant::isApproved).count() == totalApproval.size();
  }

  @JsonIgnore
  public boolean isOneSigned() {
    return participants.stream().anyMatch(Participant::isSigned);
  }

  public Long countSigned() {
    return participants.stream().filter(Participant::isSigned).count();
  }

  @JsonIgnore
  public List<Participant> getReminderParticipants(ParticipantRole role) {
    switch (role) {
      case APPROVAL -> {
        return this.participants.stream()
            .filter(p -> p.isInvited() && p.isApprover() && !p.isApproved())
            .toList();
      }
      case SIGNATORY -> {
        return this.participants.stream()
            .filter(p -> p.isInvited() && p.isSigner() && !p.isSigned())
            .toList();
      }
      default -> {
        return this.participants.stream()
            .filter(p -> p.isInvited() && !p.isSigned() && !p.isApproved())
            .toList();
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Project project)) return false;
    return Objects.equals(getFlowId(), project.getFlowId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFlowId());
  }
}
