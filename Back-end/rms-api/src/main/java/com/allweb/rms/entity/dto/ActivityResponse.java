package com.allweb.rms.entity.dto;

import com.allweb.rms.controller.ActivityController;
import com.allweb.rms.entity.jpa.Activity;
import com.allweb.rms.entity.jpa.CandidateStatus;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ToString()
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ActivityResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private int id;
  private String title;
  private String description;

  @Schema(type = "string", name = "dateActivity", format = "date")
  private Date createdAt;

  @Schema(type = "string", name = "dateActivity", format = "date")
  private Date updatedAt;

  @Schema(type = "map", description = "get candidate object")
  private transient Map<String, Object> candidate;

  private String author;

  public ActivityResponse(
      Activity activity, int candidateId, String candidateName, CandidateStatus candidateStatus) {
    this.candidate = new HashMap<>();
    this.id = activity.getId();
    this.title = activity.getTitle();
    this.description = activity.getDescription();
    this.createdAt = activity.getCreatedAt();
    this.author = activity.getUserId();
    this.candidate.put("id", candidateId);
    this.candidate.put("fullname", candidateName);
    this.candidate.put("gender", activity.getCandidate().getGender());
    this.candidate.put("photoUrl", activity.getCandidate().getPhotoUrl());
    this.candidate.put("status", candidateStatus);
    this.updatedAt = activity.getUpdatedAt();
  }

  public Map<String, Object> getLinks() {
    Map<String, Object> map = new HashMap<>();
    map.put(
        "getActivity",
        linkTo(methodOn(ActivityController.class).getActivityById(this.id))
            .withRel("Get Activity"));
    map.put(
        "update",
        linkTo(methodOn(ActivityController.class).updateActivity(new ActivityRequest(), this.id))
            .withRel("Update Activity"));
    map.put(
        "delete",
        linkTo(methodOn(ActivityController.class).deleteActivity(this.id))
            .withRel("Delete Activity"));
    return map;
  }
}
