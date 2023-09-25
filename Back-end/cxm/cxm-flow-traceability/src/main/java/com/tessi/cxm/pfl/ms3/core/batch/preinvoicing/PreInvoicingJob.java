package com.tessi.cxm.pfl.ms3.core.batch.preinvoicing;

import com.tessi.cxm.pfl.ms3.dto.DocumentCsvProjection;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentExportStatus;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class PreInvoicingJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreInvoicingJob.class);

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private FlowDocumentRepository flowDocumentRepository;

    @Autowired
    private PreInvoicingWriter preInvoicingWriter;
    @Bean(name = "preInvoicingsJob")
    public Job job() {
        return jobBuilderFactory.get("PreInvoicingJob")
                .incrementer(new RunIdIncrementer())
                .listener(new PreInvoicingJobListener())
                .start(exportStep())
                .build();
    }

    @Bean
    public Step exportStep() {
        return stepBuilderFactory.get("preInvoicingExportStep")
                .<DocumentCsvProjection, DocumentCsvProjection>chunk(1000)
                .reader(preInvoicingReader())
                .writer(preInvoicingWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10000)
                .listener(new PreInvoicingSkipListener())
                .build();
    }

    @Bean
    public RepositoryItemReader<DocumentCsvProjection> preInvoicingReader() {
        RepositoryItemReader<DocumentCsvProjection> reader = new RepositoryItemReader<>();
        reader.setRepository(flowDocumentRepository);
        reader.setMethodName("dataToCsv");
        List<Object> arguments = new ArrayList<>();
        arguments.add(FlowDocumentExportStatus.TO_EXPORT);
        reader.setArguments(arguments);
        Map<String, Direction> sort = new HashMap<>();
        sort.put("id", Direction.ASC);
        reader.setSort(sort);
        reader.setPageSize(1000);
        return reader;
    }

    public void run() throws Exception {
        JobExecution execution = jobLauncher.run(
                job(),
                new JobParametersBuilder().addLong("JobID", System.nanoTime()).toJobParameters()
        );
        LOGGER.info("Exit status: {}", execution.getStatus());
    }
}
