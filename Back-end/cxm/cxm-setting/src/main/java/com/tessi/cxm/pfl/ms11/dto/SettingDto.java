package com.tessi.cxm.pfl.ms11.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SettingDto {
  private Long id;
  private String customer;
  private String depositType;
  private String connector;
  private String extension;
  private String flowType;
  private long idCreator;
  private boolean scanActivation;

  @JsonProperty(access = Access.READ_ONLY)
  private String createdBy;
}
