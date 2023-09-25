package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.ms5.constant.AddressType;
import com.tessi.cxm.pfl.ms5.entity.converter.AddressTypeAttributeConverter;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@NamedEntityGraph(
    name = "UserEntity",
    attributeNodes = {
      @NamedAttributeNode(value = "userProfiles", subgraph = "subgraph.profile"),
      @NamedAttributeNode(value = "department")
    },
    subgraphs = {
      @NamedSubgraph(
          name = "subgraph.profile",
          attributeNodes = @NamedAttributeNode(value = "profile"))
    })
@NamedEntityGraph(
    name = "UserInfo",
    attributeNodes = {
      @NamedAttributeNode(value = "userProfiles", subgraph = "subgraph.profile"),
      @NamedAttributeNode(value = "department", subgraph = "subgraph.division")
    },
    subgraphs = {
      @NamedSubgraph(
          name = "subgraph.profile",
          attributeNodes = @NamedAttributeNode(value = "profile")),
      @NamedSubgraph(
          name = "subgraph.division",
          attributeNodes = @NamedAttributeNode(value = "division", subgraph = "subgraph.client")),
      @NamedSubgraph(
          name = "subgraph.client",
          attributeNodes = @NamedAttributeNode(value = "client")),
    })
@SequenceGenerator(
    name = "USER_ENTITY_SEQUENCE_GENERATOR",
    sequenceName = "USER_ENTITY_SEQUENCE",
    allocationSize = 1)
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ENTITY_SEQUENCE_GENERATOR")
  @Schema(type = "Integer", example = "1")
  private Long id;

  @NotEmpty
  @Schema(type = "string", example = "User1")
  @Column(nullable = false, updatable = false)
  private String username;

  @Schema(type = "string", example = "User1")
  private String firstName;

  @Schema(type = "string", example = "User1")
  private String lastName;

  @Schema(type = "string", example = "user1@mail.com")
  @Column(nullable = false, updatable = false)
  private String email;

  @Column(columnDefinition = "bool DEFAULT 'true'")
  private boolean isActive;

  @Schema(type = "string", example = "e6d52389-0a4b-4235-9786-c3807b51396e")
  private String technicalRef;

  @Column(columnDefinition = "bool DEFAULT 'false'")
  private boolean isAdmin;


  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "service_id", referencedColumnName = "id")
  private Department department;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<UserProfiles> userProfiles;

  @Convert(converter = AddressTypeAttributeConverter.class)
  private AddressType returnAddressLevel;

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
    if (StringUtils.isBlank(this.getCreatedBy())) {
      setCreatedBy(AuthenticationUtils.getPrincipal());
    }
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
    if (StringUtils.isBlank(this.getLastModifiedBy())) {
      setLastModifiedBy(AuthenticationUtils.getPrincipal());
    }
  }

  public List<UserProfiles> getUserProfiles() {
    if (this.userProfiles != null) {
      return this.userProfiles.stream().sorted().collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    UserEntity that = (UserEntity) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public String getReturnAddressLevelString() {
    if (ObjectUtils.isNotEmpty(returnAddressLevel)) {
      return returnAddressLevel.getValue();
    }
    return null;
  }
}
