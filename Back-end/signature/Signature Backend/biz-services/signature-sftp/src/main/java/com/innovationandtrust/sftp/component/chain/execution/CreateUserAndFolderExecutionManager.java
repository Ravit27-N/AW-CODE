package com.innovationandtrust.sftp.component.chain.execution;

import com.innovationandtrust.sftp.component.chain.handler.SFTPGoCreateFolderHandler;
import com.innovationandtrust.sftp.component.chain.handler.SFTPGoCreateUserHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CreateUserAndFolderExecutionManager extends ExecutionManager {

  private final SFTPGoCreateFolderHandler sftpGoCreateFolderHandler;
  private final SFTPGoCreateUserHandler sftpGoCreateUserHandler;

  public CreateUserAndFolderExecutionManager(
      SFTPGoCreateFolderHandler sftpGoCreateFolderHandler,
      SFTPGoCreateUserHandler sftpGoCreateUserHandler) {
    this.sftpGoCreateFolderHandler = sftpGoCreateFolderHandler;
    this.sftpGoCreateUserHandler = sftpGoCreateUserHandler;
  }

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(sftpGoCreateFolderHandler, sftpGoCreateUserHandler));
  }
}
