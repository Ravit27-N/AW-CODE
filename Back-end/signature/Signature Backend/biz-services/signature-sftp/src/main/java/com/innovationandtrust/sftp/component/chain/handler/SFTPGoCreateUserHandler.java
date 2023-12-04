package com.innovationandtrust.sftp.component.chain.handler;

import com.innovationandtrust.sftp.constant.SFTPGoProcessConstant;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderRequest;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderResponse;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import com.innovationandtrust.utils.sftpgo.model.CreateUserProviderRequest;
import com.innovationandtrust.utils.sftpgo.model.SFTPGoFolderResponse;
import com.innovationandtrust.utils.sftpgo.model.SFTPGoUserResponse;
import com.innovationandtrust.utils.sftpgo.provider.SFTPGoServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SFTPGoCreateUserHandler extends AbstractExecutionHandler {

  private final SFTPGoServiceProvider sftpGoServiceProvider;

  public SFTPGoCreateUserHandler(SFTPGoServiceProvider sftpGoServiceProvider) {
    this.sftpGoServiceProvider = sftpGoServiceProvider;
  }

  @Override
  public ExecutionState execute(ExecutionContext context) {

    try {
      final var request =
          context
              .find(SFTPGoProcessConstant.REQUEST, SFTPUserFolderRequest.class)
              .orElseThrow(() -> new BadRequestException("Request not found"));

      final var sourceInFolder =
          context.get(SFTPGoProcessConstant.SOURCE_IN_INFO, SFTPGoFolderResponse.class);
      final var sourceOutFolder =
          context.get(SFTPGoProcessConstant.SOURCE_OUT_INFO, SFTPGoFolderResponse.class);

      final var userResponse =
          this.requestCreateUser(request, sourceInFolder.getName(), sourceOutFolder.getName());

      final var response = prepareResponse(request, userResponse);
      context.put(SFTPGoProcessConstant.RESPONSE, response);

      return ExecutionState.END;
    } catch (FeignClientRequestException e) {
      log.error("[SFTPGoCreateUserHandler] error while create user: {0}", e);
      return ExecutionState.END;
    }
  }

  private SFTPGoUserResponse requestCreateUser(
      SFTPUserFolderRequest req, String folderSourceIn, String folderSourceOut) {

    final CreateUserProviderRequest request =
        CreateUserProviderRequest.builder()
            .email(req.getEmail())
            .username(req.getUsername())
            .password(req.getPassword())
            .folderSourceIn(folderSourceIn)
            .folderSourceOut(folderSourceOut)
            .build();

    return this.sftpGoServiceProvider.createUser(request);
  }

  private static SFTPUserFolderResponse prepareResponse(
      SFTPUserFolderRequest req, SFTPGoUserResponse res) {
    return SFTPUserFolderResponse.builder()
        .username(res.getUsername())
        .email(res.getUsername())
        .corporateUuid(req.getCorporateUuid())
        .virtualFolders(res.getVirtualFolders())
        .build();
  }
}
