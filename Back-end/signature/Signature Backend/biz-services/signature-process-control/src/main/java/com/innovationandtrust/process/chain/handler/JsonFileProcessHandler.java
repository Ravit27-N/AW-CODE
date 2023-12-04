package com.innovationandtrust.process.chain.handler;

import static com.innovationandtrust.process.utils.ProcessControlUtils.getFilename;
import static com.innovationandtrust.utils.commons.CommonUsages.convertToList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.PathConstant;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.SigningProcessDto;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserResponse;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
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
public class JsonFileProcessHandler extends AbstractExecutionHandler {

  private final FileProvider fileProvider;

  private final KeycloakProvider keycloakProvider;

  private final ProfileFeignClient profileFeignClient;

  /**
   * Contractor of the class.
   *
   * @param fileProvider signature file library
   * @param keycloakProvider signature keycloak ser
   * @param profileFeignClient open feign endpoints of profile-management service
   */
  public JsonFileProcessHandler(
      FileProvider fileProvider,
      KeycloakProvider keycloakProvider,
      ProfileFeignClient profileFeignClient) {
    this.fileProvider = fileProvider;
    this.keycloakProvider = keycloakProvider;
    this.profileFeignClient = profileFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var filename = "";

    if (Objects.nonNull(project)) {
      log.info("Project Id:{} FlowID:{} ", project.getId(), project.getFlowId());
      filename = getFilename(project.getFlowId());
    }

    var action =
        context
            .find(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.class)
            .orElse(JsonFileProcessAction.READ);
    log.info("[JSON_FILE_PROCESS_ACTION] : {} ", action);

    switch (action) {
      case CREATE -> this.fileProvider.writeJson(project, filename, PathConstant.FILE_CONTROL_PATH);
      case UPDATE -> this.fileProvider.updateJson(
          project, filename, PathConstant.FILE_CONTROL_PATH, Project.class);
      case READ_MULTIPLE -> this.readJsons(context);
      case UPDATE_MULTIPLE -> this.updateJsons(context);
      default -> readJson(context, filename);
    }

    return ExecutionState.NEXT;
  }

  private void readJson(ExecutionContext context, String filename) {
    context.put(
        SignProcessConstant.PROJECT_KEY,
        this.fileProvider.readJson(filename, PathConstant.FILE_CONTROL_PATH, Project.class));
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);

    // Check if company info is null
    ProcessControlUtils.checkCompanyInfo(project.getCorporateInfo(), project.getFlowId());

    this.setExchangeToken(project);
  }

  private void readJsons(ExecutionContext context) {
    log.info("Reading projects from json files.");
    List<String> flowIds = convertToList(context.get(SignProcessConstant.FLOW_IDS), String.class);

    if (flowIds.isEmpty()) {
      var requests =
          convertToList(
              context.get(SignProcessConstant.MULTI_SIGNING_PROJECTS), SigningProcessDto.class);
      flowIds =
          !requests.isEmpty()
              ? requests.stream().map(SigningProcessDto::getFlowId).toList()
              : Collections.emptyList();
    }

    context.put(SignProcessConstant.PROJECTS, readProjects(flowIds));
    context.put(SignProcessConstant.IS_SIGNING_PROJECTS, true);
  }

  private List<Project> readProjects(List<String> flowIds) {
    List<Project> projects = new ArrayList<>();
    flowIds.forEach(
        (final var flowId) -> this.readProjectJson(projects, flowId));
    return projects;
  }

  private void readProjectJson(List<Project> projects, String flowId) {
    final var filename = getFilename(flowId);
    log.info("Reading project {}", filename);

    var project =
            this.fileProvider.readJson(filename, PathConstant.FILE_CONTROL_PATH, Project.class);

    if (projects.isEmpty()) {
      // This will use the first project creator company as facade.
      this.setExchangeToken(project);
    }

    // Check if company info is null
    ProcessControlUtils.checkCompanyInfo(project.getCorporateInfo(), project.getFlowId());
    log.info("Project {} has been read for process.", filename);
    projects.add(project);
  }

  private void updateJsons(ExecutionContext context) {
    log.info("Updating projects.");
    var projectsObj = context.get(SignProcessConstant.PROJECTS);
    List<Project> projects =
        new ArrayList<>(new ObjectMapper().convertValue(projectsObj, new TypeReference<>() {}));

    if (!projects.isEmpty()) {
      for (var project : projects) {
        final var filename = getFilename(project.getFlowId());
        log.info("Updating project {}", filename);
        this.fileProvider.updateJson(
            project, filename, PathConstant.FILE_CONTROL_PATH, Project.class);
      }
    }

    log.info("Projects have been updated.");
  }

  /** To provide the valid company uuid for facade. */
  private void setExchangeToken(Project project) {
    if (!StringUtils.hasText(AuthenticationUtils.getAccessToken())) {
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
        } catch (RuntimeException e) {
          log.error("This user was disabled...", e);
          log.info("This process will use technical user token.");
        }
      }

      // This userUuid use for exchanging token
      AuthenticationUtils.setUserUuid(userUuid.get());
    }
  }

  // Prevent user was disabled in keycloak
  private void checkKeycloakUser(AtomicReference<String> userUuid) {
    log.info("Getting project creator with id: {} from keycloak", userUuid);
    this.keycloakProvider
        .getUserInfo(userUuid.get())
        .ifPresent(
            (KeycloakUserResponse keycloakUser) -> {

              if (keycloakUser.isEnabled()) {
                userUuid.set(keycloakUser.getId());
                return;
              }
              this.isDisabledUser(keycloakUser, userUuid);
            });
  }

  private void isDisabledUser(KeycloakUserResponse keycloakUser, AtomicReference<String> userUuid) {
    var user = keycloakUser.getSystemUser();
    if (StringUtils.hasText(user.getCorporateId())) {
      log.info("This project creator was disabled...");
      log.info("Getting corporate admin information...");
      this.keycloakProvider
              .getUserInfo(user.getCorporateId())
              .ifPresent(
                      (KeycloakUserResponse corporateUser) -> {
                        if (keycloakUser.isEnabled()) {
                          userUuid.set(corporateUser.getId());
                        } else {
                          getAnotherCorporate(userUuid, user.getCompany().getId());
                        }
                      });
    } else {
      getAnotherCorporate(userUuid, user.getCompany().getId());
    }
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
}
