package com.innovationandtrust.project.service;

import com.innovationandtrust.project.constant.ProjectStepIndexConstant;
import com.innovationandtrust.project.model.dto.ProjectDTO;
import com.innovationandtrust.project.model.dto.ProjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignatureService {
  private final ProjectService projectService;

  public SignatureService(ProjectService projectService) {
    this.projectService = projectService;
  }

  /**
   * Update a project record.
   *
   * @param projectRequest project data will validate each step
   * @return updated record of ProjectDTO
   */
  public ProjectDTO updateProjectAndSendProcess(ProjectRequest projectRequest) {
    String signatoryKey = "`signatories`";
    String detailsKey = "`project detail`";
    String documentDetailsKey = "`document details`";

    switch (projectRequest.getStep()) {
      case ProjectStepIndexConstant.STEP2 -> this.projectService.saveProjectStep2(
          projectRequest, signatoryKey);
      case ProjectStepIndexConstant.STEP3 -> this.projectService.saveProjectStep3(
          projectRequest, documentDetailsKey);
      case ProjectStepIndexConstant.STEP4 -> {
        this.projectService.saveProjectStep4(projectRequest, detailsKey);
        this.projectService.requestSignProcess(projectRequest.getId(), projectRequest);
      }
      default -> throw new IllegalStateException("Unexpected value: " + projectRequest.getStep());
    }

    return this.projectService.findById(projectRequest.getId());
  }
}
