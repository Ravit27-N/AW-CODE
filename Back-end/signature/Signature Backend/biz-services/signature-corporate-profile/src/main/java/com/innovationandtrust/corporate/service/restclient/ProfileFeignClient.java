package com.innovationandtrust.corporate.service.restclient;

import com.innovationandtrust.corporate.model.dto.CompanyDto;
import com.innovationandtrust.share.model.profile.Company;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.share.model.profile.LoginHistoryDto;
import com.innovationandtrust.share.model.profile.UserCompany;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import java.util.List;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "signature-profile",
    url = "${signature.feign-client.clients.profile-url}",
    path = "${signature.feign-client.contexts.profile-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface ProfileFeignClient {
  @GetMapping("/companies/name")
  Optional<Company> findCompanyByName(@RequestParam("name") String name);

  @GetMapping("/companies/uuid")
  Optional<Company> findCompanyByUuid(@RequestParam("uuid") String uuid);

  @GetMapping("/v1/companies/corporate/{id}")
  Optional<Company> findCompanyByCorporateId(@PathVariable("id") Long id);

  @GetMapping("/v1/companies/{id}")
  Optional<Company> findCompanyById(@PathVariable("id") Long id);

  @PutMapping("/v1/companies")
  void update(@RequestBody CompanyDto dto);

  @GetMapping("/v1/users/{id}")
  Optional<UserCompany> findUserById(
      @PathVariable("id") Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

  @GetMapping("v1/company/settings")
  List<CompanySettingDto> findCompanySettings(@RequestParam(value = "uuid") String companyUuid);

  @GetMapping("v1/company/settings/{uuid}")
  CompanySettingDto findCompanySetting(
      @PathVariable(value = "uuid") String companyUuid,
      @RequestParam(value = "signatureLevel") String signatureLevel);

  @PostMapping("/v1/users/role/ids")
  List<Long> getUserIdsByRole(@RequestParam("role") String role, @RequestBody List<Long> userIds);

  @PostMapping("/v1/login-history/users")
  List<LoginHistoryDto> getLoginHistoriesByUsers(@RequestBody List<Long> userIds);
}
