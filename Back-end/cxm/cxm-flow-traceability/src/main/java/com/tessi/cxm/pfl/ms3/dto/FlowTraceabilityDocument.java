package com.tessi.cxm.pfl.ms3.dto;

import org.springframework.beans.factory.annotation.Value;

/**
 * Handle some field in flowTraceability for documents.
 *
 * @since 11/03/2021
 * @author Piseth Khon
 */
public interface FlowTraceabilityDocument {
  String getFlowName();

  String getChannel();

  String getSubChannel();

  @Value("#{target.flowTraceabilityDetails.pageError}")
  int getPageError();

  @Value("#{target.flowTraceabilityDetails.pageCount}")
  int getPageCount();

  @Value("#{target.flowTraceabilityDetails.pageProcessed}")
  int getPageProcessed();
}
