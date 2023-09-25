package com.tessi.cxm.pfl.ms5.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
@Entity
@Table(name = "USER_REQUEST_RESET_PASSWORD")
@SequenceGenerator(
    name = "USER_REQUEST_RESET_PASSWORD_GENERATOR",
    sequenceName = "USER_REQUEST_RESET_PASSWORD_SEQUENCE",
    allocationSize = 1
)
public class UserRequestResetPassword {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_REQUEST_RESET_PASSWORD_GENERATOR")
  private long id;
  private String keycloakUserId;

  @NotEmpty(message = "Email field is required")
  private String email;
  private String username;
  private String firstName;
  private String lastName;
  private String password;
  private String origin;
  private Boolean enabled;
  private Boolean emailVerified;
  private Long createdTimestamp;

  @NotEmpty(message = "Token field is required")
  @Column(unique = true)
  private String token;

  private Date expiredDate;
  private Date createdAt;
  private Date updatedAt;
  private String createdBy;
}
