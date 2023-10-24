package com.innovationandtrust.share.model.sftp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.model.project.InvitationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@Slf4j
@JacksonXmlRootElement(localName = "project")
public class ProjectModel {
  @NotEmpty private String name;

  @JsonView(ProjectModelView.Json.class)
  private String flowId;

  private Long templateId = null;

  private boolean orderSign = true;

  private boolean orderApprove = true;

  private boolean autoReminder = false;

  @Min(1)
  private Integer channelReminder;

  private Integer reminderOption;

  @NonNull
  @JsonFormat(shape = Shape.STRING, pattern = "yyyyMMdd HH:mm:ss")
  private Date expireDate;

  @JacksonXmlElementWrapper(localName = "participants")
  @JacksonXmlProperty(localName = "participant")
  @JsonProperty(value = "signatories")
  @NotEmpty
  private @Valid List<ProjectParticipantModel> participants;

  @NotEmpty
  @JacksonXmlElementWrapper(localName = "documents")
  @JacksonXmlProperty(localName = "document")
  @JsonProperty(value = "documents")
  private @Valid List<ProjectDocumentModel> documents;

  @JacksonXmlElementWrapper(localName = "details")
  @JacksonXmlProperty(localName = "detail")
  @JsonProperty("details")
  private @Valid List<InvitationMessage> details;

  @JsonView(ProjectModelView.Json.class)
  private ProjectAuthorModel author;

  /**
   * Set only a valid date.
   *
   * @param expireDate is from xml file.
   */
  public void setExpireDate(Date expireDate) {
    if (new Date().after(expireDate)) {
      throw new IllegalArgumentException("expireDate must be greater than now");
    }
    this.expireDate = expireDate;
  }

  /**
   * Check does these participants has any signatory.
   *
   * @return true if the participants contains signatory.
   */
  @JsonIgnore
  public boolean hasSignatory() {
    if (this.getParticipants().isEmpty()) {
      return false;
    }
    return this.getParticipants().stream()
        .anyMatch(s -> Objects.equals(s.getRole(), ParticipantRole.SIGNATORY.getRole()));
  }

  @JsonIgnore
  public boolean isValidDocuments(List<String> files) {
    var docs = this.documents.stream().map(ProjectDocumentModel::getFileName).toList();
    boolean isValidDocument =
        Objects.equals(files.size(), documents.size())
            && new HashSet<>(docs).containsAll(files.stream().map(FilenameUtils::getName).toList());
    if (isValidDocument) {
      this.documents.forEach(
          doc ->
              files.forEach(
                  file -> {
                    if (doc.getFileName().equals(FilenameUtils.getName(file))) {
                      doc.setFullPath(Path.of(file).toString());
                    }
                  }));
    }
    return isValidDocument;
  }

  @JsonIgnore
  public boolean isValidParticipantsByRole(String role, int participantsNo) {
    var projectParticipants =
        this.participants.stream()
            .filter(participant -> Objects.equals(participant.getRole(), role))
            .count();
    return projectParticipants == participantsNo;
  }
}
