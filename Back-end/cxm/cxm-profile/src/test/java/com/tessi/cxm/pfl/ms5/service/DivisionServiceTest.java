package com.tessi.cxm.pfl.ms5.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.DivisionNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.DivisionRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DivisionServiceTest {

  @Mock
  private DivisionRepository divisionRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ClientService clientService;
  private final ModelMapper modelMapper = new ModelMapper();
  private DivisionService divisionService;

  @BeforeEach
  void beforeAll() {
    this.divisionService =
        new DivisionService(divisionRepository, userRepository, modelMapper, clientService);
  }

  @Test
  @Order(1)
  void testCreateDivision() {
    when(clientService.findEntity(anyLong())).thenReturn(ProfileUnitTestConstants.SAMPLE_CLIENT_1);
    when(divisionRepository.save(any(Division.class))).thenReturn(
        ProfileUnitTestConstants.SAMPLE_DIVISION);
    var result = divisionService.save(ProfileUnitTestConstants.SAMPLE_DIVISION_DTO);
    Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
  }

  @Test
  @Order(2)
  void testCreateDivisionWithClientNotFound() {
    when(clientService.findEntity(anyLong())).thenThrow(new ClientNotFoundException(1));
    var clientNotFoundException =
        Assertions.assertThrows(
            ClientNotFoundException.class,
            () -> divisionService.save(ProfileUnitTestConstants.SAMPLE_DIVISION_DTO));
    log.info("Message Error :{}", clientNotFoundException.getMessage());
  }

  @Test
  @Order(3)
  void testUpdateDivision() {
    when(divisionRepository.save(any(Division.class))).thenReturn(
        ProfileUnitTestConstants.SAMPLE_DIVISION);
    var result = divisionService.save(ProfileUnitTestConstants.SAMPLE_DIVISION_DTO);
    Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
  }

  @Test
  @Order(4)
  void testUpdateDivisionNotFound() {
    when(divisionRepository.findById(anyLong())).thenThrow(new DivisionNotFoundException(1));
    var divisionNotFoundException =
        Assertions.assertThrows(
            DivisionNotFoundException.class,
            () -> divisionService.update(ProfileUnitTestConstants.SAMPLE_DIVISION_DTO));
    log.info("Message Error :{}", divisionNotFoundException.getMessage());
  }

  @Test
  @Order(5)
  void testDeleteDivision() {
    when(divisionRepository.findById(anyLong()))
        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_DIVISION));

    Assertions.assertDoesNotThrow(() -> divisionService.delete(1L));
  }

  @Test
  @Order(6)
  void testDeleteDivisionNotFound() {
    when(divisionRepository.findById(anyLong())).thenThrow(new DivisionNotFoundException(1));
    var divisionNotFoundException =
        Assertions.assertThrows(DivisionNotFoundException.class, () -> divisionService.delete(1L));
    log.info("Message Error :{}", divisionNotFoundException.getMessage());
  }

  @Test
  @Order(7)
  void testGetAllDivision() {
    when(divisionRepository.findAll()).thenReturn(
        List.of(ProfileUnitTestConstants.SAMPLE_DIVISION));
    var result = divisionService.findAll();
    Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
  }

  @Test
  @Order(8)
  void successOnCheckIfDivisionIsDuplicated() {
    var clientId = 1L;
    var divisionId = 0L;

    when(divisionRepository.findOne(ArgumentMatchers.<Specification<Division>>any())).thenReturn(
        Optional.of(ProfileUnitTestConstants.SAMPLE_DIVISION));

    var result = divisionService.validateDuplicateName(divisionId, clientId,
        ProfileUnitTestConstants.SAMPLE_DIVISION.getName());

    Assertions.assertTrue(result);
  }

  @Test
  @Order(9)
  void successOnCheckDivisionIsNotDuplicated() {
    var clientId = 1L;
    var divisionId = 1L;

    when(divisionRepository.findById(divisionId)).thenReturn(
        Optional.of(ProfileUnitTestConstants.SAMPLE_DIVISION));

    var result = divisionService.validateDuplicateName(divisionId, clientId,
        ProfileUnitTestConstants.SAMPLE_DIVISION.getName());

    Assertions.assertFalse(result);
  }

  @Test
  @Order(10)
  void successOnCheckIfDivisionIsNotDuplicated() {
    var clientId = 1L;
    var divisionId = 1L;

    when(divisionRepository.findById(divisionId)).thenReturn(
        Optional.of(ProfileUnitTestConstants.SAMPLE_DIVISION));
    when(divisionRepository.findOne(ArgumentMatchers.<Specification<Division>>any())).thenReturn(
        Optional.empty());

    var result = divisionService.validateDuplicateName(divisionId, clientId, "Division 2");

    Assertions.assertFalse(result);
  }

  @Test
  @Order(11)
  void failedOnCheckIfDivisionIsDuplicated() {
    var clientId = 1L;
    var divisionId = 1L;

    when(divisionRepository.findById(divisionId)).thenReturn(Optional.empty());

    Assertions.assertThrows(DivisionNotFoundException.class, () ->
        divisionService.validateDuplicateName(divisionId, clientId, "Division 2"));
  }
}
