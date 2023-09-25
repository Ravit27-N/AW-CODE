package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.util.ProcessControlExecutionContextUtils;
import com.tessi.cxm.pfl.shared.core.chains.Base64FileSupporter;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import com.tessi.cxm.pfl.shared.core.mail.MailHandlerService;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.AttachmentMailMessage;
import com.tessi.cxm.pfl.shared.model.GenericMailMessage;
import com.tessi.cxm.pfl.shared.service.restclient.FileManagerResource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EmailToEmlFileConvertor extends Base64FileSupporter {
  public static final String GENERIC_EMAIL_BODY = "GENERIC_EMAIL_BODY";
  private final MailHandlerService mailHandlerService;

  protected EmailToEmlFileConvertor(
      FileManagerResource fileManagerResource, MailHandlerService mailHandlerService) {
    super(fileManagerResource);
    this.mailHandlerService = mailHandlerService;
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
  @SuppressWarnings("unchecked")
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final String token = ProcessControlExecutionContextUtils.getBearerToken(context);
    final String funckey = context.get(FlowTreatmentConstants.FUNC_KEY, String.class);
    final String privKey = context.get(FlowTreatmentConstants.PRIV_KEY, String.class);
    var portalJson =
        context.get(FlowTreatmentConstants.PORTAL_JSON_FILE_CONTROL, PortalFlowFileControl.class);
    if (portalJson.getFlow().getType().contains(FlowTreatmentConstants.PORTAL_CAMPAIGN_EMAIL)) {
      final String flowId = context.get(FlowTreatmentConstants.FLOW_UUID, String.class);
      if (context.get(flowId, Integer.class) == null) {
        List<GenericEmailEmlBody> document = context.get(GENERIC_EMAIL_BODY, List.class);
        document.parallelStream()
            .forEach(
                genericEmailBody -> {
                  var genericMail = getGenericMailMessage(genericEmailBody);
                  uploadEmlFile(
                      genericMail,
                      genericEmailBody.getFilename(),
                      genericEmailBody.getUuid(),
                      Long.parseLong(portalJson.getUserId()),
                      funckey,
                      privKey,
                      token);
                });
      }
    }
    return ExecutionState.NEXT;
  }

  private GenericMailMessage getGenericMailMessage(GenericEmailEmlBody genericEmailBody) {
    GenericMailMessage message = new GenericMailMessage();
    message.setFrom(
        String.format("%s <%s>", genericEmailBody.getSenderName(), genericEmailBody.getFrom()));
    message.setTo(genericEmailBody.getTo());
    message.setSubject(genericEmailBody.getSubject());
    message.setText(genericEmailBody.getBody());
    message.setSentDate(genericEmailBody.getSendDate());
    message.setFullName(genericEmailBody.getSenderName());
    message.setAttachments(genericEmailBody.getAttachmentMailMessages());
    return message;
  }

  public void uploadEmlFile(
      GenericMailMessage message,
      String filename,
      String uuid,
      long ownerId,
      String funckey,
      String privKey,
      String token) {
    final String emlFilename =
        StringUtils.replace(filename, FilenameUtils.getExtension(filename), "eml");
    final MimeMessage mimeMessage = this.mailHandlerService.getMimeMessageWithAttachment(message);
    final String contentType = new MimetypesFileTypeMap().getContentType(emlFilename);
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      mimeMessage.writeTo(out);
      final MultipartFile multipartFile =
          this.fileToMultiplePath(
              new ByteArrayInputStream(out.toByteArray()), contentType, emlFilename);

      uploadToFileManager(multipartFile, uuid, ownerId, funckey, privKey, token);
    } catch (IOException | MessagingException e) {
      log.error("Unable to upload eml file to fileManager", e);
      if (log.isDebugEnabled()) {
        log.error("Unable to upload eml file to fileManager", e);
      }
    }
  }

  @Setter
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class GenericEmailEmlBody {
    private String uuid; // identifier of document to reference of email.
    private String from;
    private String[] to;
    private String subject;
    private String body;
    private String senderName;
    private Date sendDate;
    private String filename;
    private Map<String, String> attachments; // <fileId,filename>
    private List<AttachmentMailMessage> attachmentMailMessages;
  }
}
