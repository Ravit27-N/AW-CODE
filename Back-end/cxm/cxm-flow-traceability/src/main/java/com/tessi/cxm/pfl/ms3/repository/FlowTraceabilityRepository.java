package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.dto.CountDocumentOfFlowProjection;
import com.tessi.cxm.pfl.ms3.dto.FlowCampaignProjection;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentOwnerProjection;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDetailsProjection;
import com.tessi.cxm.pfl.ms3.dto.FlowTraceabilityDocument;
import com.tessi.cxm.pfl.ms3.dto.FlowValidationProjection;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.shared.service.SharedRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface FlowTraceabilityRepository
    extends JpaRepository<FlowTraceability, Long>,
        JpaSpecificationExecutor<FlowTraceability>,
        SharedRepository<Long> {

  /**
   * Method used to get {@link FlowTraceability} by it id wrap by {@link FlowTraceabilityDocument}
   * projection.
   *
   * @param id refer to {@link FlowTraceability} identity.
   * @return {@link FlowTraceabilityDocument} instead of {@link FlowTraceability}.
   * @see FlowTraceability
   * @see FlowTraceabilityDocument
   */
  FlowTraceabilityDocument findFlowTraceabilityById(Long id);

  Optional<FlowTraceability> findByFlowTraceabilityDetailsCampaignId(Long campaignId);

  FlowTraceabilityDetailsProjection findFlowTraceabilityDetailsById(Long id);

  Optional<FlowTraceability> findByFileId(@Size(max = 128) @NotEmpty String fileId);

  @Query(
      "select count(fd.id) > 0 from FlowTraceability fd inner join fd.flowHistories fdh "
          + "where fd.id = :id and fdh.event = :status")
  boolean existsByHistoryStatus(@Param("id") long id, @Param("status") String status);

  @Query("select ft.id as id, "
      + "ft.createdAt as createdAt, "
      + "ft.createdBy as createdBy, "
      + "ft.fullName as fullName, "
      + "ft.status as status, "
      + "ft.dateStatus as dateStatus, "
      + "ft.ownerId as ownerId, "
      + "fcd as detail "
      + "from FlowTraceability ft left join FlowCampaignDetail fcd "
      + "on ft.id = fcd.flowTraceability.id "
      + "where ft.id = :id")
  Optional<FlowCampaignProjection> findFlowCampaignDetailById(@Param("id") Long id);

  @Query("select\n"
      + " ft as flowTraceability,\n"
      + " count(fd.id) as totalDocs\n"
      + "from FlowTraceability ft\n"
      + "inner join FlowDocument fd on ft.id = fd.flowTraceability.id\n"
      + "where ft.id in ( select fd2.flowTraceability.id "
      + "   from FlowDocument fd2\n"
      + "   where fd2.id in :docIds) group by ft.id order by ft.id")
  Page<CountDocumentOfFlowProjection> countAllDocsOfFlow(
      @Param("docIds") Collection<Long> docIds, Pageable pageable);

  @Modifying
  @Query(
      "update FlowHistory fd set fd.server =:server "
          + "where lower(fd.event) =:event and fd.flowTraceability.fileId in :fileIds")
  void updateServerIdFlowHistoryValidated(
      @Param("server") String server,
      @Param("event") String event,
      @Param("fileIds") List<String> fileIds);


  @Query("select ft.fileId as fileId,  fd.composedFileId as composedId from FlowTraceability ft "
      + " inner join FlowDeposit  fd on ft.id = fd.flowTraceability.id"
      + " where ft.id = :id")
  Optional<FlowValidationProjection> getDocumentDocIdToValidate(@Param("id") long id);

  @Query(
      "select new com.tessi.cxm.pfl.ms3.dto.FlowDocumentOwnerProjection(ft.id, fd.id, ft.ownerId) "
          + "from FlowTraceability ft inner join FlowDocument fd on ft.id = fd.flowTraceability.id "
          + "where fd.id in (:docIds)")
  List<FlowDocumentOwnerProjection> findFlowDocOwnerByDocIds(@Param("docIds") List<Long> docIds);
}
