package com.innovationandtrust.sftp.constant;

import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.share.model.project.Project;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SftpProcessConstant {

  public static final String SFTP_FILE_REQUEST = SftpFileRequest.class.getName();

  public static final String PROJECT_KEY = Project.class.getName();
}
