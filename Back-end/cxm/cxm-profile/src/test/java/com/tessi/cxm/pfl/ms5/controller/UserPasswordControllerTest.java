package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.dto.request.AuthenticationAttemptsRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.request.UserLoginAttemptDTO;
import com.tessi.cxm.pfl.ms5.dto.response.AuthenticationAttemptsDTO;
import com.tessi.cxm.pfl.ms5.entity.AuthenticationAttempts;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.service.AuthenticationAttemptsService;
import com.tessi.cxm.pfl.ms5.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserPasswordControllerTest {
    private UserService userService = mock(UserService.class);
    private AuthenticationAttemptsService authAttemptsService = mock(AuthenticationAttemptsService.class);
    private UserPasswordController userPasswordController = new UserPasswordController(userService, authAttemptsService);

    @Test
    void testLoginAttempts() {
        // Prepare test data
        String username = "example@tessi.fr";
        AuthenticationAttemptsRequestDTO request = new AuthenticationAttemptsRequestDTO();
        request.setUserName(username);

        // Create a user entity
        UserEntity user = new UserEntity();
        user.setActive(true);

        // Add attempts data for testing
        AuthenticationAttempts attempt = new AuthenticationAttempts();
        attempt.setId(1L);
        attempt.setUserEntity(user);
        attempt.setAttemptDate(LocalDateTime.now().plusMinutes(10));
        attempt.setFailedAttempt(true);

        // Set expected result
        AuthenticationAttemptsDTO expectedResult = new AuthenticationAttemptsDTO();
        expectedResult.setIsBlocked(false);
        expectedResult.setCurrentCountOfRemainingAttempts(4);
        expectedResult.setMinutesRemaining(30);
        expectedResult.setForceToChangePassword(true);

        // Mock the behavior of the AuthenticationAttemptsService
        AuthenticationAttemptsService authAttemptsService = mock(AuthenticationAttemptsService.class);
        when(authAttemptsService.isBlocked(eq(request))).thenReturn(expectedResult);

        // Create an instance of the controller and inject the mock service
        UserPasswordController userPasswordController = new UserPasswordController(userService, authAttemptsService);

        // Call the method under test
        ResponseEntity<AuthenticationAttemptsDTO> response = userPasswordController.loginAttempts(request);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        // Verify that the service method was called with the correct request
        verify(authAttemptsService).isBlocked(eq(request));

        // Print the result
        System.out.println("Result for testLoginAttempts: " + response.getBody());
    }


    @Test
    void testAddLoginAttempts() {
        // Prepare test data
        UserLoginAttemptDTO request = new UserLoginAttemptDTO();
        request.setUserName("example@tessi.fr");
        request.setLoginStatus(true);

        // Set the expected result
        UserLoginAttemptDTO expectedResult = new UserLoginAttemptDTO();
        expectedResult.setUserName(request.getUserName());
        expectedResult.setLoginStatus(request.getLoginStatus());

        // Mock the behavior of the authAttemptsService
       when(authAttemptsService.addUserLoginAttempt(any(UserLoginAttemptDTO.class)))
                .thenReturn(expectedResult);

        // Call the controller method
        ResponseEntity<UserLoginAttemptDTO> response = userPasswordController.addLoginAttempts(request);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());

        // Verify that the authAttemptsService method was called
      verify(authAttemptsService).addUserLoginAttempt(eq(request));

        // Print the result
        System.out.println("Result for testAddLoginAttempts : " + response.getBody());
    }
}
