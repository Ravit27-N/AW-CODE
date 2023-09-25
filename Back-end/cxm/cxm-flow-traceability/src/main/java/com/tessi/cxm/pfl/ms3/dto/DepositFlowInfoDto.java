package com.tessi.cxm.pfl.ms3.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class DepositFlowInfoDto implements Serializable {

  private int step;
  private String composedId;
  private boolean validation;

  public DepositFlowInfoDto() {
  }
  public DepositFlowInfoDto(int step) {
    this();
    this.step = step;
  }
  public DepositFlowInfoDto(int step, String composedId) {
    this(step);
    this.composedId = composedId;
  }
  public DepositFlowInfoDto(int step, String composedId, boolean validation) {
    this(step, composedId);
    this.validation = validation;
  }
}
