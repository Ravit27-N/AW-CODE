package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.service.elastic.AbstractElasticRequestProcessor;
import com.allweb.rms.service.elastic.BaseHandlerChain;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticRequest;
import org.springframework.stereotype.Component;

@Component
class CandidateHardDeleteElasticRequestProcessor
    extends AbstractElasticRequestProcessor<Candidate> {
  private final BaseHandlerChain handlerChain;

  CandidateHardDeleteElasticRequestProcessor(
      CandidateHardDeleteHandlerChain candidateHardDeleteHandlerChain) {
    super(ElasticConstants.CANDIDATE_HARD_REQUEST_KEY);
    this.handlerChain = candidateHardDeleteHandlerChain;
  }

  @Override
  protected void doProcess(ElasticRequest<Candidate> elasticRequest) {
    ChainContext context = new ChainContext();
    context.put(ElasticConstants.CANDIDATE_OBJECT_KEY, elasticRequest.getArgument());
    context.put(ElasticConstants.OPERATION_KEY, ElasticConstants.DELETE_OPERATION);
    this.handlerChain.handle(context);
  }

  @Override
  protected boolean validate(ElasticRequest<Candidate> elasticRequest) {
    return elasticRequest != null
        && this.getKey().equals(elasticRequest.getRequestInfo().getKey())
        && elasticRequest.getArgument() != null;
  }
}
