package com.innovationandtrust.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.profile.model.dto.SuperAdminDto;
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
class SuperAdminUserServiceTests {
  @Mock SuperAdminUserService service;
  @Mock
  SuperAdminDto dto;

  @BeforeEach
  void setup() {
    dto = new SuperAdminDto();
    dto.setId(1L);
    dto.setEmail("superadmin@gmail.com");
    dto.setFirstName("Super");
    dto.setLastName("Admin");
    dto.setPassword("234");
  }

  @Test
  @DisplayName("Save super admin test")
  void save_super_admin_test() {
    // when
    service.createSuperAdminUser(dto);

    // then
    verify(service, times(1)).createSuperAdminUser(dto);
  }

  @Nested
  @DisplayName("Find exist user by email test")
  class FindExistUserByEmailTest {
    @Test
    @DisplayName("When user with given email exist")
    void is_user_exist() {
      // when
      when(service.isUserExist("superadmin@gmail.com")).thenReturn(true);

      Boolean bool = service.isUserExist("superadmin@gmail.com");

      // then
      assertEquals(true, bool);
      verify(service, times(1)).isUserExist("superadmin@gmail.com");
    }
  }
}
