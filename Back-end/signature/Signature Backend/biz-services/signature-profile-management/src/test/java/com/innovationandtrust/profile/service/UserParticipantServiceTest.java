package com.innovationandtrust.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.profile.model.dto.UserParticipantDto;
import com.innovationandtrust.profile.model.entity.Template;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.UserParticipant;
import com.innovationandtrust.profile.model.entity.UserTemplates;
import com.innovationandtrust.profile.repository.UserParticipantRepository;
import com.innovationandtrust.profile.repository.UserTemplatesRepository;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
class UserParticipantServiceTest {
  private UserParticipantService userParticipantService;
  private IKeycloakProvider keycloakProvider;
  private ModelMapper modelMapper;
  private UserParticipantDto participantDto;
  private UserParticipant participant;
  private UserTemplates userTemplate;
  @Mock private UserParticipantRepository userParticipantRepository;
  @Mock private UserTemplatesRepository userTemplatesRepository;

  @BeforeAll
  public static void init() {
    mockStatic(AuthenticationUtils.class);
  }

  @BeforeEach
  void setup() {
    long templateId = 1L;
    long userId = 1L;
    modelMapper = spy(new ModelMapper());
    userParticipantService =
        spy(
            new UserParticipantService(
                modelMapper, keycloakProvider, userParticipantRepository, userTemplatesRepository));

    participantDto =
        UserParticipantDto.builder()
            .templateId(templateId)
            .userId(userId)
            .firstName("Anna")
            .lastName("ECO")
            .email("signature@ccertigna.fr")
            .phone("010222200")
            .sortOrder(0)
            .role(ParticipantRole.SIGNATORY.getRole())
            .build();

    participant = this.modelMapper.map(participantDto, UserParticipant.class);

    userTemplate = new UserTemplates();
    userTemplate.setTemplate(new Template());
    userTemplate.setUser(new User());
  }

  @Test
  @Order(1)
  @DisplayName("Save all participants to database test")
  void create_new_participants() {
    // given
    var participants = Collections.singletonList(participantDto);

    // when
    when(this.userTemplatesRepository.findByUserIdAndTemplateId(anyLong(), anyLong()))
        .thenReturn(Optional.ofNullable(userTemplate));
    this.userParticipantService.saveParticipants(participants);

    verify(this.userParticipantService, times(1)).saveParticipants(participants);
  }

  @Test
  @Order(2)
  @DisplayName("Save all participants to database fail test")
  void create_new_participants_fail() {
    // when
    when(this.userTemplatesRepository.findByUserIdAndTemplateId(anyLong(), anyLong()))
        .thenReturn(Optional.ofNullable(userTemplate));
    var exception =
        assertThrows(
            InvalidRequestException.class,
            () -> this.userParticipantService.saveParticipants(Collections.emptyList()));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(3)
  @DisplayName("Get participant by template id test")
  @SuppressWarnings("unchecked")
  void get_template_participants() {
    // when
    when(this.userTemplatesRepository.findAll(any(Specification.class)))
        .thenReturn(Collections.singletonList(participantDto));
    when(this.modelMapper.map(participant, UserParticipantDto.class)).thenReturn(participantDto);
    var result = this.userParticipantService.getByTemplateId(1L);

    verify(this.userParticipantService, times(1)).getByTemplateId(1L);
    assertThat(result).isNotNull();
  }

  @Test
  @Order(4)
  @DisplayName("Get participant by template ids test")
  @SuppressWarnings("unchecked")
  void get_templates_participants() {
    // given
    var templateIds = List.of(1L);
    // when
    when(this.userTemplatesRepository.findAll(any(Specification.class)))
        .thenReturn(Collections.singletonList(participantDto));
    when(this.modelMapper.map(participant, UserParticipantDto.class)).thenReturn(participantDto);
    var result = this.userParticipantService.getByTemplateIds(templateIds);

    verify(this.userParticipantService, times(1)).getByTemplateIds(templateIds);
    assertThat(result).isNotNull();
  }

  @Test
  @Order(5)
  @DisplayName("Delete template participants test")
  void delete_template_participants() {
    this.userParticipantService.deleteByTemplateId(1L, 1L);

    verify(this.userParticipantService, times(1)).deleteByTemplateId(1L, 1L);
  }
}
