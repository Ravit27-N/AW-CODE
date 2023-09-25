package com.tessi.cxm.pfl.ms5.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(
    name = "PROFILE_SEQUENCE_GENERATOR",
    sequenceName = "PROFILE_SEQUENCE",
    allocationSize = 1)
@Table(name = "profile", indexes = {@Index(name = "idx_profile_name", columnList = "name")})
@NamedEntityGraph(name = "ProfileInfo",
    attributeNodes = {
        @NamedAttributeNode("profileDetails"),
        @NamedAttributeNode("client")
    })
@NamedEntityGraph(name = "ProfileList",
    attributeNodes = {
        @NamedAttributeNode("id"),
        @NamedAttributeNode("name"),
        @NamedAttributeNode("displayName"),
        @NamedAttributeNode("createdAt"),
        @NamedAttributeNode("createdBy"),
        @NamedAttributeNode("lastModified")
    })
public class Profile implements Comparable<Profile>, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROFILE_SEQUENCE_GENERATOR")
  private long id;

  @Size(max = 128)
  @NotEmpty()
  private String name;

  @Size(max = 128)
  private String displayName;

  @Column(updatable = false)
  private Date createdAt;

  private Date lastModified;

  private String createdBy;

  private String lastModifiedBy;

  private Long ownerId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", referencedColumnName = "id")
  @JsonIgnore
  private Client client;

  @OneToMany(
      mappedBy = "profile",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @Column(nullable = false)
  private List<ProfileDetails> profileDetails = new ArrayList<>();

  public Profile(long id, Long ownerId, String name, List<ProfileDetails> profileDetails) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.profileDetails = profileDetails;
  }

  @PrePersist
  private void initCreatedAtDate() {
    this.setCreatedAt(new Date());
    this.setLastModified(new Date());
  }

  @PreUpdate
  private void initLastModified() {
    this.setLastModified(new Date());
  }

  public void addProfileDetails(List<ProfileDetails> profileDetails) {
    if (profileDetails == null || profileDetails.isEmpty()) {
      return;
    }
    this.profileDetails.addAll(profileDetails);
    this.profileDetails.forEach(item -> {
      item.setProfile(this);
      item.setClient(this.getClient());
    });
  }

  public void removeProfileDetails(List<ProfileDetails> profileDetails) {
    this.profileDetails.forEach(item -> item.setProfile(null));
    this.profileDetails.removeAll(profileDetails);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Profile profile = (Profile) o;
    return Objects.equals(id, profile.getId()) && Objects.equals(name, profile.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName());
  }

  @Override
  public int compareTo(Profile o) {
    return o.getName().compareTo(this.name);
  }
}
