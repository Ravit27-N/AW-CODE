package com.tessi.cxm.pfl.ms8.entity;

import javax.persistence.CollectionTable;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder
@SequenceGenerator(
    name = "UNLOADING_SCHEDULER_JOB_SEQUENCE_GENERATOR",
    sequenceName = "UNLOADING_SCHEDULER_JOB_SEQUENCE",
    allocationSize = 1)
@Table(indexes = {@Index(name = "client_id_index", columnList = "clientId"),
    @Index(name = "flow_unloading_index", columnList = "clientId,createdDate")})
public class UnloadingScheduleJob implements Serializable {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "UNLOADING_SCHEDULER_JOB_SEQUENCE_GENERATOR")
  private Long id;

  @Column(unique = true)
  private String flowId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "unloading_document_job")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "id")
  private List<String> documentIds;

  private String composedFileId;
  private String idCreator;
  private Date createdDate;
  private long clientId;
  private boolean validation;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    UnloadingScheduleJob that = (UnloadingScheduleJob) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
