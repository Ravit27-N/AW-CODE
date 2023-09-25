package com.tessi.cxm.pfl.ms8.service;

import com.tessi.cxm.pfl.ms8.entity.UnloadingScheduleJob;
import com.tessi.cxm.pfl.ms8.repository.UnloadingSchedulerJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProcessControlSchedulerService {
  private final UnloadingSchedulerJobRepository unloadingSchedulerJobRepository;

  public ProcessControlSchedulerService(UnloadingSchedulerJobRepository unloadingSchedulerJobRepository) {
    this.unloadingSchedulerJobRepository = unloadingSchedulerJobRepository;
  }

  @Transactional(rollbackFor = Exception.class)
  public void createSchedulerJobInfo(UnloadingScheduleJob unloadingScheduleJob) {
    createSchedulerJobInfo(Collections.singletonList(unloadingScheduleJob));
  }

  @Transactional(rollbackFor = Exception.class)
  public void createSchedulerJobInfo(List<UnloadingScheduleJob> unloadingScheduleJobs) {
    unloadingSchedulerJobRepository.saveAll(unloadingScheduleJobs);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteSchedulerJobInfo(String jobId) {
    unloadingSchedulerJobRepository.findAllByFlowId(jobId).ifPresent(unloadingSchedulerJobRepository::delete);
  }

  @Transactional(readOnly = true)
  public Optional<UnloadingScheduleJob> getUnloadingScheduleJob(String jobId) {
    return unloadingSchedulerJobRepository.findAllByFlowId(jobId);
  }
}
