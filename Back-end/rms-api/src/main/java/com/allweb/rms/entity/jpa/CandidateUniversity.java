package com.allweb.rms.entity.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "CANDIDATE_UNIVERSITY_SEQUENCE_GENERATOR",
    sequenceName = "CANDIDATE_UNIVERSITY_SEQUENCE",
    allocationSize = 1)
@Table
public class CandidateUniversity extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "CANDIDATE_UNIVERSITY_SEQUENCE_GENERATOR")
  private int id;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(referencedColumnName = "ID")
  private Candidate candidate;

  @ManyToOne(cascade = CascadeType.REMOVE)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(referencedColumnName = "ID")
  private University university;

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
