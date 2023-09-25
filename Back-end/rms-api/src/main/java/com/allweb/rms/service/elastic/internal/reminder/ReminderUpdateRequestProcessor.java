package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.service.elastic.AbstractElasticRequestProcessor;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticRequest;
import org.springframework.stereotype.Component;

@Component
class ReminderUpdateRequestProcessor extends AbstractElasticRequestProcessor<Reminder> {
  private final ReminderUpdateHandlerChain handlerChain;

  ReminderUpdateRequestProcessor(ReminderUpdateHandlerChain handlerChain) {
    super(ElasticConstants.REMINDER_UPDATE_REQUEST_KEY);
    this.handlerChain = handlerChain;
  }

  @Override
  protected void doProcess(ElasticRequest<Reminder> elasticRequest) {
    ChainContext context = new ChainContext();
    context.put(ElasticConstants.REMINDER_OBJECT_KEY, elasticRequest.getArgument());
    context.put(ElasticConstants.OPERATION_KEY, ElasticConstants.UPDATE_OPERATION);
    this.handlerChain.handle(context);
  }

  @Override
  protected boolean validate(ElasticRequest<Reminder> elasticRequest) {
    return elasticRequest != null
        && this.getKey().equals(elasticRequest.getRequestInfo().getKey())
        && elasticRequest.getArgument() != null;
  }
}
