package com.tessi.cxm.pfl.ms11.repository;

import com.tessi.cxm.pfl.ms11.entity.ChannelMetadata;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelMetadataRepository extends JpaRepository<ChannelMetadata, Long> {

  List<ChannelMetadata> findAllByCustomerAndType(String customer, String type);

  @Query("SELECT c FROM ChannelMetadata as c WHERE c.customer = :customer AND c.type in (:types)")
  List<ChannelMetadata> findAllChannelMetadata(@Param("customer") String customer,
      @Param("types") List<String> types);
}
