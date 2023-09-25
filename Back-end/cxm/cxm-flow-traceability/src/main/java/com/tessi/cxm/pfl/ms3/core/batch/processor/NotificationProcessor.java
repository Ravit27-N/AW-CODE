package com.tessi.cxm.pfl.ms3.core.batch.processor;

import com.google.common.base.Strings;
import com.tessi.cxm.pfl.ms3.constant.DateConvertor;
import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentNotification;
import com.tessi.cxm.pfl.ms3.entity.xml.Document;
import com.tessi.cxm.pfl.ms3.entity.xml.Job;
import com.tessi.cxm.pfl.ms3.entity.xml.Notification;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ConfigurationException;
import org.modelmapper.Converter;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Implementation class for item transformation. Given an item as input, this interface provides an
 * extension point which allows for the application of business logic in an item oriented processing
 * scenario. It should be noted that while it's possible to return a different type than the one
 * provided, it's not strictly necessary. Furthermore, returning {@code null} indicates that the
 * item should not be continued to be processed.
 *
 * @implNote
 *     <p>Job
 *     <p>type of input item
 *     <p>FlowDocumentNotification
 *     <p>type of output item
 * @author Piseth KHON
 * @author Sokhour LACH
 */
@Slf4j
@Component
@StepScope
public class NotificationProcessor
    implements ItemProcessor<Job, List<FlowDocumentNotification>>, StepExecutionListener {

  private final ModelMapper modelMapper;
  private final List<Document> documents = new ArrayList<>();

  private final List<FlowDocument> flowDocuments = new ArrayList<>();

  public NotificationProcessor() {
    this.modelMapper = getModelMapper();
  }

  /** {@inheritDoc} */
  @Override
  public List<FlowDocumentNotification> process(Job item) throws Exception {
    if (item.getDocuments().isEmpty()) {
      return null;
    }
    List<Document> invalidDocuments = new ArrayList<>();
    item.getDocuments()
        .removeIf(
            doc -> {
              if (doc.getId() == 0) {
                invalidDocuments.add(doc);
              }
              return doc.getId() == 0;
            });
    this.invalidDocument(invalidDocuments);
    this.documents.addAll(item.getDocuments());
    var flowNotifications =
        item.getDocuments().stream()
            .map(getDocumentFlowDocumentNotificationFunction())
            .collect(Collectors.toList());

    flowNotifications.forEach(fn -> flowDocuments.add(fn.getDocument()));
    return flowNotifications;
  }

  private void invalidDocument(List<Document> documents) {
    List<String> emptyDocuments =
        documents.stream()
            .filter(
                doc ->
                    FlowDocumentStatus.isAbleMapStatusToCompleted(doc.getNotification().getStep()))
            .map(Document::getIdDoc)
            .filter(StringUtils::isBlank)
            .collect(Collectors.toList());
    if (ObjectUtils.isNotEmpty(emptyDocuments)) {
      log.error("Some IdDoc are empty or null.");
    }
    List<String> invalidIdDocOnly =
        documents.stream()
            .filter(
                doc ->
                    FlowDocumentStatus.isAbleMapStatusToCompleted(doc.getNotification().getStep()))
            .map(Document::getIdDoc)
            .map(StringUtils::trim)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
    if (ObjectUtils.isNotEmpty(invalidIdDocOnly)) {
      log.error(
          String.format("IdDoc %s do not exist in \"flow_document\" table.", invalidIdDocOnly));
    }
    List<String> invalidIdDosOrStep =
        documents.stream()
            .filter(doc -> StringUtils.isNotBlank(doc.getIdDoc().trim()))
            .filter(
                doc ->
                    !FlowDocumentStatus.isAbleMapStatusToCompleted(doc.getNotification().getStep()))
            .map(Document::getIdDoc)
            .collect(Collectors.toList());
    if (ObjectUtils.isNotEmpty(invalidIdDosOrStep)) {
      log.error(
          String.format(
              "IdDoc or Step name %s do not exist in \"flow_document\" table or the configuration.",
              invalidIdDosOrStep));
    }
  }

  /**
   * Parses text from the beginning of the given string to produce a date. The method may not use
   * the entire text of the given string.
   *
   * <p>See the method for more information on date parsing.
   *
   * @param stringDate A <code>String</code> whose beginning should be parsed.
   * @return A <code>Date</code> parsed from the string.
   */
  private Date convertStringToDate(String stringDate) {
    return DateConvertor.parisTimeZoneToUTC(stringDate);
  }

  private ModelMapper getModelMapper() {
    var mapper = new ModelMapper();
    mapper
        .createTypeMap(Notification.class, FlowDocumentNotification.class)
        .addMappings(
            mapping ->
                mapping
                    .using(
                        (Converter<String, Date>)
                            context -> {
                              Date result = null;
                              if (StringUtils.isNotBlank(context.getSource())) {
                                result = convertStringToDate(context.getSource());
                              }
                              return result;
                            })
                    .map(Notification::getDate, FlowDocumentNotification::setDate));
    return mapper;
  }

  /**
   * Maps {@code source} to an instance of {@code destinationType}. Mapping is performed according
   * to the corresponding TypeMap. If no TypeMap exists for {@code source.getClass()} and {@code
   * destinationType} then one is created.
   *
   * @return fully mapped instance of {@code destinationType}
   * @throws IllegalArgumentException if {@code source} or {@code destinationType} are null
   * @throws ConfigurationException if the ModelMapper cannot find or create a TypeMap for the
   *     arguments
   * @throws MappingException if a runtime error occurs while mapping
   */
  private Function<Document, FlowDocumentNotification>
      getDocumentFlowDocumentNotificationFunction() {
    return document -> {
      FlowDocumentNotification flowDocumentNotification =
          this.modelMapper.map(document.getNotification(), FlowDocumentNotification.class);
      var flowDocument = new FlowDocument();
      flowDocument.setId(document.getId());
      flowDocumentNotification.setDocument(flowDocument);
      return flowDocumentNotification;
    };
  }

  /**
   * Initialize the state of the listener with the {@link StepExecution} from the current scope.
   *
   * @param stepExecution instance of {@link StepExecution}.
   */
  @Override
  public void beforeStep(StepExecution stepExecution) {
    // do nothing
  }

  /**
   * Set elementAssociation to execution context.
   *
   * <p>Give a listener a chance to modify the exit status from a step. The value returned will be
   * combined with the normal exit status using {@link ExitStatus#and(ExitStatus)}.
   *
   * <p>Called after execution of step's processing logic (both successful or failed). Throwing
   * exception in this method has no effect, it will only be logged.
   *
   * @param stepExecution {@link StepExecution} instance.
   * @return an {@link ExitStatus} to combine with the normal value. Return {@code null} to leave
   *     the old value unchanged.
   */
  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    final List<Document> documentsElementAssociation =
        this.documents.stream().filter(getDocumentPredicate()).collect(Collectors.toList());
    var context = stepExecution
        .getJobExecution()
        .getExecutionContext();
    context.put(
        FlowTraceabilityConstant.DOCUMENTS_ELEMENT_ASSOCIATION_CONTEXT,
        documentsElementAssociation);
    context.put(FlowTraceabilityConstant.DOCUMENTS_NOTIFICATION_CONTEXT, this.documents);
    context.put(FlowTraceabilityConstant.FLOW_DOCUMENT_CONTEXT,
        this.flowDocuments.stream().map(FlowDocument::getId).collect(
            Collectors.toList()));
    stepExecution.getJobExecution().setExecutionContext(context);
    return ExitStatus.COMPLETED;
  }

  /** Select only AccuseReception. */
  private Predicate<Document> getDocumentPredicate() {
    return doc -> !Strings.isNullOrEmpty(doc.getNotification().getAccuseReception());
  }
}
