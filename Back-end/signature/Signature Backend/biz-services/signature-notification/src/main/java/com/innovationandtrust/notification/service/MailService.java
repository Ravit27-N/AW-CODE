package com.innovationandtrust.notification.service;

import com.innovationandtrust.notification.utils.NotificationUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.mail.provider.MailServiceProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MailService {
  private final MailServiceProvider mailServiceProvider;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private Resource resource;

  public void send(MailRequest mailRequest, String logo) {
    Resource logoFile = logo != null ? this.getLogoFile(logo) : null;
    this.mailServiceProvider.sendMail(
        NotificationUtils.getMessage(mailRequest, logoFile, mailServiceProvider));
  }

  public void sendMultiple(List<MailRequest> mailRequests, String logo) {
    Resource logoFile = logo != null ? this.getLogoFile(logo) : null;
    mailRequests.forEach(
        mail ->
            this.mailServiceProvider.sendMail(
                NotificationUtils.getMessage(mail, logoFile, mailServiceProvider)));
  }

  private Resource getLogoFile(String logo) {
    if (StringUtils.hasText(logo)) {
      this.resource = this.corporateProfileFeignClient.viewFileContent(logo);
    }
    return this.resource;
  }
}
