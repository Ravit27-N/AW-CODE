package com.innovationandtrust.sftp.service;

import com.innovationandtrust.sftp.model.EmailModel;
import com.innovationandtrust.sftp.model.ErrorEmail;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.sftp.restclient.NotificationFeignClient;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
@RequiredArgsConstructor
public class MailService {
  private final TemplateEngine templateEngine;
  private final NotificationFeignClient notificationFeignClient;
  private final IKeycloakTokenExchange keycloakTokenExchange;

  public void sendErrorMail(SftpFileRequest request, EmailModel emailModel) {
    var token =
        String.format(
            "Bearer %s",
            keycloakTokenExchange.getToken(
                request.getCorporateUser().getNormalUser().getUserEntityId()));
    List<MailRequest> mailRequests = new ArrayList<>();
    // Corporate user mail
    var errorEmailCorporate =
        new ErrorEmail(
            request.getCorporateUser().getEmail(),
            request.getCorporateUser().getUserEntityId(),
            emailModel.getSubject(),
            emailModel.getMessage() + request.getFilename());
    mailRequests.add(errorEmailCorporate.getMailRequest(templateEngine));
    // Normal user mail
    if (Objects.nonNull(request.getCorporateUser().getNormalUser())) {
      var errorEmailNormal =
          new ErrorEmail(
              request.getCorporateUser().getNormalUser().getEmail(),
              request.getCorporateUser().getNormalUser().getUserEntityId(),
              emailModel.getSubject(),
              emailModel.getMessage() + request.getFilename());
      mailRequests.add(errorEmailNormal.getMailRequest(templateEngine));
    }
    Executors.newSingleThreadExecutor()
        .execute(() -> this.notificationFeignClient.sendMultiple(mailRequests, null, token));
  }
}
