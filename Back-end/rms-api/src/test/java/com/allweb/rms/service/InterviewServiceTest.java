package com.allweb.rms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.Optional;

import com.allweb.rms.entity.jpa.CandidateStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.allweb.rms.config.TestConfig;
import com.allweb.rms.config.WebConfiguration;
import com.allweb.rms.entity.dto.InterviewRequest;
import com.allweb.rms.entity.dto.InterviewResponse;
import com.allweb.rms.entity.jpa.Candidate;
import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.entity.jpa.InterviewStatus;
import com.allweb.rms.repository.elastic.InterviewElasticsearchRepository;
import com.allweb.rms.repository.jpa.CandidateRepository;
import com.allweb.rms.repository.jpa.InterviewRepository;
import com.allweb.rms.repository.jpa.ReminderRepository;
import com.allweb.rms.security.AuthenticatedUser;
import com.allweb.rms.security.utils.AuthenticationUtils;
import com.allweb.rms.service.elastic.ElasticIndexingService;
import com.allweb.rms.service.mail.MailService;

@SpringBootTest(classes = {WebConfiguration.class, TestConfig.class})
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InterviewServiceTest {

    /**
     * @implNote * This test class is not use real database
     * * We use mockito to test the functionality of a class in isolation
     * * Note : mocking does not require a database connection or properties file read or file server read to test a functionality
     */
    @MockBean
    private InterviewRepository interviewRepository;
    @MockBean
    private CandidateRepository candidateRepository;
    @MockBean
    private InterviewElasticsearchRepository interviewElasticsearchRepository;
    @MockBean
    private ReminderRepository reminderRepository;

    @MockBean
    private InterviewStatusService interviewStatusService;
    @MockBean
    private MailService mailService;

    @MockBean
    private ReminderService reminderService;
    @MockBean
    private ElasticIndexingService elasticIndexingService;
    @MockBean
    private AuthenticationUtils authenticationUtils;
    @MockBean
    private ModelMapper modelMapper;

    @Captor
    private ArgumentCaptor<Interview> argumentCaptor;

    private InterviewService service;

    // given-object
    private InterviewRequest request;
    private InterviewStatus interviewStatusDTO;
    private AuthenticatedUser authenticatedUser;
    private Interview interview;
    private InterviewResponse response;

    @BeforeEach
    public void setUp() {
        request = new InterviewRequest(1, "Invite for interview", "Senior Java", new Date(), "admin");
        request.setStatusId(1);
        CandidateStatus candidateStatus = new CandidateStatus();
        candidateStatus.setId(1);
        Candidate candidate = new Candidate(1,"Dara", "Sok", "Mr.", "Male", new Date(),"dara@gmail.com", "1234456780", "", 0, "", null, true, true, "true", candidateStatus);
        interviewStatusDTO = new InterviewStatus(1, "In Progress", true);
        authenticatedUser = new AuthenticatedUser("admin", "admind@gmail.com", null);
        interview = new Interview(0, candidate, "admin", "Invite for interview", "Senior Java", new Date(), interviewStatusDTO);
        response = new InterviewResponse(interview, "In Progress", 1, "Sok", "",1L, 1);
        // initialize mock constructor
        service = new InterviewService(interviewRepository, candidateRepository, authenticationUtils, interviewStatusService, modelMapper, mailService);
        service.setElasticIndexingService(this.elasticIndexingService);
        service.setReminderService(this.reminderService);
        service.setReminderRepository(this.reminderRepository);
        service.setInterviewElasticsearchRepository(this.interviewElasticsearchRepository);
    }

    @Test
    @Order(2)
    @DisplayName("Should Create Interview")
    void testCreateInterview() {
        // when authenticationUse object found then return object auth ...
        Mockito.when(authenticationUtils.getAuthenticatedUser()).thenReturn(authenticatedUser);
        // when interviewStatus object found then return object inter ...
        Mockito.when(interviewStatusService.getStatusByIdAndActiveIsTrue(1)).thenReturn(interviewStatusDTO);
        // when do convert request Object to entity then return interview object ...
        // Do service save and verify save repository ...
        service.save(request);
        Mockito.verify(interviewRepository, Mockito.times(1)).save(argumentCaptor.capture());
        //Then check the result ...
        request.setUserId(authenticatedUser.getUserId());
        request.setInterviewStatus(interviewStatusDTO);
        Assertions.assertThat(argumentCaptor.getValue()).isEqualTo(service.convertToEntity(request));
    }

    @Test
    @Order(4)
    @DisplayName("Should Delete Interviews")
    void testDelete() {
        // When interview found then return object interview ...
        Mockito.when(interviewRepository.findById(1)).thenReturn(Optional.of(interview));
        Mockito.doNothing().when(this.reminderRepository).deleteByInterviewId(this.interview.getId());
        Mockito.doNothing().when(this.interviewRepository).delete(this.interview);
        Mockito.doNothing().when(this.elasticIndexingService).execute(Mockito.any());
        // Then check result expected
        assertThat(interview).isNotNull();
        // Do service with delete and verify repository delete
        service.deleteInterview(1);
        Mockito.verify(interviewRepository, Mockito.times(1)).delete(interview);
    }

    @Test
    @Order(1)
    @DisplayName("check injectedComponentsAreNotNull")
    void injectedComponentsAreNotNull() {
        assertThat(interviewRepository).isNotNull();
        assertThat(service).isNotNull();
        assertThat(interviewStatusService).isNotNull();
        assertThat(candidateRepository).isNotNull();
        assertThat(modelMapper).isNotNull();
        assertThat(authenticationUtils).isNotNull();
        assertThat(mailService).isNotNull();

    }

}

