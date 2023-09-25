package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseFilterCriteria {
  @JsonIgnore private String filter;

  private List<String> channels;
  private List<String> categories;
  private List<String> status;
  private List<String> depositModes;
  private String startDate;
  private String endDate;
  private List<String> fillers = new ArrayList<>();
  private String searchByFiller;

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

  public List<String> getStatus() {
    if (this.status == null) {
      return List.of();
    }
    return status;
  }

  public List<String> getDepositModes() {
    if (this.depositModes == null) {
      return List.of();
    }
    return depositModes;
  }
}
