package com.allweb.rms.entity.dto;

import com.allweb.rms.entity.jpa.Interview;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString(exclude = "candidate")
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {
  private final Map<String, Object> candidate = new HashMap<>();
  private final Map<String, Object> links = new HashMap<>();
  private int id;
  private String title;
  private String description;
  private String status;
  private Long reminderCount;

  @Schema(type = "string", name = "dateTime", format = "date")
  private Date dateTime;

  @Schema(type = "string", name = "createdAt", format = "date")
  private Date createdAt;

  @Schema(type = "string", name = "updatedAt", format = "date")
  private Date updatedAt;

  @Schema(type = "string", name = "createdBy", format = "string")
  private String createdBy;

  private boolean hasResult;

  public InterviewResponse(
      Interview interview,
      String status,
      int candidateId,
      String candidateName,
      String photoUrl,
      long reminderCount,
      Integer resultId) {
    this.id = interview.getId();
    this.title = interview.getTitle();
    this.description = interview.getDescription();
    this.status = status;
    this.reminderCount = reminderCount;
    this.dateTime = interview.getDateTime();
    this.createdAt = interview.getCreatedAt();
    this.updatedAt = interview.getUpdatedAt();
    this.createdBy = interview.getCreatedBy();
    this.candidate.put("id", candidateId);
    this.candidate.put("fullName", candidateName);
    this.candidate.put("photoUrl", photoUrl);
    this.hasResult = resultId != null;
  }

  public void setCandidateId(int id) {
    this.candidate.put("id", id);
  }

  public void setCandidateFullName(String candidateFullName) {
    this.candidate.put("fullName", candidateFullName);
  }
}
