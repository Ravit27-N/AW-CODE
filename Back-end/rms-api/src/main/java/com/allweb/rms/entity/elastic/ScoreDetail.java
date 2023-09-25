package com.allweb.rms.entity.elastic;

import java.io.Serializable;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class ScoreDetail implements Serializable {
  @Field(type = FieldType.Object)
  private ScoreItem quiz;

  @Field(type = FieldType.Object)
  private ScoreItem coding;
}
