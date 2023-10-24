package com.innovationandtrust.corporate.repository;

import com.innovationandtrust.corporate.model.entity.Folder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository
    extends JpaRepository<Folder, Long>, JpaSpecificationExecutor<Folder> {
  List<Folder> findByBusinessUnitId(long id);

  @Modifying
  @Query("UPDATE Folder set businessUnit.id = :businessUnitId where createdBy = :userId")
  void updateBusinessId(@Param("userId") Long userId, @Param("businessUnitId") Long businessUnitId);
}
