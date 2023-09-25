package com.allweb.rms.entity.elastic;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = InterviewElasticsearchDocument.INDEX_NAME, createIndex = false)
public class InterviewElasticsearchDocument {
  public static final String INDEX_NAME = "idx_interview";

  @Id
  @Field(type = FieldType.Integer)
  private int id;

  @Field(type = FieldType.Text, name = "title")
  private String title;

  @Field(type = FieldType.Text, name = "description")
  private String description;

  @Field(type = FieldType.Text, name = "status")
  private String status;

  @Field(name = "reminder_count", type = FieldType.Integer)
  private int reminderCount;

  @Field(name = "date_time", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date dateTime;

  @Field(name = "created_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date createdAt;

  @Field(name = "updated_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date updatedAt;

  @Field(type = FieldType.Object)
  private Candidate candidate;

  @Field(name = "has_result", type = FieldType.Boolean)
  private boolean hasResult;

  @Field(name = "is_deleted", type = FieldType.Boolean)
  private boolean isDeleted;
}
