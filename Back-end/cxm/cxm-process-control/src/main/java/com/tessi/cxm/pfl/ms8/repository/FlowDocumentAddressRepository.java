package com.tessi.cxm.pfl.ms8.repository;

import com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddress;
import com.tessi.cxm.pfl.ms8.entity.FlowDocumentAddressId;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlowDocumentAddressRepository
    extends JpaRepository<FlowDocumentAddress, FlowDocumentAddressId> {

  List<FlowDocumentAddress> findAllByFlowId(String flowId);

  List<FlowDocumentAddress> findAllByFlowIdAndDocIdAndModifiedAddressIsNotNull(String flowId, String docId, Sort sort);

  FlowDocumentAddress findFirstByFlowId(String flowId);

  @Modifying
  @Query(
      "update FLOW_DOCUMENT_ADDRESS set modifiedAddress = null, lastModified = :lastModified , lastModifiedBy =:lastModifiedBy "
          + "where flowId=:flowId and docId=:docId and addressLineNumber not in (:addressLineNumber)")
  void updateFlowDocumentAddressModified(
      @Param("lastModified") LocalDateTime lastModified,
      @Param("lastModifiedBy") String lastModifiedBy,
      @Param("flowId") String flowId,
      @Param("docId") String docId,
      @Param("addressLineNumber") List<Integer> addressLineNumber);
}
