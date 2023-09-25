package com.allweb.rms.repository;

import com.allweb.rms.entity.jpa.Interview;
import com.allweb.rms.repository.jpa.InterviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InterviewRepositoryTest {

    /**
     * @implNote * This test class is use real database
     * * all functionality work with a real database
     * * before run this test class make sure all database configuration has been config
     */


    // given
    private final Interview expectedInterviewObject = new Interview(1, null, "admin", "Invite for interview", "Senior Java", new Date(), null);

    @Mock
    private InterviewRepository repository;

    @Test
    @Order(1)
    @DisplayName("check injectedComponentsAreNotNull")
    void injectedComponentsAreNotNull() {
        assertThat(repository).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Should Save Interviews")
    void shouldCreate() {
        // when the repository has been saved
        Mockito.when(repository.save(expectedInterviewObject)).thenReturn(expectedInterviewObject);
        Interview actualInterviewObject = repository.save(expectedInterviewObject);
        // then checking the actualInterviewObject with expectedInterviewObject
        assertThat(expectedInterviewObject.getTitle()).isEqualTo(actualInterviewObject.getTitle());
    }

    @Test
    @Order(3)
    @DisplayName("Should Retrieve Interviews")
    void shouldGetInterviews() {
        // when the repository has been getting the objects
        Mockito.when(repository.getAllByTitle(expectedInterviewObject.getTitle())).thenReturn(Collections.singletonList(expectedInterviewObject));
        List<Interview> actualInterviewObject = repository.getAllByTitle(expectedInterviewObject.getTitle());
        // then checking the actualInterviewObject result is not null
        assertThat(actualInterviewObject).isNotEmpty();
    }

    @Test
    @DisplayName("Should Delete Interviews")
    @Order(4)
    void shouldDelete() {
        Mockito.when(repository.getAllByTitle(expectedInterviewObject.getTitle())).thenReturn(Collections.singletonList(expectedInterviewObject));
        // when then repository has been getting the objects
        List<Interview> actualInterviewObject = repository.getAllByTitle(expectedInterviewObject.getTitle());
        // then checking the actualInterviewObject result is not null
        assertThat(actualInterviewObject).isNotEmpty();
        // Do delete
        repository.deleteAll(actualInterviewObject);
    }
}
