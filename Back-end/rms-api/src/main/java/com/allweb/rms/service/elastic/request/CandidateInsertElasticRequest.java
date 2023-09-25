package com.allweb.rms.service.elastic.request;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.service.elastic.BaseElasticRequest;
import com.allweb.rms.service.elastic.ElasticConstants;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateInsertElasticRequest extends BaseElasticRequest<Candidate> {
  private int candidateStatusId;
  private List<Integer> universityIds;

  public CandidateInsertElasticRequest(Candidate argument) {
    super(ElasticConstants.CANDIDATE_INSERT_REQUEST_KEY, argument);
  }
}
