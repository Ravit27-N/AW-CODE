package com.innovationandtrust.corporate.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovationandtrust.corporate.model.entity.AbstractEntity;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FolderDto extends AbstractEntity implements Serializable {
  private Long id;
  private String unitName;
  private Long businessUnitId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BusinessUnitDtoListRes businessUnits;
}
