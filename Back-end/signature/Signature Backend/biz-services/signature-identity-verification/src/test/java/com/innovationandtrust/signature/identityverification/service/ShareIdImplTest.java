package com.innovationandtrust.signature.identityverification.service;

import static org.mockito.BDDMockito.given;

import com.innovationandtrust.signature.identityverification.model.dto.shareid.OnBoardingDemandDto;
import com.innovationandtrust.signature.identityverification.model.dto.shareid.ShareIdResponse;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ShareIdImplTest {
  @Mock private ShareIdService shareIdService;
  private OnBoardingDemandDto demandDto;

  @Order(3)
  void setup() {
    demandDto =
        new OnBoardingDemandDto(
            1,
            "123",
            "success",
            "received",
            "124214124fafe",
            null,
            null,
            "12214sejgoesg",
            "21491hfifhiwf");
    Mockito.when(shareIdService.callback(demandDto)).thenReturn("SUCCESS");
  }

  @Test
  @Order(1)
  void givenOnBoardingTrue_whenInvokeOnboardingResponse_thenReturnShareIdResponse() {
    Mockito.when(shareIdService.invokeOnboardingResponse(true))
        .thenReturn(Mono.just(new ShareIdResponse("test", "test", null, "test")));
    var response = shareIdService.invokeOnboardingResponse(true);
    response.subscribe(
        shareIdResponse -> {
          assert Objects.nonNull(shareIdResponse);
          assert shareIdResponse.getStatus().equals("test");
          assert shareIdResponse.getMessage().equals("test");
          assert shareIdResponse.getTrace().equals("test");
        });
  }

  @Test
  @Order(2)
  void givenOnBoardingTrue_whenInvokeOnboardingResponse_thenExternalApiException() {
    Mockito.when(shareIdService.invokeOnboardingResponse(true)).thenThrow(RuntimeException.class);
    Assertions.assertThrows(
        Exception.class, () -> shareIdService.invokeOnboardingResponse(true).block());
  }

  @Test
  @Order(4)
  void givenOnBoardingDemandDto_whenCallBack_thenResponseSuccessMessage() {
    given(shareIdService.callback(demandDto)).willReturn("SUCCESS");
    Assertions.assertEquals("SUCCESS", shareIdService.callback(demandDto));
  }

  @Test
  @Order(5)
  void givenOnBoardingDemandDto_whenCallBack_thenThrowUnExpectedError() {
    given(shareIdService.callback(demandDto)).willThrow(RuntimeException.class);
    Assertions.assertThrows(Exception.class, () -> shareIdService.callback(demandDto));
  }
}
