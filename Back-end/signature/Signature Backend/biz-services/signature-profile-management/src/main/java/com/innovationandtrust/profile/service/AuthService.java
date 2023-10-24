package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.ActivityRes;
import com.innovationandtrust.profile.model.dto.NewPasswordRequest;
import com.innovationandtrust.profile.model.dto.UserDto;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.UserActivity;
import com.innovationandtrust.profile.repository.UserActivityRepository;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidLinkException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.KeycloakException;
import com.innovationandtrust.utils.exception.exceptions.TokenExpiredException;
import com.innovationandtrust.utils.keycloak.exception.InvalidConfirmPasswordException;
import com.innovationandtrust.utils.keycloak.model.ResetPasswordRequest;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
  private final IKeycloakProvider keycloakProvider;
  private final UserActivityRepository activityRepository;
  private final UserRepository userRepository;
  private final MailService mailService;
  private final UserService userService;
  private final ModelMapper modelMapper;

  /**
   * To reset user password.
   *
   * @param newPasswordRequest refers to the object that is required to set new password
   */
  @Transactional(propagation = Propagation.NEVER)
  public void resetPassword(NewPasswordRequest newPasswordRequest) {
    UserActivity activity = this.getActivity(newPasswordRequest.getResetToken());

    if (!Objects.equals(
        newPasswordRequest.getNewPassword(), newPasswordRequest.getConfirmPassword())) {
      throw new InvalidConfirmPasswordException("Invalid password confirmation!");
    }

    var user = activity.getUser();
    try {
      this.keycloakProvider.resetPassword(
          ResetPasswordRequest.builder()
              .id(user.getUserEntityId())
              .newPassword(newPasswordRequest.getNewPassword())
              .confirmPassword(newPasswordRequest.getConfirmPassword())
              .build());

      activity.setFinished(true);
      // Update user
      this.activityRepository.save(activity);

      mailService.sendResetPasswordSuccessfullyTemplate(user.getFirstName(), user.getEmail());
    } catch (Exception e) {
      log.info("Error while resetting user password ... ", e);
      throw new KeycloakException(e.getMessage());
    }
  }

  /**
   * To ask for reset password link via email.
   *
   * @param email refer to user's email that want to set a new password.
   */
  @Transactional(propagation = Propagation.NEVER)
  public void forgotPassword(String email) {
    User user =
        userRepository
            .findByEmailAndDeleted(email, false)
            .orElseThrow(() -> new EntityNotFoundException("User", "email"));

    if (!user.isActive()) {
      throw new InvalidRequestException("This user is inactive.");
    }

    UserActivity activity =
        UserActivity.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expireTime(DateUtils.addDays(new Date(), 2))
            .build();

    this.activityRepository.save(activity);

    log.info("Send user forgot password mail...");
    AuthenticationUtils.setUserUuid(user.getUserEntityId());
    this.mailService.sendResetPasswordLink(
        user.getFirstName(),
        user.getEmail(),
        activity.getToken(),
        activity.getExpireTime(),
        RoleConstant.isSuperAdmin(user.getRoles()));
  }

  /**
   * To activate user.
   *
   * @param token refers to refresh token when creating user
   */
  public ActivityRes activateUser(String token) {
    var activity = this.getActivity(token);

    if (activity.getUser().isActive()) {
      throw new InvalidRequestException("User already active...");
    }

    activity.setFinished(true);

    // Update keycloak: enable user login
    this.userService.setUserActive(activity.getUser().getUserEntityId(), true);

    this.activityRepository.save(activity);
    return ActivityRes.builder().active(true).build();
  }

  /**
   * To destroy activate user link (Link clicked from email). To destroy forgot password link (Link
   * clicked from email).
   *
   * @param token refers to token when creating user
   */
  public ActivityRes disableLink(String token) {
    UserActivity activity = this.checkUserAndExpiredDate(token);

    activity.setActioned(true);
    activity.setToken(UUID.randomUUID().toString());
    this.activityRepository.save(activity);

    return ActivityRes.builder().resetToken(activity.getToken()).build();
  }

  private UserActivity getActivity(String token) {
    return activityRepository
        .findByTokenAndActioned(token, true)
        .orElseThrow(() -> new EntityNotFoundException("Invalid token " + token));
  }

  private UserActivity checkUserAndExpiredDate(String token) {
    UserActivity activity =
        activityRepository
            .findByTokenAndActioned(token, false)
            .orElseThrow(
                () ->
                    new InvalidLinkException(
                        "The link is no longer valid please contact your administrator."));

    Date current = new Date();
    if (!current.before(activity.getExpireTime())) {
      throw new TokenExpiredException("The link is expired");
    }
    return activity;
  }

  public ActivityRes confirmChangeMail(String token) {
    UserActivity activity = this.getActivity(token);

    activity.setFinished(true);
    var user = activity.getUser();
    user.setActive(true);
    // Update keycloak: enable user login
    this.userService.updateStatus(this.modelMapper.map(user, UserDto.class));

    // Update user
    this.activityRepository.save(activity);
    return ActivityRes.builder().active(true).build();
  }
}
