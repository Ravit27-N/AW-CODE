package com.innovationandtrust.project.controller;

import com.innovationandtrust.project.constant.FilterByConstant;
import com.innovationandtrust.project.model.dto.DocumentContent;
import com.innovationandtrust.project.model.dto.DocumentDTO;
import com.innovationandtrust.project.model.dto.ProjectDTO;
import com.innovationandtrust.project.model.dto.ProjectRequest;
import com.innovationandtrust.project.service.DocumentService;
import com.innovationandtrust.project.service.ProjectService;
import com.innovationandtrust.project.service.SignatureService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.corporateprofile.DashboardDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.ProjectUpdateRequest;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.share.utils.PageUtils;
import com.innovationandtrust.utils.file.provider.FileProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** All project endpoints. */
@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

  private final ProjectService projectService;
  private final FileProvider fileProvider;
  private final DocumentService documentService;
  private final SignatureService signatureService;

  /**
   * Insert new Project endpoint.
   *
   * @param files pdf file for sign
   * @param dirs use for set your own directory
   * @param projectDto project info
   * @return the inserted ProjectDTO
   */
  @PostMapping
  @Tag(name = "Create project", description = "To create a project")
  public ResponseEntity<ProjectDTO> create(
      @Valid @ModelAttribute("files") MultipartFile[] files,
      @RequestParam(value = "dirs", defaultValue = "") String dirs,
      @ModelAttribute ProjectDTO projectDto) {
    ProjectDTO projectDtoData = projectService.save(projectDto, files);
    List<DocumentDTO> documentDtoList =
        documentService.save(fileProvider.uploads(files, dirs), projectDtoData.getId());
    projectDtoData.setDocuments(documentDtoList);

    return new ResponseEntity<>(projectDtoData, HttpStatus.CREATED);
  }

  /**
   * View document endpoint.
   *
   * @param docName the file path
   * @return the document to base64
   */
  @GetMapping("/view-documents")
  @Tag(name = "View document in base64", description = "To view document in base64")
  public ResponseEntity<String> viewDocuments(@RequestParam String docName) {
    return ResponseEntity.ok(documentService.viewDocumentBase64(docName));
  }

  /**
   * View document endpoint.
   *
   * @param docName the file path
   * @return the document to view
   */
  @GetMapping("/view-documents/content")
  @Tag(name = "View document content", description = "To view document content")
  public ResponseEntity<Resource> viewDocumentBase64(@RequestParam String docName) {
    DocumentContent documentContent = documentService.viewDocument(docName);
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, documentContent.getContentType());
    headers.setContentLength(documentContent.getContentLength());

    return ResponseEntity.ok().headers(headers).body(documentContent.getResource());
  }

  /**
   * Update project endpoint.
   *
   * @param projectRequest new project info for update
   * @return ProjectDTO
   */
  @PutMapping
  @Tag(name = "Update project", description = "To update project from step 2 to step 4")
  public ResponseEntity<ProjectDTO> update(@Valid @RequestBody ProjectRequest projectRequest) {
    return new ResponseEntity<>(
        this.signatureService.updateProjectAndSendProcess(projectRequest), HttpStatus.OK);
  }

  /**
   * Update signatory and document status after sign-process.
   *
   * @param request refers to the object of {@link ProjectAfterSignRequest}
   */
  @Hidden
  @PutMapping("/signed")
  @Tag(name = "Update project after sign", description = "To update project after sign")
  public ResponseEntity<Void> updateProjectAfterSign(
      @Valid @RequestBody ProjectAfterSignRequest request) {
    this.projectService.updateProjectAfterSigned(request);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Hidden
  @PutMapping("/refused")
  @Tag(name = "Update project after refused", description = "To update project after refused")
  public ResponseEntity<Void> updateProjectAfterRefused(
      @Valid @RequestBody ProjectUpdateRequest request) {
    this.projectService.updateProjectAfterRefused(request);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Hidden
  @PutMapping("/expired")
  @Tag(name = "Update project status expired", description = "To update project status to expired")
  public ResponseEntity<Void> updateProjectStatusExpired(@Valid @RequestParam("id") Long id) {
    this.projectService.updateProjectStatusExpired(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Hidden
  @PutMapping("/read")
  @Tag(name = "Read document", description = "To update viewer's document status")
  public ResponseEntity<Void> readDocument(@Valid @RequestBody ProjectUpdateRequest request) {
    this.projectService.readDocument(request);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/cancel/{id}")
  @Tag(name = "Cancel project", description = "To cancel project")
  public ResponseEntity<Void> cancelProject(@PathVariable("id") Long id) {
    this.projectService.cancelProject(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Hidden
  @PutMapping("/urgent")
  @Tag(name = "Urgent project", description = "To update project status to be urgent")
  public ResponseEntity<Void> urgentProject(@Valid @RequestParam("id") Long id) {
    this.projectService.urgentProject(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * findAll projects endpoint.
   *
   * @param page page number
   * @param pageSize number of records in a page
   * @return List of projectDTO with pagination
   */
  @GetMapping
  @Tag(name = "Get all projects", description = "To get all projects as pagination")
  public ResponseEntity<EntityResponseHandler<ProjectDTO>> findAll(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField) {
    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            projectService.findAll(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection), filter)),
        HttpStatus.OK);
  }

  /**
   * findAll projects by createdBy endpoint.
   *
   * @param page page number
   * @param pageSize number of records in a page
   * @return List of projectDTO with pagination
   */
  @GetMapping("/user")
  @Tag(name = "Get all projects by user", description = "To get all projects by user as pagination")
  public ResponseEntity<EntityResponseHandler<ProjectDTO>> findAllByUser(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField,
      @RequestParam(value = CommonParamsConstant.FILTER_BY, defaultValue = FilterByConstant.USER)
          String filterBy,
      @RequestParam(value = CommonParamsConstant.FILTER_STEP, defaultValue = "")
          List<String> filterSteps,
      @RequestParam(value = CommonParamsConstant.STATUSES, defaultValue = "") List<String> statuses,
      @RequestParam(value = CommonParamsConstant.START_DATE, defaultValue = "") String startDate,
      @RequestParam(value = CommonParamsConstant.END_DATE, defaultValue = "") String endDate) {

    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            this.projectService.findAllByFilter(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection),
                filter,
                filterSteps,
                filterBy,
                statuses,
                startDate,
                endDate)),
        HttpStatus.OK);
  }

  /**
   * findAll projects by corporate admin endpoint.
   *
   * @param page page number
   * @param pageSize number of records in a page
   * @return List of projectDTO with pagination
   */
  @GetMapping("/corporate")
  @Tag(
      name = "Get all projects by corporate",
      description = "To get all projects by corporate as pagination")
  public ResponseEntity<EntityResponseHandler<ProjectDTO>> findAllByCorporate(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField,
      @RequestParam(value = CommonParamsConstant.FILTER_STEP, defaultValue = "")
          List<String> filterSteps,
      @RequestParam(value = CommonParamsConstant.STATUSES, defaultValue = "") List<String> statuses,
      @RequestParam(value = CommonParamsConstant.START_DATE, defaultValue = "") String startDate,
      @RequestParam(value = CommonParamsConstant.END_DATE, defaultValue = "") String endDate,
      @RequestParam(value = "userId", defaultValue = "0") Long userId) {
    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            projectService.findAllByCorporate(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection),
                userId,
                filter,
                filterSteps,
                statuses,
                startDate,
                endDate)),
        HttpStatus.OK);
  }

  /**
   * findAll projects by corporate admin endpoint.
   *
   * @param page page number
   * @param pageSize number of records in a page
   * @return List of projectDTO with pagination
   */
  @GetMapping("/company")
  @Tag(
      name = "Get all projects by company",
      description = "To get all projects by company as pagination")
  public ResponseEntity<EntityResponseHandler<ProjectDTO>> findAllByCompany(
      @RequestParam(value = CommonParamsConstant.PAGE_NUMBER, defaultValue = "1") int page,
      @RequestParam(value = CommonParamsConstant.PAGE_SIZE, defaultValue = "10") int pageSize,
      @RequestParam(value = CommonParamsConstant.SEARCH, defaultValue = "") String filter,
      @RequestParam(value = CommonParamsConstant.FILTER_STEP, defaultValue = "")
          List<String> filterSteps,
      @RequestParam(value = CommonParamsConstant.SORT_DIRECTION, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = CommonParamsConstant.SORT_FIELD, defaultValue = "id")
          String sortByField) {
    return new ResponseEntity<>(
        new EntityResponseHandler<>(
            projectService.findAllByCompany(
                PageUtils.pageable(page, pageSize, sortByField, sortDirection),
                filter,
                filterSteps)),
        HttpStatus.OK);
  }

  /**
   * Find one project by ID.
   *
   * @param id project identity
   * @return ProjectDTO
   */
  @GetMapping("/{id}")
  @Tag(name = "Get project by id", description = "To get project by its id")
  public ResponseEntity<ProjectDTO> findById(@PathVariable Long id) {
    return new ResponseEntity<>(projectService.findById(id), HttpStatus.OK);
  }

  @PutMapping("/complete/{id}")
  @Tag(name = "Complete the project", description = "To complete the project")
  public ResponseEntity<Void> completeProject(
      @PathVariable("id") Long id, @RequestParam("status") String status) {
    this.projectService.completeProject(id, status);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/count/employees")
  @Tag(
      name = "Count projects by each employees",
      description = "To count projects that were created by each employees")
  public ResponseEntity<List<EmployeeDTO>> countEmployeeProject(
      @RequestBody List<EmployeeDTO> employeeDtoList,
      @RequestParam String startDate,
      @RequestParam String endDate) {
    return new ResponseEntity<>(
        projectService.countEmployeeProject(employeeDtoList, startDate, endDate), HttpStatus.OK);
  }

  @PutMapping("/uuid/{id}/{flowId}")
  @Tag(name = "Insert flow id to project", description = "To insert flow id to project")
  public ResponseEntity<Void> insertFlowId(
      @PathVariable("id") Long id, @PathVariable("flowId") String flowId) {
    this.projectService.insertProjectFlowId(id, flowId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/dashboard")
  @Tag(
      name = "Corporate dashboard",
      description = "To see projects and more information on corporate dashboard")
  public ResponseEntity<DashboardDTO> corporateDashboard(
      @RequestBody List<Long> userIds,
      @RequestParam String startDate,
      @RequestParam String endDate) {
    return new ResponseEntity<>(
        projectService.corporateDashboard(userIds, startDate, endDate), HttpStatus.OK);
  }

  @GetMapping("/count")
  @Tag(name = "Count projects status by user", description = "To count projects status by user")
  public ResponseEntity<Map<String, Object>> countProjectsStatus(
      @RequestParam(value = CommonParamsConstant.FILTER_BY, defaultValue = FilterByConstant.USER)
          String filterBy) {
    return new ResponseEntity<>(projectService.countProject(filterBy), HttpStatus.OK);
  }

  @GetMapping("/corporate/count")
  @Tag(
      name = "Count projects by corporate user",
      description = "To count projects status by corporate user")
  public ResponseEntity<Map<String, Object>> countProjectsStatusByCorporateUser(
      @RequestParam(value = "userId", defaultValue = "0") Long userId) {
    return new ResponseEntity<>(projectService.countProjectByCorporate(userId), HttpStatus.OK);
  }

  /**
   * Update expired date for a project.
   *
   * @param id refers to project id that have to update
   * @param expiredDate refers to project's expired date
   * @return an object of ProjectDTO
   */
  @PutMapping("/update/expired/{id}")
  @Tag(
      name = "Update project's expired date",
      description = "To update project's expired date only when project is in progress")
  public ResponseEntity<ProjectDTO> updateExpiredDate(
      @PathVariable("id") Long id, @RequestParam("expiredDate") String expiredDate) {
    return new ResponseEntity<>(
        this.projectService.updateExpiredDate(id, expiredDate), HttpStatus.OK);
  }

  /**
   * To assign projects created by creator. Only In Progress projects will be assigned to.
   *
   * @param creator use for search projects created by him/her.
   * @param assignTo is user id of an end user. He/She will see projects assigned to him/her
   */
  @Hidden
  @PutMapping("/assign/{id}")
  @Tag(name = "Assign project to another user", description = "To assign project to another user")
  public ResponseEntity<Void> assignProjects(
      @PathVariable("id") Long creator, @RequestParam Long assignTo) {
    this.projectService.assignProjects(creator, assignTo);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
