package com.innovationandtrust.process.chain.handler;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.share.constant.CriterionConstant;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.share.model.profile.UserCompany;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.tdc.TdcCriterionModel;
import com.innovationandtrust.share.model.tdc.TdcDocument;
import com.innovationandtrust.share.model.tdc.TdcJsonFile;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.CloseSession;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.commons.CommonUsages;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.tdcservice.TdcFeignClient;
import com.innovationandtrust.utils.tdcservice.TdcProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompleteSigningProcessHandler extends AbstractExecutionHandler {
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final TemplateEngine templateEngine;
  private final EmailService emailService;
  private final ProjectFeignClient projectFeignClient;
  private final ProfileFeignClient profileFeignClient;
  private final TdcFeignClient tdcFeignClient;
  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final TdcProperty tdcProperty;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    this.completeProcess(project);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private void completeProcess(Project project) {
    log.info("Complete project is processing");
    if (!project.hasNextParticipant()) {
      this.sendSignedDocuments(project);
    }
    if (!project.hasNextParticipant() && !project.hasNextRecipient()) {
      log.info("Update project after all signatures");
      project.setStatus(
          project.hasRefused() ? ProjectStatus.REFUSED.name() : ProjectStatus.COMPLETED.name());
      this.projectFeignClient.completeProjectWithStatus(project.getId(), project.getStatus());
      this.uploadSignedDocuments(project);
    } else if (project.hasRefused()) {
      if (Objects.equals(project.getTemplate().getSignProcess(), ScenarioStep.COUNTER_SIGN)) {
        log.info("Upload manifest when refuse of counter-sign");
        this.uploadSignedDocuments(project);
      }
      log.info("Update project to refused");
      project.setStatus(ProjectStatus.REFUSED.name());
      this.projectFeignClient.completeProjectWithStatus(project.getId(), project.getStatus());
    }
  }

  private void sendSignedDocuments(Project project) {
    var company = project.getCorporateInfo();
    var resourceLogo =
        this.corporateProfileFeignClient.viewFileContent(project.getCorporateInfo().getLogo());
    List<MailRequest> mailRequests = new ArrayList<>();
    project
        .getParticipantsByRole(ParticipantRole.SIGNATORY.getRole())
        .forEach(
            person -> {
              var mailRequest =
                  this.emailService.prepareSignCompleteMail(project, person, company, resourceLogo);
              // Change role, because invitation mail template depends on the role
              mailRequest.setRole(RoleConstant.SIGN_COMPLETED);
              mailRequests.add(mailRequest.getMailRequest(templateEngine));
            });
    this.emailService.sendInvitationMail(mailRequests, company.getLogo());
  }

  private void uploadSignedDocuments(Project project) {
    try {
      if (project.getTemplate().isTagAllowBackup()) {
        var user = this.profileFeignClient.findCompanyUserById(project.getCreatedBy());
        if (user == null) {
          log.warn("Company info is missing");
          return;
        }
        if (user.getCompany().isArchiving()) {
          log.info("Uploading the proof file to data content.");
          List<TdcCriterionModel> criterions = new ArrayList<>();
          criterions.add(
              new TdcCriterionModel(project.getId().toString(), CriterionConstant.CLIENT_ID));
          criterions.add(new TdcCriterionModel(user.getCompany().getName(), CriterionConstant.NOM));
          project
              .getDocuments()
              .forEach(
                  document -> {
                    var fileName =
                        String.format(
                            "%s_%s_signed.%s",
                            FilenameUtils.getBaseName(document.getOriginalFileName()),
                            project.getId(),
                            FilenameUtils.getExtension(document.getOriginalFileName()));
                    TdcJsonFile tdcJsonFile = new TdcJsonFile();
                    tdcJsonFile.setCriterions(criterions);
                    tdcJsonFile.setFilename(fileName);
                    tdcJsonFile.setExtension(
                        FilenameUtils.getExtension(document.getOriginalFileName()));
                    tdcJsonFile.setBaseId(this.tdcProperty.getBaseId());

                    var download = this.apiNgFeignClient.downloadDocument(document.getDocUrl());

                    log.info("download from api-ng");

                    document.setTdcDocument(this.uploadDocument(download, tdcJsonFile, fileName));
                  });
          project.setManifestData(this.uploadManifest(project, criterions, user));
        }
      }
    } catch (Exception e) {
      log.error("Error while uploading signed documents", e);
    }
  }

  private TdcDocument uploadManifest(
      Project project, List<TdcCriterionModel> criterions, UserCompany user) {
    var corporate = this.profileFeignClient.findUserById(user.getCreatedBy());
    if (corporate == null) {
      throw new IllegalArgumentException("Could not find corporate admin of user " + user.getId());
    }
    var fileName =
        String.format(
            "%s_%s_%s_proof.pdf",
            corporate.getUserEntityId(), user.getUserEntityId(), project.getId());
    TdcJsonFile tdcJsonFile = new TdcJsonFile();
    tdcJsonFile.setCriterions(criterions);
    tdcJsonFile.setFilename(fileName);
    tdcJsonFile.setExtension("pdf");
    tdcJsonFile.setBaseId(this.tdcProperty.getBaseId());
    // close session
    log.info("Closing session");
    this.apiNgFeignClient.closeSession(
        project.getSessionId(), new CloseSession(true, "getting proof after complete project"));
    // upload manifest
    log.info("downloading manifest");
    var manifest = this.apiNgFeignClient.downloadManifest(project.getSessionId());

    return this.uploadDocument(manifest, tdcJsonFile, fileName);
  }

  private TdcDocument uploadDocument(byte[] download, TdcJsonFile tdcJsonFile, String filename) {
    var inputStream = CommonUsages.byteArrayToMultipartFile(download, filename);
    MultipartFile jsonFile =
        CommonUsages.objectToMultipartFile(
            tdcJsonFile, FilenameUtils.getBaseName(filename) + ".json");
    log.info("uploading signed document");
    var uploadDocument = this.tdcFeignClient.uploadDocument(inputStream, jsonFile);
    if (uploadDocument != null) {
      return new TdcDocument(
          uploadDocument.getUuid(),
          uploadDocument.getBaseUUID(),
          uploadDocument.getBaseId(),
          uploadDocument.getFileID());
    }
    return null;
  }
}
