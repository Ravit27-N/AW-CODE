package com.allweb.rms.entity.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reminder")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(
    name = "REMINDER_SEQUENCE_GENERATOR",
    sequenceName = "REMINDER_SEQUENCE",
    allocationSize = 1)
public class Reminder extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 2086901689453374719L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REMINDER_SEQUENCE_GENERATOR")
  private int id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reminder_type_id")
  private ReminderType reminderType;

  @NotNull
  @Column(updatable = false, length = 225)
  private String userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id")
  //  @OnDelete(action = OnDeleteAction.CASCADE)
  private Candidate candidate;

  @ManyToOne()
  @JoinColumn(name = "interview_id")
  //  @OnDelete(action = OnDeleteAction.CASCADE)
  private Interview interview;

  @NotNull
  @NotBlank
  @Column(length = 50, nullable = false)
  private String title;

  @Column() private String description = "";

  @Column(nullable = false)
  private Date dateReminder;

  private boolean isSend;
  private boolean active;
  private boolean deleted;

  @PrePersist
  public void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  public void onUpdate() {
    setUpdatedAt(new Date());
  }
}
