package com.allweb.rms.service.elastic.internal.reminder;

import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.service.elastic.AbstractElasticRequestProcessor;
import com.allweb.rms.service.elastic.ChainContext;
import com.allweb.rms.service.elastic.ElasticConstants;
import com.allweb.rms.service.elastic.ElasticRequest;
import org.springframework.stereotype.Component;

@Component
class ReminderInsertElasticRequestProcessor extends AbstractElasticRequestProcessor<Reminder> {
  private final ReminderInsertHandlerChain handlerChain;

  ReminderInsertElasticRequestProcessor(ReminderInsertHandlerChain handlerChain) {
    super(ElasticConstants.REMINDER_INSERT_REQUEST_KEY);
    this.handlerChain = handlerChain;
  }

  @Override
  protected void doProcess(ElasticRequest<Reminder> elasticRequest) {
    ChainContext context = new ChainContext();
    context.put(ElasticConstants.REMINDER_OBJECT_KEY, elasticRequest.getArgument());
    context.put(ElasticConstants.OPERATION_KEY, ElasticConstants.INSERT_OPERATION);
    this.handlerChain.handle(context);
  }

  @Override
  protected boolean validate(ElasticRequest<Reminder> elasticRequest) {
    return elasticRequest != null
        && this.getKey().equals(elasticRequest.getRequestInfo().getKey())
        && elasticRequest.getArgument() != null;
  }
}
