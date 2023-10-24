package com.innovationandtrust.profile.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.innovationandtrust.profile.Constant.AbstractTest;
import com.innovationandtrust.profile.Constant.Constant;
import com.innovationandtrust.profile.constant.TemplateEnum;
import com.innovationandtrust.profile.constant.TemplateType;
import com.innovationandtrust.profile.model.dto.TemplateDto;
import com.innovationandtrust.profile.model.dto.TemplateFolder;
import com.innovationandtrust.profile.model.dto.UserParticipantDto;
import com.innovationandtrust.profile.model.entity.Template;
import com.innovationandtrust.profile.service.TemplateService;
import com.innovationandtrust.share.utils.EntityResponseHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ContextConfiguration("classpath:application-test.yml")
class TemplateControllerTests extends AbstractTest {
  private TemplateController templateController;
  @Mock TemplateService templateService;
  private TemplateDto templateDto;
  private UserParticipantDto participantDto;
  private List<TemplateFolder> templateFolders;
  private MockMvc mockMvc;
  private final String templateName = Constant.templateName;
  private final String base = Constant.templateBaseApi;
  private final String search = Constant.search;

  @BeforeEach
  void setup() {
    ModelMapper modelMapper = spy(new ModelMapper());
    templateController = spy(new TemplateController(templateService));

    mockMvc = MockMvcBuilders.standaloneSetup(templateController).build();

    templateDto = Constant.getTemplateDto();
    Template template = modelMapper.map(templateDto, Template.class);
    template.setCreatedBy(0L);

    templateFolders = new ArrayList<>();
    templateFolders.add(
        TemplateFolder.builder()
            .unitName(TemplateEnum.MOST_USED.name())
            .templates(Collections.singleton(templateDto))
            .countTemplates(1)
            .build());

    participantDto = Constant.getParticipantDto();
  }

  @Test
  @DisplayName("Get all template api test")
  void find_template_by_company_api_test() throws Exception {
    // when
    when(this.templateService.findAll(anyString())).thenReturn(templateFolders);

    // perform
    mockMvc
        .perform(
            get(Constant.path(base))
                .param(search, templateName)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].templates", notNullValue()));

    // then verify
    ResponseEntity<List<TemplateFolder>> response = templateController.findAll(templateName);
    List<TemplateFolder> templateFolderList = response.getBody();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    assertThat(templateFolderList).isEqualTo(templateFolders);
  }

  @Test
  @DisplayName("Get corporate templates api test")
  void get_corporate_templates_api_test() throws Exception {
    // given
    final List<TemplateDto> templatesDto = Collections.singletonList(templateDto);

    // when
    when(this.templateService.findByCorporate()).thenReturn(templatesDto);

    // perform
    MvcResult result =
        mockMvc
            .perform(
                get(Constant.path(base, "corporate"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    // verify
    var templatesList =
        this.mapFromJsonT(
            result.getResponse().getContentAsString(), new TypeReference<List<TemplateDto>>() {});
    assertThat(templatesList).isEqualTo(templatesDto);
  }

  @Test
  @DisplayName("Get template by id api test")
  void get_template_by_id_api_test() throws Exception {
    // when
    when(this.templateService.findById(anyLong())).thenReturn(templateDto);

    // perform
    mockMvc
        .perform(
            get(Constant.path(base, "{id}"), 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    // verify
    TemplateDto templateRes = templateController.findById(1L).getBody();
    assert templateRes != null;
    assertThat(templateRes.getId()).isEqualTo(templateDto.getId());
  }

  @Test
  @DisplayName("Get template by id api test")
  void get_template_by_id_internal_api_test() throws Exception {
    // when
    when(this.templateService.findTemplateById(anyLong()))
        .thenReturn(Optional.ofNullable(templateDto));

    // perform
    mockMvc
        .perform(get(Constant.path(base, "get/{id}"), 1L).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    // verify
    var responseTemplate = Objects.requireNonNull(templateController.getById(1L).getBody());
    responseTemplate.ifPresent(dto -> assertThat(dto.getId()).isEqualTo(templateDto.getId()));
  }

  @Test
  @DisplayName("Create new template test")
  void create_new_template_test() throws Exception {
    // when
    when(this.templateService.save(any())).thenReturn(templateDto);

    // perform
    MvcResult result =
        mockMvc
            .perform(
                post(Constant.path(base))
                    .content(this.mapToJson(templateDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

    // verify
    var templateRes =
        this.mapFromJson(result.getResponse().getContentAsString(), TemplateDto.class);
    assertThat(templateRes).isEqualTo(templateDto);
  }

  @Test
  @DisplayName("Update template test")
  void update_template_test() throws Exception {
    // when
    when(this.templateService.update(any())).thenReturn(templateDto);

    // perform
    mockMvc
        .perform(
            put(Constant.path(base))
                .content(this.mapToJson(templateDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isCreated())
        .andReturn();

    // verify
    var responseTemplate = Objects.requireNonNull(templateController.update(templateDto).getBody());
    assertThat(responseTemplate.getId()).isEqualTo(templateDto.getId());
  }

  @Test
  @DisplayName("Hard delete template by id test")
  void delete_template_by_id_test() throws Exception {
    // perform
    mockMvc
        .perform(delete(Constant.path(base, "{id}"), 1L))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Get participants of template test")
  void get_participants_by_template_id_test() throws Exception {
    // given
    final List<UserParticipantDto> participantsDto = Collections.singletonList(participantDto);

    // when
    when(templateService.getParticipants(1L)).thenReturn(participantsDto);

    // then
    MvcResult result =
        mockMvc
            .perform(
                get(Constant.path(base, "participants/{templateId}"), 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // verify
    var response = templateController.getParticipants(1L).getBody();
    UserParticipantDto[] participantsRes =
        this.mapFromJson(result.getResponse().getContentAsString(), UserParticipantDto[].class);

    assert response != null;
    var participantRes = (UserParticipantDto) response.toArray()[0];
    assertThat(participantsRes[0].getTemplateId()).isEqualTo(participantRes.getTemplateId());
    assertThat(participantsRes[0].getUserId()).isEqualTo(participantRes.getUserId());
  }

  @Test
  @DisplayName("Get template types test")
  void get_template_type_test() throws Exception {
    // given
    final var types = TemplateType.types();

    // when
    when(templateService.getTemplateTypes()).thenReturn(types);

    // then

    mockMvc
        .perform(
            get(Constant.path(base, "types"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // verify
    var response = templateController.getTemplateTypes().getBody();
    assertThat(response).isNotEmpty().isEqualTo(types);
  }

  @Test
  @DisplayName("Set favorite template test")
  void set_template_to_be_favorite_test() throws Exception {
    mockMvc
        .perform(
            put(Constant.path(base, "favorite/{id}"), 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Increase template used test")
  void increase_template_used_test() throws Exception {
    mockMvc
        .perform(
            put(Constant.path(base, "increase-used/{id}"), 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Get user favorite templates test")
  void get_user_favorite_templates_test() throws Exception {
    // given
    final var entityResponseHandler = new EntityResponseHandler<TemplateDto>();
    entityResponseHandler.setContents(Collections.singletonList(templateDto));

    // when
    when(templateService.getUserFavoriteTemplates(any(Pageable.class), anyString()))
        .thenReturn(entityResponseHandler);

    // then
    MvcResult result =
        mockMvc
            .perform(
                get(Constant.path(base, "favorite"))
                    .param(search, templateName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    // verify
    var response =
        templateController
            .getUserFavoriteTemplates(1, 5, "usedCount", "desc", templateName)
            .getBody();

    var responseHandler =
        this.mapFromJsonT(
            result.getResponse().getContentAsString(),
            new TypeReference<EntityResponseHandler<TemplateDto>>() {});

    assertThat(response).isNotNull();
    assertThat(responseHandler.getContents().get(0).getId())
        .isEqualTo(response.getContents().get(0).getId());
  }
}
