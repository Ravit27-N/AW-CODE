package com.tessi.cxm.pfl.ms3.core.batch.preinvoicing;

import com.tessi.cxm.pfl.ms3.exception.TechnicalException;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PreInvoicingRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreInvoicingRunner.class);

    private PreInvoicingJob preInvoicingJob;

    @Autowired
    public PreInvoicingRunner(PreInvoicingJob preInvoicingJob) {
        this.preInvoicingJob = preInvoicingJob;
    }


    @Scheduled(cron = "${cxm.preinvoicing.document.export.time}")
    @SchedulerLock(name = "checkForPreInvoicingToExport",
            lockAtLeastFor = "3m", lockAtMostFor = "3m")
    public void checkForPreInvoicingToExport() {
        LockAssert.assertLocked();
        LOGGER.info("Starting scheduler for preInvoicing to export");
        try {
            preInvoicingJob.run();
        } catch (Exception e) {
            LOGGER.error("PreInvoicingJob job halted unexpectedly",e);
            throw new TechnicalException(e);
        }
    }

}