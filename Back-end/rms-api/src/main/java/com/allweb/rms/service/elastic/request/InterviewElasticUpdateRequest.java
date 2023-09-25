package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;

public class InterviewElasticUpdateRequest extends BaseElasticRequest<Interview> {

  public InterviewElasticUpdateRequest(Interview interview) {
    super(ElasticConstants.INTERVIEW_UPDATE_REQUEST_KEY, interview);
  }
}
