package com.tessi.cxm.pfl.ms11.dto;

import com.tessi.cxm.pfl.ms11.constant.Language;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResourceParam {

  private int page;
  private int pageSize;
  private String sortDirection;
  private String filter;

  private String language;
  private List<String> types;
  private String sortByField;

}
