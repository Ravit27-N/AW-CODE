package com.innovationandtrust.corporate.model.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "dosiers")
@org.springframework.data.relational.core.mapping.Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Folder extends AbstractEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "name", length = 150)
  private String unitName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "business_unit_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_business_unit_folder", value = ConstraintMode.CONSTRAINT))
  private BusinessUnit businessUnit;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Folder folder)) {
      return false;
    }
    return Objects.equals(getId(), folder.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(new Date());
  }
}
