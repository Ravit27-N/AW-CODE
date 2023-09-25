package com.tessi.cxm.pfl.ms8.dto;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;

@Getter
public class ResourceFileMetaDataResponse extends HashMap<String, Object> {

  public void add(String type, List<ResourceFileMetaDataResponseItem> metadata) {
    this.put(type, metadata);
  }

}
