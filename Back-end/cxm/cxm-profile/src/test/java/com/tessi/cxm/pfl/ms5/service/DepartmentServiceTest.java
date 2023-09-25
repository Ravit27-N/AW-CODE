package com.tessi.cxm.pfl.ms5.service;

import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.USER;
import static com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants.USER_ENTITY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.DivisionDto;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.exception.DepartmentNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.DivisionNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.DivisionRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
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
class DepartmentServiceTest {
  @Mock private KeycloakService keycloakService;
  @Mock private DivisionRepository divisionRepository;
  @Mock private DepartmentRepository departmentRepository;
  @Mock private DivisionService divisionService;
  @Mock private UserRepository userRepository;
  @Mock private ProfileService profileService;
  private DepartmentService departmentService;
  private final ModelMapper modelMapper = new ModelMapper();

  @BeforeEach
  void beforeEach() {
    this.departmentService =
        new DepartmentService(
            departmentRepository,
            divisionRepository,
            modelMapper,
            divisionService,
            userRepository,
            profileService);
    this.departmentService.setKeycloakService(this.keycloakService);
  }

  @Test
  @Order(1)
  void testCreateDepartment() {
    when(departmentRepository.findById(anyLong()))
        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_DEPARTMENT));
    when(divisionService.findEntity(anyLong()))
        .thenReturn(ProfileUnitTestConstants.SAMPLE_DIVISION);
    when(departmentRepository.save(any(Department.class)))
        .thenReturn(ProfileUnitTestConstants.SAMPLE_DEPARTMENT);
    var result = departmentService.save(ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO);
    Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
  }

  @Test
  @Order(2)
  void testCreateDepartmentWithDivisionNotFound() {
    when(departmentRepository.findById(anyLong()))
        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_DEPARTMENT));
    when(divisionService.findEntity(anyLong())).thenThrow(new DivisionNotFoundException(1));
    var divisionNotFoundException =
        Assertions.assertThrows(
            DivisionNotFoundException.class,
            () -> departmentService.save(ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO));
    log.info("Error : {}", divisionNotFoundException.getMessage());
  }

  @Test
  @Order(3)
  void testUpdateDepartment() {
    when(departmentRepository.findById(anyLong()))
        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_DEPARTMENT));
    when(departmentRepository.save(any(Department.class)))
        .thenReturn(ProfileUnitTestConstants.SAMPLE_DEPARTMENT);
    var result = departmentService.update(ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO);
    Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
  }

  @Test
  @Order(4)
  void testUpdateDepartmentNotFound() {
    var departmentNotFoundException =
        Assertions.assertThrows(
            DepartmentNotFoundException.class,
            () -> departmentService.update(ProfileUnitTestConstants.SAMPLE_DEPARTMENT_DTO));
    log.info("Error : {}", departmentNotFoundException.getMessage());
  }

  @Test
  @Order(5)
  void testDelete() {
    when(departmentRepository.findById(anyLong()))
        .thenReturn(Optional.of(ProfileUnitTestConstants.SAMPLE_DEPARTMENT));
    departmentService.delete(1L);
    verify(departmentRepository, times(1)).delete(any());
  }

  @Test
  @Order(6)
  void testDeleteNotFound() {
    var departmentNotFoundException =
        Assertions.assertThrows(
            DepartmentNotFoundException.class, () -> departmentService.delete(1L));
    log.info("Error : {}", departmentNotFoundException.getMessage());
  }

  @Test
  @Order(7)
  void testGetAll() {
    when(departmentRepository.findAll())
        .thenReturn(List.of(ProfileUnitTestConstants.SAMPLE_DEPARTMENT));
    var result = departmentService.findAll();
    Assertions.assertNotNull(result, ProfileUnitTestConstants.SMG);
  }

  @Test
  @Order(8)
  void testGetAllDepartmentInClientList() {
    // Mock the dependencies of the method
    Optional<UserEntity> userEntity = Optional.of(USER_ENTITY);

    when(this.keycloakService.getUserInfo()).thenReturn(USER);
    when(this.userRepository.findByTechnicalRefAndIsActiveTrue(anyString())).thenReturn(userEntity);
    when(departmentRepository.findAll(ArgumentMatchers.<Specification<Department>>any()))
        .thenReturn(List.of(ProfileUnitTestConstants.SAMPLE_DEPARTMENT));

    // Call the method under test
    List<DepartmentDto> departmentDtos = departmentService.getAllServicesInClientList();

    // Assert the result
    verify(departmentRepository, times(1))
        .findAll(ArgumentMatchers.<Specification<Department>>any());
  }

  @Test
  @Order(9)
  void testGetAllDivisionInClientList() {
    // Mock the dependencies of the method
    Optional<UserEntity> userEntity = Optional.of(USER_ENTITY);

    when(this.keycloakService.getUserInfo()).thenReturn(USER);
    when(this.userRepository.findByTechnicalRefAndIsActiveTrue(anyString())).thenReturn(userEntity);
    when(divisionRepository.findAll(ArgumentMatchers.<Specification<Division>>any()))
        .thenReturn(List.of(ProfileUnitTestConstants.SAMPLE_DIVISION));

    // Call the method under test
    List<DivisionDto> divisionDtos = departmentService.getAllDivisionInClientList();

    // Assert the result
    verify(divisionRepository, times(1)).findAll(ArgumentMatchers.<Specification<Division>>any());
  }
}
