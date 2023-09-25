package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowDocumentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowDocumentNotificationRepository
    extends JpaRepository<FlowDocumentNotification, Long> {}
