package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;
import lombok.Getter;
import lombok.Setter;

public class InterviewInsertElasticRequest extends BaseElasticRequest<Interview> {
  @Getter @Setter private Reminder reminder;

  public InterviewInsertElasticRequest(Interview interview) {
    super(ElasticConstants.INTERVIEW_INSERT_REQUEST_KEY, interview);
  }
}
