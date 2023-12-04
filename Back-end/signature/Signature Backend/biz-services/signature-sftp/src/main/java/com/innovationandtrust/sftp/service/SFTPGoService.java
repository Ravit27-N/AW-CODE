package com.innovationandtrust.sftp.service;

import com.innovationandtrust.sftp.component.chain.execution.CreateUserAndFolderExecutionManager;
import com.innovationandtrust.sftp.constant.SFTPGoProcessConstant;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderRequest;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderResponse;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.exception.exceptions.FeignClientException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SFTPGoService {
  private final CreateUserAndFolderExecutionManager createUserAndFolderExecutionManager;

  public SFTPGoService(CreateUserAndFolderExecutionManager createUserAndFolderExecutionManager) {
    this.createUserAndFolderExecutionManager = createUserAndFolderExecutionManager;
  }

  public SFTPUserFolderResponse createUserAndFolderInSFTPGo(SFTPUserFolderRequest request) {
    final var context = new ExecutionContext();
    context.put(SFTPGoProcessConstant.REQUEST, request);
    this.createUserAndFolderExecutionManager.execute(context);
    final var response = context.get(SFTPGoProcessConstant.RESPONSE, SFTPUserFolderResponse.class);

    if (Objects.isNull(response)) {
      throw new FeignClientException("Error while creating sftpgo user or folder");
    } else {
      return response;
    }
  }
}
