package com.allweb.rms.entity.elastic;

import java.util.Date;

import com.allweb.rms.entity.jpa.AbstractEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = ReminderElasticsearchDocument.INDEX_NAME, createIndex = false)
public class ReminderElasticsearchDocument extends AbstractEntity {
  public static final String INDEX_NAME = "idx_reminder";

  @Id
  @Field(type = FieldType.Integer)
  private int id;

  @Field(name = "reminder_type", type = FieldType.Object)
  private String reminderType;

  @Field(type = FieldType.Object)
  private Candidate candidate;

  @Field(type = FieldType.Object)
  private Interview interview;

  @Field(type = FieldType.Text)
  private String title;

  @Field(type = FieldType.Text)
  private String description;

  @Field(name = "date_reminder", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date dateReminder;

  @Field(name = "created_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date createdAt;

  @Field(type = FieldType.Boolean)
  private boolean active;

  @Field(type = FieldType.Boolean)
  private boolean deleted;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Interview {
    @Field(type = FieldType.Integer)
    private Integer id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(name = "c_id", type = FieldType.Integer)
    private Integer cid;

    @Field(type = FieldType.Text)
    private String fullName;
  }
  @PrePersist
  public void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  public void onUpdate() {
    setUpdatedAt(new Date());
  }
}
