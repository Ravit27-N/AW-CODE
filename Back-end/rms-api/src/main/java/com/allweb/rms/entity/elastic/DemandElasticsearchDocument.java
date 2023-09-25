package com.allweb.rms.entity.elastic;

import com.allweb.rms.entity.jpa.AbstractEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = DemandElasticsearchDocument.INDEX_NAME,createIndex = false)
public class DemandElasticsearchDocument implements Serializable {
  public static final String INDEX_NAME = "idx_demand";

  @Id
  @Field(type = FieldType.Integer)
  private int id;

  // Reference with Project(M-1)
  @Field(type = FieldType.Object)
  private Project project;

  @Field(type = FieldType.Object)
  private JobDescription jobDescription;

  @Field(name = "nb_required", type = FieldType.Integer)
  private int nbRequired; // required user. input from user

  @Field(name = "experience_Level", type = FieldType.Text)
  private String experienceLevel; // input from user

  @Field(name = "dead_line", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date deadLine; // input from user -> deadLine

  @Field(name = "created_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date createdAt;

  @Field(name = "update_at", type = FieldType.Date, format = DateFormat.epoch_millis)
  private Date updatedAt;

  @Field(name = "created_by", type = FieldType.Text)
  private String createdBy;

  // Reference with Candidate(M-1) when status-> success
  @Field(name = "nbCandidates", type = FieldType.Text)
  private String nbCandidates; // use for count

  @Field(name = "status", type = FieldType.Boolean)
  private boolean status; // In Progress & Completed

  @Field(name = "is_deleted", type = FieldType.Boolean)
  private boolean isDeleted;

  @Field(name = "active", type = FieldType.Boolean)
  private boolean active;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof com.allweb.rms.entity.jpa.Demand demand)) return false;
    return Objects.equals(id, demand.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Data
  public static class Project {

    @Field(type = FieldType.Integer)
    private int id;

    @Field(name = "name", type = FieldType.Text)
    private String name;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(name = "active", type = FieldType.Boolean)
    private boolean active;

    @Field(name = "is_deleted", type = FieldType.Boolean)
    private boolean isDeleted;
  }

  @Data
  public static class JobDescription {

    @Field(type = FieldType.Integer)
    private int id;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String filename;

    @Field(type = FieldType.Boolean)
    private boolean active;
  }
}
