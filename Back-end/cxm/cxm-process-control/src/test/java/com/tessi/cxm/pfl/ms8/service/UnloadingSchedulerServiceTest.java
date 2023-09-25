package com.tessi.cxm.pfl.ms8.service;

import static com.tessi.cxm.pfl.ms8.constant.ProcessControlConstant.MOCK_CLIENT_UNLOAD_DETAILS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms8.repository.UnloadingSchedulerJobRepository;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UnloadingSchedulerServiceTest {
  @Mock private Scheduler scheduler;
  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private ObjectMapper objectMapper;
  @Mock private UnloadingSchedulerJobRepository unloadingSchedulerJobRepository;

  private UnloadingSchedulerService unloadingScheduler;

  @BeforeEach
  void setUp() {
    this.unloadingScheduler =
        spy(
            new UnloadingSchedulerService(profileFeignClient,
                unloadingSchedulerJobRepository, scheduler, objectMapper));
  }

  @Test
  void whenSetScheduleFlowUnloading_withMockDoNothing_thenSuccess() {
    ArgumentCaptor<SharedClientUnloadDetailsDTO> clientUnloadCapture =
        ArgumentCaptor.forClass(SharedClientUnloadDetailsDTO.class);
    doNothing().when(this.unloadingScheduler).scheduleFlowUnloading(clientUnloadCapture.capture());

    unloadingScheduler.scheduleFlowUnloading(MOCK_CLIENT_UNLOAD_DETAILS);

    var captureValue = clientUnloadCapture.getValue();
    assertEquals(1L, captureValue.getClientId());
    assertEquals("MON", captureValue.getClientUnloads().get(0).getDayOfWeek());
  }

  @Test
  void whenSetScheduleFlowUnloading_withVerify_thenSuccess() throws SchedulerException {

    unloadingScheduler.scheduleFlowUnloading(MOCK_CLIENT_UNLOAD_DETAILS);

    verify(this.scheduler, times(1)).scheduleJob(any(), any(), anyBoolean());
  }
}
