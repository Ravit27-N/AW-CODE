package com.innovationandtrust.process.constant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.process.config.ProcessControlProperty;
import com.innovationandtrust.process.model.SigningProcessDto;
import com.innovationandtrust.process.model.email.EmailInvitationRequest;
import com.innovationandtrust.process.model.email.EmailParametersModel;
import com.innovationandtrust.share.enums.SignatureFileType;
import com.innovationandtrust.share.model.profile.Company;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.share.model.profile.UserCompany;
import com.innovationandtrust.share.model.project.CorporateInfo;
import com.innovationandtrust.share.model.project.GeneratedOTP;
import com.innovationandtrust.share.model.project.Participant;
import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.share.model.project.ProjectResponse;
import com.innovationandtrust.share.model.project.SignatoryResponse;
import com.innovationandtrust.share.model.user.User;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.commons.CommonUsages;
import com.innovationandtrust.utils.eid.model.IdentityDto;
import com.innovationandtrust.utils.eid.model.VideoBiometricsDto;
import com.innovationandtrust.utils.eid.model.VideoDocumentDto;
import com.innovationandtrust.utils.eid.model.VideoIDAuthorizationDto;
import com.innovationandtrust.utils.eid.model.VideoIDVerificationDto;
import com.innovationandtrust.utils.eid.model.VideoRecordedResponse;
import com.innovationandtrust.utils.eid.model.VideoVerifiedDataDto;
import com.innovationandtrust.utils.eid.model.VideoVerifiedDto;
import com.innovationandtrust.utils.encryption.TokenParam;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class UnitTestProvider {

  public static Project getProject(boolean original) {
    var objectMapper =
        new ObjectMapper()
            .enable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    try {
      Resource resource =
          new ClassPathResource(
              original ? UnitTestConstant.FILE_PATH_ORIGINAL : UnitTestConstant.FILE_PATH);
      Reader reader = new BufferedReader(new FileReader(resource.getFile()));
      return objectMapper.readValue(reader, Project.class);
    } catch (IOException ignored) {
      log.error("Can not read test json file");
      return new Project(UnitTestConstant.FLOW_ID);
    }
  }

  public static Participant participant(ExecutionContext context) {
    // Prevent participant reference to original
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    Participant participant = project.getParticipants().get(0);
    return new ObjectMapper().convertValue(participant, Participant.class);
  }

  public static Project project(ExecutionContext context) {
    // Prevent project reference to original
    var project = context.get(SignProcessConstant.PROJECT_KEY, Project.class);
    return new ObjectMapper().convertValue(project, Project.class);
  }

  public static String basePath() {
    try {
      Resource resource = new ClassPathResource(UnitTestConstant.FILE_PATH);
      Path path = resource.getFile().toPath();
      return path.toString().split(PathConstant.FILE_CONTROL_PATH)[0];
    } catch (IOException ignored) {
      log.error("Cannot get base path. test");
      return null;
    }
  }

  public static ExecutionContext getContext() {
    ExecutionContext context = new ExecutionContext();
    context.put(SignProcessConstant.PARTICIPANT_ID, UnitTestConstant.UUID);
    context.put(SignProcessConstant.FLOW_ID, UnitTestConstant.FLOW_ID);
    context.put(SignProcessConstant.PROJECT_KEY, getProject(false));
    return context;
  }

  public static TokenParam getParam() {
    return TokenParam.builder()
        .companyUuid(UnitTestConstant.COMPANY_UUID)
        .flowId(UnitTestConstant.FLOW_ID)
        .uuid(UnitTestConstant.UUID)
        .token(UnitTestConstant.TOKEN)
        .build();
  }

  public static ProcessControlProperty getProperty() {
    return new ProcessControlProperty(
        "http://localhost:3000",
        "invitation",
        "video",
        "approve",
        "recipient",
        "view",
        "complete",
        new ProcessControlProperty.PhoneNumber());
  }

  public static FileProvider fileProvider() {
    return new FileProvider(basePath());
  }

  public static String getEncryptedLink() {
    String slash = "\\";
    return String.format(
        "%s%s%s%s%s%s?token=%s",
        getProperty().getFrontEndUrl(),
        slash,
        getProperty().getInvitationContextPath(),
        slash,
        UnitTestConstant.COMPANY_UUID,
        slash,
        UnitTestConstant.TOKEN);
  }

  public static MailRequest getMailRequest() {
    return new MailRequest(UnitTestConstant.EMAIL, "Subject", "Mail Body");
  }

  public static EmailInvitationRequest getInvitationRequest() {
    var model =
        new EmailParametersModel(
            "Sopheak",
            "Signature",
            "Message Invitation",
            "Subject Invitaion",
            getEncryptedLink(),
            UnitTestConstant.EMAIL);

    return new EmailInvitationRequest(
        model, "Certigna", "#D6056A");
  }

  public static User getUser() {
    var user = new User();
    user.setUserEntityId(java.util.UUID.fromString(UnitTestConstant.CREATOR_UUID));
    user.setId(UnitTestConstant.CREATOR_ID);
    user.setFirstName("Vothana");
    user.setLastName("CHY");
    user.setCreatedBy(10L);
    user.setPhone(UnitTestConstant.PHONE_NUMBER);
    return user;
  }

  public static UserCompany getUserCompany() {
    var userCompany = new ObjectMapper().convertValue(getUser(), UserCompany.class);
    var company = new Company();
    company.setArchiving(true);
    company.setId(1L);
    userCompany.setCompany(company);
    return userCompany;
  }

  public static void toProcessed(Participant participant) {
    var validPhone = new Participant.ValidPhone(true, 0, "6027", 4);
    participant.setValidPhone(validPhone);
    participant.setOtp(new GeneratedOTP(randomOtp(), getDate(0), getDate(1), 0));
    participant.getOtp().setValidated(true);

    participant.setActionedDate(new Date());
    participant.setSigned(true);
    participant.setSignedDate(new Date());
    participant.setApproved(true);
  }

  public static String getDate(int days) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, days);
    return calendar.getTime().toInstant().toString();
  }

  public static Date getDateTime(int days) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, days);
    return calendar.getTime();
  }

  public static String randomOtp() {
    SecureRandom rand = new SecureRandom();
    return String.valueOf(rand.nextInt(100000));
  }

  public static MultipartFile getDocument() {
    try {
      Path path = Path.of(Objects.requireNonNull(basePath()), UnitTestConstant.DOC_PATH);
      byte[] bytes = Files.readAllBytes(path);
      return CommonUsages.byteArrayToMultipartFile(
          bytes, path.getFileName().toString(), MediaType.APPLICATION_PDF);
    } catch (Exception ex) {
      return null;
    }
  }

  public static MultipartFile getMultipartFile() {
    return new MockMultipartFile("file", "file.png", null, "file.png".getBytes());
  }

  public static VideoIDAuthorizationDto getVideoIDAuthorizationDto() {
    final var videoIDAuthorizationDto = new VideoIDAuthorizationDto();
    videoIDAuthorizationDto.setId("1");
    videoIDAuthorizationDto.setAuthorization(UnitTestConstant.TOKEN);
    return videoIDAuthorizationDto;
  }

  public static VideoIDVerificationDto getVideoIDVerificationDto() {
    final var videoIDVerificationDto = new VideoIDVerificationDto();
    videoIDVerificationDto.setVideoId(UnitTestConstant.VIDEO_ID);
    videoIDVerificationDto.setId("1");

    return videoIDVerificationDto;
  }

  public static SignatoryResponse getSignatoryResponse() {
    final var project = new ProjectResponse();
    project.setId(getProject(false).getId());
    project.setFlowId(getProject(false).getFlowId());

    final var signatoryResponse = new SignatoryResponse();
    signatoryResponse.setId(1L);
    signatoryResponse.setUuid(UnitTestConstant.UUID);
    signatoryResponse.setProject(project);

    return signatoryResponse;
  }

  public static VideoVerifiedDto getVideoVerified() {
    final var videoData = new VideoVerifiedDataDto();
    videoData.setStatus("Accepted");
    videoData.setVideoId(UnitTestConstant.VIDEO_ID);
    videoData.setId(UnitTestConstant.VERIFICATION_ID);

    final var videoVerified = new VideoVerifiedDto();
    videoVerified.setData(videoData);

    return videoVerified;
  }

  public static IdentityDto getIdentityDto() {
    final var identityDto = new IdentityDto();
    identityDto.setId(UnitTestConstant.IDENTITY_ID);

    return identityDto;
  }

  public static VideoRecordedResponse getVideoRecordedResponse() {
    final var videoRecordedResponse = new VideoRecordedResponse();
    final var document = new VideoDocumentDto();
    document.setBack(UnitTestConstant.DOC_BASE64);
    document.setFront(UnitTestConstant.DOC_BASE64);

    final var face = new VideoBiometricsDto.VideoBiometricFace();
    face.setImage(UnitTestConstant.DOC_BASE64);

    final var biometrics = new VideoBiometricsDto();
    biometrics.setFace(face);

    videoRecordedResponse.setBiometrics(biometrics);
    videoRecordedResponse.setDocument(document);

    return videoRecordedResponse;
  }
  
  public static void setMultipleProjects(ExecutionContext context) {
    final var signingProcessDto =
        SigningProcessDto.builder()
            .uuid(UnitTestConstant.UUID)
            .flowId(UnitTestConstant.FLOW_ID)
            .build();

    context.put(SignProcessConstant.MULTI_SIGNING_PROJECTS, List.of(signingProcessDto));
    context.put(
        SignProcessConstant.PROJECTS,
        List.of(context.get(SignProcessConstant.PROJECT_KEY, Project.class)));
  }

  public static CompanySettingDto signatureLevel(String settingLevel) {
    return CompanySettingDto.builder()
        .active(true)
        .companyUuid(UnitTestConstant.COMPANY_UUID)
        .signatureLevel(settingLevel)
        .personalTerms("Personal terms mocked")
        .identityTerms("Identity terms mocked")
        .documentTerms("Document terms mocked")
        .channelReminder("sms_email")
        .companyChannel("sms_email")
        .fileType(SignatureFileType.getFileTypes())
        .companyFileType(SignatureFileType.getFileTypes())
        .build();
  }

  public static CorporateInfo corporateInfo() {
    return CorporateInfo.builder()
        .companyUuid(UnitTestConstant.COMPANY_UUID)
        .companyName("ECOMA ANA")
        .mainColor("#FFFFFF")
        .build();
  }
}
