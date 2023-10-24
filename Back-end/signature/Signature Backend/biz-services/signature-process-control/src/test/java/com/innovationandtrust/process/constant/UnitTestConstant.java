package com.innovationandtrust.process.constant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.process.chain.handler.JsonFileProcessHandler;
import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.utils.ProcessControlUtils;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.encryption.TokenParam;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

import com.innovationandtrust.utils.file.provider.FileProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
@RequiredArgsConstructor
public final class UnitTestConstant {
  public static final String JSON = ".json";
  public static final String FLOW_ID = "3b0d2981-111e-4462-a7bc-f43b64f8303d";
  public static final String COMPANY_UUID = "3b0d2981-111e-4462-a7bc-f43b64f8303d";
  public static final String UUID = "b17c7e52-c605-4f0d-a809-1f3449008343";
  // Token should content the same, companyUuid, flowId, uuid
  public static final String TOKEN =
      "DUCBtmFRpgN0Ya8McWP7K0C6jSqxC9UITjL-vS-pW_rMptQ8qsDg_vwT9ieODA_rgMMMvdoBHohk6HEIs8Yp_cRpLlTDXd9jcM3hBwbFkxJJBGLxmChbbvJNawaXl7jD0S-w8J7dZtCvkAwbqGLPDUYpwhy4aFN18eGfnF2G9-0";
  public static final String JSON_FILE = FLOW_ID + JSON;
  public static final String FILE_PATH =
      "/UnitTest/file_control/3b0d2981-111e-4462-a7bc-f43b64f8303d.json";

  public static Project getProject() {
    var objectMapper =
        new ObjectMapper()
            .enable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    try {
      Resource resource = new ClassPathResource(FILE_PATH);
      Reader reader = new BufferedReader(new FileReader(resource.getFile()));
      return objectMapper.readValue(reader, Project.class);
    } catch (IOException ignored) {
      log.error("Can not read test json file");
      return new Project(FLOW_ID);
    }
  }

  public static String basePath() {
    try {
      Resource resource = new ClassPathResource(FILE_PATH);
      Path path = resource.getFile().toPath();
      return path.toString().split(JsonFileProcessHandler.FILE_CONTROL_PATH)[0];
    } catch (IOException ignored) {
      log.error("Cannot get base path. test");
      return null;
    }
  }

  public static ExecutionContext getContext(){
    ExecutionContext context = ProcessControlUtils.getProject(FLOW_ID, UUID);
    context.put(SignProcessConstant.PROJECT_KEY, getProject());
    return context;
  }

  public static TokenParam getParam() {
    return TokenParam.builder()
        .companyUuid(COMPANY_UUID)
        .flowId(FLOW_ID)
        .uuid(UUID)
        .token(TOKEN)
        .build();
  }

  public static ProcessControlProperty getProperty() {
    return new ProcessControlProperty(
        "http://localhost:3000",
        "invitation",
        "approve",
        "recipient",
        "view",
        "complete",
        new ProcessControlProperty.PhoneNumber());
  }

  public static FileProvider fileProvider(){
    return new FileProvider(UnitTestConstant.basePath());
  }
}
