package com.allweb.rms.entity.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table
@IdClass(UserRoleDetailId.class)
public class UserRoleDetail implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id private int moduleId;

  @Id private String userRoleId;

  private boolean isViewAble;
  private boolean isDeleteAble;
  private boolean isEditAble;
  private boolean isInsertAble;
}
