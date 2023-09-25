package com.tessi.cxm.pfl.ms15.core.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignAttachment;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignPreProcessingRequest;
import com.tessi.cxm.pfl.ms15.constant.PreProcessingConstant;
import com.tessi.cxm.pfl.ms15.exception.AttachmentException;
import com.tessi.cxm.pfl.ms15.exception.SendPjFailureException;
import com.tessi.cxm.pfl.ms15.exception.UserAuthorizationFailureException;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFileDocumentProduction;
import com.tessi.cxm.pfl.shared.model.FileMangerBase64;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.Attachment;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AttachmentsRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AttachmentsResponse;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.utils.DateUtils;
import com.tessi.cxm.pfl.shared.utils.HubDigitalFlowHelper;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Handle process of campaign attachments.
 *
 * @author Pisey CHORN.
 * @since 09 Jan 2023.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CampaignAttachmentHandler extends AbstractExecutionHandler {

  private final FileManagerResource fileManagerResource;
  private final HubDigitalFlow hubDigitalFlow;
  private final HubDigitalFlowHelper hubDigitalFlowHelper;

  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final String token = context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class);
    Long idCreator = context.get(FlowTreatmentConstants.ID_CREATOR, Long.class);
    String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    var campaignPreProcessingRequest =
        context.get(
            PreProcessingConstant.CAMPAIGN_PRE_PROCESSING_REQUEST,
            CampaignPreProcessingRequest.class);

    if (CollectionUtils.isEmpty(campaignPreProcessingRequest.getAttachments())) {
      return ExecutionState.NEXT;
    }
    // Download base64 from ms-file-manager of attachments.
    List<String> attachmentIds = this.getAttachmentIds(
        campaignPreProcessingRequest.getAttachments());
    List<FileMangerBase64> fileMangerBase64s = this.downFileMangerBase64s(attachmentIds, funcKey,
        privKey, token);

    // Mapping campaign attachment with base64.
    List<Attachment> attachments = mappingAttachments(fileMangerBase64s,
        campaignPreProcessingRequest);

    // Upload attachments to ms-hub-digital-flow.
    String authorization = this.getUserHubToken(campaignPreProcessingRequest.getCreatedBy());
    AttachmentsRequest attachmentsRequest = new AttachmentsRequest();
    attachmentsRequest.setAttachments(attachments);
    attachmentsRequest.setTechnicalReference(
        String.valueOf(campaignPreProcessingRequest.getIdCreator()));
    List<AttachmentsResponse> attachmentHubResponses = this.uploadAttachments(attachmentsRequest,
        idCreator,
        funcKey,
        privKey,
        authorization);

    // Add attachment to context.
    if (!CollectionUtils.isEmpty(attachmentHubResponses)) {
      List<String> uuid = attachmentHubResponses.stream().map(AttachmentsResponse::getUuid)
          .collect(Collectors.toList());

      PortalFileDocumentProduction.Attachment attachment = new PortalFileDocumentProduction.Attachment();
      attachment.setAttachments(uuid);
      context.put(PreProcessingConstant.HUB_ATTACHMENT_FILE_RESPONSE, attachment);
      final Map<String, String> attachmentFile =
          attachmentHubResponses.stream()
              .collect(
                  Collectors.toMap(AttachmentsResponse::getUuid, AttachmentsResponse::getHash));
      context.put(FlowTreatmentConstants.ATTACHMENTS, attachmentFile);
    }

    // Remove campaign attachments after upload to sendPj success.
    if (!CollectionUtils.isEmpty(attachmentHubResponses)) {
      removeCampaignAttachments(attachmentIds, token, funcKey, privKey);
    }

    return ExecutionState.NEXT;
  }

  /**
   * Remove campaign attachments that store in ms-file-manager.
   *
   * @param attachmentIds - collection of attachment id {@link String}.
   */
  private void removeCampaignAttachments(List<String> attachmentIds, String token, String funcKey, String privKey) {
    try {
      this.fileManagerResource.deleteMetadataFiles(attachmentIds, funcKey, privKey, token);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  /**
   * Build attachment object of {@link Attachment} with base64.
   *
   * @param fileMangerBase64s            - collections of {@link FileMangerBase64}.
   * @param campaignPreProcessingRequest - object of {@link CampaignPreProcessingRequest}.
   * @return - collections of {@link Attachment}.
   */
  private List<Attachment> mappingAttachments(List<FileMangerBase64> fileMangerBase64s,
      CampaignPreProcessingRequest campaignPreProcessingRequest) {
    int lifeTime = this.getLifeTime(campaignPreProcessingRequest.getDateSchedule(),
        campaignPreProcessingRequest.getCreatedAt());

    return campaignPreProcessingRequest.getAttachments().stream().map(campaignAttachment -> {
      var base64 = fileMangerBase64s.stream().filter(
              fileMangerBase64 -> fileMangerBase64.getFileId().equals(campaignAttachment.getFileId()))
          .map(FileMangerBase64::getBase64)
          .findFirst()
          .get();

      return Attachment.builder()
          .filename(campaignAttachment.getFilename())
          .contentType(campaignAttachment.getContentType())
          .content(base64)
          .hash(campaignAttachment.getHash())
          .lifeTime(lifeTime)
          .build();
    }).collect(Collectors.toList());
  }

  /**
   * Get attachmentIds from collections of campaign attachments.
   *
   * @param attachments - collections of {@link CampaignAttachment}.
   * @return collections of {@link String}.
   */
  private List<String> getAttachmentIds(List<CampaignAttachment> attachments) {
    return attachments.stream().map(CampaignAttachment::getFileId)
        .collect(Collectors.toList());
  }

  /**
   * Download base64 from ms-file-manager.
   *
   * @param fileIds - collections of {@link String}.
   * @return - collections of {@link FileMangerBase64}.
   */
  private List<FileMangerBase64> downFileMangerBase64s(List<String> fileIds, String funcKey,
      String privKey, String token) {
    if (!CollectionUtils.isEmpty(fileIds)) {
      try {
        return this.fileManagerResource.getFiles(fileIds, funcKey, privKey, token).stream()
            .map(
                fileMetadata ->
                    new FileMangerBase64(fileMetadata.getUuid(), fileMetadata.getContent()))
            .collect(Collectors.toList());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new AttachmentException("Fail to download multiple base64 from ms-file-manager");
      }
    }
    return Collections.emptyList();
  }

  /**
   * Upload attachment to ms-hub-digital-flow.
   *
   * @param attachmentsRequest - object of {@link AttachmentsRequest}.
   * @param authorization      - authorization header of ms-hub-digital-flow.
   * @return collections of {@link AttachmentsResponse}.
   */
  private List<AttachmentsResponse> uploadAttachments(AttachmentsRequest attachmentsRequest,
      Long idCreator, String funcKey, String privKey, String authorization) {
    if (!CollectionUtils.isEmpty(attachmentsRequest.getAttachments())) {
      try {
        return hubDigitalFlow.sendPj(attachmentsRequest,
            idCreator,
            funcKey,
            privKey,
            authorization);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new SendPjFailureException("Fail to upload attachment to hub-digital flow");
      }
    }
    return Collections.emptyList();
  }

  /**
   * Get user hub token by user created.
   *
   * @param userCreated - value of {@link String}.
   * @return - token of user hub{@link String}.
   */
  private String getUserHubToken(String userCreated) {
    try {
      return hubDigitalFlowHelper.getUserHubTokenByKeycloakAdmin(userCreated);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new UserAuthorizationFailureException("Fail to get user hub token");
    }
  }

  /**
   * Get life-time of attachments by calculation of (start date , end date).
   *
   * @param startDate - object of {@link Date}.
   * @param endDate   - object of {@link Date}.
   * @return - value of {@link Integer}.
   */
  private int getLifeTime(Date startDate, Date endDate) {
    var days = DateUtils.findDifferentDays(startDate, endDate) + 1;
    if (days > 90) {
      days = 90;
    }
    return Math.toIntExact(days);
  }
}
