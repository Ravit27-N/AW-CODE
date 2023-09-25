package com.allweb.rms.service;

import com.allweb.rms.component.CandidateStatusModelAssembler;
import com.allweb.rms.entity.jpa.CandidateStatus;
import com.allweb.rms.entity.dto.CandidateStatusDTO;
import com.allweb.rms.exception.CandidateStatusNotFoundException;
import com.allweb.rms.exception.CandidateStatusTitleConflictException;
import com.allweb.rms.repository.jpa.CandidateStatusRepository;
import lombok.extern.slf4j.Slf4j;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CandidateStatusServiceTest {

    @Mock
    private CandidateStatusRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    CandidateStatusModelAssembler assembler;

    @Captor
    private ArgumentCaptor<CandidateStatus> argumentCaptor;

    @MockBean
    private CandidateStatusService service;

    private CandidateStatusDTO request;
    private CandidateStatus response;

    @BeforeEach
    void init() {
        service = new CandidateStatusService(repository, modelMapper, assembler);
        request = new CandidateStatusDTO(1, "In progress", false, "in progress", true, true, new Date(), new Date());
        response = new CandidateStatus(1, "In progress", false, "in progress", true, true);
    }

    @Test
    @Order(1)
    @DisplayName("Check injected components are not null")
    void checkInjectedComponentsNotNull() {
        Assertions.assertNotNull(repository);
        Assertions.assertNotNull(assembler);
        Assertions.assertNotNull(modelMapper);
    }

    @Test
    @Order(2)
    @DisplayName("Test create Success")
    void testCreateSuccess() {
        // given
        Mockito.when(service.convertToEntity(request)).thenReturn(response);

        // when
        service.createStatusCandidate(request);
        // Using the mockito verify
        Mockito.verify(repository, Mockito.times(1)).save(argumentCaptor.capture());
        log.info("{}", argumentCaptor.getValue());
        Assertions.assertEquals(response, argumentCaptor.getValue(), "Should be the same");
    }

    @Test
    @Order(3)
    @DisplayName("Test create with title already exists")
    void testCreateWithTitleAlreadyExist() {
        // given
        Mockito.doThrow(new CandidateStatusTitleConflictException()).when(repository).validateTitle(eq("In progress"));

        // when // then
        Assertions.assertThrows(CandidateStatusTitleConflictException.class, () -> service.createStatusCandidate(request));
    }

    @Test
    @Order(4)
    @DisplayName("Test find by id Found")
    void testFindByIdFound() {
        // given
        Mockito.when(repository.findById(1)).thenReturn(Optional.of(response));
        Mockito.when(service.convertToDTO(response)).thenReturn(request);

        // when
        CandidateStatusDTO res = service.getCandidateStatusById(1);
        log.info("{}", res);
        // then
        Assertions.assertNotNull(res, "Should be not null");
        Assertions.assertEquals(request.getTitle(), res.getTitle(), "Should be the same");
    }

    @Test
    @Order(5)
    @DisplayName("Test find by id then throw exception not found")
    void testFindByIdNotFound() {
        // given
        Mockito.when(repository.findById(1)).thenReturn(Optional.empty());
        // when // then
        Assertions.assertThrows(CandidateStatusNotFoundException.class, () -> service.getCandidateStatusById(1));
    }

    // TODO: test find all later
}
