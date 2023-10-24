package com.ravit.java.service;

import com.ravit.java.model.Candidate;
import com.ravit.java.model.dto.CandidateDto;
import com.ravit.java.repository.CandidateRepository;
import com.ravit.java.service.specification.CandidateSpecification;
import com.ravit.java.share.service.AbstractCrudService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Transactional
public class CandidateService extends AbstractCrudService<CandidateDto, Candidate, Long> {

  private final String NOT_FOUND = "Candidate not found";
  private final CandidateRepository candidateRepository;


  @Autowired
  protected CandidateService(ModelMapper modelMapper, CandidateRepository candidateRepository) {
    super(modelMapper);
    this.candidateRepository = candidateRepository;
  }


  protected Candidate findEntityById(long id) {
    return candidateRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
  }

  /**
   * Find CompanyDetailDTO by its id.
   *
   * @param id refers to companyDetail's id that client wants to see.
   * @return CompanyDetailDTO
   */
  @Override
  @Transactional(readOnly = true)
  public CandidateDto findById(Long id) {
    return mapData(this.findEntityById(id));
  }

  /**
   * Find CompanyDetailDTO by company's id.
   *
   * @param candidateId refers to the company's id that client wants to see.
   * @return CompanyDetailDTO
   */
  @Transactional(readOnly = true)
  public CandidateDto findByCompanyId(Long candidateId) {
    Candidate companyDetail =
        this.candidateRepository
            .findAll(
                (Sort) Specification.where(CandidateSpecification.findByCandidateId(candidateId)))
            .stream()
            .min(Comparator.comparingLong(Candidate::getId))
            .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
    return mapData(companyDetail);
  }

  /**
   * List all company details.
   *
   * @return a list of CompanyDetailDTO
   */
  @Override
  @Transactional(readOnly = true)
  public List<CandidateDto> findAll() {
    return this.mapAll(this.candidateRepository.findAll(), CandidateDto.class);
  }

  /**
   * List all companyDetailDTO in pagination
   *
   * @param pageable refers to parameter that uses to returns a page object.
   * @return a page of companyDetailDTO.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<CandidateDto> findAll(Pageable pageable) {
    if (pageable.isUnpaged()) {
      return new PageImpl<>(this.findAll());
    }
    return candidateRepository.findAll(pageable).map(super::mapData);
  }

  /**
   * save candidate
   *
   * @param candidateDto
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public CandidateDto save(CandidateDto candidateDto) {
    Candidate candidate = modelMapper.map(candidateDto, Candidate.class);
    return mapData(this.candidateRepository.save(candidate));
  }


  @Override
  @Transactional(rollbackFor = Exception.class)
  public CandidateDto update(CandidateDto candidateDto) {
    // find out the id is passed or not
    var candidate = this.findEntityById(candidateDto.getId());
    if (!ObjectUtils.isEmpty(candidate)) {
      this.mapEntity(candidateDto, candidate);
      return this.mapData(this.candidateRepository.save(candidate));
    }
    return this.mapData(this.candidateRepository.save(this.mapEntity(candidateDto)));
  }


}
