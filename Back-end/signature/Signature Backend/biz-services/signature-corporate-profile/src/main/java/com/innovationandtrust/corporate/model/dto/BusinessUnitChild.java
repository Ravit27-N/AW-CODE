package com.innovationandtrust.corporate.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BusinessUnitChild implements Serializable {
  private Long id;
  private String unitName;
  private Integer sortOrder;
  private Long companyDetailId;
  private Long parentId;
}
