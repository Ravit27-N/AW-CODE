package com.allweb.rms.entity.elastic;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class University {
  @Field(type = FieldType.Integer)
  private int id;

  @Field(type = FieldType.Text)
  private String name;
}
