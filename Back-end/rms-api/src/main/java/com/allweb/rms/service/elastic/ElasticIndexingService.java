package com.allweb.rms.service.elastic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Aspect
public class ElasticIndexingService {
    private final Map<String, ElasticRequestProcessor<?>> elasticRequestProcessorMap =
            new HashMap<>();

    public ElasticIndexingService(List<ElasticRequestProcessor<?>> requestProcessorList) {
        requestProcessorList.stream()
                .map(AbstractElasticRequestProcessor.class::cast)
                .forEach(
                        elasticRequestProcessor ->
                                elasticRequestProcessorMap.put(
                                        elasticRequestProcessor.getKey(), elasticRequestProcessor));
    }

    @SuppressWarnings("unchecked")
    public <T> void execute(ElasticRequest<T> elasticRequest) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        ElasticRequestProcessor<T> elasticRequestProcessor =
                                (ElasticRequestProcessor<T>)
                                        elasticRequestProcessorMap.get(elasticRequest.getRequestInfo().getKey());
                        elasticRequestProcessor.process(elasticRequest);
                    }
                });
    }
}
