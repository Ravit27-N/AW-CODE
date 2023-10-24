package com.innovationandtrust.profile.model.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "templates")
@org.springframework.data.relational.core.mapping.Table
public class Template extends AbstractEntity implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column private String name;

  @Column(name = "sign_process")
  private String signProcess;

  @Column(name = "notification_service")
  private String notificationService;

  @Column(columnDefinition = "int default 0")
  private int signature;

  @Column(columnDefinition = "int default 0")
  private int approval;

  @Column(columnDefinition = "int default 0")
  private int recipient;

  @Column(columnDefinition = "int default 0")
  private int viewer;

  @Column(columnDefinition = "int default 0")
  private int format;

  @Column(columnDefinition = "int default 0")
  private int level;

  @Column(name = "folder_id")
  private long folderId;

  @Column(name = "business_unit_id")
  private int businessUnitId;

  @Column(nullable = false)
  private String type;

  @Column(columnDefinition = "int default 1", nullable = false)
  private int step;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "company_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_company_templates", value = ConstraintMode.CONSTRAINT))
  private Company company;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "template_message_id", referencedColumnName = "id")
  private TemplateMessage templateMessage;

  @PrePersist
  void onCreate() {
    this.setCreatedAt(new Date());
    this.setModifiedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    this.setModifiedAt(new Date());
  }
}
