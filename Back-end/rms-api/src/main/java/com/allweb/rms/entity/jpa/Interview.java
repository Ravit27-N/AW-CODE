package com.allweb.rms.entity.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "INTERVIEW_SEQUENCE_GENERATOR",
    sequenceName = "INTERVIEW_SEQUENCE",
    allocationSize = 1)
@Table(
    indexes = {
      @Index(name = "IDX_INTERVIEW", columnList = "title,userId,description"),
      @Index(name = "INTERVIEW_CANDIDATE_ID_INDEX", columnList = "candidate_id")
    })
@Cacheable
@Cache(region = "interviewCache", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

public class Interview extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INTERVIEW_SEQUENCE_GENERATOR")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @Schema(hidden = true)
  @JsonIgnore
  private Candidate candidate;

  private String userId;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Column(name = "date_time", nullable = false)
  private Date dateTime;

  @OneToOne
  @JoinColumn(name = "interview_status_id")
  private InterviewStatus interviewStatus;

  @Column(name = "is_delete")
  private boolean isDelete = Boolean.FALSE;

  public Interview(
      int id,
      Candidate candidate,
      String userId,
      String title,
      String description,
      Date dateTime,
      InterviewStatus status) {
    this.id = id;
    this.candidate = candidate;
    this.userId = userId;
    this.title = title;
    this.description = description;
    this.dateTime = dateTime;
    this.interviewStatus = status;
  }

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setCreatedAt(new Date());
  }
}
