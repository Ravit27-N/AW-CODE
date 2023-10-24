package com.innovationandtrust.corporate.service;

import com.innovationandtrust.corporate.service.restclient.ProjectFeignClient;
import com.innovationandtrust.share.model.corporateprofile.DashboardDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DashboardService {

  private final ProjectFeignClient projectFeignClient;

  private final EmployeeService employeeService;

  public DashboardService(ProjectFeignClient projectFeignClient, EmployeeService employeeService) {
    this.projectFeignClient = projectFeignClient;
    this.employeeService = employeeService;
  }

  public DashboardDTO findAll(
      Long companyId, Long businessUnitId, String startDate, String endDate) {

    var employeesIds =
        this.employeeService
            .getEmployees(Pageable.unpaged(), businessUnitId, companyId, "", "", true, null)
            .stream()
            .map(EmployeeDTO::getUserId)
            .filter(Objects::nonNull)
            .toList();

    if (!employeesIds.isEmpty())
      return this.projectFeignClient.getUsersProjects(employeesIds, startDate, endDate);
    else return new DashboardDTO();
  }
}
