package com.tessi.cxm.pfl.ms32.repository;

import com.tessi.cxm.pfl.ms32.dto.DocumentTotalDto;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.ProductionDetails;
import java.util.List;

public interface CustomFlowTraceabilityReportRepository {

  List<ProductionDetails> reportProductionDetails(StatisticRequestFilter requestFilter);

  List<DocumentTotalDto> reportDocument(StatisticRequestFilter requestFilter);
}
