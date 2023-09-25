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
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "CLIENT_ALLOW_UNLOADING_SEQUENCE_GENERATOR",
    sequenceName = "CLIENT_ALLOW_UNLOADING_SEQUENCE",
    allocationSize = 1)
public class ClientAllowUnloading extends BaseEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "CLIENT_ALLOW_UNLOADING_SEQUENCE_GENERATOR")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", referencedColumnName = "id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Client client;

  @Column(columnDefinition = "int default 0")
  private int month;

  @Column(columnDefinition = "int default 0")
  private int day;

  @Column(columnDefinition = "int default 0")
  private long holidayId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ClientAllowUnloading)) {
      return false;
    }
    ClientAllowUnloading that = (ClientAllowUnloading) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getClient().getId(), that.getClient().getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getClient().getId());
  }

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }
}
