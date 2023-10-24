package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.DateUtil;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.SignatureType;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.Document;
import com.innovationandtrust.utils.aping.model.ManifestData;
import com.innovationandtrust.utils.aping.model.Scenario;
import com.innovationandtrust.utils.aping.model.ScenarioStep;
import com.innovationandtrust.utils.aping.model.Session;
import com.innovationandtrust.utils.aping.model.SessionUserData;
import com.innovationandtrust.utils.aping.signing.Actor;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.file.provider.FileProvider;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestSigningHandler extends AbstractExecutionHandler {

  private final ApiNgFeignClientFacade apiNgFeignClient;

  private final FileProvider fileProvider;

  private final ProfileFeignClient profileFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    // step 1
    this.createSession(project);
    // step 2
    this.createActor(project);
    // step 3
    this.createDefaultDocument(project);
    // step 4
    this.createScenario(project);
    // step 5
    this.activateScenario(project);

    context.put(SignProcessConstant.PROJECT_KEY, project);
    if (!project
        .getTemplate()
        .getSignProcess()
        .equals(com.innovationandtrust.share.enums.ScenarioStep.INDIVIDUAL_SIGN)) {
      context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.CREATE);
    }
    return ExecutionState.NEXT;
  }

  private void createSession(Project project) {
    int ttl =
        (int)
            TimeUnit.MILLISECONDS.toSeconds(
                DateUtil.getExpiredTime(project.getDetail().getExpireDate()));
    var user = this.profileFeignClient.findUserById(project.getCreatedBy());
    var sessionUser =
        new SessionUserData(user.getUserEntityId().toString(), user.getFullName(), user.getEmail());
    var response = this.apiNgFeignClient.createSession(new Session(ttl, sessionUser));
    project
        .getDetail()
        .setSessionId(
            Long.parseLong(response.getUrl().substring(response.getUrl().lastIndexOf("/") + 1)));
  }

  private void createActor(Project project) {
    project.getParticipants().stream()
        .sorted(Comparator.comparingInt(Participant::getOrder))
        .forEach(
            person -> {
              var actor =
                  new Actor(
                      person.getLastName(),
                      person.getFirstName(),
                      String.valueOf(person.getId()),
                      person.getEmail(),
                      List.of(ParticipantRole.getByRole(person.getRole()).getApiNgRole()),
                      person.getPhone());
              var response =
                  this.apiNgFeignClient.createActor(project.getDetail().getSessionId(), actor);
              person.setActorUrl(response.getUrl());
            });
  }

  private void createDefaultDocument(Project project) {
    project
        .getDocuments()
        .forEach(
            doc -> {
              var resource =
                  this.fileProvider.loadFile(
                      Path.of(project.getFlowId())
                          .resolve(DocumentProcessingHandler.DOCUMENT_PATH)
                          .resolve(doc.getFileName()),
                      false);
              var response =
                  this.apiNgFeignClient.uploadFile("file", doc.getOriginalFileName(), resource);
              var docResponse =
                  this.apiNgFeignClient.addDocument(
                      project.getSessionId(),
                      new Document(response.getUrl(), doc.getOriginalFileName()));
              doc.setDocUrl(docResponse.getUrl());
            });
  }

  private void createScenario(Project project) {
    var cardinality = "all";
    var approvalActors = project.getActorUrls(RoleConstant.ROLE_APPROVAL);
    var recipients = project.getActorUrls(RoleConstant.ROLE_RECEIPT);
    var template = project.getTemplate();

    List<ScenarioStep> scenarioSteps = new ArrayList<>();
    if (!approvalActors.isEmpty()) {
      scenarioSteps.add(
          new ScenarioStep(template.getApprovalProcess().getVal(), cardinality, approvalActors));
    }
    scenarioSteps.add(
        new ScenarioStep(
            template.getSignProcess().getVal(),
            cardinality,
            SignatureType.ENVELOPED.getVal(),
            project.getActorUrls(RoleConstant.ROLE_SIGNATORY)));
    if (!recipients.isEmpty()) {
      scenarioSteps.add(
          new ScenarioStep(RoleConstant.ROLE_API_NG_RECEIPT, cardinality, recipients));
    }
    var scenario =
        new Scenario(
            project.getDocumentUrls(),
            Collections.unmodifiableList(scenarioSteps),
            template.getFormat().getNumVal(),
            template.getLevel().getValue());
    var response = this.apiNgFeignClient.createScenario(project.getSessionId(), scenario);
    var url = response.getUrl();
    project.getDetail().setScenarioId(Long.parseLong(url.substring(url.lastIndexOf("/") + 1)));
  }

  private void activateScenario(Project project) {
    var dt = project.getDetail();
    this.apiNgFeignClient.activeScenario(dt.getSessionId(), dt.getScenarioId(), new ManifestData());
  }
}
