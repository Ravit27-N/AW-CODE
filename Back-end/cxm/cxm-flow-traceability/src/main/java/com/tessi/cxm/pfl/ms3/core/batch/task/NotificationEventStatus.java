package com.tessi.cxm.pfl.ms3.core.batch.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms3.constant.DateConvertor;
import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import com.tessi.cxm.pfl.ms3.entity.xml.Document;
import com.tessi.cxm.pfl.ms3.entity.xml.Notification;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentHistoryRepository;
import com.tessi.cxm.pfl.ms3.service.ReportingService;
import com.tessi.cxm.pfl.shared.core.batch.task.AbstractTasklet;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentHistoryStatusReportModel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@StepScope
@Slf4j
public class NotificationEventStatus extends AbstractTasklet implements StepExecutionListener {

  private final FlowDocumentHistoryRepository flowDocumentHistoryRepository;

  private final ReportingService reportingService;
  private List<Document> documents;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    this.documents = this.getDocuments(this.getJobExecutionContext(stepExecution));
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    return ExitStatus.COMPLETED;
  }

  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
      throws Exception {
    List<String> idDocs =
        this.documents.stream().map(Document::getIdDoc).collect(Collectors.toList());
    List<String> events =
        this.documents.stream()
            .map(Document::getNotification)
            .map(Notification::getStep)
            .collect(Collectors.toList());
    List<Date> dateTimes =
        this.documents.stream()
            .map(Document::getNotification)
            .map(Notification::getDate)
            .map(this::convertStringToDate)
            .collect(Collectors.toList());
    List<FlowDocumentHistory> flowDocumentHistories =
        flowDocumentHistoryRepository
            .findAllByEventIgnoreCaseInAndDateTimeInAndFlowDocumentIdDocIn(
                events, dateTimes, idDocs)
            .stream()
            .filter(distinctBy(FlowDocumentHistory::getEvent))
            .collect(Collectors.toList());
    createFlowDocumentHistoryReport(flowDocumentHistories);
    return RepeatStatus.FINISHED;
  }

  public static <T> Predicate<T> distinctBy(Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }

  public List<Document> getDocuments(ExecutionContext executionContext) {
    var mapper = new ObjectMapper();
    return mapper.convertValue(
        executionContext.get(FlowTraceabilityConstant.DOCUMENTS_NOTIFICATION_CONTEXT),
        new TypeReference<>() {});
  }

  public void createFlowDocumentHistoryReport(List<FlowDocumentHistory> flowDocumentHistories) {
    flowDocumentHistories.forEach(
        flowDocumentHistory -> {
          CreateFlowDocumentHistoryStatusReportModel historyStatusReportModel =
              CreateFlowDocumentHistoryStatusReportModel.builder()
                  .id(flowDocumentHistory.getId())
                  .flowDocumentId(flowDocumentHistory.getFlowDocument().getId())
                  .status(flowDocumentHistory.getEvent())
                  .dateStatus(flowDocumentHistory.getDateTime())
                  .build();
          reportingService.createFlowDocumentEventHistory(historyStatusReportModel);
        });
  }

  private Date convertStringToDate(String stringDate) {
    return DateConvertor.parisTimeZoneToUTC(stringDate);
  }
}
