package com.allweb.rms.service.elastic.internal.candidate;

import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.service.elastic.AbstractElasticRequestProcessor;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticRequest;
import com.allweb.rms.service.elastic.request.CandidateInsertElasticRequest;
import org.springframework.stereotype.Component;

@Component
class CandidateInsertElasticRequestProcessor extends AbstractElasticRequestProcessor<Candidate> {
  private final CandidateInsertHandlerChain handlerChain;

  CandidateInsertElasticRequestProcessor(CandidateInsertHandlerChain handlerChain) {
    super(ElasticConstants.CANDIDATE_INSERT_REQUEST_KEY);
    this.handlerChain = handlerChain;
  }

  @Override
  protected void doProcess(ElasticRequest<Candidate> elasticRequest) {
    CandidateInsertElasticRequest candidateInsertElasticRequest =
        (CandidateInsertElasticRequest) elasticRequest;
    ChainContext context = new ChainContext();
    context.put(ElasticConstants.CANDIDATE_OBJECT_KEY, candidateInsertElasticRequest.getArgument());
    context.put(
        ElasticConstants.UNIVERSITY_ID_LIST_KEY, candidateInsertElasticRequest.getUniversityIds());
    this.handlerChain.handle(context);
  }

  @Override
  protected boolean validate(ElasticRequest<Candidate> elasticRequest) {
    return elasticRequest != null
        && this.getKey().equals(elasticRequest.getRequestInfo().getKey())
        && elasticRequest.getArgument() != null;
  }
}
