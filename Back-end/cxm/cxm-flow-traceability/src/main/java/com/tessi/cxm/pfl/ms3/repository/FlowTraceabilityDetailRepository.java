package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails;
import com.tessi.cxm.pfl.ms3.dto.FlowDetailReview;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowTraceabilityDetailRepository
    extends JpaRepository<FlowTraceabilityDetails, Long> {

  @Lock(value = LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
  FlowDetailReview findByCampaignId(Long campaignId);

  @Modifying
  @Query(
      "Update FlowTraceabilityDetails dt "
          + "SET dt.pageError = :errorCount, dt.version = :version  where dt.id = :id and dt.version = :version")
  void updateFlowDocumentRecordProcessed(
      @Param("id") long id, @Param("errorCount") int errorCount, @Param("version") long version);

  Optional<FlowTraceabilityDetails> findById(@Param("id") long id);

  @Lock(value = LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
  Optional<FlowTraceabilityDetails> findFlowTraceabilityDetailsByCampaignId(
      Long flowTraceabilityId);
}
