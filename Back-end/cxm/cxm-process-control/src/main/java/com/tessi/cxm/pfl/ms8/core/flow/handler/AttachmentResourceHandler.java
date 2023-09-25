package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProduction.Attachment;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.AttachmentPosition;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AttachmentResourceHandler implements ResourceHandler {

  private final FileManagerResource fileManagerResource;

  @Override
  public void addContext(ExecutionContext context, List<ResourceFile> resourceFiles) {
    Map<String, ResourceFile> backgroundFileMap =
        resourceFiles.stream()
            .collect(
                Collectors.toMap(ResourceFile::getPosition, backgroundFile -> backgroundFile));

    Attachment attachment = new Attachment();
    Attachments attachmentDto = new Attachments();

    Arrays.stream(AttachmentPosition.values()).collect(Collectors.toList())
        .forEach(attachmentPosition -> {
          String attachmentPath = "";
          if (backgroundFileMap.containsKey(attachmentPosition.value)) {
            ResourceFile resourceFile = backgroundFileMap.get(attachmentPosition.value);
            attachmentPath = getAttachmentPath(resourceFile);
            attachment.getAttachments().add(attachmentPath);
          } else {
            attachment.getAttachments().add("");
          }

          this.mappingAttachmentDto(attachmentDto, attachmentPosition.value, attachmentPath);
        });
    populatePageSizeToContext(context, resourceFiles);
    context.put(ProcessControlConstants.FLOW_DOC_PRODUCTION_ATTACHMENT, attachment);
    context.put(ProcessControlConstants.ATTACHMENT_DTO, attachmentDto);
  }

  private void mappingAttachmentDto(Attachments attachmentDto, String position,
      String attachmentPath) {
    if (AttachmentPosition.FIRST_POSITION.value.equalsIgnoreCase(position)) {
      attachmentDto.setAttachment1(attachmentPath);
    }

    if (AttachmentPosition.SECOND_POSITION.value.equalsIgnoreCase(position)) {
      attachmentDto.setAttachment2(attachmentPath);
    }

    if (AttachmentPosition.THIRD_POSITION.value.equalsIgnoreCase(position)) {
      attachmentDto.setAttachment3(attachmentPath);
    }

    if (AttachmentPosition.FOURTH_POSITION.value.equalsIgnoreCase(position)) {
      attachmentDto.setAttachment4(attachmentPath);
    }

    if (AttachmentPosition.FIFTH_POSITION.value.equalsIgnoreCase(position)) {
      attachmentDto.setAttachment5(attachmentPath);
    }
  }

  private String getAttachmentPath(ResourceFile resourceFile) {
    final String configPath = fileManagerResource.getConfigPath();
    return Path.of(configPath)
        .resolve(resourceFile.getFileId().concat("." + resourceFile.getExtension()))
        .toString();
  }
  private void populatePageSizeToContext(ExecutionContext context, List<ResourceFile> resources) {
    int pageSize = resources.stream().mapToInt(ResourceFile::getNumberOfPages).sum();
    context.put(ProcessControlConstants.RESOURCE_PAGE_SIZE, pageSize);
  }

}
