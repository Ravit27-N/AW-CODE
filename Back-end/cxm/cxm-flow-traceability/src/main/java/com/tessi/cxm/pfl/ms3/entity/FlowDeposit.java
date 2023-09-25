package com.tessi.cxm.pfl.ms3.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Date;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class FlowDeposit extends BaseEntity {

  @Id
  private Long id;

  private String fileId;
  private String composedFileId;

  @Column(columnDefinition = "INT DEFAULT 0")
  private int step;

  @Column(columnDefinition = "bool DEFAULT 'true'")
  private boolean isActive;

  @Column(columnDefinition = "varchar DEFAULT 'To finalize'")
  private String status;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "id")
  @JsonBackReference
  private FlowTraceability flowTraceability;

  @PrePersist
  private void onCreate() {
    this.setActive(true);
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
    if (!(o instanceof FlowDeposit)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    FlowDeposit that = (FlowDeposit) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getId());
  }
}
