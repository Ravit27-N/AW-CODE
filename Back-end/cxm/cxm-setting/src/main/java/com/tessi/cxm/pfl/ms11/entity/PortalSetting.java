package com.tessi.cxm.pfl.ms11.entity;

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
import lombok.ToString;

@Builder
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PortalSetting extends BaseEntity {

  @Id private Long id;
  private String configPath;
  private String section;

  @MapsId
  @JoinColumn(name = "setting_id", referencedColumnName = "id")
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @ToString.Exclude
  private Setting setting;

  @Column(columnDefinition = "bool DEFAULT 'false'")
  private boolean isActive;

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
    if (!(o instanceof PortalSetting)) {
      return false;
    }
    PortalSetting that = (PortalSetting) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
