package com.innovationandtrust.signature.identityverification.model.dto.shareid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request class for onboarding demand dto. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnBoardingDemandDto {

  private int demandId;

  private String id;
  private String result;
  private String reason;

  @JsonProperty("reason_id")
  private String reasonId;

  private MetaDataDto metadata;

  private DocumentDto document;

  @JsonProperty("document_front")
  private String documentFront;

  @JsonProperty("document_back")
  private String documentBack;
}
