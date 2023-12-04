package com.innovationandtrust.process.chain.handler.sign;

import static com.innovationandtrust.utils.commons.CommonUsages.convertToList;

import com.innovationandtrust.process.chain.execution.sign.SigningProcessExecutionManager;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.SigningProcessDto;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
public class MultiSigningProcessHandler extends AbstractExecutionHandler {
  private final SigningProcessExecutionManager signingProcessExecutionManager;
  private final JsonFileProcessHandler jsonFileProcessHandler;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final ProjectFeignClient projectFeignClient;

  public MultiSigningProcessHandler(
      SigningProcessExecutionManager signingProcessExecutionManager,
      JsonFileProcessHandler jsonFileProcessHandler,
      CorporateProfileFeignClient corporateProfileFeignClient,
      ProjectFeignClient projectFeignClient) {
    this.signingProcessExecutionManager = signingProcessExecutionManager;
    this.jsonFileProcessHandler = jsonFileProcessHandler;
    this.corporateProfileFeignClient = corporateProfileFeignClient;
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var requests =
        convertToList(
            context.get(SignProcessConstant.MULTI_SIGNING_PROJECTS), SigningProcessDto.class);

    // number 20 refers to 2 pages selected
    if (requests.size() < 20) {
      this.parallelProcess(context, requests);
    } else {
      // Process is execute in background
      this.setStatuses(requests);
      Executors.newSingleThreadExecutor().execute(() -> this.parallelProcess(context, requests));
    }

    return ExecutionState.END;
  }

  private void parallelProcess(ExecutionContext context, List<SigningProcessDto> requests) {
    this.jsonFileProcessHandler.execute(context);
    var projects = convertToList(context.get(SignProcessConstant.PROJECTS), Project.class);
    this.validateCompaniesSetting(projects);

    ProcessControlUtils.validateAndGetPhone(requests, projects);

    // Update all signatories that will be in signing
    this.projectFeignClient.updateInSigningStatus(
        requests.stream().map(SigningProcessDto::getUuid).toList());

    // For the same date of documents signatures
    context.put(SignProcessConstant.SIGNED_DATE, Date.from(Instant.now()));

    requests.parallelStream()
        .forEach(
            (SigningProcessDto request) -> {
              var message =
                  String.format(
                      "project:%s participant:%s", request.getFlowId(), request.getUuid());
              try {
                this.signingProcessExecutionManager.execute(
                    ProcessControlUtils.getProject(request.getFlowId(), request.getUuid()));
                log.info("[Successfully] Signing project {}", message);
                this.setStatus(request, ProcessStatus.SUCCESSFUL, message);
              } catch (Exception exception) {
                log.error("[Error] While process signing {}", message, exception);
                this.setStatus(
                    request,
                    ProcessStatus.FAIL,
                    String.format("%s Reason: %s.", message, exception.getMessage()));
              }
            });
  }

  private void validateCompaniesSetting(List<Project> projects) {
    List<Long> usersId = projects.stream().map(Project::getCreatedBy).toList();
    var signatureLevel =
        !CollectionUtils.isEmpty(projects)
            ? projects.get(0).getSignatureLevel()
            : SignatureSettingLevel.SIMPLE.name();
    var companySettings =
        this.corporateProfileFeignClient.getCompaniesSettings(usersId, signatureLevel);
    projects.forEach(
        (Project project) ->
            companySettings.stream()
                .filter(
                    setting ->
                        Objects.equals(
                            project.getCorporateInfo().getCompanyUuid(), setting.getCompanyUuid()))
                .findFirst()
                .ifPresent(
                    (CompanySettingDto setting) -> {
                      log.info(
                          "Validating project signature level flowId: {}", project.getFlowId());
                      CompanySettingUtils.validateSettingOption(setting, project.getSetting());
                    }));
  }

  private void setStatuses(List<SigningProcessDto> requests) {
    requests.parallelStream()
        .forEach(
            request ->
                this.setStatus(
                    request, ProcessStatus.PROCESSING, "Project processing in background."));
  }

  private void setStatus(SigningProcessDto request, String status, String message) {
    request.setStatus(status);
    request.setMessage(message);
  }
}
