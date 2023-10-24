package com.innovationandtrust.project.service;

import com.innovationandtrust.project.constant.FilterByConstant;
import com.innovationandtrust.project.enums.ProjectHistoryStatus;
import com.innovationandtrust.project.exception.CannotCancelProjectException;
import com.innovationandtrust.project.model.dto.ProjectDTO;
import com.innovationandtrust.project.model.dto.ProjectHistoryDTO;
import com.innovationandtrust.project.model.dto.ProjectRequest;
import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.model.entity.AbstractBaseEntity_;
import com.innovationandtrust.project.model.entity.Project;
import com.innovationandtrust.project.model.entity.Project_;
import com.innovationandtrust.project.model.entity.Signatory;
import com.innovationandtrust.project.repository.ProjectHistoryRepository;
import com.innovationandtrust.project.repository.ProjectRepository;
import com.innovationandtrust.project.restclient.ProcessControlFeignClient;
import com.innovationandtrust.project.restclient.ProfileFeignClient;
import com.innovationandtrust.project.service.specification.ProjectSpecification;
import com.innovationandtrust.project.utils.DateUtil;
import com.innovationandtrust.project.utils.ProjectUtil;
import com.innovationandtrust.share.constant.DashboardConstant;
import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.NotificationChannel;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.ChartItem;
import com.innovationandtrust.share.model.corporateprofile.DashboardDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.share.model.project.Document;
import com.innovationandtrust.share.model.project.InvitationMessage;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.ProjectDetail;
import com.innovationandtrust.share.model.project.ProjectUpdateRequest;
import com.innovationandtrust.utils.aping.constant.ApiNgConstant;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.CommonUsages;
import com.innovationandtrust.utils.commons.CommonValidations;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientException;
import com.innovationandtrust.utils.exception.exceptions.ForbiddenRequestException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.InvalidTTLValueException;
import com.innovationandtrust.utils.exception.exceptions.UnauthorizedException;
import com.innovationandtrust.utils.file.exception.FileRequestException;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/** Project logical. */
@Slf4j
@Service
@Transactional
public class ProjectService extends CommonCrudService<ProjectDTO, Project, Long> {

  private static final String PROJECT_NOT_FOUND = "Project Not Found!";
  private static final String PROJECT_UNAUTHOR = "Project unauthorized";
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private final ProjectRepository projectRepository;
  private final SignatoryService signatoryService;
  private final ProjectHistoryService projectHistoryService;
  private final DocumentDetailService documentDetailService;
  private final DocumentService documentService;
  private final ProcessControlFeignClient processControlFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final ProfileFeignClient profileFeignClient;
  private final ProjectUtil projectUtil;
  private final ProjectHistoryRepository projectHistoryRepository;
  private final FileProvider fileProvider;

  /** Independence injections. */
  @Autowired
  public ProjectService(
      ProjectRepository projectRepository,
      ModelMapper modelMapper,
      SignatoryService signatoryService,
      ProjectHistoryService projectHistoryService,
      DocumentDetailService documentDetailService,
      IKeycloakProvider keycloakProvider,
      ProcessControlFeignClient processControlFeignClient,
      DocumentService documentService,
      CorporateProfileFeignClient corporateProfileFeignClient,
      ProfileFeignClient profileFeignClient,
      ProjectUtil projectUtil,
      ProjectHistoryRepository projectHistoryRepository,
      FileProvider fileProvider) {
    super(modelMapper, keycloakProvider);
    this.projectRepository = projectRepository;
    this.signatoryService = signatoryService;
    this.projectHistoryService = projectHistoryService;
    this.documentDetailService = documentDetailService;
    this.processControlFeignClient = processControlFeignClient;
    this.documentService = documentService;
    this.corporateProfileFeignClient = corporateProfileFeignClient;
    this.profileFeignClient = profileFeignClient;
    this.projectUtil = projectUtil;
    this.projectHistoryRepository = projectHistoryRepository;
    this.fileProvider = fileProvider;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws EntityNotFoundException if {@literal id} is {@literal null}.
   */
  protected Project findEntityById(long id) {
    return this.projectRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND));
  }

  /**
   * Find all projects in pagination.
   *
   * @param pageable refers to pageable use for pagination.
   * @param filter refers to what client what to filter or search.
   * @return a page of ProjectDTO.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<ProjectDTO> findAll(Pageable pageable, String filter) {

    return this.projectRepository
        .findAll(Specification.where(ProjectSpecification.searchByName(filter)), pageable)
        .map(super::mapData);
  }

  /**
   * To retrieve projects base on provided criteria.
   *
   * @param pageable refers to the object of {@link Pageable}
   * @param filter refers to any {@link String} used for query
   * @param statuses refers to the list of status of {@link Project}
   * @param filterSteps refers to steps of projects
   * @param filterBy refers the option of criteria
   * @return pagination of {@link ProjectDTO}
   */
  @Transactional(readOnly = true)
  public Page<ProjectDTO> findAllByFilter(
      Pageable pageable,
      String filter,
      List<String> filterSteps,
      String filterBy,
      List<String> statuses,
      String startDate,
      String endDate) {
    return this.getByCreatedBy(
        pageable, filter, filterSteps, getUsersIdByFilter(filterBy), statuses, startDate, endDate);
  }

  private List<Long> getUsersIdByFilter(String filterBy) {
    List<Long> usersId = new ArrayList<>();
    switch (filterBy) {
      case FilterByConstant.BUSINESS_UNIT -> usersId = this.findAllByDepartment();

      case FilterByConstant.USER -> usersId.add(this.getUserId());

      default -> usersId = this.profileFeignClient.findUserInTheSameCompany(this.getUserId());
    }
    return usersId.stream().filter(Objects::nonNull).toList();
  }

  /**
   * Find projects by user.
   *
   * @param pageable refers to pageable use for pagination.
   * @param filter refers to what client what to filter or search.
   * @return a page of ProjectDTO by user.
   */
  @Transactional(readOnly = true)
  public Page<ProjectDTO> findAllByUser(Pageable pageable, String filter, String status) {

    var filters =
        new ArrayList<>(
            List.of(
                Filter.builder()
                    .fields(Arrays.asList(AbstractBaseEntity_.CREATED_BY, Project_.ASSIGNED_TO))
                    .value(this.getUserId().toString())
                    .operator(QueryOperator.OR_FIELDS)
                    .build()));
    if (StringUtils.hasText(filter)) {
      filters.add(
          Filter.builder().field(Project_.NAME).value(filter).operator(QueryOperator.LIKE).build());
    }

    if (StringUtils.hasText(status)) {
      filters.add(
          Filter.builder()
              .field(Project_.STATUS)
              .operator(QueryOperator.EQUALS)
              .value(status)
              .build());
    }

    return this.projectRepository
        .findAll(AdvancedFilter.searchByFields(filters), pageable)
        .map(super::mapData);
  }

  /**
   * Find all projects by corporates.
   *
   * @param pageable refers to pageable use for pagination.
   * @param filter refers to what client what to filter or search.
   * @param filterSteps refers to step that will be used to filter.
   * @return a page of ProjectDTO by corporate.
   */
  @Transactional(readOnly = true)
  public Page<ProjectDTO> findAllByCorporate(
      Pageable pageable,
      Long userId,
      String filter,
      List<String> filterSteps,
      List<String> statuses,
      String startDate,
      String endDate) {
    log.info("Corporate admin getting users projects...");

    List<Long> usersId = this.getUserIdsByCorporateFilter(userId);
    if (usersId.isEmpty()) {
      log.warn("There are no users...");
      return Page.empty();
    }

    return this.getByCreatedBy(
        pageable, filter, filterSteps, usersId, statuses, startDate, endDate);
  }

  /**
   * To retrieve all users in a department.
   *
   * @return return the collection of {@link Long} as user's identity
   */
  private List<Long> findAllByDepartment() {
    return this.corporateProfileFeignClient.getEmployeesByUser(this.getUserId()).stream()
        .map(EmployeeDTO::getUserId)
        .toList();
  }

  /**
   * Find all by users in a company.
   *
   * @param pageable refers to pageable use for pagination.
   * @param filter refers to what client what to filter or search.
   * @param filterSteps refers to step that will be used to filter.
   * @return a page of ProjectDTO by corporate.
   */
  @Transactional(readOnly = true)
  public Page<ProjectDTO> findAllByCompany(
      Pageable pageable, String filter, List<String> filterSteps) {
    List<Long> usersId = this.profileFeignClient.findUserInTheSameCompany(this.getUserId());

    return this.getByCreatedBy(
        pageable, filter, filterSteps, usersId, Collections.emptyList(), "", "");
  }

  /**
   * Find all projects by list of users id.
   *
   * @param pageable for pagination
   * @param filter for search project name
   * @param filterSteps for project step 1,2,3,4
   * @param statuses refers to the list of status of {@link Project}
   * @param userIds list of project createdBy
   * @return pagination of ProjectDTO
   */
  private Page<ProjectDTO> getByCreatedBy(
      Pageable pageable,
      String filter,
      List<String> filterSteps,
      List<Long> userIds,
      List<String> statuses,
      String startDate,
      String endDate) {

    if (userIds.isEmpty()) {
      return new PageImpl<>(List.of());
    }

    log.info("Building filtering projects...");
    var specs =
        Specification.where(
            Objects.requireNonNull(ProjectSpecification.searchByAssignedToOrCreatedBy(userIds)));

    if (!filterSteps.isEmpty()) {
      specs = specs.and(ProjectSpecification.filterBySteps(filterSteps));
    }

    if (StringUtils.hasText(filter)) {
      specs = specs.and(ProjectSpecification.searchByNameAndSignatoryName(filter));
    }

    if (!statuses.isEmpty()) {
      specs =
          specs.and(
              AdvancedFilter.searchByField(
                  Filter.builder()
                      .field(Project_.STATUS)
                      .operator(QueryOperator.IN)
                      .values(statuses)
                      .build()));
    }

    if (StringUtils.hasText(startDate)) {
      var start = DateUtil.convertFrom(startDate, DATE_FORMAT);
      Date end;
      if (StringUtils.hasText(endDate)) {
        end = DateUtil.plushDays(DateUtil.convertFrom(endDate, DATE_FORMAT), 1);
      } else {
        end = DateUtil.plushDays(start, 1);
      }
      specs =
          specs.and(
              AdvancedFilter.searchByField(
                  Filter.builder()
                      .field(Project_.EXPIRE_DATE)
                      .operator(QueryOperator.BETWEEN_DATE)
                      .values(
                          Arrays.asList(DateUtil.convertToUtc(start), DateUtil.convertToUtc(end)))
                      .build()));
    }

    log.info("Getting projects from database...");
    return this.projectRepository.findAll(specs, pageable).map(super::mapData);
  }

  /**
   * Insert new project record.
   *
   * @param projectDto project data for inserting
   * @return inserted record ProjectDTO
   */
  @Transactional(rollbackFor = Exception.class)
  public ProjectDTO save(ProjectDTO projectDto, MultipartFile[] files) {

    if (files.length == 0) {
      throw new FileRequestException("Document is required!");
    }

    projectDto.setCreatedBy(this.getUserId());
    var entity = this.mapEntity(projectDto);
    entity.setCreatedBy(this.getUserId());
    var templateId = entity.getTemplateId();
    if (Objects.nonNull(templateId)) {
      Template template = this.profileFeignClient.findTemplateById(entity.getTemplateId());
      entity.setTemplateName(template.getName());
      this.profileFeignClient.increaseTemplateUsed(templateId);
    }
    entity.setSignatureLevel(SignatureSettingLevel.SIMPLE.name());

    var project = this.mapData(this.projectRepository.save(entity));

    project.setHistories(new ArrayList<>(List.of(this.saveProjectStep1(project.getId()))));
    return project;
  }

  /**
   * Update signatory and document status.
   *
   * @param projectAfterSignRequest for update status
   */
  @Transactional(rollbackFor = Exception.class)
  public void updateProjectAfterSigned(ProjectAfterSignRequest projectAfterSignRequest) {
    if (projectAfterSignRequest.getSignatory() != null) {
      var signatory =
          this.signatoryService.findById(projectAfterSignRequest.getSignatory().getId());
      var status = projectAfterSignRequest.getSignatory().getDocumentStatus();
      signatory.setComment(projectAfterSignRequest.getSignatory().getComment());
      signatory.setDocumentStatus(status.name());
      signatory.setDateStatus(new Date());
      var signatoryDto = this.signatoryService.update(signatory);
      if (!status.equals(DocumentStatus.RECEIVED)) {
        this.projectHistoryService.save(
            ProjectHistoryDTO.builder()
                .dateStatus(CommonUsages.now())
                .action(status.name())
                .actionBy(signatoryDto.getFirstName() + " " + signatoryDto.getLastName())
                .projectId(signatoryDto.getProjectId())
                .build());
      }
    }

    if (!projectAfterSignRequest.getDocuments().isEmpty()) {
      this.documentService.updateSignedDocUrl(projectAfterSignRequest);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateProjectAfterRefused(ProjectUpdateRequest request) {
    this.updateProject(request);
  }

  // status READ
  @Transactional(rollbackFor = Exception.class)
  public void readDocument(ProjectUpdateRequest request) {
    this.updateProject(request);
  }

  @Transactional(rollbackFor = Exception.class)
  public void cancelProject(Long id) {
    var project = this.findEntityById(id);
    var userId = this.getUserId();

    if (Objects.equals(project.getCreatedBy(), userId)
        || Objects.equals(project.getAssignedTo(), userId)) {
      var status = project.getStatus();
      log.info("Project status: {}...", status);

      if (Objects.equals(status, ProjectStatus.DRAFT.name())) {
        log.info("Deleting project and all related information...");
        this.deleteDocumentFiles(project);
        this.projectRepository.delete(project);
        return;
      }

      if (Objects.equals(status, ProjectStatus.COMPLETED.name())) {
        throw new CannotCancelProjectException("Project completed, cannot cancel...");
      }

      if (StringUtils.hasText(project.getFlowId())) {
        log.info("Canceling project flowId: {} ", project.getFlowId());
        try {
          // Use try catch, Prevent not found json project on process control
          this.processControlFeignClient.cancelProject(project.getFlowId());
        } catch (Exception e) {
          log.error("Project with this flowId: {} doesn't have json file...", project.getFlowId());
        }
      } else {
        log.warn("This project id:{} has no flowId...", project.getId());
      }

      log.info("Updating project status...");
      this.projectRepository.updateStatus(ProjectStatus.ABANDON.name(), userId, id);
      this.saveHistory(
          AuthenticationUtils.getUserFullName(), ProjectStatus.ABANDON.name(), project.getId());
    } else {
      throw new UnauthorizedException(PROJECT_UNAUTHOR);
    }
  }

  private void deleteDocumentFiles(Project project) {
    project
        .getDocuments()
        .forEach(
            document -> {
              log.info("Deleting file: {}", document.getFullPath());
              this.fileProvider.deleteFileFullPath(document.getFullPath());
            });
  }

  @Transactional(rollbackFor = Exception.class)
  public void urgentProject(Long id) {
    var project = this.findEntityById(id);
    var userId = this.getUserId();
    if (Objects.equals(project.getCreatedBy(), userId)
        || Objects.equals(project.getAssignedTo(), userId)) {
      if (!Objects.equals(project.getStatus(), ProjectStatus.IN_PROGRESS.name())) {
        throw new UnauthorizedException("Project not in progress");
      }

      this.projectRepository.updateStatus(ProjectStatus.URGENT.name(), getUserId(), id);
      this.saveHistory(null, ProjectStatus.URGENT.name(), project.getId());
    } else {
      throw new UnauthorizedException(PROJECT_UNAUTHOR);
    }
  }

  private void updateProject(ProjectUpdateRequest request) {
    if (Objects.nonNull(request.getSignatory())) {
      var signatory = this.signatoryService.findById(request.getSignatory().getId());
      this.projectRepository
          .findProjectById(signatory.getProjectId())
          .ifPresent(
              project -> {
                var docStatus = request.getSignatory().getDocumentStatus();
                signatory.setDocumentStatus(docStatus.name());
                signatory.setComment(request.getSignatory().getComment());
                signatory.setDateStatus(new Date());
                this.signatoryService.update(signatory);

                if (docStatus.name().equals(DocumentStatus.REFUSED.name())) {
                  this.projectRepository.updateStatus(
                      ProjectStatus.REFUSED.name(), getUserId(), project.getId());
                }

                var fullName = signatory.getFirstName() + " " + signatory.getLastName();
                this.saveHistory(fullName, docStatus.name(), project.getId());
              });
    }
  }

  private void saveHistory(String fullName, String status, Long id) {
    var maxOrder = projectHistoryRepository.findMaxSortOrder(id);
    var history =
        ProjectHistoryDTO.builder()
            .dateStatus(CommonUsages.now())
            .action(status)
            .sortOrder((int) (maxOrder + 1))
            .projectId(id)
            .build();

    if (StringUtils.hasText(fullName)) {
      history.setActionBy(fullName);
    }

    log.info("Saving project history...");
    this.projectHistoryService.save(history);
  }

  /**
   * To update project status to expire, it should call from a schedule task in Process Control
   * service.
   *
   * @param id use to find an existing project to be updated
   */
  @Transactional(rollbackFor = Exception.class)
  public void updateProjectStatusExpired(Long id) {
    this.projectRepository
        .findById(id)
        .ifPresent(
            project -> {
              this.projectRepository.updateStatus(
                  ProjectStatus.EXPIRED.name(), getUserId(), project.getId());

              var maxOrder = projectHistoryRepository.findMaxSortOrder(id);
              this.projectHistoryService.save(
                  ProjectHistoryDTO.builder()
                      .dateStatus(CommonUsages.now())
                      .action(project.getStatus())
                      .sortOrder((int) (maxOrder + 1))
                      .projectId(id)
                      .build());
            });
  }

  /**
   * send invitation mail to participant method.
   *
   * @param id refers to identity of the {@link Project}.
   */
  @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
  public void requestSignProcess(Long id, ProjectRequest projectRequest) {
    var source =
        this.projectRepository
            .findProjectInfoById(id)
            .orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND));
    var project = new com.innovationandtrust.share.model.project.Project();

    // Map signatories in projectDTO to participants and add to project
    var participants =
        source.getSignatories().stream()
            .map(signatory -> modelMapper.map(signatory, Participant.class))
            .toList();
    project.setParticipants(participants);

    // Map document in projectDTO to document in project
    var documents =
        source.getDocuments().stream()
            .map(
                doc -> {
                  var document = modelMapper.map(doc, Document.class);
                  document.setTotalPages(doc.getTotalPages());
                  document.setDetails(this.projectUtil.prepareDocumentDetail(document));
                  return document;
                })
            .toList();
    project.setDocuments(documents);

    // Map project detail in projectDTO to project detail in a project
    ProjectDetail projectDetail = new ProjectDetail();
    List<InvitationMessage> invitationMessages = new ArrayList<>();
    source
        .getDetails()
        .forEach(
            dt ->
                invitationMessages.add(
                    new InvitationMessage(
                        dt.getTitleInvitation(), dt.getMessageInvitation(), dt.getType())));

    projectDetail.setInvitationMessages(invitationMessages);
    projectDetail.setExpireDate(source.getExpireDate());
    project.setDetail(projectDetail);
    project.setId(source.getId());
    project.setFlowId(source.getFlowId());
    project.setName(source.getName());
    project.setStatus(source.getStatus());
    project.setCreatedBy(source.getCreatedBy());
    project.setCreatedAt(source.getCreatedAt());
    project.setUserKeycloakId(AuthenticationUtils.getUserUuid());
    project.setAutoReminder(source.isAutoReminder());
    project.setReminderChannel(source.getChannelReminder());
    project.setReminderOption(source.getReminderOption());

    project.setTemplate(this.projectUtil.getValidTemplate(source));

    project.setSignatureLevel(projectRequest.getSignatureLevel());
    project.setSetting(projectRequest.getSetting());

    this.processControlFeignClient.requestSign(project);
  }

  /**
   * Get all employees by corporate in corporate profile.
   *
   * @return list of employees
   */
  protected List<EmployeeDTO> getEmployeesByCorporate(Long corporateId) {
    try {
      return this.corporateProfileFeignClient.getEmployeesOfCorporate(corporateId);
    } catch (Exception e) {
      throw new FeignClientException(e.getMessage());
    }
  }

  /**
   * Create project 'step 1' function.
   *
   * @param projectId id of the project
   * @return a record of ProjectHistoryDTO
   */
  @Transactional(rollbackFor = Exception.class)
  public ProjectHistoryDTO saveProjectStep1(Long projectId) {
    return this.projectHistoryService.save(
        new ProjectHistoryDTO(
            null, CommonUsages.now(), ProjectHistoryStatus.CREATED.name(), "", 1, projectId));
  }

  /**
   * Create project 'step 2' function (add signatories to project).
   *
   * @param projectRequest is the project model to save
   * @param signatoryKey is signatories detail
   */
  @Transactional(rollbackFor = Exception.class)
  public void saveProjectStep2(ProjectRequest projectRequest, String signatoryKey) {
    CommonValidations.listNotEmpty(projectRequest.getSignatories(), signatoryKey);

    this.validateProjectLevel(projectRequest);

    var project = this.findEntityById(projectRequest.getId());
    if (project.getTemplateId() != null) {
      Template template = this.profileFeignClient.findTemplateById(project.getTemplateId());
      var signatories =
          projectRequest.getSignatories().stream()
              .map(signatoryDto -> modelMapper.map(signatoryDto, Signatory.class))
              .collect(Collectors.toSet());
      this.projectUtil.validateTemplate(signatories, template);
    }

    project.setOrderSign(projectRequest.isOrderSign());
    project.setOrderApprove(projectRequest.isOrderApprove());
    project.setStep(projectRequest.getStep());
    project.setSignatureLevel(projectRequest.getSignatureLevel());

    projectRequest
        .getSignatories()
        .forEach(s -> projectUtil.checkParticipantsRole(s.getRole(), s.getEmail()));

    this.projectRepository.save(project);
    this.signatoryService.saveAll(projectRequest.getSignatories());
  }

  /**
   * Create project 'step 3' function (save document detail to its document).
   *
   * @param projectRequest is the project model to save
   * @param documentDetailsKey is document detail
   */
  @Transactional(rollbackFor = Exception.class)
  public void saveProjectStep3(ProjectRequest projectRequest, String documentDetailsKey) {
    CommonValidations.listNotEmpty(projectRequest.getDocumentDetails(), documentDetailsKey);
    this.projectRepository.updateStep(projectRequest.getStep(), projectRequest.getId());
    this.documentDetailService.save(projectRequest.getDocumentDetails());
  }

  /**
   * Create project 'step 4' function (set project detail to project).
   *
   * @param projectRequest is the project model to save
   * @param detailsKey is project detail
   */
  @Transactional(rollbackFor = Exception.class)
  public void saveProjectStep4(ProjectRequest projectRequest, String detailsKey) {
    var checkDate = DateUtil.plushDays(new Date(), 1);
    var expiredDate = projectRequest.getExpireDate();
    if (DateUtil.removeTime(checkDate).after(expiredDate)) {
      throw new InvalidTTLValueException("You must select a date in the future");
    }

    CommonValidations.listNotEmpty(projectRequest.getDetails(), detailsKey);

    this.validateProjectLevel(projectRequest);

    Project project = this.findEntityById(projectRequest.getId());
    // for template used increment
    projectRequest.setTemplateId(project.getTemplateId());
    // no insert one to many data through the project

    var details =
        project.getDetails().stream()
            .map(
                dt ->
                    projectRequest.getDetails().stream()
                        .filter(prd -> prd.getType().equals(dt.getType()))
                        .findAny()
                        .map(
                            pr -> {
                              dt.setTitleInvitation(pr.getTitleInvitation());
                              dt.setMessageInvitation(pr.getMessageInvitation());
                              return dt;
                            })
                        .orElse(null))
            .collect(Collectors.toSet());

    if (details.isEmpty() && !projectRequest.getDetails().isEmpty()) {
      details =
          projectRequest.getDetails().stream()
              .map(
                  pr -> {
                    var dt = new com.innovationandtrust.project.model.entity.ProjectDetail();
                    dt.setProject(project);
                    dt.setType(pr.getType());
                    dt.setTitleInvitation(pr.getTitleInvitation());
                    dt.setMessageInvitation(pr.getMessageInvitation());
                    return dt;
                  })
              .collect(Collectors.toSet());
      project.setDetails(details);
    } else {
      project.setDetails(details.stream().filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    project.setStep(projectRequest.getStep());
    project.setReminderOption(projectRequest.getReminderOption());
    project.setAutoReminder(projectRequest.isAutoReminder());
    project.setChannelReminder(projectRequest.getChannelReminder());
    project.setName(projectRequest.getName());
    project.setExpireDate(projectRequest.getExpireDate());
    project.setModifiedBy(this.getUserId());

    this.setUrgent(project);

    this.mapData(this.projectRepository.save(project));
  }

  private void validateProjectLevel(ProjectRequest projectRequest) {
    var signatureLevel = projectRequest.getSignatureLevel();
    if (Objects.isNull(signatureLevel)) {
      throw new InvalidRequestException("Signature Level cannot be null...");
    }

    var companyUuid = this.getCompanyUuid();
    if (Objects.isNull(companyUuid)) {
      log.info("Getting corporate info from corporate profile service...");
      var corporateInfo =
          this.corporateProfileFeignClient.findCorporateInfo(projectRequest.getCreatedBy());
      companyUuid = corporateInfo.getCompanyUuid();
    }

    var projectStep = projectRequest.getStep();
    if (Objects.isNull(projectStep)) {
      throw new InvalidRequestException("Project step cannot be null...");
    }

    boolean isFinalStep =
        Objects.equals(projectStep, "4") && Objects.nonNull(projectRequest.getChannelReminder());

    log.info("Getting company setting:{} from company:{}...", signatureLevel, companyUuid);
    var companySetting =
        this.corporateProfileFeignClient.getCompanySettingByLevel(companyUuid, signatureLevel);

    if (isFinalStep) {
      var setting = projectRequest.getSetting();
      if (Objects.isNull(setting)) {
        throw new InvalidRequestException("Setting cannot be null...");
      }

      if (!Objects.equals(signatureLevel, setting.getSignatureLevel())) {
        throw new InvalidRequestException("Signature Level must be the same...");
      }

      var channel = NotificationChannel.getByChannel(projectRequest.getChannelReminder());
      if (!Objects.equals(channel.getName(), setting.getChannelReminder())) {
        throw new InvalidRequestException("Channel reminder must be the same...");
      }

      log.info("Validating project signature level and options...");
      CompanySettingUtils.validateSettingOption(companySetting, projectRequest.getSetting());
    }
  }

  /**
   * Get a project record by id.
   *
   * @param id projectId
   * @return a record of ProjectDTO
   */
  @Override
  @Transactional(readOnly = true)
  public ProjectDTO findById(Long id) {
    ProjectDTO projectDto = this.mapData(this.findEntityById(id));
    projectDto.getSignatories().sort(SignatoryDto::compareToSentDate);
    projectDto.getSignatories().sort(SignatoryDto::compareToDateStatus);
    var createdByUser = this.profileFeignClient.getUserInfo(projectDto.getCreatedBy());
    if (Objects.nonNull(projectDto.getFlowId())) {
      var isFinished = this.processControlFeignClient.isProjectFinished(projectDto.getFlowId());
      if (Boolean.TRUE.equals(isFinished)) {
        var corporateUser = this.profileFeignClient.getUserInfo(createdByUser.getCreatedBy());
        projectDto.setManifestName(
            String.format(
                "%s_%s_%s_proof.pdf",
                corporateUser.getUserEntityId(),
                createdByUser.getUserEntityId(),
                projectDto.getId()));
      }
    }
    projectDto.setCreatedByUser(createdByUser);
    return projectDto;
  }

  /**
   * Delete project by id.
   *
   * @param id refers to project.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    this.projectRepository.findById(id).ifPresent(this.projectRepository::delete);
  }

  /**
   * Complete project by id.
   *
   * @param id projectId.
   */
  @Transactional(rollbackFor = SQLException.class)
  public void completeProject(Long id, String status) {
    this.projectRepository.completeProject(id, status);
    if (!ProjectStatus.REFUSED.name().equals(status)) {
      this.projectHistoryService.save(
          new ProjectHistoryDTO(null, CommonUsages.now(), status, "", 1, id));
    }
  }

  /**
   * To count a project created by employees.
   *
   * @param employees for find projects by them
   */
  @Transactional(readOnly = true)
  public List<EmployeeDTO> countEmployeeProject(
      List<EmployeeDTO> employees, String startDate, String endDate) {
    var projects =
        getProjectsBetweenDate(
            employees.stream().map(EmployeeDTO::getUserId).map(String::valueOf).toList(),
            startDate,
            DateUtil.convertToUtc(
                DateUtil.plushDays(DateUtil.convertFrom(endDate, DATE_FORMAT), 1)));

    employees.forEach(
        employee -> {
          var empProjects =
              projects.stream()
                  .filter(
                      project ->
                          Objects.equals(
                              project.getAssignedTo() != null
                                  ? project.getAssignedTo()
                                  : project.getCreatedBy(),
                              employee.getUserId()))
                  .count();
          employee.setTotalProjects(empProjects);
          if (!projects.isEmpty()) {
            employee.setPercentage((double) (empProjects * 100) / projects.size());
          }
        });

    return employees;
  }

  /**
   * To get cards and counted project statuses for corporate dashboard. When calling this method,
   * means the process-control is completed of creating project
   *
   * @param id project id
   * @param flowId refers to the project unique flowId
   */
  @Transactional(rollbackFor = SQLException.class)
  public void insertProjectFlowId(Long id, String flowId) {
    this.projectRepository
        .findById(id)
        .ifPresent(
            project -> {
              if (!StringUtils.hasText(project.getFlowId())) {
                project.setFlowId(flowId);
                project.setStatus(ProjectStatus.IN_PROGRESS.name());
                this.projectRepository.save(project);
              }
            });
  }

  /**
   * To get cards and counted project statuses for corporate dashboard.
   *
   * @param userIds the list of userIds
   * @return Object of {@link DashboardDTO}
   */
  public DashboardDTO corporateDashboard(List<Long> userIds, String startDate, String endDate) {
    var projects =
        getProjectsBetweenDate(
            userIds.stream().map(String::valueOf).toList(),
            startDate,
            DateUtil.convertToUtc(
                DateUtil.plushDays(DateUtil.convertFrom(endDate, DATE_FORMAT), 1)));

    var signatories =
        projects.stream()
            .map(
                project ->
                    project.getSignatories().stream()
                        .map(signatory -> modelMapper.map(signatory, SignatoryDto.class))
                        .filter(
                            signatory ->
                                Objects.equals(
                                    signatory.getRole(), ParticipantRole.SIGNATORY.getRole()))
                        .toList())
            .flatMap(List::stream)
            .toList();

    var signed =
        signatories.stream()
            .filter(
                signatory ->
                    Objects.nonNull(signatory.getSentDate())
                        && Objects.nonNull(signatory.getDateStatus())
                        && Objects.equals(
                            signatory.getDocumentStatus(), DocumentStatus.SIGNED.name()))
            .toList();

    long totalTimeTaken =
        signed.stream()
            .mapToLong(
                signatory ->
                    signatory.getDateStatus().getTime() - signatory.getSentDate().getTime())
            .sum();

    var expiredOrAbandoned =
        projects.stream()
            .filter(
                project ->
                    Objects.equals(project.getStatus(), ProjectStatus.EXPIRED.name())
                        || Objects.equals(project.getStatus(), ProjectStatus.ABANDON.name()))
            .count();

    var refusedProject =
        projects.stream()
            .filter(project -> Objects.equals(project.getStatus(), ProjectStatus.REFUSED.name()))
            .count();

    var signerRefused =
        signatories.stream()
            .filter(
                signatory ->
                    Objects.equals(signatory.getDocumentStatus(), DocumentStatus.REFUSED.name()))
            .count();

    var statuses = projects.stream().map(Project::getStatus).toList();
    Map<String, Object> contents = getStatusCount(statuses);
    contents.put(
        DashboardConstant.CARDS,
        Arrays.asList(
            ChartItem.builder()
                .id(ProjectStatus.COMPLETED.name())
                .label("Temps moyen de signature")
                .value(
                    !projects.isEmpty()
                        ? (double) TimeUnit.MILLISECONDS.toSeconds(totalTimeTaken)
                            / signatories.size()
                        : 0.0)
                .build(),
            ChartItem.builder()
                .id(ProjectStatus.ABANDON.name())
                .label("Taux d’abandon de dossiers")
                .value(
                    !projects.isEmpty()
                        ? (double) (expiredOrAbandoned * 100) / (projects.size() - refusedProject)
                        : 0.0)
                .build(),
            ChartItem.builder()
                .id(ProjectStatus.REFUSED.name())
                .label("Taux de refus de signer")
                .value(
                    !projects.isEmpty() ? (double) (signerRefused * 100) / signatories.size() : 0.0)
                .build()));

    return DashboardDTO.builder()
        .contents(contents)
        .totalProjects(projects.size())
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }

  /** To get counted projects by categories. */
  public Map<String, Object> countProject(String filterBy) {
    log.info("End user getting counted projects ...");
    List<Long> usersId = this.getUsersIdByFilter(filterBy);
    return getCountedProjects(usersId);
  }

  /** To get counted projects by categories. */
  public Map<String, Object> countProjectByCorporate(Long userId) {
    log.info("Corporate admin getting counted users projects...");
    List<Long> usersId = this.getUserIdsByCorporateFilter(userId);
    if (usersId.isEmpty()) {
      log.warn("There are no users...");
    }
    return getCountedProjects(usersId);
  }

  private Map<String, Object> getCountedProjects(List<Long> userIds) {
    log.info("Getting users projects statuses from database...");
    var statuses = this.projectRepository.findAllStatusesByUserIds(userIds);

    log.info("Counting users projects...");
    Map<String, Object> contents = getStatusCount(statuses);
    contents.put(DashboardConstant.TOTAL, statuses.size());

    log.info("Success counting users projects...");
    return contents;
  }

  /**
   * To get userIds to fetch projects created by them.
   *
   * @param userId for fetching projects created by userId.
   */
  private List<Long> getUserIdsByCorporateFilter(Long userId) {
    List<Long> usersId = new ArrayList<>();
    if (!Objects.equals(userId, 0L)) {
      log.info("Getting a user projects...");
      var user = this.profileFeignClient.findUserById(userId);
      if (!Objects.equals(AuthenticationUtils.getCompanyId(), user.getCompany().getId())) {
        return Collections.emptyList();
      }
      usersId.add(userId);
    } else {
      log.info("Getting users projects...");
      List<EmployeeDTO> employees = this.getEmployeesByCorporate(this.getUserId());
      usersId = employees.stream().map(EmployeeDTO::getUserId).toList();
    }
    return usersId;
  }

  private Map<String, Object> getStatusCount(List<String> statuses) {
    Map<String, Object> contents = new HashMap<>();
    contents.put(
        DashboardConstant.STATUSES,
        Arrays.asList(
            ChartItem.builder()
                .id(ProjectStatus.IN_PROGRESS.name())
                .label(ProjectStatus.IN_PROGRESS.getEn())
                .value(countStatus(statuses, ProjectStatus.IN_PROGRESS.name()))
                .build(),
            ChartItem.builder()
                .id(ProjectStatus.URGENT.name())
                .label(ProjectStatus.URGENT.getEn())
                .value(countStatus(statuses, ProjectStatus.URGENT.name()))
                .build(),
            ChartItem.builder()
                .id(ProjectStatus.REFUSED.name())
                .label(ProjectStatus.REFUSED.getEn())
                .value(countStatus(statuses, ProjectStatus.REFUSED.name()))
                .build(),
            ChartItem.builder()
                .id(ProjectStatus.COMPLETED.name())
                .label(ProjectStatus.COMPLETED.getEn())
                .value(countStatus(statuses, ProjectStatus.COMPLETED.name()))
                .build(),
            ChartItem.builder()
                .id(ProjectStatus.DRAFT.name())
                .label(ProjectStatus.DRAFT.getEn())
                .value(countStatus(statuses, ProjectStatus.DRAFT.name()))
                .build(),
            ChartItem.builder()
                .id(ProjectStatus.ABANDON.name())
                .label(ProjectStatus.ABANDON.getEn())
                .value(countStatus(statuses, ProjectStatus.ABANDON.name()))
                .build(),
            ChartItem.builder()
                .id(ProjectStatus.EXPIRED.name())
                .label(ProjectStatus.EXPIRED.getEn())
                .value(countStatus(statuses, ProjectStatus.EXPIRED.name()))
                .build()));
    return contents;
  }

  private long countStatus(List<String> statuses, String status) {
    return statuses.stream().filter(st -> Objects.equals(st, status)).count();
  }

  private List<Project> getProjectsBetweenDate(
      List<String> userIds, String startDate, String endDate) {
    Specification<Project> projectSpec =
        AdvancedFilter.searchByField(
            Filter.builder()
                .field(AbstractBaseEntity_.CREATED_AT)
                .operator(QueryOperator.BETWEEN_DATE)
                .values(Arrays.asList(startDate, endDate))
                .build());

    projectSpec =
        projectSpec.and(
            ProjectSpecification.searchByAssignedToOrCreatedBy(
                userIds.stream().map(Long::parseLong).toList()));

    return this.projectRepository.findAll(projectSpec);
  }

  /**
   * To update project expire date.
   *
   * @param id use to find an existing project
   * @param expiredDate use to update project expiration date
   * @return object of {@link ProjectDTO} after updating
   */
  @Transactional(rollbackFor = SQLException.class)
  public ProjectDTO updateExpiredDate(Long id, String expiredDate) {
    var project = this.findEntityById(id);
    var userId = this.getUserId();
    if (Objects.equals(project.getCreatedBy(), userId)
        || Objects.equals(project.getAssignedTo(), userId)) {

      var status = project.getStatus();
      if (Objects.equals(status, ProjectStatus.ABANDON.name())) {
        throw new ForbiddenRequestException(
            "Cannot update project expiration date. Project is cancelled...");
      }

      if (!Objects.equals(status, ProjectStatus.IN_PROGRESS.name())) {
        throw new InvalidRequestException("Project not in progress");
      }

      var calendar = Calendar.getInstance();
      calendar.setTime(Date.from(Instant.parse(expiredDate)));
      var newExpireDate =
          TimeUnit.MILLISECONDS.toSeconds(
              calendar.getTimeInMillis() - project.getCreatedAt().getTime());

      if (newExpireDate < ApiNgConstant.TTL_MIN) {
        throw new InvalidTTLValueException(
            "New expire date cannot less than: " + ApiNgConstant.TTL_MIN);
      } else if (newExpireDate > ApiNgConstant.TTL_MAX) {
        throw new InvalidTTLValueException(
            "New expire cannot greater than: " + ApiNgConstant.TTL_MAX);
      }

      project.setExpireDate(calendar.getTime());
      this.setUrgent(project);
      this.processControlFeignClient.updateExpireDate(project.getFlowId(), expiredDate);
      return this.mapData(this.projectRepository.save(project));
    }
    throw new UnauthorizedException(PROJECT_UNAUTHOR);
  }

  private void setUrgent(Project project) {
    // This code will activate next sprint
    //    var calendar = Calendar.getInstance();
    //    calendar.setTime(project.getExpireDate());
    //    calendar.add(Calendar.HOUR, -settingProperties.getUrgentProject());
    //    // If expiration date less than urgent project setting hours
    //    if ((new Date()).after(calendar.getTime())) {
    //      project.setStatus(ProjectStatus.URGENT.name());
    //    }
  }

  /**
   * To assign projects created by creator. Only In Progress projects will be assigned to.
   *
   * @param creator use for search projects created by him/her.
   * @param assignTo is user id of an end user. He/She will see projects assigned to him/her
   */
  @Transactional(rollbackFor = Exception.class)
  public void assignProjects(Long creator, Long assignTo) {
    log.info(
        "Assigning projects of user:{} to user:{} by corporate:{}", creator, assignTo, getUserId());
    var assignedDate = new Date();
    var projects =
        this.projectRepository.findAll(
            AdvancedFilter.searchByField(
                Filter.builder()
                    .field(AbstractBaseEntity_.CREATED_BY)
                    .operator(QueryOperator.EQUALS)
                    .value(creator.toString())
                    .build()));
    projects.forEach(
        project -> {
          project.setAssignedTo(assignTo);
          project.setAssignedDate(assignedDate);
        });
    this.projectRepository.saveAll(projects);
    log.info("{} projects assigned to user {}", projects.size(), assignTo);
  }
}
