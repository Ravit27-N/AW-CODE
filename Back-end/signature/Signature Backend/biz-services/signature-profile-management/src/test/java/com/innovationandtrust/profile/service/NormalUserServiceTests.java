package com.innovationandtrust.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.profile.repository.TemplateRepository;
import com.innovationandtrust.profile.repository.UserActivityRepository;
import com.innovationandtrust.utils.exception.exceptions.KeycloakException;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import com.innovationandtrust.profile.model.dto.UserDto;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.profile.service.restclient.BusinessUnitsFeignClient;
import com.innovationandtrust.profile.service.restclient.EmployeeFeignClient;
import com.innovationandtrust.profile.service.restclient.ProjectFeignClient;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class NormalUserServiceTests {
  @Mock
  NormalUserDto dto;
  @Mock Page<NormalUserDto> normalUserDTOS;
  @Mock
  UserDto userDTO;
  private NormalUserService normalUserService;
  @Mock private ModelMapper modelMapper;
  @Mock private UserRepository repository;
  @Mock private UserService userService;
  @Mock private EmployeeFeignClient employeeFeignClient;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  @Mock private BusinessUnitsFeignClient businessUnitsFeignClient;
  @Mock private ProjectFeignClient projectFeignClient;
  @Mock private IKeycloakProvider keycloakProvider;
  @Mock private CompanyService companyService;
  @Mock private MailService mailService;
@Mock private TemplateService templateService;
@Mock private UserActivityRepository activityRepository;
@Mock private TemplateRepository templateRepository;
  @BeforeEach
  void setup() {
    dto = new NormalUserDto();
    dto.setId(1L);
    dto.setFirstName("Her");
    dto.setLastName("Man");
    dto.setEmail("herman@gmail.com");
    dto.setBusinessId(1L);

    normalUserService =
        spy(
            new NormalUserService(
                userService,
                modelMapper,
                employeeFeignClient,
                corporateProfileFeignClient,
                businessUnitsFeignClient,
                companyService,
                mailService,
                projectFeignClient,
                keycloakProvider, repository, activityRepository, templateService));
  }

  @Test
  @DisplayName("Update normal user test")
  void update_normal_user_test() {
    // given
    var data = new NormalUserDto();
    data.setId(1L);
    data.setFirstName("Sok");
    data.setLastName("Man");
    data.setEmail("sokman@gmail.com");
    data.setBusinessId(2L);

    // when
    when(normalUserService.update(data)).thenReturn(data);

    var result = normalUserService.update(data);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(data.getEmail());
    verify(normalUserService, times(1)).update(data);
  }

  @Test
  @DisplayName("Find all normal user test")
  void find_all_normal_user_test() {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    // when
    when(normalUserService.findAll(paging, "")).thenReturn(normalUserDTOS);
    when(normalUserService.findAll(paging, "").getTotalPages()).thenReturn(3);

    Page<NormalUserDto> result = normalUserService.findAll(paging, "");

    // then
    assertEquals(3, result.getTotalPages());
    verify(normalUserService, times(2)).findAll(paging, "");
  }

  @Nested
  @DisplayName("Save normal user service test")
  class SaveNormalUserServiceTest {
    @Test
    @DisplayName("Save normal user test")
    void save_normal_user_test() {
      ReflectionTestUtils.setField(normalUserService, "modelMapper", modelMapper);
      // when
      when(normalUserService.save(dto)).thenReturn(dto);

      NormalUserDto result = normalUserService.save(dto);

      // then
      assertThat(result).isNotNull();
      assertEquals(result.getEmail(), dto.getEmail());
      verify(normalUserService, times(1)).save(dto);
    }

    @Test
    @DisplayName("When cannot save normal user")
    void when_cannot_save_normal_user() {
      assertThatThrownBy(() -> normalUserService.save(dto)).isInstanceOf(KeycloakException.class);
    }
  }

  @Nested
  @DisplayName("Find normal user by id")
  class FindNormalUserById {
    @Test
    @DisplayName("When normal user with given id is found in database")
    void find_normal_user_by_id() {
      // when
      when(normalUserService.findById(1L)).thenReturn(dto);

      var result = normalUserService.findById(1L);

      // then
      assertThat(result.getEmail()).isEqualTo("herman@gmail.com");
      verify(normalUserService, times(1)).findById(1L);
    }
  }

  @Nested
  @DisplayName("Find normal user with company by id")
  class FindNormalUserWithCompanyById {
    @Test
    @DisplayName("When normal user with given id is found")
    void find_with_company_by_id() {
      dto.setId(1L);
      dto.setFirstName("Her");
      dto.setLastName("Man");
      dto.setEmail("herman@gmail.com");
      dto.setBusinessId(17L);

      userDTO.setId(1L);
      userDTO.setFirstName("Her");
      userDTO.setLastName("Man");
      userDTO.setEmail("herman@gmail.com");
      userDTO.setCompanyId(21L);
      userDTO.setActive(true);
      // when
      when(userService.findById(1L)).thenReturn(userDTO);
      when(normalUserService.findWithCompanyById(21L)).thenReturn(dto);
      ReflectionTestUtils.setField(normalUserService, "modelMapper", modelMapper);

      NormalUserDto result = normalUserService.findWithCompanyById(21L);

      // then
      assertThat(result.getEmail()).isEqualTo("herman@gmail.com");
      verify(normalUserService, times(1)).findWithCompanyById(21L);
    }
  }
}
