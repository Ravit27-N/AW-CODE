package com.allweb.rms.service;

import com.allweb.rms.component.DemandModelAssembler;
import com.allweb.rms.entity.dto.*;
import com.allweb.rms.entity.elastic.DemandElasticsearchDocument;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Demand;
import com.allweb.rms.entity.jpa.JobDescription;
import com.allweb.rms.entity.jpa.Project;
import com.allweb.rms.exception.CandidateIDConflictException;
import com.allweb.rms.exception.CandidateIdLessThanOException;
import com.allweb.rms.exception.CandidateIdNullDataException;
import com.allweb.rms.exception.DemandNotFoundException;
import com.allweb.rms.exception.JobDescriptionNotFoundException;
import com.allweb.rms.exception.ProjectNotFoundException;
import com.allweb.rms.repository.elastic.DemandElasticRepository;
import com.allweb.rms.repository.jpa.CandidateRepository;
import com.allweb.rms.repository.jpa.DemandRepository;
import com.allweb.rms.repository.jpa.JobDescriptionRepository;
import com.allweb.rms.repository.jpa.ProjectRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.index.PutTemplateRequest;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class DemandService {
  private final DemandRepository demandRepository;
  private final ProjectRepository projectRepository;
  private final JobDescriptionRepository jobDescriptionRepository;

  private final CandidateRepository candidateRepository;
  private final DemandElasticRepository demandElasticsearchRepository;
  private final ModelMapper modelMapper;
  private final ElasticsearchTemplate elasticsearchTemplate;
  private final ObjectMapper objectMapper;
  @Autowired private DemandModelAssembler assembler;

  @Autowired
  public DemandService(
      DemandRepository demandRepository,
      ModelMapper modelMapper,
      ProjectRepository projectRepository,
      JobDescriptionRepository jobDescriptionRepository,
      CandidateRepository candidateRepository,
      DemandElasticRepository demandElasticsearchRepository,
      ElasticsearchTemplate elasticsearchTemplate,
      ObjectMapper objectMapper) {
    this.demandElasticsearchRepository = demandElasticsearchRepository;
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.objectMapper = objectMapper;
    this.candidateRepository = candidateRepository;
    this.demandRepository = demandRepository;
    this.modelMapper = modelMapper;
    this.projectRepository = projectRepository;
    this.jobDescriptionRepository = jobDescriptionRepository;
  }

  /**
   * @param demandDTO
   * @return
   */
  @SneakyThrows
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public DemandDTO saveDemand(DemandDTO demandDTO) {
    Demand demand = dtoToEntity(demandDTO);
    // Check when create same project and same jobDescription
    if (validateProjectJob(demandDTO.getProjectId(), demandDTO.getJobDescriptionId()) > 0) {
      throw new DemandNotFoundException("duplicate with projectName and Job DescriptionName");
    }
    Project project =
        projectRepository
            .findById(demandDTO.getProjectId())
            .orElseThrow(() -> new ProjectNotFoundException(demandDTO.getProjectId()));
    JobDescription jobDescription =
        jobDescriptionRepository
            .findById(demandDTO.getJobDescriptionId())
            .orElseThrow(
                () ->
                    new JobDescriptionNotFoundException(
                        "Cannot Found Job Description by ID: " + demandDTO.getJobDescriptionId()));
    demand.setProject(project);
    demand.setJobDescription(jobDescription);
    demand.setNbRequired(demandDTO.getNbRequired());
    demand.setExperienceLevel(demandDTO.getExperienceLevel());
    demand.setNbCandidates("");
    demand.setDeadLine(demandDTO.getDeadLine());
    demand.setActive(demandDTO.isActive());
    Demand demand1 = this.demandRepository.save(demand);
    this.demandElasticsearchRepository.save(
        this.modelMapper.map(demand1, DemandElasticsearchDocument.class));
    return entityToDTO(demand1);
  }

  public int validateProjectJob(int projectId, int jobDescriptionId) {
    return demandRepository.checkValidateCreateDemand(projectId, jobDescriptionId);
  }

  /**
   * @param id
   * @return
   */
  public DemandDTO getDemandById(int id) {
    //    DemandDTO demandDTO = convertMapToDTO(demandRepository.findDemandById(id));
    DemandDTO demandDTO =
        this.modelMapper.map(
            demandElasticsearchRepository
                .findById(id)
                .orElseThrow(
                    () -> new DemandNotFoundException("Demand was not founded by ID:" + id)),
            DemandDTO.class);
    if (demandDTO.getId() == 0) {
      throw new DemandNotFoundException("Demand was not founded by ID:" + id);
    }
    String allCandidateId = demandRepository.findNbCandidate(id);
    String[] count = allCandidateId.split(",");
    int candidateId = 0;
    List<Candidate> list = new ArrayList<>();
    if (allCandidateId.length() > 0) {
      for (String c : count) {
        candidateId = Integer.parseInt(c);
        CandidateDTO candidate =
            convertCandidateMapToDTO(candidateRepository.findCandidateById(candidateId));
        Candidate can = dtoCandidateToEntity(candidate);
        list.add(can);
      }
      JsonNode jsonNode = objectMapper.valueToTree(list);
      demandDTO.setCandidate(jsonNode);
    }
    return demandDTO;
  }

  /**
   * @param id
   * @param active
   * @return
   */
  public DemandDTO updateActiveDemand(int id, boolean active) {
    Demand demand =
        demandRepository
            .findById(id)
            .orElseThrow(() -> new DemandNotFoundException("Demand was not founded by ID:" + id));
    demand.setId(id);
    demand.setActive(active);
    DemandElasticsearchDocument demandElasticsearchDocument = demandElasticsearchRepository.findById(id).orElseThrow(() -> new DemandNotFoundException("Demand was not founded by ID:" + id));
    demandElasticsearchDocument.setId(id);
    demandElasticsearchDocument.setActive(active);
    demandElasticsearchRepository.save(demandElasticsearchDocument);
    return entityToDTO(demandRepository.save(demand));
  }

  /**
   * @param id
   * @param isDelete
   * @return
   */
  public DemandDTO softDeleteDemand(int id, boolean isDelete) {
    Demand demand =
        demandRepository
            .findById(id)
            .orElseThrow(() -> new DemandNotFoundException("Demand was not founded by ID:" + id));
    demand.setId(id);
    demand.setDeleted(isDelete);
    DemandElasticsearchDocument demandElasticsearchDocument =
        this.demandElasticsearchRepository
            .findById(id)
            .orElseThrow(() -> new DemandNotFoundException("Demand was not founded by ID:" + id));
    demandElasticsearchDocument.setId(id);
    demandElasticsearchDocument.setDeleted(isDelete);
    this.demandElasticsearchRepository.save(demandElasticsearchDocument);
    return entityToDTO(demandRepository.save(demand));
  }

  /**
   * @param id
   */
  public void hardDeleteDemand(int id) {
    demandElasticsearchRepository.deleteById(id);
    demandRepository.deleteById(id);
  }

  /**
   * @param demandDTO
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public DemandDTO updateDemand(DemandDTO demandDTO) {
    Demand demand =
        demandRepository
            .findById(demandDTO.getId())
            .orElseThrow(
                () ->
                    new DemandNotFoundException(
                        "Demand was not founded by ID:" + demandDTO.getId()));
    DemandElasticsearchDocument elasticDemand =
        this.demandElasticsearchRepository
            .findById(demandDTO.getId())
            .orElseThrow(
                () ->
                    new DemandNotFoundException(
                        "Demand was not founded by ID:" + demandDTO.getId()));
    log.info(
        "------------data------------"
            + validateUpdateProjectJob(
                demandDTO.getProjectId(), demandDTO.getJobDescriptionId(), demandDTO.getId()));
    if (validateUpdateProjectJob(
            demandDTO.getProjectId(), demandDTO.getJobDescriptionId(), demandDTO.getId())
        > 0) {
      throw new DemandNotFoundException(
          "You cannot update. ProjectId and Job DescriptionId are exist");
    }
    JobDescription jobDescription =
        jobDescriptionRepository
            .findById(demandDTO.getJobDescriptionId())
            .orElseThrow(
                () ->
                    new JobDescriptionNotFoundException(
                        "Cannot Found Job Description by ID: " + demandDTO.getJobDescriptionId()));
    Project project =
        projectRepository
            .findById(demandDTO.getProjectId())
            .orElseThrow(() -> new ProjectNotFoundException(demandDTO.getProjectId()));
    demand.setProject(project);
    demand.setJobDescription(jobDescription);
    demand.setNbRequired(demandDTO.getNbRequired());
    demand.setExperienceLevel(demandDTO.getExperienceLevel());
    demand.setDeadLine(demandDTO.getDeadLine());
    demand.setActive(demandDTO.isActive());
    // nbCandidate > nbRequired
    if (demand.getNbCandidates().split(",").length > demand.getNbRequired()) {
      throw new DemandNotFoundException("Resource Candidate > QTY Candidate");
    }
    // nbCandidate == demand.getNbRequired() -> status=false
    // nbCandidate != demand.getNbRequired() -> status=true
    boolean demandStatus =
        demand.getNbCandidates().equals("")
            || demand.getNbCandidates().split(",").length != demand.getNbRequired();
    demand.setStatus(demandStatus);
    Demand demand1 = demandRepository.save(demand);
    demandElasticsearchRepository.save(
        this.modelMapper.map(demand1, DemandElasticsearchDocument.class));
    return entityToDTO(demand1);
  }

  public int validateUpdateProjectJob(int projectId, int jobDescriptionId, int demandId) {
    return demandRepository.checkValidateUpdateDemand(projectId, jobDescriptionId, demandId);
  }

  /**
   * @param page
   * @param pageSize
   * @param filter
   * @param sortDirection
   * @param sortByField
   * @param active
   * @return 1. active is true 2. isDeleted=true && non filter 3. isDeleted=false && non filter 4.
   *     isDeleted=true && has filter 5. isDeleted=false && has filter
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<DemandResponse> getAllDemands(
      int page,
      int pageSize,
      boolean isDeleted,
      String filter,
      String sortDirection,
      String sortByField,
      boolean active) {
    Pageable pageable =
        PageRequest.of(
            page - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    DemandElasticsearchRequest request =
        DemandElasticsearchRequest.builder()
            .isDeleted(isDeleted)
            .sortDirection(sortDirection)
            .sortByField(sortByField)
            .pageable(pageable)
            .filter(filter)
            .active(active)
            .build();
    //
    //    Page<DemandElasticsearchDocument> demand =
    //            this.demandElasticsearchRepository.findAllByElasticsearch(request,pageable);
    //    return new EntityResponseHandler<>(
    //            demand.map(
    //                    demandElasticsearchDocument ->
    //                            this.assembler.toModel(
    //                                    this.modelMapper.map(demandElasticsearchDocument,
    // DemandDTO.class))));
    // return new EntityResponseHandler<>(demandElasticsearchRepository.findAll(pageable).map(
    //         object -> this.modelMapper.map(object,DemandResponse.class)));

    //    Page<DemandElasticsearchDocument> demandElasticsearchDocuments =
    // demandElasticsearchRepository.findAllByElasticsearch(request,pageable);
    //    return new EntityResponseHandler<>(
    //            demandElasticsearchDocuments.map(
    //                    candidateElasticsearchDocument ->
    //                                    this.modelMapper.map(candidateElasticsearchDocument,
    // DemandResponse.class)));
    if (active) { // Get Pin On Dashboard
      return new EntityResponseHandler<>(
          demandRepository
              .findByActiveIsTrue(pageable)
              .map(entity -> modelMapper.map(entity, DemandResponse.class)));
    }
    if (isDeleted && Strings.isNullOrEmpty(filter)) {
      return new EntityResponseHandler<>(
          demandRepository
              .getAllByDeletedIsTrue(pageable)
              .map(entity -> modelMapper.map(entity, DemandResponse.class)));
    }
    if (!isDeleted && Strings.isNullOrEmpty(filter)) {
      return new EntityResponseHandler<>(
          demandRepository
              .getAll(pageable)
              .map(entity -> modelMapper.map(entity, DemandResponse.class)));
    }
    if (isDeleted) {
      return new EntityResponseHandler<>(
          demandRepository
              .fetchAllByFilteringFieldAndDeletedIsTrue(filter, pageable)
              .map(entity -> modelMapper.map(entity, DemandResponse.class)));
    } else {
      return new EntityResponseHandler<>(
          demandRepository
              .fetchAllByFilteringField(filter, pageable)
              .map(entity -> modelMapper.map(entity, DemandResponse.class)));
    }
  }
//  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  public EntityResponseHandler<DemandDTO_List>  getAllDemandResponse(
      int page,
      int pageSize,
      boolean isDeleted,
      String filter,
      String sortDirection,
      String sortByField,
      boolean active) {
    DemandElasticsearchRequest request =
        DemandElasticsearchRequest.builder()
            .isDeleted(isDeleted)
            .sortDirection(sortDirection)
                .sortByField(sortByField)
            .pageable(   PageRequest.of(
                    page - 1,
                    pageSize,
                    Sort.by(Sort.Direction.fromString(sortDirection), sortByField)))
            .filter(filter)
            .active(active)
            .build();
    Page<DemandElasticsearchDocument> demand =
        this.demandElasticsearchRepository.findAllByElasticsearch(request);

    return new EntityResponseHandler<>(
        demand.map(
            demandElasticsearchDocument ->
                modelMapper.map(demandElasticsearchDocument, DemandDTO_List.class)));
  }

  public Page<DemandResponse> getlist() {
    return demandRepository.getAll(PageRequest.of(0, 10));
  }

  public void deleteIndex() {
    elasticsearchTemplate.indexOps(IndexCoordinates.of("idx_demand")).delete();
  }
  public void createIndex() {
    elasticsearchTemplate.indexOps(IndexCoordinates.of("idx_demand")).create();
  }
  public void putIndex(){
    elasticsearchTemplate.indexOps(IndexCoordinates.of("idx_demand")).putMapping();
  }


  public DemandDTO addDemandWithElastic(DemandResponse demand) {
    DemandElasticsearchDocument demandElasticsearchDocument =
        this.modelMapper.map(demand, DemandElasticsearchDocument.class);

    demandElasticsearchRepository.save(demandElasticsearchDocument);
    return modelMapper.map(demandElasticsearchDocument, DemandDTO.class);
  }

  /**
   * @param demandId
   * @param candidateIds 1.count candidate , 10,20,30 -> 3 2.check conflict , 10,10 -> error 3.check
   *     candidateId -10<0 -> error 4.compare nbRequired & nbCandidate if(nbRequired == nbCandidate)
   *     status=false 5.if status=false can't input continue (nbRequired < nbCandidate)
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public DemandDTO saveCandidateInToDemand(int demandId, String candidateIds) {
    Demand demand =
        demandRepository
            .findById(demandId)
            .orElseThrow(
                () -> new DemandNotFoundException("Demand was not founded by ID:" + demandId));
    String oldCandidate = demand.getNbCandidates();
    String countCandidate;
    boolean status = demand.isStatus();
    if (!status) {
      throw new DemandNotFoundException("Cannot add candidate more. We are closed this demand.");
    }
    if (candidateIds.equals("")) {
      throw new DemandNotFoundException("When add candidate to demand. candidateId cannot empty.");
    }
    ArrayList<String> candidateId = demandRepository.findAllNbCandidate();

    if (this.isExistIds(candidateIds, candidateId)) {
      throw new CandidateIDConflictException();
    }
    countCandidate = oldCandidate + candidateIds + ",";
    demand.setNbCandidates(countCandidate);
    if (demand.getNbCandidates().split(",").length == demand.getNbRequired()) {
      demand.setStatus(false);
    }
    if (demand.getNbCandidates().split(",").length > demand.getNbRequired()) {
      throw new DemandNotFoundException("Resource Candidate > QTY Candidate");
    }
    demand.setNbCandidates(countCandidate);
    Demand demand1 = demandRepository.save(demand);
    demandElasticsearchRepository.save(
        this.modelMapper.map(demand1, DemandElasticsearchDocument.class));
    return entityToDTOOne(demand1);
  }

  /**
   * @param demandId
   * @param candidateId 1. nb-required == nb_candidate change status(true) 2. candidateId no data ->
   *     error
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public DemandDTO deleteCandidateFromDemand(int demandId, int candidateId) {
    DemandElasticsearchDocument demandElasticsearchDocument =
            demandElasticsearchRepository.findById(demandId)
                    .orElseThrow(
                            () -> new DemandNotFoundException("Demand was not founded by ID:" + demandId));
    String allCandidateId = demandElasticsearchDocument.getNbCandidates();
    if (allCandidateId.equals("")) {
      throw new CandidateIdNullDataException();
    }
    String[] count = allCandidateId.split(",");
    int nbRequired = demandElasticsearchDocument.getNbRequired();
    int i = 0;
    String newCandidate = "";
    for (String c : count) {
      i++;
      if (Integer.parseInt(c) == candidateId) {
        log.info("");
      } else {
        newCandidate = newCandidate + c + ",";
      }
    }
    if (nbRequired == i) {
      demandElasticsearchDocument.setStatus(true);
    }
    if (candidateId < 0) {
      throw new CandidateIdLessThanOException();
    }
    demandElasticsearchDocument.setNbCandidates(newCandidate);
    demandElasticsearchRepository.save(demandElasticsearchDocument);
    return entityToDTOOne(demandRepository.save(this.modelMapper.map(demandElasticsearchDocument,Demand.class)));
  }

  private DemandDTO convertMapToDTO(Map<String, Object> demandMap) {
    return modelUnderScoreNamingMapper().map(demandMap, DemandDTO.class);
  }

  private ModelMapper modelUnderScoreNamingMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper
        .getConfiguration()
        .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
        .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
    return mapper;
  }

  private DemandDTO entityToDTO(Demand demand) {
    DemandDTO model = modelMapper.map(demand, DemandDTO.class);
    model.setJobDescriptionId(demand.getJobDescription().getId());
    model.setProjectId(demand.getProject().getId());
    return model;
  }

  private Demand dtoToEntity(DemandDTO demandDTO) {
    return modelMapper.map(demandDTO, Demand.class);
  }

  private DemandDTO entityToDTOOne(Demand demand) {
    return modelMapper.map(demand, DemandDTO.class);
  }

  public CandidateDTO convertCandidateMapToDTO(Map<String, Object> candidateMap) {
    return modelUnderScoreNamingMapper().map(candidateMap, CandidateDTO.class);
  }

  public Candidate dtoCandidateToEntity(CandidateDTO candidateDTO) {
    return modelMapper.map(candidateDTO, Candidate.class);
  }

  private boolean isExistId(String ids, String oldIds) {
    boolean status = false;
    for (int i = 0; i < ids.split(",").length; i++) {
      for (int j = 0; j < oldIds.split(",").length; j++) {
        if (ids.split(",")[i].equals(oldIds.split(",")[j])) {
          status = true;
          break;
        }
      }
    }
    return status;
  }

  private boolean isExistIds(String ids, ArrayList<String> allIds) {
    boolean status = false;
    for (int i = 0; i < allIds.size(); i++) {
      if (Strings.isNullOrEmpty(allIds.get(i))) {
        continue;
      }
      if (this.isExistId(ids, allIds.get(i))) {
        status = true;
        break;
      }
    }
    return status;
  }

  public boolean validateAddCandidateInToDemand(int a, int b) {
    return true;
  }
}
