package com.tessi.cxm.pfl.ms8.repository;

import com.tessi.cxm.pfl.ms8.entity.UnloadingScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UnloadingSchedulerJobRepository extends JpaRepository<UnloadingScheduleJob, Long> {

  List<UnloadingScheduleJob> findAllByClientIdAndCreatedDateLessThanEqual(
      long clientId, Date createdDate);

  Optional<UnloadingScheduleJob> findAllByFlowId(String jobId);

  void deleteAllByClientId(long clientId);
}
