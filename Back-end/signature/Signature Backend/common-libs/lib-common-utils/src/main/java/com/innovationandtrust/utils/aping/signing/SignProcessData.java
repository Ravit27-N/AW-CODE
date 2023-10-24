package com.innovationandtrust.utils.aping.signing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignProcessData {
  @NotNull(message = "ttl is required")
  @Range(min = 60)
  private int ttl;

  @NotNull(message = "templateId is required")
  @Range(min = 1)
  private long templateId;

  @NotEmpty private List<@Valid String> documents;
  @NotEmpty private List<@Valid SignStepData> steps;
  private int format;
  private int level;

  public SignProcessData(
      int ttl, long templateId, List<String> documents, List<@Valid SignStepData> steps) {
    this.ttl = ttl;
    this.templateId = templateId;
    this.documents = documents;
    this.steps = steps;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void setFormat(int format) {
    this.format = format;
  }
}
