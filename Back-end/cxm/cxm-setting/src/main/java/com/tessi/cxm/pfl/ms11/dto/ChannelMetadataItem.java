package com.tessi.cxm.pfl.ms11.dto;

import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMetadataItem {

  @Min(value = 0, message = "Id must greater than or equal to 0")
  private long id;

  private String value;

  @Min(value = 1, message = "Order must greater than 0")
  private long order;
}
