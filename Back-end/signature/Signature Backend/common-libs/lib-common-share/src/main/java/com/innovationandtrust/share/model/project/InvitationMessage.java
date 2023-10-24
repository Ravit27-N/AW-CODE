package com.innovationandtrust.share.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.innovationandtrust.share.constant.ParticipantRole;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvitationMessage implements Serializable {

  @NotEmpty private String invitationSubject;

  @NotEmpty private String invitationMessage;

  @NotEmpty @Builder.Default private String type = ParticipantRole.SIGNATORY.getRole();
}
