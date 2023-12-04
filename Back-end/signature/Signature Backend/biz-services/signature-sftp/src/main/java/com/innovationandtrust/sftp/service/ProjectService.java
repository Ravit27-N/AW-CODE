package com.innovationandtrust.sftp.service;

import static com.innovationandtrust.utils.commons.CommonUsages.byteArrayToMultipartFile;

import com.innovationandtrust.sftp.constant.DocumentDetailConstant;
import com.innovationandtrust.sftp.exception.InvalidProjectDataException;
import com.innovationandtrust.sftp.exception.InvalidProjectDocumentException;
import com.innovationandtrust.sftp.restclient.ProjectFeignClient;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.sftp.DocumentDetailModel;
import com.innovationandtrust.share.model.sftp.ProjectDocumentModel;
import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.share.model.sftp.ProjectParticipantModel;
import com.innovationandtrust.share.service.ProjectGenerator;
import com.innovationandtrust.utils.commons.FormatValidator;
import com.innovationandtrust.utils.file.exception.FileNotFoundException;
import com.innovationandtrust.utils.file.model.FileResponse;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import com.innovationandtrust.utils.pdf.provider.PdfProvider;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
  private final FileProvider fileProvider;
  private final Validator validator;
  private final ProjectFeignClient projectFeignClient;
  private final IKeycloakTokenExchange keycloakTokenExchange;
  private final FileService fileService;
  private final PdfProvider pdfProvider;

  private static final String LINE_BREAK = "\n";
  private final List<String> actorEmails = new ArrayList<>();

  /**
   * Download a sample project XML file.
   *
   * @return Resource of the project XML file.
   */
  public Resource getSampleXml() {
    var resource = ProjectGenerator.downloadSample();
    if (resource == null) {
      throw new FileNotFoundException("Could not find sample XML file");
    }
    return resource;
  }

  public void insertFileToZipped(
      String corporateUuid,
      String zipFile,
      MultipartFile[] signedFiles,
      MultipartFile manifestFile) {
    this.fileService.insertFilesToZipped(corporateUuid, zipFile, signedFiles, manifestFile);
  }

  /**
   * Push project to Project management for creation.
   *
   * @param projectModel prepared project model.
   * @return true if successful.
   */
  public boolean createProject(ProjectModel projectModel) {
    try {
      var projectDocuments = projectModel.getDocuments();
      if (projectModel.isMergeDocuments()) {
        projectDocuments = this.mergeDocuments(projectModel.getDocuments());
      }

      var token =
          String.format(
              "Bearer %s", keycloakTokenExchange.getToken(projectModel.getAuthor().getUserUuid()));
      List<FileResponse> files =
          this.projectFeignClient.uploadDocument(
              token, this.convertToMultipartFiles(projectDocuments), projectModel.getFlowId());
      var documents =
          projectDocuments.stream()
              .flatMap(
                  (ProjectDocumentModel doc) ->
                      files.stream()
                          .filter(fr -> Objects.equals(fr.getOriginalFileName(), doc.getFileName()))
                          .map(
                              (FileResponse obj) -> {
                                doc.setFullPath(obj.getFullPath());
                                doc.setFileName(obj.getFileName());
                                doc.setContentType(obj.getContentType());
                                doc.setExtension(
                                    FilenameUtils.getExtension(obj.getOriginalFileName()));
                                doc.getInfo().setSize(obj.getSize());
                                doc.getInfo().setOriginalFileName(obj.getFileName());
                                return doc;
                              }))
              .toList();
      projectModel.setDocuments(documents);
      return this.projectFeignClient.createProjectXML(projectModel, token);
    } catch (Exception e) {
      log.error("Failed to create project", e);
      return false;
    }
  }

  private List<ProjectDocumentModel> mergeDocuments(
      @NotEmpty List<ProjectDocumentModel> documentsModel) {
    if (documentsModel.size() <= 1) {
      return documentsModel;
    }
    var visibleSignatures = new ArrayList<DocumentDetailModel>();
    var numberOfMergedPdf = new AtomicInteger(0);
    List<File> filesToMerge = new ArrayList<>();
    documentsModel.forEach(
        (ProjectDocumentModel doc) -> {
          doc.getDocumentDetails()
              .forEach(
                  (DocumentDetailModel v) -> {
                    v.setPageNum(v.getPageNum() + numberOfMergedPdf.get());
                    visibleSignatures.add(v);
                  });
          numberOfMergedPdf.getAndAdd(getPageNumber(doc, filesToMerge));
        });

    var mergePath = Path.of(documentsModel.get(0).getFullPath());
    this.pdfProvider.mergePdf(filesToMerge, mergePath.toString());

    var document = new ProjectDocumentModel();
    document.setFullPath(mergePath.toString());
    document.setFileName(FilenameUtils.getName(mergePath.toString()));
    document.setExtension(FilenameUtils.getExtension(mergePath.toString()));
    document.setContentType(MediaType.APPLICATION_PDF_VALUE);
    document.setDocumentDetails(visibleSignatures);
    document.setInfo(new Document());
    return List.of(document);
  }

  private static Integer getPageNumber(
      ProjectDocumentModel documentModel, List<File> filesToMerge) {
    try (var document = new PdfDocument(new PdfReader(documentModel.getFullPath())); ) {
      var file = new File(documentModel.getFullPath());
      filesToMerge.add(file);
      return document.getNumberOfPages();
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private List<MultipartFile> convertToMultipartFiles(List<ProjectDocumentModel> documents) {
    return documents.stream()
        .map(
            doc ->
                byteArrayToMultipartFile(
                    this.fileProvider.loadFile(Path.of(doc.getFullPath()), true),
                    doc.getFileName(),
                    MediaType.APPLICATION_PDF))
        .toList();
  }

  /**
   * Validate project content.
   *
   * @param source the extracted xml file to read.
   * @param pdfFiles the extracted pdf files.
   * @return the valid project model.
   */
  public ProjectModel validateProject(String source, List<String> pdfFiles) {
    ProjectModel projectModel = this.fileProvider.readXmlValue(Path.of(source), ProjectModel.class);
    this.validateProjectData(projectModel, pdfFiles);
    return projectModel;
  }

  private void validateProjectData(ProjectModel projectModel, List<String> pdfFiles) {
    Set<ConstraintViolation<ProjectModel>> violations = validator.validate(projectModel);
    var sb = new StringBuilder();
    if (!projectModel.hasSignatory()) {
      sb.append("-Participants: at least have one signatory.").append(LINE_BREAK);
    }
    if (!violations.isEmpty()) {
      for (ConstraintViolation<ProjectModel> constraintViolation : violations) {
        sb.append(constraintViolation.getPropertyPath());
        sb.append(": ");
        sb.append(constraintViolation.getMessage()).append(LINE_BREAK);
      }
    }
    if (!projectModel.isValidDocuments(pdfFiles)) {
      throw new InvalidProjectDocumentException("Document does not match with xml");
    }
    sb.append(checkParticipants(projectModel));
    // Validate documentDetails
    sb.append(this.validateDocumentDetails(projectModel));

    if (StringUtils.hasText(sb.toString())) {
      throw new InvalidProjectDataException(sb.toString());
    }
  }

  private String validateDocumentDetails(ProjectModel projectModel) {
    var errorMessage = new StringBuilder();
    projectModel
        .getDocuments()
        .forEach(
            (ProjectDocumentModel d) ->
                d.getDocumentDetails()
                    .forEach(
                        (DocumentDetailModel dt) -> {
                          dt.setContentType(DocumentDetailConstant.TEXT_SIGNATURE);
                          dt.setWidth(DocumentDetailConstant.WIDTH);
                          dt.setHeight(DocumentDetailConstant.HEIGHT);
                          dt.setFontSize(DocumentDetailConstant.FONT_SIZE);
                          var info = new Document();
                          info.setOriginalFileName(d.getFileName());
                          // Load the pdf file
                          try (var pdf = new PdfDocument(new PdfReader(d.getFullPath()))) {
                            info.setTotalPages(pdf.getNumberOfPages());
                            errorMessage.append(this.checkPdf(pdf, dt));
                          } catch (IOException e) {
                            info.setTotalPages(0);
                            log.error("Failed to load PDF pages: ", e);
                            throw new InvalidProjectDocumentException(
                                "Failed to load PDF pages: " + e.getMessage());
                          }
                          d.setInfo(info);
                        }));
    if (!errorMessage.isEmpty()) {
      errorMessage.insert(0, "DocumentDetails validation failed:");
    }
    return errorMessage.toString();
  }

  private String checkPdf(PdfDocument pdf, DocumentDetailModel dt) {
    var errorMessage = new StringBuilder();
    if (!this.actorEmails.contains(dt.getSignatoryEmail())) {
      errorMessage
          .append(" -Email address  ")
          .append(dt.getSignatoryEmail())
          .append(" does not match any signatory/approve email.")
          .append(LINE_BREAK);
    }
    if (dt.getPageNum() < 0 || dt.getPageNum() > pdf.getNumberOfPages()) {
      errorMessage.append(" -Invalid pageNum of pdf, pdf has only 1 page(s).").append(LINE_BREAK);
    }
    var size = pdf.getFirstPage().getPageSize();
    if (dt.getX() > size.getWidth() || dt.getX() < 0) {
      errorMessage.append(" -Position is invalid, x: ").append(dt.getX()).append(LINE_BREAK);
    }
    if (dt.getY() > size.getHeight() || dt.getY() < 0) {
      errorMessage.append(" -Position is invalid, y: ").append(dt.getY()).append(LINE_BREAK);
    }
    return errorMessage.toString();
  }

  private String checkParticipants(ProjectModel projectModel) {
    var errorMessage = new StringBuilder();
    projectModel
        .getParticipants()
        .forEach(
            (ProjectParticipantModel p) -> {
              if (FormatValidator.isValidEmailAddress(p.getEmail())) {
                if (p.isActor()) {
                  this.actorEmails.add(p.getEmail());
                }
              } else {
                errorMessage
                    .append(" -Email address  ")
                    .append(p.getEmail())
                    .append(" is invalid")
                    .append(LINE_BREAK);
              }
              if (!FormatValidator.isValidPhoneNumber(p.getPhone())) {
                errorMessage
                    .append(" -Phone number ")
                    .append(p.getPhone())
                    .append(" is invalid.")
                    .append(LINE_BREAK);
              }
            });
    if (!errorMessage.isEmpty()) {
      errorMessage.insert(0, "Participants validation failed:\n");
    }
    return errorMessage.toString();
  }
}
