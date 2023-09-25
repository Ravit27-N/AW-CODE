package com.allweb.rms.service;

import com.allweb.rms.component.CandidateModelAssembler;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.entity.dto.AdvanceReportResponse;
import com.allweb.rms.entity.dto.CandidateDTO;
import com.allweb.rms.entity.dto.CandidateElasticsearchRequest;
import com.allweb.rms.entity.elastic.CandidateElasticsearchDocument;
import com.allweb.rms.entity.elastic.CandidateReportElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.CandidateStatus;
import com.allweb.rms.entity.jpa.CandidateUniversity;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.entity.jpa.SystemConfiguration;
import com.allweb.rms.entity.jpa.University;
import com.allweb.rms.exception.AdvancedSearchBadRequestException;
import com.allweb.rms.exception.CandidateNotFoundException;
import com.allweb.rms.exception.CandidateStatusNotFoundException;
import com.allweb.rms.exception.EmailConflictException;
import com.allweb.rms.exception.UniversityNotFoundException;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.repository.jpa.CandidateRepository;
import com.allweb.rms.repository.jpa.CandidateStatusRepository;
import com.allweb.rms.repository.jpa.CandidateUniversityRepository;
import com.allweb.rms.repository.jpa.DemandRepository;
import com.allweb.rms.repository.jpa.InterviewRepository;
import com.allweb.rms.repository.jpa.ReminderRepository;
import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import com.allweb.rms.repository.jpa.UniversityRepository;
import com.allweb.rms.service.elastic.ElasticIndexingService;
import com.allweb.rms.service.elastic.request.CandidateHardDeleteRequest;
import com.allweb.rms.service.elastic.request.CandidateInsertElasticRequest;
import com.allweb.rms.service.elastic.request.CandidateUpdateElasticRequest;
import com.allweb.rms.service.mail.MailService;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.rms.utils.ReminderType;
import com.allweb.rms.utils.StorageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.allweb.rms.utils.SystemConfigurationConstants.GPA;

@Service
@Transactional
@Log4j2
@Slf4j
@CacheConfig(cacheNames = "candidateCaching")
public class CandidateService {

  // field

  private static final String CANDIDATE_FOLDER_NAME = "candidate";
  private static final String SUB_PATH_PROFILE = "profile";
  private static final String SUB_PATH_CV = "cv";
  private static final String FILE_NAMES = "filenames";
  private final CandidateRepository candidateRepository;
  private final InterviewRepository interviewRepository;
  private final ObjectMapper objectMapper;
  private final CandidateUniversityRepository candidateUniversityRepository;
  private final SystemConfigurationRepository systemConfigurationRepository;
  private final TemporaryStorageService temporaryStorageService;
  private final StorageUtils storageUtils;
  private final StorageObject candidateFolder;
  @Autowired private DemandRepository demandRepository;
  @Autowired private CandidateElasticsearchRepository candidateElasticsearchRepository;
  @Autowired private ElasticIndexingService elasticIndexingService;
  @Autowired private ModelMapper modelMapper;
  @Autowired private ReminderRepository reminderRepository;
  @Autowired private ReminderService reminderService;
  @Autowired private UniversityRepository universityRepository;
  @Autowired private CandidateStatusRepository candidateStatusRepository;
  @Autowired private CandidateModelAssembler assembler;
  @Autowired private MailService mailService;

  public CandidateService(
      CandidateRepository candidateRepository,
      ObjectMapper objectMapper,
      CandidateUniversityRepository candidateUniversityRepository,
      SystemConfigurationRepository systemConfigurationRepository,
      TemporaryStorageService temporaryStorageService,
      StorageUtils storageUtils,
      InterviewRepository interviewRepository) {
    this.interviewRepository = interviewRepository;
    this.candidateRepository = candidateRepository;
    this.objectMapper = objectMapper;
    this.candidateUniversityRepository = candidateUniversityRepository;
    this.systemConfigurationRepository = systemConfigurationRepository;
    this.temporaryStorageService = temporaryStorageService;
    this.storageUtils = storageUtils;
    this.objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
    this.candidateFolder = storageUtils.getSubDirectory(CANDIDATE_FOLDER_NAME);
  }

  /**
   * get candidate by id
   *
   * @param id of candidate
   * @return candidate properties
   */
  @Transactional(readOnly = true)
  public CandidateDTO getCandidateById(int id) {
    CandidateDTO candidate = convertMapToDTO(candidateRepository.findCandidateById(id));
    if (candidate.getId() == 0) {
      throw new CandidateNotFoundException(id);
    }
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> candidateUniversity = candidateUniversityRepository.getUniversityByCandidateId(id);
    JsonNode node = mapper.convertValue(candidateUniversity, JsonNode.class);
    candidate.setUniversities(node);
    return candidate;
  }


  @Transactional(readOnly = true)
  public Candidate getCandidateByIdToo(int id) {
    return candidateRepository.findById(id).orElseThrow(() -> new CandidateNotFoundException(id));
  }

  /**
   * Get all candidates.
   *
   * @return Candidate
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<EntityModel<CandidateDTO>> findAllCandidates(
      CandidateElasticsearchRequest request) {
    if (request.getFilter() ==null){
      request.setFilter("");
    }
    if(request.getCandidateName() == null){
      request.setCandidateName("");
    }
    if (request.getUniversity() == null){
      request.setUniversity("");
    }
    if(request.getGender() ==null){
      request.setGender("");
    }
    if (request.getPosition() == null){
      request.setPosition("");
    }
    if (request.getFilterBy() == null){
      request.setFilterBy(new String[0]);
    }
    if (request.getCandidateStatus()==null){
      request.setCandidateStatus("");
    }
    Page<CandidateElasticsearchDocument> candidates =
        this.candidateElasticsearchRepository.elasticAdvanceSearch(request);
    return new EntityResponseHandler<>(
        candidates.map(
            candidateElasticsearchDocument ->
                this.assembler.toModel(
                    this.modelMapper.map(candidateElasticsearchDocument, CandidateDTO.class))));
  }

  /**
   * Create a new candidate
   *
   * @param candidateDTO
   * @return
   */
  @SneakyThrows
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CandidateDTO createCandidate(CandidateDTO candidateDTO) {
    Candidate candidate = convertToEntity(candidateDTO);
    candidate.setCandidateStatus(
        candidateStatusRepository
            .findById(candidateDTO.getStatusId())
            .orElseThrow(() -> new CandidateStatusNotFoundException(candidateDTO.getStatusId())));
    if (validateEmailCandidate(candidateDTO.getEmail()) != 0) {
      throw new EmailConflictException("Email is already exist!");
    }
    Candidate savedCandidate = candidateRepository.save(candidate);
    CandidateDTO responseCandidate = covertToDTO(savedCandidate);
    List<Integer> universityIds = new ArrayList<>();
    for (JsonNode university : candidateDTO.getUniversities()) {
      CandidateUniversity candidateUniversity =
          this.getCandidateUniversity(candidate, university.get("id").asInt());
      universityIds.add(candidateUniversity.getUniversity().getId());
    }

    // move files from temp directory to real directory
    // profile
    StorageObject candidateDir =
        candidateFolder
            .getStorageObjectManager()
            .createDirectory(String.valueOf(responseCandidate.getId()));
    StorageObject candidateProfileDir =
        candidateDir.getStorageObjectManager().createDirectory(SUB_PATH_PROFILE);
    StorageObject candidateAttachmentDir =
        candidateDir.getStorageObjectManager().createDirectory(SUB_PATH_CV);

    if (!Strings.isNullOrEmpty(candidateDTO.getPhotoUrl())) {
      temporaryStorageService.moveTo(
          Collections.singletonList(candidateDTO.getPhotoUrl()), candidateProfileDir, false);
    }
    if (!candidateDTO.getFilenames().isEmpty()) {
      temporaryStorageService.moveTo(candidateDTO.getFilenames(), candidateAttachmentDir, true);
    }
    // move cv from temp directory
    CandidateInsertElasticRequest request = new CandidateInsertElasticRequest(savedCandidate);
    request.setUniversityIds(universityIds);
    this.elasticIndexingService.execute(request);
    responseCandidate.setUniversities(
        this.objectMapper.convertValue(
            candidateUniversityRepository.getUniversityByCandidateId(responseCandidate.getId()),
            JsonNode.class));
    return responseCandidate;
  }

  private CandidateUniversity getCandidateUniversity(Candidate candidate, int universityId) {
    CandidateUniversity candidateUniversity = new CandidateUniversity();
    candidateUniversity.setCandidate(candidate);
    Optional<University> university1 = this.universityRepository.findById(universityId);
    candidateUniversity.setUniversity(
        university1.orElseThrow(() -> new UniversityNotFoundException(universityId)));
    candidateUniversityRepository.save(candidateUniversity);
    return candidateUniversity;
  }

  /**
   * Update candidate
   *
   * @param candidateDTO
   * @return
   */

  @CacheEvict(allEntries = true)
  @SneakyThrows
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CandidateDTO updateCandidate(CandidateDTO candidateDTO) {
    CandidateDTO candidate = this.getCandidateById(candidateDTO.getId());
    Candidate candidate1 = convertToEntity(candidateDTO);
    candidate1.setCandidateStatus(candidateStatusRepository.findById(candidateDTO.getStatusId()).orElseThrow(() -> new CandidateStatusNotFoundException(candidateDTO.getStatusId())));
    candidate1.setEmail(candidateDTO.getEmail());
    candidate1.setDeleted(candidate.isDeleted());
    candidate1.setActive(candidate.isActive());
    candidate1.setCreatedAt(candidate.getCreatedAt());
    candidate1.setCreatedBy(candidate.getCreatedBy());
    // save into candidate
    Candidate updatedCandidate = candidateRepository.save(candidate1);
    // update the candidate university tables
    // delete and set new
    candidateUniversityRepository.deleteCandidateUniversityByCandidateId(candidateDTO.getId());
    for (JsonNode university : candidateDTO.getUniversities()) {
      if (university.get("id") == null){
        break;
      }
        CandidateUniversity candidateUniversity =
            this.getCandidateUniversity(candidate1, university.get("id").asInt());
        candidateUniversityRepository.save(candidateUniversity);
    }

    /*
     * Update file profile candidate
     *
     */
    StorageObject candidateDir = candidateFolder.getStorageObjectManager().getChild(candidateDTO.getId() + "/" + SUB_PATH_PROFILE);
    // if photoUrl is null or empty it means file should be deleted
    if (Strings.isNullOrEmpty(candidateDTO.getPhotoUrl())) {
      // remove files on children
      this.removeFilesOnChildren(candidateDir.getStorageObjectManager());
    } else {
      if (!String.valueOf(candidate.getPhotoUrl()).equals(candidateDTO.getPhotoUrl())) {
        // remove files on children
        // if file exists on folder then remove it.
        for (StorageObject f : candidateDir.getStorageObjectManager().getChildren()) {
          if (!String.valueOf(candidateDTO.getPhotoUrl()).equals(f.getName())) {
            f.getStorageObjectManager().remove();
          }
        }
        // move file from temp folder to profile candidate folder
        temporaryStorageService.moveTo(Collections.singletonList(candidateDTO.getPhotoUrl()), candidateDir, false);

      }
    }
    this.elasticIndexingService.execute(new CandidateUpdateElasticRequest(updatedCandidate));
    return candidateDTO;
  }


  // find all candidates for the select boxes
  public EntityResponseHandler<Map<String, Object>> findAllCandidatesOnSelectBox(
      String filter, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page - 1, pageSize);
    if (Strings.isNullOrEmpty(filter)) {
      filter = "";
    }
    return new EntityResponseHandler<>(
        candidateRepository.findAllCandidatesOnSelectBox(filter, pageable));
  }

  /**
   * Delete a candidate just update the field isDelete to true or false
   *
   * @param id
   * @param isDelete
   * @return
   */

  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void deleteCandidate(int id, boolean isDelete) {
    Candidate candidate =
        candidateRepository.findById(id).orElseThrow(() -> new CandidateNotFoundException(id));
    candidate.setDeleted(isDelete);
    Candidate updatedCandidate = candidateRepository.save(candidate);


      this.unscheduledAllRemindersByCandidateId(id, true);

    this.elasticIndexingService.execute(new CandidateUpdateElasticRequest(updatedCandidate));
  }
//  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
//  public void softDeleteReminderById(int reminderId) {
//    Reminder reminder =
//            reminderRepository
//                    .findByIdAndDeletedIsFalse(reminderId)
//                    .orElseThrow(() -> new ReminderNotFoundException(reminderId));
//    reminder.setDeleted(true);
//    reminder.setActive(false);
//    reminderRepository.save(reminder);
//    historyOfDelete.add(reminderId);
//    this.elasticIndexingService.execute(new ReminderUpdateElasticRequest(reminder));
//    final String reminderType = reminder.getReminderType().getId();
//    this.unScheduledJob(reminderId, reminderType);
//  }
  /**
   * update status id on entity candidate
   *
   * @param id Candidate id
   * @return CandidateDTO
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public CandidateDTO updateStatusCandidate(int id, int statusId) {
    Candidate candidate =
        candidateRepository.findById(id).orElseThrow(() -> new CandidateNotFoundException(id));
    Optional<CandidateStatus> candidateStatus = this.candidateStatusRepository.findById(statusId);
    candidate.setCandidateStatus(
        candidateStatus.orElseThrow(() -> new CandidateStatusNotFoundException(statusId)));
    Candidate updatedCandidate = candidateRepository.save(candidate);
    CandidateDTO candidateDTO = covertToDTO(updatedCandidate);
    this.elasticIndexingService.execute(new CandidateUpdateElasticRequest(updatedCandidate));
    if (candidateDTO != null) {
      mailService.setMailStatusChange(candidateDTO);
    }
    return candidateDTO;
  }

  /**
   * Report all candidates by last interview
   *
   * @param request Search request.
   * @return Candidate details.
   */
  public EntityResponseHandler<EntityModel<CandidateDTO>> reportCandidates(
      CandidateElasticsearchRequest request) {
    Page<CandidateReportElasticsearchDocument> candidates =
        this.candidateElasticsearchRepository.getReportFromElasticsearch(request);
    return new EntityResponseHandler<>(
        candidates.map(
            candidateElasticsearchDocument ->
                this.assembler.toModel(
                    this.modelMapper.map(candidateElasticsearchDocument, CandidateDTO.class))));
  }

  public Long validateEmailCandidate(String email) {
    return candidateRepository.validateEmailCandidate(email.trim());
  }

  public String formatDate(String inDate) {
    SimpleDateFormat inSDF = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-MM-dd");
    String outDate = "";
    if (inDate != null) {
      try {
        Date date = inSDF.parse(inDate);
        outDate = outSDF.format(date);
      } catch (ParseException ex) {
        log.error("{}", ex.getMessage());
      }
    }
    return outDate;
  }

  /**
   * Advanced Search of candidates
   *
   * @param name
   * @param from
   * @param gender
   * @param gpa
   * @param position
   * @param pageable
   * @return
   * @throws JsonProcessingException
   */
  @Transactional
  public EntityResponseHandler<EntityModel<CandidateDTO>> findAllByAdvancedSearch(
      String name, String from, String gender, float gpa, String position, Pageable pageable)
      throws JsonProcessingException {
    if (Strings.isNullOrEmpty(name)
        && Strings.isNullOrEmpty(from)
        && Strings.isNullOrEmpty(gender)
        && gpa == 0
        && Strings.isNullOrEmpty(position)) {
      throw new AdvancedSearchBadRequestException("At least one field must have a value");
    }
    return new EntityResponseHandler<>(
        candidateRepository
            .findAllAsAdvancedSearch(name, from, gender, gpa, position, pageable)
            .map(this::convertMapToEntityModel),
        this.objectMapper);
  }

  // this function is used for response to client when update candidate

  /**
   * view candidate by id
   *
   * @param id
   * @return
   */
  @Transactional(readOnly = true)
  public EntityModel<CandidateDTO> viewCandidateById(int id) {
    Map<String, Object> candidateMap = candidateRepository.viewCandidateById(id);

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> candidateUniversity = candidateUniversityRepository.getUniversityByCandidateId(id);
    JsonNode nodeUniversity = mapper.convertValue(candidateUniversity, JsonNode.class);
    JsonNode nodeCandidateStatus = mapper.convertValue(candidateMap.get("candidatestatus"), JsonNode.class);
    if (candidateMap.isEmpty()) {
      throw new CandidateNotFoundException(id);
    }
    CandidateDTO candidate = convertMapToDTO(candidateMap);
    candidate.setUniversities(nodeUniversity);
    candidate.setCandidateStatus(nodeCandidateStatus);
    candidate.setInterviews(
        this.objectMapper.valueToTree(
            candidateRepository.getInterviewsByCandidateId(candidate.getId())));
    candidate.setActivities(
        this.objectMapper.valueToTree(
            candidateRepository.getActivityByCandidateId(candidate.getId())));
    return convertDTOToEntityViewModel(candidate);
  }

  @Transactional(readOnly = true)
  public List<Candidate> findTopCandidateByGpa(Pageable pageable) {
    SystemConfiguration config = new SystemConfiguration();
    SystemConfiguration getConfig =
        systemConfigurationRepository
            .findSystemConfigurationByConfigKey(GPA.getValue())
            .orElse(null);
    if (getConfig == null) {
      config.setConfigKey(GPA.getValue());
      config.setConfigValue("3.0");
      config.setDescription("filter gpa for top candidate on dashboard");
      systemConfigurationRepository.save(config);
    } else {
      config.setConfigValue(getConfig.getConfigValue());
    }
    return candidateRepository
        .findByIsDeletedIsFalseAndActiveIsTrueAndGpaGreaterThanEqualOrderByGpaDesc(
            Float.parseFloat(config.getConfigValue()), pageable);
  }

  /**
   * Convert DTO to Entity
   *
   * @param candidateDTO
   * @return
   */
  public Candidate convertToEntity(CandidateDTO candidateDTO) {
    return modelMapper.map(candidateDTO, Candidate.class);
  }

  public CandidateDTO convertMapToDTO(Map<String, Object> candidateMap) {
    return modelUnderScoreNamingMapper().map(candidateMap, CandidateDTO.class);
  }

  /**
   * Convert Entity to DTO
   *
   * @param candidate
   * @return
   */
  public CandidateDTO covertToDTO(Candidate candidate) {
    return modelMapper.map(candidate, CandidateDTO.class);
  }

  private EntityModel<CandidateDTO> convertMapToEntityModel(Map<String, Object> candidate) {
    return assembler.toModel(modelMapperForList().map(candidate, CandidateDTO.class));
  }

  private EntityModel<CandidateDTO> convertDTOToEntityViewModel(CandidateDTO candidate) {
    return assembler.toViewModel(modelMapper.map(candidate, CandidateDTO.class));
  }

  // configuration model mapper for list
  private ModelMapper modelMapperForList() {
    ModelMapper mapper = new ModelMapper();
    mapper
        .getConfiguration()
        .setSourceNameTokenizer(NameTokenizers.CAMEL_CASE)
        .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE);
    return mapper;
  }

  private ModelMapper modelUnderScoreNamingMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper
        .getConfiguration()
        .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
        .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
    return mapper;
  }

  @SneakyThrows
  public Map<String, Object> uploadProfile(MultipartFile filename) {
    return Maps.newHashMap(
        ImmutableMap.of("photoUrl", temporaryStorageService.uploadToTemporaryStorage(filename)));
  }

  @SneakyThrows
  public Map<String, Object> getFilesAttachment(int id) {
    List<String> filenames = new ArrayList<>();
    String candidateCvPath = id + File.separator + SUB_PATH_CV;
    StorageObjectManager candidateCvFolderManager =
        candidateFolder
            .getStorageObjectManager()
            .getChild(candidateCvPath)
            .getStorageObjectManager();
    if (candidateCvFolderManager.exists()) {
      candidateCvFolderManager.getChildren().forEach(f -> filenames.add(f.getName()));
    }
    return Maps.newHashMap(ImmutableMap.of(FILE_NAMES, filenames));
  }

  public Map<String, Object> uploadAttachment(MultipartFile[] filenames) {
    return Maps.newHashMap(
        ImmutableMap.of(
            FILE_NAMES, temporaryStorageService.uploadMultiToTemporaryStorage(filenames)));
  }

  @SneakyThrows
  public Map<String, Object> uploadAttachmentOnUpdate(int id, MultipartFile[] filenames) {
    List<String> fileNames = new ArrayList<>();
    StorageObjectManager containerManager =
        candidateFolder
            .getStorageObjectManager()
            .getChild(id + "/" + SUB_PATH_CV)
            .getStorageObjectManager();
    for (MultipartFile file : filenames) {
      String uuid = UUID.randomUUID().toString(); // uuid_fileName.extension
      String fileName = uuid + "_" + file.getOriginalFilename();
      storageUtils.saveFile(file.getInputStream(), fileName, containerManager);
      fileNames.add(fileName);
    }
    return Maps.newHashMap(ImmutableMap.of(FILE_NAMES, fileNames));
  }

  public Resource loadFile(String id, String filename) {
    return storageUtils.loadFile(filename, candidateFolder.getStorageObjectManager().getChild(id));
  }

  @SneakyThrows
  public void removeFile(int id, String filename) {
    for (StorageObject f :
        candidateFolder
            .getStorageObjectManager()
            .getChild(String.valueOf(id))
            .getStorageObjectManager()
            .getChildren(3, null)) {
      if (f.getName().startsWith(filename)) {
        f.getStorageObjectManager().remove();
      }
    }
  }

  // remove file or folder from storage
  @SneakyThrows
  public void removeFilesOnChildren(StorageObjectManager storageObjectManager) {
    for (StorageObject f : storageObjectManager.getChildren()) {
      if (f.getStorageObjectManager().exists()) {
        f.getStorageObjectManager().remove();
      }
    }
  }

  public void hardDelete(int candidateId) {
    Optional<Candidate> candidateObject = this.candidateRepository.findById(candidateId);
    Candidate candidate =
        candidateObject.orElseThrow(() -> new CandidateNotFoundException(candidateId));
    this.unscheduledAllRemindersByCandidateId(candidateId, false);
    this.candidateRepository.deleteById(candidateId);
    this.elasticIndexingService.execute(new CandidateHardDeleteRequest(candidate));
  }

  private void unscheduledAllRemindersByCandidateId(int candidateId, boolean alsoDeleteReminder) {
    List<Reminder> candidateReminderList =
        this.reminderRepository.findByReminderTypeIdAndCandidateId(
            ReminderType.SPECIAL.getValue(), candidateId);
    if (!candidateReminderList.isEmpty()) {
      candidateReminderList.forEach(
          reminder -> {
            if (alsoDeleteReminder) {
              reminderRepository.delete(reminder);
            }
            reminderService.unScheduledJob(reminder.getId(), reminder.getReminderType().getId());
          });
    }
  }

  /**
   * @param page
   * @param pageSize
   * @return
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<Map<String, Object>> findAllCandidateByIdShowInDemand(
      int page, int pageSize) {
    List<Integer> candidateIdOnInterview =
        interviewRepository.findByCandidateIdAndIsDeleteIsFalse();
    List<String> listId = new ArrayList<>();
    Set<Integer> candidateIdOnDemand = new HashSet<>();
    ArrayList<String> listDemand = demandRepository.findAllNbCandidate();
    for (String id : listDemand) {
      if (!id.equals("")) {
        listId.add(id);
      }
    }
    for (String allId : listId) {
      String[] arr = allId.split(",");
      for (String w : arr) {
        candidateIdOnDemand.add(Integer.parseInt(w));
      }
    }
    Pageable pageable = PageRequest.of(page - 1, pageSize);
    List<String> candidateStatus = Arrays.asList("New Request", "Failed");
    if (candidateIdOnDemand.isEmpty()) {
      return new EntityResponseHandler<>(
          candidateRepository.findCandidateByIds(
              candidateIdOnInterview, candidateStatus, pageable));
    }
    if (candidateIdOnInterview.size() >= candidateIdOnDemand.size()) {
      candidateIdOnInterview.removeAll(candidateIdOnDemand);
      return new EntityResponseHandler<>(
          candidateRepository.findCandidateByIds(
              candidateIdOnInterview, candidateStatus, pageable));
    }
    return null;
  }

  /**
   * @param dateFrom
   * @param dateTo
   * @param position
   * @param page
   * @param pageSize
   * @param sortDirection
   * @param sortByField
   * @return
   */
  public EntityResponseHandler<Map<String, Object>> getAllCandidateAdvanceReport(
      Date dateFrom,
      Date dateTo,
      String position,
      int page,
      int pageSize,
      String sortDirection,
      String sortByField) {
    Pageable pageable =
        PageRequest.of(
            page - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    Set<Integer> candidateIdsStaff = new HashSet<>();
    Set<Integer> candidateIdsIntern = new HashSet<>();
    Set<Integer> candidateIdsFollowingUp = new HashSet<>();
    String paramIntern = "Intern";
    String paramFollowingUp = "Following Up";
    ArrayList<AdvanceReportResponse> advanceReportList =
        interviewRepository.findAllPosition(dateFrom, dateTo);
    advanceReportList.forEach(System.out::println);
    for (AdvanceReportResponse ad : advanceReportList) {
      log.info(
          "candidateId="
              + ad.getCandidateId()
              + " Interview Position="
              + ad.getInterviewPosition()
              + " Interview Status="
              + ad.getInterviewStatus());
      // staff
      if (!ad.getInterviewPosition().contains(paramIntern)
          && !ad.getInterviewStatus().equalsIgnoreCase(paramFollowingUp)) {
        candidateIdsStaff.add(ad.getCandidateId());
      }
      // following-up
      else if (!ad.getInterviewPosition().contains(paramIntern)
          && ad.getInterviewStatus().equals(paramFollowingUp)) {
        candidateIdsFollowingUp.add(ad.getCandidateId());
      }
      // intern
      else if (ad.getInterviewPosition().contains(position)
          && ad.getInterviewStatus().equals(paramFollowingUp)) {
        candidateIdsIntern.add(ad.getCandidateId());
      }
      else if (ad.getInterviewPosition().contains(position)
          && !ad.getInterviewStatus().equals(paramFollowingUp)) {
        candidateIdsIntern.add(ad.getCandidateId());
      }
    }
    if (position.equalsIgnoreCase("intern")) {
      return new EntityResponseHandler<>(
          candidateRepository.findAllCandidatesAdvanceReportIntern(
              dateFrom, dateTo, candidateIdsIntern, pageable));
    }
    if (position.equalsIgnoreCase("Staff")) {
      return new EntityResponseHandler<>(
          candidateRepository.findAllCandidatesAdvanceReport(
              dateFrom, dateTo, candidateIdsStaff, pageable));
    }
    if (position.equalsIgnoreCase("Following Up")) {
      return new EntityResponseHandler<>(
          candidateRepository.findAllCandidatesAdvanceReport(
              dateFrom, dateTo, candidateIdsFollowingUp, pageable));
    }
    return null;
  }
}
