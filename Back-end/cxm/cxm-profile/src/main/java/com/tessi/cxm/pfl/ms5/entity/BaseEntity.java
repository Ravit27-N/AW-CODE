package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.shared.utils.JavaTypeConstants;
import com.vladmihalcea.hibernate.type.json.JsonType;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Data;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Data
@MappedSuperclass
@EntityListeners({ AuditingEntityListener.class })
@TypeDefs({
    @TypeDef(name = JavaTypeConstants.JSON, typeClass = JsonType.class)
    })
public class BaseEntity implements Serializable {

  @CreatedDate
  @Column(updatable = false)
  private Date createdAt;

  @LastModifiedDate
  private Date lastModified;

  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;
}
