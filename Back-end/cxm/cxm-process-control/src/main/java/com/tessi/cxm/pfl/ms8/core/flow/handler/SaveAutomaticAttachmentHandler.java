package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.constant.Go2pdfAttachmentPosition;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.ms8.util.ResourceFileUtil;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class SaveAutomaticAttachmentHandler extends AbstractResourceServiceHandler {

  private final ResourceFileRepository resourceFileRepository;

  public SaveAutomaticAttachmentHandler(
      ResourceFileRepository resourceFileRepository,
      SettingFeignClient settingFeignClient,
      FileManagerResource fileManagerResource) {
    super(resourceFileRepository, settingFeignClient, ResourceType.ATTACHMENT, fileManagerResource);
    this.resourceFileRepository = resourceFileRepository;
  }

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final Attachments attachments =
        context.get(ProcessControlConstants.ATTACHMENT_DTO, Attachments.class);
    if (ObjectUtils.isEmpty(attachments)) {
      return ExecutionState.NEXT;
    }
    final String configPath =
        ResourceFileUtil.getFileManagerStoragePath(context, this.fileManagerResource);
    final DepositedFlowLaunchRequest depositedFlowLaunchRequest =
        context.get(
            FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, DepositedFlowLaunchRequest.class);
    final String flowId = depositedFlowLaunchRequest.getUuid();
    final Long ownerId = context.get(FlowTreatmentConstants.OWNER_ID, Long.class);
    List<ResourceFile> resourceFiles = new ArrayList<>();

    if (StringUtils.isNotBlank(attachments.getAttachment1())
        && checkFileIsPresent(attachments.getAttachment1())) {
      resourceFiles.add(
          getResourceMapping(
              attachments.getAttachment1(),
              Go2pdfAttachmentPosition.FIRST_POSITION.getKey(),
              flowId,
              ownerId,
              context));
    }
    if (StringUtils.isNotBlank(attachments.getAttachment2())
        && checkFileIsPresent(attachments.getAttachment2())) {
      resourceFiles.add(
          getResourceMapping(
              attachments.getAttachment2(),
              Go2pdfAttachmentPosition.SECOND_POSITION.getKey(),
              flowId,
              ownerId,
              context));
    }
    if (StringUtils.isNotBlank(attachments.getAttachment3())
        && checkFileIsPresent(attachments.getAttachment3())) {
      resourceFiles.add(
          getResourceMapping(
              attachments.getAttachment3(),
              Go2pdfAttachmentPosition.THIRD_POSITION.getKey(),
              flowId,
              ownerId,
              context));
    }
    if (StringUtils.isNotBlank(attachments.getAttachment4())
        && checkFileIsPresent(attachments.getAttachment4())) {
      resourceFiles.add(
          getResourceMapping(
              attachments.getAttachment4(),
              Go2pdfAttachmentPosition.FOURTH_POSITION.getKey(),
              flowId,
              ownerId,
              context));
    }
    if (StringUtils.isNotBlank(attachments.getAttachment5())
        && checkFileIsPresent(attachments.getAttachment5())) {
      resourceFiles.add(
          getResourceMapping(
              attachments.getAttachment5(),
              Go2pdfAttachmentPosition.FIFTH_POSITION.getKey(),
              flowId,
              ownerId,
              context));
    }
    return internalExecute(resourceFiles, flowId);
  }
}
