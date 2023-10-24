package com.innovationandtrust.signature.identityverification.service;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.innovationandtrust.signature.identityverification.constant.dossier.DossierConstants;
import com.innovationandtrust.signature.identityverification.exception.UserFinishedVerifiedDocumentException;
import com.innovationandtrust.signature.identityverification.exception.UserNotAllowedException;
import com.innovationandtrust.signature.identityverification.model.dto.dossier.DossierIdResponse;
import com.innovationandtrust.signature.identityverification.model.dto.dossier.DossierResponse;
import com.innovationandtrust.signature.identityverification.model.dto.shareid.OcrDto;
import com.innovationandtrust.signature.identityverification.model.model.dossier.Dossier;
import com.innovationandtrust.signature.identityverification.model.model.dossier.DossierShareId;
import com.innovationandtrust.signature.identityverification.model.model.dossier.Status;
import com.innovationandtrust.signature.identityverification.model.model.dossier.Step;
import com.innovationandtrust.signature.identityverification.repository.DossierRepository;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.file.config.FileProperties;
import com.innovationandtrust.utils.file.model.FileResponse;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import com.innovationandtrust.utils.signatureidentityverification.dto.VerificationDocumentResponse;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Provide a service for working with dossier. */
@Service
@RequiredArgsConstructor
@Slf4j
public class DossierService {
  private final DossierRepository repository;
  private final ShareIdService shareIdService;
  private final FileProvider fileProvider;
  private final ModelMapper mapper;
  private final FileProperties fileProperties;

  public DossierIdResponse createDossier(DossierDto dto) {
    try {
      var savedDossier = this.repository.save(new Dossier(dto));
      return DossierIdResponse.builder()
          .dossierId(savedDossier.getDossierId())
          .participantUuid(savedDossier.getParticipantUuid())
          .build();
    } catch (DataIntegrityViolationException exception) {
      throw new DataIntegrityViolationException(
          String.format(DossierConstants.DOSSIER_ALREADY_EXIST));
    }
  }

  public List<DossierIdResponse> createDossiers(List<DossierDto> dtoS) {
    try {
      var savedDossiers = this.repository.saveAll(dtoS.stream().map(Dossier::new).toList());
      return savedDossiers.stream()
          .map(
              dossier ->
                  DossierIdResponse.builder()
                      .dossierId(dossier.getDossierId())
                      .participantUuid(dossier.getParticipantUuid())
                      .build())
          .toList();
    } catch (DataIntegrityViolationException exception) {
      throw new DataIntegrityViolationException(
          String.format(DossierConstants.DOSSIER_ALREADY_EXIST));
    }
  }

  public Dossier getDossierByDossierId(String dossierId) {
    return repository
        .findByDossierId(dossierId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format(DossierConstants.DOSSIER_ID_NOT_EXIST, dossierId)));
  }

  public void confirmDossier(String dossierId) {
    var dossier = this.getDossierByDossierId(dossierId);
    dossier.setUserInCurrentStep(Step.PHONE);
    repository.save(dossier);
  }

  public void validateDossier(String dossierId) {
    var dossier = this.getDossierByDossierId(dossierId);
    dossier.setStatus(Status.VERIFIED);
    repository.save(dossier);
  }

  public void updateDossierUuid(String dossierId, String uuid) {
    var dossier = this.getDossierByDossierId(dossierId);

    if (!dossier.getStatus().equals(Status.VERIFIED)) {
      throw new UserNotAllowedException(DossierConstants.USER_BANNED);
    }

    dossier.setStatus(Status.DOCUMENT_VERIFIED);
    dossier.setUuid(uuid);
    repository.save(dossier);
  }

  public DossierResponse getDossierDto(String dossierId) {
    var response = this.getDossierByDossierId(dossierId);
    return new DossierResponse(response);
  }

  public VerificationDocumentResponse verifyDocument(
      String dossierId, DocumentVerificationRequest request) {
    var dossier = this.getDossierByDossierId(dossierId);

    this.validateDossierCanBeVerified(dossier);

    var response = this.shareIdService.invokeDocumentVerificationResponse(request);
    var payload = requireNonNull(requireNonNull(response).get("payload"));

      this.handleOnDocumentValid(payload, request, dossier);
      this.repository.save(dossier);

    return VerificationDocumentResponse.builder()
        .id(payload.get("id").asText())
        .authenticity(true)
        .trace(response.get("trace").asText())
        .status(response.get("status").asText())
        .message(response.get("message").asText())
        .build();
  }

  private void validateDossierCanBeVerified(Dossier dossier) {
    if (dossier.getStatus().equals(Status.DOCUMENT_VERIFIED)) {
      throw new UserFinishedVerifiedDocumentException(
          DossierConstants.USER_FINISHED_DOCUMENT_VERIFICATION);
    }

    if (!dossier.getStatus().equals(Status.VERIFIED)) {
      throw new UserNotAllowedException(DossierConstants.USER_BANNED);
    }
  }

  private void handleOnDocumentValid(
      JsonNode node, DocumentVerificationRequest request, Dossier dossier) {
    OcrDto ocrDto = this.mapVerificationResponseToOcr(node);
    var dossierShareId = this.mapper.map(ocrDto, DossierShareId.class);
    var uploadDocumentResponse =
        this.uploadDocument(dossierShareId, request.getDocumentFront(), request.getDocumentBack());
    dossier.setDossierShareId(uploadDocumentResponse);
    dossierShareId.setDossier(Set.of(dossier));
    dossier.setStatus(Status.DOCUMENT_VERIFIED);
    dossier.setDocumentType(request.getDocumentType());
    this.repository.save(dossier);
  }

  private boolean isDocumentValid(JsonNode object) {
    return Objects.nonNull(object) && object.get("authenticity").asBoolean();
  }

  private DossierShareId uploadDocument(
      DossierShareId dossierShareId, MultipartFile front, MultipartFile back) {

    if (Objects.nonNull(back)) {
      var res = this.uploadDoc(back);
      dossierShareId.setDocumentBack(
          Paths.get(DossierConstants.UPLOAD_DIRECTORY, res.getFileName()).toString());
    }

    var res = this.uploadDoc(front);
    dossierShareId.setDocumentFront(
        Paths.get(DossierConstants.UPLOAD_DIRECTORY, res.getFileName()).toString());

    dossierShareId.setBasePath(fileProperties.getBasePath());

    return dossierShareId;
  }

  private FileResponse uploadDoc(MultipartFile doc) {
    return fileProvider.upload(doc, DossierConstants.UPLOAD_DIRECTORY, false);
  }

  private OcrDto mapVerificationResponseToOcr(JsonNode response) {
    OcrDto ocrDto = new OcrDto();
    if (Objects.nonNull(response)) {
      response
          .get("ocr")
          .forEach(
              eachField -> {
                String value = eachField.get("value").asText();
                switch (eachField.get("variable_name").asText()) {
                  case "doc_num" -> ocrDto.setDocNum(value);
                  case "surname" -> ocrDto.setSurname(value);
                  case "name" -> ocrDto.setName(value);
                  case "nationality" -> ocrDto.setNationality(value);
                  case "date_of_birth", "birth_date" -> ocrDto.setDateOfBirth(value);
                  case "sex" -> ocrDto.setSex(value);
                  case "place_of_birth", "birth_place" -> ocrDto.setPlaceOfBirth(value);
                  case "issuance_date" -> ocrDto.setIssuanceDate(value);
                  case "issuance_place" -> ocrDto.setIssuancePlace(value);
                  case "type" -> ocrDto.setType(value);
                  case "expiration_date" -> ocrDto.setExpirationDate(value);
                  case "authority_issuer" -> ocrDto.setAuthorityIssuer(value);
                  case "address" -> ocrDto.setAddress(value);
                  case "address2" -> ocrDto.setAddress2(value);
                  case "height" -> ocrDto.setHeight(value);
                  case "eye_color" -> ocrDto.setEyeColor(value);
                  case "mrz_1" -> ocrDto.setMrz1(value);
                  case "mrz_2" -> ocrDto.setMrz2(value);
                  case "mrz_3" -> ocrDto.setMrz3(value);
                  default -> log.info("No match found");
                }
              });
    }
    return ocrDto;
  }
}
