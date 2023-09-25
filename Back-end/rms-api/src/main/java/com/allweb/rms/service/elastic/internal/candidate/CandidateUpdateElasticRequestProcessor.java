package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.service.elastic.AbstractElasticRequestProcessor;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticRequest;
import org.springframework.stereotype.Component;

@Component
class CandidateUpdateElasticRequestProcessor extends AbstractElasticRequestProcessor<Candidate> {
  private final CandidateUpdateHandlerChain candidateUpdateHandlerChain;

  CandidateUpdateElasticRequestProcessor(CandidateUpdateHandlerChain candidateUpdateHandlerChain) {
    super(ElasticConstants.CANDIDATE_UPDATE_REQUEST_KEY);
    this.candidateUpdateHandlerChain = candidateUpdateHandlerChain;
  }

  @Override
  protected void doProcess(ElasticRequest<Candidate> elasticRequest) {
    ChainContext context = new ChainContext();
    context.put(ElasticConstants.CANDIDATE_OBJECT_KEY, elasticRequest.getArgument());
    this.candidateUpdateHandlerChain.handle(context);
  }

  @Override
  protected boolean validate(ElasticRequest<Candidate> elasticRequest) {
    return elasticRequest != null
        && this.getKey().equals(elasticRequest.getRequestInfo().getKey())
        && elasticRequest.getArgument() != null;
  }
}
