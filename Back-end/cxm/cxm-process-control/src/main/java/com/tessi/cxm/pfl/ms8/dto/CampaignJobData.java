package com.tessi.cxm.pfl.ms8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CampaignJobData implements Serializable {
  private String flowId;
  private String composedFileId;
  private String createdBy;
  private String type;
  private String senderEmail;
  private String senderName;
  private Map<String, String> attachments; // <fileId,hash>
}
