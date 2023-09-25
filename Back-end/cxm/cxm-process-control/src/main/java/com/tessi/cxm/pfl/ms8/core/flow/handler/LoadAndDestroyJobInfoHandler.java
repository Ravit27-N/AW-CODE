package com.tessi.cxm.pfl.ms8.core.flow.handler;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.entity.UnloadingScheduleJob;
import com.tessi.cxm.pfl.ms8.repository.UnloadingSchedulerJobRepository;
import com.tessi.cxm.pfl.shared.core.chains.AbstractExecutionHandler;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadAndDestroyJobInfoHandler extends AbstractExecutionHandler {

  private final UnloadingSchedulerJobRepository unloadingSchedulerJobRepository;

  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed state before execute or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which hold all the state from previous execution and
   *     for storing all the current state changed.
   */
  @Override
  protected ExecutionState execute(ExecutionContext context) {
    final long clientId = context.get(FlowTreatmentConstants.CLIENT_ID, Long.class);
    final Date currentDateTime = context.get(FlowTreatmentConstants.FORCE_UNLOADING_DATE, Date.class);
    final List<UnloadingScheduleJob> unloadingScheduleJobs =
        loadSchedulerJobInfos(clientId, currentDateTime);
    context.put(FlowTreatmentConstants.SCHEDULER_JOB_INFO, unloadingScheduleJobs);
    destroyScheduleJobInfos(unloadingScheduleJobs);
    return ExecutionState.NEXT;
  }

  @Transactional(readOnly = true)
  public List<UnloadingScheduleJob> loadSchedulerJobInfos(long clientId, Date currentDate) {
    return this.unloadingSchedulerJobRepository.findAllByClientIdAndCreatedDateLessThanEqual(
        clientId, currentDate);
  }

  @Transactional(rollbackFor = Exception.class)
  public void destroyScheduleJobInfos(List<UnloadingScheduleJob> unloadingScheduleJobs) {
    this.unloadingSchedulerJobRepository.deleteAll(unloadingScheduleJobs);
  }
}
