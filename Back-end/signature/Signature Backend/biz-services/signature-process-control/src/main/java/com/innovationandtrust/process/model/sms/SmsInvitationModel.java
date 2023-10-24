package com.innovationandtrust.process.model.sms;

import com.innovationandtrust.process.utils.DateUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.thymeleaf.context.Context;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsInvitationModel implements Serializable {

  private String firstName;
  private String projectName;
  private String invitationMessage;
  private Date expireDate;
  private String linkUrl;

  protected Context getParamsContext() {
    var context = new Context(Locale.FRENCH);
    context.setVariable("firstName", this.firstName);
    context.setVariable("newProjectName", this.projectName.trim());
    context.setVariable("invitationMessage", this.invitationMessage);
    context.setVariable("expireDate", DateUtil.toFrenchDate(this.expireDate));
    context.setVariable("dateOffset", DateUtil.getOffsetOfDate(this.expireDate));
    context.setVariable("linkUrl", this.linkUrl);
    return context;
  }
}
