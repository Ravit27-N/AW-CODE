package com.innovationandtrust.utils.encryption;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenParam {

  @NotEmpty private String companyUuid;

  // Project flow id
  @NotEmpty private String flowId;

  // Participant uuid
  @NotEmpty private String uuid;

  @NotEmpty private String token;
}
