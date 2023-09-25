package com.tessi.cxm.pfl.ms15.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentInstructions implements Serializable {
  private String message;
  private int status;
  private Timestamp timestamp;
  private DocumentInstructionData data;

  @Builder
  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class DocumentInstructionData {

    @JsonProperty(value = "IdBreakingPage")
    private String idBreakingPage;

    @JsonProperty(value = "BreakingPage")
    private String breakingPage;

    @JsonProperty(value = "IdRecipientId")
    private String idRecipientId;

    @JsonProperty(value = "RecipientId")
    private String recipientId;

    @JsonProperty(value = "IdEmailRecipient")
    private String idEmailRecipient;

    @JsonProperty(value = "EmailRecipient")
    private String emailRecipient;

    @JsonProperty(value = "IdEmailObject")
    private String idEmailObject;

    @JsonProperty(value = "EmailObject")
    private String emailObject;

    @JsonProperty(value = "Address")
    private String address;

    @JsonProperty(value = "Pjs")
    private String pjs;

    @JsonProperty(value = "Data")
    private String data;
  }
}
