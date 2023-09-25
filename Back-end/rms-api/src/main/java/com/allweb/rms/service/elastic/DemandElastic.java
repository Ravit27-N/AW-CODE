package com.allweb.rms.service.elastic;

import com.allweb.rms.entity.dto.CandidateDTO;
import com.allweb.rms.entity.dto.DemandDTO;
import com.allweb.rms.entity.elastic.*;
import com.allweb.rms.entity.jpa.Demand;
import com.allweb.rms.exception.*;
import com.allweb.rms.repository.elastic.CandidateElasticsearchRepository;
import com.allweb.rms.repository.elastic.DemandElasticRepository;
import com.allweb.rms.repository.elastic.JobDescriptionElasticRepository;
import com.allweb.rms.repository.elastic.ProjectElasticRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DemandElastic {
    private final DemandElasticRepository demandElasticRepository;
    private final ProjectElasticRepository projectElasticRepository;
    private final CandidateElasticsearchRepository candidateElasticsearchRepository;
    private final JobDescriptionElasticRepository jobDescriptionElasticRepository;
    private final ModelMapper modelMapper;
    private static final String ID_NOT_FOUND = "Demand was not founded by ID:";
    private final ObjectMapper objectMapper;

    public void saveDemand(final DemandDTO demandDTO, int id) {
        log.info("Elastic was saved {}", demandDTO.toString());
        DemandElasticsearchDocument demand = dtoToEntity(demandDTO);
        demand.setId(id);
        demandElasticRepository.save(demand);
    }

    public void updateActiveDemand(int id, boolean active) {
        log.info("DemandElastic:updateActiveDemand execution started");
        DemandElasticsearchDocument demand = demandElasticRepository.findById(id)
                .orElseThrow(() -> new DemandNotFoundException(ID_NOT_FOUND + " " + id));
        log.debug("DemandElastic:updateActiveDemand {}", demand);
        demand.setId(id);
        demand.setActive(active);
        demandElasticRepository.save(demand);
        log.info("DemandElastic:updateActiveDemand execution end");
    }

    public void softDeleteDemand(int id, boolean isDelete) {
        DemandElasticsearchDocument demand = demandElasticRepository.findById(id)
                .orElseThrow(() -> new DemandNotFoundException(ID_NOT_FOUND + " " + id));
        demand.setId(id);
        demand.setDeleted(isDelete);
        demandElasticRepository.save(demand);
    }

    //todo: error project id and description id
    public void updateDemand(DemandDTO demandDTO) {
        try {
            DemandElasticsearchDocument demand = demandElasticRepository.findById(demandDTO.getId())
                    .orElseThrow(() -> new DemandNotFoundException(ID_NOT_FOUND + " " + demandDTO.getId()));
            // log.info("------------data------------" + validateUpdateProjectJob(demandDTO.getProjectId(), demandDTO.getJobDescriptionId(), demandDTO.getId()));
            if (validateUpdateProjectJob(demandDTO.getProjectId(), demandDTO.getJobDescriptionId(), demandDTO.getId())> 0) {
                throw new DemandNotFoundException("You cannot update. ProjectId and Job DescriptionId are exist");
            }

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
            boolean demandStatus = demand.getNbCandidates().equals("")|| demand.getNbCandidates().split(",").length != demand.getNbRequired();
            demand.setStatus(demandStatus);
            demandElasticRepository.save(demand);
        }catch (IllegalArgumentException | OptimisticLockingFailureException e ){
            log.error(e.getClass() + ":updateDemand error occurred" + e.getMessage());
        }
    }

    public void hardDeleteDemand(int id) {
        demandElasticRepository.deleteById(id);
    }

    public int validateProjectJob(int projectId, int jobDescriptionId) {
        return demandElasticRepository.countDemandByProjectIdAndJobDescriptionId(projectId, jobDescriptionId);
    }

    public int validateUpdateProjectJob(int projectId,int jobDescriptionId,int demandId){
        return this.demandElasticRepository.countDemandByProjectIdAndJobDescriptionIdAndId(projectId, jobDescriptionId, demandId);
    }

    private DemandElasticsearchDocument dtoToEntity(DemandDTO demandDTO) {
        return this.modelMapper.map(demandDTO, DemandElasticsearchDocument.class);
    }

    public DemandDTO getDemandById(int id){
        DemandDTO demandDTO = convertMapToDTO(demandElasticRepository.findDemandById(id));
        if (demandDTO.getId() == 0) {
            throw new DemandNotFoundException(ID_NOT_FOUND + " " + id);
        }
        Optional<DemandElasticsearchDocument> allCandidateId = demandElasticRepository.findById(id);
        String candidate = allCandidateId.get().getNbCandidates();
        String[] count = candidate.split(",");
        int candidateId = 0;
        List<Candidate> list = new ArrayList<>();
        if (candidate.length() > 0) {
            for (String c : count) {
                candidateId = Integer.parseInt(c);
                Optional<CandidateElasticsearchDocument> candidateElasticsearchDocument  = candidateElasticsearchRepository.findById(candidateId);
                ObjectMapper mapObject = new ObjectMapper();
                Map<String, Object> candidateElasticsearchDocumentToObj = mapObject.convertValue(candidateElasticsearchDocument, Map.class);
                CandidateDTO candidateConvert = convertCandidateMapToDTO(candidateElasticsearchDocumentToObj);
                Candidate can = dtoCandidateToEntity(candidateConvert);
                list.add(can);
            }
            JsonNode jsonNode = objectMapper.valueToTree(list);
            demandDTO.setCandidate(jsonNode);
        }
        return demandDTO;
    }
    public void saveCandidateInToDemand(int demandId, String candidateIds) {
        DemandElasticsearchDocument demand = demandElasticRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFoundException(ID_NOT_FOUND + " " + demandId));
        String oldCandidate = demand.getNbCandidates();
        String countCandidate;
        boolean status = demand.isStatus();
        if (!status) {
            throw new DemandNotFoundException("Cannot add candidate more. We are closed this demand.");
        }
        if (candidateIds.equals("")) {
            throw new DemandNotFoundException("When add candidate to demand. candidateId cannot empty.");
        }
        ArrayList<String> candidateId = new ArrayList<>();
        List<DemandElasticsearchDocument> allCandidateIds = (List<DemandElasticsearchDocument>) demandElasticRepository.findAll();
        for(DemandElasticsearchDocument allCandidateId : allCandidateIds){
            candidateId.add(allCandidateId.getNbCandidates());
        }

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
        demandElasticRepository.save(demand);
    }
    public Candidate dtoCandidateToEntity(CandidateDTO candidateDTO) {
        return modelMapper.map(candidateDTO, Candidate.class);
    }
    public void deleteCandidateFromDemand(int demandId, int candidateId) {
        DemandElasticsearchDocument demand = demandElasticRepository.findById(demandId)
                .orElseThrow(() -> new DemandNotFoundException(ID_NOT_FOUND + " " + demandId));
        Optional<DemandElasticsearchDocument> candidate = demandElasticRepository.findById(demandId);
        String allCandidateId = candidate.get().getNbCandidates();
        if (allCandidateId.equals("")) {
            throw new CandidateIdNullDataException();
        }
        String[] count = allCandidateId.split(",");
        Optional<DemandElasticsearchDocument> allNbRequired = demandElasticRepository.findById(demandId);
        int nbRequired = allNbRequired.get().getNbRequired();
        int i = 0;
        StringBuilder newCandidate = new StringBuilder();
        for (String c : count) {
            i++;
            if (Integer.parseInt(c) == candidateId) {
                log.info("can not parse the data to interger");
            } else {
                newCandidate.append(c).append(",");
            }
        }
        if (nbRequired == i) {
            demand.setStatus(true);
        }
        if (candidateId < 0) {
            throw new CandidateIdLessThanOException();
        }
        demand.setNbCandidates(newCandidate.toString());
        demandElasticRepository.save(demand);
    }

    private DemandDTO convertMapToDTO(Map<String, Object> demandMap) {
        return modelUnderScoreNamingMapper().map(demandMap, DemandDTO.class);
    }

    private ModelMapper modelUnderScoreNamingMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper
                .getConfiguration()
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
                .setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE)
                .setAmbiguityIgnored(true);
        return mapper;
    }

    private CandidateDTO convertCandidateMapToDTO(Map<String, Object> candidateMap) {
        return modelUnderScoreNamingMapper().map(candidateMap, CandidateDTO.class);
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

}

/*
  private String findAllNbCandidate() {
    NativeQuery srcQuery = new NativeQueryBuilder().withFields("nbCandidates").build();
    String demandSearchHits = elasticsearchOperations.search(srcQuery, Demand.class, IndexCoordinates.of(DEMAND_INDEX));
    return demandSearchHits;
  }

  private DemandDTO entityToDTO(Demand demand) {
    DemandDTO model = modelMapper.map(demand, DemandDTO.class);
    model.setJobDescriptionId(demand.getJobDescriptionId());
    model.setProjectId(demand.getProjectId());
    return model;
  }
 */
