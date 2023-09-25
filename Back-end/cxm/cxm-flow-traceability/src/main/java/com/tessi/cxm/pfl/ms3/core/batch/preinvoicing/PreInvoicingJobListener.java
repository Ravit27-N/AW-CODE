package com.tessi.cxm.pfl.ms3.core.batch.preinvoicing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
public class PreInvoicingJobListener implements JobExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreInvoicingJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOGGER.info("Démarrage du batch {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LOGGER.info("Exit code: {}", jobExecution.getExitStatus().getExitCode());
        LOGGER.info("Fin du batch {}", jobExecution.getJobInstance().getJobName());
    }
}