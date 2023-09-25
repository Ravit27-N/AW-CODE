package com.tessi.cxm.pfl.ms32.service.specification;

import com.tessi.cxm.pfl.ms32.constant.DepositModeResponseDto;
import com.tessi.cxm.pfl.ms32.dto.DocumentSummary;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.service.AbstractStatisticService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostalStatisticService extends AbstractStatisticService {
  public PostalStatisticService(
      FlowTraceabilityReportRepository flowTraceabilityReportRepository,
      SettingFeignClient settingFeignClient,
      ProfileFeignClient profileFeignClient) {
    super(flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
  }

  public DocumentSummary calculateProductionDeliveredSummary(
      StatisticRequestFilter requestFilter) {
    this.validateAndResolve(requestFilter);
    long totalPND = this.getTotalPND(requestFilter);
    DocumentSummary documentProcess = this.getDocumentProcess(requestFilter);
    long totalPostalDocumentProcess =
        ObjectUtils.defaultIfNull(documentProcess.get(FlowDocumentChannelConstant.POSTAL), 0L);
    DocumentSummary documentSummary = new DocumentSummary();
    documentSummary.put("PND", totalPND);
    documentSummary.put("Non_PND", totalPostalDocumentProcess - totalPND);
    return documentSummary;
  }

  public List<DepositModeResponseDto> calculateNonDistributedDocumentDetailsSummary(
      StatisticRequestFilter requestFilter) {
    requestFilter.setChannels(List.of(FlowDocumentChannelConstant.POSTAL));
    this.validateAndResolve(requestFilter);
    List<String> subStatus = FlowDocumentStatus.getPNDStatus();
    requestFilter.setSubStatuses(subStatus);
    DocumentSummary totalDocumentSummaryPND = this.getTotalDocumentSummaryPND(requestFilter);
    return getDepositModeResponse(subStatus, totalDocumentSummaryPND);
  }
}
