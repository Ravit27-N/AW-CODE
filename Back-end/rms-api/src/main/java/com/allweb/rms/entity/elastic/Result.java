package com.allweb.rms.entity.elastic;

import java.io.Serializable;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.lang.Nullable;

@Data
public class Result implements Serializable {
  @Field(type = FieldType.Integer)
  private int id;

  @Nullable
  @Field(name = "score", type = FieldType.Object)
  private ScoreDetail score;

  @Field(type = FieldType.Float)
  private float average;

  @Field(type = FieldType.Text)
  private String english;

  @Field(type = FieldType.Text)
  private String logical;

  @Field(type = FieldType.Text)
  private String flexibility;

  @Field(type = FieldType.Text)
  private String oral;
}
