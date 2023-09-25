package com.tessi.cxm.pfl.ms3.service.consumer.campaign;

import com.tessi.cxm.pfl.ms3.repository.FlowCampaignDetailRepository;
import com.tessi.cxm.pfl.ms3.service.consumer.AbstractFlowTraceabilityConsumer;
import com.tessi.cxm.pfl.shared.model.kafka.FlowCampaignDetailsModel;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import java.sql.SQLException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handling process of consuming data from <strong>cxm-process-control</strong> after
 * <strong>cxm-composition</strong> step.
 *
 * @author Sokhour LACH
 * @version 1.17.0
 * @see KafkaUtils#UPDATE_FLOW_CAMPAIGN_HTML_TEMPLATE_AFTER_COMPOSITION_TOPIC
 * @since 10 Jan 2023
 */
@Slf4j
@Component("updateFlowCampaignHtmlTemplate")
@RequiredArgsConstructor
public class UpdateFlowCampaignDetailsListener
    extends AbstractFlowTraceabilityConsumer<FlowCampaignDetailsModel> {

  private final FlowCampaignDetailRepository flowCampaignDetailRepository;

  @Transactional(rollbackOn = SQLException.class)
  @Override
  public void accept(FlowCampaignDetailsModel payload) {
    try {
      log.info("<< UPDATE_FLOW_CAMPAIGN_DETAIL_HTML_TEMPLATE_AFTER_COMPOSITION >>");
      var flowTraceability = this.getFlowTraceabilityByFileId(payload.getFileId());
      flowCampaignDetailRepository.updateFlowCampaignDetail(
          flowTraceability.getId(), payload.getHtmlTemplate());
    } catch (Exception ex) {
      log.error("Failed to update campaign details", ex);
    }
  }
}
