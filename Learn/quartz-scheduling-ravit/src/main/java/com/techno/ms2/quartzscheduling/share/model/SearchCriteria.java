package com.techno.ms2.quartzscheduling.share.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
  private String filter;
  private Pageable pageable;
  private Map<String, String> customQuery;

  public String getCustomQueryValue(String key){
    return customQuery.get(key);
  }

}
