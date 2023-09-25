package com.tessi.cxm.pfl.ms11.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
    name = "RESOURCE_TYPE_TRANSLATE_SEQUENCE_GENERATOR",
    sequenceName = "RESOURCE_TYPE_TRANSLATE_SEQUENCE",
    allocationSize = 1)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"language", "key"})
    })
public class ResourceTypeTranslate {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "RESOURCE_TYPE_TRANSLATE_SEQUENCE_GENERATOR")
  private Long id;

  private String key;
  private String translate;
  private String language;

}
