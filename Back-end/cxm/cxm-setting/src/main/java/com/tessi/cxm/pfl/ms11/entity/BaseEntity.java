package com.tessi.cxm.pfl.ms11.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity implements Serializable {

  @CreatedDate
  @Column(updatable = false)
  private Date createdAt;

  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @LastModifiedDate
  private Date lastModified;

  @LastModifiedBy
  private String lastModifiedBy;
}
