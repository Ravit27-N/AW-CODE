package com.innovationandtrust.signature.identityverification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.innovationandtrust.configuration.payload.ShareIdProperty;
import com.innovationandtrust.signature.identityverification.constant.dossier.DossierConstants;
import com.innovationandtrust.signature.identityverification.model.dto.shareid.AuthenticationRequest;
import com.innovationandtrust.signature.identityverification.model.dto.shareid.OnBoardingDemandDto;
import com.innovationandtrust.signature.identityverification.model.dto.shareid.ShareIdResponse;
import com.innovationandtrust.signature.identityverification.model.model.dossier.Dossier;
import com.innovationandtrust.signature.identityverification.model.model.dossier.DossierShareId;
import com.innovationandtrust.signature.identityverification.repository.DossierRepository;
import com.innovationandtrust.signature.identityverification.repository.DossierShareIdRepository;
import com.innovationandtrust.utils.file.config.FileProperties;
import com.innovationandtrust.utils.file.provider.FileProvider;
import java.util.Objects;
import java.util.Optional;

import com.innovationandtrust.utils.signatureidentityverification.dto.DocumentVerificationRequest;
import com.innovationandtrust.utils.signatureidentityverification.enums.DocumentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** Service class for share id. */
@Service
@EnableConfigurationProperties({ShareIdProperty.class})
@Slf4j
public class ShareIdService {

  private final ShareIdProperty shareIdProperty;
  private final WebClient shareIdClient;
  private final DossierShareIdRepository dossierShareIdRepository;
  private final DossierRepository dossierRepository;
  private final FileProvider fileProvider;
  private final FileProperties fileProperties;

  /**
   * Constructor for ShareIdServiceImpl.
   *
   * @param shareIdProperty shareIdProperty
   * @param shareIdClient webClient
   * @param dossierShareIdRepository dossierShareIdRepository
   * @param dossierRepository dossierRepository
   * @param fileProvider fileProvider
   */
  @Autowired
  public ShareIdService(
      ShareIdProperty shareIdProperty,
      WebClient shareIdClient,
      DossierShareIdRepository dossierShareIdRepository,
      DossierRepository dossierRepository,
      FileProvider fileProvider,
      FileProperties fileProperties,
      @Value("${share-id.base-url}") String baseUrl) {
    this.shareIdProperty = shareIdProperty;
    this.shareIdClient = shareIdClient.mutate().baseUrl(baseUrl).build();
    this.dossierShareIdRepository = dossierShareIdRepository;
    this.dossierRepository = dossierRepository;
    this.fileProvider = fileProvider;
    this.fileProperties = fileProperties;
  }

  /**
   * Retrieve response if onboarding otherwise retrieve authentication response.
   *
   * @return onboarding response as object
   */
  public Mono<ShareIdResponse> invokeOnboardingResponse(boolean isOnboarding) {
    return shareIdClient
        .post()
        .uri("/authorization/v1/auth_business/login")
        .bodyValue(isOnboarding ? shareIdProperty : new AuthenticationRequest(shareIdProperty))
        .retrieve()
        .bodyToMono(ShareIdResponse.class);
  }

  /**
   * handle callback.
   *
   * @param onBoardingDemandDto on boarding demand dto
   * @return String as response
   */
  @Transactional
  public String callback(OnBoardingDemandDto onBoardingDemandDto) {
    String externalId = onBoardingDemandDto.getMetadata().getExternalId();
    if (externalId.contains("---")) {
      externalId = externalId.split("---")[0];
      onBoardingDemandDto.getMetadata().setExternalId(externalId);
    }

    if (StringUtils.isNotEmpty(onBoardingDemandDto.getDocumentFront())) {
      var frontDocPath =
          fileProvider.uploadImage(
              onBoardingDemandDto.getDocumentFront(), DossierConstants.UPLOAD_DIRECTORY);
      onBoardingDemandDto.setDocumentFront(frontDocPath);
    }
    if (StringUtils.isNotEmpty(onBoardingDemandDto.getDocumentBack())) {
      var backDocPath =
          fileProvider.uploadImage(
              onBoardingDemandDto.getDocumentBack(), DossierConstants.UPLOAD_DIRECTORY);
      onBoardingDemandDto.setDocumentBack(backDocPath);
    }

    Optional<Dossier> dossier = dossierRepository.findByDossierId(externalId);
    dossier.ifPresent(
        value ->
            dossierShareIdRepository
                .findById(value.getDossierShareId() != null ? value.getDossierShareId().getId() : 0)
                .ifPresentOrElse(
                    dossierShareId -> {
                      DossierShareId dossierShareIdUpdate = new DossierShareId(onBoardingDemandDto);
                      dossierShareIdUpdate.setId(dossierShareId.getId());
                      if (StringUtils.isNotEmpty(onBoardingDemandDto.getDocumentFront())) {
                        dossierShareIdUpdate.setBasePath(fileProperties.getBasePath());
                      }
                      dossierShareIdRepository.save(dossierShareIdUpdate);
                    },
                    () -> {
                      DossierShareId dossierShareId = new DossierShareId(onBoardingDemandDto);
                      if (StringUtils.isNotEmpty(onBoardingDemandDto.getDocumentFront())) {
                        dossierShareId.setBasePath(fileProperties.getBasePath());
                      }
                      dossierShareId = dossierShareIdRepository.save(dossierShareId);
                      dossierRepository.updateDossierShareIdId(
                          dossierShareId.getId(), value.getId());
                    }));

    return "SUCCESS";
  }

  public JsonNode invokeDocumentVerificationResponse(DocumentVerificationRequest request) {
    return this.shareIdClient
        .post()
        .uri(DossierConstants.DOC_VERIFICATION_URL)
        .bodyValue(this.buildDocForm(request))
        .headers(
            httpHeader ->
                httpHeader.setBearerAuth(
                    Objects.requireNonNull(this.invokeOnboardingResponse(false).block())
                        .getPayload()
                        .getAccessToken()))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();
  }

  private MultiValueMap<String, Object> buildDocForm(DocumentVerificationRequest request) {
    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add(DossierConstants.DOCUMENT_FRONT, request.getDocumentFront().getResource());
    if (!request.getDocumentType().equals(DocumentType.PASSPORT)) {
      formData.add(
          DossierConstants.DOCUMENT_BACK,
          Objects.requireNonNull(request.getDocumentBack()).getResource());
    }
    formData.add(DossierConstants.DOCUMENT_ROTATION, request.getDocumentRotation().getValue());
    formData.add(DossierConstants.DOCUMENT_TYPE, request.getDocumentType().getValue());
    formData.add(DossierConstants.DOCUMENT_COUNTRY, request.getDocumentCountry().getValue());
    return formData;
  }
}
