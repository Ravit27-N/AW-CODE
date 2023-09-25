package com.tessi.cxm.pfl.ms11.repository;

import com.tessi.cxm.pfl.ms11.entity.ResourceLibrary;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResourceLibraryRepository
    extends JpaRepository<ResourceLibrary, Long>, JpaSpecificationExecutor<ResourceLibrary> {

  Optional<ResourceLibrary> findByFileId(String fileId);

  boolean existsByLabelIgnoreCaseAndClientId(String label, Long clientId);
  boolean existsByLabelIgnoreCaseAndClientIdAndType(String label, Long clientId, String type);
}
