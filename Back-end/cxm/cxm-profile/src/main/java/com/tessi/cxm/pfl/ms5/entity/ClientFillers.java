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
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(
    name = "CLIENT_FILLERS_SEQUENCE_GENERATOR",
    sequenceName = "CLIENT_SEQUENCE",
    allocationSize = 1)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "key"})})
public class ClientFillers extends BaseEntity {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "CLIENT_FILLERS_SEQUENCE_GENERATOR")
  private Long id;

  @Column(nullable = false)
  @OrderColumn
  private String key;

  @Length(max = 20)
  @Size(max = 20)
  private String value;

  private boolean enabled;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id")
  private Client client;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ClientFillers that = (ClientFillers) o;
    return Objects.equals(id, that.id) && Objects.equals(client, that.client);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, client);
  }

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
    setLastModified(new Date());
  }

  @PreUpdate
  private void update() {
    super.setLastModified(new Date());
  }
}
