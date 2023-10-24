package com.innovationandtrust.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.project.model.dto.SignatoryDto;
import com.innovationandtrust.project.model.entity.Signatory;
import com.innovationandtrust.project.repository.SignatoryRepository;
import com.innovationandtrust.share.constant.InvitationStatus;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class SignatoryServiceTests {
  @Mock SignatoryService signatoryService;
  @Mock
  SignatoryRepository signatoryRepository;
  @Mock Signatory signatory;
  @Mock
  SignatoryDto dto;
  @Mock List<SignatoryDto> signatoryDtos;

  @BeforeEach
  public void setup() {
    signatory = new Signatory();
    signatory.setId(1L);
    signatory.setFirstName("Sok");
    signatory.setLastName("Panharith");
    signatory.setRole("sign");
    signatory.setEmail("sokpanharith.dev@gmail.com");
    signatory.setPhone("085123123");
    signatory.setSortOrder(1);

    dto = new SignatoryDto();
    dto.setId(1L);
    dto.setFirstName("Sok");
    dto.setLastName("Panharith");
    dto.setRole("signatory");
    dto.setEmail("sokpanharith.dev@gmail.com");
    dto.setPhone("085123123");
    dto.setSortOrder(1);
  }

  @DisplayName("Get all signatories")
  @Test
  void get_all_signatories() {
    // when
    when(signatoryDtos.size()).thenReturn(5);
    when(signatoryService.findAll()).thenReturn(signatoryDtos);

    List<SignatoryDto> result = signatoryService.findAll();

    // then
    assertEquals(5, result.size());
    verify(signatoryService, times(1)).findAll();
  }

  @DisplayName("Save signatory")
  @Test
  void save_signatory() {
    // when
    when(signatoryService.save(dto)).thenReturn(dto);

    SignatoryDto result = signatoryService.save(dto);

    // then
    assertThat(result).isNotNull();
    assertEquals(dto.getFirstName(), result.getFirstName());
    verify(signatoryService, times(1)).save(dto);
  }

  @DisplayName("Save all signatories")
  @Test
  void save_all_signatories() {
    // when
    when(signatoryService.saveAll(signatoryDtos)).thenReturn(signatoryDtos);
    when(signatoryDtos.size()).thenReturn(5);

    List<SignatoryDto> result = signatoryService.saveAll(signatoryDtos);

    // then
    assertEquals(5, signatoryDtos.size());
    assertEquals(result, signatoryDtos);
    verify(signatoryService, times(1)).saveAll(signatoryDtos);
  }

  @Nested
  @DisplayName("Get signatory by ID")
  class GetSignatoryById {
    @DisplayName("When signatory is found in database, then query it")
    @Test
    void get_signatory_by_id() {
      // when
      when(signatoryService.findById(1L)).thenReturn(dto);
      when(signatoryRepository.findById(1L)).thenReturn(Optional.of(signatory));

      SignatoryDto signatoryDTO = signatoryService.findById(1L);
      Optional<Signatory> result = signatoryRepository.findById(1L);

      // then
      assertThat(signatoryDTO).isNotNull();
      assertThat(signatoryDTO.getFirstName())
          .isEqualTo(result.isPresent() ? result.get().getFirstName() : "");
      verify(signatoryService, times(1)).findById(1L);
    }
  }

  @Nested
  @DisplayName("Get all signatories by project id")
  class GetAllSignatoriesByProjectId {
    @DisplayName("When project id is found, then query all signatories in that project")
    @Test
    void get_all_signatories_by_project_id() {
      // given
      var projectId = 168L;
      // when
      when(signatoryService.findAllByProjectId(projectId)).thenReturn(signatoryDtos);
      when(signatoryDtos.size()).thenReturn(3);

      List<SignatoryDto> result = signatoryService.findAllByProjectId(168L);

      // then
      assertEquals(3, result.size());
      verify(signatoryService, times(1)).findAllByProjectId(168L);
    }
  }

  @Nested
  @DisplayName("Update signatory")
  class UpdateSignatory {
    @DisplayName("When signatory is found in database, then update it")
    @Test
    void update_signatory() {
      // given
      SignatoryDto signatoryDTO = new SignatoryDto();
      signatoryDTO.setId(1L);
      signatoryDTO.setFirstName("Sok");
      signatoryDTO.setLastName("Panharith");
      signatoryDTO.setRole(RoleConstant.ROLE_SIGNATORY);
      signatoryDTO.setEmail("sokpanharith.dev@gmail.com");
      signatoryDTO.setPhone("085123123");
      signatoryDTO.setSortOrder(1);

      // when
      when(mock(signatoryDTO.getClass()).getId() == 0L)
          .thenThrow(new EntityNotFoundException("Signatory with this id is not exist!"));
      when(signatoryService.update(signatoryDTO)).thenReturn(signatoryDTO);

      SignatoryDto result = signatoryService.update(signatoryDTO);

      // then
      assertThat(result).isNotNull();
      assertEquals(signatoryDTO.getFirstName(), result.getFirstName());
      verify(signatoryService, times(1)).update(dto);
    }
  }

  @Nested
  @DisplayName("Delete signatory")
  class DeleteSignatory {
    @DisplayName("When signatory is found in database, then delete it")
    @Test
    void delete_signatory() {
      signatoryService.delete(1L);

      // then
      verify(signatoryService, times(1)).delete(1L);
    }
  }

  @Nested
  @DisplayName("Update signatory invitation status")
  class UpdateInvitationStatus {
    @DisplayName("When signature is found in database, then update the invitation status")
    @Test
    void update_signatory_invitation_status() {
      var signatories = List.of(new SignatoryRequest(1L, InvitationStatus.SIGNED));
      signatoryService.updateStatus(1L, signatories);

      // then
      verify(signatoryService, times(1)).updateStatus(1L, signatories);
    }
  }

  @Nested
  @DisplayName("Get signatory entity by id")
  class GetSignatoryEntityById {
    @DisplayName("When signatory with the given id is exist, then query it")
    @Test
    void get_signatory_entity_by_id() {
      // when
      when(signatoryService.findEntityById(1L)).thenReturn(signatory);
      when(signatoryRepository.findById(1L)).thenReturn(Optional.of(signatory));

      Signatory result = signatoryService.findEntityById(1L);

      // then
      assertThat(result).isNotNull();
      assertThat(signatory.getFirstName()).isEqualTo(result.getFirstName());
      verify(signatoryService, times(1)).findEntityById(1L);
    }
  }
}
