package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.RecipientInvitationHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessHandler;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.email.EmailInvitationRequest;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.GeneratedOTP;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionManager;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import io.swagger.v3.oas.annotations.Hidden;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;

/** This class for quickly testing sign documents. */
@Hidden
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class TestController {
  private final SignProcess signProcess;
  private final TestProcess testProcess;
  private final ImpersonateTokenService impersonateToken;

  private final CompleteEidP completeEidP;

  @GetMapping("/test/eid/complete")
  public ResponseEntity<Void> completedEidProcess(
      @RequestParam("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @RequestParam("status") String status) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    context.put(SignProcessConstant.VIDEO_VERIFIED_STATUS, status);
    this.completeEidP.execute(context);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequiredArgsConstructor
  static @Component class CompleteEidP extends ExecutionManager {
    private final JsonFileProcessHandler jsonFileProcessHandler;
    private final CompleteEid completeEid;

    @Override
    public void afterPropertiesSet() {
      super.addHandlers(List.of(jsonFileProcessHandler, completeEid, jsonFileProcessHandler));
    }
  }

  @RequiredArgsConstructor
  static @Component class CompleteEid extends AbstractExecutionHandler {
    private final ProjectFeignClient projectFeignClient;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @Override
    public ExecutionState execute(ExecutionContext context) {
      var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
      var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
      var status = context.get(SignProcessConstant.VIDEO_VERIFIED_STATUS, String.class);
      CorporateInfo company = project.getCorporateInfo();
      project
          .getParticipantByUuid(uuid)
          .ifPresent(
              participant -> {
                var videoId = UUID.randomUUID().toString();
                this.projectFeignClient.updateDocumentStatus(participant.getId(), status);
                participant.setVerificationId(videoId);
                participant.setVideoVerifiedStatus(status);

                EmailInvitationRequest request =
                    this.emailService.prepareVideoVerifiedMail(project, participant, company);
                this.emailService.sendInvitationMail(
                    request.getMailRequest(templateEngine), project.getCorporateInfo().getLogo());
                context.put(
                    SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
              });
      return ExecutionState.NEXT;
    }
  }

  private final EidToInProgressP eidToInProgressP;

  @GetMapping("/test/eid/in-progress")
  public ResponseEntity<Void> inEidProcess(
      @RequestParam("flowId") String flowId, @RequestParam("uuid") String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    this.eidToInProgressP.execute(context);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequiredArgsConstructor
  static @Component class EidToInProgressP extends ExecutionManager {
    private final JsonFileProcessHandler jsonFileProcessHandler;
    private final EidToInProgress eidToInProgress;

    @Override
    public void afterPropertiesSet() {
      super.addHandlers(List.of(jsonFileProcessHandler, eidToInProgress, jsonFileProcessHandler));
    }
  }

  @RequiredArgsConstructor
  static @Component class EidToInProgress extends AbstractExecutionHandler {
    private final ProjectFeignClient projectFeignClient;

    @Override
    public ExecutionState execute(ExecutionContext context) {
      var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
      var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
      project
          .getParticipantByUuid(uuid)
          .ifPresent(
              participant -> {
                var videoId = UUID.randomUUID().toString();
                this.projectFeignClient.updateVideoId(participant.getId(), videoId);
                participant.setVideoId(videoId);
                participant.setVideoVerifiedStatus(ProcessStatus.EID_IN_PROGRESS);
                context.put(
                    SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
              });
      return ExecutionState.NEXT;
    }
  }

  @GetMapping("/test")
  public ResponseEntity<Void> testProcess(
      @RequestParam("flowId") String flowId, @RequestParam("uuid") String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    this.testProcess.execute(context);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequiredArgsConstructor
  static @Component class TestProcess extends ExecutionManager {
    private final JsonFileProcessHandler jsonFileProcessHandler;
    private final RecipientInvitationHandler completeSigningProcessHandler;

    @Override
    public void afterPropertiesSet() {
      super.addHandlers(List.of(jsonFileProcessHandler, completeSigningProcessHandler));
    }
  }

  @GetMapping("/sign/{companyUuid}")
  public ResponseEntity<String> test(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("flowId") String flowId,
      @RequestParam("uuid") String uuid) {
    var context = ProcessControlUtils.getProject(flowId, uuid);
    this.signProcess.execute(context);
    return new ResponseEntity<>(getToken(companyUuid, flowId, uuid), HttpStatus.OK);
  }

  @GetMapping("/token/{companyUuid}")
  public ResponseEntity<String> getEncryptedToken(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("flowId") String flowId,
      @RequestParam("uuid") String uuid) {
    return new ResponseEntity<>(getToken(companyUuid, flowId, uuid), HttpStatus.OK);
  }

  private String getToken(String companyUuid, String flowId, String uuid) {
    // Get completed encrypted link of requesting participants.
    var link = this.impersonateToken.getTokenUrlParam(flowId, uuid, "LOCAL", "PATH", companyUuid);
    var token = link.split("=")[1];
    log.info("TOKEN: {}", token);
    return token;
  }

  @GetMapping("/download/{companyUuid}")
  public ResponseEntity<List<String>> getDownloadDocLinks(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @RequestBody List<Doc> docs)
      throws IOException {

    List<String> docIds = new ArrayList<>();
    String ip = System.getenv("IP_ADDRESS");
    try (Socket socket = new Socket(ip, 6667)) {
      for (Doc doc : docs) {
        String link =
            String.format(
                "%s%s%s%s%s%s",
                socket.getInetAddress().getHostAddress(),
                companyUuid,
                "?token=",
                getToken(companyUuid, flowId, uuid),
                "&docId=",
                doc.getDocId());
        docIds.add(link);
      }
    }
    return new ResponseEntity<>(docIds, HttpStatus.OK);
  }

  @RequiredArgsConstructor
  static @Component class SignProcess extends ExecutionManager {
    private final SigningProcessHandler signingProcessHandler;
    private final JsonFileProcessHandler jsonFileProcessHandler;
    private final AddOn addOn;

    @Override
    public void afterPropertiesSet() {
      super.addHandlers(
          List.of(jsonFileProcessHandler, addOn, signingProcessHandler, jsonFileProcessHandler));
    }
  }

  static @Component class AddOn extends AbstractExecutionHandler {
    @Override
    public ExecutionState execute(ExecutionContext context) {
      SecureRandom rand = new SecureRandom();
      String randomOtp = String.valueOf(rand.nextInt(100000));
      var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
      var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
      project
          .getParticipantByUuid(uuid)
          .ifPresent(
              participant -> {
                var validPhone = participant.getValidPhone();
                validPhone.setValid(true);
                participant.setValidPhone(validPhone);
                participant.setOtp(
                    new GeneratedOTP(
                        randomOtp, "2023-10-19T02:56:42.554Z", "2023-10-19T02:56:42.554Z", 0));
                participant.getOtp().setValidated(true);
              });
      return ExecutionState.NEXT;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  static class Doc {
    private String docId;
  }
}
