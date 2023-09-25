package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.constant.Go2pdfBackgroundPosition;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaveAutomaticBackgroundHandler extends AbstractResourceServiceHandler {

  public SaveAutomaticBackgroundHandler(
      ResourceFileRepository resourceFileRepository,
      SettingFeignClient settingFeignClient,
      FileManagerResource fileManagerResource) {
    super(resourceFileRepository, settingFeignClient, ResourceType.BACKGROUND, fileManagerResource);
  }
  /**
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   * @return ExecutionState
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final BackgroundPage backgroundPage =
        context.get(ProcessControlConstants.BACKGROUND_DTO, BackgroundPage.class);
    if (ObjectUtils.isEmpty(backgroundPage)) {
      return ExecutionState.NEXT;
    }

    final DepositedFlowLaunchRequest depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    final String flowId = depositedFlowLaunchRequest.getUuid();
    final Long ownerId = context.get(FlowTreatmentConstants.OWNER_ID, Long.class);
    List<ResourceFile> resourceFiles = new ArrayList<>();
    if (StringUtils.isNotBlank(backgroundPage.getPositionFirst())
        && checkFileIsPresent(backgroundPage.getBackgroundFirst())) {
      final String positionKey =
          Go2pdfBackgroundPosition.getKeyByValue(backgroundPage.getPositionFirst()).getKey();
      resourceFiles.add(
          getResourceMapping(
              backgroundPage.getBackgroundFirst(), positionKey, flowId, ownerId, context));
    }
    if (StringUtils.isNotBlank(backgroundPage.getPosition())
        && checkFileIsPresent(backgroundPage.getBackground())) {
      final String positionKey =
          Go2pdfBackgroundPosition.getKeyByValue(backgroundPage.getPosition()).getKey();
      resourceFiles.add(
          getResourceMapping(
              backgroundPage.getBackground(), positionKey, flowId, ownerId, context));
    }
    if (StringUtils.isNotBlank(backgroundPage.getPositionLast())
        && checkFileIsPresent(backgroundPage.getPositionLast())) {
      final String positionKey =
          Go2pdfBackgroundPosition.getKeyByValue(backgroundPage.getPositionLast()).getKey();
      resourceFiles.add(
          getResourceMapping(
              backgroundPage.getBackgroundLast(), positionKey, flowId, ownerId, context));
    }
    return internalExecute(resourceFiles, flowId);
  }
}
