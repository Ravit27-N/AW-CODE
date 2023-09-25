package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowHistoryRepository
    extends JpaRepository<FlowHistory, Long>, JpaSpecificationExecutor<FlowHistory> {

  @Modifying
  @Query("delete from FlowHistory as fh where fh.flowTraceability.id = :id and fh.event = :event")
  void deleteFlowHistory(@Param("id") Long flowTraceabilityId, @Param("event") String event);
}
