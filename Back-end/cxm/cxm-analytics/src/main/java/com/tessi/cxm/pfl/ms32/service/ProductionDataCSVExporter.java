package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public abstract class ProductionDataCSVExporter implements ProductionDataExporter {

  protected abstract String[] getHeaders(Context context);

  protected abstract void exportInternal(Context context, CSVPrinter csvPrinter) throws IOException;

  @Override
  public void export(Context context, Writer writer) throws IOException {
    final var delimiter = context.get(AnalyticsConstants.CSV_DELIMITER_KEY, String.class);
    CSVFormat csvFormat =
        CSVFormat.DEFAULT
            .builder()
            .setHeader(getHeaders(context))
            .setDelimiter(delimiter)
            .setNullString("")
            .build();
    CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
    exportInternal(context, csvPrinter);
  }

  public <T extends GlobalStatisticRequestFilter> T getRequestFilter(
      Context context, Class<T> requestFilterClass) {
    return context.get(AnalyticsConstants.EXPORTING_REQUEST_FILTER_KEY, requestFilterClass);
  }

  @SuppressWarnings("unchecked")
  public List<SharedClientFillersDTO> getClientFillers(Context context) {
    return context.get(AnalyticsConstants.CLIENT_FILLERS_KEY, List.class);
  }
}
