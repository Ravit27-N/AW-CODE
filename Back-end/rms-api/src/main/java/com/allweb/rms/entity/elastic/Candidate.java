package com.allweb.rms.entity.elastic;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class Candidate {
  @Field(type = FieldType.Integer)
  private Integer id;

  @Field(name = "full_name", type = FieldType.Text)
  private String fullName;
}
