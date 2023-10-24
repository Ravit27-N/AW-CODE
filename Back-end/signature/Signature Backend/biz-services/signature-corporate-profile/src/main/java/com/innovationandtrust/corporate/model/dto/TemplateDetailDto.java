package com.innovationandtrust.corporate.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TemplateDetailDto implements Serializable {
  private Long id;
  private Long templateId;
  private Long roleId;
  private String name;
}
