package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.ActivityRes;
import com.innovationandtrust.profile.model.dto.NewPasswordRequest;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.UserActivity;
import com.innovationandtrust.profile.repository.UserActivityRepository;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidLinkException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.TokenExpiredException;
import com.innovationandtrust.utils.keycloak.exception.InvalidConfirmPasswordException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceTests {
    User user;
    UserActivity userActivity;
    @Mock
    NewPasswordRequest request;
    private AuthService authService;
    @Mock
    private IKeycloakProvider keycloakProvider;
    @Mock
    private MailService mailService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserActivityRepository activityRepository;

    @BeforeEach
    public void setup() {
        authService =
                spy(
                        new AuthService(
                                keycloakProvider, activityRepository, userRepository, mailService, userService, modelMapper));
        var iniUser = new User();
        iniUser.setId(1L);
        iniUser.setFirstName("ALL");
        iniUser.setLastName("WEB");
        iniUser.setEmail("herman@gmail.com");
        iniUser.setActive(true);
        this.user = iniUser;

        var initActivityUser = new UserActivity();
        initActivityUser.setFinished(true);
        initActivityUser.setActioned(true);
        initActivityUser.setUser(iniUser);

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 1);
        var previousDate = new Date(instance.getTimeInMillis());
        initActivityUser.setExpireTime(previousDate);
        this.userActivity = initActivityUser;
        request = new NewPasswordRequest();
        request.setResetToken("123");
        request.setNewPassword("123");
        request.setConfirmPassword("123");
    }

    @DisplayName("Test reset password")
    @Test
    @Order(1)
    void reset_password_test() {
        // when
        when(this.activityRepository.findByTokenAndActioned(anyString(), anyBoolean())).thenReturn(Optional.of(userActivity));
        // invoke
        this.authService.resetPassword(request);
        // verify
        verify(authService, times(1)).resetPassword(request);
    }

    @DisplayName("Test reset password failed")
    @Test
    @Order(2)
    void reset_password_failed() {
        userActivity.setUser(null);
        // when
        when(this.activityRepository.findByTokenAndActioned(anyString(), anyBoolean())).thenReturn(Optional.of(userActivity));
        // verify
        var exception = assertThrows(Exception.class, () -> this.authService.resetPassword(request));
        log.info("Error : {}", exception.getMessage());
    }

    @DisplayName("Test reset password failed when newPassword and confirmPassword not match")
    @Test
    @Order(3)
    void reset_password_not_match() {
        request.setConfirmPassword("test");
        // when
        when(this.activityRepository.findByTokenAndActioned(anyString(), anyBoolean())).thenReturn(Optional.of(userActivity));
        // verify
        var exception = assertThrows(InvalidConfirmPasswordException.class, () -> this.authService.resetPassword(request));
        log.info("Error : {} || newPassword={} and confirmationPassword={}", exception.getMessage(), request.getNewPassword(), request.getConfirmPassword());
    }

    @Test
    @Order(4)
    @DisplayName("Test activate user")
    void testActivateUser() {
        this.user.setActive(false);
        // when
        when(this.activityRepository.findByTokenAndActioned(anyString(), anyBoolean())).thenReturn(Optional.of(userActivity));
        ActivityRes activityRes = this.authService.activateUser("123");
        verify(this.authService, times(1)).activateUser("123");
        log.info("Result {}", activityRes.toString());
    }

    @Test
    @Order(5)
    @DisplayName("Test failed activate user when already activated")
    void testActivateUserFailed() {
        // when
        when(this.activityRepository.findByTokenAndActioned(anyString(), anyBoolean())).thenReturn(Optional.of(userActivity));
        var exception = assertThrows(InvalidRequestException.class, () -> authService.activateUser("123"));
        log.info("Error : {}", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Test forgot password")
    void forgot_password_test() {
        // when
        when(userRepository.findByEmailAndDeleted(anyString(), anyBoolean()))
                .thenReturn(Optional.ofNullable(user));
        authService.forgotPassword("sokpanharith.dev@gmail.com");
//            user.setResetToken("123");

        when(userRepository.save(user)).thenReturn(user);
        mailService.sendResetPasswordLink("ALL", "sokpanharith.dev@gmail.com", "123", new
                Date(), true);
        verify(this.authService, times(1)).forgotPassword("sokpanharith.dev@gmail.com");
    }

    @Test
    @Order(7)
    @DisplayName("Test forgot password failed when user not activate")
    void forgot_password_test_failed() {
        user.setActive(false);
        // when
        when(userRepository.findByEmailAndDeleted(anyString(), anyBoolean()))
                .thenReturn(Optional.ofNullable(user));
        var invalidRequestException = assertThrows(InvalidRequestException.class, () -> this.authService.forgotPassword("sokpanharith.dev@gmail.com"));
        log.info("Error : {}", invalidRequestException.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Test disable Link")
    void test_disable_link() {
        when(this.activityRepository.findByTokenAndActioned(anyString(), anyBoolean()))
                .thenReturn(Optional.of(userActivity));
        var token = UUID.randomUUID().toString();
        ActivityRes activityRes = this.authService.disableLink(token);
        verify(this.authService, times(1)).disableLink(token);
        log.info("Result {}", activityRes.toString());
    }

    @Test
    @Order(9)
    @DisplayName("Test failed disable Link when link expired")
    void test_disable_link_failed() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, -1);
        var previousDate = new Date(instance.getTimeInMillis());
        userActivity.setExpireTime(previousDate);
        when(this.activityRepository.findByTokenAndActioned(anyString(), anyBoolean()))
                .thenReturn(Optional.of(userActivity));
        var token = UUID.randomUUID().toString();
        var invalidRequestException = assertThrows(TokenExpiredException.class, () -> this.authService.disableLink(token));
        log.info("Error : {}", invalidRequestException.getMessage());
    }

    @Test
    @Order(10)
    @DisplayName("Test failed disable Link when link no longer")
    void test_disable_link_failed_when_link_no_longer() {
        var token = UUID.randomUUID().toString();
        var invalidRequestException = assertThrows(InvalidLinkException.class, () -> this.authService.disableLink(token));
        log.info("Error : {}", invalidRequestException.getMessage());
    }

    @Test
    @Order(11)
    @DisplayName("Test confirm change email")
    void test_change_email() {
        when(this.activityRepository.findByTokenAndActioned(anyString(), anyBoolean()))
                .thenReturn(Optional.of(userActivity));
        var token = UUID.randomUUID().toString();
        ActivityRes activityRes = this.authService.confirmChangeMail(token);
        verify(this.authService, times(1)).confirmChangeMail(token);
        log.info("Result {}", activityRes.toString());
    }

    @Test
    @Order(12)
    @DisplayName("Test confirm change email failed when token invalid")
    void test_change_email_failed() {
        var token = UUID.randomUUID().toString();
        var invalidRequestException = assertThrows(EntityNotFoundException.class, () -> this.authService.confirmChangeMail(token));
        log.info("Error : current token {}=invoke token {}", invalidRequestException.getMessage(), UUID.randomUUID());
    }
}
