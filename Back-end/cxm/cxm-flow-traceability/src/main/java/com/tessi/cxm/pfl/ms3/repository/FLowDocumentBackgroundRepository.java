package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowDocumentBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FLowDocumentBackgroundRepository
    extends JpaRepository<FlowDocumentBackground, Long> {
  List<FlowDocumentBackground> findAllByFlowDocumentDetailsId(Long flowDocumentDetailsId);
}
