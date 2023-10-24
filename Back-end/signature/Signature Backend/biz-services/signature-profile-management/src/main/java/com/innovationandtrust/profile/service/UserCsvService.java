package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.constant.UserJobParamConstant;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class UserCsvService extends CommonCrudService<NormalUserDto, User, Long> {
  private final FileProvider fileProvider;
  private final JobLauncher jobLauncher;
  private final Job job;

  protected UserCsvService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      FileProvider fileProvider,
      JobLauncher jobLauncher,
      Job job) {
    super(modelMapper, keycloakProvider);
    this.fileProvider = fileProvider;
    this.jobLauncher = jobLauncher;
    this.job = job;
  }

  public String saveFromCSV(MultipartFile file) {
    var uploadedFile = this.fileProvider.upload(file, "csv", false);
    var errorFile =
        Paths.get(fileProvider.getBasePath(), "csv", "error_" + uploadedFile.getFileName()).toString();
    try {
      JobParameters jobParameters =
          new JobParametersBuilder()
              .addLong(UserJobParamConstant.TIME, System.currentTimeMillis())
              .addLong(UserJobParamConstant.USER_ID, this.getUserId())
              .addString(UserJobParamConstant.SOURCE_FILE_PATH, uploadedFile.getFullPath())
              .addString(UserJobParamConstant.ERROR_FILE_PATH, errorFile)
              .toJobParameters();
      jobLauncher.run(job, jobParameters);
      return null;
    } catch (Exception e) {
      log.error("Job run failed", e);
      return errorFile;
    }
  }
}
