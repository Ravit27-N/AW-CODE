package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.dto.CountDocumentOfFlowProjection;
import com.tessi.cxm.pfl.ms3.dto.DocumentCsvProjection;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentExportStatus;
import com.tessi.cxm.pfl.shared.service.SharedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.transaction.annotation.Transactional;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

@Repository
public interface FlowDocumentRepository
    extends JpaRepository<FlowDocument, Long>,
    JpaSpecificationExecutor<FlowDocument>,
    SharedRepository<Long>,
    JpaSpecificationExecutorWithProjection<FlowDocument> {

  Optional<FlowDocument> findByCsvLineNumberAndFlowTraceabilityId(
      int csvLineNumber, Long flowTraceabilityId);

  @Modifying
  @Query(
      "Update FlowDocument dt "
          + "SET dt.fileSize = :fileSize,dt.status = :status,dt.fileId = :fileId, dt.version = :version  where dt.csvLineNumber = :lineNumber and dt.flowTraceability.id =:flowId and dt.version = :version")
  void updateFlowDocument(
      @Param("lineNumber") int lineNumber,
      @Param("flowId") long flowId,
      @Param("fileSize") long fileSize,
      @Param("status") String status,
      @Param("fileId") String fileId,
      @Param("version") long version);

  @Modifying
  @Query(
      "update FlowDocument dt set dt.status = :status where dt.flowTraceability.id = :flowId and dt.status <> :preventStatus")
  void updateFlowDocumentStatus(
      @Param("flowId") long flowId,
      @Param("status") String status,
      @Param("preventStatus") String preventStatus);

  @Query("select fd from  FlowDocument as fd where fd.flowTraceability.id = :flowTraceabilityId")
  List<FlowDocument> getFlowDocuments(@Param("flowTraceabilityId") Long flowTraceabilityId);

  @Query(
      "select fd from  FlowDocument as fd where fd.flowTraceability.id = :flowTraceabilityId and fd.status <> :preventStatus")
  List<FlowDocument> getFlowDocumentsAndStatus(
      @Param("flowTraceabilityId") Long flowTraceabilityId,
      @Param("preventStatus") String preventStatus);

  @Query(
      "select true from FlowDocument as doc inner join doc.notifications as noti "
          + "on doc.id = noti.document.id where doc.idDoc = :idDoc and noti.step = :step")
  boolean existsByIdDocAndNotificationStep(
      @Param("idDoc") String idDoc, @Param("step") String step);

  @Query("select fd from FlowDocument fd left join fd.notifications noti where fd.channel = 'Postal' and fd.idDoc in (:idDocs)")
  Page<FlowDocument> findFlowDocumentByIdDocIn(
      @Param("idDocs") List<String> idDocs, Pageable pageable);

  Optional<FlowDocument> findByFileId(String fileId);

  @Query("select "
      + "ft as flowTraceability,\n"
      + "count(fd.id) as totalDocs\n"
      + "from FlowTraceability ft \n"
      + "inner join FlowDocument fd on ft.id = fd.flowTraceability.id\n"
      + "where fd.flowTraceability.id in :ids\n"
      + "and fd.status not in :status\n"
      + "group by ft.id order by ft.id")
  Page<CountDocumentOfFlowProjection> countFlowDocumentByFlowIdInAndNotInStatus(
      @Param("ids") Collection<Long> ids, @Param("status") Collection<String> status,
      Pageable pageable);

  @Modifying
  @Query("update FlowDocument fd set fd.hubIdDoc = :hubIdDoc, fd.status = :status where fd.csvLineNumber = :lineNumber and fd.flowTraceability.id = :flowId and fd.version = :version")
  void updateEmailCampaignDocumentUuid(@Param("lineNumber") int lineNumber,
      @Param("flowId") long flowId, @Param("hubIdDoc") String hubIdDoc,
      @Param("status") String status,
      @Param("version") long version);

  Optional<FlowDocument> findByHubIdDoc(String hubIdDoc);

  @Query("select count(d) from FlowDocument d where d.status <> 'In error' and d.flowTraceability.id = :flowId")
  long countDocumentNoErrorByFlowId(@Param("flowId") long flowId);

  @Query("select count(d) from FlowDocument d where d.status = 'Completed' and d.flowTraceability.id = :flowId")
  long countDocumentCompletedByFlowId(@Param("flowId") long flowId);

  @Query("select fd from FlowDocument fd " +
      "inner join FlowTraceability ft on ft.id = fd.flowTraceability.id where ft.fileId in :flowIds and fd.status = :status")
  List<FlowDocument> findAllFlowDocumentByFlowIdIn(@Param("flowIds") List<String> flowIds, @Param("status") String status);

  @Modifying
  @Query(
      "update FlowDocumentHistory fd set fd.server =:server where lower(fd.event) = :event and fd.flowDocument.flowTraceability.fileId in :flowIds")
  void updateServerIdFlowDocumentHistoryValidated(
      @Param("server") String server,
      @Param("event") String event,
      @Param("flowIds") List<String> flowIds);

  List<FlowDocument> findAllByFlowTraceabilityIdAndStatusNotIn(
      Long flowTraceabilityId, List<String> status);

  @Query(value = "SELECT\n" +
          "    fd.id AS id_document,\n" +
          "    fd.created_at AS date_production,\n" +
          "    fd.date_status AS date_distribution,\n" +
          "    fd.channel AS canal,\n" +
          "    fd.sub_channel AS categorie,\n" +
          "    CASE\n" +
          "        WHEN fd.channel = 'Postal' AND fdd.impression = 'Recto' THEN 0\n" +
          "        WHEN fd.channel = 'Postal' AND fdd.impression = 'Recto/Verso' THEN 1\n" +
          "    END AS recto_verso,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fd.page_number ELSE NULL END AS nb_pages,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fd.sheet_number ELSE NULL END AS nb_feuilles,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN\n" +
          "        CASE\n" +
          "            WHEN fdd.color='0' THEN 0\n" +
          "            WHEN fdd.color='1' THEN 1\n" +
          "            ELSE NULL\n" +
          "        END\n" +
          "    ELSE NULL END AS couleur,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN\n" +
          "        CASE\n" +
          "            WHEN REGEXP_REPLACE(fdd.address, '[^0-9]', '') ~ '^[0-9]{5}$' THEN REGEXP_REPLACE(fdd.address, '[^0-9]', '')\n" +
          "            WHEN fdd.address ~ '(\\d{5})' THEN substring(fdd.address, '(\\d{5})')\n" +
          "            ELSE NULL\n" +
          "        END\n" +
          "    ELSE NULL END AS code_postal,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fdd.envelope ELSE NULL END AS enveloppe_reelle,\n" +
          "    ftd.campaign_name AS compagnie,\n" +
          "    COALESCE(fd.recipient, fdd.email) AS id_dest,\n" +
          "    fdd.fillers[1] AS filler_1,\n" +
          "    fdd.fillers[2] AS filler_2,\n" +
          "    fdd.fillers[3] AS filler_3,\n" +
          "    fdd.fillers[4] AS filler_4,\n" +
          "    fdd.fillers[5] AS filler_5,\n" +
          "    fd.status AS statut,\n" +
          "    fdd.doc_name AS doc_name,\n" +
          "    fdd.archiving AS duree_archivage,\n" +
          "    fdd.reference as nom_prestataire,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fd.version ELSE NULL END AS type_agrafe,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fdn.stamp_real ELSE NULL END AS urgence_reelle,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN COALESCE(fd.file_size, CAST(fdn.weight_real AS bigint)) ELSE NULL END AS poids,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fdd.postage ELSE NULL END AS tranche_reelle,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fdn.cost_real ELSE NULL END AS affranchissement,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fdd.address  ELSE NULL END AS zone_geo,\n" +
          "    CASE WHEN fd.channel = 'Postal' THEN fdd.postal_pickup  ELSE NULL END AS code_pays,\n" +
          "    fdh.\"server\" AS service \n" +
          "FROM \n" +
          "    flow_document fd\n" +
          "    inner JOIN flow_document_notification fdn ON fd.id = fdn.document_id  \n" +
          "    LEFT JOIN flow_document_details fdd ON fd.id = fdd.id\n" +
          "    LEFT JOIN flow_traceability ft ON fd.flow_traceability_id = ft.id\n" +
          "    LEFT JOIN flow_traceability_details ftd ON ft.id = ftd.id\n" +
          "    LEFT JOIN flow_document_history fdh ON fd.id = fdh.flow_document_id \n" +
          "WHERE \n" +
          " fd.export_status = :#{#documentExportStatus.name()} \n" +
          " AND fd.channel IN ('Postal', 'Digital')\n" +
          " ORDER BY fd.id\n", nativeQuery = true)
  Page<DocumentCsvProjection> dataToCsv(@Param("documentExportStatus") FlowDocumentExportStatus documentExportStatus, Pageable pageable);



  @Query("select fd from FlowDocument fd where fd.channel = 'Postal' and fd.idDoc in (:idDocs)")
  List<FlowDocument> findFlowDocumentByIdDocIn(@Param("idDoc") List<String> idDoc);
}

