package com.innovationandtrust.process.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Class about support to signing multiple projects */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigningProcessDto {
  // flowId refers project flow control unique id
  private String flowId;

  // uuid refers to participant unique uuid
  private String uuid;

  // status refers to status
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String status;

  // message refers to message of process status
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String message;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Integer total;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Integer totalFailed;
}
