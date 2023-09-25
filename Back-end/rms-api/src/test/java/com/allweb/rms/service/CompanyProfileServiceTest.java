package com.allweb.rms.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.allweb.rms.config.TestConfig;
import com.allweb.rms.config.WebConfiguration;
import com.allweb.rms.entity.dto.CompanyProfileDTO;
import com.allweb.rms.entity.jpa.CompanyProfile;
import com.allweb.rms.repository.jpa.CompanyProfileRepository;
import com.allweb.rms.utils.StorageUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = {WebConfiguration.class, TestConfig.class})
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompanyProfileServiceTest {

    @MockBean
    ModelMapper modelMapper;

    @MockBean
    private CompanyProfileRepository repository;

    @Mock
    StorageUtils storageUtils;

    @Captor
    private ArgumentCaptor<CompanyProfile> argumentCaptor;

    private CompanyProfileService service;

    @BeforeEach
    public void setUp() {
        // initial mock constructor
        service = new CompanyProfileService(repository, modelMapper, storageUtils);
    }

    // Given
    private final CompanyProfileDTO request = new CompanyProfileDTO(1, "ALLWEB Co., Ltd", "Description", "Phnom Penh", "099999999", "admin@allweb.com.kh", "www.allweb.com", new Date(), new Date());
    private final CompanyProfile response = new CompanyProfile(1, "ALLWEB Co., Ltd", "Description", "Phnom Penh", "099999999", "admin@allweb.com.kh", "www.allweb.com");
    @Spy
    private final List<CompanyProfile> expectedCompanyProfileList = Stream
            .of(response, new CompanyProfile(2, "IT Company Co., Ltd", "Description", "Phnom Penh", "099999999", "admin@allweb.com.kh", "www.allweb.com"))
            .collect(Collectors.toList());

    @Test
    @Order(1)
    @DisplayName("check injected components are not null")
    void checkInjectedComponentsNotNull() {
        Assertions.assertNotNull(repository);
        Assertions.assertNotNull(service);
        Assertions.assertNotNull(storageUtils);
    }

    @Test
    @DisplayName("Test save Company Profile")
    @Order(2)
    void testSave() {
        //Mockito.when(service.convertToEntity(request)).thenReturn(response);
        Mockito.when(service.convertToEntity(request)).thenReturn(response);
        Mockito.when(repository.save(response)).thenReturn(response);
        service.createCompanyProfile(request);
        // use behavior of test using mockito verify
        Mockito.verify(repository, Mockito.times(1)).save(argumentCaptor.capture());
        // check the result
		Assertions.assertEquals(response, argumentCaptor.getValue(), "Should be the same");
    }

    @Test
    @DisplayName("Test Find First")
    @Order(3)
    void testFindFirst() {
        // when
        Mockito.when(repository.findAll()).thenReturn(expectedCompanyProfileList);
        Mockito.when(expectedCompanyProfileList.get(0)).thenReturn(response);
        log.info("{}", expectedCompanyProfileList.get(0));
        Mockito.when(modelMapper.map(response, CompanyProfileDTO.class)).thenReturn(request);
        //Do
        CompanyProfileDTO actualCompanyProfileObject = service.getCompanyProfile();
        // verify
        Assertions.assertNotNull(actualCompanyProfileObject, "Actual company profile should not null");
        Assertions.assertEquals(response.getTitle(), actualCompanyProfileObject.getTitle());
        Assertions.assertEquals(response.getId(), actualCompanyProfileObject.getId());
    }
}
