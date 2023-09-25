package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.ms8.util.ResourceFileUtil;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SignatureResourceHandler implements ResourceHandler {

  private final FileManagerResource fileManagerResource;

  @Override
  public void addContext(ExecutionContext context, List<ResourceFile> resourceFiles) {
    if (!resourceFiles.isEmpty()) {
      final ResourceFile resourceFile = resourceFiles.get(0);
      var fileManagerStoragePath =
          Paths.get(ResourceFileUtil.getFileManagerStoragePath(context, this.fileManagerResource));
      var signature =
          fileManagerStoragePath
              .resolve(resourceFile.getFileId().concat("." + resourceFile.getExtension()))
              .toString();
      context.put(ProcessControlConstants.SIGNATURE_RESOURCE, signature);
    }
  }
}
