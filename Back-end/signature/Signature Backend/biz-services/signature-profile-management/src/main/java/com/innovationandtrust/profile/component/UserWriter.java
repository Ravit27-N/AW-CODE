package com.innovationandtrust.profile.component;

import com.innovationandtrust.profile.constant.UserJobParamConstant;
import com.innovationandtrust.profile.exception.SkipStepException;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import com.innovationandtrust.profile.service.NormalUserService;
import jakarta.validation.constraints.NotNull;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserWriter implements ItemWriter<NormalUserDto> {
  @Autowired private NormalUserService normalUserService;

  private StepExecution stepExecution;

  private List<NormalUserDto> errorUsers;

  @Override
  public void write(@NotNull Chunk<? extends NormalUserDto> items) {
    List<NormalUserDto> itemsList =
        (List<NormalUserDto>)
            stepExecution.getExecutionContext().get(UserJobParamConstant.ERROR_USERS);

    Long userId = stepExecution.getJobParameters().getLong(UserJobParamConstant.USER_ID);
    for (NormalUserDto item : items) {
      try {
        item.setCreatedBy(userId);
        normalUserService.save(item);
      } catch (Exception e) {
        log.error("Error while saving user", e);
        if (itemsList != null) {
          itemsList.add(item);
          this.errorUsers = itemsList;
        }
        // this exception is not effects yet
        throw new SkipStepException("Fail to save user" + item.getEmail());
      }
    }
  }

  @BeforeStep
  public void saveStepExecution(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
    this.stepExecution
        .getExecutionContext()
        .put(UserJobParamConstant.ERROR_USERS, new ArrayList<>());
  }

  @AfterStep
  public void writeUserToCsv() {
    String errorPath =
        stepExecution.getJobParameters().getString(UserJobParamConstant.ERROR_FILE_PATH);
    if (this.errorUsers != null && errorPath != null) {
      log.error("Error path " + errorPath);
      try (Writer writer = new FileWriter(errorPath);
          CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

        csvPrinter.printRecord(UserJobParamConstant.CSV_HEADER);
        for (NormalUserDto user : this.errorUsers) {
          csvPrinter.printRecord(
              user.getFirstName(), user.getLastName(), user.getEmail(), user.getBusinessId());
        }
      } catch (IOException e) {
        log.error("Error While writing CSV ", e);
        throw new IllegalArgumentException("Error on saving user ");
      }
    }
  }
}
