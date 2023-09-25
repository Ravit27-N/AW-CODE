package com.tessi.cxm.pfl.ms32.repository;

import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowDocumentReportRepository extends JpaRepository<FlowDocumentReport, Long> {
}
