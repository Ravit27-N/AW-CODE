package com.innovationandtrust.utils.keycloak.mapper;

import com.innovationandtrust.utils.keycloak.exception.InvalidConfirmPasswordException;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserRequest;
import com.innovationandtrust.utils.keycloak.model.ResetPasswordRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeycloakMappingHandler {
  public static UserRepresentation toUserRepresentation(KeycloakUserRequest userRequest) {
    var user = new UserRepresentation();
    user.setFirstName(userRequest.getFirstName());
    user.setLastName(userRequest.getLastName());
    user.setUsername(userRequest.getEmail());
    user.setEmail(userRequest.getEmail());
    user.setEnabled(true);
    user.setCreatedTimestamp(Timestamp.valueOf(LocalDateTime.now()).getTime());
    return user;
  }

  public static CredentialRepresentation toCredentialRepresentation(
      KeycloakUserRequest userKeycloak) {
    var credential = new CredentialRepresentation();
    credential.setTemporary(false);
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(userKeycloak.getPassword());
    return credential;
  }

  public static CredentialRepresentation toCredentialRepresentation(
      ResetPasswordRequest passwordRequest) {
    if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())) {
      throw new InvalidConfirmPasswordException(
          "The new password and it confirmation is not the same!");
    }
    var credential = new CredentialRepresentation();
    credential.setTemporary(false);
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(passwordRequest.getNewPassword());
    return credential;
  }
}
