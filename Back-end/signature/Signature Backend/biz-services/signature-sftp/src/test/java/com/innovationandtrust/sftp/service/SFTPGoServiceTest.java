package com.innovationandtrust.sftp.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.innovationandtrust.sftp.component.chain.execution.CreateUserAndFolderExecutionManager;
import com.innovationandtrust.sftp.component.chain.handler.SFTPGoCreateFolderHandler;
import com.innovationandtrust.sftp.component.chain.handler.SFTPGoCreateUserHandler;
import com.innovationandtrust.sftp.constant.Constant;
import com.innovationandtrust.share.model.sftp.SFTPUserFolderRequest;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.exception.exceptions.FeignClientException;
import com.innovationandtrust.utils.exception.exceptions.FeignClientRequestException;
import com.innovationandtrust.utils.sftpgo.model.CreateUserProviderRequest;
import com.innovationandtrust.utils.sftpgo.provider.SFTPGoServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class SFTPGoServiceTest {
  private SFTPGoService sftpGoService;

  @Mock private CreateUserAndFolderExecutionManager createUserAndFolderExecutionManager;

  @Mock private SFTPUserFolderRequest sftpUserFolderRequest;

  @Mock private ExecutionContext context;

  @Mock private SFTPGoServiceProvider sftpGoServiceProvider;

  @Mock private SFTPGoCreateUserHandler sftpGoCreateUserHandler;
  @Mock private SFTPGoCreateFolderHandler sftpGoCreateFolderHandler;

  @BeforeEach
  public void setUp() {

    this.sftpGoServiceProvider = mock(SFTPGoServiceProvider.class);
    this.sftpGoCreateUserHandler = spy(new SFTPGoCreateUserHandler(sftpGoServiceProvider));
    this.sftpGoCreateFolderHandler = spy(new SFTPGoCreateFolderHandler(sftpGoServiceProvider));
    this.createUserAndFolderExecutionManager =
        spy(
            new CreateUserAndFolderExecutionManager(
                sftpGoCreateFolderHandler, sftpGoCreateUserHandler));
    this.createUserAndFolderExecutionManager.afterPropertiesSet();
    this.sftpGoService = spy(new SFTPGoService(createUserAndFolderExecutionManager));

    sftpUserFolderRequest =
        SFTPUserFolderRequest.builder()
            .username("username")
            .email("test@gmail.com")
            .password("password@@222")
            .corporateUuid("1313c0e7-7703-45e8-b409-b3fd58e18beb")
            .build();

    context = Constant.setUpContext();
  }

  @Test
  @DisplayName("Test create user and folder in sftpGo success")
  void testCreateUserAndFolderInSFTPGoSuccess() {
    when(this.sftpGoServiceProvider.createFolder(anyString(), anyString(), anyString()))
        .thenReturn(Constant.getSftpGoFolderResponse());
    when(this.sftpGoServiceProvider.createUser(any(CreateUserProviderRequest.class)))
        .thenReturn(Constant.getSftpGoUserResponse());
    this.createUserAndFolderExecutionManager.execute(context);
    var result = sftpGoService.createUserAndFolderInSFTPGo(sftpUserFolderRequest);

    // then - verify the output
    Assertions.assertNotNull(result, Constant.MESSAGE_NOT_NULL);
  }

  @Test
  @DisplayName("Test create user and folder in sftpGo fail")
  void testCreateUserAndFolderInSFTPGoFail() {
    when(this.sftpGoServiceProvider.createFolder(anyString(), anyString(), anyString()))
        .thenThrow(new FeignClientRequestException());

    this.createUserAndFolderExecutionManager.execute(context);

    // then - verify the result
    final var result =
        assertThrows(
            FeignClientException.class,
            () -> this.sftpGoService.createUserAndFolderInSFTPGo(sftpUserFolderRequest),
            Constant.MESSAGE_ASSERT_THROW);
    log.info("{}", result.getMessage());
  }
}
