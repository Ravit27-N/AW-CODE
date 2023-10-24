package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.chain.handler.sign.SigningProcessHandler;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.utils.chain.ExecutionManager;
import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
  private final Test test;
  private final ImpersonateTokenService impersonateToken;
  private final String flowId = "3b0d2981-111e-4462-a7bc-f43b64f8303d";
  private final String uuid = "b17c7e52-c605-4f0d-a809-1f3449008343";

  @GetMapping("/test")
  public ResponseEntity<Void> test() {
    getLink();
    var context = ProcessControlUtils.getProject(flowId, uuid);
    test.execute(context);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequiredArgsConstructor
  static @Component class Test extends ExecutionManager {
    private final SigningProcessHandler signingProcessHandler;
    private final JsonFileProcessHandler jsonFileProcessHandler;

    @Override
    public void afterPropertiesSet() {
      super.addHandlers(List.of(jsonFileProcessHandler, signingProcessHandler));
    }
  }

  private void getLink() {
    // Get completed encrypted link
    var link =
        this.impersonateToken.getTokenUrlParam(
            flowId, uuid, "LOCAL", "PATH", "244eb546-2343-41d4-8c47-9c5d1ec947e0");
    var token = link.split("=")[1];
    System.out.println(token);
  }
}
