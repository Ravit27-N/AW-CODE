package com.tessi.cxm.pfl.ms11.dto;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;

@Getter
public class ChannelMetadataResponseDto extends HashMap<String, Object> {

  public void setCustomer(String customer) {
    this.put("customer", customer);
  }

  public void add(String type, List<ChannelMetadataItem> metadata) {
    this.put(type, metadata);
  }
}
