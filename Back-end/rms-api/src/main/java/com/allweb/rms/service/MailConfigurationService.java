package com.allweb.rms.service;

import com.allweb.rms.component.MailConfigurationModelAssembler;
import com.allweb.rms.entity.dto.MailConfigurationDTO;
import com.allweb.rms.entity.jpa.CandidateStatus;
import com.allweb.rms.entity.jpa.MailConfiguration;
import com.allweb.rms.entity.jpa.MailTemplate;
import com.allweb.rms.exception.CandidateStatusNotFoundException;
import com.allweb.rms.exception.MailConfigurationNotFoundException;
import com.allweb.rms.exception.MailTemplateNotFoundException;
import com.allweb.rms.exception.RelationDatabaseException;
import com.allweb.rms.repository.elastic.impl.MailConfigurationRepositoryImpl;
import com.allweb.rms.repository.jpa.CandidateStatusRepository;
import com.allweb.rms.repository.jpa.MailConfigurationRepository;
import com.allweb.rms.repository.jpa.MailTemplateRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import java.util.List;
import java.util.Optional;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@CacheConfig(cacheNames = "mailConfigurationCaching")
public class MailConfigurationService {
  private static final String FORMAT_SMG = "MailConfiguration id %s not found";
  private final MailConfigurationRepository mailConfigurationRepository;
  private final CandidateStatusRepository candidateStatusRepository;
  private final MailTemplateRepository mailTemplateRepository;
  private final MailConfigurationRepositoryImpl mailConfigurationImpl;
  private final ModelMapper modelMapper;
  private final MailConfigurationModelAssembler assembler;

  @Autowired
  public MailConfigurationService(
      MailConfigurationRepository mailConfigurationRepository,
      CandidateStatusRepository candidateStatusRepository,
      MailTemplateRepository mailTemplateRepository,
      MailConfigurationRepositoryImpl mailConfigurationImpl,
      ModelMapper modelMapper,
      MailConfigurationModelAssembler assembler) {
    this.mailConfigurationRepository = mailConfigurationRepository;
    this.candidateStatusRepository = candidateStatusRepository;
    this.mailTemplateRepository = mailTemplateRepository;
    this.mailConfigurationImpl = mailConfigurationImpl;
    this.modelMapper = modelMapper;
    this.assembler = assembler;
    this.modelMapper
        .getConfiguration()
        .setAmbiguityIgnored(true)
        .setMatchingStrategy(MatchingStrategies.STRICT);
  }

  private static void isValidEmail(List<String> email) throws AddressException {
    try {
      for (String s : email) {
        InternetAddress internetAddress = new InternetAddress(s);
        internetAddress.validate();
      }
    } catch (AddressException e) {
      log.error("Invalid Email : ", e);
      throw e;
    }
  }

  /**
   * Method use retrieve mailTemplate info by its id
   *
   * @param id of mailTemplate
   * @return mailTemplate properties
   */
  @Transactional(readOnly = true)
  public MailConfigurationDTO getMailConfigurationById(int id) {
    return convertEntityToDTO(
        mailConfigurationRepository
            .findByIdAndDeletedIsFalse(id)
            .orElseThrow(() -> new MailTemplateNotFoundException(String.format(FORMAT_SMG, id))));
  }

  public MailConfigurationDTO getMailConfigurationByCandidateStatus(int id) {
    return convertEntityToDTO(
        mailConfigurationRepository
            .findByCandidateStatusIdAndDeletedIsFalse(id)
            .orElseThrow(
                () ->
                    new MailConfigurationNotFoundException(
                        String.format("CandidateStatus id %s not found", id))));
  }

  /**
   * Method use to get pagination result
   *
   * @param filter is value of string that u want to get it from activity
   * @param size is number of page
   * @param page is number of page index
   * @param sortByField is field of Activity
   * @param sortDirection is direction sort ex(ASC,DESC)
   * @return {@link MailConfiguration} Result Page
   */
  @CacheEvict(value = "mailConfigurationCaching", allEntries = true)
  @Transactional(readOnly = true)
  public EntityResponseHandler<EntityModel<MailConfigurationDTO>> getMailConfigurations(
      int page,
      int size,
      String filter,
      String sortDirection,
      String sortByField,
      String selectType) {
    Pageable pageable =
        PageRequest.of(
            page - 1, size, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    return new EntityResponseHandler<>(
        mailConfigurationImpl
            .advanceFilters(pageable, filter, selectType)
            .map(this::convertToEntityModel));
  }

  /**
   * Method use to perform save of MailConfiguration
   *
   * @param mailConfigurationDTO is object of MailConfiguration
   * @return {@link MailConfigurationDTO} object result
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public MailConfigurationDTO save(MailConfigurationDTO mailConfigurationDTO)
      throws AddressException {
    MailConfiguration mailConfiguration;
    MailTemplate mailTemplate;
    CandidateStatus candidateStatus;
    isValidEmail(mailConfigurationDTO.getTo());
    isValidEmail(mailConfigurationDTO.getCc());
    if (mailConfigurationRepository
        .findByCandidateStatusId(mailConfigurationDTO.getCandidateStatusId())
        .isPresent())
      throw new RelationDatabaseException(
          "Candidate status id "
              + mailConfigurationDTO.getCandidateStatusId()
              + " is already in use !");
    mailConfiguration = convertToEntity(mailConfigurationDTO);
    if (mailConfigurationDTO.getMailTemplateId() != 0) {
      mailTemplate =
          mailTemplateRepository
              .findByIdAndDeletedIsFalse(mailConfigurationDTO.getMailTemplateId())
              .orElseThrow(
                  () ->
                      new MailTemplateNotFoundException(
                          "Mail Template id "
                              + mailConfigurationDTO.getMailTemplateId()
                              + " not found"));
      mailConfiguration.setMailTemplate(mailTemplate);
    }
    if (mailConfigurationDTO.getCandidateStatusId() != 0) {
      candidateStatus =
          candidateStatusRepository
              .findByIdAndActiveIsTrueAndDeletedIsFalse(mailConfigurationDTO.getCandidateStatusId())
              .orElseThrow(
                  () ->
                      new CandidateStatusNotFoundException(
                          mailConfigurationDTO.getCandidateStatusId()));
      mailConfiguration.setCandidateStatus(candidateStatus);
    }
    return convertEntityToDTO(mailConfigurationRepository.save(mailConfiguration));
  }

  /**
   * Method use to perform update of MailConfiguration
   *
   * @param mailConfigurationDTO is object of MailConfiguration
   * @return {@link MailConfigurationDTO} object result
   */
  @CacheEvict(allEntries = true)
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public MailConfigurationDTO update(MailConfigurationDTO mailConfigurationDTO)
      throws AddressException {
    MailConfiguration mailConfiguration;
    MailTemplate mailTemplate;
    CandidateStatus candidateStatus;
    int id = 0;
    isValidEmail(mailConfigurationDTO.getTo());
    isValidEmail(mailConfigurationDTO.getCc());
    Optional<MailConfiguration> byIdAndDeletedIsFalse =
        mailConfigurationRepository.findById(mailConfigurationDTO.getId());
    if (byIdAndDeletedIsFalse.isPresent()) {
      mailConfiguration = byIdAndDeletedIsFalse.get();
      mailConfiguration.setTitle(mailConfigurationDTO.getTitle());
      mailConfiguration.setFrom(mailConfigurationDTO.getFrom());
      mailConfiguration.setTo(mailConfigurationDTO.getTo());
      mailConfiguration.setCc(mailConfigurationDTO.getCc());
      mailConfiguration.setActive(mailConfigurationDTO.isActive());
    } else
      throw new MailConfigurationNotFoundException(
          String.format(FORMAT_SMG, mailConfigurationDTO.getId()));

    if (mailConfigurationDTO.getMailTemplateId() != mailConfiguration.getMailTemplate().getId()) {
      mailTemplate =
          mailTemplateRepository
              .findByIdAndDeletedIsFalse(mailConfigurationDTO.getMailTemplateId())
              .orElseThrow(
                  () ->
                      new MailTemplateNotFoundException(
                          "Mail Template id "
                              + mailConfigurationDTO.getMailTemplateId()
                              + " not found"));
      deleteMailConfiguration(mailConfiguration.getId());
      mailConfiguration.setMailTemplate(mailTemplate);
      id = mailConfigurationImpl.insertToMailConfigure(mailConfiguration);
    }
    if (mailConfigurationDTO.getCandidateStatusId()
        != mailConfiguration.getCandidateStatus().getId()) {
      candidateStatus =
          candidateStatusRepository
              .findByIdAndActiveIsTrueAndDeletedIsFalse(mailConfigurationDTO.getCandidateStatusId())
              .orElseThrow(
                  () ->
                      new CandidateStatusNotFoundException(
                          mailConfigurationDTO.getCandidateStatusId()));
      deleteMailConfiguration(mailConfiguration.getId());
      mailConfiguration.setCandidateStatus(candidateStatus);
      id = mailConfigurationImpl.insertToMailConfigure(mailConfiguration);
    }
    if (id != 0) {
      return convertEntityToDTO(mailConfiguration);
    } else {
      return convertEntityToDTO(mailConfigurationRepository.save(mailConfiguration));
    }
  }

  /**
   * Method use to update Active MailConfiguration
   *
   * @param id of mailTemplate
   * @param active boolean type true or false
   * @return void
   */
  @CacheEvict(allEntries = true)
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void updateActiveMailConfiguration(int id, boolean active) {
    MailConfiguration mailConfiguration =
        mailConfigurationRepository
            .findByIdAndDeletedIsFalse(id)
            .orElseThrow(() -> new MailTemplateNotFoundException(String.format(FORMAT_SMG, id)));
    mailConfiguration.setActive(active);
    mailConfigurationRepository.save(mailConfiguration);
  }

  /**
   * Method use to update Active MailConfiguration
   *
   * @param id of MailConfiguration
   * @param deleted boolean type true or false
   * @return void
   */
  @CacheEvict(allEntries = true)
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void updateDeleted(int id, boolean deleted) {
    MailConfiguration mailConfiguration =
        mailConfigurationRepository
            .findById(id)
            .orElseThrow(() -> new MailTemplateNotFoundException(String.format(FORMAT_SMG, id)));
    mailConfiguration.setDeleted(deleted);
    mailConfigurationRepository.save(mailConfiguration);
  }

  /**
   * Method use to delete MailConfiguration
   *
   * @param id of MailConfiguration
   * @return void
   */
  @CacheEvict(allEntries = true)
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void deleteMailConfiguration(int id) {
    mailConfigurationRepository.delete(
        mailConfigurationRepository
            .findById(id)
            .orElseThrow(() -> new MailTemplateNotFoundException(String.format(FORMAT_SMG, id))));
  }

  /**
   * Method use to convert MailConfiguration to DTO
   *
   * @param mailConfiguration of MailConfiguration
   * @return {@link MailConfigurationDTO}
   */
  public MailConfigurationDTO convertEntityToDTO(MailConfiguration mailConfiguration) {
    return modelMapper.map(mailConfiguration, MailConfigurationDTO.class);
  }

  /**
   * Method use to convert MailConfigurationDTO to entity
   *
   * @param mailConfigurationDTO of MailConfigurationDTO
   * @return {@link MailConfiguration}
   */
  public MailConfiguration convertToEntity(MailConfigurationDTO mailConfigurationDTO) {
    return modelMapper.map(mailConfigurationDTO, MailConfiguration.class);
  }

  /**
   * Method use to convert MailConfigurationDTO to entity
   *
   * @param mailConfiguration of MailConfigurationDTO
   * @return MailConfigurationEntityModel
   */
  public EntityModel<MailConfigurationDTO> convertToEntityModel(
      MailConfiguration mailConfiguration) {
    return assembler.toModel(modelMapper.map(mailConfiguration, MailConfigurationDTO.class));
  }
}
