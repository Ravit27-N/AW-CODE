package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.ms5.constant.AddressType;
import com.tessi.cxm.pfl.ms5.entity.converter.AddressTypeAttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@SequenceGenerator(
    name = "RETURN_ADDRESS_SEQUENCE_GENERATOR",
    sequenceName = "RETURN_ADDRESS_SEQUENCE",
    allocationSize = 1)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"refId", "type"})})
public class ReturnAddress {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "RETURN_ADDRESS_SEQUENCE_GENERATOR")
  private Long id;

  @NotNull
  @Column(updatable = false)
  @Min(value = 1, message = "refId must greater than or equal to 1")
  private Long refId;

  @Column(name = "type", updatable = false)
  @NotNull
  @Convert(converter = AddressTypeAttributeConverter.class)
  private AddressType type;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_id")
  public Client client;

  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String line5;

  @Column(nullable = false)
  private String line6;

  private String line7;
}
