package com.innovationandtrust.signature.identityverification.model.dto.shareid;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request class for ocr dto. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrDto {

  @JsonProperty("doc_num")
  private String docNum;

  private String surname;

  @JsonProperty("alternate_name")
  private String alternateName;

  @JsonProperty("widow_of")
  private String windowOf;

  @JsonProperty("mariage_name")
  private String mariageName;

  private String name;

  private String sex;

  @JsonProperty("date_of_birth")
  @JsonAlias("birth_date")
  private String dateOfBirth;

  @JsonProperty("place_of_birth")
  @JsonAlias("birth_place")
  private String placeOfBirth;

  private String size;

  private String address;

  @JsonProperty("expiration_date")
  private String expirationDate;

  @JsonProperty("issuance_date")
  private String issuanceDate;

  @JsonProperty("issuance_place")
  private String issuancePlace;

  @JsonProperty("authority_issuer_fr")
  private String authorityIssuerFr;

  @JsonProperty("authority_issuer")
  private String authorityIssuer;

  @JsonProperty("birth_name")
  private String birthName;

  @JsonProperty("type")
  private String type;

  @JsonProperty("remark")
  private String remark;

  @JsonProperty("nationality")
  private String nationality;

  @JsonProperty("num_etranger_recto")
  private String numEtrangerRecto;

  @JsonProperty("num_etranger_verso")
  private String numEtrangerVerso;

  @JsonProperty("mrz_1")
  private String mrz1;

  @JsonProperty("mrz_2")
  private String mrz2;

  @JsonProperty("mrz_3")
  private String mrz3;

  private String height;

  @JsonProperty("country_code")
  private String countryCode;

  @JsonProperty("address2")
  private String address2;

  @JsonProperty("eye_color")
  private String eyeColor;
}
