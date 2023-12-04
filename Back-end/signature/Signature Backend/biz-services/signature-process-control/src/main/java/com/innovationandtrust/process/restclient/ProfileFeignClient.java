package com.innovationandtrust.process.restclient;

import com.innovationandtrust.share.model.profile.UserCompany;
import com.innovationandtrust.share.model.user.User;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "signature-profile",
    url = "${signature.feign-client.clients.profile-url}",
    path = "${signature.feign-client.contexts.profile-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface ProfileFeignClient {

  @GetMapping("/v1/users/{id}")
  User findUserById(@PathVariable("id") Long id);

  @GetMapping("/v1/users/{id}/company")
  UserCompany findCompanyUserById(@PathVariable("id") Long id);

  @GetMapping("/v1/users/corporate-role/{companyId}")
  Optional<User> getActiveUserByRole(
      @PathVariable("companyId") Long companyId, @RequestParam("role") String role);

  @GetMapping("/v1/users/{id}/info")
  User getUserInfo(@PathVariable("id") Long id);

  @GetMapping("/v1/users/optional/{id}")
  Optional<User> findByIdOptional(@PathVariable("id") Long id);

  @GetMapping("/v1/users/ids/")
  List<User> getProjectOwners(@RequestParam("ids") List<String> ids);

  @GetMapping("/v1/users/end-user")
  User getOwnInfo();
}
