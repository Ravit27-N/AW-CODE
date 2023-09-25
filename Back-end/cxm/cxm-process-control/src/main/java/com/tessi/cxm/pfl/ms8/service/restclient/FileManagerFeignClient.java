package com.tessi.cxm.pfl.ms8.service.restclient;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.FileManagerRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FileManagerResponse;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(FeignClientConstants.CXM_FILE_MANAGER)
public interface FileManagerFeignClient extends FileManagerResource {

  @PostMapping("/api/v1/file-manager")
  FileManagerResponse removeFile(@RequestBody FileManagerRequest requestDto);
}
