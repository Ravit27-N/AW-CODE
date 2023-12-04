package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserResponse;
import com.innovationandtrust.utils.keycloak.model.UserInfo;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class JsonFileProcessHandlerTest {
  private JsonFileProcessHandler jsonFileProcessHandler;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private CorporateProfileFeignClient corporateProfileFeignClient;
  private Project project;
  private ExecutionContext context;
  private ModelMapper modelMapper;

  @BeforeAll
  public static void init() {
    mockStatic(CompanySettingUtils.class);
  }

  @BeforeEach
  public void setup() {
    modelMapper = new ModelMapper();
    jsonFileProcessHandler =
        spy(
            new JsonFileProcessHandler(
                UnitTestProvider.fileProvider(),
                keycloakProvider,
                profileFeignClient));
    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
  }

  private void before() {
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    this.jsonFileProcessHandler.execute(context);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  private void after() {
    // update json file to original project for other test case
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
    jsonFileProcessHandler.execute(context);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  @Test
  @Order(1)
  @DisplayName("[JSON Handler] READ")
  void jsonProcessRead() {
    this.jsonFileProcessHandler.execute(context);
    verify(this.jsonFileProcessHandler).execute(context);
  }

  @Test
  @Order(3)
  @DisplayName("[JSON Handler] READ (With keycloak id) checked")
  void jsonProcessReadNoKeycloakId() {
    // before
    project.getCorporateInfo().setCompanyUuid(null);
    this.before();

    // given
    var corporateInfo = new CorporateInfo();
    corporateInfo.setCompanyUuid(UnitTestConstant.COMPANY_UUID);
    var user = UnitTestProvider.getUserCompany();
    var keyCloakResponse = new KeycloakUserResponse();
    var userInfo = this.modelMapper.map(user, UserInfo.class);
    userInfo.setCorporateId(UnitTestConstant.UUID);
    keyCloakResponse.setSystemUser(userInfo);
    keyCloakResponse.setEnabled(true);

    // when
    when(this.profileFeignClient.findUserById(anyLong())).thenReturn(user);
    when(this.keycloakProvider.getUserInfo(anyString())).thenReturn(Optional.of(keyCloakResponse));
    when(this.corporateProfileFeignClient.findCorporateInfo(anyLong())).thenReturn(corporateInfo);

    this.jsonFileProcessHandler.execute(context);
    verify(this.jsonFileProcessHandler, times(2)).execute(context);

    keyCloakResponse.setEnabled(false);
    when(this.keycloakProvider.getUserInfo(anyString())).thenReturn(Optional.of(keyCloakResponse));
    when(this.profileFeignClient.getActiveUserByRole(anyLong(), anyString()))
        .thenReturn(Optional.of(user));
    this.jsonFileProcessHandler.execute(context);
    verify(this.jsonFileProcessHandler, times(3)).execute(context);

    userInfo.setCorporateId(null);
    keyCloakResponse.setSystemUser(userInfo);
    when(this.keycloakProvider.getUserInfo(anyString())).thenReturn(Optional.of(keyCloakResponse));
    this.jsonFileProcessHandler.execute(context);
    verify(this.jsonFileProcessHandler, times(4)).execute(context);

    when(this.profileFeignClient.getActiveUserByRole(anyLong(), anyString()))
        .thenReturn(Optional.empty());
    this.jsonFileProcessHandler.execute(context);
    verify(this.jsonFileProcessHandler, times(5)).execute(context);

    // after
    this.after();
  }

  @Test
  @Order(3)
  @DisplayName("[JSON Handler] READ (No Keycloak id)")
  void jsonProcessReadNoKeycloakIdTest() {
    // before
    project.setUserKeycloakId(null);
    this.before();

    // given
    var user = UnitTestProvider.getUser();

    // when
    when(this.profileFeignClient.findUserById(anyLong())).thenReturn(user);

    this.jsonFileProcessHandler.execute(context);
    verify(this.jsonFileProcessHandler, times(2)).execute(context);

    // after
    this.after();
  }

  @Test
  @Order(4)
  @DisplayName("[JSON Handler] READ (No Keycloak id) Throw exception")
  void jsonProcessReadNoKeycloakIdError() {
    // before
    project.setUserKeycloakId(null);
    context.put(SignProcessConstant.PROJECT_KEY, project);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    this.before();

    this.jsonFileProcessHandler.execute(context);
    verify(this.jsonFileProcessHandler, times(2)).execute(context);

    // after
    this.after();
  }

  @Test
  @DisplayName("[JSON Handler] CREATE")
  void jsonProcessCreate() {
    // given
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.CREATE);
    try (MockedStatic<FileProvider> fileProviderMockedStatic = mockStatic(FileProvider.class)) {

      this.jsonFileProcessHandler.execute(context);
      verify(this.jsonFileProcessHandler).execute(context);
    }
  }
}
