package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.constant.UserRecordMessageConstant;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import java.util.List;
import java.util.stream.Stream;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ValidationRecordHandlerTest {

  @Autowired
  private Validator validator;

  private ValidationRecordHandler validationRecordHandler;

  @BeforeEach
  void beforeEach() {
    this.validationRecordHandler = new ValidationRecordHandler(validator);
  }

  @ParameterizedTest
  @MethodSource("validateRecordParameters")
  @Order(1)
  void validateEachLineOfRecord_ThenReturnSuccess(ExecutionContext context) {
    Assertions.assertDoesNotThrow(() -> {
      this.validationRecordHandler.execute(context);
    });
  }

  @ParameterizedTest
  @MethodSource("validateRecordParametersWithException")
  @Order(2)
  void validateEachLineOfRecord_ThenReturnThrowException(ExecutionContext context,
      String messageExpected) {

    Throwable throwable = Assertions.assertThrows(ConstraintViolationException.class, () -> {
      this.validationRecordHandler.execute(context);
    });

    Assertions.assertEquals(messageExpected, throwable.getMessage());
    log.info("Throwable message: {}", throwable.getMessage());
  }

  private static Stream<Arguments> validateRecordParameters() {
    ExecutionContext context = new ExecutionContext();
    UserRecord userRecord = UserRecord.builder()
        .client("CLIENT_TEST")
        .firstName("Dev")
        .lastName("TEST")
        .email("dev.test@gmail.com")
        .division("DIV A")
        .service("SER A")
        .profiles(List.of("Profile TEST", "DEV TEST"))
        .build();
    context.put(DataReaderConstant.USER_RECORD, userRecord);
    return Stream.of(Arguments.arguments(context));
  }

  private static Stream<Arguments> validateRecordParametersWithException() {
    ExecutionContext context1 = new ExecutionContext();
    UserRecord userRecord1 = UserRecord.builder()
        .client(null)
        .firstName("Dev")
        .lastName("TEST")
        .email("dev.test@gmail.com")
        .division("DIV A")
        .service("SER A")
        .profiles(List.of("Profile TEST", "DEV TEST"))
        .build();
    context1.put(DataReaderConstant.USER_RECORD, userRecord1);
    var argument1 = Arguments.arguments(context1, "client: " + UserRecordMessageConstant.CLIENT);

    ExecutionContext context2 = new ExecutionContext();
    UserRecord userRecord2 = UserRecord.builder()
        .client("CLIENT_TEST")
        .firstName(null)
        .lastName("TEST")
        .email("dev.test@gmail.com")
        .division("DIV A")
        .service("SER A")
        .profiles(List.of("Profile TEST", "DEV TEST"))
        .build();
    context2.put(DataReaderConstant.USER_RECORD, userRecord2);
    var argument2 = Arguments.arguments(context2, "firstName: " + UserRecordMessageConstant.FIRST_NAME);

    ExecutionContext context3 = new ExecutionContext();
    UserRecord userRecord3 = UserRecord.builder()
        .client("CLIENT_TEST")
        .firstName("Dev")
        .lastName(null)
        .email("dev.test@gmail.com")
        .division("DIV A")
        .service("SER A")
        .profiles(List.of("Profile TEST", "DEV TEST"))
        .build();
    context3.put(DataReaderConstant.USER_RECORD, userRecord3);
    var argument3 = Arguments.arguments(context3,
        "lastName: " + UserRecordMessageConstant.LAST_NAME);

    ExecutionContext context4 = new ExecutionContext();
    UserRecord userRecord4 = UserRecord.builder()
        .client("CLIENT_TEST")
        .firstName("Dev")
        .lastName("TEST")
        .email("mailto:1234567890@example.com@")
        .division("DIV A")
        .service("SER A")
        .profiles(List.of("Profile TEST", "DEV TEST"))
        .build();
    context4.put(DataReaderConstant.USER_RECORD, userRecord4);
    var argument4 = Arguments.arguments(context4, "email: " + UserRecordMessageConstant.EMAIL);

    ExecutionContext context5 = new ExecutionContext();
    UserRecord userRecord5 = UserRecord.builder()
        .client("CLIENT_TEST")
        .firstName("Dev")
        .lastName("TEST")
        .email("dev.test@gmail.com")
        .division(null)
        .service("SER A")
        .profiles(List.of("Profile TEST", "DEV TEST"))
        .build();
    context5.put(DataReaderConstant.USER_RECORD, userRecord5);
    var argument5 = Arguments.arguments(context5,
        "division: " + UserRecordMessageConstant.DIVISION);

    ExecutionContext context6 = new ExecutionContext();
    UserRecord userRecord6 = UserRecord.builder()
        .client("CLIENT_TEST")
        .firstName("Dev")
        .lastName("TEST")
        .email("dev.test@gmail.com")
        .division("DIV A")
        .service(null)
        .profiles(List.of("Profile TEST", "DEV TEST"))
        .build();
    context6.put(DataReaderConstant.USER_RECORD, userRecord6);
    var argument6 = Arguments.arguments(context6,
        "service: " + UserRecordMessageConstant.SERVICE);

    ExecutionContext context7 = new ExecutionContext();
    UserRecord userRecord7 = UserRecord.builder()
        .client("CLIENT_TEST")
        .firstName("Dev")
        .lastName("TEST")
        .email("dev.test@gmail.com")
        .division("DIV A")
        .service("SER A")
        .profiles(null)
        .build();
    context7.put(DataReaderConstant.USER_RECORD, userRecord7);
    var argument7 = Arguments.arguments(context7,
        "profiles: " + UserRecordMessageConstant.PROFILES);

    return Stream.of(argument1, argument2, argument3, argument4, argument5, argument6, argument7);
  }
}