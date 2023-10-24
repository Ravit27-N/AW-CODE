package com.innovationandtrust.profile.service.restclient;

import com.innovationandtrust.profile.model.dto.CompanyEmployee;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeResponseDto;
import com.innovationandtrust.share.model.profile.CompanyIdListDTO;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "employee",
    url = "${signature.feign-client.clients.corporate-profile-url}",
    path = "${signature.feign-client.contexts.corporate-profile-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface EmployeeFeignClient {

  @PostMapping("/v1/employees")
  EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO);

  @PutMapping("/v1/employees")
  EmployeeDTO updateEmployee(@RequestBody @Valid EmployeeDTO employeeDTO);

  @PutMapping("/v1/employees/{id}")
  void deleteUser(@PathVariable Long id);

  @PostMapping("/v1/employees/company")
  List<CompanyEmployee> listByCompany(@RequestBody CompanyIdListDTO companyIdListDTO);

  @GetMapping("/v1/employees/user/ids")
  List<EmployeeResponseDto> listEmployeesByUserIds(@RequestParam("userIds") List<Long> userIds);

  @GetMapping("/v1/employees/user")
  EmployeeDTO findByUserId(@RequestParam Long id);
}
