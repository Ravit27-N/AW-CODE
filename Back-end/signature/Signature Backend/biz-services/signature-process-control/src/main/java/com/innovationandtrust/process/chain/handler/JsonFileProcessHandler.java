package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Handling a process of creation, modification and reading json file of the process.
 *
 * @since 17 May 2023
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonFileProcessHandler extends AbstractExecutionHandler {

  public static final String JSON_EXTENSION = "json";

  public static final String FILE_CONTROL_PATH = "file_control";

  private final ProjectFeignClient projectFeignClient;

  private final FileProvider fileProvider;

  private final KeycloakProvider keycloakProvider;

  private final ProfileFeignClient profileFeignClient;

  private final CorporateProfileFeignClient corporateProfileFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);

    var filename = this.getFilename(project.getFlowId());
    var action =
        context
            .find(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.class)
            .orElse(JsonFileProcessAction.READ);
    switch (action) {
      case CREATE -> this.fileProvider.writeJson(project, filename, FILE_CONTROL_PATH);
      case UPDATE -> this.fileProvider.updateJson(
          project, filename, FILE_CONTROL_PATH, Project.class);
      default -> readJson(context, filename);
    }

    return ExecutionState.NEXT;
  }

  private void readJson(ExecutionContext context, String filename) {
    context.put(
        SignProcessConstant.PROJECT_KEY,
        this.fileProvider.readJson(filename, FILE_CONTROL_PATH, Project.class));
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);

    // Check if company info is null
    ProcessControlUtils.checkCompanyInfo(project.getCorporateInfo(), project.getFlowId());

    this.setExchangeToken(project);

    log.info("To validate project setting");
    this.validateProjectLevel(project);
  }

  private String getFilename(String flowId) {
    return String.format("%s.%s", flowId, JSON_EXTENSION);
  }

  /** To provide the valid company uuid for facade */
  private void setExchangeToken(Project project) {
    AtomicReference<String> userUuid = new AtomicReference<>(project.getUserKeycloakId());

    if (Objects.nonNull(userUuid.get())) {
      this.checkKeycloakUser(userUuid);
    } else {
      // This checking is for old project before 25/08/2023
      log.info("This project {} has no property userKeycloakId...", project.getFlowId());
      log.info("Getting project creator with id: {} ...", project.getCreatedBy());
      try {
        var user = this.profileFeignClient.findUserById(project.getCreatedBy());
        userUuid.set(String.valueOf(user.getUserEntityId()));
      } catch (Exception e) {
        log.error("This user was disabled...", e);
        log.info("This process will use technical user token.");
      }
    }

    // This userUuid use for exchanging token
    AuthenticationUtils.setUserUuid(userUuid.get());
  }

  // Prevent user was disabled in keycloak
  private void checkKeycloakUser(AtomicReference<String> userUuid) {
    log.info("Getting project creator with id: {} from keycloak", userUuid);
    this.keycloakProvider
        .getUserInfo(userUuid.get())
        .ifPresent(
            keycloakUser -> {
              var user = keycloakUser.getSystemUser();

              if (keycloakUser.isEnabled()) {
                userUuid.set(keycloakUser.getId());
                return;
              }

              if (StringUtils.hasText(user.getCorporateId())) {
                log.info("This project creator was disabled...");
                log.info("Getting corporate admin information...");
                this.keycloakProvider
                    .getUserInfo(user.getCorporateId())
                    .ifPresent(
                        corporateUser -> {
                          if (keycloakUser.isEnabled()) {
                            userUuid.set(corporateUser.getId());
                          } else {
                            getAnotherCorporate(userUuid, user.getCompany().getId());
                          }
                        });
              } else {
                getAnotherCorporate(userUuid, user.getCompany().getId());
              }
            });
  }

  private void getAnotherCorporate(AtomicReference<String> userUuid, Long companyId) {
    log.info("This corporate was disabled...");
    log.info("Getting another corporate admin info...");
    if (Objects.nonNull(companyId)) {
      this.profileFeignClient
          .getActiveUserByRole(companyId, RoleConstant.CORPORATE_ADMIN)
          .ifPresentOrElse(
              corporateAdmin -> userUuid.set(String.valueOf(corporateAdmin.getUserEntityId())),
              () ->
                  log.warn(
                      "Non-corporate user active in company, this process will use technical user token."));
    }
  }

  private void validateProjectLevel(Project project) {

    // Keep checking null
    // Because no requirement to create project from xml with signature level
    if (Objects.nonNull(project.getSetting())) {
      var companyUuid = project.getCorporateInfo().getCompanyUuid();
      if (Objects.isNull(companyUuid)) {
        log.info("Getting corporate info from corporate profile service...");
        var corporateInfo =
            this.corporateProfileFeignClient.findCorporateInfo(project.getCreatedBy());
        companyUuid = corporateInfo.getCompanyUuid();
      }

      log.info(
          "Getting company setting:{} from company:{}...",
          project.getSignatureLevel(),
          companyUuid);
      var companySetting =
          this.corporateProfileFeignClient.getCompanySettingByLevel(
              companyUuid, project.getSignatureLevel());

      // It will throw exception, If super-admin make any changed on settings
      // and project created have chosen options that unavailable in current settings
      log.info("Validating project signature level");
      CompanySettingUtils.validateSettingOption(companySetting, project.getSetting());
    }
  }
}
