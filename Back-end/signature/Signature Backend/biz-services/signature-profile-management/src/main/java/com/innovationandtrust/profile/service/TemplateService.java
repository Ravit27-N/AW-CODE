package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.constant.TemplateEnum;
import com.innovationandtrust.profile.constant.TemplateType;
import com.innovationandtrust.profile.exception.InvalidTemplateException;
import com.innovationandtrust.profile.exception.TemplateNotFoundException;
import com.innovationandtrust.profile.exception.UserNotFoundException;
import com.innovationandtrust.profile.model.dto.TemplateDto;
import com.innovationandtrust.profile.model.dto.TemplateFolder;
import com.innovationandtrust.profile.model.dto.UserParticipantDto;
import com.innovationandtrust.profile.model.entity.Template;
import com.innovationandtrust.profile.model.entity.TemplateMessage;
import com.innovationandtrust.profile.model.entity.Template_;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.UserTemplateSetting;
import com.innovationandtrust.profile.model.entity.UserTemplateSetting_;
import com.innovationandtrust.profile.model.entity.UserTemplates;
import com.innovationandtrust.profile.repository.TemplateRepository;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.profile.repository.UserTemplateSettingRepository;
import com.innovationandtrust.profile.repository.UserTemplatesRepository;
import com.innovationandtrust.profile.service.restclient.BusinessUnitsFeignClient;
import com.innovationandtrust.profile.service.spefication.UserTemplatesSpec;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.corporateprofile.BusinessUnitDTO;
import com.innovationandtrust.share.model.corporateprofile.FolderDTO;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.exception.exceptions.AccessDeniedException;
import com.innovationandtrust.utils.exception.exceptions.BusinessUnitNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.FolderNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.MissingParamException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional
public class TemplateService extends CommonCrudService<TemplateDto, Template, Long> {
  private final TemplateRepository templateRepository;
  private final UserRepository userRepository;
  private final UserTemplatesRepository userTemplatesRepository;
  private final BusinessUnitsFeignClient businessUnitsFeignClient;
  private final UserParticipantService userParticipantsService;
  private final UserTemplateSettingRepository userTemplateSettingRepository;

  protected TemplateService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      TemplateRepository templateRepository,
      UserRepository userRepository,
      UserTemplatesRepository userTemplatesRepository,
      BusinessUnitsFeignClient businessUnitsFeignClient,
      UserParticipantService userParticipantsService,
      UserTemplateSettingRepository userTemplateSettingRepository) {
    super(modelMapper, keycloakProvider);
    this.templateRepository = templateRepository;
    this.userRepository = userRepository;
    this.userTemplatesRepository = userTemplatesRepository;
    this.businessUnitsFeignClient = businessUnitsFeignClient;
    this.userParticipantsService = userParticipantsService;
    this.userTemplateSettingRepository = userTemplateSettingRepository;
  }

  /**
   * Retrieved folder of templates by companyId and template's name.
   *
   * @param filter refers to the given template name
   * @return a TemplateFolder
   */
  @Transactional(readOnly = true)
  public List<TemplateFolder> findAll(String filter) {

    List<TemplateFolder> templateFolders = new ArrayList<>();
    var foundUser =
        this.userRepository.findById(this.getUserId()).orElseThrow(UserNotFoundException::new);
    var corporateAdmin =
        this.userRepository
            .findById(foundUser.getCreatedBy())
            .orElseThrow(UserNotFoundException::new);

    var corporateTemplates =
        getTemplatesByUser(
            corporateAdmin, userTemplatesSpecification(corporateAdmin.getId(), filter));

    List<TemplateDto> allTemplates = new ArrayList<>(corporateTemplates);
    var userTemplates =
        getTemplatesByUser(foundUser, userTemplatesSpecification(this.getUserId(), filter));
    allTemplates.addAll(userTemplates);

    log.info("Getting corporate folder and user folders");
    List<FolderDTO> folders =
        this.businessUnitsFeignClient.getFoldersByUsersId(
            List.of(corporateAdmin.getId(), foundUser.getId()));

    var favoriteTemplates = this.getUserFavoriteTemplates(Pageable.unpaged(), filter).getContents();

    favoriteTemplates.forEach(
        templateDto -> {
          folders.stream()
              .filter(folderDto -> Objects.equals(folderDto.getId(), templateDto.getFolderId()))
              .findFirst()
              .ifPresent(folderDto -> templateDto.setFolderName(folderDto.getUnitName()));

          var creator =
              Objects.equals(templateDto.getCreatedBy(), foundUser.getId())
                  ? foundUser
                  : corporateAdmin;
          this.setCreatedByFullName(creator, templateDto);
        });

    // About adding users favorite templates
    templateFolders.add(
        TemplateFolder.builder()
            .unitName(TemplateEnum.FAVORITE.name())
            .templates(favoriteTemplates)
            .countTemplates(favoriteTemplates.size())
            .build());

    // There must be no templates without folders
    folders.forEach(
        folder -> {
          var templateList =
              allTemplates.stream()
                  .map(
                      templateDTO -> {
                        if (Objects.equals(templateDTO.getFolderId(), folder.getId())) {
                          templateDTO.setFolderName(folder.getUnitName());
                          return templateDTO;
                        }
                        return null;
                      })
                  .filter(Objects::nonNull)
                  .toList();

          var templateFolder =
              TemplateFolder.builder()
                  .id(folder.getId())
                  .unitName(folder.getUnitName())
                  .createdBy(folder.getCreatedBy())
                  .templates(templateList)
                  .countTemplates(templateList.size())
                  .businessUnitId(folder.getBusinessUnitId())
                  .build();
          templateFolders.add(templateFolder);
        });

    return templateFolders;
  }

  public List<TemplateDto> findByCorporate() {
    var corporateAdmin =
        this.userRepository
            .findById(
                this.userRepository
                    .findById(getUserId())
                    .orElseThrow(UserNotFoundException::new)
                    .getCreatedBy())
            .orElseThrow(UserNotFoundException::new);
    return getTemplatesByUser(
        corporateAdmin,
        Specification.where(UserTemplatesSpec.findByUserId(corporateAdmin.getId())));
  }

  private Specification<UserTemplates> userTemplatesSpecification(
      long userId, String templateName) {
    return !templateName.isEmpty()
        ? Specification.where(
            UserTemplatesSpec.findByUserId(userId)
                .and(UserTemplatesSpec.findByTemplateName(templateName)))
        : Specification.where(UserTemplatesSpec.findByUserId(userId));
  }

  private List<TemplateDto> getTemplatesByUser(
      User user, Specification<UserTemplates> userTemplatesSpecification) {
    var userTemplates = this.userTemplatesRepository.findAll(userTemplatesSpecification);

    var templatesList =
        userTemplates.stream().map(UserTemplates::getTemplate).map(this::mapData).toList();

    var templateIds = templatesList.stream().map(TemplateDto::getId).toList();
    var participantList = this.userParticipantsService.getByTemplateIds(templateIds);

    var userTemplateSettings =
        this.userTemplateSettingRepository.findAll(
            AdvancedFilter.searchByField(
                Filter.builder()
                    .referenceField(Arrays.asList(UserTemplateSetting_.TEMPLATE, Template_.ID))
                    .operator(QueryOperator.IN)
                    .values(templateIds.stream().map(String::valueOf).toList())
                    .build()));

    templatesList.forEach(
        templateDto -> {
          var participants =
              participantList.stream()
                  .filter(
                      participant ->
                          Objects.equals(participant.getTemplateId(), templateDto.getId()))
                  .toList();

          userTemplateSettings.stream()
              .filter(
                  userTemplateSetting ->
                      Objects.equals(
                          userTemplateSetting.getTemplate().getId(), templateDto.getId()))
              .findFirst()
              .ifPresent(
                  userTemplateSetting -> {
                    templateDto.setFavorite(userTemplateSetting.isFavorite());
                    templateDto.setUsedCount(userTemplateSetting.getUsedCount());
                  });

          templateDto.setParticipants(participants);
          this.setCreatedByFullName(user, templateDto);
        });

    return templatesList;
  }

  private void setCreatedByFullName(User user, TemplateDto templateDto) {
    templateDto.setCreatedByFullName(
        String.format("%s %s", user.getFirstName(), user.getLastName()));
  }

  /**
   * Retrieved templateDTO by its id.
   *
   * @param id refers to the given template id
   * @return a TemplateDto
   */
  @Override
  @Transactional(readOnly = true)
  public TemplateDto findById(Long id) {
    var template = this.findTemplateById(id).orElseThrow(() -> new TemplateNotFoundException(id));

    this.userTemplateSettingRepository
        .findByUserIdAndTemplateId(this.getUserId(), id)
        .ifPresent(
            userTemplateSetting -> {
              template.setFavorite(userTemplateSetting.isFavorite());
              template.setUsedCount(userTemplateSetting.getUsedCount());
            });

    template.setBusinessUnitName(getBusinessUnitById(template.getBusinessUnitId()).getUnitName());
    template.setFolderName(getFolderById(template.getFolderId()).getUnitName());

    return template;
  }

  public Optional<TemplateDto> findTemplateById(Long id) {
    return this.templateRepository.findById(id).map(this::mapData);
  }

  /**
   * To create new template step 1.
   *
   * @param templateDto refers to template information
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public TemplateDto save(TemplateDto templateDto) {
    List<Object> validated = this.validateTemplateDto(templateDto);

    log.info("Saving template step 1...");
    templateDto.setCreatedBy(this.getUserId());
    var template = this.templateRepository.save(this.mapEntity(templateDto));

    log.info("Saving user template...");
    var userTemplates = new UserTemplates();
    userTemplates.setUser((User) validated.get(0));
    userTemplates.setTemplate(template);
    this.userTemplatesRepository.save(userTemplates);

    log.info("Successfully creating template step 1...");
    var templateRes = mapData(template);
    templateRes.setBusinessUnitName(((BusinessUnitDTO) validated.get(1)).getUnitName());
    templateRes.setFolderName(((FolderDTO) validated.get(2)).getUnitName());
    return templateRes;
  }

  private Template findByTemplateId(Long id) {
    return this.templateRepository
        .findById(id)
        .orElseThrow(() -> new InvalidTemplateException("Template not found!"));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TemplateDto update(TemplateDto templateDto) {
    Template template = findByTemplateId(templateDto.getId());
    if (!Objects.equals(template.getCreatedBy(), this.getUserId())) {
      throw new AccessDeniedException();
    }

    var step = templateDto.getStep();
    switch (step) {
      case 1 -> {
        log.info("Updating template on step 1");
        this.validateTemplateDto(templateDto);
        if (!Objects.equals(templateDto.getType(), TemplateType.PREDEFINE.name())) {
          this.deleteParticipants(template.getId());
        }
        this.modelMapper.map(templateDto, template);
      }
      case 2 -> {
        log.info("Updating template on step 2");
        var signatory = templateDto.getSignature();
        if (ObjectUtils.defaultIfNull(signatory, 0) <= 0) {
          throw new InvalidRequestException("Template must include at less one signatory.");
        }

        template.setStep(2);
        template.setSignature(signatory);
        template.setSignProcess(templateDto.getSignProcess());
        template.setViewer(templateDto.getViewer());
        template.setApproval(templateDto.getApproval());
        template.setRecipient(templateDto.getRecipient());

        // Create user participants template
        var participants = templateDto.getParticipants();
        if (!CollectionUtils.isEmpty(participants)
            && Objects.equals(template.getType(), TemplateType.PREDEFINE.name())) {

          this.validateSortOrder(templateDto);
          templateDto
              .getInvalidParticipant()
              .ifPresent(
                  p -> {
                    throw new InvalidRequestException(
                        "Invalid participant role " + p.getFullName() + " role = " + p.getRole());
                  });
          this.validateParticipantsRole(templateDto);
          this.deleteParticipants(template.getId());

          participants.forEach(
              participant -> {
                participant.setTemplateId(template.getId());
                participant.setUserId(this.getUserId());
              });
          this.userParticipantsService.saveParticipants(participants);
        }
      }
      case 3 -> {
        log.info("Updating template on step 3");
        var templateMessageDto = templateDto.getTemplateMessage();

        if (Objects.isNull(templateMessageDto)) {
          throw new InvalidRequestException("Missing template message.");
        }

        template.setTemplateMessage(
            TemplateMessage.builder()
                .titleInvitation(templateMessageDto.getTitleInvitation())
                .messageInvitation(templateMessageDto.getMessageInvitation())
                .expiration(templateMessageDto.getExpiration())
                .sendReminder(templateMessageDto.getSendReminder())
                .template(template)
                .build());
        template.setNotificationService(templateDto.getNotificationService());
      }

      default -> throw new InvalidRequestException("Invalid step " + step);
    }

    templateDto.setModifiedBy(this.getUserId());
    this.templateRepository.save(template);
    var responseTemplate = this.mapData(template);

    var savedParticipants = this.userParticipantsService.getByTemplateId(template.getId());
    responseTemplate.setParticipants(savedParticipants);

    return responseTemplate;
  }

  private List<Object> validateTemplateDto(TemplateDto templateDto) {
    log.info("Validating requesting template.");
    var userId = this.getUserId();
    List<Object> requiredObject = new ArrayList<>();

    Long folderId = templateDto.getFolderId();
    var businessUnitId = templateDto.getBusinessUnitId();
    if (Objects.isNull(folderId)) {
      throw new MissingParamException(" folder id");
    } else if (Objects.isNull(businessUnitId)) {
      throw new MissingParamException(" business Id");
    }

    var foundUser =
        this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    requiredObject.add(foundUser);

    log.info("Getting business unit with id:{}...", businessUnitId);
    var businessUnit = getBusinessUnitById(businessUnitId);
    if (Objects.isNull(businessUnit)) {
      throw new BusinessUnitNotFoundException(businessUnitId);
    }
    requiredObject.add(businessUnit);

    log.info("Getting folder info with id:{}...", folderId);
    var folder = getFolderById(folderId);
    if (Objects.isNull(folder)) {
      throw new FolderNotFoundException(folderId);
    }
    requiredObject.add(folder);

    if (!Objects.equals(folder.getBusinessUnitId(), businessUnit.getId())) {
      throw new FolderNotFoundException(
          "Folder with this id:"
              + folderId
              + " not in this business unit with id:"
              + businessUnitId);
    }

    if (!Objects.equals(folder.getCreatedBy(), userId)
        && RoleConstant.isCorporateUser(foundUser.getRoles())) {
      throw new FolderNotFoundException(
          "Folder with this id:" + folderId + " is private to end-user");
    }

    return requiredObject;
  }

  private void validateParticipantsRole(TemplateDto templateDto) {
    log.info("Validating role quantities and participants info of template");
    String message = " role quantities not the same as info";
    if (Objects.nonNull(templateDto.getParticipants())
        && !templateDto.getParticipants().isEmpty()) {

      if (Objects.nonNull(templateDto.getSignature())
          && (templateDto.getParticipantsByRole(ParticipantRole.SIGNATORY.getRole()).size()
              != templateDto.getSignature())) {

        throw new InvalidRequestException(ParticipantRole.SIGNATORY.getRole() + message);
      } else if (Objects.nonNull(templateDto.getApproval())
          && (templateDto.getParticipantsByRole(ParticipantRole.APPROVAL.getRole()).size()
              != templateDto.getApproval())) {

        throw new InvalidRequestException(ParticipantRole.APPROVAL.getRole() + message);
      } else if (Objects.nonNull(templateDto.getViewer())
          && (templateDto.getParticipantsByRole(ParticipantRole.VIEWER.getRole()).size()
              != templateDto.getViewer())) {

        throw new InvalidRequestException(ParticipantRole.VIEWER.getRole() + message);
      } else if (Objects.nonNull(templateDto.getRecipient())
          && (templateDto.getParticipantsByRole(ParticipantRole.RECEIPT.getRole()).size()
              != templateDto.getRecipient())) {

        throw new InvalidRequestException(ParticipantRole.RECEIPT.getRole() + message);
      }
    }
  }

  private void validateSortOrder(TemplateDto templateDto) {
    List<Integer> sortOrders =
        templateDto.getParticipants().stream().map(UserParticipantDto::getSortOrder).toList();
    List<Integer> foundDuplicate =
        sortOrders.stream().filter(i -> Collections.frequency(sortOrders, i) > 1).toList();

    if (!foundDuplicate.isEmpty()) {
      throw new InvalidRequestException("Duplicate sort order " + foundDuplicate);
    }
  }

  private void deleteParticipants(Long templateId) {
    var foundParticipants = this.userParticipantsService.getByTemplateId(templateId);
    if (!foundParticipants.isEmpty()) {
      this.userParticipantsService.deleteByTemplateId(templateId, this.getUserId());
    }
  }

  public List<UserParticipantDto> getParticipants(Long templateId) {
    findByTemplateId(templateId);
    return this.userParticipantsService.getByTemplateId(templateId);
  }

  public Set<String> getTemplateTypes() {
    return TemplateType.types();
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(long id) {
    var template = this.templateRepository.findById(id);
    if (template.isPresent()) {
      this.templateRepository.deleteById(id);
    }
  }

  private BusinessUnitDTO getBusinessUnitById(long id) {
    return this.businessUnitsFeignClient.findById(id);
  }

  private FolderDTO getFolderById(long id) {
    return this.businessUnitsFeignClient.findFolderById(id);
  }

  public void updateBusinessId(Long userId, Long businessId) {
    var userTemplates = this.userTemplatesRepository.findUserTemplatesByUserId(userId);
    this.templateRepository.updateBusinessId(
        userTemplates.stream().map(UserTemplates::getTemplate).map(Template::getId).toList(),
        businessId);
  }

  private UserTemplates getUserTemplate(Long templateId) {
    var userTemplateOpt = this.userTemplatesRepository.findByTemplateId(templateId);

    if (userTemplateOpt.isPresent()) {
      return userTemplateOpt.get();
    }

    throw new TemplateNotFoundException(templateId);
  }

  private UserTemplateSetting getUserTemplateSetting(Long userId, Long templateId) {
    log.info("Getting user template with userId: {}, templateId: {} ", userId, templateId);
    var userTemplate = this.getUserTemplate(templateId);

    var userTemplateSettingOpt =
        this.userTemplateSettingRepository.findByUserIdAndTemplateId(userId, templateId);

    return userTemplateSettingOpt.orElseGet(
        () -> {
          var userTemplateSetting =
              UserTemplateSetting.builder()
                  .userId(userId)
                  .template(userTemplate.getTemplate())
                  .build();
          userTemplateSetting.setCreatedBy(this.getUserId());
          userTemplateSetting.setModifiedBy(this.getUserId());
          return this.userTemplateSettingRepository.save(userTemplateSetting);
        });
  }

  public void setFavoriteTemplate(Long templateId) {
    var userTemplate = getUserTemplateSetting(this.getUserId(), templateId);
    var isFavorite = userTemplate.isFavorite();

    log.info("Setting template favorite: {}", !isFavorite);
    userTemplate.setFavorite(!isFavorite);
    userTemplate.setModifiedBy(this.getUserId());
    this.userTemplateSettingRepository.save(userTemplate);
  }

  public void increaseTemplateUsed(Long templateId) {
    var userTemplate = getUserTemplateSetting(this.getUserId(), templateId);
    var usedCount = userTemplate.getUsedCount();

    log.info("Setting template used count from: {} to: {}", usedCount, usedCount + 1);
    userTemplate.setUsedCount(usedCount + 1);
    userTemplate.setModifiedBy(this.getUserId());
    this.userTemplateSettingRepository.save(userTemplate);
  }

  public EntityResponseHandler<TemplateDto> getUserFavoriteTemplates(
      Pageable pageable, String search) {

    boolean isUnPaged = pageable.isUnpaged();

    Specification<UserTemplateSetting> specification =
        AdvancedFilter.searchByFields(
            Arrays.asList(
                Filter.builder()
                    .field(UserTemplateSetting_.USER_ID)
                    .value(String.valueOf(this.getUserId()))
                    .operator(QueryOperator.EQUALS)
                    .build(),
                Filter.builder()
                    .field(UserTemplateSetting_.FAVORITE)
                    .operator(QueryOperator.IS_TRUE)
                    .build()));

    if (!isUnPaged) {
      var sort =
          Sort.by("usedCount")
              .descending()
              .and(Sort.by("favorite"))
              .descending()
              .and(pageable.getSort());
      pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

      specification =
          specification.or(
              AdvancedFilter.searchByField(
                  Filter.builder()
                      .field(UserTemplateSetting_.USED_COUNT)
                      .operator(QueryOperator.GREATER_THAN)
                      .value("0")
                      .build()));
    }

    if (StringUtils.hasText(search)) {
      specification =
          specification.and(
              AdvancedFilter.searchByField(
                  Filter.builder()
                      .referenceField(Arrays.asList(UserTemplateSetting_.TEMPLATE, Template_.NAME))
                      .value(search)
                      .operator(QueryOperator.LIKE)
                      .build()));
    }

    var userTemplates = this.userTemplateSettingRepository.findAll(specification, pageable);
    var templateIds =
        userTemplates.stream().map(UserTemplateSetting::getTemplate).map(Template::getId).toList();
    var participantList = this.userParticipantsService.getByTemplateIds(templateIds);

    var templateDtoList =
        userTemplates
            .map(
                userTemplate -> {
                  var templateDto =
                      this.modelMapper.map(userTemplate.getTemplate(), TemplateDto.class);

                  var participants =
                      participantList.stream()
                          .filter(
                              participant ->
                                  Objects.equals(participant.getTemplateId(), templateDto.getId()))
                          .toList();

                  templateDto.setFavorite(userTemplate.isFavorite());
                  templateDto.setUsedCount(userTemplate.getUsedCount());
                  templateDto.setParticipants(participants);
                  return templateDto;
                })
            .stream()
            .toList();

    return new EntityResponseHandler<>(
        templateDtoList,
        isUnPaged ? 0 : pageable.getPageNumber() + 1,
        isUnPaged ? 0 : pageable.getPageSize(),
        userTemplates.getTotalPages(),
        userTemplates.getTotalElements(),
        userTemplates.hasNext());
  }
}
