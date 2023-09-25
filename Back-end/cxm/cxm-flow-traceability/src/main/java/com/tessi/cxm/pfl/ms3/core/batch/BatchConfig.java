package com.tessi.cxm.pfl.ms3.core.batch;

import com.tessi.cxm.pfl.ms3.constant.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.ms3.core.batch.listener.FlowTraceabilityListener;
import com.tessi.cxm.pfl.ms3.core.batch.listener.NotificationReaderListener;
import com.tessi.cxm.pfl.ms3.core.batch.processor.ElementAssociationProcessor;
import com.tessi.cxm.pfl.ms3.core.batch.processor.FlowTraceabilityProcessor;
import com.tessi.cxm.pfl.ms3.core.batch.reader.CountFlowDocumentJpaReader;
import com.tessi.cxm.pfl.ms3.core.batch.reader.CountFlowTraceabilityJpaReader;
import com.tessi.cxm.pfl.ms3.core.batch.reader.ElementAssociationReader;
import com.tessi.cxm.pfl.ms3.core.batch.reader.FlowTraceabilityReader;
import com.tessi.cxm.pfl.ms3.core.batch.reader.NotificationJpaReader;
import com.tessi.cxm.pfl.ms3.core.batch.task.NotificationEventStatus;
import com.tessi.cxm.pfl.ms3.core.batch.task.TaskletBase64Executor;
import com.tessi.cxm.pfl.ms3.core.batch.writer.ElementsAssociationWriter;
import com.tessi.cxm.pfl.ms3.core.batch.writer.FlowTraceabilityItemWriter;
import com.tessi.cxm.pfl.ms3.core.batch.writer.NotificationWriter;
import com.tessi.cxm.pfl.ms3.dto.CountDocumentOfFlowImpl;
import com.tessi.cxm.pfl.ms3.dto.CountDocumentOfFlowProjection;
import com.tessi.cxm.pfl.ms3.dto.ElementAssociationDocumentContext;
import com.tessi.cxm.pfl.ms3.entity.ElementAssociation;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentNotification;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.xml.Document;
import com.tessi.cxm.pfl.ms3.entity.xml.Job;
import com.tessi.cxm.pfl.ms3.entity.xml.Notification;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.shared.core.batch.AbstractBatchConfigure;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * Implementation class for batch processing.
 *
 * @author Piseth KHON
 * @author Sokhour LACH
 */
@Slf4j
@Configuration
public class BatchConfig extends AbstractBatchConfigure {

  private static final String TABLE_PREFIX = "BATCH_";
  protected DataSource batchDataSource;

  public BatchConfig(
      BatchProperties properties,
      DataSource batchDataSource,
      TransactionManagerCustomizers transactionManagerCustomizers,
      EntityManagerFactory entityManagerFactory,
      StepBuilderFactory stepBuilderFactory,
      JobBuilderFactory jobBuilderFactory) {
    super(properties, batchDataSource, transactionManagerCustomizers, entityManagerFactory);
    this.stepBuilderFactory = stepBuilderFactory;
    this.batchDataSource = batchDataSource;
    setJobBuilderFactory(jobBuilderFactory);
    setStepBuilderFactory(stepBuilderFactory);
  }

  /**
   * Item reader for reading XML input based on StAX.
   *
   * <p>It extracts fragments from the input XML document which correspond to records for
   * processing. The fragments are wrapped with StartDocument and EndDocument events so that the
   * fragments can be further processed like standalone XML documents.
   *
   * <p>The implementation is <b>not</b> thread-safe.
   */
  @Bean
  public StaxEventItemReader<Job> notificationXmlItemReader() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setClassesToBeBound(Job.class);
    StaxEventItemReader<Job> reader =
        new StaxEventItemReader<>();
    reader.setFragmentRootElementName("job");
    reader.setUnmarshaller(marshaller);

    return reader;
  }

  /**
   * Reads items from multiple resources sequentially - resource list is given by array of {@link
   * Resource}, the actual reading is delegated to {@link ResourceAwareItemReaderItemStream}.
   *
   * <p>Input resources are ordered using {@link Comparator} to make sure resource ordering is
   * preserved between job runs in restart scenario.
   */
  @Bean
  @StepScope
  public MultiResourceItemReader<Job> multiResourceItemReader(
      @Value("#{jobParameters[XML_FILE_PATH]}") String xmlResources) {
    MultiResourceItemReader<Job> multiResourceItemReader = new MultiResourceItemReader<>();
    var xmlPath = this.getAllXmlFiles(Path.of(xmlResources));
    multiResourceItemReader.setResources(xmlPath);
    multiResourceItemReader.setDelegate(notificationXmlItemReader());
    return multiResourceItemReader;
  }

  /**
   * Creates a step and initializes its job repository and transaction manager. Note that if the
   * builder is used to create a &#64;Bean definition then the name of the step and the bean name
   * might be different.
   *
   * @return a step
   */
  @Bean
  @Qualifier("notificationStep")
  public Step notificationStep(
      MultiResourceItemReader<Job> multiResourceItemReader,
      ItemWriter<List<FlowDocumentNotification>> writer,
      ItemProcessor<Job, List<FlowDocumentNotification>> processor,
      NotificationReaderListener notificationReaderListener) {
    return stepBuilderFactory
        .get("notificationStep")
        .<Job, List<FlowDocumentNotification>>chunk(1)
        .reader(multiResourceItemReader)
        .listener(notificationReaderListener)
        .processor(processor)
        .writer(writer)
        .build();
  }

  /**
   * This implementation creates a Resource from FileSystemResource, applying the given path
   * relative to the path of the underlying resource of this descriptor.
   *
   * @see org.springframework.util.StringUtils#applyRelativePath(String, String)
   */
  private Resource[] getAllXmlFiles(Path path) {
    log.info("Read xml files in batch");
    List<Resource> resources = new ArrayList<>();
    var resource =
        FileUtils.listFiles(path.toFile(), new String[] {"xml"}, true).stream()
            .map(
                f -> {
                  try {
                    return f.getCanonicalPath();
                  } catch (IOException e) {
                    log.error("Xml files are not found!", e);
                    if (log.isDebugEnabled()) {
                      log.debug("Xml files are not found!", e);
                    }
                  }
                  return "";
                })
            .collect(Collectors.toList());
    resource.forEach(
        pathResource -> Collections.addAll(resources, new FileSystemResource(pathResource)));
    return resources.toArray(new Resource[0]);
  }

  /**
   * Initialize {@link ItemWriter} context bean.
   *
   * @implNote some parameters of this class are just set by default when the bean created.
   */
  @Bean
  public ItemWriter<List<FlowDocumentNotification>> writer(
      EntityManagerFactory entityManagerFactory) {
    JpaItemWriter<FlowDocumentNotification> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return new NotificationWriter(jpaItemWriter);
  }

  /**
   * Initialize {@link NotificationJpaReader} context bean.
   *
   * @implNote some parameters of this class are just set by default when the bean created.
   */
  @Bean
  public NotificationJpaReader<FlowDocument> reader(FlowDocumentRepository documentRepository) {
    var jpaRepo = new NotificationJpaReader<FlowDocument>();
    jpaRepo.setRepository(documentRepository);
    jpaRepo.setMethodName("findFlowDocumentByIdDocIn");
    jpaRepo.setSort(Map.of("id", Direction.ASC));
    return jpaRepo;
  }

  @Bean("countDocumentOfFlowReader")
  public CountFlowDocumentJpaReader<CountDocumentOfFlowProjection> docReader(
      FlowDocumentRepository documentRepository) {
    var jpaDoc = new CountFlowDocumentJpaReader<CountDocumentOfFlowProjection>();
    jpaDoc.setRepository(documentRepository);
    jpaDoc.setMethodName("countFlowDocumentByFlowIdInAndNotInStatus");
    jpaDoc.setSort(Map.of());
    return jpaDoc;
  }

  @Bean("countDocumentFromFlowReader")
  public CountFlowTraceabilityJpaReader<CountDocumentOfFlowProjection> flowReader(
      FlowTraceabilityRepository traceabilityRepository) {
    var jpaDoc = new CountFlowTraceabilityJpaReader<CountDocumentOfFlowProjection>();
    jpaDoc.setRepository(traceabilityRepository);
    jpaDoc.setMethodName("countAllDocsOfFlow");
    jpaDoc.setSort(Map.of());
    return jpaDoc;
  }

  /**
   * Initialize JobRepository.
   */
  @Override
  protected JobRepository createJobRepository() throws Exception {
    JobRepositoryFactoryBean jobRepository = new JobRepositoryFactoryBean();
    jobRepository.setDatabaseType(DatabaseType.POSTGRES.name());
    jobRepository.setDataSource(this.batchDataSource);
    jobRepository.setTablePrefix(TABLE_PREFIX);
    jobRepository.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
    jobRepository.setTransactionManager(this.getTransactionManager());
    jobRepository.setSerializer(executionContextSerializer());
    jobRepository.afterPropertiesSet();
    return jobRepository.getObject();
  }

  /**
   * A composite method that combines both serialization and deserialization of an execution context
   * into a single implementation. Implementations of this interface are used to serialize the
   * execution context for persistence during the execution of a job.
   *
   * @see Serializer
   * @see Deserializer
   */
  public ExecutionContextSerializer executionContextSerializer() {
    return new Jackson2ExecutionContextStringSerializer(
        Document[].class.getName(),
        Document.class.getName(),
        Notification[].class.getName(),
        Notification.class.getName(),
        ElementAssociationDocumentContext[].class.getName(),
        ElementAssociationDocumentContext.class.getName(),
        CountDocumentOfFlowProjection.class.getName(),
        CountDocumentOfFlowProjection[].class.getName(),
        CountDocumentOfFlowImpl.class.getName(),
        CountDocumentOfFlowImpl[].class.getName());
  }

  /** Initialize TaskletBase64Executor step. */
  @Bean("stepTaskletBase64Executor")
  public Step stepTaskletBase64Executor(TaskletBase64Executor taskletBase64Executor) {
    return this.stepBuilderFactory
        .get("stepTaskletBase64Executor")
        .tasklet(taskletBase64Executor)
        .build();
  }

  @Bean("stepTaskletNotificationEventStatus")
  public Step stepTaskletNotificationEventStatus(NotificationEventStatus notificationEventStatus) {
    return this.stepBuilderFactory
            .get("stepTaskletNotificationEventStatus")
            .tasklet(notificationEventStatus)
            .build();
  }

  /** Initialize elementsAssociation step. */
  @Bean("stepElementAssociation")
  public Step stepElementAssociation(
      ElementAssociationReader elementAssociationReader,
      ElementAssociationProcessor elementAssociationProcessor,
      ItemWriter<ElementAssociation> elementAssociationItemWriter) {

    return this.stepBuilderFactory
        .get("stepElementAssociation")
        .<ElementAssociationDocumentContext, ElementAssociation>chunk(1)
        .reader(elementAssociationReader)
        .processor(elementAssociationProcessor)
        .writer(elementAssociationItemWriter)
        .build();
  }

  /**
   * Initialize elementAssociationItemWriter step.
   */
  @Bean("elementAssociationItemWriter")
  public ItemWriter<ElementAssociation> elementAssociationItemWriter(
      EntityManagerFactory entityManagerFactory) {
    JpaItemWriter<ElementAssociation> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return new ElementsAssociationWriter(jpaItemWriter);
  }

  @Bean("flowTraceabilityItemWriter")
  public FlowTraceabilityItemWriter flowTraceabilityItemWriter(
      EntityManagerFactory entityManagerFactory) {
    JpaItemWriter<FlowTraceability> jpaItemWriter = new JpaItemWriter<>();
    jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
    return new FlowTraceabilityItemWriter(jpaItemWriter);
  }

  @Bean("stepUpdateFlowStatus")
  public Step stepUpdateFlowStatus(FlowTraceabilityListener listener,
      FlowTraceabilityReader flowTraceabilityReader,
      @Qualifier("flowTraceabilityProcessor")
      FlowTraceabilityProcessor flowProcessor,
      @Qualifier("flowTraceabilityItemWriter")
      FlowTraceabilityItemWriter flowItemWriter) {
    return this.stepBuilderFactory.get("stepUpdateFlowStatus")
        .<CountDocumentOfFlowProjection, FlowTraceability>chunk(1)
        .reader(flowTraceabilityReader).processor(flowProcessor).writer(flowItemWriter)
        .listener(listener)
        .build();
  }

  /**
   * Initialize notification job.
   */
  @Bean(FlowTraceabilityConstant.NOTIFICATION_JOB_BEAN)
  public org.springframework.batch.core.Job notificationJob(
      @Qualifier("notificationStep") Step notificationStep,
      @Qualifier("stepTaskletBase64Executor") Step stepTaskletBase64Executor,
      @Qualifier("stepTaskletNotificationEventStatus") Step stepTaskletNotificationEventStatus,
      @Qualifier("stepElementAssociation") Step stepElementAssociation,
      @Qualifier("stepUpdateFlowStatus") Step stepUpdateFlowStatus) {
    return this.jobBuilderFactory
        .get(
            String.format(
                "%s-%s", FlowTraceabilityConstant.NOTIFICATION_JOB, System.currentTimeMillis()))
        .incrementer(new RunIdIncrementer())
        .start(notificationStep)
        .on(ExitStatus.COMPLETED.getExitCode())
        .to(stepTaskletBase64Executor)
        .on(ExitStatus.COMPLETED.getExitCode())
        .to(stepElementAssociation)
        .on(ExitStatus.COMPLETED.getExitCode())
        .to(stepTaskletNotificationEventStatus)
        .on(ExitStatus.COMPLETED.getExitCode())
        .to(stepUpdateFlowStatus)
        .end()
        .build();
  }
}
