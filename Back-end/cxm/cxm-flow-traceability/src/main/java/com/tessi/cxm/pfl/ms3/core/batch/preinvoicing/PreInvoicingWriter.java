package com.tessi.cxm.pfl.ms3.core.batch.preinvoicing;

import com.tessi.cxm.pfl.ms3.core.batch.helper.JobHelper;
import com.tessi.cxm.pfl.ms3.dto.DocumentCsvProjection;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PreInvoicingWriter implements ItemWriter<DocumentCsvProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreInvoicingWriter.class);

    private final JobHelper jobHelper;

    @Autowired
    public PreInvoicingWriter(JobHelper jobHelper) {
        this.jobHelper = jobHelper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,noRollbackFor = Exception.class)
    public void write(List documents) {
        LOGGER.info("archive documents");
        //CollectionUtils.emptyIfNull((List<DocumentCsvProjection>) documents).forEach(jobHelper::exportDocument);
        jobHelper.exportAllToCsv(documents);
    }

}