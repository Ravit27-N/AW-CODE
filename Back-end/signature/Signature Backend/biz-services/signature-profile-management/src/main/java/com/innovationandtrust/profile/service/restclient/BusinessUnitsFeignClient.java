package com.innovationandtrust.profile.service.restclient;

import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.corporateprofile.BusinessUnitDTO;
import com.innovationandtrust.share.model.corporateprofile.FolderDTO;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "business-units",
    url = "${signature.feign-client.clients.corporate-profile-url}",
    path = "${signature.feign-client.contexts.corporate-profile-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface BusinessUnitsFeignClient {

  @PostMapping("/v1/business-units")
  BusinessUnitDTO save(@Valid @RequestBody BusinessUnitDTO businessUnitDTO);

  @GetMapping("/v1/business-units/{id}")
  BusinessUnitDTO findById(@PathVariable("id") Long id);

  @GetMapping("/v1/business-units")
  EntityResponseHandler<BusinessUnitDTO> findByCompanyId(
      @RequestParam("companyId") Long companyId,
      @RequestParam(CommonParamsConstant.SORT_DIRECTION) String sortDirection);

  @GetMapping("/v1/business-units/parent/{id}")
  List<BusinessUnitDTO> findByParentId(@PathVariable("id") Long id);

  @GetMapping("/v1/folders/{id}")
  FolderDTO findFolderById(@PathVariable("id") Long id);

  @GetMapping("/v1/folders/company/{id}")
  List<FolderDTO> findByCompany(@PathVariable("id") Long companyId);

  @GetMapping("/v1/folders/users")
  List<FolderDTO> getFoldersByUsersId(@RequestParam("ids") List<Long> userIds);

  @GetMapping("/v1/business-units/user/{id}")
  BusinessUnitDTO findByUserId(@PathVariable("id") Long id);
}
