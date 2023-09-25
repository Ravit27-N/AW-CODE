package com.tessi.cxm.pfl.ms8.repository;

import com.tessi.cxm.pfl.ms8.entity.Watermark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatermarkRepository extends JpaRepository<Watermark, Long> {
  Optional<Watermark> findByFlowId(String flowId);
}
