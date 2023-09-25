package com.allweb.rms.entity.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DemandDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;

  private int projectId;

  @Schema(hidden = true, description = "this field use when return project in list")
  private transient JsonNode project;

  private int jobDescriptionId; // get from table-jobDescription

  @Schema(hidden = true, description = "")
  private transient JsonNode jobDescription;

  private int nbRequired; // required user. input from user

  @NotEmpty private String experienceLevel; // input from user

  @Column(nullable = false)
  private Date deadLine; // input from user -> deadLine

  // Reference with Candidate(M-1) when status-> success
  private String nbCandidates;

  private int candidateId;

  @Schema(hidden = true, description = "this field use when return candidate in list")
  private transient JsonNode candidate;

  @Column(nullable = false)
  private boolean status; // In Progress & Completed

  private boolean isDeleted;
  private boolean active;

  @Schema(hidden = true)
  private Date createdAt;

  @Schema(hidden = true)
  private Date updatedAt;

  @Column(name = "created_by")
  private String createdBy;

  public JsonNode getProject() {
    if (this.project == null) {
      return JsonNodeFactory.instance.objectNode();
    }
    return project;
  }

  public JsonNode getCandidate() {
    if (this.candidate == null) {
      return JsonNodeFactory.instance.objectNode();
    }
    return candidate;
  }
}
