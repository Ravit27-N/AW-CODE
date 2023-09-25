package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;

public class CandidateHardDeleteRequest extends BaseElasticRequest<Candidate> {
  public CandidateHardDeleteRequest(Candidate argument) {
    super(ElasticConstants.CANDIDATE_HARD_REQUEST_KEY, argument);
  }
}
