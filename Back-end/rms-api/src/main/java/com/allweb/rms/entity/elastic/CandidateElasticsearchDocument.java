package com.allweb.rms.entity.elastic;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = CandidateElasticsearchDocument.INDEX_NAME, createIndex = false)
public class CandidateElasticsearchDocument {
  public static final String INDEX_NAME = "idx_candidate";

  @Id
  @Field(type = FieldType.Integer)
  private int id;

  @MultiField(
      mainField = @Field(name = "first_name", type = FieldType.Text, normalizer = "lowercase"))
  private String firstName;

  @Field(name = "last_name", type = FieldType.Text, normalizer = "lowercase")
  private String lastName;

  @Field(name = "salutation", type = FieldType.Text)
  private String salutation;

  @Field(type = FieldType.Text)
  private String gender;

  @Field(type = FieldType.Float)
  private float gpa;

  @Field(name = "date_of_birth", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date dateOfBirth;

  @Field(name = "year_of_experience", type = FieldType.Text, normalizer = "lowercase")
  private String yearOfExperience;

  @Field(name = "photo_url", type = FieldType.Text)
  private String photoURL;

  @Field(type = FieldType.Text)
  private String telephone;

  @Field(type = FieldType.Text)
  private String email;

  @Field(name = "status_id", type = FieldType.Text)
  private String statusId;

  @Field(type = FieldType.Text)
  private String description;

  @Field(type = FieldType.Text)
  private String priority;

  @Field(name = "candidate_status", type = FieldType.Object)
  private CandidateStatus candidateStatus;

  @Field(name = "universities", type = FieldType.Object)
  private List<University> universities;

  @Field(name = "interviews", type = FieldType.Object)
  private Interview interviews;

  @Field(name = "interview_count", type = FieldType.Integer)
  private int interviewCount;

  @Field(name = "reminder_count", type = FieldType.Integer)
  private int reminderCount;

  @Field(name = "created_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date createdAt;

  @Field(name = "updated_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date updatedAt;

  @Field(name = "is_deleted", type = FieldType.Boolean)
  private boolean isDeleted;

  @Field(name = "active", type = FieldType.Boolean)
  private boolean isActive;

  @Data
  public static class Interview {
    @Field(type = FieldType.Integer)
    private int id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(name = "last_interview", type = FieldType.Date, format = DateFormat.epoch_second)
    private Date lastInterview;

    @Field(name = "result", type = FieldType.Object)
    private Result result;
  }

  @Getter
  @Setter
  public static class CandidateStatus {
    @Field(type = FieldType.Integer)
    private int id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Boolean)
    private boolean active;
  }
}
