package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.shared.core.Context;
import java.io.IOException;
import java.util.List;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public abstract class PageableProductionDataCSVExporter<T> extends ProductionDataCSVExporter {

  protected abstract Page<T> readByPageable(Context context, Pageable pageable);

  protected abstract void doExportInternal(List<T> datas, CSVPrinter csvPrinter) throws IOException;

  @Override
  protected void exportInternal(Context context, CSVPrinter csvPrinter) throws IOException {
    var pageSize = context.get(AnalyticsConstants.PAGE_SIZE_KEY, Integer.class);
    var pageRequest = PageRequest.of(0, pageSize);
    var hasMoreRecord = false;
    do {
      Page<T> pagedDatas = readByPageable(context, pageRequest);
      // Write
      if (pagedDatas != null && pagedDatas.hasContent()) {
        doExportInternal(pagedDatas.getContent(), csvPrinter);
      }
      if (pagedDatas != null){
        pageRequest = PageRequest.of(pagedDatas.getNumber() + 1, pageSize);
        hasMoreRecord = pagedDatas.hasNext();
      }
    } while (hasMoreRecord);
  }
}
