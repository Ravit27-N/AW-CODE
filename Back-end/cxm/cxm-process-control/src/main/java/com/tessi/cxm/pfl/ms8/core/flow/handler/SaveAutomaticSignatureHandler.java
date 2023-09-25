package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SaveAutomaticSignatureHandler extends AbstractResourceServiceHandler {

  public SaveAutomaticSignatureHandler(
      ResourceFileRepository resourceFileRepository,
      SettingFeignClient settingFeignClient,
      FileManagerResource fileManagerResource) {
    super(resourceFileRepository, settingFeignClient, ResourceType.SIGNATURE, fileManagerResource);
  }

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    List<ResourceFile> resourceFiles = new ArrayList<>();
    final var defaultSignature =
        context.get(ProcessControlConstants.DEFAULT_SIGNATURE, String.class);
    final DepositedFlowLaunchRequest depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    final String flowId = depositedFlowLaunchRequest.getUuid();
    final Long ownerId = context.get(FlowTreatmentConstants.OWNER_ID, Long.class);
    if (StringUtils.hasText(defaultSignature)) {
      var defaultSignatureFullPath = this.resolveResourcePath(defaultSignature, context);
      resourceFiles.add(
          this.getResourceMapping(
              defaultSignatureFullPath, null, flowId, ownerId, context));
    }

    return internalExecute(resourceFiles, flowId);
  }
}
