package com.tessi.cxm.pfl.ms32.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner.UserDetailsOwner;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalStatisticRequestFilter {
  private List<String> channels;
  private List<String> categories;

  @NotNull
  @DateTimeFormat(pattern = AnalyticsConstants.DATE_FORMAT)
  private Date startDate;

  @NotNull
  @DateTimeFormat(pattern = AnalyticsConstants.DATE_FORMAT)
  private Date endDate;

  @NotNull
  @DateTimeFormat(pattern = AnalyticsConstants.DATE_FORMAT_ISO)
  private Date requestedAt;

  private List<String> fillers = new ArrayList<>();
  private String searchByFiller;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<Long> ownerIds = new ArrayList<>();

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<Long, UserDetailsOwner> ownerDetails = new HashMap<>();

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<SharedClientFillersDTO> clientFillers = new ArrayList<>();

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<String> statuses = new ArrayList<>();

  public List<String> getChannels() {
    if (this.channels == null) {
      return List.of();
    }
    return this.channels;
  }

  public List<String> getCategories() {
    if (categories == null) {
      return List.of();
    }
    return categories;
  }

  public List<String> getFillers() {
    if (CollectionUtils.isEmpty(fillers)) {
      return new ArrayList<>();
    }

    return fillers;
  }
}
