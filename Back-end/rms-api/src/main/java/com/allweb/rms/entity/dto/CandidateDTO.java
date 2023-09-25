package com.allweb.rms.entity.dto;

import com.allweb.rms.utils.CustomFormatDateDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private int id;

  @NotEmpty
  @Length(max = 45, min = 2)
  private String firstname;

  @NotEmpty
  @Length(max = 45, min = 2)
  private String lastname;

  @Schema(
      type = "string",
      allowableValues = {"Mr.", "Ms.", "Mrs.", "Dr.", "Prof.", "Rev."},
      maximum = "10")
  private String salutation;

  @Schema(
      type = "string",
      allowableValues = {"Male", "Female"},
      maximum = "10")
  private String gender;

  @Schema(
      type = "string",
      format = "date-time",
      pattern = "dd-MM-yyyy",
      example = "14-01-2021",
      required = true)
  @JsonSerialize(using = DateSerializer.class)
  @JsonDeserialize(using = CustomFormatDateDeserializer.class)
  private Date dateOfBirth;

  @Schema(type = "String", name = "email", maximum = "128", required = true)
  private String email;

  @NotEmpty
  @Length(max = 20, min = 8)
  private String telephone;

  @Schema(hidden = true)
  private String fullName;

  private String photoUrl;
  private float gpa;
  private String yearOfExperience;
  private String priority;

  @Schema(hidden = true)
  private boolean isDeleted;

  private String description;
  private boolean active;
  private int statusId;

  @Schema(hidden = true)
  private Long countInterview;

  @Schema(hidden = true)
  private Long countReminder;
  // used for storing part of the file cv
  private List<String> filenames;

  @Schema(hidden = true, description = "this field use when return result in list")
  private transient JsonNode candidateStatus;

  @Schema(hidden = true, description = "this field use when return result in list")
  private transient JsonNode interviews;

  @Schema(hidden = true, description = "this field use when return result in list")
  private transient JsonNode activities;

  @Schema(
      description = "this field is used when response candidate as list or json",
      example = "[\n" + "    {\n" + "      \"id\": 0\n" + "    }\n" + "  ]")
  private transient JsonNode universities;

  @Schema(hidden = true)
  private Date createdAt;

  @Schema(hidden = true)
  @UpdateTimestamp
  private Date updatedAt;

  @Schema(hidden = true)
  @Column(name = "created_by")
  @CreationTimestamp
  private String createdBy;

  public String getFullName() {
    return this.salutation + " " + this.firstname + " " + this.lastname;
  }

  public JsonNode getActivities() {
    if (this.activities == null) {
      return JsonNodeFactory.instance.arrayNode();
    }
    return activities;
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

  public List<String> getFilenames() {
    if (this.filenames == null) return new ArrayList<>();
    return filenames;
  }
}
