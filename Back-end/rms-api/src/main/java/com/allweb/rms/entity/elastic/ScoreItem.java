package com.allweb.rms.entity.elastic;

import java.io.Serializable;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class ScoreItem implements Serializable {
  @Field(type = FieldType.Integer)
  private double score;

  @Field(type = FieldType.Integer)
  private double max;
}
