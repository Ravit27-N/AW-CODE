package com.tessi.cxm.pfl.ms3.repository;

import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowDepositRepository
    extends JpaRepository<FlowDeposit, Long>, JpaSpecificationExecutor<FlowDeposit> {
  Optional<FlowDeposit> findByFileId(@Param("fileId") String fileId);
}
