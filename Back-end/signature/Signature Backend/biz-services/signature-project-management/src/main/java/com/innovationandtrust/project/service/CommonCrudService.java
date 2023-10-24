package com.innovationandtrust.project.service;

import com.innovationandtrust.share.service.AbstractCrudService;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserResponse;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * CommonCrudService.
 *
 * @param <D> refers to a DTO object
 * @param <E> refers to Entity
 * @param <T> refers to an identity data type of {@link E}
 */
public class CommonCrudService<D, E, T> extends AbstractCrudService<D, E, T> {

  protected final IKeycloakProvider keycloakProvider;

  protected CommonCrudService(ModelMapper modelMapper, IKeycloakProvider keycloakProvider) {
    super(modelMapper);
    this.keycloakProvider = keycloakProvider;
  }

  /**
   * To retrieve information of a user from keycloak by jwt token.
   *
   * @return object of {@link KeycloakUserResponse}
   */
  protected Optional<KeycloakUserResponse> getUserInfo() {
    return this.keycloakProvider.getUserInfo(
        AuthenticationUtils.getAuthenticatedUser(
                SecurityContextHolder.getContext().getAuthentication())
            .getUuid());
  }

  /**
   * To retrieve the identity of a user with current action.
   *
   * @return object of {@link KeycloakUserResponse}
   */
  protected Long getUserId() {
    var userId = AuthenticationUtils.getUserId();
    if (Objects.isNull(userId)) {
      AtomicReference<Long> userRef = new AtomicReference<>(0L);
      this.getUserInfo().ifPresent(value -> userRef.set(value.getSystemUser().getUserId()));
      return userRef.get();
    }
    return userId;
  }

  protected String getUserUuid() {
    var userUuid = AuthenticationUtils.getUserUuid();
    if (Objects.isNull(userUuid)) {
      AtomicReference<String> userRef = new AtomicReference<>("");
      this.getUserInfo().ifPresent(value -> userRef.set(value.getId()));
      return userRef.get();
    }
    return userUuid;
  }

  protected String getCompanyUuid() {
    AtomicReference<String> companyUuid =
        new AtomicReference<>(AuthenticationUtils.getCompanyUuid());
    if (Objects.isNull(companyUuid.get())) {
      this.getUserInfo()
          .ifPresent(userInfo -> companyUuid.set(userInfo.getSystemUser().getCompany().getUuid()));
    }
    return companyUuid.get();
  }
}
