package com.innovationandtrust.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.profile.model.dto.CompanyDto;
import com.innovationandtrust.profile.model.dto.CorporateUserDto;
import com.innovationandtrust.profile.model.entity.Company;
import com.innovationandtrust.profile.repository.CompanyRepository;
import com.innovationandtrust.profile.repository.CompanySettingRepository;
import com.innovationandtrust.profile.service.restclient.CompanyDetailFeignClient;
import com.innovationandtrust.profile.service.spefication.CompanySpec;
import com.innovationandtrust.share.model.profile.CompanyIdListDTO;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserResponse;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
class CompanyServiceTests {
  @Mock List<CompanyDto> companyDTOList;
  @Mock CorporateUserDto corporateUserDTO;
  @Mock CompanyDto companyDTO;
  @Mock Company company;
  @Mock Page<CompanyDto> companyDTOPage;
  @Mock List<Company> companies;
  @Mock KeycloakUserResponse keycloakUserResponse;
  private CompanyService companyService;
  @Mock private CorporateUserService corporateUserService;
  @Mock private CompanyRepository repository;
  @Mock private ModelMapper modelMapper;
  @Mock private IKeycloakProvider keycloakProvider;
  @Mock private CompanyDetailFeignClient detailFeignClient;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  @Mock private UserService userService;
  private CommonCrudService<Company, CompanyDto, Long> commonCrudService;

  @Mock private CompanySettingRepository companySettingRepository;

  @BeforeEach
  void setup() {
    companyDTO = new CompanyDto();
    companyDTO.setId(1L);
    companyDTO.setName("HERMAN");
    companyDTO.setSiret("123");

    commonCrudService = spy(new CommonCrudService<>(modelMapper, keycloakProvider));

    companyService =
        spy(
            new CompanyService(
                modelMapper,
                keycloakProvider,
                repository,
                detailFeignClient,
                corporateProfileFeignClient,
                corporateUserService,
                companySettingRepository,
                userService));
  }

  @Test
  @DisplayName("Save company test")
  void save_company_test() {
    // when
    when(companyService.save(companyDTO)).thenReturn(companyDTO);

    CompanyDto result = companyService.save(companyDTO);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("HERMAN");
    verify(companyService, times(1)).save(companyDTO);
  }

  @Test
  @DisplayName("Update company test")
  void update_company_test() {
    companyDTO = new CompanyDto();
    companyDTO.setId(1L);
    companyDTO.setName("HERMAN");
    companyDTO.setSiret("123");
    // when
    when(repository.findById(1L)).thenReturn(Optional.ofNullable(company));
    when(commonCrudService.getUserId()).thenReturn(1L);
    when(keycloakProvider.getUserInfo("")).thenReturn(Optional.ofNullable(keycloakUserResponse));
    when(commonCrudService.getUserInfo()).thenReturn(Optional.ofNullable(keycloakUserResponse));
    when(companyService.getUserId()).thenReturn(1L);
    when(companyService.update(companyDTO)).thenReturn(companyDTO);

    CompanyDto result = companyService.update(companyDTO);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("HERMAN Co,. LTD");
    verify(companyService, times(1)).update(companyDTO);
  }

  @Test
  @DisplayName("Find company by name")
  void find_company_by_name() {
    // when
    when(repository.findOne(Specification.where(CompanySpec.findByName("AllWEB"))))
        .thenReturn(Optional.ofNullable(company));
    when(companyService.findByName("AllWEB")).thenReturn(companyDTO);

    var result = companyService.findByName("AllWEB");

    // then
    assertThat(result).isNotNull();
    assertEquals(result.getName(), companyDTO.getName());
    verify(companyService, times(1)).findByName("AllWEB");
  }

  @Test
  @DisplayName("Find all companies test")
  void find_all_companies_test() {
    // when
    when(repository.findAll()).thenReturn(companies);
    when(companyService.findAll()).thenReturn(companyDTOList);
    when(companyService.findAll().size()).thenReturn(3);

    List<CompanyDto> result = companyService.findAll();

    // then
    assertEquals(3, result.size());
    verify(companyService, times(2)).findAll();
  }

  @Test
  @DisplayName("Find all companies test in pagination")
  void find_all_companies_in_pagination_test() {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());

    // when
    when(companyService.findAll(paging, "")).thenReturn(companyDTOPage);
    when(companyService.findAll(paging, "").getTotalPages()).thenReturn(10);

    Page<CompanyDto> result = companyService.findAll(paging, "");

    // then
    assertThat(result).isNotNull();
    assertEquals(10, result.getTotalPages());
    verify(companyService, times(2)).findAll(paging, "");
  }

  @Test
  @DisplayName("List all companies")
  void list_all_companies() {
    // when
    when(companyService.listAll()).thenReturn(companyDTOList);
    when(companyService.listAll().size()).thenReturn(10);

    var companyIds = new CompanyIdListDTO();
    companyIds.setCompanyIds(companyDTOList.stream().map(CompanyDto::getId).toList());
    companyService.loopAndSetData(companyIds, companyDTOList);

    var result = companyService.listAll();

    // then
    assertEquals(10, result.size());
    verify(companyService, times(3)).listAll();
    verify(companyService, times(1)).loopAndSetData(companyIds, companyDTOList);
  }

  @Nested
  @DisplayName("Find company by Id test")
  class FindCompanyByIdTest {
    @Test
    @DisplayName("When company with the given id is found in database")
    void find_company_by_id_test() {
      companyDTO = new CompanyDto();
      companyDTO.setId(1L);
      companyDTO.setName("HERMAN Co,. LTD");
      companyDTO.setSiret("123");
      ReflectionTestUtils.setField(companyService, "modelMapper", modelMapper);
      // when
      when(repository.findById(1L)).thenReturn(Optional.ofNullable(company));
      when(companyService.findById(1L)).thenReturn(companyDTO);

      CompanyDto result = companyService.findById(1L);

      // then
      assertThat(result).isNotNull();
      assertEquals(result.getName(), companyDTO.getName());
      verify(companyService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Find company by id then throw exception")
    void find_company_by_id_then_throw_exception() {
      assertThatThrownBy(() -> companyService.findById(1L))
          .isInstanceOf(EntityNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("Find companies by corporate")
  class FindCompaniesByCorporate {
    @Test
    @DisplayName("When corporate with the given id is found in database")
    void find_companies_by_corporate_test() {
      // when
      when(corporateUserService.findById(1L)).thenReturn(corporateUserDTO);
      when(repository.findById(corporateUserDTO.getCompanyId()))
          .thenReturn(Optional.ofNullable(company));
      when(companyService.findByCorporateId(1L)).thenReturn(companyDTO);

      var result = companyService.findByCorporateId(1L);

      // then
      assertThat(result).isNotNull();
      assertEquals(result.getName(), companyDTO.getName());
      verify(companyService, times(1)).findByCorporateId(1L);
    }
  }
}
