package com.innovationandtrust.project.service;

import com.innovationandtrust.project.model.dto.DocumentContent;
import com.innovationandtrust.project.model.dto.SignedDocumentDTO;
import com.innovationandtrust.project.model.entity.SignedDocument;
import com.innovationandtrust.project.repository.SignedDocumentRepository;
import com.innovationandtrust.project.service.specification.SignedDocumentSpecification;
import com.innovationandtrust.share.service.AbstractCrudService;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.file.exception.FileRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class SignedDocumentService
    extends AbstractCrudService<SignedDocumentDTO, SignedDocument, Long> {
  public static final String DIRECTORY_PATH = "signedDocument";
  private static final String NOT_FOUND_ERROR_MESSAGE = "SignedDocument not found";
  private final SignedDocumentRepository signedDocumentRepository;
  private final FileProvider fileProvider;

  public SignedDocumentService(
      ModelMapper modelMapper, SignedDocumentRepository fileRepository, FileProvider fileProvider) {
    super(modelMapper);
    this.signedDocumentRepository = fileRepository;
    this.fileProvider = fileProvider;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  private SignedDocument findEntityById(long id) {
    return this.signedDocumentRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_ERROR_MESSAGE));
  }

  /**
   * Returns all document by projectId
   *
   * @return all entities
   */
  @Transactional(readOnly = true)
  public List<SignedDocumentDTO> findBySignatoryId(Long signatoryId) {
    return this.mapAll(
        signedDocumentRepository.findAll(
            Specification.where(SignedDocumentSpecification.findAllBySignatoryId(signatoryId))),
        SignedDocumentDTO.class);
  }

  /**
   * Retrieves an {@link SignedDocumentDTO} by its id.
   *
   * @param id must not be {@literal null}.
   * @return the {@link SignedDocumentDTO} with the given id or {@literal Optional#empty()} if none
   *     found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  @Override
  @Transactional(readOnly = true)
  public SignedDocumentDTO findById(Long id) {
    return mapData(findEntityById(id));
  }

  /**
   * Insert new signedDocument record
   *
   * @param signedDocumentDTO dta to insert into database
   * @return inserted record SignatoryDto
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public SignedDocumentDTO save(SignedDocumentDTO signedDocumentDTO) {
    var entity = this.mapEntity(signedDocumentDTO);
    return this.mapData(this.signedDocumentRepository.save(entity));
  }

  /**
   * Download a signed document.
   *
   * @param id refers to signed document id
   * @return DocumentContent
   */
  public DocumentContent downloadSignedDocument(Long id) {
    try {
      var document = this.findById(id);
      var resource = this.fileProvider.download(document.getFullPath(), true);
      return new DocumentContent(
          resource,
          document.getFileName(),
          resource.getURL().openConnection().getContentType(),
          document.getContentLength());
    } catch (IOException exception) {
      log.error("Unable to download signed document: ", exception);
      throw new FileRequestException("Unable to download signed document" + exception.getMessage());
    }
  }
}
