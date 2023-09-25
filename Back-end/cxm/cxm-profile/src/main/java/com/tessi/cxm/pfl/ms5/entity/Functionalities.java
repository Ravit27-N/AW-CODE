package com.tessi.cxm.pfl.ms5.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(
    name = "FUNCTIONALITIES_SEQUENCE_GENERATOR",
    sequenceName = "FUNCTIONALITIES_SEQUENCE_SEQUENCE",
    allocationSize = 1)
@Table(name = "functionality")
public class Functionalities implements Serializable {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "FUNCTIONALITIES_SEQUENCE_GENERATOR")
  private long id;

  @Column(unique = true)
  private String key;

  @OneToMany(mappedBy = "functionalities", fetch = FetchType.LAZY)
  private List<ClientFunctionalitiesDetails> clientFunctionalitiesDetails = new ArrayList<>();
}
