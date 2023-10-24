package com.innovationandtrust.project.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.ProjectStatus;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Project is a class that is representing a Project table in the database, and it has a
 * relationship with signatories, projectDetail, ProjectHistory, Document.
 */
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projects")
@DynamicUpdate
@NamedEntityGraph(
    name = "FindProjectByInfo",
    attributeNodes = {
      @NamedAttributeNode(value = "signatories"),
      @NamedAttributeNode(value = "documents", subgraph = "subgraph.documentDetails"),
      @NamedAttributeNode(value = "details")
    },
    subgraphs = {
      @NamedSubgraph(
          name = "subgraph.documentDetails",
          attributeNodes = @NamedAttributeNode(value = "documentDetails"))
    })
@org.springframework.data.relational.core.mapping.Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Project extends AbstractBaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @NotEmpty(message = "`name` field is required and cannot be empty!")
  private String name;

  private String status;

  private String step;

  @Column(name = "signature_level", length = 30)
  private String signatureLevel;

  @Column(name = "order_sign", columnDefinition = "boolean default true")
  private boolean orderSign;

  @Column(name = "order_approve", columnDefinition = "boolean default true")
  private boolean orderApprove;

  @Column(name = "template_id")
  private Long templateId;

  @Column(name = "template_name")
  private String templateName;

  @Column(name = "expire_date")
  private Date expireDate;

  @Column(name = "auto_reminder", columnDefinition = "boolean default false")
  private boolean autoReminder;

  @Column(name = "channel_reminder", columnDefinition = "int default 0")
  private Integer channelReminder;

  @Column(name = "reminder_option", columnDefinition = "int default 0")
  private Integer reminderOption;

  @Column(name = "flow_id")
  private String flowId;

  @Column(name = "assigned_to")
  private Long assignedTo;

  @Column(name = "assigned_date")
  private Date assignedDate;

  @OneToMany(
      mappedBy = "project",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
  @Fetch(FetchMode.JOIN)
  private Set<Signatory> signatories = new TreeSet<>();

  @OneToMany(
      mappedBy = "project",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
  private Set<ProjectDetail> details = new HashSet<>();

  @OneToMany(
      mappedBy = "project",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
  private Set<ProjectHistory> histories = new TreeSet<>();

  @OneToMany(
      mappedBy = "project",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
  @Fetch(FetchMode.JOIN)
  private Set<Document> documents = new HashSet<>();

  public Project(Long id, String name, String status) {
    this.id = id;
    this.name = name;
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Project project)) {
      return false;
    }
    return Objects.equals(getId(), project.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
    this.setStatus(ProjectStatus.DRAFT.name());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(new Date());
  }

  @JsonIgnore
  public List<Signatory> getSignatoryByRole(String role) {
    if (this.getSignatories().isEmpty()) {
      return List.of();
    }
    return signatories.stream()
        .filter(s -> ParticipantRole.getByRole(role).equals(ParticipantRole.getByRole(s.getRole())))
        .toList();
  }
}
