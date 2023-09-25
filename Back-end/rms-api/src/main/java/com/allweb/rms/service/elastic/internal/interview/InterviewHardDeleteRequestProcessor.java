package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.service.elastic.AbstractElasticRequestProcessor;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticRequest;
import org.springframework.stereotype.Component;

@Component
class InterviewHardDeleteRequestProcessor extends AbstractElasticRequestProcessor<Interview> {
  private final InterviewHardDeleteHandlerChain handlerChain;

  InterviewHardDeleteRequestProcessor(InterviewHardDeleteHandlerChain handlerChain) {
    super(ElasticConstants.INTERVIEW_HARD_DELETE_REQUEST_KEY);
    this.handlerChain = handlerChain;
  }

  @Override
  protected void doProcess(ElasticRequest<Interview> elasticRequest) {
    ChainContext context = new ChainContext();
    context.put(ElasticConstants.INTERVIEW_OBJECT_KEY, elasticRequest.getArgument());
    handlerChain.handle(context);
  }

  @Override
  protected boolean validate(ElasticRequest<Interview> elasticRequest) {
    return elasticRequest != null
        && this.getKey().equals(elasticRequest.getRequestInfo().getKey())
        && elasticRequest.getArgument() != null;
  }
}
