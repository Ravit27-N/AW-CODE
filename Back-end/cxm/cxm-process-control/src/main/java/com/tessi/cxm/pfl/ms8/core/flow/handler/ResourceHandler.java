package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import java.util.List;

public interface ResourceHandler {
  void addContext(ExecutionContext context, List<ResourceFile> resourceFiles);
}
