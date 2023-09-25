package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.service.elastic.AbstractElasticRequestProcessor;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticRequest;
import org.springframework.stereotype.Component;

@Component
class InterviewUpdateElasticRequestProcessor extends AbstractElasticRequestProcessor<Interview> {
  private final InterviewUpdateHandlerChain handlerChain;

  InterviewUpdateElasticRequestProcessor(InterviewUpdateHandlerChain handlerChain) {
    super(ElasticConstants.INTERVIEW_UPDATE_REQUEST_KEY);
    this.handlerChain = handlerChain;
  }

  @Override
  protected void doProcess(ElasticRequest<Interview> elasticRequest) {
    ChainContext context = new ChainContext();
    context.put(ElasticConstants.INTERVIEW_OBJECT_KEY, elasticRequest.getArgument());
    this.handlerChain.handle(context);
  }

  @Override
  protected boolean validate(ElasticRequest<Interview> elasticRequest) {
    return elasticRequest != null
        && this.getKey().equals(elasticRequest.getRequestInfo().getKey())
        && elasticRequest.getArgument() != null;
  }
}
