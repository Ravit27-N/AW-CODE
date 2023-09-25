package com.tessi.cxm.pfl.ms32.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticRequestFilter extends GlobalStatisticRequestFilter {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<String> subStatuses = new ArrayList<>();

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private boolean isGlobalFillers;

  private String secondFillerText;
  private String secondFillerKey;

  private String thirdFillerText;
  private String thirdFillerKey;

  private boolean includeMetadata = true;

  public List<String> getGroupFillers() {
    var groupFillers = new ArrayList<String>();
    if (!CollectionUtils.isEmpty(this.getFillers())) {
      groupFillers.addAll(this.getFillers());
    }
    if (StringUtils.hasText(secondFillerKey)) {
      groupFillers.add(secondFillerKey);
    }
    if (StringUtils.hasText(thirdFillerKey)) {
      groupFillers.add(thirdFillerKey);
    }
    return groupFillers;
  }

  public int getGroupFillersOffset() {
    var result = 0;
    if (!CollectionUtils.isEmpty(this.getFillers())) {
      result += 1;
    }
    if (StringUtils.hasText(this.secondFillerKey)) {
      result += 2;
    }
    if (StringUtils.hasText(this.thirdFillerKey)) {
      result += 3;
    }
    return result;
  }

  public Map<String, String> getFillerKeyText() {
    Map<String, String> map = new HashMap<>();
    if (!CollectionUtils.isEmpty(this.getFillers()) && StringUtils.hasText(getSearchByFiller())) {
      map.put(this.getFillers().get(0), getSearchByFiller());
    }
    if (StringUtils.hasText(secondFillerKey) && StringUtils.hasText(secondFillerText)) {
      map.put(secondFillerKey, secondFillerText);
    }
    if (StringUtils.hasText(thirdFillerKey) && StringUtils.hasText(thirdFillerText)) {
      map.put(thirdFillerKey, thirdFillerText);
    }
    return map;
  }
}
