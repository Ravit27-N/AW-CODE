package com.allweb.rms.entity.elastic;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@Document(indexName = CandidateReportElasticsearchDocument.INDEX_NAME, createIndex = false)
public class CandidateReportElasticsearchDocument implements Serializable {
  public static final String INDEX_NAME = "idx_candidate_report";

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

  @Field(type = FieldType.Text)
  private String description;

  @Field(type = FieldType.Text)
  private String priority;

  @Field(name = "universities", type = FieldType.Object)
  private List<University> universities;

  @Nullable
  @Field(name = "interviews", type = FieldType.Object)
  private Interview interview;

  @Field(name = "created_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date createdAt;

  @Field(name = "updated_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date updatedAt;

  @Field(name = "active", type = FieldType.Boolean)
  private boolean isActive;

  @Data
  public static class Interview implements Serializable {
    @Field(type = FieldType.Integer)
    private int id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(
        name = "date_time",
        type = FieldType.Date,
        pattern = "yyyy-MM-dd'T'HH:mm:ss",
        storeNullValue = true)
    private Date dateTime;

    @Field(name = "result", type = FieldType.Object)
    private Result result;
  }
}
