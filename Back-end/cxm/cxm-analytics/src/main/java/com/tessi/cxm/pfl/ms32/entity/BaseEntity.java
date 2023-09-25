package com.tessi.cxm.pfl.ms32.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity implements Serializable {

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private Date createdAt;

  @LastModifiedDate private Date modifiedAt;
}
