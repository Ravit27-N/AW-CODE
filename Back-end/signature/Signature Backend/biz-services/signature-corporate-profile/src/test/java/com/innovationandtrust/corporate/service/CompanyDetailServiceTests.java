package com.innovationandtrust.corporate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.share.model.corporateprofile.CompanyDetailDTO;
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
class CompanyDetailServiceTests {
    @Mock CompanyDetailService service;
    @Mock
    CompanyDetailDTO dto;
    @Mock
    Page<CompanyDetailDTO> companyDetailDTOPage;
    @BeforeEach
    void setup() {
        dto = new CompanyDetailDTO();
        dto.setId(1L);
        dto.setCompanyId(1L);
        dto.setFirstName("Her");
        dto.setLastName("Man");
        dto.setGender("Male");
        dto.setAddress("PP");
        dto.setUserId(1L);
    }

    @Test
    @DisplayName("Get all company details test")
    void get_all_company_details_test() {
        Pageable paging = PageRequest.of(0, 10, Sort.by("id").descending());
        when(service.findAll(paging)).thenReturn(companyDetailDTOPage);
        when(service.findAll(paging).getTotalPages()).thenReturn(3);

        var result = service.findAll(paging);

        //then
        assertThat(service.findAll(paging)).isEqualTo(companyDetailDTOPage);
        assertEquals(3, result.getTotalPages());
        verify(service, times(3)).findAll(paging);
    }

    @Test
    @DisplayName("Save company detail test")
    void save_company_detail_test() {
        //when
        when(service.save(dto)).thenReturn(dto);

        var result = service.save(dto);

        //then
        assertThat(service.save(dto)).isEqualTo(dto);
        assertEquals("Her", result.getFirstName());
        verify(service, times(2)).save(dto);
    }

    @Test
    @DisplayName("Update company detail test")
    void update_company_detail_test() {
        //given
        dto = new CompanyDetailDTO();
        dto.setId(1L);
        dto.setCompanyId(1L);
        dto.setFirstName("Levi");
        dto.setLastName("Jax");
        dto.setGender("Male");
        dto.setAddress("PP");
        dto.setUserId(1L);

        //when
        when(service.update(dto)).thenReturn(dto);

        var result = service.update(dto);

        //then
        assertThat(service.update(dto)).isEqualTo(dto);
        assertEquals("Jax", result.getLastName());
        verify(service, times(2)).update(dto);
    }

    @Nested
    @DisplayName("Get company detail by id test")
    class GetCompanyDetailById {
        @Test
        @DisplayName("When company detail with given id is found in database")
        void get_company_detail_by_id() {
            //when
            when(service.findById(1L)).thenReturn(dto);

            var result = service.findById(1L);

            //then
            assertThat(service.findById(1L)).isEqualTo(dto);
            assertEquals("Her", result.getFirstName());
            verify(service, times(2)).findById(1L);
        }
    }

    @Nested
    @DisplayName("Get company detail by company Id test")
    class GetCompanyDetailByCompanyIDTest {
        @Test
        @DisplayName("When company detail with given company ID is found in database")
        void get_by_company_id() {
            when(service.findByCompanyId(1L)).thenReturn(dto);

            var result = service.findByCompanyId(1L);

            //then
            assertThat(service.findByCompanyId(1L)).isEqualTo(dto);
            assertEquals("Her", result.getFirstName());
            verify(service, times(2)).findByCompanyId(1L);
        }
    }
}
