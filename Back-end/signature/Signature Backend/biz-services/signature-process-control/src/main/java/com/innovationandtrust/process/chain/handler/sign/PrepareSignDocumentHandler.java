package com.innovationandtrust.process.chain.handler.sign;

import static com.innovationandtrust.utils.commons.CommonUsages.getFirstUpperCase;

import com.innovationandtrust.process.constant.PathConstant;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.utils.PdfUtils;
import com.innovationandtrust.share.enums.SignatureMode;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.DocumentDetail;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class PrepareSignDocumentHandler extends AbstractExecutionHandler {

  private final ApiNgFeignClientFacade apiNgFeignClient;
  private final ModelMapper modelMapper;
  private final FileProvider fileProvider;

  public PrepareSignDocumentHandler(
      ApiNgFeignClientFacade apiNgFeignClient, ModelMapper modelMapper, FileProvider fileProvider) {
    this.apiNgFeignClient = apiNgFeignClient;
    this.modelMapper = modelMapper;
    this.fileProvider = fileProvider;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    var uuid = context.get(SignProcessConstant.PARTICIPANT_ID, String.class);
    // Multiple sign have the same sign date.
    var signedDate = context.get(SignProcessConstant.SIGNED_DATE, Date.class);

    log.info("Getting participant:{}...", uuid);
    project
        .getParticipantByUuid(uuid)
        .ifPresent(
            participant -> {
              this.validateSigner(participant);

              var signRequest =
                  new SignRequest(
                      participant.getActorUrl(),
                      participant.getCertificate(),
                      project.getTemplate().getSignProcess().getVal());
              participant.setSignedDate(Objects.isNull(signedDate) ? new Date() : signedDate);
              this.prepareSignDocuments(project, signRequest, participant);

              context.put(SignProcessConstant.PARTICIPANT, participant);
              context.put(SignProcessConstant.API_NG_SIGN_REQUEST, signRequest);
            });

    return ExecutionState.NEXT;
  }

  private void validateSigner(Participant participant) {

    if (Objects.isNull(participant.getOtp()) || !participant.getOtp().isValidated()) {
      throw new IllegalArgumentException(
          "Invalid the signing process! The signer is not confirm the OTP code yet!");
    }

    if (participant.isSigner() && participant.isProcessing()) {
      throw new IllegalArgumentException("The documents are processing, cannot be process again!");
    }

    if (participant.isSigner() && participant.isSigned()) {
      throw new IllegalArgumentException(
          "The documents are already signed and cannot be processed again!");
    }

    if (participant.isApprover() && participant.isApproved()) {
      throw new IllegalArgumentException(
          "The documents are already approved and cannot be processed again!");
    }
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

    var tempPath =
        PdfUtils.buildTempPath(
            Path.of(
                fileProvider.basePath(),
                String.valueOf(getDocPath(project.getFlowId(), document.getFileName()))),
            participant.getId());

    PDDocument pdDocument = Loader.loadPDF(resource.getContentAsByteArray());
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

    // check and for multiple documents
    if (!StringUtils.hasText(participant.getShortName())) {
      participant.setShortName(
          (project.countSigned() > 0 ? "/" : "") + getFirstUpperCase(participant.getFullName()));

      project.setParaph(project.getParaph() + participant.getShortName());
      log.info("Generated participant name {} as paraphrase", participant.getShortName());
    }

    log.info("Generating paraphrase annotations on all pages for {}...", participant.getFullName());
    document.getDetails().stream()
        .filter(detail -> detail.getType().contains(PdfUtils.PARAPH))
        .findFirst()
        .ifPresentOrElse(
            detail -> {
              try {
                PdfUtils.setParaphraseAnnotations(
                    pdDocument, project, participant, detail, fileProvider.basePath());
              } catch (Exception e) {
                var message = "Error while setting paraphrase...";
                log.error(message, e);
                throw new InternalErrorException(message);
              }
            },
            () -> log.info("There is no paraphrase in this project..."));

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
      // Generated signature file in PNG.
      var imagePath =
          Path.of(
              fileProvider.basePath(),
              project.getFlowId(),
              PathConstant.SIGNATURE_PATH,
              participant.getUuid() + ".png");
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
    log.info(
        "Deleting prepared document {} of participant {} {}",
        path,
        participant.getId(),
        participant.getFullName());
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
      criteria.setY(Math.max((int) (detail.getY() - detail.getHeight()), 0));
    }

    return new SignRequest.SignCriteria(docUrl, base64, criteria);
  }

  private Resource getDocumentFile(Project project, Document document) {
    Resource resource;

    log.info("Getting document file with name: {}...", document.getFileName());
    // For no one signed, It will get original document
    if (!project.isOneSigned()) {
      log.info("Getting original document.");
      resource =
          this.fileProvider.download(getDocPath(project.getFlowId(), document.getFileName()));
    } else {

      // For someone has been signed, It will get current signed document
      final var path =
          Path.of(
              this.fileProvider.basePath(),
              project.getFlowId(),
              PathConstant.SIGNED_DOCUMENT_PATH,
              document.getFileName());

      if (Files.exists(path)) {
        try {
          log.info("Downloading signed document from {}", PathConstant.SIGNED_DOCUMENT_PATH);
          resource = new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException exception) {
          log.error("Cannot download document", exception);
          resource = new ByteArrayResource(getFromApiNg(document.getDocUrl()));
        }
      } else {
        resource = new ByteArrayResource(getFromApiNg(document.getDocUrl()));
      }
    }

    if (Objects.isNull(resource) || !resource.isReadable()) {
      throw new InternalErrorException("Cannot get document file....");
    }

    return resource;
  }

  private byte[] getFromApiNg(String docUrl) {
    log.info("Downloading signed document from API NG ...");
    return this.apiNgFeignClient.downloadDocument(docUrl);
  }

  private Path getDocPath(String flowId, String docFile) {
    return FileUtils.path(flowId, PathConstant.DOCUMENT_PATH).resolve(docFile);
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
          Path.of(
              basePath, flowId, PathConstant.SIGNATURE_FILE_PATH, participant.getSignatureImage());
      signature = PdfUtils.createImageSign(signText, participant, projectDir, signatureImage);
    }

    if (Objects.isNull(signature)) {
      throw new InternalErrorException("Cannot create sign image...");
    }

    return signature;
  }
}
