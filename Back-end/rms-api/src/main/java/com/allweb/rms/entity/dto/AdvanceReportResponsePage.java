package com.allweb.rms.entity.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class AdvanceReportResponsePage implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;

  private String fullName;

  @Schema(
      type = "string",
      allowableValues = {"Male", "Female"},
      maximum = "10")
  private String gender;

  private int age;

  private transient JsonNode universities;

  private float gpa;

  private String yearOfExperience;

  @Schema(hidden = true, description = "this field use when return result in list")
  private transient JsonNode interviews;

  @Schema(hidden = true, description = "this field use when return result in list")
  private transient JsonNode candidateStatus;

  AdvanceReportResponsePage(int id) {
    this.id = id;
  }

  public JsonNode getCandidateStatus() {
    if (this.candidateStatus == null) {
      return JsonNodeFactory.instance.objectNode();
    }
    return candidateStatus;
  }

  public JsonNode getInterviews() {
    if (this.interviews == null) {
      return JsonNodeFactory.instance.arrayNode();
    }
    return interviews;
  }

  public JsonNode getUniversities() {
    if (this.universities == null) {
      return JsonNodeFactory.instance.arrayNode();
    }
    return universities;
  }
}
