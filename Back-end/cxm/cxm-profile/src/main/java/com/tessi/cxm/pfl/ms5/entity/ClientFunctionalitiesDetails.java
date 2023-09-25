package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(
    name = "CLIENT_FUNCTIONALITIES_DETAILS_SEQUENCE_GENERATOR",
    sequenceName = "CLIENT_FUNCTIONALITIES_DETAILS_SEQUENCE",
    allocationSize = 1)
public class ClientFunctionalitiesDetails extends BaseEntity {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "CLIENT_FUNCTIONALITIES_DETAILS_SEQUENCE_GENERATOR")
  private long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_id")
  public Client client;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "functionalities_id")
  public Functionalities functionalities;

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
    setLastModified(new Date());
    setCreatedBy(AuthenticationUtils.getPrincipal());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
    setLastModifiedBy(AuthenticationUtils.getPrincipal());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ClientFunctionalitiesDetails)) {
      return false;
    }
    ClientFunctionalitiesDetails that = (ClientFunctionalitiesDetails) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
