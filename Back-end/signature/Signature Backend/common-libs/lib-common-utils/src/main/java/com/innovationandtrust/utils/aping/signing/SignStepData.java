package com.innovationandtrust.utils.aping.signing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignStepData {
  @NotNull private String process;
  @NotNull private String cardinality;
  private int signatureType;
  @NotEmpty private List<@Valid Actor> actors;
}
