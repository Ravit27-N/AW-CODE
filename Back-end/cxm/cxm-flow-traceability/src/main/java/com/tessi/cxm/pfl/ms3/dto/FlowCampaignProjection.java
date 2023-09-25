package com.tessi.cxm.pfl.ms3.dto;

import com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail;
import java.util.Date;

public interface FlowCampaignProjection {

  long getId();
  Date getCreatedAt();
  String getCreatedBy();
  String getFullName();
  String getStatus();
  Date getDateStatus();
  FlowCampaignDetail getDetail();

  long getOwnerId();
}
