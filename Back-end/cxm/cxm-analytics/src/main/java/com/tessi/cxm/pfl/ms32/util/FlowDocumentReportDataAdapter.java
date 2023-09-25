package com.tessi.cxm.pfl.ms32.util;

import com.google.common.util.concurrent.AtomicDouble;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentReportData;
import java.util.concurrent.atomic.AtomicLong;

public class FlowDocumentReportDataAdapter {

  private final AtomicLong totalSubChannel = new AtomicLong(0);
  private final AtomicLong totalVolume = new AtomicLong(0);
  private final AtomicLong totalToValidate = new AtomicLong(0);
  private final AtomicLong totalScheduled = new AtomicLong(0);
  private final AtomicLong totalInProgress = new AtomicLong(0);
  private final AtomicLong totalOther = new AtomicLong(0);
  private final AtomicDouble totalCompletedPercentage = new AtomicDouble(0);

  public void addingPerSubChannel(FlowDocumentReportData flowDocumentReportData) {
    totalSubChannel.getAndAdd(1);
    totalVolume.getAndAdd(flowDocumentReportData.getVolume());
    totalToValidate.getAndAdd(flowDocumentReportData.getToValidate());
    totalScheduled.getAndAdd(flowDocumentReportData.getScheduled());
    totalInProgress.getAndAdd(flowDocumentReportData.getInProgress());
    totalOther.getAndAdd(flowDocumentReportData.getOther());
    totalCompletedPercentage.getAndAdd(flowDocumentReportData.getCompletedPercentage());
  }

  public FlowDocumentReportData getTotalAllSubChannel() {
    double totalCompletedPercent = totalCompletedPercentage.get() / totalSubChannel.get();
    return FlowDocumentReportData.builder()
        .subChannel("TOTAL")
        .volume(totalVolume.get())
        .toValidate(totalToValidate.get())
        .scheduled(totalScheduled.get())
        .inProgress(totalInProgress.get())
        .other(totalOther.get())
        .completedPercentage(totalCompletedPercent)
        .build();
  }
}
