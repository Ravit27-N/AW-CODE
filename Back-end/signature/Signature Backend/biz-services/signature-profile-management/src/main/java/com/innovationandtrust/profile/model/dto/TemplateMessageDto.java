package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovationandtrust.share.enums.NotificationReminderOption;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMessageDto implements Serializable {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long templateId;

  @NotEmpty private String titleInvitation;

  @NotEmpty private String messageInvitation;

  @NotNull private Integer expiration;

  @NotEmpty private int sendReminder;

  public void setSendReminder(int sendReminder) {
    if (Objects.isNull(NotificationReminderOption.getByOption(sendReminder))) {
      throw new InvalidRequestException("There is no reminder option " + sendReminder);
    }
    this.sendReminder = sendReminder;
  }

  public void setExpiration(Integer expirationDay) {
    if (expirationDay > 10) {
      throw new InvalidRequestException(
          "Expiration days cannot greater then 10. (API-NG constant)");
    }

    this.expiration = expirationDay;
  }
}
