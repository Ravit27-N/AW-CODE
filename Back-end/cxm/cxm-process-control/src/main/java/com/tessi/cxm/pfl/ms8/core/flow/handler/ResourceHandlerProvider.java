package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ResourceHandlerProvider {

  private final FileManagerResource fileManagerResource;

  public ResourceHandler getResourceHandler(String resourceType) {
    if (ResourceType.BACKGROUND.getValue().equalsIgnoreCase(resourceType)) {
      return new BackgroundResourceHandler(fileManagerResource);
    } else if (ResourceType.ATTACHMENT.getValue().equalsIgnoreCase(resourceType)) {
      return new AttachmentResourceHandler(fileManagerResource);
    } else if (ResourceType.SIGNATURE.getValue().equalsIgnoreCase(resourceType)) {
      return new SignatureResourceHandler(fileManagerResource);
    }

    throw new NullPointerException("Resource type not found");
  }
}
