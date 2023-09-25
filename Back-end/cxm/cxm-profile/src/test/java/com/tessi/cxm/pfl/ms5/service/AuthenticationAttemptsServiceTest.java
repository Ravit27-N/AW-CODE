package com.tessi.cxm.pfl.ms5.service;


import com.tessi.cxm.pfl.ms5.dto.request.AuthenticationAttemptsRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.request.UserLoginAttemptDTO;
import com.tessi.cxm.pfl.ms5.dto.response.AuthenticationAttemptsDTO;
import com.tessi.cxm.pfl.ms5.entity.AuthenticationAttempts;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.repository.AuthenticationAttemptsRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.implementation.AuthenticationAttemptsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AuthenticationAttemptsServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private AuthenticationAttemptsRepository attemptsRepository;
    @InjectMocks private AuthenticationAttemptsServiceImpl authenticationAttemptsServiceImpl;

    // Test data
    private UserLoginAttemptDTO userLoginAttempt;


    @Test
    void testIsBlockedWithOneFailedAttempt() {
        // Prepare test data
        String username = "example@tessi.fr";
        AuthenticationAttemptsRequestDTO request = new AuthenticationAttemptsRequestDTO();
        request.setUserName(username);

        UserEntity user = new UserEntity();
        user.setActive(true);

        List<AuthenticationAttempts> attempts = new ArrayList<>();

        // Add attempts data for testing
        AuthenticationAttempts attempt1 = new AuthenticationAttempts();
        attempt1.setId(1L);
        attempt1.setUserEntity(user);
        attempt1.setAttemptDate(LocalDateTime.now().plusMinutes(10));
        attempt1.setFailedAttempt(true);
        attempts.add(attempt1);


        // Mock the behavior of UserRepository
        when(userRepository.findByUsernameAndIsActiveTrue(eq(username)))
                .thenReturn(Optional.of(user));

        // Mock the behavior of AttemptsRepository
        when(attemptsRepository.findLastAttemptByUserName(eq(username), any(), any()))
                .thenReturn(attempts);

        // Call the method under test
        AuthenticationAttemptsDTO result = authenticationAttemptsServiceImpl.isBlocked(request);

        // Assert the result
        // Add assertions based on the expected behavior of the method
        AuthenticationAttemptsDTO expected = new AuthenticationAttemptsDTO();
        expected.setIsBlocked(false);
        expected.setCurrentCountOfRemainingAttempts(4);
        expected.setMinutesRemaining(0);
        expected.setForceToChangePassword(true);

        assertEquals(expected, result);
        System.out.println("Result for testIsBlockedWithOneFailedAttempt: " + result);
    }
    @Test
    void testIsBlockedWithFiveFailedAttempts() {
        AuthenticationAttemptsRequestDTO request2 = new AuthenticationAttemptsRequestDTO();
        String username = "example2@tessi.fr";
        request2.setUserName(username);
        UserEntity user = new UserEntity();
        user.setActive(true);

        List<AuthenticationAttempts> attempts = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            AuthenticationAttempts attempt = new AuthenticationAttempts();
            attempt.setId((long) i);
            attempt.setUserEntity(user);
            attempt.setAttemptDate(LocalDateTime.now().minusMinutes(10));
            attempt.setFailedAttempt(true);
            attempts.add(attempt);
        }

        when(userRepository.findByUsernameAndIsActiveTrue(eq(username)))
                .thenReturn(Optional.of(user));
        when(attemptsRepository.findLastAttemptByUserName(eq(username), any(), any()))
                .thenReturn(attempts);

        LocalDateTime lastAttemptTime = attempts.get(0).getAttemptDate(); // Assuming the last attempt is at index 0
        Duration duration = Duration.between(lastAttemptTime, LocalDateTime.now());
        long minutesElapsedSinceLastAttempt = duration.toMinutes();
        long minutesRemaining = Math.max(30 - minutesElapsedSinceLastAttempt, 0);

        AuthenticationAttemptsDTO expected = new AuthenticationAttemptsDTO();
        expected.setIsBlocked(true);
        expected.setCurrentCountOfRemainingAttempts(0);
        expected.setMinutesRemaining(minutesRemaining);// Adjust this value based on the time elapsed since the last attempt
        expected.setForceToChangePassword(false);

        AuthenticationAttemptsDTO result = authenticationAttemptsServiceImpl.isBlocked(request2);
        assertEquals(expected, result);

        System.out.println("Result for testIsBlockedWithFiveFailedAttempts: " + result);

    }

    @Test
    void testIsBlockedWithFiveAttemptsAndOneSuccessful() {
        // User with 5 attempts but 1 successful attempt
        AuthenticationAttemptsRequestDTO request3 = new AuthenticationAttemptsRequestDTO();
        String username = "example3@tessi.fr";
        request3.setUserName(username);

        UserEntity user = new UserEntity();
        user.setActive(true);

        List<AuthenticationAttempts> attempts3 = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            AuthenticationAttempts attempt = new AuthenticationAttempts();
            attempt.setId((long) i);
            attempt.setUserEntity(user);
            attempt.setAttemptDate(LocalDateTime.now().minusMinutes(10));
            attempt.setFailedAttempt(true);
            attempts3.add(attempt);
        }

        AuthenticationAttempts successfulAttempt = new AuthenticationAttempts();
        successfulAttempt.setId(5L);
        successfulAttempt.setUserEntity(user);
        successfulAttempt.setAttemptDate(LocalDateTime.now().minusMinutes(10));
        successfulAttempt.setFailedAttempt(false);
        attempts3.add(successfulAttempt);

        when(userRepository.findByUsernameAndIsActiveTrue(eq(username)))
                .thenReturn(Optional.of(user));
        when(attemptsRepository.findLastAttemptByUserName(eq(username), any(), any()))
                .thenReturn(attempts3);

        AuthenticationAttemptsDTO expected = new AuthenticationAttemptsDTO();
        expected.setIsBlocked(false);
        expected.setCurrentCountOfRemainingAttempts(1);
        expected.setMinutesRemaining(0);
        expected.setForceToChangePassword(true);

        AuthenticationAttemptsDTO result = authenticationAttemptsServiceImpl.isBlocked(request3);
        assertEquals(expected, result);

        System.out.println("Result for testIsBlockedWithFiveAttemptsAndOneSuccessful: " + result);
    }

    @Test
    public void testAddUserLoginAttempt() {
        // Create a sample UserLoginAttemptDTO object
        userLoginAttempt = new UserLoginAttemptDTO();
        userLoginAttempt.setUserName("example@tessi.fr");
        userLoginAttempt.setLoginStatus(true);

        // Create a sample UserEntity object
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("example@tessi.fr");
        userEntity.isActive();

        // Mock the behavior of userRepository.findByUsernameAndIsActiveTrue()
        when(userRepository.findByUsernameAndIsActiveTrue("example@tessi.fr")).thenReturn(Optional.of(userEntity));

        // Invoke the method under test
        UserLoginAttemptDTO result = authenticationAttemptsServiceImpl.addUserLoginAttempt(userLoginAttempt);

        // Verify that the attemptsRepository.save() method was called
        verify(attemptsRepository).save(any(AuthenticationAttempts.class));

        // Assert that the result is the same as the input
        assertEquals(userLoginAttempt, result);

        System.out.println("User added with his login status : "  +result);

    }


}


