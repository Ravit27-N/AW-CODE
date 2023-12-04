package com.innovationandtrust.process.chain.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innovationandtrust.process.constant.JsonFileProcessAction;
import com.innovationandtrust.process.constant.SignProcessConstant;
import com.innovationandtrust.process.constant.UnitTestProvider;
import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.ResponseData;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** This class only for duplication. */
@Slf4j
@ExtendWith(SpringExtension.class)
class RequestSigningHandlerTest {
  private RequestSigningHandler requestSigningHandler;
  @Mock private KeycloakProvider keycloakProvider;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private ApiNgFeignClientFacade apiNgFeignClient;
  @Mock private FileProvider fileProvider;
  private Project project;
  private ResponseData responseData;
  private ExecutionContext context;
  private JsonFileProcessHandler jsonFileProcessHandler;

  @BeforeEach
  public void setup() {
    jsonFileProcessHandler =
        new JsonFileProcessHandler(
            UnitTestProvider.fileProvider(), keycloakProvider, profileFeignClient);

    requestSigningHandler =
        spy(new RequestSigningHandler(apiNgFeignClient, fileProvider, profileFeignClient));

    context = UnitTestProvider.getContext();
    project = ProcessControlUtils.getProject(context);
    responseData = new ResponseData("url/231");
  }

  private void after() {
    // update json file to original project for other test case
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.UPDATE);
    context.put(SignProcessConstant.PROJECT_KEY, UnitTestProvider.getProject(true));
    jsonFileProcessHandler.execute(context);
    context.put(SignProcessConstant.JSON_FILE_PROCESS_ACTION, JsonFileProcessAction.READ);
  }

  @Test
  @DisplayName("[Request to sign]")
  void requestToSign() {
    // given
    var user = UnitTestProvider.getUser();
    var recipient = UnitTestProvider.participant(context);
    recipient.setRole(RoleConstant.ROLE_RECEIPT);

    var approval = UnitTestProvider.participant(context);
    approval.setRole(RoleConstant.ROLE_APPROVAL);

    project.getParticipants().add(recipient);
    project.getParticipants().add(approval);
    context.put(SignProcessConstant.PROJECT_KEY, project);

    // when
    when(this.profileFeignClient.findUserById(anyLong())).thenReturn(user);
    when(this.apiNgFeignClient.createSession(any())).thenReturn(responseData);
    when(this.apiNgFeignClient.createActor(anyLong(), any())).thenReturn(responseData);
    when(this.apiNgFeignClient.uploadFile(anyString(), anyString(), any()))
        .thenReturn(responseData);
    when(this.apiNgFeignClient.addDocument(anyLong(), any())).thenReturn(responseData);
    when(this.apiNgFeignClient.createScenario(anyLong(), any())).thenReturn(responseData);

    this.requestSigningHandler.execute(context);
    verify(this.requestSigningHandler).execute(context);
  }
}
