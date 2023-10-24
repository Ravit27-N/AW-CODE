package com.innovationandtrust.corporate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.corporate.model.dto.BusinessUnitDto;
import java.util.List;

import com.innovationandtrust.share.utils.EntityResponseHandler;
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
class BusinessUnitServiceTests {
  @Mock BusinessUnitService service;
  @Mock
  BusinessUnitDto dto;
  @Mock List<BusinessUnitDto> businessUnitDtoList;
  @Mock Page<BusinessUnitDto> businessUnitDTOListResPage;

  @BeforeEach
  void setup() {
    dto = new BusinessUnitDto();
    dto.setCompanyId(1L);
    dto.setParentId(1L);
    dto.setUnitName("IT");
  }

  @Test
  @DisplayName("Find all business unit test")
  void find_all_business_unit_test() {
    // when
    when(service.findAll()).thenReturn(businessUnitDtoList);
    when(service.findAll().size()).thenReturn(3);

    var result = service.findAll();

    // then
    assertThat(result).hasSize(3);
    verify(service, times(2)).findAll();
  }

  @Test
  @DisplayName("Find all business unit as pagination test")
  void find_all_business_unit_as_pagination_test() {
    Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
    // when
    when(service.findAll(paging, "", 1L)).thenReturn(businessUnitDTOListResPage);
    when(service.findAll(paging, "", 1L).getTotalPages()).thenReturn(10);

    var result = service.findAll(paging, "", 1L);

    // then
    assertThat(result).isNotNull();
    assertEquals(10, result.getTotalPages());
    verify(service, times(2)).findAll(paging, "", 1L);
  }

  @Test
  @DisplayName("Create business unit test")
  void create_business_unit_test() {
    // when
    when(service.create(dto)).thenReturn(dto);

    var result = service.create(dto);

    // then
    assertThat(result).isNotNull();
    assertEquals(dto.getId(), result.getId());
    verify(service, times(1)).create(dto);
  }

  @Test
  @DisplayName("Update business unit test")
  void update_business_unit_test() {
    // given
    dto = new BusinessUnitDto();
    dto.setCompanyId(1L);
    dto.setParentId(1L);
    dto.setUnitName("Design");

    // when
    when(service.update(dto)).thenReturn(dto);

    var result = service.update(dto);

    // then
    assertThat(result).isNotNull();
    assertEquals("Design", result.getUnitName());
    verify(service, times(1)).update(dto);
  }

  @Nested
  @DisplayName("Find business unit by id test")
  class FindBusinessUnitByIdTest {
    @Test
    @DisplayName("When business unit with given id is found in database")
    void find_business_unit_by_id_test() {
      // when
      when(service.findById(1L)).thenReturn(dto);

      var result = service.findById(1L);

      // then
      assertThat(result).isNotNull();
      assertEquals(dto.getCompanyId(), result.getCompanyId());
      verify(service, times(1)).findById(1L);
    }
  }
}
