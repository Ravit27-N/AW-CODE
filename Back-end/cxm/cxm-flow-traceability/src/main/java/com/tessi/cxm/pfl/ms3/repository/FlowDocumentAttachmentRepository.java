package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowDocumentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowDocumentAttachmentRepository extends
    JpaRepository<FlowDocumentAttachment, Long> {

}
