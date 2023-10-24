package com.innovationandtrust.project.service;

import static org.apache.pdfbox.Loader.loadPDF;

import com.innovationandtrust.project.model.dto.DocumentContent;
import com.innovationandtrust.project.model.dto.DocumentDTO;
import com.innovationandtrust.project.model.entity.Document;
import com.innovationandtrust.project.model.entity.Signatory;
import com.innovationandtrust.project.model.entity.SignedDocument;
import com.innovationandtrust.project.repository.FileRepository;
import com.innovationandtrust.project.repository.SignedDocumentRepository;
import com.innovationandtrust.project.service.specification.DocumentSpecification;
import com.innovationandtrust.share.model.project.DocumentRequest;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.exception.exceptions.ApiRequestException;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.file.exception.FileRequestException;
import com.innovationandtrust.utils.file.model.FileResponse;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** DocumentService. */
@Service
@Slf4j
@Transactional
public class DocumentService extends CommonCrudService<DocumentDTO, Document, Long> {
  private static final String NOT_FOUND_ERROR_MESSAGE = "Document not found";
  private final FileRepository fileRepository;
  private final FileProvider fileProvider;
  private final SignedDocumentRepository signedDocumentRepository;
  private final ApiNgFeignClientFacade apiNgFeignClient;

  protected DocumentService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      FileRepository fileRepository,
      FileProvider fileProvider,
      SignedDocumentRepository signedDocumentRepository,
      ApiNgFeignClientFacade apiNgFeignClient) {
    super(modelMapper, keycloakProvider);
    this.fileRepository = fileRepository;
    this.fileProvider = fileProvider;
    this.signedDocumentRepository = signedDocumentRepository;
    this.apiNgFeignClient = apiNgFeignClient;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected Document findEntityById(long id) {
    return this.fileRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_ERROR_MESSAGE));
  }

  /**
   * Save file data records to the database.
   *
   * @param files findAll of files information
   * @param projectId is used for add files to project with that id
   * @return List of DocumentDTO
   */
  public List<DocumentDTO> save(List<FileResponse> files, Long projectId) {
    List<DocumentDTO> documentDTOList = new ArrayList<>();

    for (FileResponse file : files) {
      if (!file.getContentType().equals("application/pdf"))
        throw new FileRequestException("Document must be pdf file");

      DocumentDTO documentDTO = new DocumentDTO();
      documentDTO.setFileName(file.getFileName());
      documentDTO.setOriginalFileName(file.getOriginalFileName());
      documentDTO.setContentType(file.getContentType());
      documentDTO.setSize(file.getSize());
      documentDTO.setFullPath(file.getFullPath());
      documentDTO.setExtension(FilenameUtils.getExtension(file.getFileName()));
      documentDTO.setProjectId(projectId);

      // Load the pdf file
      try (PDDocument pdfDoc = loadPDF(new File(file.getFullPath()))) {
        documentDTO.setTotalPages(pdfDoc.getNumberOfPages());
      } catch (IOException e) {
        log.error("Failed to load PDF pages: ", e);
        throw new ApiRequestException("Failed to load all pdf pages!");
      }
      var insertedFile = this.fileRepository.save(this.mapEntity(documentDTO));
      documentDTOList.add(this.mapData(insertedFile));
    }

    return documentDTOList;
  }

  /**
   * Returns all document by projectId.
   *
   * @return all entities
   */
  @Transactional(readOnly = true)
  public List<DocumentDTO> findAllByProjectId(Long projectId) {
    return this.mapAll(
        fileRepository.findAll(
            Specification.where(DocumentSpecification.findAllByProjectId(projectId))),
        DocumentDTO.class);
  }

  /**
   * View document.
   *
   * @param docName refers to document name
   * @return DocumentContent
   */
  public DocumentContent viewDocument(String docName) {
    try {
      Resource resource = fileProvider.download(docName, false);

      return new DocumentContent(resource, resource.getURL().openConnection().getContentType());
    } catch (Exception exception) {
      throw new FileRequestException("Unable to view the document: " + exception.getMessage());
    }
  }

  /**
   * View document in base64.
   *
   * @param docName refers to document name
   * @return a string of base64
   */
  public String viewDocumentBase64(String docName) {
    try {
      Resource resource = fileProvider.download(docName, false);
      return encodeFileToBase64(resource);
    } catch (Exception exception) {
      log.error("Load document failed", exception);
      throw new FileRequestException("Unable to view the document: " + exception.getMessage());
    }
  }

  /**
   * Download document.
   *
   * @param docId refers to a signed document id.
   * @return DocumentContent
   */
  @Transactional(readOnly = true)
  public DocumentContent downloadDocument(Long docId) {
    try {
      var document = this.fileRepository.findById(docId);
      if (document.isPresent()) {
        var doc = document.get();
        if (StringUtils.hasText(doc.getSignedDocUrl())) {
          var response = this.apiNgFeignClient.downloadDocument(doc.getSignedDocUrl());
          return new DocumentContent(
              new ByteArrayResource(response),
              doc.getOriginalFileName(),
              doc.getContentType(),
              response.length);
        } else {
          return new DocumentContent(
              this.fileProvider.download(doc.getFileName(), false),
              doc.getOriginalFileName(),
              doc.getContentType(),
              doc.getSize());
        }
      }
      throw new FileRequestException("Unable to download the document");
    } catch (Exception exception) {
      log.error("Unable to download the document", exception);
      throw new FileRequestException("Unable to download the document: " + exception.getMessage());
    }
  }

  /**
   * Download a signed document.
   *
   * @param docId refers to a signed document id.
   * @return DocumentContent
   */
  @Transactional(readOnly = true)
  public DocumentContent downloadSignedDocument(Long docId) {
    var document =
        this.fileRepository
            .findById(docId)
            .orElseThrow(() -> new FileRequestException("Unable to download the signed document"));

    if (StringUtils.hasText(document.getSignedDocUrl())) {
      var response = this.apiNgFeignClient.downloadDocument(document.getSignedDocUrl());
      return new DocumentContent(
          new ByteArrayResource(response),
          document.getOriginalFileName(),
          document.getContentType(),
          response.length);
    }
    throw new FileRequestException("Unable to download the signed document");
  }

  /**
   * Download original document
   *
   * @param docId refers to document name
   * @return DocumentContent
   */
  @Transactional(readOnly = true)
  public DocumentContent downloadOriginalDocument(Long docId) {
    var document =
        this.fileRepository
            .findById(docId)
            .orElseThrow(() -> new FileRequestException("Unable to find document"));

    return new DocumentContent(
        this.fileProvider.download(document.getFileName(), false),
        document.getOriginalFileName(),
        document.getContentType(),
        document.getSize());
  }

  public String encodeFileToBase64(Resource resource) {
    try {
      return Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
    } catch (IOException e) {
      log.error("Error encoding to base64", e);
      throw new FileRequestException(
          String.format("Unable to covert file to base64  because %s", e.getMessage()));
    }
  }

  /**
   * Update document record.
   *
   * @param documentDTO that must be contained id
   * @return updated record SignatoryDto
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public DocumentDTO update(DocumentDTO documentDTO) {
    if (Objects.isNull(documentDTO.getId()) || documentDTO.getId() == 0)
      throw new EntityNotFoundException(NOT_FOUND_ERROR_MESSAGE);

    return this.mapData(this.fileRepository.save(this.mapEntity(documentDTO)));
  }

  /**
   * Retrieves an {@link DocumentDTO} by its id.
   *
   * @param id must not be {@literal null}.
   * @return the {@link DocumentDTO} with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  @Override
  @Transactional(readOnly = true)
  public DocumentDTO findById(Long id) {
    return mapData(findEntityById(id), new DocumentDTO());
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateSignedDocUrl(ProjectAfterSignRequest project) {
    var documents =
        this.fileRepository.findAllById(
            project.getDocuments().stream().map(DocumentRequest::getId).toList());
    documents.forEach(
        doc ->
            project.getDocuments().stream()
                .filter(req -> Objects.equals(doc.getId(), req.getId()))
                .findAny()
                .ifPresent(
                    value -> {
                      doc.setSignedDocUrl(value.getSignedDocUrl());
                      if (project.getSignatory() != null) {
                        this.saveSignedDocument(doc, project.getSignatory().getId());
                      }
                    }));
    this.fileRepository.saveAll(documents);
  }

  /**
   * Download and save the signed document.
   *
   * @param document to notice which this signed document from
   * @param signatoryId to notice who signed this signed document
   */
  private void saveSignedDocument(Document document, Long signatoryId) {
    var bytes = this.apiNgFeignClient.downloadDocument(document.getSignedDocUrl());
    var fileName = signatoryId + "-" + document.getFileName();
    var uploaded =
        this.fileProvider.upload(
            new ByteArrayResource(bytes), SignedDocumentService.DIRECTORY_PATH, fileName);
    var signatory = new Signatory();
    signatory.setId(signatoryId);
    this.signedDocumentRepository.save(
        new SignedDocument(null, fileName, uploaded.getFullPath(), bytes.length, signatory));
  }
}
