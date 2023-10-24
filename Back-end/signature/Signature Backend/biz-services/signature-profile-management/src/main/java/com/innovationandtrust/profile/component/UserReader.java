package com.innovationandtrust.profile.component;

import com.innovationandtrust.profile.constant.UserJobParamConstant;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserReader implements ItemReader<FlatFileItemReader<NormalUserDto>> {
  private String sourcePath;

  @Override
  public FlatFileItemReader<NormalUserDto> read() {
    FlatFileItemReader<NormalUserDto> itemReader = new FlatFileItemReader<>();
    log.info("source file path: {}", this.sourcePath);
    itemReader.setResource(new FileSystemResource(sourcePath));
    itemReader.setName("csvReader");
    itemReader.setLinesToSkip(1);
    itemReader.setLineMapper(lineMapper());
    return itemReader;
  }

  private LineMapper<NormalUserDto> lineMapper() {
    DefaultLineMapper<NormalUserDto> lineMapper = new DefaultLineMapper<>();

    DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
    lineTokenizer.setDelimiter(",");
    lineTokenizer.setStrict(false);
    lineTokenizer.setNames(UserJobParamConstant.CSV_HEADER.toArray(String[]::new));

    BeanWrapperFieldSetMapper<NormalUserDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(NormalUserDto.class);

    lineMapper.setLineTokenizer(lineTokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);
    return lineMapper;
  }

  public void setSourcePath(String sourcePath) {
    this.sourcePath = sourcePath;
  }
}
