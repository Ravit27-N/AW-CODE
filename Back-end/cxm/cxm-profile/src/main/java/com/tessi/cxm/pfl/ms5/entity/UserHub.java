package com.tessi.cxm.pfl.ms5.entity;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(
    name = "USER_HUB_SEQUENCE_GENERATOR",
    sequenceName = "USER_HUB_SEQUENCE",
    allocationSize = 1)
public class UserHub extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_HUB_SEQUENCE_GENERATOR")
  private Long id;

  private String username;
  private String password;

  @Column(columnDefinition = "boolean DEFAULT false")
  private boolean encrypted = false;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", referencedColumnName = "id")
  private Client client;

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserHub userHub = (UserHub) o;
    return Objects.equals(id, userHub.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(34566);
  }
}
