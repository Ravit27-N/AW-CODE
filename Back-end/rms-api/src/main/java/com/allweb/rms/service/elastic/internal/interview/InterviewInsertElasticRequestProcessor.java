package com.allweb.rms.service.elastic.internal.interview;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.service.elastic.AbstractElasticRequestProcessor;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticRequest;
import com.allweb.rms.service.elastic.request.InterviewInsertElasticRequest;
import org.springframework.stereotype.Component;

@Component
class InterviewInsertElasticRequestProcessor extends AbstractElasticRequestProcessor<Interview> {
  InterviewInsertHandlerChain handlerChain;

  InterviewInsertElasticRequestProcessor(InterviewInsertHandlerChain interviewInsertHandlerChain) {
    super(ElasticConstants.INTERVIEW_INSERT_REQUEST_KEY);
    this.handlerChain = interviewInsertHandlerChain;
  }

  @Override
  protected void doProcess(ElasticRequest<Interview> elasticRequest) {
    InterviewInsertElasticRequest interviewRequest = (InterviewInsertElasticRequest) elasticRequest;
    ChainContext context = new ChainContext();
    context.put(ElasticConstants.INTERVIEW_OBJECT_KEY, interviewRequest.getArgument());
    context.put(ElasticConstants.REMINDER_OBJECT_KEY, interviewRequest.getReminder());
    this.handlerChain.handle(context);
  }

  @Override
  protected boolean validate(ElasticRequest<Interview> elasticRequest) {
    return elasticRequest != null
        && this.getKey().equals(elasticRequest.getRequestInfo().getKey())
        && elasticRequest.getArgument() != null;
  }
}
