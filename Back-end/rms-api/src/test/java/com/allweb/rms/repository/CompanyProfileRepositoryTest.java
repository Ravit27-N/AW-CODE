package com.allweb.rms.repository;

import com.allweb.rms.entity.jpa.CompanyProfile;
import com.allweb.rms.repository.jpa.CompanyProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompanyProfileRepositoryTest {

    private final CompanyProfile response = new CompanyProfile(1, "ALLWEB,.Co.Ltd", "Description", "Phnom Penh", "099999999", "admin@allweb.com.kh", "www.allweb.com");
    @Spy
    private final List<CompanyProfile> responseList = Stream
            .of(response, new CompanyProfile(2, "IT Company Co., Ltd", "Description", "Phnom Penh", "099999999", "admin@allweb.com.kh", "www.allweb.com"))
            .collect(Collectors.toList());

    @MockBean
    private CompanyProfileRepository repository;

    @Test
    @Order(1)
    @DisplayName("Check injected components are not null")
    void injectedComponentsAreNotNull() {
        Assertions.assertNotNull(response, "Injected component should be not null");
    }

    @Test
    @Order(2)
    @DisplayName("Test Save")
    void testSave() {
        Mockito.when(repository.save(response)).thenReturn(response);
        CompanyProfile returnedCompanyProfile = repository.save(response);
        Assertions.assertEquals(1, returnedCompanyProfile.getId(), "The Company profile should be 1");
    }

    @Test
    @Order(3)
    @DisplayName("Test Find First record Success")
    void testFindFirst() {
        Mockito.when(repository.findAll()).thenReturn(responseList);
        Optional<CompanyProfile> returnedCompanyProfile = repository.findAll().stream().findFirst();
        Assertions.assertTrue(returnedCompanyProfile.isPresent(), "The company profile should be present");
    }

    @Test
    @Order(4)
    @DisplayName("Test Find By Id Success")
    void testFindByIdSuccess() {
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(response));
        Optional<CompanyProfile> returnedCompanyProfile = repository.findById(1);
        Assertions.assertTrue(returnedCompanyProfile.isPresent(), "We should find a company profile with ID 1");
        Assertions.assertEquals(1, returnedCompanyProfile.get().getId(), "The company profile id should by 1");
        Assertions.assertEquals("ALLWEB,.Co.Ltd", returnedCompanyProfile.get().getTitle(), "The company profile title should be ALLWEB,.Co.Ltd");
    }

    @Test
    @Order(5)
    @DisplayName("Test Find By Id Not Found")
    void testFindByIdNotFound() {
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(response));
        Optional<CompanyProfile> returnedCompanyProfile = repository.findById(3);
        Assertions.assertFalse(returnedCompanyProfile.isPresent(), "A Company profile with ID 3 should not be found");
    }

    @Test
    @Order(6)
    @DisplayName("Test Delete By Id")
    void testDeleteById() {
        // when then
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(response));

        repository.delete(response);
        Mockito.verify(repository, Mockito.times(1)).delete(response);
    }
}
