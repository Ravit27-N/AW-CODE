package com.allweb.rms.entity.jpa;

import jakarta.persistence.*;

import java.io.Serializable;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "reminder_type")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReminderType implements Serializable {
  private static final long serialVersionUID = -7271724903056118590L;

  @Id
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mail_template")
  private MailTemplate mailTemplate;
}
