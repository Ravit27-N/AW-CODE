package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.DepositModeResponseDto;
import com.tessi.cxm.pfl.ms32.dto.DocumentSummary;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DigitalStatisticService extends AbstractStatisticService {
  public DigitalStatisticService(
      FlowTraceabilityReportRepository flowTraceabilityReportRepository,
      SettingFeignClient settingFeignClient,
      ProfileFeignClient profileFeignClient) {
    super(flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
  }

  public List<DepositModeResponseDto> getDistributionBySubStatus(
      StatisticRequestFilter requestFilter) {
    requestFilter.setChannels(List.of(FlowDocumentChannelConstant.DIGITAL));
    this.validateAndResolve(requestFilter);
    List<String> subStatus =
        FlowDocumentStatus.getDigitalDistributionStatus(requestFilter.getCategories().get(0));
    requestFilter.setSubStatuses(subStatus);
    DocumentSummary docSumByStatus = this.getTotalDocumentByStatus(requestFilter);
    return getDepositModeResponse(subStatus, docSumByStatus);
  }
}
