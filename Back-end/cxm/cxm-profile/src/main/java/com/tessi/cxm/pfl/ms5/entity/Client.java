package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
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
    name = "CLIENT_SEQUENCE_GENERATOR",
    sequenceName = "CLIENT_SEQUENCE",
    allocationSize = 1)
@NamedEntityGraph(
    name = "ClientList",
    attributeNodes = {@NamedAttributeNode("userHub")})
public class Client extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENT_SEQUENCE_GENERATOR")
  private long id;

  @Column(unique = true, updatable = false)
  private String name;

  @Size(max = 125)
  private String email;

  private String contactFirstName;
  private String contactLastname;

  // Metadata of file
  private String fileId;
  private String filename;

  @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private long fileSize;

  @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
  private List<Profile> profiles;

  @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  private UserHub userHub;

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Division> divisions = new ArrayList<>();

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClientFunctionalitiesDetails> clientFunctionalitiesDetails = new ArrayList<>();

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClientUnloading> clientUnloads = new ArrayList<>();

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClientAllowUnloading> clientAllowUnloads = new ArrayList<>();

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClientFillers> fillers = new ArrayList<>();

  public void addClientFunctionalityDetails(
      List<ClientFunctionalitiesDetails> clientFunctionalitiesDetails) {
    this.clientFunctionalitiesDetails.clear();
    this.clientFunctionalitiesDetails.addAll(clientFunctionalitiesDetails);
    clientFunctionalitiesDetails.forEach(cfd -> cfd.setClient(this));
  }

  public void removeClientFunctionalityDetails(
      List<ClientFunctionalitiesDetails> clientFunctionalitiesDetails) {
    this.clientFunctionalitiesDetails.removeAll(clientFunctionalitiesDetails);
    clientFunctionalitiesDetails.forEach(cfd -> cfd.setClient(null));
  }

  public void addClientUnloads(List<ClientUnloading> clientUnloads) {
    this.clientUnloads.clear();
    this.clientUnloads.addAll(clientUnloads);
    clientUnloads.forEach(
        unload -> {
          unload.setClient(this);
          unload.setCreatedBy(this.getCreatedBy());
        });
  }

  public void removeClientUnloads(List<ClientUnloading> clientUnloads) {
    this.clientUnloads.removeAll(clientUnloads);
    clientUnloads.forEach(cfd -> cfd.setClient(null));
  }

  public void addClientAllowUnloads(List<ClientAllowUnloading> clientAllowUnloads) {
    this.clientAllowUnloads.clear();
    this.clientAllowUnloads.addAll(clientAllowUnloads);
    clientAllowUnloads.forEach(
        unload -> {
          unload.setClient(this);
          unload.setCreatedBy(this.getCreatedBy());
        });
  }

  public void removeFillers(List<ClientFillers> fillers) {
    this.fillers.removeAll(fillers);
    fillers.forEach(cfd -> cfd.setClient(null));
  }

  public void addFillers(List<ClientFillers> fillers) {
    this.fillers.clear();
    this.fillers.addAll(fillers);
    fillers.forEach(
        unload -> {
          unload.setClient(this);
          unload.setCreatedBy(this.getCreatedBy());
          unload.setLastModifiedBy(this.getLastModifiedBy());
        });
  }

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
    setLastModified(new Date());
    setCreatedBy(AuthenticationUtils.getPrincipal());
  }

  @PreUpdate
  private void update() {
    super.setLastModified(new Date());
    super.setLastModifiedBy(AuthenticationUtils.getPrincipal());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Client)) {
      return false;
    }
    Client that = (Client) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  public void addDivisions(List<Division> divisions) {
    this.divisions.clear();
    this.divisions.addAll(divisions);
    divisions.forEach(e -> e.setClient(this));
  }

  public void removeDivision(Division division) {
    this.divisions.remove(division);
    division.setClient(null);
  }

  public void setDivisions(List<Division> divisions) {
    this.divisions.clear();
    this.divisions.addAll(divisions);
  }
}
