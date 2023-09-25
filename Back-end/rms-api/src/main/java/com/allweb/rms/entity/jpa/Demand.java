package com.allweb.rms.entity.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(
    name = "DEMAND_SEQUENCE_GENERATOR",
    sequenceName = "DEMAND_SEQUENCE",
    initialValue = 1,
    allocationSize = 1)
@Table(
    indexes = {
      @Index(
          name = "IDX_DEMAND",
          columnList = "nbRequired,experienceLevel,deadLine,nbCandidates,status")
    })
public class Demand extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEMAND_SEQUENCE_GENERATOR")
  @Column(name = "id")
  private int id;

  // Reference with Project(M-1)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "jobDescription_id")
  private JobDescription jobDescription;

  private int nbRequired; // required user. input from user

  @NotEmpty private String experienceLevel; // input from user

  @Column(nullable = false)
  private Date deadLine; // input from user -> deadLine

  // Reference with Candidate(M-1) when status-> success
  private String nbCandidates; // use for count

  @Column(nullable = false)
  private boolean status; // In Progress & Completed

  private boolean isDeleted;
  private boolean active;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Demand demand)) return false;
    return Objects.equals(id, demand.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @PrePersist
  void onCreate() {
    this.isDeleted = false;
    this.status = true;
    setCreatedAt(new Date());
    setUpdatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
