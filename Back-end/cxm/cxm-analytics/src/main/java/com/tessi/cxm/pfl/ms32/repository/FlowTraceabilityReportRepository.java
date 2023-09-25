package com.tessi.cxm.pfl.ms32.repository;

import com.tessi.cxm.pfl.ms32.dto.FlowDepositMode;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.entity.projection.FlowDocumentReportProjection;
import com.tessi.cxm.pfl.ms32.entity.projection.FlowDocumentTimeSeriesProjection;
import com.tessi.cxm.pfl.shared.repository.SpecificationExecutorWithProjection;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

@Repository
public interface FlowTraceabilityReportRepository
    extends JpaRepository<FlowTraceabilityReport, Long>,
        JpaSpecificationExecutor<FlowTraceabilityReport>,
        JpaSpecificationExecutorWithProjection<FlowTraceabilityReport>,
        CustomFlowTraceabilityReportRepository,
        SpecificationExecutorWithProjection<FlowTraceabilityReport> {

  /**
   * Count all flow document reports by deposit mode & collection of ownerId.
   *
   * @param ownerIds - collections of {@link Long}.
   * @return total of flow document reports {@link Long}.
   */
  @Query(
      "SELECT ftr.depositMode as key, COUNT (ftr.id) as value FROM FlowTraceabilityReport ftr "
          + " WHERE ftr.ownerId IN (:ownerIds) "
          + " AND DATE(ftr.createdAt) BETWEEN :startDate AND :endDate "
          + " AND ftr.createdAt <= :requestedAt "
          + " group by ftr.depositMode")
  List<FlowDepositMode> countTotalFlowPerDepositModes(
      @Param("ownerIds") Collection<Long> ownerIds,
      @Param("startDate") Date startDate,
      @Param("endDate") Date endDate,
      @Param("requestedAt") Date requestedAt);

  @Query(
      "select fdr.status as status, count(fdr.status) as total "
          + "from FlowDocumentReport fdr "
          + "inner join FlowTraceabilityReport ftr "
          + "on ftr.id = fdr.flowTraceabilityReport.id "
          + "WHERE ftr.ownerId IN (:ownerIds) "
          + "AND ftr.subChannel IN (:subChannel) "
          + "AND DATE(fdr.createdAt) BETWEEN :startDate AND :endDate "
          + "AND fdr.createdAt <= :requestedAt "
          + "group by fdr.status")
  List<FlowDocumentReportProjection> reportDocument(
      @Param("ownerIds") Collection<Long> ownerIds,
      @Param("subChannel") Collection<String> subChannel,
      @Param("startDate") Date startDate,
      @Param("endDate") Date endDate,
      @Param("requestedAt") Date requestedAt);

  @Query(
      "SELECT count(fdr.id) FROM FlowDocumentReport fdr "
          + "INNER JOIN FlowTraceabilityReport ftr "
          + "ON ftr.id = fdr.flowTraceabilityReport.id "
          + "WHERE ftr.ownerId IN (:ownerIds) "
          + "AND fdr.status = 'Completed' "
          + "AND ftr.subChannel IN (:subChannel) "
          + "AND DATE(fdr.createdAt) BETWEEN :startDate AND :endDate "
          + "AND fdr.createdAt <= :requestedAt")
  long countDocumentPerChannel(
      @Param("ownerIds") Collection<Long> ownerIds,
      @Param("subChannel") Collection<String> subChannel,
      @Param("startDate") Date startDate,
      @Param("endDate") Date endDate,
      @Param("requestedAt") Date requestedAt);

  @Query(
      "SELECT count(fdr.status) as counter, CUS_DATE(fdr.createdAt) as timeline "
          + "FROM FlowDocumentReport fdr "
          + "INNER JOIN FlowTraceabilityReport ftr "
          + "ON ftr.id = fdr.flowTraceabilityReport.id "
          + "WHERE ftr.ownerId IN (:ownerIds) "
          + "AND ftr.subChannel IN (:subChannel) "
          + "AND fdr.status = 'Completed' "
          + "AND DATE(fdr.createdAt) BETWEEN :startDate AND :endDate "
          + "AND fdr.createdAt <= :requestedAt "
          + "Group by CUS_DATE(fdr.createdAt) ORDER BY timeline")
  List<FlowDocumentTimeSeriesProjection> reportDocumentTimeSeries(
      @Param("ownerIds") Collection<Long> ownerIds,
      @Param("subChannel") Collection<String> subChannel,
      @Param("startDate") Date startDate,
      @Param("endDate") Date endDate,
      @Param("requestedAt") Date requestedAt);
}
