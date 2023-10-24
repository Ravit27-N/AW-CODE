package com.innovationandtrust.share.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.enums.SignatureFormat;
import com.innovationandtrust.share.enums.SignatureLevel;
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
public class Template implements Serializable {
  private Long id;
  @NotEmpty private String name;
  @NotEmpty private ScenarioStep signProcess;
  @NotEmpty private ScenarioStep approvalProcess;
  @NotEmpty private SignatureLevel level;
  @NotEmpty private SignatureFormat format;

  @Builder.Default @NotEmpty private String notificationService = NotificationConstant.SMS;
  @Builder.Default private boolean orderApprove = false;
  @Builder.Default private boolean orderSign = false;

  private int approval;
  @NotEmpty private int signature;
  private int recipient;
  private int viewer;

  private long companyId;
  private int businessUnitId;
  private Long createdBy;

  public boolean isTagAllowBackup() {
    return this.signProcess.equals(ScenarioStep.COUNTER_SIGN)
        || this.signProcess.equals(ScenarioStep.COSIGN);
  }

  public Template(
      Long id,
      String name,
      ScenarioStep signProcess,
      ScenarioStep approvalProcess,
      SignatureLevel level,
      SignatureFormat format,
      int approval,
      int signature) {
    this.id = id;
    this.name = name;
    this.signProcess = signProcess;
    this.approvalProcess = approvalProcess;
    this.level = level;
    this.format = format;
    this.approval = approval;
    this.signature = signature;
  }
}
