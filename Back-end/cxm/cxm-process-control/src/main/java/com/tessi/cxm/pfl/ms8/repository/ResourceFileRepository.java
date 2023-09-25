package com.tessi.cxm.pfl.ms8.repository;

import com.tessi.cxm.pfl.ms8.entity.ResourceFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ResourceFileRepository extends JpaRepository<ResourceFile, Long> {

  List<ResourceFile> findAllByFlowId(String flowId);

  Optional<ResourceFile> findFirstByFlowIdAndTypeIgnoreCase(String flowId, String type);

  Optional<ResourceFile> findByFileId(String fileId);

  Optional<List<ResourceFile>> findAllByFileIdIn(List<String> fileIds);

  Optional<ResourceFile> findByFlowIdAndTypeAndPosition(
      String flowId, String type, String position);

  @Transactional
  @Modifying
  @Query(
      "delete from RESOURCE_FILE r where r.flowId = :flowId and r.type = :type and r.isDefault = :isDefault")
  void deleteByFlowIdAndTypeAndIsDefault(
      @Param("flowId") String flowId,
      @Param("type") String type,
      @Param("isDefault") boolean isDefault);

  @Transactional
  @Modifying
  @Query("delete from RESOURCE_FILE r where r.flowId = :flowId and r.type = :type")
  void deleteByFlowIdAndType(@Param("flowId") String flowId, @Param("type") String type);

  List<ResourceFile> findAllByFlowIdAndType(
      @Param("flowId") String flowId, @Param("type") String type);
}
