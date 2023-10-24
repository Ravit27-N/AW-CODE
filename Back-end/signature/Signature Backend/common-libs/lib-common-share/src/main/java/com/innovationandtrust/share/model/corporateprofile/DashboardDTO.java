package com.innovationandtrust.share.model.corporateprofile;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

  private String startDate;

  private String endDate;

  private long totalProjects;

  private Map<String, Object> contents;
}
