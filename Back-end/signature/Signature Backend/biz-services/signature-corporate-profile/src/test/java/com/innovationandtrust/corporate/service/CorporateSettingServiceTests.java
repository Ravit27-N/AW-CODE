package com.innovationandtrust.corporate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CorporateSettingServiceTests {
  @Mock CorporateSettingService service;
  @Mock
  CorporateSettingDto corporateSettingDTO;
  @Mock Page<CorporateSettingDto> corporateSettingDTOPage;

  @BeforeEach
  void setup() {
    corporateSettingDTO = new CorporateSettingDto();
    corporateSettingDTO.setId(1L);
    corporateSettingDTO.setLogo("logo_test.png");
    corporateSettingDTO.setCompanyId(1L);
    corporateSettingDTO.setLinkColor("#000000");
    corporateSettingDTO.setMainColor("#000000");
    corporateSettingDTO.setSecondaryColor("#000000");
  }

  @Nested
  @DisplayName("Get corporate setting detail by id test")
  class GetCorporateSettingDetailById {
    @Test
    @DisplayName("When corporate setting detail with given id is found in database")
    void get_company_detail_by_id() {
      //when
      when(service.findById(1L)).thenReturn(corporateSettingDTO);

      var result = service.findById(1L);

      //then
      assertThat(service.findById(1L)).isEqualTo(corporateSettingDTO);
      assertEquals("logo_test.png", result.getLogo());
      verify(service, times(2)).findById(1L);
    }

    @Test
    @DisplayName("Get all corporate setting test")
    void get_all_corporate_setting_test() {
      Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
      when(service.findAll(paging)).thenReturn(corporateSettingDTOPage);
      when(service.findAll(paging).getTotalPages()).thenReturn(3);

      var result = service.findAll(paging);

      //then
      assertThat(service.findAll(paging)).isEqualTo(corporateSettingDTOPage);
      assertEquals(3, result.getTotalPages());
      verify(service, times(3)).findAll(paging);
    }

    @Test
    @DisplayName("Save corporate setting test")
    void save_corporate_setting_test() {
      //when
      when(service.save(corporateSettingDTO)).thenReturn(corporateSettingDTO);

      var result = service.save(corporateSettingDTO);

      //then
      assertThat(service.save(corporateSettingDTO)).isEqualTo(corporateSettingDTO);
      assertEquals("logo_test.png", result.getLogo());
      verify(service, times(2)).save(corporateSettingDTO);
    }

    @Test
    @DisplayName("Update corporate setting test")
    void update_company_detail_test() {
      corporateSettingDTO.setLogo("logo_test_updated.png");
      //when
      when(service.update(corporateSettingDTO)).thenReturn(corporateSettingDTO);

      var result = service.update(corporateSettingDTO);

      //then
      assertThat(service.update(corporateSettingDTO)).isEqualTo(corporateSettingDTO);
      assertEquals("logo_test_updated.png", result.getLogo());
      verify(service, times(2)).update(corporateSettingDTO);
    }
  }
}
