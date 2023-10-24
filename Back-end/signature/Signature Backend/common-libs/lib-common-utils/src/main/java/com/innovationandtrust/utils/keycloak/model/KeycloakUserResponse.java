package com.innovationandtrust.utils.keycloak.model;

import com.innovationandtrust.utils.keycloak.constant.KeycloakUserConstant;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.keycloak.representations.idm.UserRepresentation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakUserResponse implements Serializable {

  private String id;
  private String firstName;
  private String lastName;

  private String email;

  private boolean isEnabled;

  private String[] roles;

  private UserInfo systemUser;

  public KeycloakUserResponse(UserRepresentation user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.isEnabled = user.isEnabled();

    if (Objects.nonNull(user.getRealmRoles()) && !user.getRealmRoles().isEmpty()) {
      this.roles = user.getRealmRoles().toArray(new String[] {});
    }
    var attr = user.getAttributes();
    var userInfo = new UserInfo();
    if (attr != null) {
      attr.get(KeycloakUserConstant.USER_ATTRIBUTE_IDENTITY).stream()
          .findAny()
          .map(Long::parseLong)
          .ifPresent(userInfo::setUserId);
      var company = attr.get(KeycloakUserConstant.USER_COMPANY);
      if (Objects.nonNull(company)) {
        company.stream().findAny().ifPresent(str -> userInfo.setCompany(new Company(str)));
      }

      var corporate = attr.get(KeycloakUserConstant.CORPORATE_UUID);
      if (Objects.nonNull(corporate)) {
        corporate.stream().findAny().ifPresent(userInfo::setCorporateId);
      }

      var firstLogin = attr.get(KeycloakUserConstant.FIRST_LOGIN);
      if (Objects.nonNull(firstLogin)) {
        firstLogin.stream()
            .findAny()
            .ifPresent(isFirst -> userInfo.setFirstLogin(Boolean.parseBoolean(isFirst)));
      }
    }
    this.systemUser = userInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof KeycloakUserResponse that)) return false;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getEmail(), that.getEmail());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getEmail());
  }
}
