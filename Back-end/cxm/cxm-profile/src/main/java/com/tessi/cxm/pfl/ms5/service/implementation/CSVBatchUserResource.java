package com.tessi.cxm.pfl.ms5.service.implementation;

import com.tessi.cxm.pfl.ms5.constant.DataReaderHeader;
import com.tessi.cxm.pfl.ms5.constant.DataReaderHeaderConstant;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.ms5.exception.CSVNotAcceptableException;
import com.tessi.cxm.pfl.ms5.exception.DataReaderHeaderNotAcceptable;
import com.tessi.cxm.pfl.ms5.service.BatchUserResource;
import com.tessi.cxm.pfl.shared.utils.FileEncodingUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class CSVBatchUserResource implements BatchUserResource {
  private static final String CSV_EXTENSION = "csv";
  private static final List<String> CSV_CONTENT_TYPES =
      List.of("text/csv", "application/csv", "text/plain");
  private static final String DELIMITER = ";";
  private final Scanner scanner;

  public CSVBatchUserResource(MultipartFile multipartFile) throws IOException {
    final byte[] bytes = multipartFile.getBytes();
    InputStream sourceStream = new ByteArrayInputStream(FileEncodingUtils.convertToUTF8(bytes));

    final var inputStreamReader =
        new InputStreamReader(new BOMInputStream(sourceStream), StandardCharsets.UTF_8);
    this.scanner = new Scanner(inputStreamReader);
    this.validateSchema(multipartFile.getOriginalFilename(), multipartFile.getContentType());
  }

  private void validateSchema(String originalFilename, String contentType) {
    // Validate CSV extension.
    if (!Objects.requireNonNull(FileNameUtils.getExtension(originalFilename))
        .equalsIgnoreCase(CSV_EXTENSION)) {
      throw new CSVNotAcceptableException("The file is not csv format");
    }

    if (CSV_CONTENT_TYPES.stream().noneMatch(mimeType -> mimeType.equalsIgnoreCase(contentType))) {
      throw new CSVNotAcceptableException(
          "Content type is of \"" + contentType + "\" is not accepted");
    }

    // Open first row in csv file.
    if (scanner.hasNext()) {
      String row = scanner.nextLine();

      // Validate CSV delimiter.
      if (!row.contains(DELIMITER)) {
        throw new CSVNotAcceptableException("The file is not separate by \";\" format");
      }

      // Validate CSV headers.
      var csvHeaders =
          Arrays.stream(row.split(DELIMITER))
              .map(s -> s.toLowerCase().trim())
              .collect(Collectors.joining(DELIMITER));

      if (!StringUtils.equalsIgnoreCase(DataReaderHeader.HEADER_DEFINED, csvHeaders)) {
        throw new DataReaderHeaderNotAcceptable("The csv header not valid");
      }
    } else {
      throw new DataReaderHeaderNotAcceptable("The csv header is not presented");
    }
  }

  @Override
  public UserRecord read() {
    final var csvLine = nextLine();
    if (csvLine != null) {
      var csvRecords = csvLine.split(DELIMITER);
      UserRecord userRecord = new UserRecord();
      if (ArrayUtils.isNotEmpty(csvRecords)) {
        this.mappingModel(csvRecords, userRecord);
      }
      return userRecord;
    }
    return null;
  }

  private String nextLine() {
    while (this.scanner.hasNext()) {
      final var csvLine = this.scanner.nextLine();
      if (!StringUtils.isBlank(csvLine.trim())) {
        return csvLine;
      }
    }
    return null;
  }

  @Override
  public void close() {
    this.scanner.close();
  }

  private void mappingModel(String[] records, UserRecord userRecord) {
    for (DataReaderHeader header : DataReaderHeader.values()) {
      if (header.getOrder() < records.length) {
        final String data = records[header.getOrder()].trim();
        switch (header.getKey()) {
          case DataReaderHeaderConstant.CLIENT:
            userRecord.setClient(data);
            break;
          case DataReaderHeaderConstant.FIRST_NAME:
            userRecord.setFirstName(data);
            break;
          case DataReaderHeaderConstant.LAST_NAME:
            userRecord.setLastName(data);
            break;
          case DataReaderHeaderConstant.EMAIL:
            userRecord.setEmail(data);
            break;
          case DataReaderHeaderConstant.DIVISION:
            userRecord.setDivision(data);
            break;
          case DataReaderHeaderConstant.SERVICE:
            userRecord.setService(data);
            break;
          case DataReaderHeaderConstant.PROFILE:
            List<String> profiles = Arrays.asList(data.split(","));
            userRecord.setProfiles(
                profiles.stream().map(String::trim).distinct().collect(Collectors.toList()));
            break;
          default:
            break;
        }
      }
    }
  }
}
