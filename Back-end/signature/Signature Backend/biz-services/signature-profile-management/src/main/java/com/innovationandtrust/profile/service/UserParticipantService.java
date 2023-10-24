package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.UserParticipantDto;
import com.innovationandtrust.profile.model.entity.Template_;
import com.innovationandtrust.profile.model.entity.UserParticipant;
import com.innovationandtrust.profile.model.entity.UserParticipant_;
import com.innovationandtrust.profile.repository.UserParticipantRepository;
import com.innovationandtrust.profile.repository.UserTemplatesRepository;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class UserParticipantService
    extends CommonCrudService<UserParticipantDto, UserParticipant, Long> {
  private final UserParticipantRepository userParticipantRepository;
  private final UserTemplatesRepository userTemplatesRepository;

  protected UserParticipantService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      UserParticipantRepository repository,
      UserTemplatesRepository userTemplatesRepository) {
    super(modelMapper, keycloakProvider);
    this.userParticipantRepository = repository;
    this.userTemplatesRepository = userTemplatesRepository;
  }

  @Transactional(rollbackFor = Exception.class)
  public void saveParticipants(List<UserParticipantDto> userParticipants) {

    log.info("Saving user participants");
    if (CollectionUtils.isEmpty(userParticipants)) {
      throw new InvalidRequestException("Participants must be not empty.");
    }

    var toSaveParticipants = mapAll(userParticipants, UserParticipant.class);
    userTemplatesRepository
        .findByUserIdAndTemplateId(getUserId(), userParticipants.get(0).getTemplateId())
        .ifPresent(
            userTemplate ->
                toSaveParticipants.forEach(
                    participant -> {
                      participant.setUser(userTemplate.getUser());
                      participant.setTemplate(userTemplate.getTemplate());
                    }));
    this.mapAll(
        this.userParticipantRepository.saveAll(toSaveParticipants), UserParticipantDto.class);
  }

  public List<UserParticipantDto> getByTemplateId(Long templateId) {
    log.info("Getting participants by template id {}", templateId);
    return this.mapAll(
        this.userParticipantRepository.findAll(
            AdvancedFilter.searchByField(
                Filter.builder()
                    .referenceField(Arrays.asList(UserParticipant_.TEMPLATE, Template_.ID))
                    .operator(QueryOperator.EQUALS)
                    .value(String.valueOf(templateId))
                    .build())),
        UserParticipantDto.class);
  }

  public List<UserParticipantDto> getByTemplateIds(List<Long> templateIds) {
    return this.mapAll(
        this.userParticipantRepository.findAll(
            AdvancedFilter.searchByField(
                Filter.builder()
                    .referenceField(Arrays.asList(UserParticipant_.TEMPLATE, Template_.ID))
                    .operator(QueryOperator.IN)
                    .values(templateIds.stream().map(String::valueOf).toList())
                    .build())),
        UserParticipantDto.class);
  }

  public void deleteByTemplateId(Long templateId, Long userId) {
    log.info("Deleting participants by templateId:{} and userId:{}", templateId, userId);
    this.userParticipantRepository.deleteAllByTemplateIdAndUserId(templateId, userId);
  }
}
