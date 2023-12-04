package com.innovationandtrust.sftp.chain.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.innovationandtrust.sftp.component.chain.handler.SFTPGoCreateFolderHandler;
import com.innovationandtrust.sftp.constant.Constant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.sftpgo.provider.SFTPGoServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
class SFTPGoCreateFolderHandlerTest {

  @Mock private SFTPGoCreateFolderHandler sftpGoCreateFolderHandler;
  @Mock private ExecutionContext executionContext;

  @BeforeEach
  public void setUp() {
    sftpGoCreateFolderHandler =
        spy(new SFTPGoCreateFolderHandler(mock(SFTPGoServiceProvider.class)));
    executionContext = Constant.setUpContext();
  }

  @Test
  @DisplayName("[SFTPGoCreateFolderHandler] Request create folder handler")
  void testCreateFolderHandler() {
    this.sftpGoCreateFolderHandler.execute(executionContext);
    verify(this.sftpGoCreateFolderHandler).execute(executionContext);
  }
}
