package com.innovationandtrust.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.profile.model.dto.CorporateUserDto;
import com.innovationandtrust.profile.repository.CompanyRepository;
import com.innovationandtrust.profile.repository.UserActivityRepository;
import com.innovationandtrust.profile.service.restclient.EmployeeFeignClient;
import com.innovationandtrust.profile.service.restclient.ProjectFeignClient;
import com.innovationandtrust.profile.service.restclient.SftpFeignClient;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class CorporateUserServiceTests {
    @Mock
    CorporateUserDto dto;
    private CorporateUserService corporateUserService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private EmployeeFeignClient employeeFeignClient;

    @Mock
    private SftpFeignClient sftpFeignClient;

    @Mock
    CompanyRepository companyRepository;
    @Mock
    private CorporateProfileFeignClient corporateProfileFeignClient;
    @Mock
    private MailService mailService;
    @Mock
    private ProjectFeignClient projectFeignClient;
    @Mock
    private UserActivityRepository activityRepository;

    @BeforeEach
    void setup() {
        dto = new CorporateUserDto();
        dto.setCompanyId(1L);

        corporateUserService =
                spy(
                        new CorporateUserService(
                                userService,
                                modelMapper, employeeFeignClient,
                                sftpFeignClient,
                                companyRepository,
                                mailService,
                                corporateProfileFeignClient,
                                projectFeignClient, activityRepository));
    }

    @Test
    @DisplayName("Save corporate user test")
    void save_corporate_user_test() {
        // when
        when(corporateUserService.save(dto)).thenReturn(dto);

        CorporateUserDto result = corporateUserService.save(dto);

        // then
        assertThat(result).isNotNull();
        verify(corporateUserService, times(1)).save(dto);
    }

    @Test
    @DisplayName("Update corporate user test")
    void update_corporate_user_test() {
        // given
        dto = new CorporateUserDto();
        dto.setCompanyId(2L);
        // when
        when(corporateUserService.update(dto)).thenReturn(dto);

        CorporateUserDto result = corporateUserService.update(dto);

        // then
        assertThat(result).isNotNull();
        assertEquals(2, result.getCompanyId());
        verify(corporateUserService, times(1)).update(dto);
    }

    @Test
    @DisplayName("Find author test")
    void find_author_test() {
        // when
        when(corporateUserService.findAuthor()).thenReturn(dto);

        var result = corporateUserService.findAuthor();

        // then
        assertThat(result).isNotNull();
        verify(corporateUserService, times(1)).findAuthor();
    }

    @Nested
    @DisplayName("Find by id test")
    class FindByIdTest {
        @Test
        @DisplayName("When corporate user with the given id is found in database")
        void find_by_id_test() {
            dto = new CorporateUserDto();
            dto.setCompanyId(2L);
            ReflectionTestUtils.setField(corporateUserService, "modelMapper", modelMapper);
            // when
            when(corporateUserService.findById(1L)).thenReturn(dto);

            CorporateUserDto result = corporateUserService.findById(1L);

            // then
            assertThat(result).isNotNull();
            verify(corporateUserService, times(1)).findById(1L);
        }
    }
}
