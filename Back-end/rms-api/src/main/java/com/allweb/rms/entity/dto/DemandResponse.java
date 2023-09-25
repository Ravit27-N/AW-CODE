package com.allweb.rms.entity.dto;

import com.allweb.rms.entity.jpa.Demand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class DemandResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private int id;

  @Schema(type = "string", name = "dateActivity", format = "date")
  private String nbCandidates;

  private String experienceLevel;
  private boolean isDeleted;
  private boolean active;
  private boolean status;
  private int nbRequired;

  @Schema(type = "string", name = "deadlineDemand", format = "date")
  private Date deadLine;

  @Schema(type = "string", name = "createdAtActivity", format = "date")
  private Date createdAt;

  @Schema(type = "string", name = "dateActivity", format = "date")
  private Date updatedAt;

  @Schema(type = "map", description = "get job description object")
  private transient Map<String, Object> jobDescription;


  @Schema(type = "map", description = "get project object")
  private transient Map<String, Object> project;

  public DemandResponse(
      Demand demand,
      int projectId,
      String projectName,
      int jobDescriptionId,
      String jobDescriptionName) {

    this.project = new HashMap<>();
    this.jobDescription = new HashMap<>();
    this.id = demand.getId();
    this.nbCandidates = demand.getNbCandidates();
    this.experienceLevel = demand.getExperienceLevel();
    this.isDeleted = demand.isDeleted();
    this.active = demand.isActive();
    this.status = demand.isStatus();
    this.nbRequired = demand.getNbRequired();
    this.deadLine = demand.getDeadLine();
    this.createdAt = demand.getCreatedAt();
    this.updatedAt = demand.getUpdatedAt();
    this.project.put("id", projectId);
    this.project.put("title", projectName);
    this.jobDescription.put("id", jobDescriptionId);
    this.jobDescription.put("title", jobDescriptionName);
  }
}
