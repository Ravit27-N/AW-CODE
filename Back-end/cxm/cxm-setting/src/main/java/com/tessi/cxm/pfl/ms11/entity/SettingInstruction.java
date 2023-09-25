package com.tessi.cxm.pfl.ms11.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@SequenceGenerator(
    name = "SETTING_INSTRUCTION_SEQUENCE_GENERATOR",
    sequenceName = "SETTING_INSTRUCTION_SEQUENCE",
    allocationSize = 1)
public class SettingInstruction implements Serializable {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "SETTING_INSTRUCTION_SEQUENCE_GENERATOR")
  private Long id;

  private String flowName;

  private String codeTemplate;

  private String idTemplate;

  private String template;

  private String modelType;

  private String channel;

  private String subChannel;

  private String idBreakingPage;

  private String breakingPage;

  private String idRecipientId;

  private String recipientId;

  private String idEmailRecipient;

  private String emailRecipient;

  private String idEmailObject;

  private String emailObject;

  private String address;

  private String pjs;

  private String data;

  private String others;

  @JoinColumn(name = "setting_id", referencedColumnName = "id")
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @ToString.Exclude
  private Setting setting;
}
