package com.innovationandtrust.process.chain.handler.eid;

import static com.innovationandtrust.share.constant.ProcessStatus.EID_REJECTED;

import com.innovationandtrust.process.chain.handler.ValidateCorporateSettingHandler;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.PathConstant;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.email.EmailInvitationRequest;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.share.constant.FileExtensionConstant;
import com.innovationandtrust.share.constant.ProcessStatus;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.eid.model.IdentityDto;
import com.innovationandtrust.utils.eid.model.VideoRecordedResponse;
import com.innovationandtrust.utils.eid.provider.EIDServiceProvider;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.util.StringUtils;

@Component
@Slf4j
public class VideoVerifiedMailHandler extends AbstractExecutionHandler {

  private final EIDServiceProvider eIDServiceProvider;
  private final TemplateEngine templateEngine;
  private final EmailService emailService;
  private final FileProvider fileProvider;
  private final ValidateCorporateSettingHandler validateCorporateSettingHandler;
  private final ProjectFeignClient projectFeignClient;

  public VideoVerifiedMailHandler(
      EIDServiceProvider eIDServiceProvider,
      TemplateEngine templateEngine,
      EmailService emailService,
      FileProvider fileProvider,
      ValidateCorporateSettingHandler validateCorporateSettingHandler,
      ProjectFeignClient projectFeignClient) {
    this.eIDServiceProvider = eIDServiceProvider;
    this.templateEngine = templateEngine;
    this.emailService = emailService;
    this.fileProvider = fileProvider;
    this.validateCorporateSettingHandler = validateCorporateSettingHandler;
    this.projectFeignClient = projectFeignClient;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("[VideoVerifiedMailHandler] Processing mail invitation.");

    this.validateCorporateSettingHandler.execute(context);

    final Project project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    checkIsComplete(project.getStatus());

    final String verifiedStatus =
        context.get(SignProcessConstant.VIDEO_VERIFIED_STATUS, String.class);
    final Long signatoryId = context.get(SignProcessConstant.SIGNATORY_ID, Long.class);
    this.updateDocumentStatus(signatoryId, verifiedStatus);

    final String videoId = context.get(SignProcessConstant.VIDEO_ID, String.class);

    final String verificationId =
        context.get(SignProcessConstant.VIDEO_VERIFICATION_ID, String.class);

    this.mailVideoVerifiedProcess(project, videoId, verifiedStatus, verificationId);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);

    if (verifiedStatus.equalsIgnoreCase(EID_REJECTED)) {
      context.put(SignProcessConstant.COMMENT, "La vidéo a été rejetée.");
    }

    return ExecutionState.NEXT;
  }

  private static void checkIsComplete(String status) {
    if (Objects.equals(status, ProjectStatus.COMPLETED.name())) {
      throw new BadRequestException("Invalid action. Project is completed...");
    }
  }

  private void updateDocumentStatus(Long id, String verifiedStatus) {
    Executors.newSingleThreadExecutor()
        .execute(() -> this.projectFeignClient.updateDocumentStatus(id, verifiedStatus));
  }

  private void mailVideoVerifiedProcess(
      Project project, String videoId, String verifiedStatus, String verificationId) {
    final var company = project.getCorporateInfo();
    final String flowId = project.getFlowId();
    final List<MailRequest> mailRequests = new ArrayList<>();
    project.getParticipants().stream()
        .filter(
            (Participant participant) -> {
              if (Objects.nonNull(participant.getVideoId())) {
                return participant.getVideoId().equalsIgnoreCase(videoId);
              }

              return false;
            })
        .forEach(
            (Participant participant) -> {
              this.sendInvitationMailProcess(
                  participant, verifiedStatus, verificationId, videoId, flowId);

              final EmailInvitationRequest request =
                  VideoVerifiedMailHandler.this.emailService.prepareVideoVerifiedMail(
                      project, participant, company);
              mailRequests.add(request.getMailRequest(templateEngine));
            });

    final String companyLogo = project.getCorporateInfo().getLogo();
    this.emailService.sendInvitationMail(mailRequests, companyLogo);
  }

  private void sendInvitationMailProcess(
      Participant participant,
      String verifiedStatus,
      String verificationId,
      String videoId,
      String flowId) {
    if (Strings.isBlank(participant.getIdentityId())
        && ProcessStatus.EID_ACCEPTED.equalsIgnoreCase(verifiedStatus)) {
      log.info("[VideoVerifiedMailHandler] Processing create identify.");
      final IdentityDto identity =
          VideoVerifiedMailHandler.this.eIDServiceProvider.createIdentity(verificationId);
      byte[] videoBytes =
          VideoVerifiedMailHandler.this.eIDServiceProvider.requestMp4OfRecordedVideoData(videoId);
      final VideoRecordedResponse videoRecorded =
          VideoVerifiedMailHandler.this.eIDServiceProvider.requestRecordedVideoData(videoId);

      final String identityId = identity.getId();

      var documentFrontBase64 = "";
      var documentBackBase64 = "";
      var documentBack = "";
      if (Objects.nonNull(videoRecorded.getDocument().getScan())) {
        documentFrontBase64 = videoRecorded.getDocument().getScan();
      } else {
        documentFrontBase64 = videoRecorded.getDocument().getFront();
        documentBackBase64 = videoRecorded.getDocument().getBack();

        documentBack =
            VideoVerifiedMailHandler.this.storeResourceToDir(
                VideoVerifiedMailHandler.this.decodeBase64ToByte(documentBackBase64),
                flowId,
                FileExtensionConstant.PNG_EXTENSION);
      }

      final String faceFrontBase64 = videoRecorded.getBiometrics().getFace().getImage();

      log.info("[VideoVerifiedMailHandler] Processing upload video identity.");
      final String video =
          VideoVerifiedMailHandler.this.storeResourceToDir(
              videoBytes, flowId, FileExtensionConstant.MP4_EXTENSION);
      final String documentFront =
          VideoVerifiedMailHandler.this.storeResourceToDir(
              VideoVerifiedMailHandler.this.decodeBase64ToByte(documentFrontBase64),
              flowId,
              FileExtensionConstant.PNG_EXTENSION);
      final String faceFront =
          VideoVerifiedMailHandler.this.storeResourceToDir(
              VideoVerifiedMailHandler.this.decodeBase64ToByte(faceFrontBase64),
              flowId,
              FileExtensionConstant.PNG_EXTENSION);
      log.info("[VideoVerifiedMailHandler] Success upload video identity.");

      participant.setIdentityId(identityId);
      participant.setVerificationId(verificationId);
      participant.setVideo(video);
      participant.setDocumentFront(documentFront);
      participant.setDocumentBack(documentBack);
      participant.setFaceFront(faceFront);
      participant.setDocumentVerified(true);
    }

    participant.setVideoVerifiedStatus(verifiedStatus);
  }

  private byte[] decodeBase64ToByte(String base64) {
    if (Strings.isBlank(base64)) {
      log.error("[VideoVerifiedMailHandler] Invalid base64.");
      throw new BadRequestException("Unable to covert base64 to byte[].");
    }

    return Base64.getDecoder().decode(base64);
  }

  private String storeResourceToDir(byte[] byteArray, String flowId, String extension) {
    final var uuid = UUID.randomUUID().toString();
    final var fileName = StringUtils.concat(uuid, ".", extension);

    fileProvider.storeFile(
        new ByteArrayResource(byteArray),
        fileName,
        FileUtils.path(flowId, PathConstant.VIDEO_RECORD_PATH).toString());

    return fileName;
  }
}
