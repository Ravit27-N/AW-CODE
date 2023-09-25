package com.tessi.cxm.pfl.ms5.core.flow.handler;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.UserRecord;
import com.tessi.cxm.pfl.ms5.exception.BatchUserCreationFailureException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminUserValidationHandlerTest {

  @Mock
  private ClientRepository clientRepository;

  @Mock
  private ProfileRepository profileRepository;

  private AdminUserValidationHandler adminUserValidationHandler;

  @BeforeEach
  void beforeEach() {
    this.adminUserValidationHandler = new AdminUserValidationHandler(clientRepository,
        profileRepository);
  }

  @ParameterizedTest
  @MethodSource("contextParameters")
  @Order(1)
  void executeInternal_ThenReturnSuccess(ExecutionContext context,
      String contextElementExpected) {
    var userRecord = context.get(DataReaderConstant.USER_RECORD, UserRecord.class);

    if (userRecord != null) {
      if (StringUtils.isNotBlank(userRecord.getClient()) && StringUtils.isNotBlank(
          userRecord.getDivision()) &&
          StringUtils.isNotBlank(userRecord.getService())) {
        when(this.clientRepository.findDepartment(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(ProfileUnitTestConstants.DEPARTMENT));
      }

      if (!CollectionUtils.isEmpty(userRecord.getProfiles()) && StringUtils.isNotBlank(
          userRecord.getClient())) {
        when(this.profileRepository.findProfiles(anyList(), anyLong()))
            .thenReturn(List.of(ProfileUnitTestConstants.PROFILE_TEST,
                ProfileUnitTestConstants.PROFILE_DEV_TEST));
      }
    }

    this.adminUserValidationHandler.executeInternal(context);
    Assertions.assertNotNull(context.get(contextElementExpected),
        "The " + contextElementExpected + "must be not null");

    log.info("Context key: {} = {}", contextElementExpected, context.get(contextElementExpected));
  }

  @ParameterizedTest
  @MethodSource("contextParametersWithException")
  @Order(2)
  void executeInternal_ThenReturnThrowException(ExecutionContext context,
      String messageExpected) {
    var userRecord = context.get(DataReaderConstant.USER_RECORD, UserRecord.class);

    if (userRecord != null) {
      if (StringUtils.isNotBlank(userRecord.getClient()) && StringUtils.isNotBlank(
          userRecord.getDivision()) &&
          StringUtils.isNotBlank(userRecord.getService())) {
        when(this.clientRepository.findDepartment(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(ProfileUnitTestConstants.DEPARTMENT));
      }

      if (!CollectionUtils.isEmpty(userRecord.getProfiles()) && StringUtils.isNotBlank(
          userRecord.getClient())) {
        when(this.profileRepository.findProfiles(anyList(), anyLong()))
            .thenReturn(List.of(ProfileUnitTestConstants.PROFILE_TEST,
                ProfileUnitTestConstants.PROFILE_DEV_TEST));
      }
    }

    Throwable throwable = Assertions.assertThrows(BatchUserCreationFailureException.class, () -> {
      this.adminUserValidationHandler.executeInternal(context);
    });

    Assertions.assertEquals(messageExpected, throwable.getMessage());
    log.info("Throwable message: {}", throwable.getMessage());
  }

  private static Stream<Arguments> contextParametersWithException() {
    ExecutionContext context1 = new ExecutionContext();
    var argument1 = Arguments.arguments(context1,
        AdminUserValidationHandler.USER_RECORD_BAD_REQUEST);

    ExecutionContext context2 = new ExecutionContext();
    UserRecord userRecord = UserRecord.builder()
        .client("")
        .firstName("Dev")
        .lastName("TEST")
        .email("dev.test@gmail.com")
        .division("")
        .service("")
        .profiles(List.of("Profile TEST"))
        .build();

    context2.put(DataReaderConstant.USER_RECORD, userRecord);
    var argument2 = Arguments.arguments(context2,
        AdminUserValidationHandler.USER_BAD_REQUEST);

    ExecutionContext context3 = new ExecutionContext();
    UserRecord userRecord3 = UserRecord.builder()
        .client("Client_TEST")
        .firstName("Dev")
        .lastName("TEST")
        .email("dev.test@gmail.com")
        .division("Div A")
        .service("Ser A")
        .profiles(List.of("Profile TEST, DEV TEST, TESTING"))
        .build();

    context3.put(DataReaderConstant.USER_RECORD, userRecord3);
    var argument3 = Arguments.arguments(context3,
        AdminUserValidationHandler.PROFILE_NOT_EXIST);

    return Stream.of(argument1, argument2, argument3);
  }

  private static Stream<Arguments> contextParameters() {
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

    return Stream.of(
        Arguments.arguments(context, DataReaderConstant.USER_RECORD),
        Arguments.arguments(context, DataReaderConstant.DEPARTMENT_ENTITY),
        Arguments.arguments(context, DataReaderConstant.PROFILES_ENTITY)
    );
  }
}