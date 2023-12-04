package com.innovationandtrust.process.chain.handler.sign;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.model.TdcUploadDocument;
import com.innovationandtrust.process.model.email.SignedDocumentSftpMailModel;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.SftpFeignClient;
import com.innovationandtrust.process.service.EmailService;
import com.innovationandtrust.process.service.SignedDocumentService;
import com.innovationandtrust.share.constant.CriterionConstant;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.tdc.TdcCriterionModel;
import com.innovationandtrust.share.model.tdc.TdcDocument;
import com.innovationandtrust.share.model.tdc.TdcJsonFile;
import com.innovationandtrust.share.model.user.User;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.commons.CommonUsages;
import com.innovationandtrust.utils.tdcservice.TdcFeignClient;
import com.innovationandtrust.utils.tdcservice.TdcProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

@Slf4j
@Component
public class UploadSignedDocument extends AbstractExecutionHandler {
  private final TemplateEngine templateEngine;
  private final EmailService emailService;
  private final ProfileFeignClient profileFeignClient;
  private final TdcFeignClient tdcFeignClient;
  private final SftpFeignClient sftpFeignClient;
  private final TdcProperty tdcProperty;
  private final SignedDocumentService signedDocumentService;

  public UploadSignedDocument(
      TemplateEngine templateEngine,
      EmailService emailService,
      ProfileFeignClient profileFeignClient,
      TdcFeignClient tdcFeignClient,
      SftpFeignClient sftpFeignClient,
      TdcProperty tdcProperty,
      SignedDocumentService signedDocumentService) {
    this.templateEngine = templateEngine;
    this.emailService = emailService;
    this.profileFeignClient = profileFeignClient;
    this.tdcFeignClient = tdcFeignClient;
    this.sftpFeignClient = sftpFeignClient;
    this.tdcProperty = tdcProperty;
    this.signedDocumentService = signedDocumentService;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    this.uploadSignedDocuments(project);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    return ExecutionState.NEXT;
  }

  private void uploadSignedDocuments(Project project) {
    try {
      var user = this.profileFeignClient.findCompanyUserById(project.getCreatedBy());
      List<TdcCriterionModel> criterionList = new ArrayList<>();
      criterionList.add(
          new TdcCriterionModel(project.getId().toString(), CriterionConstant.CLIENT_ID));
      criterionList.add(new TdcCriterionModel(user.getCompany().getName(), CriterionConstant.NOM));
      List<TdcUploadDocument> tdcUploadDocuments = new ArrayList<>();

      for (Document document : project.getDocuments()) {
        var fileName =
            String.format(
                "%s_%s_signed.%s",
                FilenameUtils.getBaseName(document.getOriginalFileName()),
                project.getId(),
                FilenameUtils.getExtension(document.getOriginalFileName()));
        var tdcJsonFile =
            new TdcJsonFile(
                fileName,
                FilenameUtils.getExtension(document.getOriginalFileName()),
                this.tdcProperty.getBaseId(),
                criterionList);
        var download =
            this.signedDocumentService.downloadSignedDocument(project, document.getUuid());
        tdcUploadDocuments.add(
            new TdcUploadDocument(download.getResource(), tdcJsonFile, fileName));
      }

      var manifest = this.signedDocumentService.downloadManifest(project);

      if (Objects.nonNull(project.getSftpZipFile())) {
        Executors.newSingleThreadExecutor()
            .execute(
                () -> this.uploadToSftp(project, tdcUploadDocuments, user, manifest.getResource()));
      }

      if (project.getTemplate().isTagAllowBackup() && (user.getCompany().isArchiving())) {
        log.info("Uploading the proof file to data content.");
        tdcUploadDocuments.forEach(
            tdcDoc ->
                this.uploadDocument(
                    tdcDoc.getFileContent(), tdcDoc.getTdcJsonFile(), tdcDoc.getFilename()));
        project.setManifestData(
            this.uploadManifest(project, criterionList, manifest.getResource()));
      }
    } catch (Exception e) {
      log.error("Error while uploading signed documents", e);
    }
  }

  private void uploadToSftp(
      Project project, List<TdcUploadDocument> documents, User user, byte[] manifest) {
    try {
      log.info("Uploading the proof file to SFTP");
      var signedDocs =
          documents.stream()
              .map(d -> CommonUsages.byteArrayToMultipartFile(d.getFileContent(), d.getFilename()))
              .toList();

      this.sftpFeignClient.insertSignedDocument(
          project.getCorporateFolder(),
          project.getSftpZipFile(),
          signedDocs.toArray(new MultipartFile[0]),
          CommonUsages.byteArrayToMultipartFile(manifest, project.getManifestFileName()));
    } catch (Exception e) {
      log.warn("Error while uploading signed documents and manifest to SFTP", e);
      // send alert problem email to end user
      var message =
          new SignedDocumentSftpMailModel(
              user.getFirstName(),
              project.getSftpZipFile(),
              user.getEmail(),
              project.getCorporateInfo().getCompanyName(),
              project.getCorporateInfo().getMainColor());
      this.emailService.sendInvitationMail(
          message.getMailRequest(templateEngine), project.getCorporateInfo().getLogo());
    }
  }

  private TdcDocument uploadManifest(
      Project project, List<TdcCriterionModel> criterions, byte[] manifest) {

    var tdcJsonFile = new TdcJsonFile();
    tdcJsonFile.setCriterions(criterions);
    tdcJsonFile.setFilename(project.getManifestFileName());
    tdcJsonFile.setExtension("pdf");
    tdcJsonFile.setBaseId(this.tdcProperty.getBaseId());

    return this.uploadDocument(manifest, tdcJsonFile, project.getManifestFileName());
  }

  private TdcDocument uploadDocument(byte[] download, TdcJsonFile tdcJsonFile, String filename) {
    var inputStream = CommonUsages.byteArrayToMultipartFile(download, filename);
    var jsonFile =
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
