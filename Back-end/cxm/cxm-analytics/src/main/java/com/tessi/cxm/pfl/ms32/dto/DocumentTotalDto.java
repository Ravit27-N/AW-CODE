package com.tessi.cxm.pfl.ms32.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTotalDto {

  private String channel;
  private String subChannel;
  private Long total;
}
