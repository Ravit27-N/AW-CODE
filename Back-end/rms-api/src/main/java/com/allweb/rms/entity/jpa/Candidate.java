package com.allweb.rms.entity.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(
    name = "CANDIDATE_SEQUENCE_GENERATOR",
    sequenceName = "CANDIDATE_SEQUENCE",
    allocationSize = 1)
@Table(indexes = {@Index(name = "IDX_CANDIDATE", columnList = "firstname,lastname,gender,gpa")})
public class Candidate extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CANDIDATE_SEQUENCE_GENERATOR")
  @Column(name = "id")
  private int id;

  @NotEmpty
  @Length(min = 2)
  @Column(length = 45)
  private String firstname;

  @NotEmpty
  @Length(min = 2)
  @Column(length = 45)
  private String lastname;

  @NotEmpty @NotNull private String salutation;

  @Column(length = 10)
  private String gender;

  private Date dateOfBirth;

  @NotEmpty
  @Column(length = 128, unique = true)
  private String email;

  @NotEmpty
  @Length(min = 8, max = 20)
  private String telephone;

  private String photoUrl;

  @Column(precision = 10, scale = 2)
  private float gpa;

  @Timestamp
  private String yearOfExperience;
  private String priority;
  private boolean isDeleted;
  private boolean active;

  @Column(columnDefinition = "text")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id")
  private CandidateStatus candidateStatus;

  @OneToMany(fetch = FetchType.LAZY)
  private Set<Interview> interviews;

  public Candidate(int id, String firstname, String lastname, String salutation, String gender, Date dateOfBirth, String email, String telephone, String photoUrl, float gpa, String yearOfExperience, String priority, boolean isDeleted, boolean active, String description, CandidateStatus candidateStatus) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.salutation = salutation;
    this.gender = gender;
    this.dateOfBirth = dateOfBirth;
    this.email = email;
    this.telephone = telephone;
    this.photoUrl = photoUrl;
    this.gpa = gpa;
    this.yearOfExperience = yearOfExperience;
    this.priority = priority;
    this.isDeleted = isDeleted;
    this.active = active;
    this.description = description;
    this.candidateStatus = candidateStatus;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Candidate book)) return false;
    return Objects.equals(id, book.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @PrePersist
  void onCreate() {
    this.isDeleted = false;
    this.active = true;
    setCreatedAt(new Date());
    setUpdatedAt(new Date());
  }

  @UpdateTimestamp
  void onUpdate() {
    setUpdatedAt(new Date());
  }

  public String getFullName() {
    return String.format("%s %s %s", this.salutation, this.firstname, this.lastname);
  }
}
