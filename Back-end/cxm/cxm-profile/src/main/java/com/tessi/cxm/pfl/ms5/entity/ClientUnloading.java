package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.ms5.constant.DayOfWeek;
import java.time.LocalTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "CLIENT_UNLOADING_SEQUENCE_GENERATOR",
    sequenceName = "CLIENT_UNLOADING_SEQUENCE",
    allocationSize = 1)
public class ClientUnloading extends BaseEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "CLIENT_UNLOADING_SEQUENCE_GENERATOR")
  private Long id;

  @Enumerated(EnumType.STRING)
  private DayOfWeek dayOfWeek;
  private LocalTime time;

  @Column(columnDefinition = "bool DEFAULT false")
  private boolean enabled;

  private String zoneId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "client_id")
  private Client client;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClientUnloading)) return false;
    return id != null && id.equals(((ClientUnloading) o).getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
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
