package com.innovationandtrust.profile.service.restclient;

import com.innovationandtrust.share.model.corporateprofile.CompanyDetailDTO;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "company-detail",
    url = "${signature.feign-client.clients.corporate-profile-url}",
    path = "${signature.feign-client.contexts.corporate-profile-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface CompanyDetailFeignClient {
  @PostMapping("/v1/company/details")
  void saveCompanyDetails(@RequestBody CompanyDetailDTO companyDetail);

  @PutMapping("/v1/company/details")
  void updateCompanyDetails(@RequestBody CompanyDetailDTO companyDetail);
}
