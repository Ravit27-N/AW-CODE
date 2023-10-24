package com.innovationandtrust.project.service;

import com.innovationandtrust.project.enums.ProjectHistoryStatus;
import com.innovationandtrust.project.model.dto.ProjectDTO;
import com.innovationandtrust.project.model.dto.ProjectRequest;
import com.innovationandtrust.project.model.entity.Document;
import com.innovationandtrust.project.model.entity.DocumentDetail;
import com.innovationandtrust.project.model.entity.Project;
import com.innovationandtrust.project.model.entity.ProjectDetail;
import com.innovationandtrust.project.model.entity.ProjectHistory;
import com.innovationandtrust.project.model.entity.Signatory;
import com.innovationandtrust.project.repository.DocumentDetailRepository;
import com.innovationandtrust.project.repository.ProjectRepository;
import com.innovationandtrust.project.utils.ProjectUtil;
import com.innovationandtrust.share.constant.NotificationChannel;
import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.file.model.FileResponse;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
public class ProjectXmlService extends CommonCrudService<ProjectDTO, Project, Long> {
  private final ProjectRepository projectRepository;
  private final ProjectUtil projectUtil;
  private final FileProvider fileProvider;
    private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final ProjectService projectService;
  private final DocumentDetailRepository documentDetailRepository;

  @Autowired
  public ProjectXmlService(
          ProjectRepository projectRepository,
          ModelMapper modelMapper,
          IKeycloakProvider keycloakProvider,
          ProjectUtil projectUtil,
          FileProvider fileProvider,
          CorporateProfileFeignClient corporateProfileFeignClient, ProjectService projectService,
          DocumentDetailRepository documentDetailRepository) {
    super(modelMapper, keycloakProvider);
    this.projectRepository = projectRepository;
    this.projectUtil = projectUtil;
    this.fileProvider = fileProvider;
      this.corporateProfileFeignClient = corporateProfileFeignClient;
      this.projectService = projectService;
    this.documentDetailRepository = documentDetailRepository;
  }

  /** Create a project from xml in SFTP */
  @Transactional(rollbackFor = SQLException.class)
  public void createProjectFromXml(ProjectModel projectModel) {
    // will create a new project by this user
    var project = new Project();
    this.modelMapper.map(projectModel, project);

    project.setDetails(new HashSet<>());
    project.getDetails().addAll(super.mapAll(projectModel.getDetails(), ProjectDetail.class));
    project.getDetails().stream().parallel().forEach(dt -> dt.setProject(project));

    // Set documents of the project
    project.setDocuments(new HashSet<>());
    project.getDocuments().addAll(super.mapAll(projectModel.getDocuments(), Document.class));
    project.getDocuments().stream()
        .parallel()
        .forEach(
            doc -> {
              doc.setProject(project);
              // We need to insert document's detail after insertion participants
              doc.setDocumentDetails(new HashSet<>());
            });

    // set signatories of the project
    project.setSignatories(new TreeSet<>());
    project.getSignatories().addAll(super.mapAll(projectModel.getParticipants(), Signatory.class));
    project.getSignatories().stream()
        .parallel()
        .forEach(
            signer -> {
              projectUtil.checkParticipantsRole(signer.getRole(), signer.getEmail());
              signer.setProject(project);
            });

    var history = new ProjectHistory();
    history.setProject(project);
    history.setCreatedBy(super.getUserId());
    history.setSortOrder(1);
    history.setDateStatus(new Date());
    history.setAction(ProjectHistoryStatus.CREATED.name());
    project.setHistories(new HashSet<>(List.of(history)));

    project.setCreatedBy(super.getUserId());
    if (project.getTemplateId() != null) {
      var template = this.projectUtil.getValidTemplate(project);
      if (Objects.nonNull(template)) {
        project.setTemplateId(template.getId());
        project.setTemplateName(template.getName());
      }
    }

    project.setSignatureLevel(SignatureSettingLevel.SIMPLE.name());

    // Save project
    var response = this.projectRepository.save(project);
    List<DocumentDetail> documentDetails = new ArrayList<>();

    projectModel.getDocuments().forEach(
        d -> response.getDocuments().stream()
                .filter(document -> Objects.equals(document.getFileName(), d.getFileName()))
                .findAny().ifPresent(savedDoc -> d.getDocumentDetails()
                    .forEach(
                        dt -> response.getSignatories().stream()
                            .filter(s -> Objects.equals(s.getEmail(), dt.getSignatoryEmail()))
                            .findAny().ifPresent(signatory -> {
                              var docDetail = this.modelMapper.map(dt, DocumentDetail.class);
                              docDetail.setDocument(savedDoc);
                              docDetail.setTextAlign("1");
                              docDetail.setSignatoryId(signatory.getId());
                              docDetail.setText(String.format("%s %s", signatory.getFirstName(), signatory.getLastName()));
                              documentDetails.add(docDetail);
                            }))));
    this.documentDetailRepository.saveAll(documentDetails);
    this.requestSign(response);
  }

  private void requestSign(Project project) {

    var projectRequest = new ProjectRequest();
    var companyUuid = this.getCompanyUuid();
    var signatureLevel = project.getSignatureLevel();

    if (Objects.isNull(companyUuid)) {
      log.info("Getting corporate info from corporate profile service...");
      var corporateInfo =
          this.corporateProfileFeignClient.findCorporateInfo(projectRequest.getCreatedBy());
      companyUuid = corporateInfo.getCompanyUuid();
    }

    log.info("Getting company setting:{} from company:{}...", signatureLevel, companyUuid);
    var companySetting =
        this.corporateProfileFeignClient.getCompanySettingByLevel(companyUuid, signatureLevel);

    var channelReminder = NotificationChannel.getByChannel(project.getChannelReminder()).getName();
    var companyChannel = companySetting.getCompanyChannel();
    if (Objects.nonNull(companyChannel)
        && (!Objects.equals(companyChannel, NotificationConstant.SMS_EMAIL)
            && !Objects.equals(companyChannel, channelReminder))) {
      throw new InvalidRequestException("Channel reminder:" + channelReminder + " not available...");
    }

    // Set default message for project
    projectRequest.setSetting(
        CompanySettingDto.builder()
            .signatureLevel(signatureLevel)
            .companyUuid(companySetting.getCompanyUuid())
            .channelReminder(channelReminder)
            .personalTerms(companySetting.getPersonalTerms())
            .build());

    Executors.newSingleThreadExecutor()
        .execute(
            () -> this.projectService.requestSignProcess(project.getId(), new ProjectRequest()));
  }

  public List<FileResponse> uploadDocFiles(List<MultipartFile> documents, String... dirs) {
    return documents.stream()
        .map(doc -> this.fileProvider.upload(doc, Path.of(Arrays.toString(dirs)), false))
        .toList();
  }
}
