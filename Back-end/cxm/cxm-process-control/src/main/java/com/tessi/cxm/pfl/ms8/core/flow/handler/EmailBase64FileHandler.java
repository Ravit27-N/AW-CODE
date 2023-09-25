package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.constant.ProcessControlConstants;
import com.tessi.cxm.pfl.shared.core.chains.Base64FileSupporter;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionException;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.AttachmentMailMessage;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AttachmentId;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class EmailBase64FileHandler extends Base64FileSupporter {

  protected EmailBase64FileHandler(FileManagerResource fileManagerResource) {
    super(fileManagerResource);
  }

  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    var portalJson =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    if (portalJson.getFlow().getType().contains(FlowTreatmentConstants.PORTAL_CAMPAIGN_EMAIL)) {
      String token = BearerAuthentication.PREFIX_TOKEN.concat(context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class));
      String funcKey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
      String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
      final String base64 =
          StringUtils.defaultIfBlank(
              context.get(FlowTreatmentConstants.BASE64, String.class),
              this.base64File(
                  context.get(FlowTreatmentConstants.COMPOSED_FILE_ID, String.class), funcKey,
                  privKey, token));
      try {
        final List<MultipartFile> files =
            this.convertBase64ToMultiPart(base64, FlowTreatmentConstants.HTML_EXTENSION);
        final Map<String, String> htmlContent =
            files.stream()
                .parallel()
                .collect(Collectors.toMap(this::getFileName, this::convertHtmlToPlainText));
        portalJson.getFlow().getFlowDocuments().stream()
            .findFirst()
            .ifPresent(
                document ->
                    context.put(
                        ProcessControlConstants.HTML_CONTENT_PORTAL_DEPOSIT_CAMPAIGN,
                        htmlContent.get(document.getDocUUID())));
        updateGenericEmailEmlBody(context, htmlContent, funcKey,privKey, token);
      } catch (IOException e) {
        log.error("Unable to convert file base64 to multipart", e);
        throw new ExecutionException("Unable to convert file base64 to multipart.");
      }
    }
    return ExecutionState.NEXT;
  }

  private String getFileName(MultipartFile multipartFile) {
    return FilenameUtils.getBaseName(multipartFile.getOriginalFilename());
  }

  private String convertHtmlToPlainText(MultipartFile multipartFile) {
    try {
      return this.convertInputStreamToString(multipartFile.getInputStream());
    } catch (IOException e) {
      log.error("Unable to convert file html to plain text", e);
      throw new ExecutionException("Unable to convert file html to plain text.");
    }
  }

  @SuppressWarnings("unchecked")
  private void updateGenericEmailEmlBody(
      ExecutionContext context, Map<String, String> htmlContent, String funcKey, String privKey, String token) {
    List<AttachmentMailMessage> attachmentMailMessages = new ArrayList<>();
    List<EmailToEmlFileConvertor.GenericEmailEmlBody> genericEmailEmlBodies =
        context.get(EmailToEmlFileConvertor.GENERIC_EMAIL_BODY, List.class);
    genericEmailEmlBodies.stream()
        .findFirst()
        .ifPresent(
            genericEmailEmlBody ->
                attachmentMailMessages.addAll(
                    this.getAttachmentMailMessage(genericEmailEmlBody.getAttachments(), funcKey, privKey, token)));
    genericEmailEmlBodies.parallelStream()
        .forEach(
            genericEmailEmlBody -> {
              genericEmailEmlBody.setBody(htmlContent.get(genericEmailEmlBody.getUuid()));
              genericEmailEmlBody.setAttachmentMailMessages(attachmentMailMessages);
            });
    context.put(EmailToEmlFileConvertor.GENERIC_EMAIL_BODY, genericEmailEmlBodies);
  }

  // get attachment file
  private List<AttachmentMailMessage> getAttachmentMailMessage(
      Map<String, String> orderAttachments, String funcKey, String privKey, String token) {
    final List<AttachmentId> attachmentIds =
        orderAttachments.keySet().stream().map(AttachmentId::new).collect(Collectors.toList());
    List<String> uuids =
        attachmentIds.stream().map(AttachmentId::getUuid).collect(Collectors.toList());
    var attachments = this.fileManagerResource.getFiles(uuids, funcKey, privKey, token);
    return attachments.stream()
        .map(
            attachment ->
                new AttachmentMailMessage(
                    attachment.getFileName(), attachment.getContent(), attachment.getContentType()))
        .collect(Collectors.toList());
  }
}
