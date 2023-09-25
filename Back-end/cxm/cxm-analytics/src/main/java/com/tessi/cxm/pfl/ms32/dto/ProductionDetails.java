package com.tessi.cxm.pfl.ms32.dto;

import java.io.Serializable;

public interface ProductionDetails extends Serializable {
  String getChannel();

  String getSubChannel();

  String getStatus();

  Long getTotal();
}
