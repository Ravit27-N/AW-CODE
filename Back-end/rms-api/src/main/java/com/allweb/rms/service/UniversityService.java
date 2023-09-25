package com.allweb.rms.service;

import com.allweb.rms.component.UniversityModelAssembler;
import com.allweb.rms.entity.dto.UniversityDTO;
import com.allweb.rms.entity.jpa.University;
import com.allweb.rms.exception.RelationDatabaseException;
import com.allweb.rms.exception.UniversityNameConflictException;
import com.allweb.rms.exception.UniversityNotFoundException;
import com.allweb.rms.repository.jpa.CandidateUniversityRepository;
import com.allweb.rms.repository.jpa.UniversityRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.rms.utils.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UniversityService {

  private final UniversityRepository universityRepository;
  private final ModelMapper modelMapper;
  private final UniversityModelAssembler assembler;
  private final CandidateUniversityRepository candidateUniversityRepository;

  @Autowired
  public UniversityService(
      UniversityRepository universityRepository,
      ModelMapper modelMapper,
      UniversityModelAssembler assembler,
      CandidateUniversityRepository candidateUniversityRepository) {
    this.universityRepository = universityRepository;
    this.modelMapper = modelMapper;
    this.assembler = assembler;
    this.candidateUniversityRepository = candidateUniversityRepository;
  }

  // convert dto to entity
  public University convertToEntity(UniversityDTO universityDTO) {
    return modelMapper.map(universityDTO, University.class);
  }

  // convert entity to dto
  public UniversityDTO convertToDTO(University university) {
    return modelMapper.map(university, UniversityDTO.class);
  }

  // convert to model entity
  public EntityModel<UniversityDTO> convertToModelEntity(University university) {
    return assembler.toModel(convertToDTO(university));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public UniversityDTO createUniversity(UniversityDTO universityDTO) {
    if (universityRepository.validateUniversity(universityDTO.getName()) != 0) {
      throw new UniversityNameConflictException("This name is already in using!");
    }
    return convertToDTO(universityRepository.save(convertToEntity(universityDTO)));
  }

  @Transactional
  public UniversityDTO getUniversityById(int id) {
    University university =
        universityRepository.findById(id).orElseThrow(() -> new UniversityNotFoundException(id));
    return convertToDTO(university);
  }

  @Transactional
  public EntityResponseHandler<EntityModel<UniversityDTO>> getAllUniversity(
      String filter, int page, int pageSize, String sortDirection, String sortByField) {
    filter = filter == null ? "" : filter.trim();
    int filterWordCount =
        org.apache.commons.lang3.StringUtils.isBlank(filter) ? 0 : filter.split(" ").length;
    sortByField = filterWordCount > 1 ? StringUtils.toSnakeCase(sortByField) : sortByField;
    Pageable pageable =
        PageRequest.of(
            page - 1,
            pageSize,
            filterWordCount == 0
                ? Sort.by(Sort.Direction.fromString(sortDirection), sortByField)
                : Sort.unsorted());
    Page<University> universities;
    if (filterWordCount == 0) {
      universities = this.universityRepository.findAll(pageable);
    } else if (filterWordCount == 1) {
      universities = this.universityRepository.findByNameStartingWithIgnoreCase(filter, pageable);
    } else {
      universities = this.universityRepository.findByRelevantName(filter, pageable);
    }
    return new EntityResponseHandler<>(universities.map(this::convertToModelEntity));
  }

  @Modifying
  public void deleteUniversityById(int id) {
    // must be throws exception when deleting a university
    if (candidateUniversityRepository.countCandidateByUniversityId(id) != 0) {
      throw new RelationDatabaseException("University id " + id + " is using with another!");
    }
    universityRepository.delete(
        universityRepository.findById(id).orElseThrow(() -> new UniversityNotFoundException(id)));
  }

  // update university
  public UniversityDTO updateUniversity(UniversityDTO universityDTO) {
    if (universityRepository.validateUniversityOnUpdate(
            universityDTO.getId(), universityDTO.getName())
        != 0) {
      throw new UniversityNameConflictException("This name is already in using!");
    }
    University university =
        universityRepository
            .findById(universityDTO.getId())
            .orElseThrow(() -> new UniversityNotFoundException(universityDTO.getId()));
    university.setName(universityDTO.getName());
    universityDTO.setCreatedAt(university.getCreatedAt());
    return convertToDTO(universityRepository.save(convertToEntity(universityDTO)));
  }
}
