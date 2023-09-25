package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.ms3.entity.FlowDocumentDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowDocumentDetailsRepository
    extends JpaRepository<FlowDocumentDetails, Long>,
        JpaSpecificationExecutor<FlowDocumentDetails> {

    Optional<FlowDocumentDetails> findByFlowDocument(FlowDocument flowDocument);
}
