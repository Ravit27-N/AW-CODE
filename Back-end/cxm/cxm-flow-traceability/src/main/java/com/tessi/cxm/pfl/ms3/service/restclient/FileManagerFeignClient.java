package com.tessi.cxm.pfl.ms3.service.restclient;

import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(FeignClientConstants.CXM_FILE_MANAGER)
public interface FileManagerFeignClient extends FileManagerResource {

}
