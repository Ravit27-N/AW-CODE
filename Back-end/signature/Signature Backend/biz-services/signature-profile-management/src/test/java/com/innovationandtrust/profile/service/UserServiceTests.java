package com.innovationandtrust.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.profile.model.dto.UserDto;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.repository.CompanyRepository;
import com.innovationandtrust.profile.repository.UserActivityRepository;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.profile.service.spefication.UserSpec;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserRequest;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserResponse;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
class UserServiceTests {
  @Mock
  UserDto dto;
  @Mock User user;
  @Mock KeycloakUserRequest keycloakUserRequest;
  @Mock KeycloakUserResponse keycloakUserResponse;
  @Mock List<UserDto> userDtos;
  @Mock Page<UserDto> userDTOPage;
  private UserService service;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private UserRepository repository;
  @Mock private ModelMapper modelMapper;
  @Mock private RoleService roleService;
  @Mock private CompanyRepository companyRepository;
    @Mock
    private UserActivityRepository activityRepository;
  @BeforeEach
  void setup() {
    user = new User();
    user.setId(1L);
    user.setFirstName("Her");
    user.setLastName("Man");
    user.setEmail("herman@gmail.com");
    user.setCompanyId(1L);

    dto = new UserDto();
    dto.setId(1L);
    dto.setFirstName("Her");
    dto.setLastName("Man");
    dto.setEmail("herman@gmail.com");
    dto.setRoles(Collections.singleton("Admin"));
    dto.setPassword("123");
    dto.setCompanyId(1L);

    keycloakUserRequest =
        KeycloakUserRequest.builder()
            .id("123456")
            .email(dto.getEmail())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .password(dto.getPassword())
            .build();

    service = spy(new UserService(repository, modelMapper,activityRepository, roleService, keycloakProvider, companyRepository));
  }

  @Test
  @DisplayName("Save user test")
  void save_user_test() {
    // when
    when(keycloakProvider.createUser(keycloakUserRequest)).thenReturn(keycloakUserResponse);
    when(repository.save(user)).thenReturn(user);
    when(service.save(dto)).thenReturn(dto);

    var result = service.save(dto);

    // then
    assertThat(result).isNotNull();
    assertEquals("Her", result.getFirstName());
    verify(service, times(1)).save(dto);
  }

  @Test
  @DisplayName("Update user test")
  void update_user_test() {
    // given
    dto = new UserDto();
    dto.setId(1L);
    dto.setFirstName("Sok");
    dto.setLastName("Man");
    dto.setEmail("sokpan@gmail.com");
    dto.setRoles(Collections.singleton("Admin"));
    dto.setPassword("123");
    dto.setCompanyId(1L);
    // when
    when(service.update(dto)).thenReturn(dto);

    var result = service.update(dto);

    // then
    assertThat(result).isNotNull();
    assertEquals("sokpan@gmail.com", result.getEmail());
    verify(service, times(1)).update(dto);
  }

  @Test
  @DisplayName("Find all user test")
  void find_all_user_test() {
    // when
    when(service.findAll()).thenReturn(userDtos);
    when(service.findAll().size()).thenReturn(3);

    var result = service.findAll();

    // then
    assertEquals(3, result.size());
    verify(service, times(2)).findAll();
  }

  @Test
  @DisplayName("Find all user pagination test")
  void find_all_user_pagination_test() {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    // when
    when(service.findAll(paging, "")).thenReturn(userDTOPage);
    when(service.findAll(paging, "").getTotalPages()).thenReturn(3);

    var result = service.findAll(paging, "");

    // then
    assertEquals(3, result.getTotalPages());
    verify(service, times(2)).findAll(paging, "");
  }

  @Test
  @DisplayName("Check if user exist")
  void check_if_user_exist() {
    // given
    var specification = Specification.where(UserSpec.findByEmail("herman@gmail.com"));
    // when
    when(repository.findOne(specification)).thenReturn(Optional.ofNullable(user));
    when(service.isUserExist("herman@gmail.com")).thenReturn(true);

    var result = service.isUserExist("herman@gmail.com");
    var OptionalUser = repository.findOne(specification);

    // then
    assertThat(result).isTrue();
    assertTrue(OptionalUser.isPresent());
    verify(service, times(1)).isUserExist("herman@gmail.com");
  }

  @DisplayName("Find user by id test")
  @Nested
  class FindUserByIdTest {
    @Test
    @DisplayName("When user with given id is found in database")
    void find_user_by_id_test() {
      // when
      when(repository.findById(1L)).thenReturn(Optional.ofNullable(user));
      when(service.findById(1L)).thenReturn(dto);

      var result = service.findById(1L);
      var user = repository.findById(1L);

      // then
      assertThat(user).isNotNull();
      assertEquals("Her", result.getFirstName());
      verify(service, times(1)).findById(1L);
    }
  }
}
