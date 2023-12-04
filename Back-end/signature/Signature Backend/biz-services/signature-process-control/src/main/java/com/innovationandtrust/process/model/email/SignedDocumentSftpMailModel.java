package com.innovationandtrust.process.model.email;

import com.innovationandtrust.process.constant.InvitationTemplateConstant;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;

public class SignedDocumentSftpMailModel extends EmailInvitationModel {
  private final String sftpZipFile;

  public SignedDocumentSftpMailModel(
      String firstName,
      String zipFile,
      String email,
      String companyName,
      String theme) {
    super(firstName, null, null, null, null, email, companyName, theme);
    this.sftpZipFile = zipFile;
  }

  @Override
  public String getDefaultSubject() {
    return "SFTP n'a pas réussi à stocker le fichier manifeste";
  }

  @Override
  public String getBody(TemplateEngine templateEngine) {
    var context = this.getParamsContext();
    context.setVariable("zipFile", this.sftpZipFile);
    return templateEngine.process(InvitationTemplateConstant.SFTP_INSERT_FILE_FAIL, context);
  }
}
