package com.tessi.cxm.pfl.ms11.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChannelMetadataRequestDto {

  private String customer;
  private String type;
  @Valid private List<ChannelMetadataItem> metadata = new ArrayList<>();
}
