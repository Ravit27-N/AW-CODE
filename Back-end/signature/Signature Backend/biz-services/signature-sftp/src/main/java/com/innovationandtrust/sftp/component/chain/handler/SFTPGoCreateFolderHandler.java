package com.innovationandtrust.sftp.component.chain.handler;

import com.innovationandtrust.sftp.constant.SFTPGoProcessConstant;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderRequest;
import com.innovationandtrust.share.constant.SFTOGoSourceType;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.exception.exceptions.BadRequestException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import com.innovationandtrust.utils.sftpgo.model.SFTPGoFolderResponse;
import com.innovationandtrust.utils.sftpgo.provider.SFTPGoServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SFTPGoCreateFolderHandler extends AbstractExecutionHandler {

  private final SFTPGoServiceProvider sftpGoServiceProvider;

  public SFTPGoCreateFolderHandler(SFTPGoServiceProvider sftpGoServiceProvider) {
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
          this.requestCreateFolderSource(
              request.getCorporateUuid(), SFTOGoSourceType.SOURCE_IN.getValue());
      context.put(SFTPGoProcessConstant.SOURCE_IN_INFO, sourceInFolder);

      final var sourceOutFolder =
          this.requestCreateFolderSource(
              request.getCorporateUuid(), SFTOGoSourceType.SOURCE_OUT.getValue());
      context.put(SFTPGoProcessConstant.SOURCE_OUT_INFO, sourceOutFolder);

      return ExecutionState.NEXT;
    } catch (FeignClientRequestException e) {
      log.error("[SFTPGoCreateFolderHandler] error while create folder: {0}", e);
      return ExecutionState.END;
    }
  }

  private SFTPGoFolderResponse requestCreateFolderSource(String uuid, String type) {

    final var suffixName =
        SFTOGoSourceType.contain(type).orElseThrow(() -> new BadRequestException("Type not found"));
    final var name = String.format("%s-%s", uuid, suffixName.getSuffixName());

    return this.sftpGoServiceProvider.createFolder(name, type, uuid);
  }
}
