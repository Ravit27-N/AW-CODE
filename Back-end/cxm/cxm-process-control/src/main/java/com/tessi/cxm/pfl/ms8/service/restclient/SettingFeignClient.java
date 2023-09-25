package com.tessi.cxm.pfl.ms8.service.restclient;

import com.tessi.cxm.pfl.ms8.dto.ResourceLibraryDto;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(FeignClientConstants.CXM_SETTING)
public interface SettingFeignClient
    extends com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient {

  @GetMapping("/api/v1/resources/{fileId}/resource")
  ResourceLibraryDto getResource(
      @PathVariable("fileId") String fileId,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @GetMapping("/api/v1/resources/fileIds")
  List<ResourceLibraryDto> getAllByFileIds(
      @RequestParam("fileIds") List<String> fileIds,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);
}
