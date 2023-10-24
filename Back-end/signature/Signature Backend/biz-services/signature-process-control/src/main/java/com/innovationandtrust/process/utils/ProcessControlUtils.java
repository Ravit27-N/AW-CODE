package com.innovationandtrust.process.utils;

import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.exception.MissingCompanyInfoException;
import com.innovationandtrust.process.model.email.EmailInvitationRequestInterface;
import com.innovationandtrust.share.constant.ProjectStatus;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.exception.exceptions.ForbiddenRequestException;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.mail.provider.MailServiceProvider;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProcessControlUtils {

  public static ExecutionContext getProject(String flowId, String uuid) {
    var context = new ExecutionContext();
    context.put(SignProcessConstant.PROJECT_KEY, new Project(flowId));
    context.put(SignProcessConstant.PARTICIPANT_ID, uuid);
    return context;
  }

  public static Project getProject(ExecutionContext context) {
    return context
        .find(SignProcessConstant.PROJECT_KEY, Project.class)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "The project file flow is require and cannot be null;"));
  }

  public static String buildInvitationUrl(
      String flowId, String uuid, String frontUrl, String contextPath, String companyUuid) {
    if (StringUtils.hasText(companyUuid)) {
      return String.format(
          "%s/%s/%s?uuid=%s&company=%s", frontUrl, contextPath, flowId, uuid, companyUuid);
    }
    return String.format("%s/%s/%s?uuid=%s&company=", frontUrl, contextPath, flowId, uuid);
  }

  public static String buildInvitationParams(String flowId, String uuid, String companyUuid) {
    return String.format("flowId=%s&uuid=%s&company=%s", flowId, uuid, companyUuid);
  }

  public static MimeMessage getMessage(
      MailRequest request, Resource logo, MailServiceProvider mailServiceProvider) {
    if (logo == null) {
      return mailServiceProvider.prepareMimeMessage(
          request,
          Map.of(
              EmailInvitationRequestInterface.LOGO_KEY,
              new ClassPathResource(EmailInvitationRequestInterface.LOGO_PATH.toString())));
    }
    return mailServiceProvider.prepareMimeMessageAttachment(
        request, Map.of(EmailInvitationRequestInterface.LOGO_KEY, logo), "image/png");
  }

  public static String convertHtmlToString(String html) {
    var doc = Jsoup.parse(html);
    return doc.body().wholeText();
  }

  /**
   * To check if corporate info was null.
   *
   * @param corporateInfo refers to corporate info {@link CorporateInfo}
   * @param flowId refers to project flow id
   */
  public static void checkCompanyInfo(CorporateInfo corporateInfo, String flowId) {
    if (!Objects.nonNull(corporateInfo)) {
      log.error("Company was null ... ");
      throw new MissingCompanyInfoException(flowId);
    }
  }

  public static void checkIsCanceled(String status) {
    if (Objects.equals(status, ProjectStatus.ABANDON.name())) {
      throw new ForbiddenRequestException("Invalid action. Project is cancelled...");
    }
  }
}
