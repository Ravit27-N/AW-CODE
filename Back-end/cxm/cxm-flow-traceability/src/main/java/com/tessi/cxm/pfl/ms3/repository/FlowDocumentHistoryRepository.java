package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentHistory;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("FlowDocumentHistoryRepository")
public interface FlowDocumentHistoryRepository
    extends JpaRepository<FlowDocumentHistory, Long>,
        JpaSpecificationExecutor<FlowDocumentHistory> {

  Optional<FlowDocumentHistory> findByFlowDocument(FlowDocument flowDocument);

  @Modifying
  @Query(
      "update FlowDocumentHistory dh set dh.dateTime = :dateTime where dh.flowDocument.id = :documentId and dh.event = :event")
  void updateHistoryByDocumentIdAndEvent(
      @Param("dateTime") Date dateTime,
      @Param("documentId") long documentId,
      @Param("event") String event);

  boolean existsByFlowDocumentIdAndEvent(long flowDocumentId, String event);

  boolean existsByFlowDocumentIdAndEventIn(long flowDocumentId, Collection<String> event);

  Optional<FlowDocumentHistory> findByFlowDocumentIdAndEvent(long flowDocumentId, String event);

  List<FlowDocumentHistory> findAllByEventIgnoreCaseInAndDateTimeInAndFlowDocumentIdDocIn(
      List<String> event, List<Date> dateTime, List<String> idDoc);
}
