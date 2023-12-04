package com.innovationandtrust.sftp.constant;

import com.innovationandtrust.share.model.sftp.SFTPUserFolderRequest;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderResponse;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.sftpgo.model.SFTPGoFolderResponse;
import com.innovationandtrust.utils.sftpgo.model.SFTPGoUserResponse;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {

  public static final String MESSAGE_NOT_NULL = "Result must not be null";
  public static final String MESSAGE_ASSERT_THROW = "The exception is be.";

  public static SFTPUserFolderRequest getUserFolderRequest() {
    return SFTPUserFolderRequest.builder()
        .username("test")
        .email("test@gmail.com")
        .password("test")
        .corporateUuid("1313c0e7-7703-45e8-b409-b3fd58e18beb")
        .build();
  }

  public static SFTPUserFolderResponse getUserFolderResponse() {
    return SFTPUserFolderResponse.builder()
        .username("test")
        .email("test@gmail.com")
        .corporateUuid("1313c0e7-7703-45e8-b409-b3fd58e18beb")
        .build();
  }

  public static SFTPGoFolderResponse getSftpGoFolderResponse() {
    return new SFTPGoFolderResponse("folder", "mappedPath", "description", 1L, 1, 1L);
  }

  public static ExecutionContext setUpContext() {
    ExecutionContext context = new ExecutionContext();
    context.put(SFTPGoProcessConstant.REQUEST, Constant.getUserFolderRequest());
    return context;
  }

  public static SFTPGoUserResponse getSftpGoUserResponse() {
    return new SFTPGoUserResponse("test", "test", "test", new ArrayList<>());
  }
}
