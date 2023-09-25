package com.tessi.cxm.pfl.ms8.util;

import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public final class ResourceFileUtil {

  private ResourceFileUtil() {}

  public static boolean isFilePresent(
      String filename,  ResourceType resourceType) {
    if (ObjectUtils.isEmpty(resourceType)) {
      return false;
    }
    boolean isMatch = false;
    switch (resourceType) {
      case BACKGROUND:
      case ATTACHMENT:
        isMatch = FilenameUtils.isExtension(filename, "pdf");
        break;
      case SIGNATURE:
        isMatch = FilenameUtils.isExtension(filename, "png");
        break;
    }
    return isMatch;
  }

  public static String getFileManagerStoragePath(
      ExecutionContext context, FileManagerResource fileManagerResource) {
    String fileManagerStoragePath =
        context.get(ProcessControlConstants.FILE_MANAGER_STORAGE_PATH, String.class);
    if (!StringUtils.hasText(fileManagerStoragePath)) {
      fileManagerStoragePath = fileManagerResource.getConfigPath();
      context.put(ProcessControlConstants.FILE_MANAGER_STORAGE_PATH, fileManagerStoragePath);
    }
    return fileManagerStoragePath;
  }
}
