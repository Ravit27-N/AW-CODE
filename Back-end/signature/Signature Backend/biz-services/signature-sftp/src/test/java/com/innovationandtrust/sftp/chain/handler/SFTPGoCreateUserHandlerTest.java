package com.innovationandtrust.sftp.chain.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.sftp.component.chain.handler.SFTPGoCreateUserHandler;
import com.innovationandtrust.sftp.constant.Constant;
import com.innovationandtrust.sftp.constant.SFTPGoProcessConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.sftpgo.model.CreateUserProviderRequest;
import com.innovationandtrust.utils.sftpgo.provider.SFTPGoServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Slf4j
class SFTPGoCreateUserHandlerTest {
  private SFTPGoCreateUserHandler sftpGoCreateUserHandler;
  private ExecutionContext executionContext;

  @BeforeEach
  public void setUp() {
    SFTPGoServiceProvider sftpGoServiceProviderMock = mock(SFTPGoServiceProvider.class);

    sftpGoCreateUserHandler = spy(new SFTPGoCreateUserHandler(sftpGoServiceProviderMock));
    executionContext = Constant.setUpContext();

    when(sftpGoServiceProviderMock.createUser(any(CreateUserProviderRequest.class)))
        .thenReturn(Constant.getSftpGoUserResponse());
  }

  @Test
  @DisplayName("[SFTPGoCreateUserHandler] Request create user handler fail")
  void testCreateFolderHandlerFail() {

    final var exception =
        assertThrows(
            NullPointerException.class,
            () -> this.sftpGoCreateUserHandler.execute(executionContext),
            Constant.MESSAGE_ASSERT_THROW);
    log.info("[Exception thrown]: {}", exception.getMessage());
  }

  @Test
  @DisplayName("[SFTPGoCreateUserHandler] Request create user handler success")
  void testCreateFolderHandler() {
    executionContext.put(SFTPGoProcessConstant.SOURCE_IN_INFO, Constant.getSftpGoFolderResponse());
    executionContext.put(SFTPGoProcessConstant.SOURCE_OUT_INFO, Constant.getSftpGoFolderResponse());
    this.sftpGoCreateUserHandler.execute(executionContext);
    verify(this.sftpGoCreateUserHandler).execute(executionContext);
  }
}
