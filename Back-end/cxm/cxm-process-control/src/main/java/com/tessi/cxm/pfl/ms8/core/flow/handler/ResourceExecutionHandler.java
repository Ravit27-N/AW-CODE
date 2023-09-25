package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ResourceExecutionHandler extends AbstractExecutionHandler {

  private final ResourceFileRepository resourceFileRepository;
  private final FileManagerResource fileManagerResource;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final String flowId =
        context
            .get(
                FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
                DepositedFlowLaunchRequest.class)
            .getUuid();

    // Get all resource by flowId.
    List<ResourceFile> resourceFiles = this.resourceFileRepository.findAllByFlowId(flowId);

    if (resourceFiles.isEmpty()) {
      return ExecutionState.NEXT;
    }

    // Group all resource file by type.
    var resourceFileGroups =
        resourceFiles.stream().collect(Collectors.groupingBy(ResourceFile::getType));

    // Add each resource file to context by type.
    resourceFileGroups.forEach(
        (type, resourceFileList) -> {
          ResourceHandlerProvider resourceHandlerProvider =
              new ResourceHandlerProvider(fileManagerResource);

          // Get concrete class of resource handler.
          ResourceHandler resourceHandler = resourceHandlerProvider.getResourceHandler(type);
          resourceHandler.addContext(context, resourceFileList);
        });
    return ExecutionState.NEXT;
  }
}
