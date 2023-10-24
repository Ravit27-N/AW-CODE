package com.innovationandtrust.signature.identityverification.model.model.dossier;

import com.innovationandtrust.signature.identityverification.constant.dossier.DossierShareIdEntity;
import com.innovationandtrust.signature.identityverification.model.dto.shareid.OnBoardingDemandDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DossierShareId entity. */
@Entity
@Table(name = DossierShareIdEntity.TABLE_NAME)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DossierShareId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = DossierShareIdEntity.DOCUMENT_NUMBER)
  private String documentNumber;
  @Column(name = DossierShareIdEntity.SURNAME)
  private String surname;
  @Column(name = DossierShareIdEntity.ALTERNATE_NAME)
  private String alternateName;
  @Column(name = DossierShareIdEntity.WIDOW_OF)
  private String widowOf;
  @Column(name = DossierShareIdEntity.MARIAGE_NAME)
  private String mariageName;
  @Column(name = DossierShareIdEntity.NAME)
  private String name;

  @Column(name = DossierShareIdEntity.SEX)
  private String sex;

  @Column(name = DossierShareIdEntity.DATE_OF_BIRTH)
  private String dateOfBirth;
  @Column(name = DossierShareIdEntity.PLACE_OF_BIRTH)
  private String placeOfBirth = "";
  @Column(name = DossierShareIdEntity.SIZE)
  private String size;
  @Column(name = DossierShareIdEntity.ADDRESS)
  private String address;
  @Column(name = DossierShareIdEntity.EXPIRATION_DATE)
  private String expirationDate;
  @Column(name = DossierShareIdEntity.ISSUANCE_DATE)
  private String issuanceDate;
  @Column(name = DossierShareIdEntity.ISSUANCE_PLACE)
  private String issuancePlace;
  @Column(name = DossierShareIdEntity.AUTHORITY_ISSUER)
  private String authorityIssuer;
  @Column(name = DossierShareIdEntity.AUTHORITY_ISSUER_FR)
  private String authorityIssuerFr;
  @Column(name = DossierShareIdEntity.ADDRESS2)
  private String address2;

  @Column(name = DossierShareIdEntity.DOCUMENT_TYPE)
  private String documentType;
  @Column(name = DossierShareIdEntity.DOCUMENT_YEAR)
  private String documentYear;

  @Column(name = DossierShareIdEntity.DOCUMENT_FRONT)
  private String documentFront;
  @Column(name = DossierShareIdEntity.DOCUMENT_BACK)
  private String documentBack;
  @Column(name = DossierShareIdEntity.BASE_PATH)
  private String basePath;

  @Column(name = DossierShareIdEntity.FIRST_NAME)
  private String firstName;
  @Column(name = DossierShareIdEntity.LAST_NAME)
  private String lastName;

  @Column(name = DossierShareIdEntity.BIRTH_NAME)
  private String birthName;
  @Column(name = DossierShareIdEntity.CITY)
  private String city;
  @Column(name = DossierShareIdEntity.COUNTRY)
  private String country;
  @Column(name = DossierShareIdEntity.TYPE)
  private String type;
  @Column(name = DossierShareIdEntity.REMARK)
  private String remark;
  @Column(name = DossierShareIdEntity.NATIONALITY)
  private String nationality;
  @Column(name = DossierShareIdEntity.NUM_ETRANGER_RECTO)
  private String numEtrangerRecto;
  @Column(name = DossierShareIdEntity.NUM_ETRANGER_VERSO)
  private String numEtrangerVerso;
  @Column(name = DossierShareIdEntity.MRZ1)
  private String mrz1;
  @Column(name = DossierShareIdEntity.MRZ2)
  private String mrz2;
  @Column(name = DossierShareIdEntity.MRZ3)
  private String mrz3;

  @OneToMany(mappedBy = "dossierShareId")
  private Set<Dossier> dossier = new HashSet<>();

  /**
   * Constructor.
   *
   * @param demandDto the demand dto
   */
  public DossierShareId(OnBoardingDemandDto demandDto) {
    var document = demandDto.getDocument();
    var ocrDto = document.getOcr();
    var typeDto = document.getType();

    this.documentNumber = ocrDto.getDocNum();
    this.surname = ocrDto.getSurname();
    this.alternateName = ocrDto.getAlternateName();
    this.widowOf = ocrDto.getWindowOf();
    this.mariageName = ocrDto.getMariageName();
    this.name = ocrDto.getName();
    this.sex = ocrDto.getSex();
    this.dateOfBirth = ocrDto.getDateOfBirth();
    this.placeOfBirth = ocrDto.getPlaceOfBirth();
    if (ocrDto.getPlaceOfBirth() != null) {
      String[] birthPlaceParts = ocrDto.getPlaceOfBirth().trim().split(", ", 2);
      this.city = birthPlaceParts[0];
      this.country = birthPlaceParts.length > 1 ? birthPlaceParts[1] : "";
    }
    this.size = ocrDto.getSize();
    this.address = ocrDto.getAddress();
    this.expirationDate = ocrDto.getExpirationDate();
    this.issuanceDate = ocrDto.getIssuanceDate();
    this.issuancePlace = ocrDto.getIssuancePlace();
    this.authorityIssuer = ocrDto.getAuthorityIssuer();
    this.authorityIssuerFr = ocrDto.getAuthorityIssuerFr();

    this.documentType = typeDto.getDocumentType();
    this.documentYear = typeDto.getDocumentYear();

    this.documentBack = demandDto.getDocumentBack();
    this.documentFront = demandDto.getDocumentFront();

    this.firstName = ocrDto.getName();
    this.lastName = ocrDto.getSurname();

    this.birthName = ocrDto.getBirthName();
    this.type = ocrDto.getType();
    this.remark = ocrDto.getRemark();
    this.nationality = ocrDto.getNationality();
    this.numEtrangerRecto = ocrDto.getNumEtrangerRecto();
    this.numEtrangerVerso = ocrDto.getNumEtrangerVerso();
    this.mrz1 = ocrDto.getMrz1();
    this.mrz2 = ocrDto.getMrz2();
    this.mrz3 = ocrDto.getMrz3();
  }
}
