package com.tessi.cxm.pfl.ms11.entity.projection;

public interface DepositValidationProjection {
  String getFlowType();

  Long getIdCreator();

  boolean isScanActivation();

  boolean isConfigurationActivation();
}
