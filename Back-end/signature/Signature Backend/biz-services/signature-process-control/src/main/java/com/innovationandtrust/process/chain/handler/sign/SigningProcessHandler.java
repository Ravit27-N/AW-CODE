package com.innovationandtrust.process.chain.handler.sign;

import static com.innovationandtrust.utils.commons.CommonUsages.getFirstUpperCase;

import com.innovationandtrust.process.chain.handler.DocumentProcessingHandler;
import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.utils.PdfUtils;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.DocumentDetail;
import com.innovationandtrust.share.model.project.DocumentRequest;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.aping.constant.ActorActionConstant;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.SignRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.InternalErrorException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.file.utils.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SigningProcessHandler extends AbstractExecutionHandler {

  private final ApiNgFeignClientFacade apiNgFeignClient;

  private final ProjectFeignClient projectFeignClient;

  private final ModelMapper modelMapper;

  private final FileProvider fileProvider;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    this.sign(project, uuid);
    // Set property to update json file in the next step
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    return ExecutionState.NEXT;
  }

  private void sign(Project project, String uuid) {
    var sessionId = project.getDetail().getSessionId();

    log.info("Getting participant:{}...", uuid);
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            actor -> {
              if (Objects.isNull(actor.getOtp()) || !actor.getOtp().isValidated()) {
                throw new IllegalArgumentException(
                    "Invalid the signing process! The signer is not confirm the OTP code yet!");
              }

              if (actor.isSigner() && actor.isSigned()) {
                throw new IllegalArgumentException(
                    "The documents are already signed and cannot be processed again!");
              }

              if (actor.isApprover() && actor.isApproved()) {
                throw new IllegalArgumentException(
                    "The documents are already approved and cannot be processed again!");
              }
              var signRequest =
                  new SignRequest(
                      actor.getActorUrl(),
                      actor.getCertificate(),
                      project.getTemplate().getSignProcess().getVal());
              this.prepareSignDocuments(project, signRequest, actor);
              this.signDocuments(sessionId, signRequest);
              this.updateProjectAfterSign(actor.getId(), project.getDocuments());
              // Store value after signed by this participant
              actor.setSigned(true);
              actor.setSignedDate(new Date());
            });
  }

  private void signDocuments(Long sessionId, SignRequest signRequest) {
    try {
      var signRes = this.apiNgFeignClient.signDocuments(sessionId, signRequest);
      if (Objects.nonNull(signRes) && signRes.containsKey(ActorActionConstant.STATUS_KEY)) {
        if (Objects.equals(signRes.get(ActorActionConstant.STATUS_KEY), ActorActionConstant.DONE)) {
          log.warn("This signatory is already signed");
        } else if (Objects.equals(
            signRes.get(ActorActionConstant.STATUS_KEY), ActorActionConstant.IN_PROGRESS)) {
          log.warn("This signatory is signing the document");
          throw new InternalErrorException(
              "This signatory is signing the document in background process");
        }
      }
    } catch (Exception e) {
      log.error("Could not sign document: ", e);
      throw new InternalErrorException("Error when sign document: " + e.getMessage());
    }
  }

  private void updateProjectAfterSign(Long participantId, List<Document> documents) {
    var signatory = new SignatoryRequest(participantId, DocumentStatus.SIGNED);
    this.projectFeignClient.updateProjectAfterSigned(
        new ProjectAfterSignRequest(
            signatory, documents.stream().map(DocumentRequest::new).toList()));
  }

  private void prepareSignDocuments(Project project, SignRequest request, Participant participant) {
    request.setDocuments(new ArrayList<>());
    project
        .getDocuments()
        .forEach(
            document -> {
              log.info("Generating pdf annotations for document:{}...", document.getFileName());
              var details = document.getParticipantDetails(participant.getId(), PdfUtils.SIGNATORY);
              log.info(
                  "There are {} signature blocks on document:{}...",
                  details.size(),
                  document.getFileName());
              if (details.isEmpty()) {
                request
                    .getDocuments()
                    .add(this.mapDocToSignCriteria(null, null, document.getDocUrl(), null));
                return;
              }

              var sign =
                  this.generateSignatureImage(
                      participant, details.get(0).getText(), project.getFlowId());

              Resource resource = this.getDocumentFile(project, document);
              try {
                this.setAnnotations(request, participant, resource, project, document, sign);
                Files.deleteIfExists(sign.toPath());
                log.info("Sign file which was generated, was deleted...");
              } catch (IOException e) {
                var message = "Error while creating signature annotations...";
                log.error(message, e);
                throw new InternalErrorException(message);
              }
            });
  }

  private void setAnnotations(
      SignRequest request,
      Participant participant,
      Resource resource,
      Project project,
      Document document,
      File sign)
      throws IOException {

    var participantName =
        (project.countSigned() > 0 ? "/" : "") + getFirstUpperCase(participant.getFullName());
    project.setParaph(project.getParaph() + participantName);

    var tempPath =
        PdfUtils.buildTempPath(
            Path.of(
                fileProvider.basePath(),
                String.valueOf(getDocPath(project.getFlowId(), document.getFileName()))),
            participant.getId());
    PDDocument pdDocument = Loader.loadPDF(resource.getInputStream());
    FileOutputStream fos = new FileOutputStream(tempPath);

    log.info("There are {} pages on this document...", pdDocument.getNumberOfPages());
    var documentDetails = document.getParticipantDetails(participant.getId(), PdfUtils.SIGNATORY);
    List<DocumentDetail> details = new ArrayList<>(documentDetails);
    AtomicReference<DocumentDetail> primaryDetail = new AtomicReference<>();

    log.info("Keep first signature detail for real signature...");
    documentDetails.stream()
        .min(Comparator.comparing(DocumentDetail::getPageNum))
        .ifPresent(primaryDetail::set);
    details.remove(primaryDetail.get());

    log.info("Generating annotations on each page...");
    details.forEach(
        detail -> {
          String message =
              String.format(
                  "on position x:%s y:%s on page:%s type:%s",
                  detail.getX(), detail.getY(), detail.getPageNum(), detail.getType());
          try {
            log.info("Setting annotation {}", message);
            PdfUtils.setImageAnnotation(pdDocument, participant, detail, sign);
          } catch (IOException e) {
            log.error("Can't not set annotation {}", message, e);
            throw new InternalErrorException(message);
          }
        });

    log.info("Generating paraph annotations on all pages for {}...", participant.getFullName());
    document.getDetails().stream()
        .filter(detail -> detail.getType().contains(PdfUtils.PARAPH))
        .findFirst()
        .ifPresentOrElse(
            detail -> {
              try {
                PdfUtils.setParaphAnnotations(
                    pdDocument,
                    project,
                    participant,
                    detail,
                    fileProvider.basePath(),
                    participantName);
              } catch (Exception e) {
                var message = "Error while setting paraph...";
                log.error(message, e);
                throw new InternalErrorException(message);
              }
            },
            () -> log.warn("There is no paraph in this project..."));

    log.info("Saving document after adding annotations...");
    pdDocument.getPages().getCOSObject().setNeedToBeUpdated(true);
    pdDocument.getDocumentCatalog().getCOSObject().setNeedToBeUpdated(true);
    pdDocument.saveIncremental(fos);
    fos.close();
    pdDocument.close();
    log.info("Successfully put signature blocks annotations on documents...");

    var detail = primaryDetail.get();
    var path = Path.of(tempPath);

    log.info("Preparing criteria (visual-parameters)...");
    detail.setText(PdfUtils.buildSignature(detail.getText(), participant));
    var documents = request.getDocuments();

    String signImageBase64 = null;
    if (Objects.nonNull(participant.getSignatureImage())) {
      var imagePath =
          Path.of(
              fileProvider.basePath(),
              project.getFlowId(),
              PdfUtils.SIGN_DIR,
              participant.getSignatureImage());
      signImageBase64 =
          Objects.isNull(participant.getSignatureMode())
                  || participant.getSignatureMode().equals(SignatureMode.WRITE.name())
              ? null
              : Base64.getEncoder().encodeToString(Files.readAllBytes(imagePath));
    }

    documents.add(
        this.mapDocToSignCriteria(
            detail,
            Base64.getEncoder().encodeToString(Files.readAllBytes(path)),
            document.getDocUrl(),
            signImageBase64));

    log.info("Converted prepared document to base64....");
    log.info("Deleting prepared document...");
    Files.deleteIfExists(path);
  }

  private SignRequest.SignCriteria mapDocToSignCriteria(
      DocumentDetail detail, String base64, String docUrl, String signImage) {
    if (Objects.isNull(detail)) {
      return new SignRequest.SignCriteria(docUrl, base64, null);
    }
    var criteria = this.modelMapper.map(detail, SignRequest.Criteria.class);
    criteria.setSignature(Objects.isNull(signImage) ? detail.getText() : null);
    criteria.setPageNumber(detail.getPageNum());
    criteria.setTextAlign(Integer.parseInt(detail.getTextAlign()));
    criteria.setSignImage(signImage);

    if (Objects.nonNull(signImage)) {
      criteria.setWidth((int) detail.getWidth() - 10);
      criteria.setHeight((int) detail.getHeight() * 2);
      criteria.setY((int) (detail.getY() - detail.getHeight()));
    }

    return new SignRequest.SignCriteria(docUrl, base64, criteria);
  }

  private Resource getDocumentFile(Project project, Document document) {
    Resource resource;
    log.info("Getting document file with name: {}...", document.getFileName());
    if (project.isOneSigned()) {
      log.info("Downloading signed document from API NG ...");
      byte[] bytes = this.apiNgFeignClient.downloadDocument(document.getDocUrl());
      resource = new ByteArrayResource(bytes);
    } else {
      resource =
          this.fileProvider.download(getDocPath(project.getFlowId(), document.getFileName()));
    }

    if (Objects.isNull(resource) || !resource.isReadable()) {
      throw new InternalErrorException("Cannot get document file....");
    }
    return resource;
  }

  private Path getDocPath(String flowId, String docFile) {
    return FileUtils.path(flowId, DocumentProcessingHandler.DOCUMENT_PATH).resolve(docFile);
  }

  private File generateSignatureImage(Participant participant, String signText, String flowId) {
    File signature;
    String basePath = fileProvider.basePath();
    Path projectDir = Path.of(basePath, flowId);

    // Old project before 30/09/2023 will not present signature mode property
    if (Objects.isNull(participant.getSignatureMode())
        || participant.getSignatureMode().equals(SignatureMode.WRITE.name())) {
      signature = PdfUtils.createSign(signText, participant, projectDir);
    } else {
      var signatureImage =
          Path.of(basePath, flowId, PdfUtils.SIGNATURE_FILE_PATH, participant.getSignatureImage());
      signature = PdfUtils.createImageSign(signText, participant, projectDir, signatureImage);
    }

    if (Objects.isNull(signature)) {
      throw new InternalErrorException("Cannot create sign image...");
    }

    return signature;
  }
}
