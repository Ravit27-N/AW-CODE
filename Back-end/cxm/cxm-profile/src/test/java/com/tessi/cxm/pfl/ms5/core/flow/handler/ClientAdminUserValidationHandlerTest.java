package com.tessi.cxm.pfl.ms5.core.flow.handler;

import com.tessi.cxm.pfl.ms5.constant.DataReaderConstant;
import com.tessi.cxm.pfl.ms5.constant.ProfileUnitTestConstants;
import com.tessi.cxm.pfl.ms5.dto.UserOrganization;
import com.tessi.cxm.pfl.ms5.exception.BatchUserCreationFailureException;
import com.tessi.cxm.pfl.ms5.util.BatchUserOrganization;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ClientAdminUserValidationHandlerTest {
  private final ClientAdminUserValidationHandler clientAdminUserValidationHandler =
      new ClientAdminUserValidationHandler();

  @Test
  @Order(1)
  void executeInternal_ThenReturnSuccess() {
    ExecutionContext context = new ExecutionContext();
    BatchUserOrganization batchUserOrganization =
        new BatchUserOrganization(List.of(ProfileUnitTestConstants.USER_ORGANIZATION));

    context.put(DataReaderConstant.USER_RECORD, ProfileUnitTestConstants.USER_RECORD);
    context.put(DataReaderConstant.KEY_USER_ORGANIZATION_BY_LEVEL, batchUserOrganization);
    context.put(
        DataReaderConstant.KEY_USER_ORGANIZATION_PROFILES,
        List.of(ProfileUnitTestConstants.PROFILE_TEST));

    List<String> contextElementExpects =
        List.of(DataReaderConstant.DEPARTMENT_ENTITY, DataReaderConstant.PROFILES_ENTITY);

    this.clientAdminUserValidationHandler.executeInternal(context);

    contextElementExpects.forEach(
        contextElementExpected -> {
          Assertions.assertNotNull(
              context.get(contextElementExpected),
              "The " + contextElementExpected + "must be not null");

          log.info(
              "Context key: {} = {}", contextElementExpected, context.get(contextElementExpected));
        });
  }

  @ParameterizedTest
  @MethodSource("contextParametersWithException")
  @Order(2)
  void executeInternal_ThenReturnThrowException(ExecutionContext context, String messageExpected) {
    Throwable throwable =
        Assertions.assertThrows(
            BatchUserCreationFailureException.class,
            () -> {
              this.clientAdminUserValidationHandler.executeInternal(context);
            });

    log.info("Throwable message: {}", throwable.getMessage());
  }

  private static Stream<Arguments> contextParametersWithException() {
    ExecutionContext context1 = new ExecutionContext();
    UserOrganization buildUserOrganization1 =
        UserOrganization.builder()
            .clientId(1L)
            .clientName("")
            .divisionId(1L)
            .divisionName("DIV A")
            .serviceId(1L)
            .serviceName("SER A")
            .build();

    BatchUserOrganization batchUserOrganization1 =
        new BatchUserOrganization(List.of(buildUserOrganization1));

    context1.put(DataReaderConstant.USER_RECORD, ProfileUnitTestConstants.USER_RECORD);
    context1.put(DataReaderConstant.KEY_USER_ORGANIZATION_BY_LEVEL, batchUserOrganization1);

    ExecutionContext context2 = new ExecutionContext();
    UserOrganization buildUserOrganization2 =
        UserOrganization.builder()
            .clientId(1L)
            .clientName("CLIENT_TEST")
            .divisionId(1L)
            .divisionName("")
            .serviceId(1L)
            .serviceName("SER A")
            .build();

    BatchUserOrganization batchUserOrganization2 =
        new BatchUserOrganization(List.of(buildUserOrganization2));

    context2.put(DataReaderConstant.USER_RECORD, ProfileUnitTestConstants.USER_RECORD);
    context2.put(DataReaderConstant.KEY_USER_ORGANIZATION_BY_LEVEL, batchUserOrganization2);

    ExecutionContext context3 = new ExecutionContext();
    UserOrganization buildUserOrganization3 =
        UserOrganization.builder()
            .clientId(1L)
            .clientName("CLIENT_TEST")
            .divisionId(1L)
            .divisionName("DIV A")
            .serviceId(1L)
            .serviceName("")
            .build();

    BatchUserOrganization batchUserOrganization3 =
        new BatchUserOrganization(List.of(buildUserOrganization3));

    context3.put(DataReaderConstant.USER_RECORD, ProfileUnitTestConstants.USER_RECORD);
    context3.put(DataReaderConstant.KEY_USER_ORGANIZATION_BY_LEVEL, batchUserOrganization3);

    ExecutionContext context4 = new ExecutionContext();
    UserOrganization buildUserOrganization4 = ProfileUnitTestConstants.USER_ORGANIZATION;
    context4.put(
        DataReaderConstant.KEY_USER_ORGANIZATION_PROFILES,
        List.of(ProfileUnitTestConstants.PROFILE_DEV_TEST));

    BatchUserOrganization batchUserOrganization4 =
        new BatchUserOrganization(List.of(buildUserOrganization4));

    context4.put(DataReaderConstant.USER_RECORD, ProfileUnitTestConstants.USER_RECORD);
    context4.put(DataReaderConstant.KEY_USER_ORGANIZATION_BY_LEVEL, batchUserOrganization4);

    var argument1 =
        Arguments.arguments(context1, ClientAdminUserValidationHandler.USER_BAD_REQUEST);
    var argument2 =
        Arguments.arguments(context2, ClientAdminUserValidationHandler.USER_BAD_REQUEST);
    var argument3 =
        Arguments.arguments(context3, ClientAdminUserValidationHandler.USER_BAD_REQUEST);
    var argument4 =
        Arguments.arguments(context4, ClientAdminUserValidationHandler.PROFILE_NOT_EXIST);
    return Stream.of(argument1, argument2, argument3, argument4);
  }
}
