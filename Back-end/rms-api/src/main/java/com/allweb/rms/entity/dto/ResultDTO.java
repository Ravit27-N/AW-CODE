package com.allweb.rms.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;

  @JsonIgnore
  @Schema(name = "interviewId", description = "interview id")
  private int interviewId;

  @NotEmpty
  @Schema(
      type = "String",
      description = "this column score have to request as json format",
      example = "{\"quiz\":{\"score\": 10,\"max\":10},\"coding\":{\"score\": 10,\"max\":10}}")
  private String score;

  private float average;
  private String english;
  private String logical;
  private String flexibility;
  private String oral;
  private String remark;

  @Schema(hidden = true)
  private Date createdAt;

  @Schema(hidden = true)
  private Date updatedAt;
}
