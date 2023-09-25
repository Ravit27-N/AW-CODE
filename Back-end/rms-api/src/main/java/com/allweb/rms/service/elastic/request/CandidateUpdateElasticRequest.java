package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;

public class CandidateUpdateElasticRequest extends BaseElasticRequest<Candidate> {
  public CandidateUpdateElasticRequest(Candidate candidate) {
    super(ElasticConstants.CANDIDATE_UPDATE_REQUEST_KEY, candidate);
  }
}
