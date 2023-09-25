package com.allweb.rms.service;

import com.allweb.rms.component.MailTemplateModelAssembler;
import com.allweb.rms.entity.dto.MailTemplateDTO;
import com.allweb.rms.entity.jpa.MailTemplate;
import com.allweb.rms.exception.MailTemplateNotFoundException;
import com.allweb.rms.repository.jpa.MailTemplateRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@Service
@Transactional
@CacheConfig(cacheNames = {"mailTemplateCaching"})
public class MailTemplateService {
  private static final String MSG_FORMAT = "MailTemplate id %s not found";
  private final MailTemplateRepository mailTemplateRepository;
  private final ModelMapper modelMapper;
  private final MailTemplateModelAssembler assembler;

  @Autowired
  public MailTemplateService(
      MailTemplateRepository mailTemplateRepository,
      ModelMapper modelMapper,
      MailTemplateModelAssembler assembler) {
    this.mailTemplateRepository = mailTemplateRepository;
    this.modelMapper = modelMapper;
    this.assembler = assembler;
  }

  /**
   * Method use retrieve mailTemplate info by its id
   *
   * @param id of mailTemplate
   * @return mailTemplate properties
   */
  @Transactional(readOnly = true)
  public MailTemplateDTO getMailTemplateById(int id) {
    return convertToMailTemplateDTO(
        mailTemplateRepository
            .findByIdAndDeletedIsFalse(id)
            .orElseThrow(() -> new MailTemplateNotFoundException(String.format(MSG_FORMAT, id))));
  }

  @Transactional(readOnly = true)
  public MailTemplateDTO getMailTemplateByType(String typeName) {
    return mailTemplateRepository
        .findBySubjectAndDeletedIsFalse(typeName)
        .orElseThrow(() -> new MailTemplateNotFoundException(String.format(MSG_FORMAT, typeName)));
  }

  /**
   * Method use to get pagination result
   *
   * @param filter is value of string that u want to get it from activity
   * @param size is number of page
   * @param page is number of page index
   * @param sortByField is field of Activity
   * @param sortDirection is direction sort ex(ASC,DESC)
   * @return MailTemplate Result Page
   */
  @Transactional(readOnly = true)
  public EntityResponseHandler<EntityModel<MailTemplateDTO>> getMailTemplates(
      int page,
      int size,
      String filter,
      String sortDirection,
      String sortByField,
      String selectType) {

    Pageable pageable =
        PageRequest.of(
            page - 1, size, Sort.by(Sort.Direction.fromString(sortDirection), sortByField));
    Page<MailTemplateDTO> mailTemplateDTOPage;
    switch (selectType) {
      case "inactive":
        if (emptyString(filter)) {
          mailTemplateDTOPage =
              mailTemplateRepository
                  .findAllByActiveIsFalseAndDeletedIsFalse(pageable)
                  .map(this::convertToMailTemplateDTO);
        } else {
          mailTemplateDTOPage =
              mailTemplateRepository.findAllByActiveIsFalseAndFilter(
                  filter.toLowerCase(), pageable);
        }
        break;
      case "active":
        if (emptyString(filter)) {
          mailTemplateDTOPage =
              mailTemplateRepository
                  .findAllByActiveIsTrueAndDeletedIsFalse(pageable)
                  .map(this::convertToMailTemplateDTO);
        } else {
          mailTemplateDTOPage =
              mailTemplateRepository.findAllByActiveIsTrueWithFilter(
                  filter.toLowerCase(), pageable);
        }
        break;
      case "deleted":
        if (emptyString(filter)) {
          mailTemplateDTOPage =
              mailTemplateRepository
                  .findAllByDeletedIsTrue(pageable)
                  .map(this::convertToMailTemplateDTO);
        } else {
          mailTemplateDTOPage =
              mailTemplateRepository.findAllByDeletedIsTrueWithFilter(
                  filter.toLowerCase(), pageable);
        }
        break;
      default:
        if (emptyString(filter)) {
          mailTemplateDTOPage =
              mailTemplateRepository.findAll(pageable).map(this::convertToMailTemplateDTO);
        } else {
          mailTemplateDTOPage =
              mailTemplateRepository.findAllBySubject(filter.toLowerCase(), pageable);
        }
    }
    return new EntityResponseHandler<>(mailTemplateDTOPage.map(assembler::toModel));
  }
  // check string is empty or not
  private boolean emptyString(String value) {
    return Strings.isNullOrEmpty(value);
  }
  /**
   * Method use to perform save MailTemplate
   *
   * @param mailTemplate is object of MailTemplate
   * @return MailTemplate object result
   */
  @Transactional
  public MailTemplateDTO saveMailTemplate(MailTemplateDTO mailTemplate) {
    return convertToMailTemplateDTO(save(convertToMailTemplateEntity(mailTemplate)));
  }

  /**
   * Method use to perform save MailTemplate
   *
   * @param mailTemplate is object of MailTemplate
   * @return MailTemplate object result
   */
  @Transactional
  @CacheEvict(allEntries = true)
  public MailTemplateDTO updateMailTemplate(MailTemplateDTO mailTemplate) {
    getMailTemplateById(mailTemplate.getId());
    return convertToMailTemplateDTO(save(convertToMailTemplateEntity(mailTemplate)));
  }

  /**
   * Method use to perform save and update of MailTemplate
   *
   * @param mailTemplate is object of MailTemplate
   * @return MailTemplate object result
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public MailTemplate save(MailTemplate mailTemplate) {
    return mailTemplateRepository.save(mailTemplate);
  }

  /**
   * Method use to update Active MailTemplate
   *
   * @param id of mailTemplate
   * @param active boolean type true or false
   * @return void
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  @CacheEvict(allEntries = true)
  public void updateActiveMailTemplate(int id, boolean active) {
    MailTemplate mailTemplateById = convertToMailTemplateEntity(getMailTemplateById(id));
    mailTemplateById.setActive(active);
    save(mailTemplateById);
  }

  /**
   * Method use to update deleted
   *
   * @param id of mailTemplate
   * @param deleted boolean type true or false
   * @return void
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  @CacheEvict(allEntries = true)
  public void updateDeleted(int id, boolean deleted) {
    MailTemplate mailTemplate =
        mailTemplateRepository
            .findById(id)
            .orElseThrow(() -> new MailTemplateNotFoundException(String.format(MSG_FORMAT, id)));
    mailTemplate.setDeleted(deleted);
    save(mailTemplate);
  }

  /**
   * Method use to update IsAbleDelete
   *
   * @param id of mailTemplate
   * @param isAbleDelete boolean type true or false
   * @return void
   */
  @CacheEvict(allEntries = true)
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void updateIsAbleDelete(int id, boolean isAbleDelete) {
    log.info(id + "  " + isAbleDelete);
    MailTemplate mailTemplateById = convertToMailTemplateEntity(getMailTemplateById(id));
    mailTemplateById.setDeletable(isAbleDelete);
    save(mailTemplateById);
  }

  /**
   * Method use to delete mailTemplate
   *
   * @param id of mailTemplate
   * @return void
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  @CacheEvict(allEntries = true)
  public void deleteMailTemplate(int id) {
    List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
    if (mailTemplateRepository.findById(id).isPresent()) {
      if (mailTemplateRepository.deleteByIdAndIdNotIn(id, ints) == 0)
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN, String.format("Can not delete Default Setting of id %s", id));
    } else throw new MailTemplateNotFoundException(String.format(MSG_FORMAT, id));
  }

  /**
   * Method use to covert entity to DTO
   *
   * @param mailTemplate object
   * @return MailTemplateDTO
   */
  public MailTemplateDTO convertToMailTemplateDTO(MailTemplate mailTemplate) {
    return modelMapper.map(mailTemplate, MailTemplateDTO.class);
  }

  /**
   * Method use to covert DTO to entity
   *
   * @param mailTemplateDTO object
   * @return MailTemplate
   */
  public MailTemplate convertToMailTemplateEntity(MailTemplateDTO mailTemplateDTO) {
    return modelMapper.map(mailTemplateDTO, MailTemplate.class);
  }
}
