package com.innovationandtrust.project.restclient;

import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.profile.UserCompany;
import com.innovationandtrust.share.model.user.User;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import java.util.List;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** ProfileFeignClient use to communicate with profile management service. */
@FeignClient(
    value = "signature-profile",
    url = "${signature.feign-client.clients.profile-url}",
    path = "${signature.feign-client.contexts.profile-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface ProfileFeignClient {

  @GetMapping("/v1/users/{id}")
  UserCompany findUserById(@PathVariable("id") Long id);

  @GetMapping("/v1/users/company/{id}")
  Optional<UserCompany> findUserCompany(@PathVariable("id") Long id);

  @GetMapping("/v1/users/valid-user")
  boolean checkUserEmail(@RequestParam("email") String email);

  @GetMapping("/v1/users/uuid/{id}")
  User findUserByUuid(@PathVariable("id") String id);

  @GetMapping("/v1/templates/{id}")
  Template findTemplateById(@PathVariable("id") Long id);

  @GetMapping("/v1/templates/get/{id}")
  Optional<Template> getTemplateById(@PathVariable("id") Long id);

  @GetMapping("/v1/users/{id}/users-company")
  List<Long> findUserInTheSameCompany(@PathVariable("id") Long id);

  @GetMapping("/v1/users/ids")
  List<User> findUsersByUsersId(@RequestParam("ids") List<String> ids);

  @GetMapping("/v1/users/{id}/info")
  User getUserInfo(@PathVariable("id") Long id);

  @PutMapping("/v1/templates/increase-used/{id}")
  void increaseTemplateUsed(@PathVariable("id") Long id);
}
