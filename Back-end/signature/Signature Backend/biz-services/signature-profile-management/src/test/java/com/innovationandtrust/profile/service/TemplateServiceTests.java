package com.innovationandtrust.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.profile.Constant.Constant;
import com.innovationandtrust.profile.constant.TemplateType;
import com.innovationandtrust.profile.exception.TemplateNotFoundException;
import com.innovationandtrust.profile.model.dto.TemplateDto;
import com.innovationandtrust.profile.model.dto.TemplateFolder;
import com.innovationandtrust.profile.model.dto.UserParticipantDto;
import com.innovationandtrust.profile.model.entity.Template;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.UserParticipant;
import com.innovationandtrust.profile.model.entity.UserTemplateSetting;
import com.innovationandtrust.profile.model.entity.UserTemplates;
import com.innovationandtrust.profile.repository.TemplateRepository;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.profile.repository.UserTemplateSettingRepository;
import com.innovationandtrust.profile.repository.UserTemplatesRepository;
import com.innovationandtrust.profile.service.restclient.BusinessUnitsFeignClient;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.corporateprofile.BusinessUnitDTO;
import com.innovationandtrust.share.model.corporateprofile.FolderDTO;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.exception.exceptions.AccessDeniedException;
import com.innovationandtrust.utils.exception.exceptions.BusinessUnitNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.FolderNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.MissingParamException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Arrays;
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
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
class TemplateServiceTests {
  private TemplateDto templateDto;
  private Template template;
  private List<TemplateFolder> templateFolders;
  private TemplateService templateService;
  private ModelMapper modelMapper;
  private User user;
  private UserTemplates userTemplate;
  private UserParticipantDto participantDto;
  private FolderDTO folder;
  private BusinessUnitDTO businessUnit;
  @Mock private IKeycloakProvider keycloakProvider;
  @Mock private TemplateRepository templateRepository;
  @Mock private UserRepository userRepository;
  @Mock private UserTemplatesRepository userTemplatesRepository;
  @Mock private BusinessUnitsFeignClient businessUnitsFeignClient;
  @Mock private UserParticipantService userParticipantsService;
  @Mock private UserTemplateSettingRepository userTemplateSettingRepository;

  @BeforeAll
  public static void init() {
    mockStatic(AuthenticationUtils.class);
  }

  @BeforeEach
  void setup() {
    long userId = Constant.userId;
    modelMapper = spy(new ModelMapper());

    templateService =
        spy(
            new TemplateService(
                modelMapper,
                keycloakProvider,
                templateRepository,
                userRepository,
                userTemplatesRepository,
                businessUnitsFeignClient,
                userParticipantsService,
                userTemplateSettingRepository));

    templateDto = Constant.getTemplateDto();

    template = this.modelMapper.map(templateDto, Template.class);
    template.setCreatedBy(0L);

    user = new User(0L, "Anna", "ECO");
    user.setCreatedBy(userId);

    userTemplate = new UserTemplates();
    userTemplate.setTemplate(template);
    userTemplate.setUser(user);

    participantDto = Constant.getParticipantDto();

    UserParticipant participant = this.modelMapper.map(participantDto, UserParticipant.class);

    folder = new FolderDTO(1L, "Folder", 1L, userId);

    businessUnit = new BusinessUnitDTO();
    businessUnit.setId(1L);
    businessUnit.setUnitName("Business Name");
  }

  @Test
  @Order(1)
  @DisplayName("Get all folder of templates created by end user and corporate with search test")
  void find_template_by_company() {
    // when
    get_user_templates();
    String templateName = Constant.templateName;
    var result = templateService.findAll(templateName);
    verify(templateService, times(1)).findAll(templateName);

    assertThat(result).isNotNull();
  }

  @Test
  @Order(2)
  @DisplayName("Get all folder of templates created by end user and corporate no search test")
  void find_template_by_company_search() {
    // when
    get_user_templates();

    var result = templateService.findAll("");
    verify(templateService, times(1)).findAll("");

    assertThat(result).isNotNull();
  }

  @SuppressWarnings("unchecked")
  private void get_user_templates() {
    // given
    final List<UserTemplateSetting> userTemplateSettings =
        Arrays.asList(
            UserTemplateSetting.builder().template(template).userId(1L).favorite(true).build(),
            UserTemplateSetting.builder().template(template).userId(1L).usedCount(10).build());
    final User corporateUser = user;
    final Page<UserTemplateSetting> templateDtoPage = new PageImpl<>(userTemplateSettings);

    // when
    when(this.userTemplateSettingRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(templateDtoPage);
    when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
    when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(corporateUser));
    when(userTemplatesRepository.findAll(isA(Specification.class)))
        .thenReturn(Collections.singletonList(userTemplate));
    when(userParticipantsService.getByTemplateIds(any()))
        .thenReturn(Collections.singletonList(participantDto));
    when(businessUnitsFeignClient.getFoldersByUsersId(any()))
        .thenReturn(Collections.singletonList(folder));
  }

  @Test
  @Order(3)
  @DisplayName("Get templates by corporate test")
  void find_by_corporate_test() {
    // when
    get_user_templates();
    when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

    var result = templateService.findByCorporate();

    // then
    verify(templateService, times(1)).findByCorporate();
    assertThat(result).isNotNull();
  }

  @Test
  @Order(4)
  @DisplayName("Find template by id")
  void find_by_id() {
    // when
    find_template_by_id();

    var result = templateService.findById(1L);

    // then
    verify(templateService, times(1)).findById(1L);
    assertThat(result).isNotNull();
    assertEquals(templateDto.getId(), result.getId());
  }

  @Test
  @Order(4)
  @DisplayName("Find template by id fail")
  void find_by_id_fail() {
    // when
    find_template_by_id();

    var exception =
        assertThrows(TemplateNotFoundException.class, () -> this.templateService.findById(null));
    log.info("Error : {}", exception.getMessage());
  }

  private void find_template_by_id() {
    // when
    templateDto.setBusinessUnitId(1L);
    get_folder_and_business();
    when(templateService.findTemplateById(anyLong())).thenReturn(Optional.ofNullable(templateDto));
    when(userParticipantsService.getByTemplateId(anyLong()))
        .thenReturn(Collections.singletonList(participantDto));
  }

  private void get_folder_and_business() {
    when(businessUnitsFeignClient.findById(anyLong())).thenReturn(businessUnit);
    when(businessUnitsFeignClient.findFolderById(anyLong())).thenReturn(folder);
  }

  @Test
  @Order(4)
  @DisplayName("Create new template")
  void save_template_test() {
    // when
    validateTemplate();

    var result = templateService.save(templateDto);

    verify(templateService, times(1)).save(templateDto);
    assertThat(result).isNotNull();
  }

  @Test
  @Order(5)
  @DisplayName("Create new template missing business id test")
  void save_template_fail_business_id_test() {
    // when
    validateTemplate();

    templateDto.setBusinessUnitId(null);
    var exception =
        assertThrows(MissingParamException.class, () -> this.templateService.save(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(5)
  @DisplayName("Create new template missing folder id test")
  void save_template_fail_folder_id_test() {
    // when
    validateTemplate();

    templateDto.setFolderId(null);
    var exception =
        assertThrows(MissingParamException.class, () -> this.templateService.save(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(6)
  @DisplayName("Create new template with not found business test")
  void save_template_business_not_found_test() {
    // when
    validateTemplate();

    when(businessUnitsFeignClient.findById(anyLong())).thenReturn(null);
    var exception =
        assertThrows(
            BusinessUnitNotFoundException.class, () -> this.templateService.save(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(7)
  @DisplayName("Create new template with not found folder test")
  void save_template_folder_not_found_test() {
    // when
    validateTemplate();

    when(businessUnitsFeignClient.findFolderById(anyLong())).thenReturn(null);
    var exception =
        assertThrows(FolderNotFoundException.class, () -> this.templateService.save(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(8)
  @DisplayName("Create new template with folder not in business test")
  void save_template_folder_not_in_business_test() {
    // when
    validateTemplate();

    folder.setBusinessUnitId(2L);
    var exception =
        assertThrows(FolderNotFoundException.class, () -> this.templateService.save(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(9)
  @DisplayName("Create new template with in other private folder test")
  void save_template_in_other_private_folder_test() {
    try (MockedStatic<RoleConstant> roleStatic = mockStatic(RoleConstant.class)) {
      // given
      given(RoleConstant.isCorporateUser(any())).willReturn(true);

      // when
      validateTemplate();

      var exception =
          assertThrows(FolderNotFoundException.class, () -> this.templateService.save(templateDto));
      log.info("Error : {}", exception.getMessage());
    }
  }

  private void validateTemplate() {
    // when
    get_folder_and_business();
    when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
    when(this.modelMapper.map(template, TemplateDto.class)).thenReturn(templateDto);
    when(this.templateRepository.save(any(Template.class))).thenReturn(template);
  }

  private void getTemplate() {
    when(templateRepository.findById(anyLong())).thenReturn(Optional.ofNullable(template));
  }

  private void updateTemplate() {
    when(this.modelMapper.map(template, TemplateDto.class)).thenReturn(templateDto);
    var result = templateService.update(templateDto);
    verify(templateService, times(1)).update(templateDto);
    assertNotNull(result);
  }

  @Test
  @Order(10)
  @DisplayName("Update template step 1")
  void update_template_step_1_test() {
    // when
    this.validateTemplate();
    this.getTemplate();
    when(this.userParticipantsService.getByTemplateId(anyLong()))
        .thenReturn(Collections.emptyList());

    this.updateTemplate();
  }

  @Test
  @Order(11)
  @DisplayName("Update template change type PREDEFINE To DEFAULT")
  void update_template_step_1_change_type_test() {
    // given
    templateDto.setType(TemplateType.DEFAULT.name());

    // when
    this.validateTemplate();
    this.getTemplate();
    when(this.userParticipantsService.getByTemplateId(anyLong()))
        .thenReturn(Collections.emptyList());

    this.updateTemplate();
  }

  @Test
  @Order(12)
  @DisplayName("Update template step 2 with DEFAULT Type")
  void update_template_step_2_test() {
    // given
    templateDto.setStep(2);

    // when
    this.getTemplate();
    when(this.userParticipantsService.getByTemplateId(anyLong()))
        .thenReturn(Collections.emptyList());

    this.updateTemplate();
  }

  @Test
  @Order(13)
  @DisplayName("Update template step 2 with PREDEFINE Type")
  void update_template_step_2_predefine_type_test() {
    // given
    templateDto.setStep(2);
    template.setType(TemplateType.PREDEFINE.name());
    templateDto.setParticipants(Collections.singletonList(participantDto));

    // when
    this.getTemplate();
    when(this.userParticipantsService.getByTemplateId(anyLong()))
        .thenReturn(Collections.singletonList(participantDto));

    var result = templateService.update(templateDto);
    verify(templateService, times(1)).update(templateDto);
    assertNotNull(result);
  }

  @Test
  @Order(14)
  @DisplayName("Update template step 3")
  void update_template_step_3_test() {
    // given
    templateDto.setStep(3);

    // when
    this.getTemplate();
    when(this.userParticipantsService.getByTemplateId(anyLong()))
        .thenReturn(Collections.emptyList());

    this.updateTemplate();
  }

  @Test
  @Order(15)
  @DisplayName("Update template with invalid step")
  void update_template_invalid_step_test() {
    // given
    templateDto.setStep(5);

    // when
    this.getTemplate();

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(16)
  @DisplayName("Update template with fail access denied")
  void update_template_fail_access_denied_test() {
    // given
    template.setCreatedBy(1L);

    // when
    this.getTemplate();

    var exception =
        assertThrows(AccessDeniedException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(17)
  @DisplayName("Update template with fail zero signatory")
  void update_template_fail_zero_signatory_test() {
    // given
    templateDto.setStep(2);
    templateDto.setSignature(0);

    // when
    this.getTemplate();

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(17)
  @DisplayName("Update template with a signatory has invalid role test")
  void update_template_fail_invalid_role_test() {
    // given
    this.setStep2();
    participantDto.setRole("INVALID_ROLE");
    templateDto.setParticipants(Collections.singletonList(participantDto));

    // when
    this.getTemplate();

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(18)
  @DisplayName("Update template step 3 with missing template message test")
  void update_template_fail_missing_template_message_test() {
    // given
    templateDto.setStep(3);
    templateDto.setTemplateMessage(null);

    // when
    this.getTemplate();

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(19)
  @DisplayName("Update template with a signatories has invalid sort order test")
  void update_template_fail_invalid_sort_order_test() {
    // given
    this.setStep2();
    final UserParticipantDto userParticipant = participantDto;
    userParticipant.setSortOrder(1);
    templateDto.setParticipants(List.of(participantDto, userParticipant));

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  private UserParticipantDto getParticipantDto(ParticipantRole role, int sortOrder) {
    UserParticipantDto userParticipantDto = new UserParticipantDto(participantDto);
    userParticipantDto.setRole(role.getRole());
    userParticipantDto.setSortOrder(sortOrder);
    return userParticipantDto;
  }

  private void setStep2() {
    templateDto.setStep(2);
    template.setType(TemplateType.PREDEFINE.name());

    // when
    this.getTemplate();
  }

  @Test
  @Order(20)
  @DisplayName("Update template with a signatory has invalid role quantities test")
  void update_template_fail_invalid_signatory_role_quantities_test() {
    // given
    this.setStep2();
    templateDto.setSignature(2);
    templateDto.setParticipants(List.of(getParticipantDto(ParticipantRole.SIGNATORY, 0)));

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(21)
  @DisplayName("Update template with a approval has invalid role quantities test")
  void update_template_fail_invalid_approval_role_quantities_test() {
    // given
    this.setStep2();
    templateDto.setSignature(1);
    templateDto.setApproval(2);
    var participantList =
        List.of(
            getParticipantDto(ParticipantRole.SIGNATORY, 0),
            getParticipantDto(ParticipantRole.APPROVAL, 1));
    templateDto.setParticipants(participantList);

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(22)
  @DisplayName("Update template with a viewer has invalid role quantities test")
  void update_template_fail_invalid_viewer_role_quantities_test() {
    // given
    this.setStep2();
    templateDto.setSignature(1);
    templateDto.setApproval(1);
    templateDto.setViewer(2);
    var participantList =
        List.of(
            getParticipantDto(ParticipantRole.SIGNATORY, 0),
            getParticipantDto(ParticipantRole.APPROVAL, 1),
            getParticipantDto(ParticipantRole.VIEWER, 2));
    templateDto.setParticipants(participantList);

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(23)
  @DisplayName("Update template with a receipt has invalid role quantities test")
  void update_template_fail_invalid_receipt_role_quantities_test() {
    // given
    this.setStep2();
    templateDto.setSignature(1);
    templateDto.setApproval(1);
    templateDto.setViewer(1);
    templateDto.setRecipient(2);
    var participantList =
        List.of(
            getParticipantDto(ParticipantRole.SIGNATORY, 0),
            getParticipantDto(ParticipantRole.APPROVAL, 1),
            getParticipantDto(ParticipantRole.VIEWER, 2),
            getParticipantDto(ParticipantRole.RECEIPT, 3));
    templateDto.setParticipants(participantList);

    var exception =
        assertThrows(InvalidRequestException.class, () -> this.templateService.update(templateDto));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(24)
  @DisplayName("Get participant of a template test")
  void get_participants_of_template() {
    // when
    this.getTemplate();
    when(this.userParticipantsService.getByTemplateId(anyLong()))
        .thenReturn(Collections.singletonList(participantDto));
    var result = templateService.getParticipants(1L);
    verify(templateService, times(1)).getParticipants(1L);

    assertThat(result).isNotNull();
  }

  @Test
  @Order(25)
  @DisplayName("Delete template test")
  void delete_template_test() {
    // when
    this.getTemplate();

    this.templateService.delete(1L);
    verify(templateService, times(1)).delete(1L);
  }

  @Test
  @Order(26)
  @DisplayName("Update templates business unit id test")
  void update_templates_business_unit_test() {
    // when
    when(this.userTemplatesRepository.findUserTemplatesByUserId(anyLong()))
        .thenReturn(Collections.singletonList(userTemplate));

    this.templateService.updateBusinessId(1L, 1L);
    verify(templateService, times(1)).updateBusinessId(1L, 1L);
  }

  @Test
  @Order(27)
  @DisplayName("Get template types test")
  void get_template_types_test() {
    this.templateService.getTemplateTypes();
    verify(templateService, times(1)).getTemplateTypes();
  }

  @Test
  @Order(28)
  @DisplayName("Set favorite template test")
  void set_favorite_template_test() {
    // when
    when(this.userTemplatesRepository.findByTemplateId(anyLong()))
        .thenReturn(Optional.ofNullable(userTemplate));
    when(this.userTemplateSettingRepository.findByUserIdAndTemplateId(anyLong(), anyLong()))
        .thenReturn(Optional.of(new UserTemplateSetting()));

    this.templateService.setFavoriteTemplate(1L);

    verify(templateService, times(1)).setFavoriteTemplate(1L);
  }

  @Test
  @Order(29)
  @DisplayName("Set favorite template fail test")
  void set_favorite_template_fail_test() {
    // when
    when(this.userTemplatesRepository.findByTemplateId(anyLong())).thenReturn(Optional.empty());

    var exception =
        assertThrows(
            TemplateNotFoundException.class, () -> this.templateService.setFavoriteTemplate(1L));
    log.info("Error : {}", exception.getMessage());
  }

  @Test
  @Order(30)
  @DisplayName("Set favorite template if not found in user template setting test")
  void set_favorite_template__test() {
    // when
    when(this.userTemplatesRepository.findByTemplateId(anyLong()))
        .thenReturn(Optional.ofNullable(userTemplate));
    when(this.userTemplateSettingRepository.findByUserIdAndTemplateId(anyLong(), anyLong()))
        .thenReturn(Optional.empty());
    when(this.userTemplateSettingRepository.save(any())).thenReturn(new UserTemplateSetting());

    this.templateService.setFavoriteTemplate(1L);

    verify(templateService, times(1)).setFavoriteTemplate(1L);
  }

  @Test
  @Order(31)
  @DisplayName("Increment used template by user test")
  void increment_used_template__test() {
    // when
    when(this.userTemplatesRepository.findByTemplateId(anyLong()))
        .thenReturn(Optional.ofNullable(userTemplate));
    when(this.userTemplateSettingRepository.findByUserIdAndTemplateId(anyLong(), anyLong()))
        .thenReturn(Optional.of(new UserTemplateSetting()));

    this.templateService.increaseTemplateUsed(1L);

    verify(templateService, times(1)).increaseTemplateUsed(1L);
  }

  @Test
  @Order(30)
  @SuppressWarnings("unchecked")
  @DisplayName("Get all user favorite and most used templates")
  void get_all_user_favorite_template__test() {
    // given
    final List<UserTemplateSetting> userTemplateSettings =
        Arrays.asList(
            UserTemplateSetting.builder().template(template).userId(1L).favorite(true).build(),
            UserTemplateSetting.builder().template(template).userId(1L).usedCount(10).build());

    final Page<UserTemplateSetting> templateDtoPage = new PageImpl<>(userTemplateSettings);

    // when
    when(this.userTemplateSettingRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(templateDtoPage);
    when(this.userParticipantsService.getByTemplateIds(any()))
        .thenReturn(Collections.singletonList(participantDto));

    // then
    var result = this.templateService.getUserFavoriteTemplates(Pageable.unpaged(), "");
    verify(templateService, times(1)).getUserFavoriteTemplates(Pageable.unpaged(), "");

    result = this.templateService.getUserFavoriteTemplates(Pageable.unpaged(), "TemplateName");
    verify(templateService, times(1)).getUserFavoriteTemplates(Pageable.unpaged(), "TemplateName");

    var templates = (List<TemplateDto>) result.getContents();
    assertThat(result).isNotNull();
    assertThat(templates.get(0).isFavorite()).isTrue();
  }
}
