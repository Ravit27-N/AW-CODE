package com.innovationandtrust.share.model.project;

import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.InvitationStatus;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SignatoryRequest implements Serializable {
  @Min(1)
  private Long id;

  private InvitationStatus invitationStatus;
  private DocumentStatus documentStatus;
  private String comment;
  private String uuid;

  public SignatoryRequest(Long id, InvitationStatus invitationStatus) {
    this.id = id;
    this.invitationStatus = invitationStatus;
  }

  public SignatoryRequest(Long id, InvitationStatus invitationStatus, String uuid) {
    this.id = id;
    this.invitationStatus = invitationStatus;
    this.uuid = uuid;
  }

  public SignatoryRequest(Long id, DocumentStatus documentStatus) {
    this.id = id;
    this.documentStatus = documentStatus;
  }

  public SignatoryRequest(Long id, DocumentStatus documentStatus, String comment){
    this.id = id;
    this.documentStatus = documentStatus;
    this.comment = comment;
  }
}
