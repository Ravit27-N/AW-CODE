package com.innovationandtrust.project.service;

import static com.innovationandtrust.utils.commons.CommonUsages.now;

import com.innovationandtrust.project.enums.ProjectHistoryStatus;
import com.innovationandtrust.project.model.SignatoryProject;
import com.innovationandtrust.project.model.UserProjectResponse;
import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.model.entity.ProjectHistory;
import com.innovationandtrust.project.model.entity.Project_;
import com.innovationandtrust.project.model.entity.Signatory;
import com.innovationandtrust.project.model.entity.Signatory_;
import com.innovationandtrust.project.repository.ProjectHistoryRepository;
import com.innovationandtrust.project.repository.SignatoryRepository;
import com.innovationandtrust.project.restclient.ProfileFeignClient;
import com.innovationandtrust.project.service.specification.SignatorySpecification;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class SignatoryService extends CommonCrudService<SignatoryDto, Signatory, Long> {

  private static final String SIGNATORY_NOT_FOUND = "Signatory Not Found!";
  private final SignatoryRepository signatoryRepository;
  private final ProjectHistoryRepository projectHistoryRepository;
  private final ProfileFeignClient profileFeignClient;

  @Autowired
  public SignatoryService(
      SignatoryRepository signatoryRepository,
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      ProjectHistoryRepository projectHistoryRepository,
      ProfileFeignClient profileFeignClient) {
    super(modelMapper, keycloakProvider);
    this.signatoryRepository = signatoryRepository;
    this.projectHistoryRepository = projectHistoryRepository;
    this.profileFeignClient = profileFeignClient;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected Signatory findEntityById(long id) {
    return signatoryRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(SIGNATORY_NOT_FOUND));
  }

  /**
   * Retrieves an {@link SignatoryDto} by its id.
   *
   * @param id must not be {@literal null}.
   * @return the {@link SignatoryDto} with the given id or {@literal Optional#empty()} if none found
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  @Override
  @Transactional(readOnly = true)
  public SignatoryDto findById(Long id) {
    return mapData(findEntityById(id), new SignatoryDto());
  }

  /**
   * Returns all instances of {@link SignatoryDto}.
   *
   * @return list of signatoryDTO
   */
  @Override
  @Transactional(readOnly = true)
  public List<SignatoryDto> findAll() {
    return signatoryRepository.findAll().stream().map(super::mapData).toList();
  }

  /**
   * Returns all signatory by projectId.
   *
   * @return list of SignatoryDto
   */
  @Transactional(readOnly = true)
  public List<SignatoryDto> findAllByProjectId(Long projectId) {
    return this.mapAll(
        signatoryRepository.findAll(
            Specification.where(SignatorySpecification.findAllByProjectId(projectId)),
            Sort.by(
                Sort.Direction.fromString(Sort.Direction.DESC.toString()), Signatory_.SORT_ORDER)),
        SignatoryDto.class);
  }

  /**
   * Returns page of SignatoryDto that filtered and paginate.
   *
   * @param pageable for pagination
   * @param filter   string for search name
   * @return a page of signatory
   */
  @Override
  @Transactional(readOnly = true)
  public Page<SignatoryDto> findAll(Pageable pageable, String filter) {

    return signatoryRepository
        .findAll(Specification.where(SignatorySpecification.searchByName(filter)), pageable)
        .map(super::mapData);
  }

  /**
   * Insert new signatory record.
   *
   * @param dto dta to insert into database
   * @return inserted record SignatoryDto
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public SignatoryDto save(SignatoryDto dto) {
    return this.mapData(signatoryRepository.save(this.mapEntity(dto)));
  }

  /**
   * Update signatory record.
   *
   * @param dto that must be contained id
   * @return updated record SignatoryDto
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public SignatoryDto update(SignatoryDto dto) {
    if (Objects.isNull(dto.getId()) || dto.getId() == 0) {
      throw new EntityNotFoundException(SIGNATORY_NOT_FOUND);
    }
    var entity = this.mapEntity(dto, this.findEntityById(dto.getId()));
    entity.setModifiedBy(this.getUserId());
    return this.mapData(signatoryRepository.save(entity));
  }

  /**
   * Save all signatories.
   *
   * @param signatoryDtos refers to a list of signatories that have to be saved into the database.
   * @return List of signatoryDTO
   */
  @Transactional(rollbackFor = Exception.class)
  public List<SignatoryDto> saveAll(List<SignatoryDto> signatoryDtos) {
    var resultEntity = signatoryRepository.saveAll(mapAll(signatoryDtos, Signatory.class));
    return mapAll(resultEntity, SignatoryDto.class);
  }

  /**
   * To delete a signatory.
   *
   * @param id refers to the id of signatory that we want to delete.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    signatoryRepository.deleteById(id);
  }

  /**
   * Update status
   *
   * @param projectId refers to the project's id.
   * @param requests  refers to the list of signatoryRequest.
   */
  @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = Exception.class,
      isolation = Isolation.READ_UNCOMMITTED)
  public void updateStatus(Long projectId, List<SignatoryRequest> requests) {
    var signatories =
        this.signatoryRepository.findAll(
            SignatorySpecification.findByIds(
                requests.stream().map(SignatoryRequest::getId).toList()));
    log.info("Signatories ids: {}", signatories.stream().map(Signatory::getId).toList());

    log.info("Signatories request update: {}",
        requests.stream().map(SignatoryRequest::getId).toList());
    signatories.forEach(
        signer ->
            requests.stream()
                .filter(
                    req ->
                        Objects.equals(req.getId(), signer.getId())
                            && !Objects.equals(
                            signer.getInvitationStatus(), req.getInvitationStatus().name()))
                .findFirst()
                .ifPresent(
                    value -> {
                      signer.setInvitationStatus(value.getInvitationStatus().name());
                      signer.setUuid(value.getUuid());
                      signer.setSentDate(new Date());
                      var maxOrder = projectHistoryRepository.findMaxSortOrder(projectId);
                      projectHistoryRepository.save(
                          new ProjectHistory(
                              null,
                              now(),
                              ProjectHistoryStatus.getByInvitationStatus(
                                      value.getInvitationStatus())
                                  .name(),
                              String.format("%s %s", signer.getFirstName(), signer.getLastName()),
                              (int) (maxOrder + 1),
                              signer.getProject()));
                    }));
    this.signatoryRepository.saveAll(signatories);
  }

  public List<SignatoryDto> findByProjects(List<Long> projectIds) {
    return mapAll(
        this.signatoryRepository.findAll(
            AdvancedFilter.searchByField(
                Filter.builder()
                    .referenceField(Arrays.asList(Signatory_.PROJECT, Project_.ID))
                    .operator(QueryOperator.IN)
                    .values(projectIds.stream().map(String::valueOf).toList())
                    .build())),
        SignatoryDto.class);
  }

  /**
   * List all projects assigned to end-user by email.
   *
   * @param status to list projects signed and approved, or in progress
   * @return list of all projects
   */
  @Transactional(readOnly = true)
  public UserProjectResponse listEndUserProjects(Pageable pageable, String status) {
    var user = this.profileFeignClient.findUserById(this.getUserId());
    var inProgress =
        this.signatoryRepository.findAll(
            Specification.where(
                SignatorySpecification.filterByDocumentStatus(
                    user.getEmail(),
                    List.of(DocumentStatus.IN_PROGRESS.name()),
                    InvitationStatus.SENT.name())),
            pageable);
    var approvedOrSinged =
        this.signatoryRepository.findAll(
            Specification.where(
                SignatorySpecification.filterByDocumentStatus(
                    user.getEmail(),
                    List.of(DocumentStatus.SIGNED.name(), DocumentStatus.APPROVED.name()),
                    InvitationStatus.SENT.name())),
            pageable);
    var projects = new UserProjectResponse();
    Page<SignatoryProject> signatories;

    if (Objects.equals(status, DocumentStatus.IN_PROGRESS.name())) {
      signatories = inProgress.map(s -> this.modelMapper.map(s, SignatoryProject.class));
    } else {
      signatories = approvedOrSinged.map(s -> this.modelMapper.map(s, SignatoryProject.class));
    }
    List<String> usersId =
        signatories.getContent().stream()
            .map(p -> p.getProject().getCreatedBy().toString())
            .distinct()
            .toList();
    var users = this.profileFeignClient.findUsersByUsersId(usersId);

    signatories
        .getContent()
        .forEach(
            s -> {
              var createdBy =
                  users.stream()
                      .filter(u -> u.getId().equals(s.getProject().getCreatedBy()))
                      .findFirst()
                      .orElse(null);
              s.getProject().setCreatedByUser(createdBy);
            });

    projects.setSignatories(new EntityResponseHandler<>(signatories));
    projects.setTotalDone(approvedOrSinged.getTotalElements());
    projects.setTotalInProgress(inProgress.getTotalElements());
    return projects;
  }
}
