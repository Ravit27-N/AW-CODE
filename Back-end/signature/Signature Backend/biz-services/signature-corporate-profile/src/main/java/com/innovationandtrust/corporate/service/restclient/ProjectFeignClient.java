package com.innovationandtrust.corporate.service.restclient;

import com.innovationandtrust.share.model.corporateprofile.DashboardDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "project",
    url = "${signature.feign-client.clients.project-url}",
    path = "${signature.feign-client.contexts.project-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface ProjectFeignClient {
  @PostMapping("/v1/projects/count/employees")
  List<EmployeeDTO> countEmployeesProject(
      @RequestBody List<EmployeeDTO> employeeDTOS,
      @RequestParam String startDate,
      @RequestParam String endDate);

  @PostMapping("/v1/projects/dashboard")
  DashboardDTO getUsersProjects(
      @RequestBody List<Long> employeeIds,
      @RequestParam String startDate,
      @RequestParam String endDate);
}
