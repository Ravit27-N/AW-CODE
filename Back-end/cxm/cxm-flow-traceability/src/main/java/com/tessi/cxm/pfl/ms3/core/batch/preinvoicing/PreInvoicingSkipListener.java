package com.tessi.cxm.pfl.ms3.core.batch.preinvoicing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
public class PreInvoicingSkipListener implements SkipListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreInvoicingSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        LOGGER.error("Enregistrement écarté lors de la lecture", t);
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        LOGGER.error("Enregistrement écarté lors de l'écriture", t);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        LOGGER.error("Enregistrement écarté lors du traitement", t);
    }
}
