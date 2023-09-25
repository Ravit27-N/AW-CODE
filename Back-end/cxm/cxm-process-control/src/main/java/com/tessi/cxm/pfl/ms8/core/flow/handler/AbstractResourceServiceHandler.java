package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.constant.Go2pdfBackgroundPosition;
import com.tessi.cxm.pfl.ms8.dto.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.ms8.util.ResourceFileUtil;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.ResourceFileType;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractResourceServiceHandler extends AbstractExecutionHandler {
  private final ResourceFileRepository resourceFileRepository;
  private final SettingFeignClient settingFeignClient;
  private final ResourceType resourceType;
  protected final FileManagerResource fileManagerResource;

  @Transactional(rollbackFor = Exception.class)
  public ExecutionState internalExecute(List<ResourceFile> resourceFiles, String flowId) {
    List<ResourceFile> entities = new ArrayList<>();
    this.validateResourceFiles(resourceFiles, resourceType, entities, flowId);

    // clear old resource before override.
    this.resourceFileRepository.deleteByFlowIdAndTypeAndIsDefault(
        flowId, resourceType.getValue(), true);

    this.resourceFileRepository.saveAll(entities);
    return ExecutionState.NEXT;
  }

  private void validateResourceFiles(
      List<ResourceFile> dtos,
      ResourceType resourceType,
      List<ResourceFile> resourceFiles,
      String flowId) {
    List<ResourceFile> entities =
        this.resourceFileRepository.findAllByFlowIdAndType(flowId, resourceType.getValue());

    if (resourceType.equals(ResourceType.BACKGROUND)) {
      var allPages = Go2pdfBackgroundPosition.ALL_PAGES.getKey();
      var resourceDtoPositionAll = this.getResourceFile(dtos, allPages);
      var resourceEntityPositionAll = this.getResourceFile(entities, allPages);
      if (resourceEntityPositionAll.isPresent() && resourceDtoPositionAll.isPresent()) {
        resourceDtoPositionAll.get().setId(resourceEntityPositionAll.get().getId());
        resourceFiles.add(resourceDtoPositionAll.get());
        return;
      }

      if (resourceDtoPositionAll.isPresent()) {
        resourceFiles.add(resourceDtoPositionAll.get());
        this.resourceFileRepository.deleteByFlowIdAndType(
            flowId, ResourceType.BACKGROUND.getValue());
        return;
      }

      if (resourceEntityPositionAll.isPresent()) {
        resourceFiles.add(resourceEntityPositionAll.get());
        return;
      }
    }

    dtos.forEach(
        dto -> {
          if (ResourceType.SIGNATURE.getValue().equalsIgnoreCase(dto.getType())) {
            entities.stream()
                .filter(
                    entity -> ResourceType.SIGNATURE.getValue().equalsIgnoreCase(entity.getType()))
                .findFirst()
                .ifPresent(signature -> dto.setId(signature.getId()));
          } else {
            this.getResourceFile(entities, dto.getPosition())
                .ifPresent(entity -> dto.setId(entity.getId()));
          }
        });
    resourceFiles.addAll(dtos);
  }

  private Optional<ResourceFile> getResourceFile(
      List<ResourceFile> resourceFiles, String position) {
    return resourceFiles.stream()
        .filter(resource -> resource.getPosition().equalsIgnoreCase(position))
        .findFirst();
  }

  protected ResourceFile getResourceMapping(
      String resourceFileUrl,
      String position,
      String flowId,
      Long ownerId,
      ExecutionContext context) {
    ResourceFile resourceFile = new ResourceFile();
    final String fileId = FilenameUtils.getBaseName(resourceFileUrl);
    resourceFile.setPosition(position);
    resourceFile.setExtension(FilenameUtils.getExtension(resourceFileUrl));
    resourceFile.setOwnerId(ownerId);
    resourceFile.setFlowId(flowId);
    resourceFile.setFileId(fileId);
    resourceFile.setOriginalName(FilenameUtils.getName(resourceFileUrl));
    resourceFile.setSource(ResourceFileType.LIBRARY.getValue());
    resourceFile.setType(resourceType.getValue());
    resourceFile.setDefault(true);
    this.setResourceLabel(resourceFile, context);
    return resourceFile;
  }

  private void setResourceLabel(ResourceFile resourceFile, ExecutionContext context) {
    try {
      ResourceLibraryDto resource =
          settingFeignClient.getResource(
              resourceFile.getFileId(),
              this.getToken(context, FlowTreatmentConstants.BEARER_TOKEN));
      resourceFile.setOriginalName(resource.getLabel());
      resourceFile.setNumberOfPages(resource.getPageNumber());
    } catch (Exception exception) {
      log.error("{0}", exception);
    }
  }

  protected boolean checkFileIsPresent(String filename) {
    return ResourceFileUtil.isFilePresent(filename, resourceType);
  }

  protected String resolveResourcePath(String resource, ExecutionContext context) {
    if (!resource.startsWith(
        ResourceFileUtil.getFileManagerStoragePath(context, this.fileManagerResource))) {
      return Paths.get(
              ResourceFileUtil.getFileManagerStoragePath(context, this.fileManagerResource))
          .resolve(resource)
          .toString();
    }
    return resource;
  }
}
