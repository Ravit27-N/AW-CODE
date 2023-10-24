package com.innovationandtrust.profile.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "template_message")
@org.springframework.data.relational.core.mapping.Table
public class TemplateMessage implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column private String titleInvitation;

  @Column private String messageInvitation;

  @Column(columnDefinition = "int default 1")
  private Integer expiration;

  @Column private int sendReminder;

  @OneToOne(mappedBy = "templateMessage")
  private Template template;
}
