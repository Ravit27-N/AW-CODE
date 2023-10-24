package com.innovationandtrust.utils.aping.feignclient;

import com.innovationandtrust.utils.aping.config.ApiNgFeignClientFacadeConfiguration;
import com.innovationandtrust.utils.aping.model.ApprovalRequest;
import com.innovationandtrust.utils.aping.model.CaCguResponse;
import com.innovationandtrust.utils.aping.model.CertificateRequest;
import com.innovationandtrust.utils.aping.model.CloseSession;
import com.innovationandtrust.utils.aping.model.Document;
import com.innovationandtrust.utils.aping.model.GenerateOtpRequest;
import com.innovationandtrust.utils.aping.model.ManifestData;
import com.innovationandtrust.utils.aping.model.RecipientRequest;
import com.innovationandtrust.utils.aping.model.RefuseRequest;
import com.innovationandtrust.utils.aping.model.ResponseData;
import com.innovationandtrust.utils.aping.model.Scenario;
import com.innovationandtrust.utils.aping.model.Session;
import com.innovationandtrust.utils.aping.model.SignRequest;
import com.innovationandtrust.utils.aping.model.ValidateOtpRequest;
import com.innovationandtrust.utils.aping.signing.Actor;
import com.innovationandtrust.utils.aping.signing.GeneratedOTP;
import feign.Headers;
import feign.Param;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

// TEST
@FeignClient(
    value = "api-ng-facade",
    url = "${signature.api-ng.url}",
    path = "${signature.api-ng.context-path}",
    configuration = ApiNgFeignClientFacadeConfiguration.class)
public interface ApiNgFeignClientFacade {

  @GetMapping("/session/{sessionId}/actor/{actorId}")
  Actor getActor(@PathVariable("sessionId") Long sessionId, @PathVariable("actorId") Long actorId);

  @PostMapping("/session/{sessionId}/actors")
  ResponseData createActor(@PathVariable("sessionId") Long sessionId, @RequestBody Actor actor);

  @PostMapping("/sessions")
  ResponseData createSession(Session session);

  default byte[] downloadManifest(Long sessionId) {
    var currentUrl = this.sessionManifest(sessionId).getUrl();
    return this.downloadDocument(this.getIdAtIndex(currentUrl, 1));
  }

  @GetMapping("/session/{id}/manifest")
  ResponseData sessionManifest(@PathVariable("id") Long sessionId);

  @PutMapping("/session/{id}/close")
  void closeSession(@PathVariable("id") Long sessionId, @RequestBody CloseSession session);

  default ResponseData uploadFile(String name, String filename, byte[] content) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);

    ContentDisposition contentDisposition =
        ContentDisposition.builder("form-data").name(name).filename(filename).build();
    headers.setContentDisposition(contentDisposition);

    return this.uploadDocument(content, headers);
  }

  @PostMapping("/uploads")
  @Headers("Content-Type: application/pdf")
  ResponseData uploadDocument(@Param("file") byte[] content, @RequestHeader HttpHeaders headers);

  @PostMapping("/session/{sessionId}/documents")
  ResponseData addDocument(
      @PathVariable("sessionId") Long sessionId, @RequestBody Document document);

  @GetMapping("/session/{sessionId}/document/{documentId}/current")
  ResponseData getCurrentOfDocument(
      @PathVariable("sessionId") Long sessionId, @PathVariable("documentId") Long documentId);

  @GetMapping("/download/{documentId}")
  byte[] downloadDocument(@PathVariable("documentId") Long documentId);

  default byte[] downloadDocument(String url) {
    var currentUrl =
        this.getCurrentOfDocument(this.getIdAtIndex(url, 3), this.getIdAtIndex(url, 1)).getUrl();
    return this.downloadDocument(this.getIdAtIndex(currentUrl, 1));
  }

  @PostMapping("/session/{sessionId}/scenarios")
  ResponseData createScenario(
      @PathVariable("sessionId") Long sessionId, @RequestBody Scenario scenario);

  @PutMapping("/session/{sessionId}/scenario/{scenarioId}/activate")
  ResponseData activeScenario(
      @PathVariable("sessionId") Long sessionId,
      @PathVariable("scenarioId") Long scenarioId,
      @RequestBody ManifestData scenario);

  @PutMapping("/session/{sessionId}/generate-otp")
  GeneratedOTP generateOTP(
      @PathVariable("sessionId") Long sessionId,
      @RequestBody GenerateOtpRequest generateOtpRequest);

  @PutMapping("/session/{id}/check-otp")
  ValidateOtpRequest validateOTP(
      @PathVariable("id") Long sessionId, @RequestBody ValidateOtpRequest request);

  @GetMapping("/ca/{caId}/cgu")
  CaCguResponse getCaCGU(
      @PathVariable("caId") long caId,
      @RequestParam("session") Long sessionId,
      @RequestParam("actor") Long actorId);

  @PostMapping("/session/{id}/certificates")
  Map<String, String> generateCertificate(
      @PathVariable("id") Long sessionId, @RequestBody CertificateRequest request);

  @GetMapping("/session/{id}/certificates")
  Map<String, String> getCertificate(
      @PathVariable("id") Long sessionId,
      @RequestParam("caid") Long caId,
      @RequestParam("actorIds") List<Long> actorIds);

  @PutMapping("/session/{id}/direct-sign-documents")
  Map<String, Object> signDocuments(
      @PathVariable("id") Long sessionId, @RequestBody SignRequest request);

  @PutMapping("/session/{id}/approve-documents")
  Map<String, Object> approveDocuments(
      @PathVariable("id") Long sessionId, @RequestBody ApprovalRequest request);

  @PutMapping("/session/{id}/refuse")
  Map<String, Object> refuseDocuments(
      @PathVariable("id") Long sessionId, @RequestBody RefuseRequest request);

  @PutMapping("/session/{id}/recipient")
  Map<String, Object> completeSignProcess(
      @PathVariable("id") Long sessionId, @RequestBody RecipientRequest request);

  default Long getIdAtIndex(String url, int lastIndex) {
    String[] parts = url.split("/");
    return Long.parseLong(parts[parts.length - lastIndex]);
  }

  @PutMapping("/session/{id}/extendSession")
  ResponseData extendSession(@PathVariable("id") Long sessionId, @RequestBody Session session);

  @GetMapping("/session/{id}")
  Map<String, Object> getSession(@PathVariable("id") Long sessionId);
}
