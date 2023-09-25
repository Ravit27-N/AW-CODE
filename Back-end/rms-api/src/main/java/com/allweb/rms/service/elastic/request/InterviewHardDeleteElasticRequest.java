package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;

public class InterviewHardDeleteElasticRequest extends BaseElasticRequest<Interview> {

  public InterviewHardDeleteElasticRequest(Interview interview) {
    super(ElasticConstants.INTERVIEW_HARD_DELETE_REQUEST_KEY, interview);
  }
}
